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

package com.bc.appbase.properties;

import com.bc.appcore.properties.AuthSvcNameToLabel;
import com.bc.appcore.properties.AuthSvcProperties;
import java.io.File;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 2, 2017 10:22:02 AM
 */
public class AuthSvcPropertiesManager extends PropertiesManagerViaUserPrompt {
    public AuthSvcPropertiesManager(File dest, String dialogTitle, 
            int maxTrials, boolean displayPromptAtLeastOnce) {
        super(dest, AuthSvcProperties.class, dialogTitle, 
                new AuthSvcNameToLabel(), maxTrials, displayPromptAtLeastOnce);
    }
}
