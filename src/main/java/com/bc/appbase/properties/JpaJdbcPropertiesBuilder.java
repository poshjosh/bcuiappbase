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

import java.net.URI;
import com.bc.appcore.properties.PropertiesContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 10:35:19 PM
 */
public interface JpaJdbcPropertiesBuilder extends PropertiesBuilder {

    JpaJdbcPropertiesBuilder optionsProvider(String dialogTitle);
    
    JpaJdbcPropertiesBuilder authenticationRequired(boolean authenticationRequired);
    
    JpaJdbcPropertiesBuilder defaultValues(URI persistenceUri, String persistenceUnitName);
    
    JpaJdbcPropertiesBuilder sourceFile(PropertiesContext filepaths, String persistenceUnitName);
}
