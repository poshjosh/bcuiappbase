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

package com.bc.appbase.properties;

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appbase.ui.Components;
import com.bc.appbase.ui.DateFromUIBuilderImpl;
import com.bc.appbase.ui.DateUIUpdaterImpl;
import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.VerticalLayout;
import com.bc.appcore.jpa.SelectionContext;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.bc.appcore.properties.OptionsProvider;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 1, 2017 11:17:17 PM
 */
public class GetOptionsViaUserPrompt implements OptionsProvider {

    private final Function<String, String> nameToLabel;
    
    private final String dialogTitle;
    
    private final SequentialLayout layout;
    
    private final ComponentModel componentModel;
    
    private final Component parent;
    
    private final int width;
    
    private Container optionsUI;
    
    public GetOptionsViaUserPrompt(String dialogTitle, Function<String, String> nameToLabel) {
        this(
                null,
                new ComponentModelImpl(
                        SelectionContext.NO_OP, new DateFromUIBuilderImpl(), new DateUIUpdaterImpl(),
                        ComponentModel.ComponentProperties.DEFAULT, 1000
                ),
                new VerticalLayout(), 
                dialogTitle,
                nameToLabel,
                500
        );
    }

    public GetOptionsViaUserPrompt(
            Component parent, ComponentModel componentModel, SequentialLayout layout, 
            String dialogTitle, Function<String, String> nameToLabel, int width) {
        this.parent = parent;
        this.componentModel = Objects.requireNonNull(componentModel);
        this.layout = Objects.requireNonNull(layout);
        this.dialogTitle = Objects.requireNonNull(dialogTitle);
        this.nameToLabel = Objects.requireNonNull(nameToLabel);
        this.width = width;
    }
    
    @Override
    public Map<String, Object> get(Map<String, Object> params)  {
        
        if(optionsUI == null) {
            
            final Set<Entry<String, Object>> entrySet = params.entrySet();
            for(Entry<String, Object> entry : entrySet) {
                final String key = entry.getKey();
                final Object val = entry.getValue();
//System.out.println("====================================" + key + " = " + val + " "+this.getClass());                
                final Container uiEntry = this.createUIEntry(key, val);
                layout.addComponent(uiEntry);
            }

            this.optionsUI = new JPanel();
            layout.addComponents(optionsUI, true, true);
        }
        
        return this.execute(parent, optionsUI, params);
    }
    
    public Map<String, Object> execute(Component parent, Container ui, Map<String, Object> params)  {

        final Dimension dim = new Dimension(width, 50 * params.size());
        
        final JScrollPane scrolls = new JScrollPane(ui);
        ui.setPreferredSize(dim);
        scrolls.setPreferredSize(new Dimension(dim.width + dim.width/10, dim.height + dim.height/10));
        
        final int option = JOptionPane.showConfirmDialog(parent, 
                scrolls, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        final Map<String, Object> output = new LinkedHashMap<>(params.size(), 1.0f);
        
        if(option != JOptionPane.OK_OPTION) {
            output.putAll(params);
        }else{
        
            final Components cx = new Components();

            for(String name : params.keySet()) {

                final Component found = cx.findFirstChild(ui, (c) -> name.equals(c.getName()), false, null);

                if(found != null) {

                    final Object value = componentModel.getValue(found, null);

                    output.put(name, value);
                }
            }
        }
        
        return output;
    }
    
    public Container createUIEntry(String name, Object value) {
        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(width, -1));
        panel.setLayout(new GridLayout(1, 2));
        panel.add(this.createLabel(name));
        panel.add(this.createInput(name, value));
        return panel;
    }
    
    public JLabel createLabel(String name) {
        final JLabel label = new JLabel(nameToLabel.apply(name));
        this.setPreferredSize(label);
        return label;
    }
    
    public Component createInput(String name, Object value) {
        final Component comp = componentModel.getComponent(null, this.getType(name, value), name, value);
        comp.setName(name);
        this.setPreferredSize(comp);
        return comp;
    }
    
    private void setPreferredSize(Component comp) {
        comp.setPreferredSize(new Dimension((width / 2) - 8, 50 - 4));
    }
    
    public Function<String, String> getNameToLabel() {
        return nameToLabel;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public SequentialLayout getLayout() {
        return layout;
    }

    public ComponentModel getComponentModel() {
        return componentModel;
    }

    public int getWidth() {
        return width;
    }

    public Container getOptionsUI() {
        return optionsUI;
    }
}
