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

package com.bc.appbase.ui.table.cell;

import com.bc.appcore.table.model.EntityTableModel;
import com.bc.ui.table.cell.TableCellUIStateImpl;
import java.awt.Color;
import java.awt.Component;
import java.util.Objects;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 7, 2017 6:48:54 PM
 */
public class DefaultTableCellUIState extends TableCellUIStateImpl {

    private final Color pendingUpdateBackgroundColor;
    
    public DefaultTableCellUIState() { 
        this(Color.PINK);
    }

    public DefaultTableCellUIState(Color pendingUpdateBackgroundColor) {
        this.pendingUpdateBackgroundColor = Objects.requireNonNull(pendingUpdateBackgroundColor);
    }

    @Override
    public void updateState(JTable table, Component cellUI, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.updateState(table, cellUI, value, isSelected, hasFocus, row, column);
        
        if(table.getModel() instanceof EntityTableModel) {
            
            final EntityTableModel model = (EntityTableModel)table.getModel();
            
            if(model.getResultModel().isPendingUpdate(row, column)) {
                
                cellUI.setBackground(this.pendingUpdateBackgroundColor);
            }
        }
    }
}
