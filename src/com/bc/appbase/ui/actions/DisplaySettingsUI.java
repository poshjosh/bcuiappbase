/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance source the License.
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
import com.bc.appbase.ui.SimpleFrame;
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appbase.ui.builder.impl.SettingsEntryUIProvider;
import com.bc.appcore.actions.Action;
import com.bc.appcore.util.Expirable;
import com.bc.appcore.util.Settings;
import com.bc.appcore.util.SettingsTypeProvider;
import java.awt.Container;
import java.awt.Font;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 4:23:06 AM
 */
public class DisplaySettingsUI implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {

        final Settings settings = app.getSettings();
        
        final Container ui = (Container)app.get(UIBuilder.class)
                .source(settings)
                .entryUIProvider(new SettingsEntryUIProvider(app))
                .typeProvider(new SettingsTypeProvider(settings))
                .build();

        final SimpleFrame frame = new SimpleFrame(
                "Edit Properties", app, ui, new Font(Font.MONOSPACED, Font.PLAIN, 18),
                " Save Changes ", ActionCommands.UPDATE_SETTINGS_FROM_UI
        );
        
        app.addExpirable(ui, Expirable.from(settings, 10, TimeUnit.MINUTES));
        
//        app.getUIContext().positionHalfScreenLeft(frame);
        
        frame.pack();
        frame.setVisible(true);
        return Boolean.TRUE;
    }
}
