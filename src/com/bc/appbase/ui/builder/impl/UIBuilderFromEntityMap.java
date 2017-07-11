/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance sourceData the License.
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

import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.VerticalLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 5:15:16 PM
 */
public class UIBuilderFromEntityMap extends UIBuilderFromMapImpl {

    private static final Logger logger = Logger.getLogger(UIBuilderFromEntityMap.class.getName());
    
    private final Set<Class> builtEntityTypes;
        
    public UIBuilderFromEntityMap() {
        this(new VerticalLayout());
    }
    
    public UIBuilderFromEntityMap(SequentialLayout sequentialLayout) {
        super(sequentialLayout);
        this.builtEntityTypes = new HashSet();
    }

    @Override
    public boolean accept(Class type) {
        final boolean accepted = !this.builtEntityTypes.contains(type);
        return accepted;
    }
    
    public boolean addToBuiltEntityTypes(Class type) {
        if(type.getAnnotation(Entity.class) != null) {
            return this.builtEntityTypes.add(type);
        }else{
            return false;
        }
    }

    @Override
    public Collection<Component> getComponents(Class sourceType, Map source, Container container) {
        
//        this.addToBuiltEntityTypes(sourceType);
        
        final Collection<Component> components = super.getComponents(sourceType, source, container);
        
        return components;
    }

    @Override
    public Component getEntryUI(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType, Component outputIfNone) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component output;
        
        final boolean selectionType = this.getSelectionContext().isSelectionType(valueType);

        if(selectionType) {    

            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Selection type: {0}#{1}", new Object[]{sourceType.getName(), name});
            }

            output = this.getComponentModel().getComponent(sourceType, valueType, name, value); 
            
        }else{

            output = super.getEntryUI(parentContainer, sourceType, name, value, valueType, outputIfNone);
        }  
            
        return output == null ? outputIfNone : output;
    }
}
