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

import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.SequentialLayout;
import com.bc.appcore.properties.OptionsProvider;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 9:42:24 PM
 */
public interface PropertiesBuilder {

    Properties build() throws IOException;

    PropertiesBuilder charsetName(String charsetName);

    PropertiesBuilder defaultValues(Map defaultValues);

    //////////////////////////// Setters ////////////////////////////////
    PropertiesBuilder displayPromptAtLeastOnce(boolean displayPromptAtLeastOnce);

    String getCharsetName();

    Map getDefaultValues();

    int getMaxTrials();

    OptionsProvider getOptionsProvider();

    Set<String> getPropertyNames();

    File getSourceFile();

    Class getType();

    int getUpdateCount();

    int getValidationAttempts();

    Predicate<Properties> getValidator();

    //////////////////////////// Getters ////////////////////////////////
    boolean isBuilt();

    boolean isDisplayPromptAtLeastOnce();
    
    boolean isBuildResultValid();

    PropertiesBuilder maxTrials(int maxTrials);

    PropertiesBuilder optionsProvider(String dialogTitle, Function<String, String> nameToLabel);
    
    PropertiesBuilder optionsProvider(
            Component parent, ComponentModel componentModel, SequentialLayout layout, 
            String dialogTitle, Function<String, String> nameToLabel, int width);
            
    PropertiesBuilder optionsProvider(OptionsProvider optionsProvider);

    PropertiesBuilder propertyNames(Set<String> propertyNames);

    PropertiesBuilder sourceFile(File file);

    PropertiesBuilder type(Class type);

    PropertiesBuilder validator(Predicate<Properties> validator);

}
