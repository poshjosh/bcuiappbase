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
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.util.Selection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author Josh
 */
public class SelectAxisTypesPanel extends javax.swing.JPanel {

    public SelectAxisTypesPanel() {
        this(null);
    }
    
    public SelectAxisTypesPanel(App app) {
        initComponents();
        if(app != null) {
            this.init(app);
        }
    }
    
    public void init(App app) {
        
        final Set<Class> entityTypes = app.getActivePersistenceUnitContext().getMetaData().getEntityClasses();
        
        final List<Selection> options = new ArrayList();
        final SelectionContext selectionContext = app.getOrException(SelectionContext.class);
        
        for(Class entityType : entityTypes) {
            if(selectionContext.isSelectionType(entityType)) {
                options.add(this.toSelection(entityType));
            }
        }
        
        this.updateComboBox(app, this.xAxisComboBox, options, 'x');

        this.updateComboBox(app, this.yAxisComboBox, options, 'y');
    }
    
    public Selection toSelection(Class type) {
        return Selection.from(type.getSimpleName(), type);
    }
    
    public void updateComboBox(App app, JComboBox combo, List<Selection> options, char axis) {
        this.addOptions(combo, "Select "+Character.toUpperCase(axis)+"-axis Type", options);
        final String axisKey = this.getAxisKey(axis);
        final Class cachedType = (Class)app.getAttributes().get(axisKey);
        if(cachedType != null) {
            combo.setSelectedItem(this.toSelection(cachedType));
        }
        combo.addItemListener(new ComboBoxItemListener(){
            @Override
            public void process(int stateChange, Object selection) {
                app.getAttributes().put(axisKey, selection);
            }
        });
    }
    
    public String getAxisKey(char axis) {
        axis = Character.toLowerCase(axis);
        if(axis != 'x' && axis != 'y') {
            throw new IllegalArgumentException();
        }
        return "SummaryReport."+axis+"Axis.type";
    }
    
    public void addOptions(JComboBox comboBox, String defaultOptionLabel, List<Selection> otherOptions) {
        final List<Selection> allOptions = new ArrayList();
        allOptions.add(Selection.from(defaultOptionLabel, null));
        allOptions.addAll(otherOptions);
        comboBox.setModel(new DefaultComboBoxModel(allOptions.toArray(new Selection[0])));
    }
    
    public Class getSelectedXAxisType() {
        return ((Selection<Class>)this.getxAxisComboBox().getSelectedItem()).getValue();
    }

    public Class getSelectedYAxisType() {
        return ((Selection<Class>)this.getyAxisComboBox().getSelectedItem()).getValue();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xAxisLabel = new javax.swing.JLabel();
        yAxisLabel = new javax.swing.JLabel();
        xAxisComboBox = new javax.swing.JComboBox<>();
        yAxisComboBox = new javax.swing.JComboBox<>();

        xAxisLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        xAxisLabel.setText(" X Axis   > ");

        yAxisLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        yAxisLabel.setText(" Y Axis   v ");

        xAxisComboBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        xAxisComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        yAxisComboBox.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        yAxisComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(yAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yAxisLabel)
                    .addComponent(xAxisLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(xAxisComboBox, 0, 197, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xAxisLabel)
                    .addComponent(xAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yAxisLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> xAxisComboBox;
    private javax.swing.JLabel xAxisLabel;
    private javax.swing.JComboBox<String> yAxisComboBox;
    private javax.swing.JLabel yAxisLabel;
    // End of variables declaration//GEN-END:variables

    public JComboBox<String> getxAxisComboBox() {
        return xAxisComboBox;
    }

    public JLabel getxAxisLabel() {
        return xAxisLabel;
    }

    public JComboBox<String> getyAxisComboBox() {
        return yAxisComboBox;
    }

    public JLabel getyAxisLabel() {
        return yAxisLabel;
    }
}
