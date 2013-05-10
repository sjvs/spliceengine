package com.splicemachine.derby.impl.job.index;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.splicemachine.constants.HBaseConstants;
import com.splicemachine.derby.hbase.SpliceDriver;
import com.splicemachine.derby.impl.job.ZkTask;
import com.splicemachine.derby.impl.job.operation.OperationJob;
import com.splicemachine.derby.impl.sql.execute.index.WriteContextFactoryPool;
import com.splicemachine.derby.utils.SpliceUtils;
import com.splicemachine.derby.utils.SpliceZooKeeperManager;
import com.splicemachine.hbase.CallBuffer;
import com.splicemachine.hbase.MutationRequest;
import com.splicemachine.hbase.MutationResponse;
import com.splicemachine.hbase.TableWriter;
import com.splicemachine.hbase.batch.WriteContextFactory;
import org.apache.derby.iapi.services.io.ArrayUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.regionserver.WrongRegionException;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Scott Fines
 * Created on: 4/5/13
 */
public class CreateIndexTask extends ZkTask {
    private static final long serialVersionUID = 2l;
    private String transactionId;
    private long indexConglomId;
    private long baseConglomId;
    private int[] indexColsToBaseColMap;
    private boolean isUnique;

    private HRegion region;

    public CreateIndexTask() {
    }

    public CreateIndexTask(String transactionId,
                           long indexConglomId,
                           long baseConglomId,
                           int[] indexColsToBaseColMap,
                           boolean unique,
                           String jobId ) {
        super(jobId, OperationJob.operationTaskPriority,transactionId,false);
        this.transactionId = transactionId;
        this.indexConglomId = indexConglomId;
        this.baseConglomId = baseConglomId;
        this.indexColsToBaseColMap = indexColsToBaseColMap;
        isUnique = unique;
    }

    @Override
    public void prepareTask(HRegion region, SpliceZooKeeperManager zooKeeper) throws ExecutionException {
        this.region = region;
        super.prepareTask(region, zooKeeper);
    }

    @Override
    protected String getTaskType() {
        return "createIndexTask";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeUTF(transactionId);
        out.writeLong(indexConglomId);
        out.writeLong(baseConglomId);
        ArrayUtil.writeIntArray(out, indexColsToBaseColMap);
        out.writeBoolean(isUnique);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        transactionId = in.readUTF();
        indexConglomId = in.readLong();
        baseConglomId = in.readLong();
        indexColsToBaseColMap = ArrayUtil.readIntArray(in);
        isUnique = in.readBoolean();
    }

    @Override
    public boolean invalidateOnClose() {
        return true;
    }

    @Override
    public void execute() throws ExecutionException, InterruptedException {
        Scan regionScan = SpliceUtils.createScan(transactionId);
        regionScan.setCaching(100);
        regionScan.setStartRow(region.getStartKey());
        regionScan.setStopRow(region.getEndKey());

        for(int mainTablePos:indexColsToBaseColMap){
            regionScan.addColumn(HBaseConstants.DEFAULT_FAMILY_BYTES,Integer.toString(mainTablePos-1).getBytes());
        }

        try{
            //add index to table watcher
            WriteContextFactory contextFactory = WriteContextFactoryPool.getContextFactory(baseConglomId);
            contextFactory.addIndex(indexConglomId, indexColsToBaseColMap, isUnique);

            //backfill the index with previously committed data
            RegionScanner sourceScanner = region.getScanner(regionScan);

            byte[] indexBytes = Long.toString(indexConglomId).getBytes();
            CallBuffer<Mutation> writeBuffer =
                    SpliceDriver.driver().getTableWriter().writeBuffer(indexBytes, new TableWriter.FlushWatcher() {
                        @Override
                        public List<Mutation> preFlush(List<Mutation> mutations) throws Exception {
                            return mutations;
                        }

                        @Override
                        public Response globalError(Throwable t) throws Exception {
                            if(t instanceof NotServingRegionException) return Response.RETRY;
                            else if(t instanceof WrongRegionException) return Response.RETRY;
                            else
                                return Response.THROW_ERROR;
                        }

                        @Override
                        public Response partialFailure(MutationRequest request, MutationResponse response) throws Exception {
                            for(String failureMessage:response.getFailedRows().values()){
                                if(failureMessage.contains("NotServingRegion")||failureMessage.contains("WrongRegion"))
                                    return Response.RETRY;
                            }
                            return  Response.THROW_ERROR;
                        }
                    });

            List < KeyValue > nextRow = Lists.newArrayListWithExpectedSize(indexColsToBaseColMap.length);
            //translate down to zero-indexed
            int[] indexColMap = new int[indexColsToBaseColMap.length];
            for(int pos=0;pos<indexColsToBaseColMap.length;pos++){
                indexColMap[pos] = indexColsToBaseColMap[pos]-1;
            }
            boolean shouldContinue = true;
            while(shouldContinue){
                nextRow.clear();
                shouldContinue  = sourceScanner.next(nextRow);
                List<Put> indexPuts = translateResult(nextRow,indexColMap);

                writeBuffer.addAll(indexPuts);
            }
            writeBuffer.flushBuffer();
            writeBuffer.close();

        } catch (IOException e) {
            throw new ExecutionException(e);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private List<Put> translateResult(List<KeyValue> result,int[] indexColsToMainColMap) throws IOException{
        Map<String,List<KeyValue>> putConstructors = Maps.newHashMapWithExpectedSize(1);
        for(KeyValue keyValue:result){
            String row = Bytes.toString(keyValue.getRow());
            List<KeyValue> cols = putConstructors.get(row);
            if(cols==null){
                cols = Lists.newArrayListWithExpectedSize(indexColsToMainColMap.length);
                putConstructors.put(row,cols);
            }
            cols.add(keyValue);
        }
        //build Puts for each row
        List<Put> indexPuts = Lists.newArrayListWithExpectedSize(putConstructors.size());
        for(String mainRowStr: putConstructors.keySet()){
            List<KeyValue> rowData = putConstructors.get(mainRowStr);
            byte[][] indexRowData = getDataArray();
            int rowSize=0;
            byte[] rowKey = null;
            for(KeyValue kv:rowData){
                if(rowKey==null)
                    rowKey = kv.getRow();
                int colPos = Integer.parseInt(Bytes.toString(kv.getQualifier()));
                for(int indexPos=0;indexPos<indexColsToMainColMap.length;indexPos++){
                    if(colPos == indexColsToMainColMap[indexPos]){
                        byte[] val = kv.getValue();
                        indexRowData[indexPos] = val;
                        rowSize+=val.length;
                        break;
                    }
                }
            }
            if(!isUnique){
                byte[] postfix = SpliceUtils.getUniqueKey();
                indexRowData[indexRowData.length-1] = postfix;
                rowSize+=postfix.length;
            }

            byte[] finalIndexRow = new byte[rowSize];
            int offset =0;
            for(byte[] indexCol:indexRowData){
                System.arraycopy(indexCol,0,finalIndexRow,offset,indexCol.length);
                offset+=indexCol.length;
            }
            Put indexPut = SpliceUtils.createPut(finalIndexRow, transactionId);
            for(int dataPos=0;dataPos<indexRowData.length;dataPos++){
                byte[] putPos = Integer.toString(dataPos).getBytes();
                indexPut.add(HBaseConstants.DEFAULT_FAMILY_BYTES,putPos,indexRowData[dataPos]);
            }

            indexPut.add(HBaseConstants.DEFAULT_FAMILY_BYTES,
                    Integer.toString(rowData.size()).getBytes(),rowKey);
            indexPuts.add(indexPut);
        }

        return indexPuts;
    }

    private byte[][] getDataArray() {
        if(isUnique)
            return new byte[indexColsToBaseColMap.length][];
        else
            return new byte[indexColsToBaseColMap.length+1][];
    }
}
