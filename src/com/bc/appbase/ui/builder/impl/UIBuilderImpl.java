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

import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.VerticalLayout;
import com.bc.appbase.ui.builder.UIBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 12:28:40 PM
 */
public class UIBuilderImpl<T> extends AbstractUIBuilder<UIBuilder<T, Container>, T> 
        implements UIBuilder<T, Container> {
    
    private int depth;
    
    private static final Logger logger = Logger.getLogger(UIBuilderImpl.class.getName());
    
    private final BiFunction<Class, T, Map> toMap;
    
    private final SequentialLayout sequentialLayout;

    public UIBuilderImpl(BiFunction<Class, T, Map> toMap, SequentialLayout sequentialLayout) {
        this.toMap = Objects.requireNonNull(toMap);
        this.sequentialLayout = Objects.requireNonNull(sequentialLayout);
    }
    
    @Override
    public boolean build(Class sourceType, T source, Container container) {
        
        logger.log(Level.FINE, "Building: {0}", sourceType);
        
        final boolean output;
        
        if(!this.accept(sourceType)) {
        
            logger.log(Level.FINER, "Filter rejected {0}", sourceType.getName());

            output = false;
            
        }else{
            
            final Collection<Component> components = this.getComponents(sourceType, source, container);

            this.sequentialLayout.addComponents(container, components);
            
            output = true;
        }
        
        return output;
    }
    
    public boolean accept(Class type) {
        return true;
    }
    
    public Map toMap(Class type, T instance) {
        if(instance instanceof Map) {
            return (Map)instance;
        }else{
            return toMap.apply(type, instance);
        }
    }
    
    public boolean isParent(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType) {
        return false;
    }
    
    public Collection<Component> getComponents(Class sourceType, T source, Container parentContainer) {
        
        final List<Component> components = new ArrayList();
        
        final Map map = this.toMap(sourceType, source);

        final Set entries = map.entrySet(); 

        for(Object oval : entries) {

            final Map.Entry entry = (Map.Entry)oval;

            final String name = entry.getKey().toString();
            final Object value = entry.getValue();

            final Class valueType = this.getTypeProvider().getType(sourceType, name, value, null);
            Objects.requireNonNull(valueType, "Failed to resolve type of "+sourceType.getName()+"#"+name+" = "+value+", using "+this.getTypeProvider().getClass().getName());

            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0}#{1} has type: {2}", new Object[]{sourceType.getName(), name, valueType});
            }

            final Component entryUI = this.getEntryUIMain(parentContainer, sourceType, name, value, valueType, null);

            if(entryUI != null) {

                logger.log(Level.FINER, "Adding ui: {0}", entryUI);
                
                components.add(entryUI);
            }
        }
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Type: {0} keys: {1}, components: {2}", 
                    new Object[]{sourceType.getName(), map.keySet(), components.size()});
        }
        
        return components;
    }
    
    public Component getEntryUIMain(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType, Component outputIfNone) {

        logger.finer(() -> MessageFormat.format("For {0}, {1} {2} = {3}", 
                sourceType.getSimpleName(), valueType.getSimpleName(), name, value));
        
        final String type;
        
        final Component entryUI;

        if(this.isParent(parentContainer, sourceType, name, value, valueType)) {
            
            type = "Container type";
            
            entryUI = this.getEntryUIForParent(parentContainer, sourceType, name, (T)value, valueType, outputIfNone);

        }else if(value instanceof Collection) {
            
            type = "Collection type";
            
            final Collection collection = (Collection)value;
            
            final boolean singleElement =  collection.size() < 2;

            if(singleElement) { 
                
                entryUI = this.getEntryUIForCollection(parentContainer, sourceType, 
                        name, collection, valueType, outputIfNone);
            }else{
                entryUI = this.getComponentModel().getComponent(sourceType, valueType, name, value);
            }        
        }else{

            type = "Instance type";

            entryUI = this.getComponentModel().getComponent(sourceType, valueType, name, value);
        }
        
        logger.finer(() -> MessageFormat.format("{0} for {1}, {2} {3} = {4}", 
                type, sourceType.getSimpleName(), valueType.getSimpleName(), name, value));
        
        return entryUI == null ? outputIfNone : entryUI;
    }
    
    public Component getEntryUIForParent(Container parentContainer, Class sourceType, 
            String name, T child, Class valueType, Component outputIfNone) {
        
        final Level level = Level.FINER;
        
        final Component entryUI;

        if(!this.accept(valueType)) {

            if(logger.isLoggable(level)) {
                logger.log(level, "Filter rejected {0}#{1} with type: {2}", new Object[]{sourceType.getName(), name, valueType});
            }

            entryUI = null;

        }else{

            final Container childUI = this.createContainer(valueType, child, name);

            ++depth;

            if(this.build(valueType, child, childUI)) {

                childUI.setName(name);

                if(logger.isLoggable(level)) {
                    logger.log(level, "Set name of: {0} to: {1}", new Object[]{childUI.getClass().getName(), name});
                }

                this.setSubContainerBorder(parentContainer, childUI, name);

                entryUI = childUI;

            }else{

                throw new UnsupportedOperationException("Failed to build ui for type "+valueType+", with data names: "+toMap(valueType, child).keySet());
            }

            --depth;
        }
        
        return entryUI == null ? outputIfNone : entryUI;
    }

    public Component getEntryUIForCollection(Container parentContainer, Class sourceType, 
            String name, Collection value, Class valueType, Component outputIfNone) {
        
        final Level level = Level.FINER;
        
        final Component entryUI;

        final Class collectionGenericType = (Class)this.getTypeProvider().getGenericTypeArguments(sourceType, name, null).get(0);

        if(logger.isLoggable(level)) {
            logger.log(level, "Collection type: {0}#{1} with generic type: {2}", 
                    new Object[]{sourceType.getName(), name, collectionGenericType.getName()});
        }

        final String genericName = collectionGenericType.getSimpleName();

        if(!this.accept(collectionGenericType)) {

            entryUI = null;

        }else{

            ++depth;

            final List<Component> components = new ArrayList();

            final VerticalLayout verticalLayout = new VerticalLayout();

            final Collection collection = (Collection)value;

            for(Object e : collection) {

                final Component c = this.getEntryUIMain(parentContainer, valueType, genericName, e, collectionGenericType, null);

                if(c != null) {
                    components.add(c);
                }
            }

            final Container container = this.createContainer(valueType, value, name); 

            verticalLayout.addComponents(container, components);

            container.setName(name);

            this.setSubContainerBorder(parentContainer, container, null);

            entryUI = container;

            --depth;
        }
        
        return entryUI == null ? outputIfNone : entryUI;
    }
    
    public void setSubContainerBorder(Container parentContainer, Container container, String name) {
        
        container.setBackground(this.getBackground(parentContainer, container, this.depth));
        
        if(container instanceof JComponent) {
            final boolean noname = name == null || name.isEmpty();
            final Border border;
            if(noname) {
                border = new BevelBorder(BevelBorder.RAISED);
            }else{
                final BevelBorder bevelBorder = new BevelBorder(BevelBorder.RAISED);
                final String title = Character.toTitleCase(name.charAt(0))+name.substring(1);
                final Font font = Font.decode("MONOSPACED-BOLD-24");
                border = new TitledBorder(bevelBorder, title, 
                        TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, font);
            }
            ((JComponent)container).setBorder(border);
        }
    }
    
    public Color getBackground(Container parentContainer, Container container, int depth) {
        if(true) {
            return parentContainer.getBackground();
        }
        final Color parentColor = parentContainer.getBackground();
        if(parentColor == null) {
            return null;
        }else{
            Color color = parentColor;
            while(depth > 0) {
                color = color.brighter();
                --depth;
            }
            return color;
        }
    }
}
