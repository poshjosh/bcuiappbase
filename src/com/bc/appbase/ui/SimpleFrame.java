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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 12:20:09 AM
 */
public class SimpleFrame extends JFrame {

    public SimpleFrame(UIContext uiContext, Container ui, String title, Font font, AbstractButton button) throws HeadlessException {
        this(uiContext, ui, title, font, button, null, null);
    }

    public SimpleFrame(UIContext uiContext, Container ui, String title, Font font, 
            AbstractButton addButtonTop, AbstractButton addButtonBottom, String actionCommand) throws HeadlessException {
        
        super(title);
        
        if(addButtonTop != null) {
            addButtonTop.setFont(font);
            if(actionCommand != null) {
                addButtonTop.setActionCommand(actionCommand);
                uiContext.addActionListeners(ui, addButtonTop);
            }
        }
        
        if(addButtonBottom != null) {
            addButtonBottom.setFont(font);
            if(actionCommand != null) {
                addButtonBottom.setActionCommand(actionCommand);
                uiContext.addActionListeners(ui, addButtonBottom);
            }
        }
        
        final JPanel combined = new JPanel();
        final VerticalLayout verticalLayout = new VerticalLayout();
        final List<Component> components = new ArrayList();
        Arrays.asList(addButtonTop, ui, addButtonBottom).stream().forEach((c) -> {
            if(c != null) {
                components.add(c);
            }
        });
        verticalLayout.addComponents(combined, components);
        final JScrollPane scrolls = new JScrollPane(combined);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().add(scrolls);
        
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(ui.getPreferredSize().width + 40, dim.height - 50));
        this.setLocation(0, 0);
    }
}
