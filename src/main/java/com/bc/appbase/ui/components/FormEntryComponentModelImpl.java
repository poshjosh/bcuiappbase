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

import com.bc.ui.builder.model.ComponentWalker;
import com.bc.ui.builder.model.ComponentModel.ComponentProperties;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appbase.ui.FormEntryPanel;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Component;
import java.util.Objects;
import com.bc.selection.Selection;
import com.bc.ui.builder.model.impl.ComponentWalkerImpl;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import javax.swing.JList;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 26, 2017 10:57:32 PM
 */
public class FormEntryComponentModelImpl implements FormEntryComponentModel {

    private static final Logger logger = Logger.getLogger(FormEntryComponentModelImpl.class.getName());
    
    private final int labelWidth;
    
    private final ComponentModel componentModel;
    
    private final ThirdComponentProvider thirdComponentProvider;

    public FormEntryComponentModelImpl(ComponentModel componentModel) {
        this(componentModel, -1, ThirdComponentProvider.PROVIDE_NONE);
    }
    
    public FormEntryComponentModelImpl(ComponentModel componentModel, int labelWidth, 
            ThirdComponentProvider thirdComponentProvider) {
        this.componentModel = Objects.requireNonNull(componentModel);
        this.labelWidth = labelWidth;
        this.thirdComponentProvider = Objects.requireNonNull(thirdComponentProvider);
    }

    @Override
    public Dimension computeMaxSize(Collection<Component> children, Font font) {
        return this.componentModel.computeMaxSize(children, font);
    }

    @Override
    public double computeMaxWidth(Collection<Component> children, Font font) {
        return this.componentModel.computeMaxWidth(children, font);
    }

    @Override
    public boolean isPasswordName(String name) {
        return this.componentModel.isPasswordName(name);
    }

    @Override
    public JPanel getComponent(Class parentType, Class valueType, String name, Object value) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component component = componentModel.getComponent(parentType, valueType, name, value);
        
        final JScrollPane scrolls = this.getEntryComponentScrolls(valueType, name, value, component, null);
        if(scrolls != null) {
            scrolls.setViewportView(component);
        }
        
        final ComponentModel.ComponentProperties props = componentModel.getComponentProperties();
        final int width = props.getWidth(component);
        final int height = props.getHeight(component);
        
        final JLabel label = this.getLabel(valueType, name, value);
        
        final FormEntryPanel ui = new FormEntryPanel(
                label, this.labelWidth == -1 ? width : this.labelWidth, props.getHeight(label), 
                scrolls != null ? scrolls : component, width, height, 
                props.getFont(component).getName(), 
                this.thirdComponentProvider.get(parentType, valueType, name, value, label, component, null)
        );
        
        try{
            
            final Class typePersistenceGeneratedValue = Class.forName("javax.persistence.GeneratedValue");
            
            final Field field = parentType.getDeclaredField(name);
            if(field.getAnnotation(typePersistenceGeneratedValue) != null) {
                final ComponentWalker componentWalker = new ComponentWalkerImpl();
                componentWalker.transverseChildren(component, true, (comp) -> { comp.setEnabled(false); });
            }
        }catch(ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            logger.fine(() -> "Encountered exception while trying to access field named: "+name+", in type: "+parentType+". Exception: "+e);
        }
        
        ui.initComponents();
//        ui.getEntryComponent().setName(name);
//        cm.setValue(ui.getEntryComponent(), value);
        
        return ui;
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new FormEntryComponentModelImpl(this.componentModel.deriveNewFrom(properties), this.labelWidth, this.thirdComponentProvider);
    }

    @Override
    public ComponentProperties getComponentProperties() {
        return componentModel.getComponentProperties();
    }

    @Override
    public Object getValue(Component component, Object outputIfNone) {
        if(component instanceof FormEntryPanel) {
            FormEntryPanel formEntryPanel = (FormEntryPanel)component;
            final Component actual = formEntryPanel.getEntryComponent();
            component = actual;
        }
        return componentModel.getValue(component, outputIfNone);
    }

    @Override
    public <T> T setValue(Component component, T value) {
        if(component instanceof FormEntryPanel) {
            final FormEntryPanel formEntryPanel = (FormEntryPanel)component;
            final Component actual = formEntryPanel.getEntryComponent();
            component = actual;
        }
        return componentModel.setValue(component, value);
    }

    @Override
    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
        final List<Selection> values = componentModel.getSelectionValues(parentType, valueType, name, value);
        return values;
    }
    
    public JScrollPane getEntryComponentScrolls(Class valueType, 
            String name, Object value, Component component, JScrollPane outputIfNone) {
        int size;
        if(component instanceof JTextArea){
            size = ((JTextArea)component).getRows();
        }else if(component instanceof JList) { 
            size = ((JList)component).getModel().getSize();
        }else if(component instanceof JTable) { 
            size = ((JTable)component).getRowCount();
        }else{
            size = 1;
        }
        return size > 1 ? new JScrollPane() : outputIfNone;
    }
    
    public JLabel getLabel(Class valueType, String name, Object value) {
        return new JLabel(this.getLabelText(valueType, name, value));
    }
    
    public String getLabelText(Class valueType, String name, Object value) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public int getLabelWidth() {
        return labelWidth;
    }

    public ComponentModel getComponentModel() {
        return componentModel;
    }

    public ThirdComponentProvider getThirdComponentProvider() {
        return thirdComponentProvider;
    }
}
