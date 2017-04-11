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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 12:20:09 AM
 */
public class SimpleFrame extends JFrame {

    public SimpleFrame(String title, App app, Container ui, Font font, 
            String buttonText, String buttonActionCommand) throws HeadlessException {
        super(title);
        final JButton addButtonTop = new JButton(buttonText);
        addButtonTop.setActionCommand(buttonActionCommand);
        
        final JButton addButtonBottom = new JButton(buttonText);
        addButtonBottom.setActionCommand(buttonActionCommand);
        
        app.getUIContext().addActionListeners(ui, addButtonTop, addButtonBottom);
        
        addButtonTop.setFont(font);
        addButtonBottom.setFont(font);
        
        final JPanel combined = new JPanel();
        final VerticalLayout verticalLayout = new VerticalLayout();
        final List<Component> components = Arrays.asList(addButtonTop, ui, addButtonBottom);
        verticalLayout.addComponents(combined, components);
        final JScrollPane scrolls = new JScrollPane(combined);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().add(scrolls);
        
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(ui.getPreferredSize().width + 40, dim.height - 50));
        this.setLocation(0, 0);
    }
}
