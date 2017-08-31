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
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appbase.ui.builder.FormEntryComponentModel;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.builder.impl.FormEntryComponentModelImpl;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.appcore.typeprovider.MemberTypeProvider;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 21, 2017 10:17:47 PM
 */
public class DisplayOptionsUI implements Action<App, Container> {

    private static final Logger logger = Logger.getLogger(DisplayOptionsUI.class.getName());
    
    private Map<String, Object> updatedParams;
    
    @Override
    public Container execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        this.updatedParams = new HashMap<>(params);
        
        final ParameterExtractor paramExtractor = app.getOrException(ParameterExtractor.class);
        
        final Map options = paramExtractor.getFirstValue(this.updatedParams, Map.class);
        
        logger.log(Level.FINE, "Options to display: {0}", options);
        
        if(options == null) {
            throw new ParameterNotFoundException("options");
        }
        
        if(options.isEmpty()) {
            throw new InvalidParameterException("options");
        }
        
        FormEntryComponentModel componentModel = paramExtractor.getFirstValue(this.updatedParams, FormEntryComponentModel.class);
        if(componentModel == null) {
            componentModel = new FormEntryComponentModelImpl(new ComponentModelImpl(app));
            this.updatedParams.put(FormEntryComponentModel.class.getName(), componentModel);
        }
        
        MemberTypeProvider typeProvider = paramExtractor.getFirstValue(this.updatedParams, MemberTypeProvider.class);
        if(typeProvider == null) {
            typeProvider = MemberTypeProvider.fromValueType();
            this.updatedParams.put(MemberTypeProvider.class.getName(), typeProvider);
        }
        
        final Container ui = (Container)app.getOrException(UIBuilderFromMap.class)
                .sourceData(options)
                .entryUIProvider(componentModel)
                .typeProvider(typeProvider)
                .build();
        
        return ui;
    }

    public Map<String, Object> getUpdatedParams() {
        return updatedParams;
    }
}
