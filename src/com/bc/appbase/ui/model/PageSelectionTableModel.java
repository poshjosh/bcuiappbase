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

import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appbase.ui.DialogManager;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2017 10:38:19 PM
 */
public class PageSelectionTableModel implements TableModel {
    
    private final TableModel tableModel;
    
    public PageSelectionTableModel(
            App app, JTable table, ResultModel resultModel, DialogManager.PageSelection pageSelection) {
        Objects.requireNonNull(app);
        Objects.requireNonNull(table);
        Objects.requireNonNull(pageSelection);
        switch(pageSelection) {
            case CurrentPage: 
                tableModel = table.getModel(); 
                break;
            case AllPages: 
                tableModel = new SearchResultsTableModel(app, 
                        app.getUIContext().getLinkedSearchResults(table), 
                        resultModel); 
                break;
            case FirstPage:
                tableModel = new SearchResultsTableModel(app, 
                        app.getUIContext().getLinkedSearchResults(table), 
                        resultModel, 0, 1); 
                break;
            default:
                throw new IllegalArgumentException("Unexpected "+DialogManager.PageSelection.class.getName()+", found: "+pageSelection+", expected any of: " + Arrays.toString(DialogManager.PageSelection.values()));

        }
    }

    @Override
    public int getRowCount() {
        return tableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return tableModel.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return tableModel.getColumnName(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tableModel.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return tableModel.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableModel.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tableModel.setValueAt(aValue, rowIndex, columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModel.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModel.removeTableModelListener(l);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.tableModel);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PageSelectionTableModel) {
            return tableModel.equals(((PageSelectionTableModel)obj).tableModel);
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return tableModel.toString();
    }
}
