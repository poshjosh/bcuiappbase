/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance source the License.
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

import com.bc.appbase.ui.VerticalLayout;
import com.bc.appcore.TypeProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 5:15:16 PM
 */
public class UIBuilderFromMap extends AbstractUIBuilder<Map, Container> {

    private static final Logger logger = Logger.getLogger(UIBuilderFromMap.class.getName());
    
    private final Set<Map> alreadyBuilt;
    
    public UIBuilderFromMap() {
        this.alreadyBuilt = new HashSet();
    }
    
    public Container createContainer(Map map, String name) {
        final JPanel panel = new JPanel();
        return panel;
    }
    
    @Override
    protected Container doBuild() {
        
        if(this.getTarget() == null) {
            this.target(this.createContainer(this.getSource(), null));
        }
        
        final Container container = this.build(this.getSource(), null, this.getTarget());

        return container;
    }
    
    public Container build(Map source, String title, Container container) {
        
        final Level level = Level.FINER;
        
        this.alreadyBuilt.add(source);
        
        final List<Component> components = new ArrayList();
        
        if(title != null) {
            components.add(new JLabel(title));
        }
        
        final Set entries = source.entrySet();
        
        for(Object oval : entries) {
            
            final Map.Entry entry = (Map.Entry)oval;
            
            final String name = entry.getKey().toString();
            final Object value = entry.getValue();
            
            final Component entryUI;
            
            if(value instanceof Map) {
                
                final Map child = (Map)value;
                
                if(this.alreadyBuilt.contains(child)) {
                    
                    logger.log(level, "Already built: {0}", child);
                    
                    continue;
                    
                }else{
                    
                    final Container childUI = this.build(child, name, this.createContainer(child, name));
                    
                    childUI.setName(name);
                    
                    if(childUI instanceof JComponent) {
                        ((JComponent)childUI).setBorder(new javax.swing.border.LineBorder(Color.GRAY, 1));
                    }
                    
                    entryUI = childUI;
                }
            }else{
                
                final TypeProvider tp = this.getTypeProvider();
                
                final Class type = tp.getType(name, value, value==null?null:value.getClass());

                if(logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER, "{0}#getType({1}, {2}, {3}) = {4}", 
                            new Object[]{tp.getClass().getName(), name, value,
                                value==null?null:value.getClass().getName(), 
                                type==null?null:type.getName()});
                }
                
                Objects.requireNonNull(type);
                
                entryUI = this.getEntryUIProvider().getEntryUI(type, name, value);
            }
            
            logger.log(level, "Set name of: {0} to: {1}", new Object[]{entryUI.getClass().getName(), name});
            
            components.add(entryUI);
        }
        
        new VerticalLayout().addComponents(container, components);

        return container;
    }
}
