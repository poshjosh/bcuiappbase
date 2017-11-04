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

import com.bc.appcore.AppContext;
import com.bc.appcore.functions.GetPropertiesFileForPersistenceUnit;
import com.bc.appcore.properties.JdbcNameToLabel;
import com.bc.appcore.properties.JpaJdbcProperties;
import com.bc.appcore.properties.JpaJdbcPropertiesWithoutAuthentication;
import com.bc.jpa.dom.PersistenceDOMImpl;
import java.net.URI;
import com.bc.appcore.properties.PropertiesContext;
import com.bc.jpa.context.PersistenceContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 10:32:58 PM
 */
public class JpaJdbcPropertiesBuilderImpl extends PropertiesBuilderImpl 
        implements JpaJdbcPropertiesBuilder {

    public JpaJdbcPropertiesBuilderImpl() {
    }
    
    public JpaJdbcPropertiesBuilderImpl(AppContext app, String persistenceUnit) {
        final PersistenceContext jpa = app.getPersistenceContext();
        this.defaultValues(jpa.getPersistenceConfigURI(), persistenceUnit)
                .optionsProvider("Properties for Persistence Unit: " + persistenceUnit)
                .sourceFile(app.getPropertiesContext(), persistenceUnit)
                .authenticationRequired(app.getMasterPersistenceUnitTest().test(persistenceUnit));
    }
    
    @Override
    public JpaJdbcPropertiesBuilder optionsProvider(String dialogTitle) {
        this.optionsProvider(dialogTitle, new JdbcNameToLabel()); 
        return this;
    }

    @Override
    public JpaJdbcPropertiesBuilder authenticationRequired(boolean authenticationRequired) {
        if(authenticationRequired) {
            this.type(JpaJdbcProperties.class);
        }else{
            this.type(JpaJdbcPropertiesWithoutAuthentication.class);
        }
        return this;
    }  

    @Override
    public JpaJdbcPropertiesBuilder defaultValues(URI persistenceUri, String persistenceUnitName) {
        this.defaultValues(new PersistenceDOMImpl(persistenceUri).getProperties(persistenceUnitName));
        return this;
    }

    @Override
    public JpaJdbcPropertiesBuilder sourceFile(PropertiesContext filepaths, String persistenceUnit) {
        this.sourceFile(new GetPropertiesFileForPersistenceUnit(filepaths).apply(persistenceUnit));
        return this;
    }
}
