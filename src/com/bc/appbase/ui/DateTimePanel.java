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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 1:08:38 PM
 */
public class DateTimePanel extends javax.swing.JPanel {

    private javax.swing.JTextField dayTextfield;
    private javax.swing.JLabel hoursLabel;
    private javax.swing.JTextField hoursTextfield;
    private javax.swing.JTextField minutesTextfield;
    private javax.swing.JComboBox<String> monthCombobox;
    private javax.swing.JComboBox<String> yearCombobox;
    
    public DateTimePanel() {
//        this(new java.awt.Font("Tahoma", 0, 14), 22, 65, 22, 5);
        this(new java.awt.Font("Tahoma", 0, 24), 40, 85, 40, 15);
    }
    
    public DateTimePanel(Font font, int textWidth, int comboWidth, int height, int horizontalGap) {
        initComponents(font, textWidth, comboWidth, height, horizontalGap);
    }

    @SuppressWarnings("unchecked")
    public void initComponents(Font font, int textWidth, int comboWidth, int height, int hGap) {

        this.removeAll();
        
        hoursLabel = new javax.swing.JLabel();
        dayTextfield = new javax.swing.JTextField();
        monthCombobox = new javax.swing.JComboBox<>();
        yearCombobox = new javax.swing.JComboBox<>();
        hoursTextfield = new javax.swing.JTextField();
        minutesTextfield = new javax.swing.JTextField();

        hoursLabel.setFont(font);
        hoursLabel.setForeground(new java.awt.Color(153, 153, 153));
        hoursLabel.setText("Hrs");

        dayTextfield.setFont(font);

        monthCombobox.setFont(font);
        monthCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        yearCombobox.setFont(font);
        yearCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        hoursTextfield.setFont(font);

        minutesTextfield.setFont(font);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dayTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, textWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monthCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, comboWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yearCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, comboWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(hGap, hGap, hGap)
                .addComponent(hoursTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, textWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minutesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, textWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hoursLabel, javax.swing.GroupLayout.PREFERRED_SIZE, textWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap()
            )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(dayTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(monthCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(yearCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(hoursTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(minutesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(hoursLabel, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    public JTextField getDayTextfield() {
        return dayTextfield;
    }

    public JLabel getHoursLabel() {
        return hoursLabel;
    }

    public JTextField getHoursTextfield() {
        return hoursTextfield;
    }

    public JTextField getMinutesTextfield() {
        return minutesTextfield;
    }

    public JComboBox<String> getMonthCombobox() {
        return monthCombobox;
    }

    public JComboBox<String> getYearCombobox() {
        return yearCombobox;
    }
}
