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
import com.bc.appcore.properties.PropertiesManager;
import com.bc.jpa.dom.PersistenceDOM;
import com.bc.jpa.dom.PersistenceDOMImpl;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 12, 2017 11:38:07 AM
 */
public class JdbcPropertiesProviderImpl implements JdbcPropertiesProvider {
    
    private final PersistenceDOM persistenceDom;
    
    private final Function<String, File> getPropsFileForPersistenceUnit;
    
    private final boolean displayPromptAtLeastOnce;
    
    private final Predicate<String> authenticationRequiredTest;

    public JdbcPropertiesProviderImpl(AppContext appContext, boolean displayPromptAtLeaseOnce) {
        this(
                appContext.getJpaContext().getPersistenceConfigURI(), 
                new GetPropertiesFileForPersistenceUnit(appContext), 
                appContext.getMasterPersistenceUnitTest(),
                displayPromptAtLeaseOnce);
    }
    
    public JdbcPropertiesProviderImpl(
            URI persistenceConfigURI, Function<String, File> getPropsFileForPersistenceUnit, 
            Predicate<String> authenticationRequired, boolean displayPromptAtLeastOnce) {
        this(
                persistenceConfigURI == null ? null : new PersistenceDOMImpl(persistenceConfigURI),
                getPropsFileForPersistenceUnit, authenticationRequired, displayPromptAtLeastOnce
        );
    }

    public JdbcPropertiesProviderImpl(
            PersistenceDOM persistenceDom, Function<String, File> getPropsFileForPersistenceUnit, 
            Predicate<String> authenticationRequired, boolean displayPromptAtLeastOnce) {
        this.persistenceDom = persistenceDom;
        this.getPropsFileForPersistenceUnit = Objects.requireNonNull(getPropsFileForPersistenceUnit);
        this.authenticationRequiredTest = authenticationRequired;
        this.displayPromptAtLeastOnce = displayPromptAtLeastOnce;
    }
    
    @Override
    public Properties apply(String puName) {
        
        final Properties defaultProperties = this.getDefaultProperties(puName);
        
        final File propertiesFile = this.getPropsFileForPersistenceUnit.apply(puName);
            
        try{
            
            final boolean authenticationRequired = this.authenticationRequiredTest.test(puName);
            
            final String dialogTitle = "Properties for Peristence Unit: " + puName;
            
            final PropertiesManager propsMgr = new JdbcPropertiesManager(
                    propertiesFile, authenticationRequired, dialogTitle, 3, this.displayPromptAtLeastOnce
            );
            
            final Properties puProps = propsMgr.getProperties(defaultProperties);
            
            return puProps;
            
        }catch(IOException e) {
            
            Logger.getLogger(this.getClass().getName()).log(
                    Level.WARNING, "Exception reading/writing file: " + propertiesFile, e);
            
            
            return defaultProperties;
        }
    }

    public Properties getDefaultProperties(String persistenceUnit) {
        return persistenceDom == null ? new Properties() : persistenceDom.getProperties(persistenceUnit);
    }
}
