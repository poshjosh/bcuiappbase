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
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.Container;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 21, 2017 8:50:39 PM
 */
public class DisplayAndExtractOptionsFromUI implements Action<App, Map> {

    private static final Logger logger = Logger.getLogger(DisplayAndExtractOptionsFromUI.class.getName());
    
    @Override
    public Map execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final DisplayOptionsUI displayUI = new DisplayOptionsUI();
        
        final Container ui = (Container)displayUI.execute(app, params);
        
        final Map output = new LinkedHashMap();
        
        final Callable extractOptionsFromUI = (Callable) () -> {
            
            final Map<String, Object> map = new HashMap(displayUI.getUpdatedParams());
            map.put("ui", ui);
            
            final Map extracted = (Map)app.getAction(ActionCommands.EXTRACT_FROM_UI).execute(app, map);
            output.clear();
            output.putAll(extracted);
            
            return extracted;
        };
        
        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                ui, "Edit", "OK", extractOptionsFromUI, true);
        
        return output;
    }
}
