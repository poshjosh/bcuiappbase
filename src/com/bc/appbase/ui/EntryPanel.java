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
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 26, 2017 10:13:16 PM
 */
public class EntryPanel<C extends Component> extends javax.swing.JPanel {

    private final int labelWidth;
    private final int labelHeight;
    private final int entryComponentWidth;
    private final int entryComponentHeight;
    private final int fontSize;
    private final String fontName;
    
    private final JLabel label;
    private final C entryComponent;
    
    private JScrollPane entryComponentScrollPane;
    
    public EntryPanel(int labelWidth, 
            Class<C> componentType, int componentWidth, int componentHeight, String fontName) {
        this(JLabel.class, labelWidth, componentHeight, componentType, componentWidth, componentHeight, fontName);
    }
    
    public EntryPanel(Class<JLabel> labelType, int labelWidth, int labelHeight, 
            Class<C> componentType, int componentWidth, int componentHeight, String fontName) {
        this(getComponent(labelType), labelWidth, labelHeight, 
                getComponent(componentType), componentWidth, componentHeight, fontName);
    }
    
    public EntryPanel(int labelWidth, 
            C component, int componentWidth, int componentHeight, String fontName) {
        this(new JLabel(), labelWidth, componentHeight, component, componentWidth, componentHeight, fontName);
    }
    
    public EntryPanel(JLabel label, int labelWidth, int labelHeight, 
            C component, int componentWidth, int componentHeight, String fontName) {
        this.labelWidth = labelWidth;
        this.labelHeight = labelHeight;
        this.entryComponentWidth = componentWidth;
        this.entryComponentHeight = componentHeight;
        this.fontSize = deriveFontSize(labelHeight);
        this.fontName = fontName;
        this.label = Objects.requireNonNull(label);
        this.entryComponent = Objects.requireNonNull(component);
    }
    
    public static int deriveFontSize(int height) {
        return (height / 2) - 2;
    }
    
    private static <T> T getComponent(Class<T> type) {
        try{
            return type.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void format(JLabel label) { }
    
    public void format(C component) { }

    @SuppressWarnings("unchecked")
    public void initComponents() {

        final Font font = new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize);
        
        label.setFont(font);
//        label.setPreferredSize(new Dimension(labelWidth, labelHeight));
        
        this.format(label);

        entryComponent.setFont(font);
//        entryComponent.setPreferredSize(new Dimension(entryComponentWidth, entryComponentHeight));

        final Component toAdd;
        if(this.entryComponentScrollPane != null) {
            this.entryComponentScrollPane.setViewportView(this.entryComponent);
            toAdd = this.entryComponentScrollPane;
        }else{
            toAdd = this.entryComponent;
        }
        
        this.format(entryComponent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, labelWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toAdd, javax.swing.GroupLayout.DEFAULT_SIZE, entryComponentWidth, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, labelHeight, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(toAdd, javax.swing.GroupLayout.PREFERRED_SIZE, entryComponentHeight, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }

    public JLabel getLabel() {
        return label;
    }

    public C getEntryComponent() {
        return entryComponent;
    }

    public JScrollPane getEntryComponentScrollPane() {
        return entryComponentScrollPane;
    }

    public void setEntryComponentScrollPane(JScrollPane entryComponentScrollPane) {
        this.entryComponentScrollPane = entryComponentScrollPane;
    }
}
