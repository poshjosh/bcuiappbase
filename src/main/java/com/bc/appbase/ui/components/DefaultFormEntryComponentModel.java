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

import com.bc.ui.builder.model.ComponentModel;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appcore.jpa.model.ColumnLabelProvider;
import com.bc.reflection.TypeProvider;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.context.PersistenceUnitContext;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 24, 2017 10:36:44 AM
 */
public class DefaultFormEntryComponentModel extends FormEntryComponentModelImpl {

    private static final Logger logger = Logger.getLogger(DefaultFormEntryComponentModel.class.getName());

    private final PersistenceUnitContext puContext;
    private final TypeProvider typeProvider;
    private final ColumnLabelProvider columnLabelProvider;
    private final Map<Class, int[]> _$cnullables;
    private final Map<Class, List<String>> _$cnames;
    
    public DefaultFormEntryComponentModel(
            PersistenceUnitContext puContext, TypeProvider typeProvider, 
            ComponentModel componentModel, int labelWidth, 
            ColumnLabelProvider columnLabelProvider, ThirdComponentProvider thirdComponentProvider) {
        super(componentModel, labelWidth, thirdComponentProvider);
        this.puContext = Objects.requireNonNull(puContext);
        this.typeProvider = typeProvider;
        this.columnLabelProvider = columnLabelProvider;
        this._$cnullables = new HashMap<>();
        this._$cnames = new HashMap<>();
    }
    
    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new DefaultFormEntryComponentModel(this.puContext, this.typeProvider, 
                this.getComponentModel().deriveNewFrom(properties), this.getLabelWidth(), 
                this.columnLabelProvider, this.getThirdComponentProvider());
    }
    
    @Override
    public String getLabelText(Class valueType, String name, Object value) {
//System.out.println("================ "+valueType.getSimpleName()+" "+name+". @"+this.getClass());        
        final Class entityType = this.getParentType(valueType, name, value, null);
        
        final List columnNames = entityType == null ? Collections.EMPTY_LIST : this.getColumnNames(entityType);
//System.out.println("================ Entity type: "+entityType+", columnNames: "+columnNames+". @"+this.getClass());                                       

        final String columnLabel = entityType == null ? name: this.columnLabelProvider.getColumnLabel(entityType, name);
        
        final String labelText;
        
        if(columnNames.indexOf(name) == -1) {
            
            labelText = columnLabel;
            
        }else{
            
//System.out.println("================ name "+name+", label: "+columnLabel+". @"+this.getClass());                
            final int [] columnNullables = this.getColumnNullables(entityType);
//System.out.println("================ Column nullables: "+Arrays.toString(columnNullables)+". @"+this.getClass());                                       
            final int columnNullable = columnNullables[columnNames.indexOf(name)];

            if(columnNullable == ResultSetMetaData.columnNoNulls) {
                labelText = "<html><span style=\"color:red; font-weight:900;\">*&nbsp;</span>" + columnLabel + "</html>";
            }else{
                labelText = columnLabel;
            }
        }
        return labelText;
    }
    
    public List<String> getColumnNames(Class entityType) {
        final List<String> columnNames;
        final List<String> cached = this._$cnames.get(entityType);
        if(cached != null) {
            columnNames = cached;
        }else{
            columnNames = Arrays.asList(puContext.getMetaData().getColumnNames(entityType));
            Objects.requireNonNull(columnNames);
            this._$cnames.put(entityType, columnNames);
        }
        return columnNames;
    }
    
    public Class getParentType(Class valueType, String name, Object value, Class outputIfNone) {
        final List<Class> parentTypeList = this.typeProvider.getParentTypeList(valueType, name, value);
        final Class parentType = parentTypeList.isEmpty() ? outputIfNone : parentTypeList.get(0);
        return parentType;
    }
    
    public int [] getColumnNullables(Class entityType) {
        final int [] columnNullables;
        final int [] cached = this._$cnullables.get(entityType);
        if(cached != null) {
            columnNullables = cached;
        }else{
            columnNullables = puContext.getMetaData().getColumnNullables(entityType);
            Objects.requireNonNull(columnNullables);
            this._$cnullables.put(entityType, columnNullables);
        }
        return columnNullables;
    }

    public PersistenceUnitContext getPersistenceUnitContext() {
        return puContext;
    }

    public TypeProvider getTypeProvider() {
        return typeProvider;
    }

    public ColumnLabelProvider getColumnLabelProvider() {
        return columnLabelProvider;
    }
}
