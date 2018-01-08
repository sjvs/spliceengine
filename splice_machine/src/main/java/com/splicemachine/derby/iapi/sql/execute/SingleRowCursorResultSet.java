/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.splicemachine.derby.iapi.sql.execute;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultDescription;
import com.splicemachine.db.iapi.sql.ResultSet;
import com.splicemachine.db.iapi.sql.execute.CursorResultSet;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.sql.execute.NoPutResultSet;
import com.splicemachine.db.iapi.types.RowLocation;

import java.sql.SQLWarning;
import java.sql.Timestamp;

/**
 * A CursorResultSet that holds a single ExecRow.
 */
public class SingleRowCursorResultSet implements CursorResultSet {

    private final ResultDescription resultDescription;
    private final ExecRow row;

    public SingleRowCursorResultSet(ResultDescription resultDescription, ExecRow constantRow) {
        this.resultDescription = resultDescription;
        this.row = constantRow;
    }

    @Override
    public RowLocation getRowLocation() throws StandardException {
        return null;
    }

    @Override
    public ExecRow getCurrentRow() throws StandardException {
        return row;
    }

    @Override
    public boolean returnsRows() {
        return false;
    }

    @Override
    public int modifiedRowCount() {
        return 0;
    }

    @Override
    public ResultDescription getResultDescription() {
        return resultDescription;
    }

    @Override
    public Activation getActivation() {
        return null;
    }

    @Override
    public void open() throws StandardException {
    }

    @Override
    public ExecRow getAbsoluteRow(int row) throws StandardException {
        return this.row;
    }

    @Override
    public ExecRow getRelativeRow(int row) throws StandardException {
        return this.row;
    }

    @Override
    public ExecRow setBeforeFirstRow() throws StandardException {
        return row;
    }

    @Override
    public ExecRow getFirstRow() throws StandardException {
        return row;
    }

    @Override
    public ExecRow getNextRow() throws StandardException {
        return row;
    }

    @Override
    public ExecRow getPreviousRow() throws StandardException {
        return row;
    }

    @Override
    public ExecRow getLastRow() throws StandardException {
        return row;
    }

    @Override
    public ExecRow setAfterLastRow() throws StandardException {
        return row;
    }

    @Override
    public void clearCurrentRow() {
    }

    @Override
    public boolean checkRowPosition(int isType) throws StandardException {
        return false;
    }

    @Override
    public int getRowNumber() {
        return 0;
    }

    @Override
    public void close() throws StandardException {

    }

    @Override
    public void cleanUp() throws StandardException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isKilled() {
        return false;
    }

    @Override
    public boolean isTimedout() {
        return false;
    }

    @Override
    public void finish() throws StandardException {

    }

    @Override
    public long getExecuteTime() {
        return 0;
    }

    @Override
    public Timestamp getBeginExecutionTimestamp() {
        return null;
    }

    @Override
    public Timestamp getEndExecutionTimestamp() {
        return null;
    }

    @Override
    public long getTimeSpent(int type) {
        return 0;
    }

    @Override
    public NoPutResultSet[] getSubqueryTrackingArray(int numSubqueries) {
        return new NoPutResultSet[0];
    }

    @Override
    public ResultSet getAutoGeneratedKeysResultset() {
        return null;
    }

    @Override
    public String getCursorName() {
        return null;
    }

    @Override
    public void addWarning(SQLWarning w) {

    }

    @Override
    public SQLWarning getWarnings() {
        return null;
    }
}
