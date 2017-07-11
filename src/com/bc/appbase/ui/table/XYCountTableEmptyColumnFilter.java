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
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 9:28:14 AM
 */
public class XYCountTableEmptyColumnFilter implements Predicate<Integer> {

    private final XYCountTableModel tableModel;
    
    private final ToInteger toInteger;
    
    private final int sumRow;

    public XYCountTableEmptyColumnFilter(XYCountTableModel tableModel) {
        
        this.tableModel = Objects.requireNonNull(tableModel);
        
        this.toInteger = new ToInteger();
        
        final int rowCount = tableModel.getRowCount();
        int sc = -1;
        for(int rowIndex =0; rowIndex < rowCount; rowIndex++) {
            if(tableModel.isSumRow(rowIndex)) {
                sc = rowIndex;
                break;
            }
        }
        this.sumRow = sc;
    }
    
    @Override
    public boolean test(Integer columnIndex) {
        
        return columnIndex == 0 || tableModel.isSumColumn(columnIndex) || this.toInteger.apply(tableModel.getValueAt(sumRow, columnIndex), -1) > 0;
    }
}
