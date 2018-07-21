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

import com.bc.selection.Selection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 7, 2017 4:29:17 PM
 */
public class ComboBoxItemListener implements ItemListener {

    public ComboBoxItemListener() { }
    
    @Override
    public void itemStateChanged(ItemEvent e) {

        final int stateChange = e.getStateChange();
        
        final Object selection;
        
        switch(stateChange) {
            case ItemEvent.DESELECTED: 
                selection = null;
                this.itemDeselected();
                break;
                
            case ItemEvent.SELECTED: 
                final Object first = e.getItemSelectable().getSelectedObjects()[0];
                if(first instanceof Selection) {
                    selection = ((Selection)first).getValue();
                }else{
                    selection = first;
                }
                this.itemSelected(selection);
                break;
                
            default:
                throw new IllegalArgumentException("ItemEvent.stateChange = "+stateChange);
        }
        
        this.process(stateChange, selection);
    }
    
    public void itemDeselected() {}
    
    public void itemSelected(Object selection) {}
    
    /**
     * @param stateChange e.g {@link java.awt.event.ItemEvent#SELECTED}
     * @param selection May be null
     */
    public void process(int stateChange, Object selection) { }
}
