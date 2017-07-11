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
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.builder.impl.SettingsFormEntryComponentModel;
import com.bc.appcore.actions.Action;
import com.bc.appcore.util.Settings;
import com.bc.appcore.typeprovider.SettingsTypeProvider;
import java.awt.Container;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 4:23:06 AM
 */
public class DisplaySettingsUI implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(DisplaySettingsUI.class.getName());

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {

        final Settings settings = app.getSettings();
        
        logger.log(Level.FINE, "Settings: {0}", settings);
        
        final Container ui = (Container)app.getOrException(UIBuilderFromMap.class)
                .sourceData(new TreeMap(settings))
                .entryUIProvider(new SettingsFormEntryComponentModel(app, -1))
                .typeProvider(new SettingsTypeProvider(settings))
                .build();

        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                ui, "Edit Properties", "Save Changes", ActionCommands.UPDATE_SETTINGS_FROM_UI, false);
        
        app.getExpirableAttributes().putFor(ui, settings);
        
        return Boolean.TRUE;
    }
}
