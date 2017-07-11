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

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 1, 2017 10:25:22 PM
 */
public class JCheckBoxMenuItemListComboBox<T> extends JComponent {
    
    private final Map<Long, T> selectedValues = new TreeMap();

    private class MenuItemActionListener implements ActionListener {

        private final JPopupMenu menu;
        private final JButton button;

        private MenuItemActionListener(JPopupMenu menu, JButton button) {
            this.menu = menu;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            menu.show(button, 0, button.getHeight());
            
            final JMenuItem menuItem = (JMenuItem)e.getSource();
            
            final T value = getValue(menuItem);
                
            if(menuItem.isSelected()) {
            
                if(!selectedValues.containsValue(value)) {

                    selectedValues.put(e.getWhen(), value);
                }
            }else{
                
                selectedValues.values().remove(value);
            }
        }
    }
    
    private List<T> values;
    
    private List<String> displayValues;
    
    private final JButton button;

    private final JPopupMenu menu;
    
    public JCheckBoxMenuItemListComboBox() {
        this(null);
    }
    
    public JCheckBoxMenuItemListComboBox(List<T> values) {
        this.button = new JButton();
        this.menu = new JPopupMenu();
        this.init();
        if(values != null) {
            this.setValues(values);
        }
    }
    
    private void init() {
        this.setLayout(new GridLayout(1, 1));
        this.add(button);
        button.addActionListener((ActionEvent actionEvent) -> {
            if (!menu.isVisible()) {
                Point p = button.getLocationOnScreen();
                menu.setInvoker(button);
                menu.setLocation((int) p.getX(),
                        (int) p.getY() + button.getHeight());
                menu.setVisible(true);
            } else {
                menu.setVisible(false);
            }
        });
    }
    
    public void setValues(List<T> values) {

        this.values = Collections.unmodifiableList(values);
        
        this.displayValues = new ArrayList(values.size());
        
        if(values.isEmpty()) {
            return;
        }
        
        for(T t : values) {
            this.displayValues.add(this.getDisplayValue(t));
        }
        
        this.button.setText(this.displayValues.get(0));

        for(int i=1; i<values.size(); i++) {
            
            final JMenuItem menuItem = this.getMenuItem(displayValues.get(i));
            
            menuItem.addActionListener(new MenuItemActionListener(this.menu, this.button));
            
            this.menu.add(menuItem);
        }
    }
    
    public JMenuItem getMenuItem(String displayValue) {
        return new JCheckBoxMenuItem(displayValue);
    }
    
    public String getDisplayValue(T value) {
        return value == null ? null : value.toString();
    }
    
    public T getValue(JMenuItem menuItem) {
        return this.getValue(menuItem.getText());
    }
    
    public T getValue(String displayValue) {
        final int index = this.displayValues.indexOf(displayValue);
        return this.values.get(index);
    }
    
    public void clearSelection() {
        
        final Predicate<JMenuItem> test = (menuItem) -> menuItem.isSelected();
        
        final Consumer<JMenuItem> consumer = (menuItem) -> {
            menuItem.doClick();
        };
        
        this.processMenuItems(test, consumer);
    }
    
    public void setSelectedValue(T value) {
        this.setSelectedValues(Collections.singletonList(value));
    }
    
    public void setSelectedValues(List<T> values) {
        
        final Predicate<JMenuItem> test = (menuItem) -> {
            final T value = this.getValue(menuItem);
            if(!menuItem.isSelected()) {
                return values.contains(value);
            }else{
                return !values.contains(value);
            }
        };
        
        final Consumer<JMenuItem> consumer = (menuItem) -> {
            menuItem.doClick();
        };
        
        this.processMenuItems(test, consumer);
    }
    
    public List<T> getSelectedValuesList() {
        
        final List selected = new ArrayList(this.selectedValues.values());
//System.out.println("Selected: "+selected+". @"+this.getClass());        
        return Collections.unmodifiableList(selected);
    }

    public void processMenuItems(Predicate<JMenuItem> test, Consumer<JMenuItem> consumer) {
        
        final int count = this.menu.getComponentCount();
        
        for(int i=0; i<count; i++) {
            
            final Component c = this.menu.getComponent(i);
            
            if(c instanceof JMenuItem) {
                
                final JMenuItem menuItem = (JMenuItem)c;
                
                if(test.test(menuItem)) {
                    
                    consumer.accept(menuItem);
                }
            }
        }
    }
}
