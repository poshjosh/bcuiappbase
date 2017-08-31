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

import com.bc.appbase.App;
import com.bc.appbase.ui.table.cell.DefaultTableCellDisplayFormat;
import com.bc.appbase.ui.table.model.TableModelDisplayFormatImpl;
import com.bc.appcore.table.model.DisplayTableModelFromModel;
import com.bc.appcore.table.model.MapTableModel;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.table.model.TableModelDisplayFormat;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 14, 2017 9:14:15 AM
 */
public class ComponentModelWithTableAsEntityListUI extends ComponentModelImpl {

    private static final Logger logger = Logger.getLogger(ComponentModelWithTableAsEntityListUI.class.getName());

    private final App app;
    
    private final TypeProvider typeProvider;

    public ComponentModelWithTableAsEntityListUI(App app,
            ComponentProperties componentProperties, int contentLengthAboveWhichTextAreaIsUsed) {
        
        super(app.getOrException(SelectionContext.class), 
                app.getOrException(DateFromUIBuilder.class), app.getOrException(DateUIUpdater.class),
                componentProperties, contentLengthAboveWhichTextAreaIsUsed);
        
        this.app = Objects.requireNonNull(app);
        
        this.typeProvider = app.getOrException(TypeProvider.class);
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new ComponentModelWithTableAsEntityListUI(this.app, properties, 
                this.getContentLengthAboveWhichTextAreaIsUsed());
    }
    
    @Override
    public Component getComponent(Class parentType, Class valueType, String name, Object value) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component component;

        if(value instanceof Collection) {
            
            final String serialColumnName = null; //"Serial";
            
            final int serialColumnIndex = -1; //0;
        
            final JTable table = this.getTable(parentType, name, (Collection)value, serialColumnName, null);
            
//            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            final UIContext uiContext = this.app.getUIContext();
            
            uiContext.setTableFont(table);
            
            if(parentType != null) {
                uiContext.updateTableUI(table, parentType, serialColumnIndex);
            }
            
            component = table;
            
            component.setName(name);
            
        }else{
            
            component = super.getComponent(parentType, valueType, name, value);
        }
        
        return component;
    }
    
    public JTable getTable(Class parentType, String name, Collection values, 
            String serialColumnName, JTable outputIfNone) {
        
        final Class collectionGenericType = (Class)this.typeProvider.getGenericTypeArguments(parentType, name, null).get(0);

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Collection type: {0}#{1} with generic type: {2}", 
                    new Object[]{parentType.getName(), name, collectionGenericType.getName()});
        }

        final TableModel tableModel = this.getTableModel(values, collectionGenericType, serialColumnName, null);

        return tableModel == null ? outputIfNone : new JTable(tableModel);
    }
    
    public TableModel getTableModel(Collection values, Class valueType, 
            String serialColumnName, TableModel outputIfNone) {
        
        this.removeIds(values, valueType);
        
        final TableModel ref = new MapTableModel(values, serialColumnName);
        
        final int serialColumnIndex = serialColumnName == null ? - 1 : this.getColumnIndex(ref, serialColumnName);
        
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        
        final TableCellDisplayFormat cellDisplayFormat = new DefaultTableCellDisplayFormat(
                app.getOrException(SelectionContext.class), dateFormat, serialColumnIndex);
        
        final TableModelDisplayFormat displayFmt = new TableModelDisplayFormatImpl(cellDisplayFormat) {
            private final Map<String, Map> maps = new HashMap();
            @Override
            public Object toDisplayValue(Class columnClass, Object value, int row, int column) {
                if(value instanceof Map) {
                    final Map map = (Map)value;
                    final Collection c = map.values();
                    final String key = this.getKey(row, column);
                    maps.put(key, map);
                    return c;
                }
                return super.toDisplayValue(columnClass, value, row, column); 
            }

            @Override
            public Object fromDisplayValue(Class columnClass, Object displayValue, int row, int column) {
                if(displayValue instanceof Collection) {
                    final Object cached = this.maps.get(this.getKey(row, column));
                    if(cached != null) {
                        return cached;
                    }
                }
                return super.fromDisplayValue(columnClass, displayValue, row, column); 
            }
            public String getKey(int row, int column) {
                return ""+row+':'+column;
            }
        };
        
        final TableModel tableModel = new DisplayTableModelFromModel(ref, displayFmt);
        
        return tableModel;
    }
    
    public void removeIds(Collection values, Class valueType) {
        final String idColumnName = app.getJpaContext().getMetaData().getIdColumnName(valueType);
        final Consumer removeIds = (e) -> {
            final Map map = (Map)e;
            map.remove(idColumnName);
        };
        values.stream().forEach(removeIds);
    }
    
    public int getColumnIndex(TableModel ref, String columnName) {
        final int columnCount = ref.getColumnCount();
        for(int columnIndex=0; columnIndex<columnCount; columnIndex++) {
            if(ref.getColumnName(columnIndex).equals(columnName)) {
                return columnIndex;
            }
        }
        return -1;
    }
}
