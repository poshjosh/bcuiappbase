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

import com.bc.appbase.ui.EntryPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.bc.appbase.App;
import com.bc.appbase.ui.ComponentModel;
import java.awt.Component;
import com.bc.appbase.ui.builder.EntryUIProvider;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 26, 2017 10:57:32 PM
 */
public class EntryUIProviderImpl implements EntryUIProvider<JPanel> {

    private static final Logger logger = Logger.getLogger(EntryUIProviderImpl.class.getName());
    
    private final int labelWidth;
    
    private final ComponentModel cm;

    public EntryUIProviderImpl(App app) {
        this(app.get(ComponentModel.class), -1);
    }
    
    public EntryUIProviderImpl(ComponentModel cm, int labelWidth) {
        this.cm = cm;
        this.labelWidth = labelWidth == -1 ? cm.getWidth() : labelWidth;
    }

    @Override
    public JPanel getEntryUI(Class valueType, String name, Object value) {
        final Font font = cm.getFont();
        final int width = cm.getWidth();
        final int height = cm.getHeight();
        final Component c = cm.getComponent(valueType, name, value);
        final JScrollPane scrolls;
        final int inputHeight;
        if(c instanceof JTextArea) {
            inputHeight = 3 * height;
            scrolls = new JScrollPane();
            scrolls.setPreferredSize(new Dimension(width, inputHeight));
        }else{
            inputHeight = height;
            scrolls = null;
        }
        final JLabel label = this.getLabel(valueType, name, value);
        final EntryPanel ui = new EntryPanel(
                label, this.labelWidth, height, 
                c, width, inputHeight, font.getName()
        );
        if(scrolls != null) {
            ui.setEntryComponentScrollPane(scrolls);
        }
        
        ui.initComponents();
//        ui.getEntryComponent().setName(name);
//        cm.setValue(ui.getEntryComponent(), value);
        
        return ui;
    }
    
    public JLabel getLabel(Class valueType, String name, Object value) {
        return new JLabel(this.getLabelText(valueType, name, value));
    }
    
    public String getLabelText(Class valueType, String name, Object value) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
