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

import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DatePanel;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.JCheckBoxMenuItemListComboBox;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.util.Selection;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.util.SelectionValues;
import com.bc.ui.table.cell.TableCellComponentModelImpl;
import com.bc.ui.table.cell.TableCellTextArea;
import java.awt.Component;
import java.awt.Font;
import java.awt.ItemSelectable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 11:05:40 AM
 */
public class ComponentModelImpl extends TableCellComponentModelImpl implements ComponentModel {

    private static final Logger logger = Logger.getLogger(ComponentModelImpl.class.getName());
    
    private final SelectionValues selectionValues;
    
    private final DateFromUIBuilder dateFromUIBuilder;
    
    private final DateUIUpdater dateUIUpdater;
    
    private final ComponentProperties componentProperties;

    private final int contentLengthAboveWhichTextAreaIsUsed;
    
    public ComponentModelImpl(ObjectFactory objectFactory) {
        this(objectFactory.getOrException(SelectionContext.class), 
                objectFactory.getOrException(DateFromUIBuilder.class), objectFactory.getOrException(DateUIUpdater.class));
    }
    
    public ComponentModelImpl(SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater) {
        this(selectionValues, dateFromUIBuilder, dateUIUpdater, 
                ComponentModel.ComponentProperties.DEFAULT, 50);
    }
    
    public ComponentModelImpl(SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater, 
            ComponentProperties componentProperties, int contentLengthAboveWhichTextAreaIsUsed) {
        this.selectionValues = Objects.requireNonNull(selectionValues);
        this.dateFromUIBuilder = Objects.requireNonNull(dateFromUIBuilder);
        this.dateFromUIBuilder.defaultHousrs(0).defaultMinutes(0);
        this.dateUIUpdater = Objects.requireNonNull(dateUIUpdater);
        this.componentProperties = Objects.requireNonNull(componentProperties);
        this.contentLengthAboveWhichTextAreaIsUsed = contentLengthAboveWhichTextAreaIsUsed;
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new ComponentModelImpl(this.selectionValues, 
                this.dateFromUIBuilder, this.dateUIUpdater, properties, 
                this.contentLengthAboveWhichTextAreaIsUsed);
    }

    @Override
    public Component getComponent(Class parentType, Class valueType, String name, Object value) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component component = this.doGetComponent(parentType, valueType, name, value);
        
        component.setName(name);
        
        this.setValue(component, value);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Name: {0}, value type: {1}, component type: {2}", 
                    new Object[]{name, valueType==null?null:valueType.getName(), component.getClass().getName()});
        }
        
        return component;
    }
    
    protected Component doGetComponent(Class parentType, Class valueType, String name, Object value) {
        
        logger.finer(() -> "doGetComponent(..) For type: " + (parentType==null?null:parentType.getName()) 
                + ", " + (valueType==null?"NULL":valueType.getSimpleName()) + ' ' + name + '=' + value);                        

        final Component component;
        
        List<Selection> selectionList;
        
        if (valueType == Boolean.class || valueType == boolean.class) {
            
            component = this.getBooleanComponent(valueType, name, value);
            
        }else if(Date.class.isAssignableFrom(valueType)) {
            
            component = this.getDateComponent(valueType, name, value);
            
        }else if( ! (selectionList = this.getSelectionValues(parentType, valueType, name, value)).isEmpty()) {
            
            logger.finer(() -> MessageFormat.format("{0} {1} = {2};\nSelection values: {3}", 
                    (valueType==null?"NULL":valueType.getSimpleName()), name, value, selectionList));
            
            component = this.getSelectionComponent(valueType, name, value, selectionList);
            
        }else if(this.isPasswordName(name)) {    
            
            component = this.getPasswordComponent(valueType, name, value);
            
        }else if(String.class.isAssignableFrom(valueType)) {    
            
            component = this.getTextComponent(valueType, name, value);
            
        }else{
            
            component = this.getFormattedTextComponent(valueType, name, value);
        }
        
        final ComponentProperties props = this.getComponentProperties();
        component.setFont(props.getFont(component));
        
        if(props.getWidth(component) > 0) {
            component.setPreferredSize(props.getDimension(component));
        }
        
        component.setEnabled(props.isEnabled(component));

        logger.fine(() -> "doGetComponent(..) Component: " + component.getClass().getName()+ 
                ", For type: " + (parentType==null?null:parentType.getName()) + 
                ", " + (valueType==null?"NULL":valueType.getSimpleName()) + ' ' + name + '=' + value); 
        
        return component;
    }
    
    @Override
    public boolean isPasswordName(String name) {
        return name.toLowerCase().contains("password");
    }

    @Override
    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
//System.out.println("getSelectionValues(..) "+valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                        
        final List<Selection> values = selectionValues.getSelectionValues(valueType);
        return values;
    }
    
    @Override
    public Object getValue(Component component, Object outputIfNone) {
        
        if(component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        
        Objects.requireNonNull(component);
        
        Object value;
        
        if(component instanceof DatePanel) {
            value = dateFromUIBuilder.ui(component).build(null);
        }else if(component instanceof JComboBox) {
            final Object [] selected = ((JComboBox)component).getSelectedObjects();
            value = selected == null ? null : this.getValue(Arrays.asList(selected));
        }else if(component instanceof JList) {
            final List selected = ((JList)component).getSelectedValuesList();
            value = this.getValue(selected);
        }else if(component instanceof JCheckBoxMenuItemListComboBox) {
            final JCheckBoxMenuItemListComboBox comboBox = (JCheckBoxMenuItemListComboBox)component;
            final List selected = comboBox.getSelectedValuesList();
            value = this.getValue(selected);
        }else{
            
            value = super.getValue(component, outputIfNone);
        }
        
        value = this.format(value);
//System.out.println(component.getClass().getName() + ", value: " + value + ". @" + this.getClass());        
        return value;
    }
    
    private Object getValue(List selected) {
        final Object value;
        if(selected == null) {
            value = null;
        }else if(selected.size() == 1) {
            value = this.toActualValue(selected.get(0));
        }else{
            value = this.toActualValues(selected);
        }
        return value;
    }
    
    public List toActualValues(List selected) {
        final List output;
        if(selected == null || selected.isEmpty()) {
            output = Collections.EMPTY_LIST;
        }else{
            output= new ArrayList(selected.size());
            for(Object sel : selected) {
                final Object actual = this.toActualValue(sel);
                output.add(actual);
            }
        }
        return output;
    }
    
    public Object toActualValue(Object selected) {
        final Object actual;
        if(selected instanceof Selection) {
            actual = ((Selection)selected).getValue();
        }else{
            actual = selected;
        }
        return actual;
    }
    
    @Override
    public Object setValue(Component component, Object value) {
        
        if(component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        
        Objects.requireNonNull(component);
        
        value = this.format(value);
        
        if(component instanceof DatePanel) {
            final DatePanel dateTimePanel = (DatePanel)component;
            final Calendar cal = Calendar.getInstance();
            if(value != null) {
                Date date = (Date)value;
                cal.setTime(date);
                this.dateUIUpdater.update(dateTimePanel, cal);
            }else{
                this.dateUIUpdater.updateMonth(dateTimePanel.getMonthCombobox(), cal);
                this.dateUIUpdater.updateYear(dateTimePanel.getYearCombobox(), cal);
            }
        }else if(component instanceof JComboBox) {
            ((JComboBox)component).setSelectedItem(Selection.from(component.getName(), value));
        }else if(component instanceof JList) {
            ((JList)component).setSelectedValue(Selection.from(component.getName(), value), true);
        }else if(component instanceof JCheckBoxMenuItemListComboBox) {
            final JCheckBoxMenuItemListComboBox jx = (JCheckBoxMenuItemListComboBox)component;
            jx.setSelectedValue(Selection.from(component.getName(), value));
        }else{
            value = super.setValue(component, value);
        }
        
        return value;
    }
    
    public Component getBooleanComponent(Class valueType, String name, Object value) {
        final JCheckBox component = new JCheckBox(name);
        return component;
    }
    
    public Component getDateComponent(Class valueType, String name, Object value) {
        final JPanel panel = new JPanel();
        final Font font = this.componentProperties.getFont(panel);
        final int height = this.componentProperties.getHeight(panel);
        final DatePanel dtp = new DatePanel(font, height, 78, height, 4);
        for(Component c : dtp.getComponents()) {
            if(c instanceof JTextField) {
                final JTextField tf = ((JTextField)c);
                tf.setEditable(this.componentProperties.isEditable(c));
//System.out.println("Editable: "+tf.isEditable()+", component: "+tf.getClass().getName()+". @"+this.getClass());                                        
            }
        }
        return dtp;
    }
    
    public Component getSelectionComponent(Class valueType, 
            String name, Object value, List<Selection> selectionList) {
        final JComboBox component = new JComboBox(selectionList.toArray(new Selection[0]));
        return component;
    }
    
    public Component getPasswordComponent(Class valueType, String name, Object value) {
        final JPasswordField component = new JPasswordField();
        return component;
    }
    
    public Component getFormattedTextComponent(Class valueType, String name, Object value) {
        
        final JTextComponent component = new JFormattedTextField();
        
        component.setEditable(this.componentProperties.isEditable(component));
//System.out.println("Editable: "+component.isEditable()+", component: "+component.getClass().getName()+". @"+this.getClass());                                        
        return component;
    }
    
    public Component getTextComponent(Class valueType, String name, Object value) {
        final JTextComponent component;
        if(this.getDisplaySize(valueType, name, value) <= this.contentLengthAboveWhichTextAreaIsUsed) {
            component = this.getTextField(valueType, name, value);
        }else{
            component = this.getTextArea(valueType, name, value);
        }
        
        component.setEditable(this.componentProperties.isEditable(component));
//System.out.println("Editable: "+component.isEditable()+", component: "+component.getClass().getName()+". @"+this.getClass());                                        
        return component;
    }

    public JTextField getTextField(Class valueType, String name, Object value) {
        final JTextField component = new JTextField();
        return component;
    }
    
    public JTextArea getTextArea(Class valueType, String name, Object value) {
        
        final JTextArea component = new TableCellTextArea();
        
        return component;
    }
    
    public int getDisplaySize(Class valueType, String name, Object value) {
        //@todo use app.getJpaContext() to compute display sizes
        // NOTE: app may be null
        return value == null ? -1 : value.toString().length();
    }
    
    public SelectionValues getSelectionValues() {
        return selectionValues;
    }

    public DateFromUIBuilder getDateFromUIBuilder() {
        return dateFromUIBuilder;
    }

    public DateUIUpdater getDateUIUpdater() {
        return dateUIUpdater;
    }

    @Override
    public ComponentProperties getComponentProperties() {
        return componentProperties;
    }
    
    public int getContentLengthAboveWhichTextAreaIsUsed() {
        return contentLengthAboveWhichTextAreaIsUsed;
    }
}
