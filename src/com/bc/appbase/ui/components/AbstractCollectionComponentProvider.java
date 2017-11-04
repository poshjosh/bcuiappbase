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
import com.bc.appcore.jpa.EntityStructureFactory;
import com.bc.appcore.typeprovider.TypeProvider;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 9, 2017 5:55:23 PM
 */
public abstract class AbstractCollectionComponentProvider implements CollectionComponentProvider {

    private static final Logger logger = Logger.getLogger(AbstractCollectionComponentProvider.class.getName());
    
    private final App app;
    private final TypeProvider typeProvider;
    private final ComponentModel.ComponentProperties componentProperties;
    private final int batchSize;

    public AbstractCollectionComponentProvider(App app, ComponentModel.ComponentProperties props, int batchSize) {
        this.app = Objects.requireNonNull(app);
        this.typeProvider = app.getOrException(TypeProvider.class);
        this.componentProperties = Objects.requireNonNull(props);
        this.batchSize = batchSize;
    }

    public List toEntitiesList(Collection values, Class valueType) {
        
        final List entities = new ArrayList(values.size());
        
        final EntityStructureFactory esf = app.getOrException(EntityStructureFactory.class);
        
        for(Object e : values) {
            
            final Object val;
            
            if(e instanceof Map) {
                final List list = esf.buildEntities(valueType, (Map)e);
                if(list.isEmpty()) {
                    throw new IllegalArgumentException("Cannot build an entity from: " + e);
                }
                val = list.get(list.size()-1);
            }else{
                throw new IllegalArgumentException("Expecting a Map, but found: " + e);
            }
            
            entities.add(val);
        }
        
        return entities;
    }
     
    public Dimension getCollectionDimension(Component component, Collection c) {
        final int size = c.size();
        final int width = componentProperties.getWidth(component);
        final int height = componentProperties.getHeight(component);
        final int n = size > batchSize ? batchSize : size;
        final Dimension dim = new Dimension(width, height * n);
        return dim;
    }
    
    public Class getCollectionValueType(Class parentType, String name) {
        
        final Class collectionGenericType = (Class)this.typeProvider.getGenericTypeArguments(parentType, name, null).get(0);

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Collection type: {0}#{1} with generic type: {2}", 
                    new Object[]{parentType.getName(), name, collectionGenericType.getName()});
        }

        return collectionGenericType;
    }

    public App getApp() {
        return app;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public TypeProvider getTypeProvider() {
        return typeProvider;
    }

    public ComponentModel.ComponentProperties getComponentProperties() {
        return componentProperties;
    }
}
