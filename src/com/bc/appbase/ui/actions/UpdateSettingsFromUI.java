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
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.appcore.util.Expirable;
import com.bc.appcore.util.Settings;
import java.awt.Window;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 7:15:04 PM
 */
public class UpdateSettingsFromUI implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(UpdateSettingsFromUI.class.getName());

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Optional optional = params.values().stream().filter((value) -> value instanceof JComponent).findFirst();
        
        if(!optional.isPresent()) {
            throw new ParameterNotFoundException();
        }
        
        final JComponent ui = (JComponent)optional.get();
        
        final Window window = (Window)ui.getTopLevelAncestor();
        
        try{
            
            window.setVisible(false);
            
            final Expirable uiSourceExpirable = app.removeExpirable(ui, null);

            if(uiSourceExpirable == null) {
                throw new TaskExecutionException("Session has expired. Begin process afresh");
            }

            final Settings settings = (Settings)uiSourceExpirable.get();
            
            final Map update = (Map)app.get(FromUIBuilder.class)
//                    .componentModel(app.get(ComponentModel.class))
                    .context(app)
                    .filter(FromUIBuilder.Filter.ACCEPT_UPDATES_ONLY)
                    .ui(ui)
                    .source(settings)
                    .target(new LinkedHashMap())
                    .build();
            
            logger.log(Level.FINE, "Updates: {0}", update);

            if(!update.isEmpty()) {
                
                settings.updateAll(update);
            
                app.getUIContext().showSuccessMessage("SUCCESS");
            }
        }catch(IOException e) {    
            throw new TaskExecutionException("Failed to save changes", e);
        }finally{
            window.dispose();
        }
        
        return Boolean.TRUE;
    }
}

