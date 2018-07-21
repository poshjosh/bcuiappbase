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
import java.util.Map;
import com.bc.appcore.user.User;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 5, 2017 3:55:11 PM
 */
public class LoginOrOut implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws ParameterException, TaskExecutionException {
    
        final User user = app.getUser();
        
        if(user.isLoggedIn()) {
            
            app.getAction(ActionCommands.LOGOUT).execute(app);
            
            return Boolean.TRUE;
            
        }else{
            
            app.getAction(ActionCommands.LOGIN_VIA_USER_PROMPT).execute(app);
            
            return Boolean.TRUE;
        }
    }
}
