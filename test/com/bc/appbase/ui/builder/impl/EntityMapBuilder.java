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

package com.bc.appbase.ui.builder.impl;

import com.bc.appcore.TypeProvider;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.GeneratedValue;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2017 12:53:28 PM
 */
public class EntityMapBuilder<E> {
    
    private transient static final Logger logger = Logger.getLogger(EntityMapBuilder.class.getName());
    
    private final JpaContext jpaContext;
    
    private final Set<Class> entityTypes;
    
    private final Set<Class> parsedEntityTypes;
    
    private final Map<Class, Set<String>> columnTypes;
    
    public EntityMapBuilder(JpaContext jpaContext) {
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.parsedEntityTypes = new HashSet();
        this.columnTypes = new HashMap(32, 0.75f);
        final JpaMetaData metaData = jpaContext.getMetaData();
        final String [] puNames = metaData.getPersistenceUnitNames();
        this.entityTypes = new HashSet();
        for(String puName : puNames) {
            Class [] puClasses = metaData.getEntityClasses(puName);
            this.entityTypes.addAll(Arrays.asList(puClasses));
        }
    }

    public Set<Class> getTypes(String name, Object value) {
        final Set<Class> output = new HashSet();
        final Set<Entry<Class, Set<String>>> entrySet = columnTypes.entrySet();
        for(Entry<Class, Set<String>> entry : entrySet) {
            if(entry.getValue().contains(name)) {
                output.add(entry.getKey());
            }
        }
        return output.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(output);
    }

    public Map<String, Object> getMap(Class<E> entityType, E entity, boolean recursive) {
    
        Map map = this.getMap(entityType, entity, new int[]{
            ResultSetMetaData.columnNoNulls, ResultSetMetaData.columnNullableUnknown, ResultSetMetaData.columnNullable
        }, recursive);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Entity type: {0}, entity: {1}, recursive: {2}\nNames: {3}", 
                    new Object[]{entityType.getName(), entity, recursive, map.keySet()});
        }
        return map;
    }
    
    public Map<String, Object> getMap(Class<E> entityType, E entity, int [] columnNullables, boolean recursive) {
        final Map<String, Object> map = new HashMap();
        for(int columnNullable : columnNullables) {
            final Map m = this.getMap(entityType, entity, columnNullable, recursive);
            map.putAll(m);
        }
        return map;
    }
    
    public Map<String, Object> getMap(Class entityType, Object entity, int columnNullable, boolean recursive) {
        
        final Map<String, Object> map;
        
        if(this.parsedEntityTypes.contains(entityType)) {
            
            map = Collections.EMPTY_MAP;
            
        }else{
            
            this.parsedEntityTypes.add(entityType);
            
            map = new HashMap();
            
            final JpaMetaData metaData = this.jpaContext.getMetaData();
            
            final String idColumnName = metaData.getIdColumnName(entityType);
            final List<String> generatedFields = this.getGeneratedFields(entityType);
            
            final String [] columnNames = metaData.getColumnNames(entityType);
            final int [] columnNullables = metaData.getColumnNullables(entityType);

            final EntityUpdater updater = this.jpaContext.getEntityUpdater(entityType);
            final TypeProvider typeProvider = new EntityTypeProvider(updater);
            
            for(String columnName : columnNames) {

                if(generatedFields.contains(columnName)) {
                    continue;
                }
                
                if(idColumnName.equals(columnName)) {
                    continue;
                }

                final int columnIndex = metaData.getColumnIndex(entityType, columnName);

                if(columnNullables[columnIndex] == columnNullable) {

                    final Object value = entity == null ? null : updater.getValue(entity, columnName);
                    final Class valueType = typeProvider.getType(columnName, value, value==null?null:value.getClass());
                    
                    Set<String> colNames = columnTypes.get(valueType);
                    if(colNames == null) {
                        colNames = new HashSet();
                        columnTypes.put(valueType, colNames);
                    }
                    colNames.add(columnName);

                    if(logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "{0}#{1} has type of {2}", 
                                new Object[]{entityType.getName(), columnName, valueType.getName()});
                    }

                    if(entityTypes.contains(valueType)) {
                        if(recursive) {
                            final Map<String, Object> valueMap = this.getMap(
                                    valueType, value, columnNullable, recursive);
                            map.putAll(valueMap);
                        }
                    }else{
                        map.put(columnName, value);
                    }
                }
            }
        }
        
        return map;
    }
    
    public List<String> getGeneratedFields(Class<E> type) {
        final List<String> generatedFields = new ArrayList();
        final Field [] fields = type.getDeclaredFields();
        for(Field field : fields) {
            GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
            if(gv != null) {
                generatedFields.add(field.getName());
            }
        }
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Type: {0}, generated fields: {1}", new Object[]{type, generatedFields});
        }        
        return generatedFields;
    }

    public Map<Class, Set<String>> getColumnTypes() {
        return columnTypes;
    }
}
