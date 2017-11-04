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
import com.bc.appcore.actions.ActionCommandsCore;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.sync.JpaSync;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 12, 2017 11:23:21 AM
 */
public class SyncDatabaseWithUserPrompt implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        if(!app.isUsingMasterDatabase()) {
            
            app.getUIContext().showErrorMessage(null, "Sync not allowed");
            
            return Boolean.FALSE;
        }
        
        final JpaSync jpaSync = app.getJpaSync();
        
        if(jpaSync.isRunning()) {
            
            app.getUIContext().showSuccessMessage("Sync already running");
            
            return Boolean.FALSE;
            
        }else{
            
            app.getUIContext().showSuccessMessage("Running sync in background. You will be notified on completion");
            
            app.getAction(ActionCommandsCore.SYNC_DATABASE).execute(app, params);

            return Boolean.TRUE;
        }
    }
}
