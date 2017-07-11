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
import com.bc.appbase.ui.builder.PromptUserCreateNew;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.exceptions.EntityInstantiationException;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 8, 2017 11:46:27 AM
 */
public class DisplayAddSelectionTypeUI implements Action<App, Object> {
    
    @Override
    public Object execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Class entityType;
        if(params.get(ParamNames.ENTITY_TYPE) == null) {
            entityType = (Class)app.getAction(ActionCommands.PROMPT_SELECT_SELECTION_TYPE).execute(app, params);
        }else{
            entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        }
        
        if(entityType == null) {
            
            return null;
        }
        
        final PromptUserCreateNew prompt = app.getOrException(PromptUserCreateNew.class);
        
        try{
            
            return prompt.promptCreateNew(entityType, null);
            
        }catch(EntityInstantiationException e) {
            
            throw new TaskExecutionException(e);
        }
    }
}
