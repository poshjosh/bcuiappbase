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

package com.bc.appbase.ui;

import com.bc.appcore.jpa.model.EntityResultModel;
import com.bc.appcore.table.model.EntityTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 7, 2017 9:28:58 PM
 */
public class UpdateTableActionListener implements ActionListener {

    private final UIContext uiContext;
    
    private final JTable table;
    
    public UpdateTableActionListener(UIContext uiContext, JTable table) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.table = Objects.requireNonNull(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        final TableModel tableModel = table.getModel();
        
        final EntityResultModel entityResultModel = tableModel instanceof EntityTableModel ?
                ((EntityTableModel)tableModel).getResultModel() : null;
        
        if(entityResultModel == null) {
            
            return;
        }
        
        new Thread(UpdateTableActionListener.this.getClass().getName() + 
                "_ResultModelUpdateThread") {
            @Override
            public void run() {
                
                entityResultModel.update();
                
                if(tableModel instanceof AbstractTableModel) {

                    java.awt.EventQueue.invokeLater(() -> {
                        
                        final AbstractTableModel abstractTableModel = (AbstractTableModel)tableModel;

                        try{
                            
                            abstractTableModel.fireTableDataChanged();
                            
                        }finally{

                            uiContext.updateTableUI(table, entityResultModel.getEntityType(), 
                                    entityResultModel.getSerialColumnIndex());
                        }
                    });
                }    
            }
        }.start();
    }
}
