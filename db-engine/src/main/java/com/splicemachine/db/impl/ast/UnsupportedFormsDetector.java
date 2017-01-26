/*
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Some parts of this source code are based on Apache Derby, and the following notices apply to
 * Apache Derby:
 *
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified the Apache Derby code in this file.
 *
 * All such Splice Machine modifications are Copyright 2012 - 2017 Splice Machine, Inc.,
 * and are licensed to you under the GNU Affero General Public License.
 */

package com.splicemachine.db.impl.ast;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.reference.MessageId;
import com.splicemachine.db.iapi.sql.compile.Visitable;
import com.splicemachine.db.impl.sql.compile.*;
import org.spark_project.guava.base.Function;
import org.spark_project.guava.collect.Lists;
import java.util.*;

/**
 * Visitor that checks for plan-time structures know to be unsupported by Splice, and
 * throws exception when found.
 *
 * Currently the only unsupported structure identified is an update or delete with a
 * materializing operation underneath.
 *
 * @author P Trolard
 *         Date: 10/02/2014
 */
public class UnsupportedFormsDetector extends AbstractSpliceVisitor {
    @Override
    public Visitable visit(DeleteNode node) throws StandardException {
        // checkForUnsupported(node);
        return node;
    }

    @Override
    public Visitable visit(UpdateNode node) throws StandardException {
        // checkForUnsupported(node);
        return node;
    }

    public static void checkForUnsupported(DMLStatementNode node) throws StandardException {
        List<ResultSetNode> sinks = Lists.newLinkedList(RSUtils.sinkingChildren(node.getResultSetNode()));
        if (sinks.size() > 0){
            throw StandardException.newException(MessageId.SPLICE_UNSUPPORTED_OPERATION,
                                                    unsupportedSinkingMsg(node, sinks));
        }
    }

    public static String unsupportedSinkingMsg(DMLStatementNode dml, List<ResultSetNode> rsns) {
        String modder = dml instanceof DeleteNode ? "A Delete" : "An Update";
        List<String> sinkingOps = Lists.transform(rsns, new Function<ResultSetNode, String>() {
            @Override
            public String apply(ResultSetNode input) {
                return RSUtils.sinkingNames.get(input.getClass());
            }
        });
        return String.format("%s over %s operations", modder,
                                StringUtils.asEnglishList(sinkingOps, "or"));

    }

}
