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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.UIDisplayHandler;
import com.bc.appbase.ui.components.FormEntryComponentModel;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appcore.actions.Action;
import java.awt.Container;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appbase.ui.components.FormEntryWithThreeColumnsComponentModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 17, 2017 12:14:44 PM
 */
public class DisplaySelectedEntitiesUIs implements Action<App, Container> {
    
    private static final Logger logger = Logger.getLogger(DisplaySelectedEntitiesUIs.class.getName());

    @Override
    public Container execute(App app, Map<String, Object> params) {
        
        final Class selectedEntityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        Objects.requireNonNull(selectedEntityType);
        final String idColumnName = app.getActivePersistenceUnitContext().getMetaData().getIdColumnName(selectedEntityType);
        final List idsList = (List)params.get(idColumnName+"List");
        
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Entity type: {0}, id column: {1}, ids to process: {2}", 
                    new Object[]{selectedEntityType, idColumnName, idsList});
        }
        
        Container output = null;
        
        for(Object id : idsList) {
            
            final Object target = this.getTarget(app, selectedEntityType, id);
            
            final Container ui = displayUI(app, target, 
                    (String)params.get(ParamNames.TITLE),
                    (String)params.get(ParamNames.TEXT),
                    (String)params.get(ParamNames.ACTION_COMMAND),
                    (Boolean)params.getOrDefault(ParamNames.EDITABLE, Boolean.TRUE));
                    
            final String name = this.getUIName(app, selectedEntityType, id);

            ui.setName(name);
          
            this.saveReference(app, selectedEntityType, id, name);
            
            output = ui;
        }
        
        Objects.requireNonNull(output);
        
        return output;
    }
    
    public Object getTarget(App app, Class selectedEntityType, Object id) {
    
        final Object target = app.getActivePersistenceUnitContext().getDao().find(selectedEntityType, id);
//System.out.println(selectedEntityType.getSimpleName()+"#"+id+" = "+target);        
        return target;
    }
    
    public String getUIName(App app, Class selectedEntityType, Object id) {
    
        final String name = this.getClass().getName() + '_' + selectedEntityType.getSimpleName() + 
                '_' + id + '_' + Long.toHexString(System.currentTimeMillis());
        
        return name;
    }
    
    public void saveReference(App app, Class selectedEntityType, Object id, String name) {
        
        app.getExpirableAttributes().putFor(name, Collections.singletonMap(id, selectedEntityType));
    }
    
    public Container displayUI(App app, Object target, String uiTitle, 
            String actionButtonText, String actionCommand, Boolean editable) {
        
        Objects.requireNonNull(target);
        Objects.requireNonNull(uiTitle);
        Objects.requireNonNull(editable);
        
        final ComponentModel componentModel = editable ? 
                app.getOrException(FormEntryWithThreeColumnsComponentModel.class) : 
                app.getOrException(FormEntryComponentModel.class);
        
        final Container ui = app.getOrException(UIBuilderFromEntity.class)
                .componentModel(componentModel)
                .sourceType(target instanceof Class ? (Class)target : target.getClass())
                .sourceData(target instanceof Class ? null : target)
                .editable(editable)
                .addOptionToViewRelated(false)
                .build();
        
        final UIDisplayHandler displayHandler = app.getUIContext().getDisplayHandler();
        
        final boolean block = false;
        
        if(actionButtonText == null || actionCommand == null) {
            displayHandler.displayUI(ui, uiTitle, true, block);
        }else{
            displayHandler.displayWithTopAndBottomActionButtons(
                    ui, uiTitle, actionButtonText, actionCommand, block);
        }    
        
        return ui;
    }
}
