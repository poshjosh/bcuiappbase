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

package com.bc.appbase.ui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 27, 2017 8:35:16 AM
 */
public class WorksheetTableModel extends AbstractTableModel {
    
    private int columnCount;
    
    private final List<Cell[]> rows;

    public WorksheetTableModel(Sheet sheet, int offset, int limit) {
        rows = new ArrayList(limit);
        final int end = offset + limit;
        for(int row=offset; row < end; row++) {
            if(row < 0 || row >= sheet.getRows()) {
                break;
            }
            Cell [] cells = sheet.getRow(row);
            if(cells.length < 1) {
                continue;
            }
            columnCount = Math.max(columnCount, cells.length);
            rows.add(cells);
        }
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Object value = rows.get(rowIndex)[columnIndex].getContents();
//System.out.println("["+rowIndex+':'+columnIndex+"] "+value);        
        return value;
    }
}
