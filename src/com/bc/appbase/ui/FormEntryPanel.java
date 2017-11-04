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
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 26, 2017 10:13:16 PM
 */
public class FormEntryPanel extends javax.swing.JPanel {

    private final int labelWidth;
    private final int labelHeight;
    private final int entryComponentWidth;
    private final int entryComponentHeight;
    private final int fontSize;
    private final String fontName;
    
    private final JLabel label;
    private final Component entryComponent;
    private final Component thirdComponent;
    
    private final JScrollPane entryComponentScrollPane;
    
    public FormEntryPanel(int labelWidth, 
            Class componentType, int componentWidth, int componentHeight, String fontName, Component thirdComponent) {
        this(JLabel.class, labelWidth, componentHeight, componentType, componentWidth, componentHeight, fontName, thirdComponent);
    }
    
    public FormEntryPanel(Class<JLabel> labelType, int labelWidth, int labelHeight, 
            Class componentType, int componentWidth, int componentHeight, String fontName, Component thirdComponent) {
        this(getComponent(labelType), labelWidth, labelHeight, 
                (Component)getComponent(componentType), componentWidth, componentHeight, fontName, thirdComponent);
    }
    
    public FormEntryPanel(int labelWidth, 
            Component component, int componentWidth, int componentHeight, String fontName, Component thirdComponent) {
        this(new JLabel(), labelWidth, componentHeight, component, componentWidth, componentHeight, fontName, thirdComponent);
    }
    
    public FormEntryPanel(JLabel label, int labelWidth, int labelHeight, 
            Component component, int componentWidth, int componentHeight, String fontName, Component thirdComponent) {
        this.labelWidth = labelWidth;
        this.labelHeight = labelHeight;
        this.entryComponentWidth = componentWidth;
        this.entryComponentHeight = componentHeight;
        this.fontSize = deriveFontSize(labelHeight);
        this.fontName = fontName;
        this.label = Objects.requireNonNull(label);
        Objects.requireNonNull(component);
        if(component instanceof JScrollPane) {
            this.entryComponentScrollPane = (JScrollPane)component;
            this.entryComponent = this.entryComponentScrollPane.getViewport().getView();
        }else{
            this.entryComponentScrollPane = null;
            this.entryComponent = component;
        }
        this.thirdComponent = thirdComponent;
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
    
    public void format(Component component) { }

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
            toAdd = this.entryComponentScrollPane;
            this.entryComponentScrollPane.setPreferredSize(this.entryComponent.getPreferredSize());
        }else{
            toAdd = this.entryComponent;
        }
        
        final Component lastComponent = thirdComponent == null ? new JSeparator(SwingConstants.HORIZONTAL) : this.thirdComponent;
        final int lastComponentWidth = this.entryComponentWidth;
        lastComponent.setFont(font);
//System.out.println("Widths. label: "+this.labelWidth+", entry component: "+this.entryComponentWidth+", third component: "+lastComponentWidth+". "+this.getClass());
        this.format(entryComponent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, labelWidth, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toAdd, javax.swing.GroupLayout.DEFAULT_SIZE, entryComponentWidth, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lastComponent, javax.swing.GroupLayout.PREFERRED_SIZE, lastComponentWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
            )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, labelHeight, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
            )
            .addComponent(toAdd, javax.swing.GroupLayout.PREFERRED_SIZE, entryComponentHeight, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lastComponent, javax.swing.GroupLayout.PREFERRED_SIZE, entryComponentHeight, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }

    public JLabel getLabel() {
        return label;
    }

    public Component getEntryComponent() {
        return entryComponent;
    }

    public JScrollPane getEntryComponentScrollPane() {
        return entryComponentScrollPane;
    }
}
