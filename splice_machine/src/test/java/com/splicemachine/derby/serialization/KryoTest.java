/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.splicemachine.derby.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.spark_project.guava.collect.Lists;
import com.splicemachine.SpliceKryoRegistry;
import com.splicemachine.si.testenv.ArchitectureIndependent;
import org.junit.BeforeClass;
import org.junit.Test;

import com.splicemachine.db.iapi.types.SQLDecimal;
import com.splicemachine.db.iapi.types.SQLRef;
import com.splicemachine.derby.impl.store.access.hbase.HBaseRowLocation;
import com.splicemachine.derby.utils.kryo.DataValueDescriptorSerializer;
import com.splicemachine.utils.ByteSlice;
import com.splicemachine.utils.CachedByteSlice;
import com.splicemachine.utils.kryo.KryoObjectInput;
import com.splicemachine.utils.kryo.KryoObjectOutput;
import com.splicemachine.utils.kryo.KryoPool;
import org.junit.experimental.categories.Category;

/**
 * @author Jeff Cunningham
 *         Date: 11/13/13
 */
@Category(ArchitectureIndependent.class)
public class KryoTest  {

    private static final KryoPool kryoPool = new KryoPool(1);
    private static Kryo kryo;

    @BeforeClass
    public static void beforeClass() throws Exception {
        kryoPool.setKryoRegistry(new SpliceKryoRegistry());
        kryo = kryoPool.get();
    }

    @Test
    public void testSQLDecimalNull() throws Exception {
        SQLDecimal in = new SQLDecimal();
        in.setValue((Number) null);

        Output output = new Output(new byte[20],20);
        DataValueDescriptorSerializer<SQLDecimal> serializer =
                (DataValueDescriptorSerializer<SQLDecimal>) kryo.getSerializer(SQLDecimal.class);
        serializer.write(kryo, output, in);

        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(bytes);
        SQLDecimal out = serializer.read(kryo, input, SQLDecimal.class);

        assertNotNull(out);
        assertNull(out.getObject());
    }

    @Test
    public void testSQLDecimalInt() throws Exception {
        SQLDecimal in = new SQLDecimal();
        in.setValue(1234);

        Output output = new Output(new byte[20],20);
        KryoObjectOutput koo = new KryoObjectOutput(output,kryo);
        koo.writeObject(in);

        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(bytes);
        KryoObjectInput koi = new KryoObjectInput(input,kryo);
        SQLDecimal out = (SQLDecimal) koi.readObject();

        assertNotNull(out);
        assertEquals(in, out);
    }

     @Test
    public void testSQLDecimalDecimal() throws Exception {
        SQLDecimal in = new SQLDecimal();
        in.setValue(1234.4567);

        Output output = new Output(new byte[20],20);
        DataValueDescriptorSerializer<SQLDecimal> serializer =
                (DataValueDescriptorSerializer<SQLDecimal>) kryo.getSerializer(SQLDecimal.class);
        serializer.write(kryo, output, in);

        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        SQLDecimal out = serializer.read(kryo, input, SQLDecimal.class);

        assertNotNull(out);
        assertEquals(in, out);
    }

    @Test
    public void testSQLRef() throws Exception {
        SQLRef sqlRef = new SQLRef(new HBaseRowLocation(new byte[] {0, 1, 2,3,4,5,6,7,8,9}));

        Output output = new Output(new byte[30],30);
        Serializer serializer = kryo.getSerializer(SQLRef.class);
        serializer.write(kryo, output, sqlRef);
        
        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        SQLRef out = (SQLRef) serializer.read(kryo, input, SQLRef.class);

        assertNotNull(out);
        assertEquals(sqlRef, out);
    }

    @Test
    public void testDataValueStorage() throws Exception {
        SQLRef sqlRef = new SQLRef(new HBaseRowLocation(ByteSlice.wrap(new byte[] {0, 1, 2,3,4,5,6,7,8,9}, 6, 4)));

        ActivationSerializer.DataValueStorage dvdStore = new ActivationSerializer.DataValueStorage(sqlRef);
        assertNotNull(dvdStore.getValue(null));


        Output output = new Output(new byte[30],30);
        Serializer serializer = kryo.getSerializer(ActivationSerializer.DataValueStorage.class);
        serializer.write(kryo, output, dvdStore);

        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        assertNotNull(input);
        ActivationSerializer.DataValueStorage out = (ActivationSerializer.DataValueStorage)
            serializer.read(kryo, input, ActivationSerializer.DataValueStorage.class);

        assertNotNull(out);
        assertNotNull(out.getValue(null));
        assertEquals(dvdStore.getValue(null), out.getValue(null));
    }

    @Test
    public void testImmutableList() throws Exception {
        List in = Collections.unmodifiableList(Lists.newArrayList(1, 2));
        Class<? extends List> clazz = in.getClass();

        Output output = new Output(new byte[20],20);
        kryo.writeObject(output, in);

        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        List out = kryo.readObject(input, clazz);

        assertNotNull(out);
        assertEquals(in, out);
    }

    @Test
    public void testEmptyByteSlice() {
        ByteSlice byteSliceIn = ByteSlice.wrap(new byte[] {0, 1, 2,3,4,5,6,7,8,9}, 5, 0);

        Output output = new Output(new byte[20],20);
        kryo.writeObject(output, byteSliceIn);
        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        ByteSlice byteSliceOut = kryo.readObject(input, ByteSlice.class);

        assertNotNull(byteSliceOut);
        assertEquals(0, byteSliceOut.offset());
        assertEquals(0, byteSliceOut.length());
    }

    @Test
    public void testByteSlice() {
        ByteSlice byteSliceIn = ByteSlice.wrap(new byte[] {0, 1, 2,3,4,5,6,7,8,9}, 2, 4);

        Output output = new Output(new byte[20],20);
        kryo.writeObject(output, byteSliceIn);
        byte[] bytes = output.toBytes();
        assertNotNull(bytes);

        Input input = new Input(new ByteArrayInputStream(bytes), bytes.length);
        ByteSlice byteSliceOut = kryo.readObject(input, ByteSlice.class);

        assertNotNull(byteSliceOut);
        assertEquals(0, byteSliceOut.offset());
        assertEquals(4, byteSliceOut.length());
    }

    @Test
    public void testCachedByteSlice() {
        ByteSlice byteSliceIn = new CachedByteSlice(new byte[] {0, 1, 2,3,4,5,6,7,8,9}, 5, 0);

        Output output = new Output(new byte[20],20);
        try {
            kryo.writeObject(output, byteSliceIn);
            fail("Expected exception trying to serialize CachedByteSlice.");
        } catch (Throwable e) {
            assertTrue("Expected CachedByteSlice not registered.", e.getLocalizedMessage().contains("is not registered") &&
            e.getLocalizedMessage().contains("CachedByteSlice"));
        }
    }
}
