/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.appbase.ui.table;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 3:29:44 PM
 */
public class TableColumnFilter implements Consumer<Predicate<Integer>> {

    private static final Logger logger = Logger.getLogger(TableColumnFilter.class.getName());

    private final int columnCount;
    
    private final TableColumnManager tableColMgr;
    
    public TableColumnFilter(JTable table) {
        this(new TableColumnManager(table, true), table.getModel().getColumnCount());
    }
    public TableColumnFilter(TableColumnManager tableColumnManager, int columnCount) {
        this.tableColMgr = Objects.requireNonNull(tableColumnManager);
        this.columnCount = columnCount;
    }
    
    @Override
    public void accept(Predicate<Integer> columnTest) {
        
        for(int columnIndex=0; columnIndex<columnCount; columnIndex++) {
            
            final boolean visible = columnTest.test(columnIndex);
            
            tableColMgr.setVisible(columnIndex, visible);
            
            final int pos = columnIndex;
            logger.log(Level.FINE, () -> MessageFormat.format("Column: {0}, visible: {0}", pos, visible));
        }
    }
}

