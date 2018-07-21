/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance sourceData the License.
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
import com.bc.ui.builder.UIBuilderFromMap;
import com.bc.ui.builder.model.ComponentModel.ComponentProperties;
import com.bc.appbase.ui.components.ComponentModelWithTableAsEntityListUI;
import com.bc.appbase.ui.components.FormEntryComponentModel;
import com.bc.appbase.ui.builder.impl.SettingsFormEntryComponentModel;
import com.bc.appcore.actions.Action;
import com.bc.appcore.util.Settings;
import com.bc.appcore.typeprovider.SettingsTypeProvider;
import com.bc.selection.Selection;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 4:23:06 AM
 */
public class DisplaySettingsUI implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(DisplaySettingsUI.class.getName());

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.exceptions.TaskExecutionException {

        final Settings settings = app.getSettings();
        
        logger.log(Level.FINE, "Settings: {0}", settings);
        
        final Map sourceData = new TreeMap(settings);
        final ComponentProperties props = app.getOrException(ComponentProperties.class);
        final int width = props.getWidth(app.getUIContext().getMainFrame());
        final FormEntryComponentModel formEntryComponentModel = new SettingsFormEntryComponentModel(
                settings, 
                new ComponentModelWithTableAsEntityListUI(app, props, width/6) {
                    @Override
                    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
                        final List<Selection> output;
                        final List options = settings.getOptions(name);
                        if(options == null || options.isEmpty()) {
                            output = Collections.EMPTY_LIST;
                        }else{
                            final List temp = new ArrayList(options.size());
                            options.stream().forEach((e) -> temp.add(Selection.from(e)));
                            output = Collections.unmodifiableList(temp);
                        }
                        return output;
                    }
                }, 
                -1
        );
        
        final Container ui = (Container)app.getOrException(UIBuilderFromMap.class)
                .sourceData(sourceData)
                .componentModel(formEntryComponentModel)
                .typeProvider(new SettingsTypeProvider(settings))
                .build();
        
        final Callable extractOptionsFromUI = (Callable) () -> {
            
            final Map<String, Object> map = new HashMap();
            map.put("ui", ui);
            map.put("options", sourceData);
            map.put(FormEntryComponentModel.class.getName(), formEntryComponentModel);
            
            final Map extracted = (Map)app.getAction(ActionCommands.EXTRACT_FROM_UI).execute(app, map);
            
            if(!extracted.isEmpty()) {
                
                settings.updateAll(app, extracted);
            
                app.getUIContext().showSuccessMessage("SUCCESS");
            }
            
            return extracted;
        };
        
        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                ui, "Edit Properties", "Save Changes", extractOptionsFromUI, true);
        
//        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
//                ui, "Edit Properties", "Save Changes", ActionCommands.UPDATE_SETTINGS_FROM_UI, false);
//        app.getExpirableAttributes().putFor(ui, settings);
        
        return Boolean.TRUE;
    }
}
