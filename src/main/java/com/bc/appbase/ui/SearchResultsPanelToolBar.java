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

import com.bc.appbase.ui.actions.ActionCommands;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Josh
 */
public class SearchResultsPanelToolBar extends javax.swing.JPanel {

    private transient static final Logger LOG = 
            Logger.getLogger(SearchResultsPanelToolBar.class.getName());

    /**
     * Creates new form SearchPanelToolBar
     */
    public SearchResultsPanelToolBar() {
        initComponents();
    }

    public void init(UIContext uiContext, JTable searchResultsTable) {
        
        Objects.requireNonNull(uiContext);
        Objects.requireNonNull(searchResultsTable);
        
        this.getNextPageButton().setActionCommand(ActionCommands.NEXT_RESULT);
        this.getPreviousPageButton().setActionCommand(ActionCommands.PREVIOUS_RESULT);
        this.getLastPageButton().setActionCommand(ActionCommands.LAST_RESULT);
        this.getFirstPageButton().setActionCommand(ActionCommands.FIRST_RESULT);
        
        uiContext.addActionListeners(searchResultsTable, 
                this.getNextPageButton(), this.getPreviousPageButton(),
                this.getLastPageButton(), this.getFirstPageButton());

        okButton.addActionListener((ActionEvent ae) -> {
            final TableCellEditor cellEditor = searchResultsTable.getCellEditor();
            if(cellEditor != null) {
                try{
                    cellEditor.stopCellEditing();
                }catch(RuntimeException ex) {
                    LOG.log(Level.WARNING, "Exception while stopping cell editing", ex);
                }
            }
        });
        
        this.submitButton.addActionListener(new UpdateTableActionListener(
                uiContext, searchResultsTable
        ){
            @Override
            public void actionPerformed(ActionEvent e) {
                okButton.doClick();
                super.actionPerformed(e);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        submitButton = new javax.swing.JButton();
        previousPageButton = new javax.swing.JButton();
        nextPageButton = new javax.swing.JButton();
        paginationLabel = new javax.swing.JLabel();
        firstPageButton = new javax.swing.JButton();
        lastPageButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(700, 32));

        submitButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        submitButton.setText("Submit");

        previousPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        previousPageButton.setText("<");

        nextPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        nextPageButton.setText(">");

        paginationLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        paginationLabel.setPreferredSize(new java.awt.Dimension(186, 31));

        firstPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        firstPageButton.setText("<<");

        lastPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lastPageButton.setText(">>");

        okButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        okButton.setText("OK");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(submitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstPageButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previousPageButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paginationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextPageButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastPageButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextPageButton)
                    .addComponent(lastPageButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previousPageButton)
                    .addComponent(firstPageButton)
                    .addComponent(okButton)
                    .addComponent(submitButton)))
            .addComponent(paginationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton firstPageButton;
    private javax.swing.JButton lastPageButton;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel paginationLabel;
    private javax.swing.JButton previousPageButton;
    private javax.swing.JButton submitButton;
    // End of variables declaration//GEN-END:variables

    public JButton getFirstPageButton() {
        return firstPageButton;
    }

    public JButton getLastPageButton() {
        return lastPageButton;
    }

    public JButton getNextPageButton() {
        return nextPageButton;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JLabel getPaginationLabel() {
        return paginationLabel;
    }

    public JButton getPreviousPageButton() {
        return previousPageButton;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }
}