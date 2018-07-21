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

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 8, 2017 7:35:35 PM
 */
public class TextAreaPanel extends javax.swing.JPanel {

    private final int preferredWidth; 
    private final int preferredHeight;
    
    private final javax.swing.JTextArea messageTextArea;
    private final javax.swing.JScrollPane scrollPane;

    public TextAreaPanel() {
        this(5, 20, new Font("Monospaced", 0, 16), 400, 300);
    }
    
    public TextAreaPanel(int rows, int columns, Font messageFont, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.scrollPane = new javax.swing.JScrollPane();
        this.messageTextArea = new MessageTextArea(rows, columns);
        if(messageFont != null) {
            this.messageTextArea.setFont(messageFont);
        }
        initComponents();
    }

    public TextAreaPanel(JTextArea messageTextArea, int preferredWidth, int preferredHeight) {
        this.scrollPane = new javax.swing.JScrollPane();
        this.messageTextArea = messageTextArea;
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        initComponents();
    }
    
    private void initComponents() {
        
        scrollPane.setViewportView(messageTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, this.preferredWidth, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, this.preferredHeight, Short.MAX_VALUE)
        );
    }
    
    public void setText(String text) {
        messageTextArea.setText(text);
    }
    
    public JTextArea getMessageTextArea() {
        return messageTextArea;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
