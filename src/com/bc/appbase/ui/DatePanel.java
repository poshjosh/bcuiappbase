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
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 1:08:38 PM
 */
public class DatePanel extends javax.swing.JPanel {

    private javax.swing.JTextField dayTextfield;
    private javax.swing.JComboBox<String> monthCombobox;
    private javax.swing.JComboBox<String> yearCombobox;
    
    public DatePanel() {
//        this(new java.awt.Font("Tahoma", 0, 14), 22, 65, 22, 5);
        this(new java.awt.Font("Tahoma", 0, 24), 40, 85, 40, 15);
    }
    
    public DatePanel(Font font, int textWidth, int comboWidth, int height, int horizontalGap) {
        
        this.initComponents(font);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                this.addHorizontalComponents(layout.createSequentialGroup(), textWidth, comboWidth, horizontalGap)
            )
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(this.addVerticalComponents(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE), height)
            )
        );
    }
    
    protected void initComponents(Font font) {
        
        dayTextfield = new javax.swing.JTextField();
        monthCombobox = new javax.swing.JComboBox<>();
        yearCombobox = new javax.swing.JComboBox<>();

        dayTextfield.setFont(font);

        monthCombobox.setFont(font);
//        monthCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        yearCombobox.setFont(font);
//        yearCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    }
    
    protected SequentialGroup addHorizontalComponents(SequentialGroup sequentialGroup, int textWidth, int comboWidth, int horizontalGap) {
        sequentialGroup
            .addComponent(dayTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, textWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(monthCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, comboWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(yearCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, comboWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap();        
        return sequentialGroup;
    }
    
    protected ParallelGroup addVerticalComponents(ParallelGroup parallelGroup, int height) {
        parallelGroup
            .addComponent(dayTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(monthCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(yearCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE);
        return parallelGroup;
    }

    public JTextField getDayTextfield() {
        return dayTextfield;
    }

    public JComboBox<String> getMonthCombobox() {
        return monthCombobox;
    }

    public JComboBox<String> getYearCombobox() {
        return yearCombobox;
    }
}
