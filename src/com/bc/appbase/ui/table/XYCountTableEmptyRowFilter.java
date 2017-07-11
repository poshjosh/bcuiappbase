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

import com.bc.appbase.ui.table.model.XYCountTableModel;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 9:21:00 AM
 */
public class XYCountTableEmptyRowFilter implements Predicate<Integer> {

    private final XYCountTableModel tableModel;
    
    private final ToInteger toInteger;
    
    private final int sumColumn;

    public XYCountTableEmptyRowFilter(XYCountTableModel tableModel) {
        
        this.tableModel = Objects.requireNonNull(tableModel);
        
        this.toInteger = new ToInteger();
        
        final int columnCount = tableModel.getColumnCount();
        int sc = -1;
        for(int columnIndex =0; columnIndex < columnCount; columnIndex++) {
            if(tableModel.isSumColumn(columnIndex)) {
                sc = columnIndex;
                break;
            }
        }
        this.sumColumn = sc;
    }
    
    @Override
    public boolean test(Integer rowIndex) {
        
        return tableModel.isSumRow(rowIndex) || toInteger.apply(tableModel.getValueAt(rowIndex, sumColumn), -1) > 0;
    }
}
