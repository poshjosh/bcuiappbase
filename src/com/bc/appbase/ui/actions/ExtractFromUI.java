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
import com.bc.appbase.ui.components.ComponentModelImpl;
import com.bc.appbase.ui.components.FormEntryComponentModel;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.components.FormEntryComponentModelImpl;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 21, 2017 9:24:32 PM
 */
public class ExtractFromUI implements Action<App, Map> {

    private static final Logger logger = Logger.getLogger(ExtractFromUI.class.getName());

    @Override
    public Map execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final ParameterExtractor paramExtractor = app.getOrException(ParameterExtractor.class);
        
        final JComponent ui = paramExtractor.getFirstValue(params, JComponent.class);
        Objects.requireNonNull(ui);
        
        final Window window = (Window)ui.getTopLevelAncestor();
        
        try{
            
            window.setVisible(false);
            
            final Map options = paramExtractor.getFirstValue(params, Map.class);
            Objects.requireNonNull(options);
            
            FormEntryComponentModel componentModel = paramExtractor.getFirstValue(params, FormEntryComponentModel.class);
            if(componentModel == null) {
                componentModel = new FormEntryComponentModelImpl(new ComponentModelImpl(app));
            }
            
            final Map optionsFromUI = (Map)app.getOrException(FromUIBuilder.class)
                    .componentModel(componentModel)
                    .filter(FromUIBuilder.Filter.ACCEPT_UPDATES_ONLY)
                    .ui(ui)
                    .source(options)
                    .target(new LinkedHashMap())
                    .build();
            
            logger.log(Level.FINE, "Options from UI: {0}", optionsFromUI);

            return optionsFromUI;

        }finally{
            window.dispose();
        }
    }
}
