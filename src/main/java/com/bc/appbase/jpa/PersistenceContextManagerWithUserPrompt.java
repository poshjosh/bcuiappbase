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

package com.bc.appbase.jpa;

import com.bc.appbase.properties.JpaJdbcPropertiesBuilder;
import com.bc.appbase.properties.JpaJdbcPropertiesBuilderImpl;
import com.bc.appbase.properties.PropertiesBuilder;
import com.bc.appbase.properties.PropertiesBuilderExceptionHandler;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.jpa.PersistenceContextManagerImpl;
import com.bc.appcore.properties.PropertiesContext;
import com.bc.jpa.context.PersistenceContext;
import com.bc.xml.PersistenceXmlDomImpl;
import com.bc.jpa.predicates.DatabaseCommunicationsFailureTest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import com.bc.xml.PersistenceXmlDom;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2017 5:24:58 PM
 */
public class PersistenceContextManagerWithUserPrompt extends PersistenceContextManagerImpl {

    private static final Logger logger = Logger.getLogger(PersistenceContextManagerWithUserPrompt.class.getName());

    private final UIContext uiContext;
    
    private final PropertiesContext propertiesContext;
    
    private final Predicate<Throwable> dbCommsFailureTest;

    private final Predicate<String> persistenceUnitRequiresAuthentication;

    private final Map<String, Properties> defaultProperties;
        
    public PersistenceContextManagerWithUserPrompt(
            UIContext uiContext, 
            PropertiesContext filepaths, 
            Predicate<String> persistenceUnitRequiresAuthenticationTest) {
        this.persistenceUnitRequiresAuthentication = 
                Objects.requireNonNull(persistenceUnitRequiresAuthenticationTest);
        this.uiContext = uiContext;
        this.propertiesContext = Objects.requireNonNull(filepaths);
        this.dbCommsFailureTest = new DatabaseCommunicationsFailureTest();
        this.defaultProperties = new HashMap();
    }

    @Override
    public PersistenceContext create(URI uri, int maxTrials, boolean freshInstall) 
            throws URISyntaxException {
        
        final PersistenceXmlDom persistenceDom = new PersistenceXmlDomImpl(uri);
        
        final PersistenceContext persistenceContext = this.newInstance(uri);
        
        final Set<String> persistenceUnitNames = persistenceContext.getMetaData(false).getPersistenceUnitNames();
        
        for(final String persistenceUnit : persistenceUnitNames) {
            
            final JpaJdbcPropertiesBuilder propertiesBuilder = new JpaJdbcPropertiesBuilderImpl();
            
            final Predicate<Properties> propertiesValidator = (properties) -> {
                try{
                    
                    logger.finer(() -> "\n\tAdding properties for persistence unit: " + 
                            persistenceUnit + '\n' + properties);
                    
                    defaultProperties.put(persistenceUnit, properties);
                    
                    init(persistenceContext.getContext(persistenceUnit), freshInstall);
                    
                    return true;
                    
                }catch(RuntimeException | SQLException | IOException e) {
                    
                    this.handleException(e, propertiesBuilder);

                    return false;
                }
            };
            
            boolean exceptionHandled = false;
            
            try{
                
                propertiesBuilder
                        .optionsProvider("Properties for Persistence Unit: " + persistenceUnit)
                        .sourceFile(this.getPropertiesContext(), persistenceUnit)
                        .authenticationRequired(this.persistenceUnitRequiresAuthentication.test(persistenceUnit))
                        .defaultValues(persistenceDom.getProperties(persistenceUnit))
                        .validator(propertiesValidator)
                        .maxTrials(maxTrials)
                        .displayPromptAtLeastOnce(freshInstall)
                        .build();
            }catch(Exception e) {
                
                this.handleException(e, propertiesBuilder);
                
                exceptionHandled = true;
            }
            
            if(!exceptionHandled && !propertiesBuilder.isLastValidationSuccessful()) {
                
                logger.warning(() -> "Failed to initialize database properties.\n" + propertiesBuilder);
            
//                throw new UserRuntimeException("Failed to load database properties");
            }
        }
        
        return persistenceContext;
    }
    
    public void handleException(Exception e, PropertiesBuilder propertiesBuilder) {

        new PropertiesBuilderExceptionHandler(propertiesBuilder).accept(e,
                dbCommsFailureTest.test(e) ? "Internet connection failed" : "Unexpected exception");
    }

    @Override
    public Function<String, Properties> getPropertiesProvider() {
        final Function<String, Properties> getProps = (persistenceUnit) -> {
            logger.finer(() -> "\n\tFetching properties for persistence unit: " + persistenceUnit);                
            return defaultProperties.getOrDefault(persistenceUnit, new Properties());
        };
        return getProps;
    }
    
    public PropertiesContext getPropertiesContext() {
        return propertiesContext;
    }
}
