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
import com.bc.appcore.jpa.decorators.EntityManagerRetryUpdate;
import com.bc.appcore.jpa.decorators.JpaContextWithUpdateListener;
import com.bc.appcore.properties.PropertiesContext;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.dom.PersistenceDOM;
import com.bc.jpa.dom.PersistenceDOMImpl;
import com.bc.jpa.sync.PendingUpdatesManager;
import com.bc.jpa.sync.predicates.PersistenceCommunicationsLinkFailureTest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2017 5:24:58 PM
 */
public class JpaContextManagerWithUserPrompt extends JpaContextManagerImpl {

    private static final Logger logger = Logger.getLogger(JpaContextManagerWithUserPrompt.class.getName());

    private final UIContext uiContext;
    
    private final PropertiesContext propertiesContext;
    
    private final Predicate<Throwable> communicationsFailureTest;

    public JpaContextManagerWithUserPrompt(UIContext uiContext, PropertiesContext filepaths) {
        this(uiContext, filepaths, (persistenceUnit) -> true);
    }

    public JpaContextManagerWithUserPrompt(UIContext uiContext, PropertiesContext filepaths, Predicate<String> persistenceUnitTest) {
        super(persistenceUnitTest);
        this.uiContext = uiContext;
        this.propertiesContext = Objects.requireNonNull(filepaths);
        this.communicationsFailureTest = new PersistenceCommunicationsLinkFailureTest();
    }

    @Override
    public JpaContext createJpaContext(URI uri, int maxTrials, boolean freshInstall) 
            throws URISyntaxException {
        
        final PersistenceDOM persistenceDom = new PersistenceDOMImpl(uri);
        
        final List<String> persistenceUnitNames = persistenceDom.getPersistenceUnitNames();
        
        final Map<String, Properties> defaultProperties = new HashMap(persistenceUnitNames.size(), 1.0f);
        
        final JpaContext jpaContext = new JpaContextImpl(uri, null){
            @Override
            public Properties getPersistenceUnitProperties(String persistenceUnit) {
                return defaultProperties.get(persistenceUnit);
            }
        };
        
        for(final String persistenceUnit : persistenceUnitNames) {
            
            final AtomicBoolean isInternetFail = new AtomicBoolean(false);
        
            final JpaJdbcPropertiesBuilder propertiesBuilder = new JpaJdbcPropertiesBuilderImpl();
            
            final Predicate<Properties> propertiesValidator = (properties) -> {
                try{
                    
                    defaultProperties.put(persistenceUnit, properties); 
                    
                    final Class aClass = jpaContext.getMetaData().getEntityClasses(persistenceUnit)[0];
                    
                    jpaContext.getDao(aClass).builderForSelect(aClass).count(aClass);
                    
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
                        .authenticationRequired(this.getPersistenceUnitTest().test(persistenceUnit))
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
    public JpaContext configureJpaContext(JpaContext jpaContext, PendingUpdatesManager pendingUpdatesManager) {
        
        final Predicate<Exception> test = (exception) -> {
            if(communicationsFailureTest.test(exception)) {
                final int option = JOptionPane.showConfirmDialog(
                        uiContext == null ? null : uiContext.getMainFrame(), 
                        "Update failed! Do you want to retry the update in the background?", 
                        "Retry Update in Background?", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                return option == JOptionPane.YES_OPTION;
            }else{
                return false;
            }    
        };
        
        return new JpaContextWithUpdateListener(jpaContext, 
                new EntityManagerRetryUpdate(pendingUpdatesManager, test));
    }
    
    public PropertiesContext getPropertiesContext() {
        return propertiesContext;
    }
}
