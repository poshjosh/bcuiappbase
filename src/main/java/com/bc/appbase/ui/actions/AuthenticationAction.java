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

import com.authsvc.client.AppAuthenticationSession;
import com.bc.appbase.App;
import com.bc.appcore.properties.LoginProperties;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.layout.SequentialLayout;
import com.bc.ui.layout.VerticalLayout;
import com.bc.appbase.properties.GetOptionsViaUserPrompt;
import com.bc.appbase.properties.PropertiesBuilder;
import com.bc.appbase.properties.PropertiesBuilderImpl;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.selection.SelectionContext;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import com.bc.appcore.user.User;
import com.bc.jpa.search.TextSearch;
import com.bc.appcore.properties.OptionsProvider;
import com.bc.jpa.predicates.DatabaseCommunicationsFailureTest;
import java.util.Optional;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 29, 2017 10:36:03 PM
 */
public abstract class AuthenticationAction implements Action<App, User> {

    private static final Logger logger = Logger.getLogger(AuthenticationAction.class.getName());
    
    private class UserAuthOptionsViaPrompt extends GetOptionsViaUserPrompt {
        private final App app;
        public UserAuthOptionsViaPrompt(
                App app, ComponentModel componentModel, 
                SequentialLayout layout, String dialogTitle, 
                Function<String, String> nameToLabel, int width) {
            super(app.getUIContext().getMainFrame(), componentModel, 
                    layout, dialogTitle, nameToLabel, width);
            this.app = Objects.requireNonNull(app);
        }
        @Override
        public Class getType(String name, Object value) {
            final Class fieldType;
            switch(name) {
                case LoginProperties.USERNAME: 
                    fieldType = app.getUserEntityType(); 
                    break;
                default: 
                    fieldType = value == null ? Object.class : value.getClass();
            }
            return fieldType;
        }
    }
    
    private class UserAuthPropertiesBuilder extends PropertiesBuilderImpl {
        private final SelectionContext selectionContext;
        private final TextSearch textSearch;
        public UserAuthPropertiesBuilder(App app) {
            this(app.getOrException(SelectionContext.class), app.getActivePersistenceUnitContext().getTextSearch());
        }
        
        public UserAuthPropertiesBuilder(SelectionContext selectionContext, TextSearch textSearch) {
            this.selectionContext = selectionContext;
            this.textSearch = textSearch;
        }
        @Override
        public Object fromString(String key, String val) {
            final Object output;
            final Class type = this.getOptionsProvider().getType(key, val);
            if(this.selectionContext.isSelectionType(type)) {
                output = this.textSearch.search(type, val).get(0);
                this.logOutput(key, val, output);
            }else{
                output = super.fromString(key, val);
            }
            return output;
        }
        @Override
        public String toString(String key, Object val) {
            final String output;
            final Class type = this.getOptionsProvider().getType(key, val);
            if(this.selectionContext.isSelectionType(type)) {
                output = this.selectionContext.getSelection(val).getDisplayValue();
                this.logOutput(key, val, output);
            }else{
                output = super.toString(key, val);
            }
            return output;
        }
    }
    
    private final Class type;
    
    private final String charsetName;
    
    private final int maxTrials;

    private final Function<String, String> nameToLabel;
    
    private final SequentialLayout layout;
    
    private final int width;
    
    private final String dialogTitle;
    
    private final String successMessage;
    
    private final String errorMessage;
    
    public AuthenticationAction(String dialogTitle, String successMessage, String errorMessage) {
        this(
                1, (name) -> Character.toTitleCase(name.charAt(0)) + name.substring(1), 
                500, dialogTitle, successMessage, errorMessage);
    }
    
    public AuthenticationAction(
            int maxTrials, Function<String, String> nameToLabel, int width,
            String dialogTitle, String successMessage, String errorMessage) {
        this(
                LoginProperties.class, "utf-8", maxTrials, nameToLabel, new VerticalLayout(),
                width, dialogTitle, successMessage, errorMessage
        );
    }

    public AuthenticationAction(Class type, String charsetName, int maxTrials, 
            Function<String, String> nameToLabel, SequentialLayout layout, int width, 
            String dialogTitle, String successMessage, String errorMessage) {
        this.type = Objects.requireNonNull(type);
        this.charsetName = Objects.requireNonNull(charsetName);
        this.maxTrials = maxTrials;
        this.nameToLabel = Objects.requireNonNull(nameToLabel);
        this.layout = Objects.requireNonNull(layout);
        this.width = width;
        this.dialogTitle = Objects.requireNonNull(dialogTitle);
        this.successMessage = Objects.requireNonNull(successMessage);
        this.errorMessage = Objects.requireNonNull(errorMessage);
    }

    @Override
    public User execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        logger.fine(() -> "Params: " + params.keySet());
        
        final User user = app.getUser();
        
        if(user.isLoggedIn()) {
            
            app.getUIContext().showErrorMessage(null, "Already logged in as: "+user.getName());
            
            return user;
        }
        
        int noOfTimesPromptShown = 0;
        
        Map authParams;
        try{
            
            final File file = app.getPropertiesContext().getUserAuth().toFile();
            if(!file.exists()) {
                file.createNewFile();
            }

            final ComponentModel componentModel = this.getComponentModel(app);
            
            final OptionsProvider optionsProvider = new UserAuthOptionsViaPrompt(
                    app, componentModel, layout, dialogTitle, nameToLabel, width
            );

            final PropertiesBuilder builder = new UserAuthPropertiesBuilder(app);
            authParams = builder
                    .optionsProvider(optionsProvider)
                    .sourceFile(file).type(type)
                    .charsetName(charsetName)
                    .displayPromptAtLeastOnce(true)
                    .maxTrials(maxTrials).build();
            
            noOfTimesPromptShown = builder.getValidationAttempts();
            
        }catch(IOException e) {
            
            logger.log(Level.WARNING, "Unexpected exception", e);
            
            throw new TaskExecutionException("Unexpected exception. Try again later");
            
        }catch(RuntimeException e) {
            
            if(app.getOrException(DatabaseCommunicationsFailureTest.class).test(e)) {
                
                app.getUIContext().showErrorMessage(e, errorMessage + ": Not connected to internet");
                
                return user;
                
            }else{
                
                throw e;
            }
        }
        
        try{

            final Integer interval = (Integer)params.getOrDefault(ParamNames.INTERVAL, 500);
            final Integer timeout = (Integer)params.getOrDefault(ParamNames.TIMEOUT, 10_000);
            
            final Optional<AppAuthenticationSession> optAuthSess = app.getAuthenticationSession();
            
            final AppAuthenticationSession authSess = optAuthSess.orElseThrow(() -> 
                    new TaskExecutionException("Authentication not Supported")
            );
            
            if(!authSess.isServiceAvailable()) {
                try{
                    authSess.waitForInitializationToComplete(interval, timeout);
                }catch(InterruptedException e) {
                    logger.log(Level.WARNING, "Interrupted wait for authentication service to be available", e);
                }
                if(!authSess.isServiceAvailable()) {
                    throw new TaskExecutionException("Authentication Service Unavailable");
                }
            }
            
            logger.fine(() -> "Authentication Params: " + authParams.keySet());
            
            this.auth(app, authParams);
            
            app.getAction(ActionCommands.UPDATE_LOGIN_BUTTON_TEXT).executeSilently(app);
            
            if(noOfTimesPromptShown > 0) {
                app.getUIContext().showSuccessMessage(successMessage);
            }
            
        }catch(LoginException e) {
            
            logger.log(Level.WARNING, errorMessage, e);
            
            app.getUIContext().showErrorMessage(e, errorMessage + ": " + e.getLocalizedMessage());
        }
        
        return user;
    }
    
    public ComponentModel getComponentModel(App app) {
        return app.getOrException(ComponentModel.class);
    }
    
    public abstract void auth(App app, Map params) throws LoginException;
}

