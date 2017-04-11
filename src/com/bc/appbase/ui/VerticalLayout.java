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
import java.util.Iterator;
import java.util.List;
import javax.swing.GroupLayout;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2017 10:59:33 PM
 */
public class VerticalLayout {

    @SuppressWarnings("unchecked")
    public void addComponents(Container container, List<Component> components) {
        this.addComponents(container, components, true, false);
    }
    
    @SuppressWarnings("unchecked")
    public void addComponents(Container container, List<Component> components, 
            boolean horizontalResizable, boolean verticalResizable) {

        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);
        
        final GroupLayout.ParallelGroup parallelGroupForHorizontal = 
                layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        for(Component component : components) {
            if(horizontalResizable) {
                parallelGroupForHorizontal.addComponent(component, GroupLayout.Alignment.LEADING, 
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,  Short.MAX_VALUE);
            }else{
                parallelGroupForHorizontal.addComponent(component, GroupLayout.Alignment.LEADING, 
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            }
        }
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(parallelGroupForHorizontal)
            )
        );
        
        final GroupLayout.SequentialGroup sequentialGroupForVertical = layout.createSequentialGroup();
        final Iterator<Component> iter = components.iterator();
        while(iter.hasNext()) {
            if(verticalResizable) {
                sequentialGroupForVertical.addComponent(iter.next(), 
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            }else{
                sequentialGroupForVertical.addComponent(iter.next(), 
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            }
            if(iter.hasNext()) {
                sequentialGroupForVertical.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED);
            }
        }
               
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(sequentialGroupForVertical)
        );
    }
}
