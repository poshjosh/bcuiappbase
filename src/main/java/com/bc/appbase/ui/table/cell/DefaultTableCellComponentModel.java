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

package com.bc.appbase.ui.table.cell;

import com.bc.appcore.ObjectFactory;
import com.bc.selection.SelectionContext;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import com.bc.ui.date.DateFromUIBuilder;
import com.bc.ui.date.DateUIUpdater;
import com.bc.selection.SelectionValues;
import java.awt.Component;
import java.util.Date;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 7:40:50 PM
 */
public class DefaultTableCellComponentModel extends ComponentModelImpl {

    public DefaultTableCellComponentModel(ObjectFactory objectFactory) {
        this(objectFactory.getOrException(SelectionContext.class), 
                objectFactory.getOrException(DateFromUIBuilder.class), objectFactory.getOrException(DateUIUpdater.class));
    }

    public DefaultTableCellComponentModel(
            SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, 
            DateUIUpdater dateUIUpdater) {
        super(selectionValues, dateFromUIBuilder, dateUIUpdater);
    }

    public DefaultTableCellComponentModel(
            SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater, 
            ComponentModelImpl.ComponentProperties componentProperties) {
        super(selectionValues, dateFromUIBuilder, dateUIUpdater, componentProperties, -1);
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentModelImpl.ComponentProperties properties) {
        return new DefaultTableCellComponentModel(this.getSelectionValues(), 
                this.getDateFromUIBuilder(), this.getDateUIUpdater(), properties);
    }
    
    @Override
    public Component getComponent(Class parentType, Class valueType, String name, Object value) {
        final Component component;
        if(Date.class.isAssignableFrom(valueType)) {
            component = this.getTextComponent(valueType, name, value);
        }else {
            component = super.getComponent(parentType, valueType, name, value);
        }
        return component;
    }    

    @Override
    public Component getTextComponent(Class valueType, String name, Object value) {
        return this.getTextArea(valueType, name, value);
    }
}
