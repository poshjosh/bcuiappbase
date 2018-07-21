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

import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.table.cell.ColumnWidthsImpl;
import com.bc.ui.table.cell.TableCellSize;
import com.bc.ui.table.cell.TableCellUIFactoryImpl;
import com.bc.ui.table.cell.TableCellUIState;
import java.awt.Component;
import java.util.Objects;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import com.bc.ui.table.cell.TableCellDisplayFormatImpl;
import com.bc.ui.table.cell.TableCellSizeImpl;
import com.bc.ui.table.cell.TableCellSizeManager;
import com.bc.ui.table.cell.TableCellSizeManagerImpl;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 2:41:58 PM
 */
public class DefaultTableCellUIFactory extends TableCellUIFactoryImpl {

    private transient static final Logger LOG = Logger.getLogger(DefaultTableCellUIFactory.class.getName());

    private final ComponentModel componentModel;
    
    private final Class entityType;
    
    private final TableModel tableModel;

    public DefaultTableCellUIFactory(int minCellHeight, int maxCellHeight, DateFormat dateFormat,
            ComponentModel componentModel, Class entityType, TableModel tableModel) {
        this(new DefaultTableCellUIState(), 
                new TableCellSizeImpl(new TableCellDisplayFormatImpl(dateFormat), minCellHeight, maxCellHeight),
                new TableCellDisplayFormatImpl(dateFormat),
                new TableCellSizeManagerImpl(new ColumnWidthsImpl()),
                componentModel, entityType, tableModel
        ); 
    }
    
    public DefaultTableCellUIFactory(
            TableCellUIState uiState, TableCellSize size, 
            TableCellDisplayFormat displayValue, TableCellSizeManager cellSizeManager,
            ComponentModel componentModel, Class entityType, TableModel tableModel) {
        super(uiState, size, displayValue, cellSizeManager);
        this.componentModel = Objects.requireNonNull(componentModel);
        this.entityType = Objects.requireNonNull(entityType);
        this.tableModel = Objects.requireNonNull(tableModel);
        LOG.fine(() -> "Component Model type: " + this.componentModel.getClass().getName());
    }

    @Override
    public Component getEditorComponent(int columnIndex) {
        LOG.entering(this.getClass().getName(), "getEditorComponent(int)");
        final Class type = tableModel.getColumnClass(columnIndex);
        final String name = tableModel.getColumnName(columnIndex);
        final Component component = componentModel.getComponent(entityType, type, name, null);
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "[:{0}]. entity type: {1}, type: {2}, name: {3}, component type: {4}, component model type: {5}", 
                    new Object[]{columnIndex, entityType, type, name, 
                        (component==null?null:component.getClass().getName()),
                        (componentModel.getClass().getName())
                    });
        }
        return component;
    }

//    @Override
//    public Component getComponent(int columnIndex) {
//        final Class type = tableModel.getColumnClass(columnIndex);
//        final String name = tableModel.getColumnName(columnIndex);
//        return componentModel.getComponent(entityType, type, name, null);
//    }
}
