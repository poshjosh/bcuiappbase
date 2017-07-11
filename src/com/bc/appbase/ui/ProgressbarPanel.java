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

import javax.swing.JProgressBar;

/**
 *
 * @author Josh
 */
public class ProgressbarPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProgressbarPanel
     */
    public ProgressbarPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();

        setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        setMaximumSize(new java.awt.Dimension(512, 32));
        setMinimumSize(new java.awt.Dimension(256, 32));
        setPreferredSize(new java.awt.Dimension(512, 32));

        progressBar.setBackground(new java.awt.Color(51, 153, 255));
        progressBar.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        progressBar.setForeground(new java.awt.Color(255, 255, 255));
        progressBar.setBorderPainted(false);
        progressBar.setDoubleBuffered(true);
        progressBar.setMaximumSize(new java.awt.Dimension(512, 32));
        progressBar.setMinimumSize(new java.awt.Dimension(256, 32));
        progressBar.setPreferredSize(new java.awt.Dimension(512, 32));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}