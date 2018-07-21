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

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 7, 2017 5:52:22 PM
 */
public interface TableCellSelectionHandler {
    
    default void onCellSelected(JTable table, MouseEvent e) {
        
        final Point point = e.getPoint();
        
        final int row = table.rowAtPoint(point);
        
        final int col = table.columnAtPoint(point);
        
        this.onCellSelected(table, row, col);
    }
    
    default void onCellSelected(JTable table) {
        
        final int row = table.getSelectedRow();
        
        final int column = table.getSelectedColumn();
        
        this.onCellSelected(table, row, column);
    }

    void onCellSelected(JTable table, int row, int column);
}
