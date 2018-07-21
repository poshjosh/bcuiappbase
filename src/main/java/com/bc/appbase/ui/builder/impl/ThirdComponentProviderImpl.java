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

package com.bc.appbase.ui.builder.impl;

import com.bc.ui.builder.model.ComponentModel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.builder.PromptUserCreateNew;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appcore.ObjectFactory;
import com.bc.selection.SelectionContext;
import com.bc.selection.Selection;
import com.bc.jpa.exceptions.EntityInstantiationException;
import java.awt.Component;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 31, 2017 7:16:27 PM
 */
public class ThirdComponentProviderImpl implements ThirdComponentProvider {

    private static final Logger LOG = Logger.getLogger(ThirdComponentProviderImpl.class.getName());

    private final UIContext uiContext;
    private final PromptUserCreateNew prompt;
    private final SelectionContext selectionContext;
    private final ComponentModel componentModel;

    public ThirdComponentProviderImpl(UIContext uiContext, ObjectFactory factory) {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.prompt = factory.getOrException(PromptUserCreateNew.class);
        this.selectionContext = factory.getOrException(SelectionContext.class);
        this.componentModel = factory.getOrException(ComponentModel.class);
    }
    
    @Override
    public Component get(Class parentType, Class valueType, String name, Object value, 
            JLabel label, Component component, Component outputIfNone) {
        
        final boolean selectionType = this.selectionContext.isSelectionType(valueType);
        
        final Component output;
        
        if(!selectionType || !(component instanceof JComboBox)) {
            
            output = null;
            
        }else{
            
            final JButton button = new JButton("Add New "+valueType.getSimpleName());
            
            button.addActionListener((actionEvent) -> {
                
//System.out.println("ValueType: "+valueType.getSimpleName()+", name: "+name);                
                final JComboBox combo = ((JComboBox)component);
                
                final int itemCount = combo.getItemCount();

                Object created;
                try{
                    created = this.prompt.promptCreateNew(valueType, null);
                }catch(EntityInstantiationException e) {
                    final String errorMessage = "Error adding new " + valueType.getSimpleName();
                    LOG.log(Level.WARNING, errorMessage, e);
                    uiContext.showErrorMessage(e, errorMessage);
                    created = null;
                }
                
                LOG.log(Level.FINE, "Created from user prompt: {0}", new Object[]{created});
                
                if(created == null) {
                    return;
                }
                
                final Selection [] updatedValues = componentModel.getSelectionValues(
                        parentType, valueType, name, value).toArray(new Selection[0]);
                
                assert (itemCount < updatedValues.length) : "Entity created from user prompt may not have been added to JComboBox. Entity: " + created;
                
                combo.setModel(new DefaultComboBoxModel(updatedValues));
                
                componentModel.setValue(component, created);
            });
           
            output = button;
        }
        return output;
    }
}
