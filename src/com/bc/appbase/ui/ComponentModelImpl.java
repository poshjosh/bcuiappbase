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

package com.bc.appbase.ui;

import com.bc.appbase.App;
import com.bc.appcore.jpa.Selection;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.jpa.SelectionImpl;
import com.bc.table.cellui.TableCellTextArea;
import com.bc.table.cellui.TestSubClass;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.ItemSelectable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 11:05:40 AM
 */
public class ComponentModelImpl implements ComponentModel {

    private static final Logger logger = Logger.getLogger(ComponentModelImpl.class.getName());
    
    private final App app;
    
    private final SelectionContext selectionContext;
    
    private final DateFromUIBuilder dateFromUIBuilder;
    
    private final DateUIUpdater dateUIUpdater;

    private final Font font;
    
    private final int width;
    
    private final int height;
    
    private final int contentLengthAboveWhichTextAreaIsUsed;
    
    private final BiFunction<Class, Class, Boolean> subClassTest;
    
    public ComponentModelImpl(App app) {
        this(app, app.get(SelectionContext.class), app.get(DateFromUIBuilder.class), app.get(DateUIUpdater.class));
    }
    
    public ComponentModelImpl(
            App app, SelectionContext selectionContext, DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater) {
        this(app, selectionContext, dateFromUIBuilder, dateUIUpdater, new Font(Font.MONOSPACED, Font.PLAIN, EntryPanel.deriveFontSize(40)), -1, 40, 50);
    }
    
    public ComponentModelImpl(
            App app, SelectionContext selectionContext, DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater, 
            Font font, int width, int height, int contentLengthAboveWhichTextAreaIsUsed) {
        this.app = app;
        this.selectionContext = Objects.requireNonNull(selectionContext);
        this.dateFromUIBuilder = Objects.requireNonNull(dateFromUIBuilder);
        this.dateUIUpdater = Objects.requireNonNull(dateUIUpdater);
        this.font = Objects.requireNonNull(font);
        this.width = width;
        this.height = height;
        this.contentLengthAboveWhichTextAreaIsUsed = contentLengthAboveWhichTextAreaIsUsed;
        this.subClassTest = new TestSubClass();
    }

    @Override
    public Component getComponent(Class valueType, String name, Object value) {
        final Component component;
        Selection [] values;
        if (valueType == Boolean.class || valueType == boolean.class) {
            final JCheckBox checkBox = new JCheckBox();
            component = checkBox;
        }else if(this.subClassTest.apply(valueType, Date.class)) {
            final DateTimePanel datePanel = new DateTimePanel(font, this.height, 78, this.height, 4);
            component = datePanel;
        }else if((values = selectionContext.getSelectionValues(valueType)).length > 0) {
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Value type: {0}, {1}={2}\nSelection values: {3}", 
                        new Object[]{valueType.getName(), name, value, values==null?null:Arrays.toString(values)});
            }
            
            final JComboBox<Selection> comboBox = new JComboBox<>(values);
            component = comboBox;
            if(value != null) {
                for(Selection selection : values) {
                    if(value.equals(selection.getValue())) {
                        comboBox.setSelectedItem(selection);
                        break;
                    }
                }
            }
        }else{
            component = this.getTextComponent(valueType, name, value);
        }
        
        component.setName(name);
        
        this.setValue(component, value);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Name: {0}, value type: {1}, component type: {2}", 
                    new Object[]{name, valueType==null?null:valueType.getName(), component.getClass().getName()});
        }
        return component;
    }
    
    @Override
    public Object getValue(Component component) {
        return this.getValue(component, null);
    }
    
    @Override
    public <T> T getValue(Component component, Class<T> type) {
        Objects.requireNonNull(component);
        final Object value;
        if(component instanceof JTextComponent) {
            value = ((JTextComponent)component).getText();
        }else if(component instanceof AbstractButton) {
            value = ((AbstractButton)component).isSelected();
        }else if(component instanceof DateTimePanel) {
            value = dateFromUIBuilder.ui(component).build(null);
        }else if(component instanceof ItemSelectable) {
            final Object [] selected = ((ItemSelectable)component).getSelectedObjects();
            value = selected == null || selected.length == 0 ? null : 
                    selected[0] instanceof Selection ? ((Selection)selected[0]).getValue() : selected[0];
        }else if(component instanceof JList) {
            final Object selected = ((JList)component).getSelectedValue();
            value = selected == null ? null : 
                    selected instanceof Selection ? ((Selection)selected).getValue() : selected;
        }else{
            throw new UnsupportedOperationException("Unsupported UI component type: "+component.getClass().getName());
        }
        return (T)value;
    }
    
    @Override
    public Object setValue(Component component, Object value) {
        Objects.requireNonNull(component);
        if(component instanceof JTextComponent) {
            ((JTextComponent)component).setText(value==null?null:String.valueOf(value));
        }else if(component instanceof AbstractButton) {
            ((AbstractButton)component).setSelected(Boolean.valueOf(String.valueOf(value)));
        }else if(component instanceof DateTimePanel) {
            final Calendar cal = Calendar.getInstance();
            if(value != null) {
                Date date = (Date)value;
                cal.setTime(date);
            }else{
                cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
            dateUIUpdater.update((DateTimePanel)component, cal);
        }else if(component instanceof JComboBox) {
            ((JComboBox)component).setSelectedItem(new SelectionImpl(component.getName(), value));
        }else if(component instanceof JList) {
            ((JList)component).setSelectedValue(new SelectionImpl(component.getName(), value), true);
        }else{
            throw new UnsupportedOperationException("Unsupported UI component type: "+component.getClass().getName());
        }
        return value;
    }
    
    public Component getTextComponent(Class valueType, String name, Object value) {
        final Component component;
        if(this.getDisplaySize(valueType, name, value) <= this.contentLengthAboveWhichTextAreaIsUsed) {
            component = this.getTextField(valueType, name, value);
        }else{
            component = this.getTextArea(valueType, name, value);
        }
        return component;
    }

    public JTextField getTextField(Class valueType, String name, Object value) {
        final JTextField textField = new JTextField();
        textField.setOpaque(true);
        return textField;
    }
    
    public JTextArea getTextArea(Class valueType, String name, Object value) {
        
        final JTextArea component = new TableCellTextArea();
        final int scrollsHeight = 3 * this.height;
        component.setFont(font);
        final JScrollPane scrolls = new JScrollPane();
        if(this.width > 0) {
            scrolls.setPreferredSize(new Dimension(this.width, scrollsHeight));
        }
        
        return component;
    }
    
    public int getDisplaySize(Class valueType, String name, Object value) {
        //@todo use app.getJpaContext() to compute display sizes
        // NOTE: app may be null
        return value == null ? -1 : value.toString().length();
    }
    
    public BiFunction<Class, Class, Boolean> getSubClassTest() {
        return subClassTest;
    }

    public SelectionContext getSelectionContext() {
        return selectionContext;
    }

    public DateFromUIBuilder getDateFromUIBuilder() {
        return dateFromUIBuilder;
    }

    public DateUIUpdater getDateUIUpdater() {
        return dateUIUpdater;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public int getContentLengthAboveWhichTextAreaIsUsed() {
        return contentLengthAboveWhichTextAreaIsUsed;
    }
}
