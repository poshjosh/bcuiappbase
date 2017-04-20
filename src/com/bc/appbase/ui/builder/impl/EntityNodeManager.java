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

import com.bc.appbase.App;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.JpaMetaData;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 10, 2017 7:51:06 PM
 */
public class EntityNodeManager implements FromUIBuilderImpl.NodeManager {

    private static final Logger logger = Logger.getLogger(EntityNodeManager.class.getName());

    private final App app;

    private final Map<Class, EntityUpdater> entityUpdaters;

    private final Map<Class, Set<String>> entityColumnNames;

    public EntityNodeManager(App app, Predicate<String> persistenceUnitNameTest) { 
        this.app = Objects.requireNonNull(app);
        this.entityUpdaters = new HashMap<>();
        this.entityColumnNames = new HashMap<>(32, 0.75f);
        final JpaMetaData metaData = app.getJpaContext().getMetaData();
        final String [] puNames = metaData.getPersistenceUnitNames();
        for(String puName : puNames) {
            if(!persistenceUnitNameTest.test(puName)) {
                continue;
            }
            final Class [] puClasses = metaData.getEntityClasses(puName);
            for(Class puClass : puClasses) {
                final Set<String> columnNames = new HashSet(Arrays.asList(metaData.getColumnNames(puClass)));
                this.entityColumnNames.put(puClass, columnNames);
            }
        }
    }

    @Override
    public Set getKeys(Object parent, Object key, Object value) {
        final Class type;
        if(value != null) {
            type = value.getClass();
        }else{
            type = this.getEntityUpdater(parent.getClass()).getMethod(false, key.toString()).getReturnType();
        }
        return this.entityColumnNames.get(type);
    }

    @Override
    public boolean isParentKey(Object entity, Object key) {
        final Class valueType;
        final String name = key.toString();
        final EntityUpdater updater = this.getEntityUpdater(entity.getClass());
        final Object value = updater.getValue(entity, name);
        if(value != null) {
            valueType = value.getClass();
        }else{
            valueType = updater.getMethod(false, name).getReturnType();
        }
        return this.entityColumnNames.keySet().contains(valueType);
    }

    @Override
    public Object getValue(Object entity, Object key) {
        final Object value;
        if(entity == null) {
            value = null;
        }else{
            value = this.getEntityUpdater(entity.getClass()).getValue(entity, key.toString());
        }    
        return value;
    }

    @Override
    public Object add(Object entity, Object key, Object value) {
        final EntityUpdater updater = this.getEntityUpdater(entity.getClass());
        final String name = key.toString();
        final Object oldValue = updater.getValue(entity, name);
        updater.setValue(entity, name, value);
        return oldValue;
    }

    @Override
    public Object createContainerFor(Set names) {
        final Class entityType = this.getEntityType(names);
        return this.createContainerFor(entityType);
    }

    public Object createContainerFor(Class entityType) {
        try{
            return entityType.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityUpdater getEntityUpdater(Class type) {
        EntityUpdater entityUpdater = this.entityUpdaters.get(type);
        if(entityUpdater == null) {
            entityUpdater = app.getJpaContext().getEntityUpdater(type);
            this.entityUpdaters.put(type, entityUpdater);
        }
        return entityUpdater;
    }

    public Class getEntityType(Set columnNames) {
        for(Class entityType : this.entityColumnNames.keySet()) {
            if(this.entityColumnNames.get(entityType).containsAll(columnNames)) {
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Found entity type: {0} for columns: {1}", 
                            new Object[]{entityType.getName(), columnNames});
                }
                return entityType;
            }
        }
        throw new IllegalArgumentException("Could not determine the entity type for this set of column names: "+columnNames);
    }
}
