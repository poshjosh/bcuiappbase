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

package com.bc.appbase.ui.components;

import com.bc.appbase.App;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.components.ComponentModel.ComponentProperties;
import com.bc.appcore.table.model.EntityTableModelImpl;
import java.awt.Component;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 9, 2017 5:40:39 PM
 */
public class CollectionAsTableProvider extends AbstractCollectionComponentProvider {

    private static final Logger logger = Logger.getLogger(CollectionAsTableProvider.class.getName());
    
    public CollectionAsTableProvider(App app, ComponentProperties props, int batchSize) {
        super(app, props, batchSize);
    }

    @Override
    public Component execute(Class parentType, Class valueType, String name, Collection value) {
        
        logger.finer(() -> MessageFormat.format("Type: {0}, Field: {1} {2} = {3}", 
                parentType.getName(), valueType.getSimpleName(), name, value));
               
        final Component component;
        
        final Class collectionValueType = this.getCollectionValueType(parentType, name);

        final TableModel tableModel = this.getTableModel((Collection)value, collectionValueType);

        final JTable table = new JTable(tableModel);
        table.setName(name);

//            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final UIContext uiContext = this.getApp().getUIContext();
        
        uiContext.setTableFont(table);

        component = table;

        final Dimension dim = this.getCollectionDimension(component, value);

        logger.finer(() -> MessageFormat.format("For {0}, dimension = {1} of field: {2} {3} = {4}", 
                parentType.getName(), dim, valueType.getSimpleName(), name, value));

        table.setPreferredSize(dim);

        uiContext.updateTableUI(table, collectionValueType, -1);

        component.setName(name);
        
        return component;
    }

    public TableModel getTableModel(Collection values, Class valueType) {
        
        final List entities = this.toEntitiesList(values, valueType);
        
        return new EntityTableModelImpl(entities, this.getApp().getResultModel(valueType, null));
    }
}
