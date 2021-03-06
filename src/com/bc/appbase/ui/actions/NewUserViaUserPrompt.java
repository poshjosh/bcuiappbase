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
import java.util.Map;
import javax.security.auth.login.LoginException;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 29, 2017 10:01:07 PM
 */
public class NewUserViaUserPrompt extends AuthenticationAction {

    public NewUserViaUserPrompt() {
        super("Create New User", "User Creation Successful", "User Creation Failed");
    }

    @Override
    public void doExecute(App app, Map params) throws LoginException {
        app.getUser().create(params);
    }
}