/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance ui the License.
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

import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.components.ComponentWalker;
import com.bc.appbase.ui.components.ComponentWalkerImpl;
import java.awt.Component;
import java.awt.Container;
import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 9:19:49 PM
 */
public class MapFromUIBuilder extends AbstractFromUIBuilder<Container, Map> {

    private static final Logger logger = Logger.getLogger(MapFromUIBuilder.class.getName());
    
    public MapFromUIBuilder() { }
    
    @Override
    protected Map doBuild() {
        
        return (Map)this.build(null, null, this.getSource(), this.getUi(), this.getTarget());
    }
    
    public Map build(Object parent, Object sourceKey, Map source, Container ui, Map target) {
        
        final Level level = Level.FINER;
        
        if(source == null) {
            source = target;
        }
        
        final Set keys = source.keySet();
        
        if(target == null) {
            target = this.createMapContainerFor(keys);
        }
        
        Objects.requireNonNull(target);

        logger.log(level, "Building into: {0}", target.getClass().getName());
        
        final ComponentModel componentModel = this.getComponentModel();
        Objects.requireNonNull(componentModel);
        
        logger.log(level, "Keys: {0}", keys);
        
        final ComponentWalker cx = new ComponentWalkerImpl();
        
        for(Object key : keys) {
            
            final String name = key.toString();
            
            final Component childUI = cx.findFirstChild(ui, (Component c) -> name.equals(c.getName()), null);
            
            if(childUI == null) {
                logger.log(level, "No UI component found for: {0}", key);
                continue;
            }
            
            final Object oldValue = source.get(key);
            
            if(logger.isLoggable(level)) {
                logger.log(level, "UI type: {0}, name: {1}, old value: {2}", 
                        new Object[]{childUI.getClass().getName(), key, oldValue});
            }
            
            Object newValue;
            
            if(childUI instanceof ItemSelectable || childUI instanceof JList) {
                
                newValue = componentModel.getValue(childUI, null);
                
            }else 
            if(oldValue instanceof Collection) {
                
                newValue = this.getUpdate(level, source, key, (Collection)oldValue, childUI);

            }else if(oldValue instanceof Map) {    
                
                newValue = this.getUpdate(level, source, key, (Map)oldValue, childUI);
                
            }else{
                
                newValue = componentModel.getValue(childUI, null);
            }
            
            logger.log(level, "New value: {0}", newValue);
            
            if(this.getFilter().accept(target, key, oldValue, newValue)) {
                
                newValue = this.getFormatter().format(target, key, oldValue, newValue);
                
                target.put(key, newValue);
            }
        }
        
        return target;
    }
    
    public Map getUpdate(Level level, Map parent, Object key, Map oldValue, Component childUI) {
        
        final Map update = build(parent, key, oldValue, (Container)childUI, null);

        return update;
    }

    public Object getUpdate(Level level, Map parent, Object key, Collection oldValue, Component childUI) {
        
        final Object newValue;

        final Collection collection = (Collection)oldValue;

        if(collection.isEmpty()) {

            newValue = collection;

        }else if(collection.size() == 1){

            final Object only = collection.iterator().next();

            if(only instanceof Map) {

                final Object oval = this.getUpdate(level, parent, key, (Map)only, childUI);
                final Collection updates = this.createArrayContainer();
                updates.add(oval);
                newValue = updates;

            }else{

                newValue = collection;
            }
        }else{

            final Collection updates = this.createArrayContainer();
            
            for(Object e : collection) {
                
                if(e instanceof Map) {
                    
                    e = this.getUpdate(level, parent, key, (Map)e, childUI);
                }
                
                updates.add(e);
            }
            
            newValue = updates;
        }
        
        return newValue;
    }

    public Map createMapContainerFor(Set keys) {
        return new LinkedHashMap(keys.size(), 1.0f);
    }
    
    public Collection createArrayContainer() {
        return new ArrayList();
    }
}
