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

import com.bc.appbase.App;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appcore.jpa.SelectionContext;
import java.awt.Component;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 14, 2017 9:14:15 AM
 */
public class ComponentModelWithTableAsEntityListUI extends ComponentModelImpl {

    private static final Logger logger = Logger.getLogger(ComponentModelWithTableAsEntityListUI.class.getName());

    private final int batchSize = 5;
    
    private final App app;
    
    public ComponentModelWithTableAsEntityListUI(App app,
            ComponentProperties componentProperties, int contentLengthAboveWhichTextAreaIsUsed) {
        
        super(app.getOrException(SelectionContext.class), 
                app.getOrException(DateFromUIBuilder.class), app.getOrException(DateUIUpdater.class),
                componentProperties, contentLengthAboveWhichTextAreaIsUsed);
        
        this.app = Objects.requireNonNull(app);
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new ComponentModelWithTableAsEntityListUI(this.app, properties, 
                this.getContentLengthAboveWhichTextAreaIsUsed());
    }
    @Override
    public Object setValue(Component component, Object value) {
        
        if(component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        
        Objects.requireNonNull(component);
        
        if(component instanceof SearchResultsPanel || component instanceof JTable) {
            return value;
        }
        return super.setValue(component, value);
    }

    @Override
    protected Component doGetComponent(Class parentType, Class valueType, String name, Object value) {
        
        logger.finer(() -> MessageFormat.format("Type: {0}, Field: {1} {2} = {3}", 
                parentType.getName(), valueType.getSimpleName(), name, value));
               
        final Component component;
        
        if(getCollectionSize(value) > 1) {
            
            component = this.getCollectionComponentProvider()
                    .execute(parentType, valueType, name, (Collection)value);
        }else{
            
            component = super.doGetComponent(parentType, valueType, name, value);
        }
        
        return component;
    }
    

    public int getCollectionSize(Object value) {
        return value instanceof Collection ? ((Collection)value).size() : -1;
    }
    
    public CollectionComponentProvider getCollectionComponentProvider() {
        return new CollectionAsTableProvider(
                    app, this.getComponentProperties(), this.batchSize);
    }
}
