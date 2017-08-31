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

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.FormEntryPanel;
import com.bc.appbase.ui.builder.FormEntryComponentModel;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appcore.util.Selection;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.persistence.GeneratedValue;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 27, 2017 8:40:34 PM
 */
public class FormEntryComponentModelImplOld implements FormEntryComponentModel {

    private static final Logger logger = Logger.getLogger(FormEntryComponentModelImpl.class.getName());
    
    private final int labelWidth;
    
    private final ComponentModel componentModel;
    
    private final ThirdComponentProvider thirdComponentProvider;

    public FormEntryComponentModelImplOld(ComponentModel componentModel) {
        this(componentModel, -1, ThirdComponentProvider.PROVIDE_NONE);
    }
    
    public FormEntryComponentModelImplOld(ComponentModel componentModel, int labelWidth, 
            ThirdComponentProvider thirdComponentProvider) {
        this.componentModel = Objects.requireNonNull(componentModel);
        this.labelWidth = labelWidth;
        this.thirdComponentProvider = Objects.requireNonNull(thirdComponentProvider);
    }

    @Override
    public boolean isPasswordName(String name) {
        return this.componentModel.isPasswordName(name);
    }

    @Override
    public JPanel getComponent(Class parentType, Class valueType, String name, Object value) {
//System.out.println("getComponent(..) "+valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component component = componentModel.getComponent(parentType, valueType, name, value);
        
        final ComponentModel.ComponentProperties props = componentModel.getComponentProperties();
        final int width = props.getWidth(component);
        final int height = props.getHeight(component);
        
        final JScrollPane scrolls = this.getEntryComponentScrolls(valueType, name, value, component, null);
        if(scrolls != null) {
            scrolls.setPreferredSize(new Dimension(width, height));
        }
        
        final JLabel label = this.getLabel(valueType, name, value);
        
        final FormEntryPanel ui = new FormEntryPanel(
                label, this.labelWidth == -1 ? props.getWidth(component) : this.labelWidth, props.getHeight(label), 
                component, width, height, 
                props.getFont(component).getName(), 
                this.thirdComponentProvider.get(parentType, valueType, name, value, label, component, null)
        );
        
        try{
            final Field field = parentType.getDeclaredField(name);
            if(field.getAnnotation(GeneratedValue.class) != null) {
                component.setEnabled(false);
            }
        }catch(NoSuchFieldException | SecurityException e) {
            logger.warning("Encountered exception while trying to access field named: "+name+", in type: "+parentType+". Exception: "+e);
        }
        
        if(scrolls != null) {
            ui.setEntryComponentScrollPane(scrolls);
        }
        
        ui.initComponents();
//        ui.getEntryComponent().setName(name);
//        cm.setValue(ui.getEntryComponent(), value);
        
        return ui;
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentModel.ComponentProperties properties) {
        return new FormEntryComponentModelImpl(this.componentModel.deriveNewFrom(properties), this.labelWidth, this.thirdComponentProvider);
    }

    @Override
    public ComponentModel.ComponentProperties getComponentProperties() {
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
//System.out.println("getSelectionValues(..) "+valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                                
        final List<Selection> values = componentModel.getSelectionValues(parentType, valueType, name, value);
        return values;
    }
    
    public JScrollPane getEntryComponentScrolls(Class valueType, 
            String name, Object value, Component component, JScrollPane outputIfNone) {
        if(component instanceof JTextArea || component instanceof JTable){
            return new JScrollPane();
        }else{
            return outputIfNone;
        }
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
