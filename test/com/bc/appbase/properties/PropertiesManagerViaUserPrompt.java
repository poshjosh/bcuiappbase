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

import com.bc.appbase.properties.GetOptionsViaUserPrompt;
import com.bc.appbase.ui.components.ComponentModel;
import java.io.File;
import java.util.function.Function;
import java.util.logging.Logger;
import com.bc.appcore.properties.PropertyNamesValidator;
import java.util.Properties;
import java.util.function.Predicate;
import com.bc.appcore.properties.OptionsProvider;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 2, 2017 9:37:51 AM
 */
public class PropertiesManagerViaUserPrompt extends com.bc.appcore.properties.PropertiesManagerImpl {

    private static final Logger logger = Logger.getLogger(PropertiesManagerViaUserPrompt.class.getName());
    
    private final ComponentModel componentModel;
    
    public PropertiesManagerViaUserPrompt(File dest, Class type) {
        this(
                dest, type, "Enter Details (3 attempts)", 
                (name) -> Character.toTitleCase(name.charAt(0)) + name.substring(1), 
                3, false
        );
    }
    
    public PropertiesManagerViaUserPrompt(File dest, Class type, String dialogTitle, 
            Function<String, String> nameToLabel, int maxTrials, boolean displayPromptAtLeastOnce) {
        
        this(new GetOptionsViaUserPrompt(dialogTitle, nameToLabel),
                dest, type, "utf-8", new PropertyNamesValidator(type), maxTrials, displayPromptAtLeastOnce);
    }
    
    public PropertiesManagerViaUserPrompt(
            OptionsProvider prompt, File dest, Class type, String charsetName, 
            Predicate<Properties> validator, int maxTrials, boolean displayPromptAtLeastOnce) {
        
        super(prompt, dest, type, charsetName, validator, maxTrials, displayPromptAtLeastOnce);
        
        this.componentModel = ((GetOptionsViaUserPrompt)prompt).getComponentModel();
    }

    @Override
    protected boolean isPasswordName(String name) {
        return this.componentModel.isPasswordName(name);
    }
}
