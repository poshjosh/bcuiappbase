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
import com.bc.appbase.properties.PropertiesBuilderExceptionHandler;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.jpa.JpaContextManagerImpl;
import com.bc.appcore.exceptions.UserRuntimeException;
import com.bc.appcore.properties.PropertiesContext;
import com.bc.jpa.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.context.PersistenceContext;
import com.bc.jpa.context.PersistenceContextEclipselinkOptimized;
import com.bc.jpa.dom.PersistenceDOM;
import com.bc.jpa.dom.PersistenceDOMImpl;
import com.bc.jpa.functions.GetClassLoaderForPersistenceUri;
import com.bc.jpa.sync.predicates.PersistenceCommunicationsLinkFailureTest;
import com.bc.sql.MySQLDateTimePatterns;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2017 5:24:58 PM
 */
public class JpaContextManagerWithUserPrompt extends JpaContextManagerImpl {

    private static final Logger logger = Logger.getLogger(JpaContextManagerWithUserPrompt.class.getName());

    private final UIContext uiContext;
    
    private final PropertiesContext propertiesContext;
    
    private final Predicate<Throwable> communicationsFailureTest;

    private final Predicate<String> persistenceUnitRequiresAuthentication;

    private final Map<String, Properties> defaultProperties;
        
    public JpaContextManagerWithUserPrompt(
            UIContext uiContext, 
            PropertiesContext filepaths, 
            Predicate<String> persistenceUnitRequiresAuthenticationTest) {
        this.persistenceUnitRequiresAuthentication = 
                Objects.requireNonNull(persistenceUnitRequiresAuthenticationTest);
        this.uiContext = uiContext;
        this.propertiesContext = Objects.requireNonNull(filepaths);
        this.communicationsFailureTest = new PersistenceCommunicationsLinkFailureTest();
        this.defaultProperties = new HashMap();
    }

    @Override
    public PersistenceContext createJpaContext(URI uri, int maxTrials, boolean freshInstall) 
            throws URISyntaxException {
        
        final PersistenceDOM persistenceDom = new PersistenceDOMImpl(uri);
        
        final PersistenceContext jpaContext = this.newJpaContext(uri);
        
        final Set<String> persistenceUnitNames = jpaContext.getMetaData(false).getPersistenceUnitNames();
        
        for(final String persistenceUnit : persistenceUnitNames) {
            
            final AtomicBoolean isInternetFail = new AtomicBoolean(false);
        
            final JpaJdbcPropertiesBuilder propertiesBuilder = new JpaJdbcPropertiesBuilderImpl();
            
            final Predicate<Properties> propertiesValidator = (properties) -> {
                try{
                    
                    logger.finer(() -> "\n\tAdding properties for persistence unit: " + persistenceUnit + "\n"+properties);                
                    defaultProperties.put(persistenceUnit, properties);
                    
                    initJpaContext(jpaContext.getContext(persistenceUnit));
                    
                    return true;
                    
                }catch(Exception e) {
                    isInternetFail.set(communicationsFailureTest.test(e));
                    Function<Exception, String> getExceptionMsg = (exception) -> {
                        if(isInternetFail.get()) {
                            return "Internet connection failure";
                        }else{
                            return "Unexpected exception";
                        }
                    }; 
                    new PropertiesBuilderExceptionHandler(propertiesBuilder, getExceptionMsg).accept(e);
                    return false;
                }
            };
            
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
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
            
            if(!propertiesBuilder.isBuildResultValid()) {
                if(isInternetFail.get()) {
                    throw new UserRuntimeException("Internet connection problems led to database initialization failure");
                }else{
                    throw new UserRuntimeException("Database initialization failed");
                }
            }
        }
        
        return jpaContext;
    }

    @Override
    public PersistenceContext newJpaContext(URI uri) {
        final Function<String, Properties> getProps = (persistenceUnit) -> {
            logger.finer(() -> "\n\tFetching properties for persistence unit: " + persistenceUnit);                
            return defaultProperties.getOrDefault(persistenceUnit, new Properties());
        };
        final PersistenceContext jpaContext = new PersistenceContextEclipselinkOptimized(
                uri, 
                new EntityManagerFactoryCreatorImpl(
                        new GetClassLoaderForPersistenceUri().apply(uri.toString()),
                        getProps
                ), 
                new MySQLDateTimePatterns()
        );
        return jpaContext;
    }
    
    public PropertiesContext getPropertiesContext() {
        return propertiesContext;
    }
}
