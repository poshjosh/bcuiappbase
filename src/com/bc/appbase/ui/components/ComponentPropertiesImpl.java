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

package com.bc.appbase.ui.components;

import com.bc.appbase.ui.components.ComponentModel.ComponentProperties;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 15, 2017 3:08:01 PM
 */
public class ComponentPropertiesImpl implements ComponentModel.ComponentProperties {

    private final ComponentProperties delegate;
    
    public ComponentPropertiesImpl() { 
        this(null);
    }

    public ComponentPropertiesImpl(ComponentProperties props) {
        this.delegate = props;
    }

    @Override
    public Font getFont(Component component) {
        return this.delegate != null ? this.delegate.getFont(component) : 
                new Font(Font.MONOSPACED, Font.PLAIN, this.getFontSize(component));
    }
    
    public int getFontSize(Component component) {
        final int fontSize = (this.getHeight(component) / 2) - 2;
        return fontSize;
    }
    
    @Override
    public int getWidth(Component component) { 
        return this.delegate != null ? this.delegate.getWidth(component) : -1; 
    }
    
    @Override
    public int getHeight(Component component) { 
        if(this.delegate != null) {
            return this.delegate.getHeight(component);
        }else{
            if(component instanceof JTextArea || component instanceof JTable) {
                return 120;
            }else{
                return 40; 
            }
        }
    }
    
    @Override
    public boolean isEnabled(Component component) { 
        return this.delegate != null ? this.delegate.isEnabled(component) : true; 
    }
    
    @Override
    public boolean isEditable(Component component) { 
        return this.delegate != null ? this.delegate.isEditable(component) : true; 
    }
}
