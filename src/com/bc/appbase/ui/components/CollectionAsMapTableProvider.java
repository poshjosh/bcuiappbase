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
import com.bc.appbase.ui.table.cell.DefaultTableCellDisplayFormat;
import com.bc.appbase.ui.table.model.TableModelDisplayFormatImpl;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.table.model.DisplayTableModelFromModel;
import com.bc.appcore.table.model.MapTableModel;
import com.bc.appcore.table.model.MapTableModelUpdatesDb;
import com.bc.appcore.table.model.TableModelDisplayFormat;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 9, 2017 6:13:47 PM
 */
public class CollectionAsMapTableProvider extends CollectionAsTableProvider {

    private final boolean editable = false;
    
    private final String serialColumnName = "Ser";
    
    public CollectionAsMapTableProvider(App app, ComponentModel.ComponentProperties props, int batchSize) {
        super(app, props, batchSize);
    }

    @Override
    public TableModel getTableModel(Collection values, Class valueType) {
        
        if(!editable) {
            this.removeIds(values, valueType);
        }

        final TableModel ref = !editable ? new MapTableModel(values, serialColumnName) : 
                new MapTableModelUpdatesDb(this.getApp().getActivePersistenceUnitContext(), valueType, values, serialColumnName);
        
        final int serialColumnIndex = serialColumnName == null ? - 1 : this.getColumnIndex(ref, serialColumnName);
        
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        
        final TableCellDisplayFormat cellDisplayFormat = new DefaultTableCellDisplayFormat(
                this.getApp().getOrException(SelectionContext.class), dateFormat, serialColumnIndex);
        
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
        final String idColumnName = this.getApp().getActivePersistenceUnitContext().getMetaData().getIdColumnName(valueType);
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
