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

import com.bc.appbase.ui.ComponentModel;
import java.awt.Component;
import java.awt.Container;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 9:19:49 PM
 */
public class FromUIBuilderImpl<O> extends AbstractFromUIBuilder<Container, O> {

    private static final Logger logger = Logger.getLogger(FromUIBuilderImpl.class.getName());
    
    public interface NodeManager<T> {
    
        Set getKeys(T parent, Object key, T value);

        Object getValue(T node, Object key);

        boolean isParentKey(T node, Object key);

        T createContainerFor(Set keys);

        Object add(T node, Object key, Object value);
    }
    
    private final NodeManager nodeManager;

    public FromUIBuilderImpl(NodeManager nodeManager) {
        this.nodeManager = Objects.requireNonNull(nodeManager);
    }
    
    @Override
    protected O doBuild() {
        
        return (O)this.build(null, null, this.getSource(), this.getUi(), this.getTarget());
    }
    
    public Object build(Object parent, Object sourceKey, Object source, Container ui, Object target) {
        
        final Level level = Level.FINER;
        
        if(source == null) {
            source = target;
        }
        
        final Set keys = nodeManager.getKeys(parent, sourceKey, source);
        
        if(target == null) {
            target = nodeManager.createContainerFor(keys);
        }
        
        Objects.requireNonNull(target);

        if(source == null) {
            source = target;
        }
        
        logger.log(level, "Building into: {0}", target.getClass().getName());
        
        final ComponentModel cm = this.getComponentModel();
        
        logger.log(level, "Keys: {0}", keys);
        
        for(Object key : keys) {
            
            final String name = key.toString();
            
            final Component childUI = this.findChild(ui, name);
            
            if(childUI == null) {
                logger.log(level, "No UI component found for: {0}", key);
                continue;
            }
            
            final Object oldValue = nodeManager.getValue(source, key);
            
            final Object newValue;
            
            if(!nodeManager.isParentKey(source, key)) {
                
                newValue = cm.getValue(childUI);
                
            }else{
                
                if(logger.isLoggable(level)) {
                    logger.log(level, "{0}##{1} = {2}\nFrom: {3} to: null", 
                            new Object[]{source, key, oldValue, childUI.getClass().getName()});
                }
                
                final Object childContainer = build(source, key, oldValue, (Container)childUI, null);

                newValue = childContainer;
            }
            
            if(logger.isLoggable(level)) {
                logger.log(level, "Column: {0}, component type: {1}, value: {2}", 
                        new Object[]{key, childUI.getClass().getSimpleName(), newValue});
            }
            
            if(this.getFilter().accept(target, key, oldValue, newValue)) {
                
                nodeManager.add(target, key, newValue);
            }
        }
        
        return target;
    }
    
    public int getComponentCount(Component c) {
        int output;
        if(c instanceof Container) {
            output = ((Container)c).getComponentCount();
        }else{
            output = -1;
        }
        return output;
    }
    
    public Set<String> getChildNames(Component component) {
        final Set<String> childNames;
        if(component instanceof Container) {
            final Container container = (Container)component;
            final int count = container.getComponentCount();
            childNames = new HashSet(count);
            for(int i=0; i<count; i++) {
                final String childName = container.getComponent(i).getName();
                if(childName != null) {
                    childNames.add(childName);
                }
            }
        }else{
            childNames = Collections.EMPTY_SET;
        }
        return childNames;
    }

    public Component findChild(Component c, String name) {
        return this.findChildNamed(c, name, false);
    }
    
    public Component findChildNamed(Component c, String name, boolean inclusive) {
        Component output;
        if(inclusive && name.equals(c.getName())) {
            output = c;
        }else{
            output = null;
            if(c instanceof Container) {
                final Container parent = (Container)c;
                final int count = parent.getComponentCount();
                for(int i=0; i<count; i++) {
                    Component child = parent.getComponent(i);
                    output = this.findChildNamed(child, name, true);
                    if(output != null) {
                        break;
                    }
                }
            }
        }
        return output;
    }
}
