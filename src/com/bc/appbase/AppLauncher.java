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

package com.bc.appbase;

import com.authsvc.client.AppAuthenticationSession;
import com.authsvc.client.AuthenticationException;
import com.bc.appbase.jpa.JpaContextManagerWithUserPrompt;
import com.bc.appbase.properties.AuthSvcPropertiesBuilder;
import com.bc.appbase.properties.AuthSvcPropertiesBuilderImpl;
import com.bc.appbase.properties.PropertiesBuilderExceptionHandler;
import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.MessageTextAreaOnStartup;
import com.bc.appbase.ui.ScreenLog;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.actions.SetLookAndFeel;
import com.bc.appbase.ui.dialog.PopupImpl;
import com.bc.appbase.ui.dialog.SimpleErrorOptions;
import com.bc.appcore.jpa.JpaContextManager;
import com.bc.appcore.AppLauncherCore;
import com.bc.appcore.ProcessLog;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.parameter.ParameterException;
import com.bc.config.Config;
import com.bc.jpa.search.SearchResults;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 20, 2017 10:06:03 PM
 */
public class AppLauncher<A extends App> extends AppLauncherCore<A> {

    private static final Logger logger = Logger.getLogger(AppLauncher.class.getName());

    private Class entityType;
    
    private UIContext uiContext;

    public AppLauncher() { }

    public AppLauncher processLogUIForTitle(String startupScreenTitle) {
        this.processLog(new ScreenLog(startupScreenTitle, "Startup Log", new MessageTextAreaOnStartup(5, 20), 400, 300));
        return this;
    }
    
    public AppLauncher entityType(Class entityType) {
        this.entityType = entityType;
        return this;
    }

    @Override
    protected AppAuthenticationSession createAuthSession() 
            throws IOException, ParseException {
        
        final AuthSvcPropertiesBuilder propertiesBuilder = this.getAuthSvcPropertiesBuilder();
        
        final Predicate<Properties> propertiesValidator = (properties) -> {
            try{
                
                return this.createAuthSession(properties) != null;
                
            }catch(IOException | ParseException | AuthenticationException e) {
                
                final Function<Exception, String> getExceptionMsg = (exception) -> {
                    if(exception instanceof AuthenticationException) {
                        return e.getLocalizedMessage();
                    }else{
                        return "Unexpected error";
                    }
                };
                
                new PropertiesBuilderExceptionHandler(propertiesBuilder, getExceptionMsg).accept(e);
                
                return false;
            }
        };

        final Properties defaultProperties = this.loadAuthProperties();
        
        logger.fine(() -> "Loaded auth properties: " + (defaultProperties == null ? null : defaultProperties.stringPropertyNames()));
        
        if(defaultProperties == null || defaultProperties.stringPropertyNames().isEmpty()) {
            
            return null;
            
        }else{
            
            final File file = this.getPropertiesContext().getAuthsvc().toFile();

            propertiesBuilder
                    .optionsProvider("Configure Application Authentication")
                    .sourceFile(file)
                    .displayPromptAtLeastOnce(this.isNewInstallation())
                    .validator(propertiesValidator)
                    .maxTrials(this.getMaxTrials())
                    .defaultValues(defaultProperties)
                    .build();

            return Objects.requireNonNull(this.getAuthenticationSession());
        }
    }
    
    @Override
    protected Properties loadAuthProperties() throws IOException {
        final Properties defaultValues = this.getDefaultAuthProperties();
        final Properties fromFile = super.loadAuthProperties();
        final Set<String> names = fromFile.stringPropertyNames();
        for(String name : names) {
            final String value = fromFile.getProperty(name, null);
            if(value != null) {
                defaultValues.setProperty(name, value);
            }
        }
        return defaultValues;
    }

    protected AuthSvcPropertiesBuilder getAuthSvcPropertiesBuilder() {
        final AuthSvcPropertiesBuilder builder = new AuthSvcPropertiesBuilderImpl();
        return builder;
    }
    
    @Override
    public JpaContextManager getJpaContextManager() {
        return new JpaContextManagerWithUserPrompt(this.uiContext, this.getPropertiesContext(), this.getMasterPersistenceUnitTest());
    }

    @Override
    protected void onInstallationCompleted(A app) {
        ((ScreenLog)this.getProcessLog()).querySaveLogThenSave("installation");
    }
    
    protected void setLookAndFeel(Config config) {
        final String lookAndFeelName = this.getLookAndFeel(config, "Motif");
        try{
            new SetLookAndFeel().execute(lookAndFeelName);
//System.out.println(this.getClass().getName()+"--------------------------------\n"+new JsonFormat(true, true, "  ").toJSONString(config.getProperties()));            
        }catch(ParameterException | TaskExecutionException e) {
            logger.log(Level.WARNING, "Exception setting look and feel to: " + lookAndFeelName, e);
        }
    }

    protected void configureUI(UIContext uiContext, Config config) { }
    
    /**
     * <b>This method is called on the AWT Event Queue</b>
     * Initialize the app's User Interface (UI).
     * @param app The app whose UI will be initialized
     */
    @Override
    protected void initUI(A app) {

        final ProcessLog processLog = getProcessLog();
        
        processLog.log("Creating user interface");

        setLookAndFeel(app.getConfig());

        processLog.log("Configuring user interface");

        final Config config = app.getConfig();

        this.uiContext = app.getUIContext();

        final AppLauncher ref = AppLauncher.this;

        configureUI(uiContext, config);

//        if(this.isEnableSync()) {
//            app.getAction(ActionCommands.SYNC_IF_SLAVE_DATABASE_EMPTY).executeSilently(app);
//        }

        processLog.log("Loading search results");

        ref.loadSearchResults(app, entityType);

        final JFrame mainFrame = uiContext.getMainFrame();

        mainFrame.pack();

        processLog.log("Displaying user interface");

        mainFrame.setVisible(true);

        if(isNewInstallation() && app.getAuthenticationSession() != null) {
            try{
                app.getAction(ActionCommands.NEWUSER_VIA_USER_PROMPT).execute(app);
            }catch(ParameterException | TaskExecutionException e) {
                logger.log(Level.WARNING, "Error executing action: " + ActionCommands.NEWUSER_VIA_USER_PROMPT, e);
            }
        }
    }
    
    protected <T> void loadSearchResults(A app, Class<T> entityType) {

        final SearchContext<T> searchContext = app.getSearchContext(entityType);

        final SearchResults<T> searchResults = searchContext.searchAll();

        final JFrame frame = this.uiContext.getMainFrame();
        
        uiContext.positionFullScreen(frame);

        if(frame instanceof MainFrame) {
            
            final SearchResultsPanel resultsPanel = ((MainFrame)frame).getSearchResultsPanel();
            
            resultsPanel.load(searchContext, 
                    searchResults, "AppMainFrame", 0, 1, true);
        }
    }
    
    @Override
    protected void onStartupException(Throwable t, String description, int exitCode) {
        
        logger.log(Level.SEVERE, description, t);
        
        new PopupImpl(null, new SimpleErrorOptions()).showErrorMessage(t, description);

        System.exit(exitCode);
    }

    public Class getEntityType() {
        return entityType;
    }

    public UIContext getUiContext() {
        return uiContext;
    }
}
