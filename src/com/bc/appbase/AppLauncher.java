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

import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.MessageTextAreaOnStartup;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.ScreenLog;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appbase.ui.actions.SetLookAndFeel;
import com.bc.appbase.ui.dialog.PopupImpl;
import com.bc.appbase.ui.dialog.SimpleErrorOptions;
import com.bc.appcore.ResourceContext;
import com.bc.appcore.ResourceContextImpl;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.jpa.predicates.MasterPersistenceUnitTest;
import com.bc.appcore.jpa.predicates.SlavePersistenceUnitTest;
import com.bc.appcore.util.ExpirableCache;
import com.bc.appcore.util.ExpirableCacheImpl;
import com.bc.appcore.util.LoggingConfigManager;
import com.bc.appcore.util.LoggingConfigManagerImpl;
import com.bc.config.CompositeConfig;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.config.SimpleConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.search.SearchResults;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.SlaveUpdates;
import com.bc.jpa.sync.impl.JpaSyncImpl;
import com.bc.jpa.sync.impl.RemoteEntityUpdaterImpl;
import com.bc.jpa.sync.impl.SlaveUpdatesImpl;
import com.bc.jpa.sync.predicates.PersistenceCommunicationsLinkFailureTest;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import com.bc.appcore.Filenames;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 8:18:25 PM
 * @param <A> The type of <code>App</code> to be created and launched
 */
public abstract class AppLauncher<A extends App> {
    
    private final AtomicBoolean busy = new AtomicBoolean();

    private static final Logger logger = Logger.getLogger(AppLauncher.class.getName());
    
    private final boolean enableSync;
    private final Class entityType;
    private final Filenames defaultFilenames;
    private final Filenames filenames; 
    private final String [] dirsToCreate;
    private final Path slaveUpdatesDir;
    private final String settingsFile;
    private final String startupScreeTitle;

    public AppLauncher(boolean enableSync, Class entityType, 
            Filenames defaultFilenames, Filenames filenames, 
            String[] dirsToCreate, Path slaveUpdatesDir, 
            String settingsFile, String startupScreeTitle) {
        this.enableSync = enableSync;
        this.entityType = entityType;
        this.defaultFilenames = defaultFilenames;
        this.filenames = filenames;
        this.dirsToCreate = dirsToCreate;
        this.slaveUpdatesDir = slaveUpdatesDir;
        this.settingsFile = settingsFile;
        this.startupScreeTitle = startupScreeTitle;
    }
    
    public void before() { }
    
    public abstract boolean isInstalled(Config config);
    
    public abstract void setInstalled(Config config, boolean installed);
    
    public abstract String getLookAndFeel(Config config);
    
    public abstract String getPersistenceFile(Config config);
    
    public void validateJpaContext(JpaContext jpaContext) { }
    
    public abstract A createApplication(Filenames filenames, 
            ConfigService configService, Config config, Properties settingsConfig, 
            JpaContext jpaContext, SlaveUpdates slaveUpdates, 
            JpaSync jpaSync, ExpirableCache expirableCache);
    
    public void configureUI(UIContext uiContext) { }
    
    public void onLaunchCompleted(A app) { }
    
    public void onShutdown(A app) { }
    
    public synchronized boolean isBusy() {
        return busy.get();
    }
    
    public synchronized void waitTillCompletion() throws InterruptedException{
        try{
            while(busy.get()) {
                this.wait(1000);
            }
        }finally{
            this.notifyAll();
        }
    }
    
    public A launch(String [] args) {

        try{
           
            if(busy.getAndSet(true)) {
                throw new IllegalStateException();
            }

            Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                logger.log(Level.WARNING, "Uncaught exception in thread: " + t.getName(), e);
            });
                                   
            final ScreenLog uiLog = new ScreenLog("Startup Log", new MessageTextAreaOnStartup(5, 20), 400, 300);
            
            uiLog.show();
            
            uiLog.log("");
            uiLog.log(this.startupScreeTitle);
            uiLog.log("");
            uiLog.log("...Initializing");
            
            this.before();
            
            uiLog.log("Initializing folders");
                                
            final String [] filesToCreate = new String[]{
                filenames.getPropertiesFile(),
                filenames.getLoggingConfigFile()
            };
            final ResourceContext resourceContext = new ResourceContextImpl(dirsToCreate, filesToCreate);
            
            uiLog.log("Loading configurations");
                                 
            final ConfigService configService = new SimpleConfigService(
                    defaultFilenames.getPropertiesFile(),
                    filenames.getPropertiesFile()
            );

            final Config config = new CompositeConfig(configService);
            
            final boolean WAS_INSTALLED = this.isInstalled(config);
                      
            final LoggingConfigManager logConfigMgr = new LoggingConfigManagerImpl(resourceContext);
       
            if(WAS_INSTALLED) {
                logConfigMgr.read(filenames.getLoggingConfigFile(), null);
            }else{
                logConfigMgr.init(
                        defaultFilenames.getLoggingConfigFile(), 
                        filenames.getLoggingConfigFile(), null);
            }

            uiLog.log("Setting look and feel");

            new SetLookAndFeel().execute(null, 
                    Collections.singletonMap(ParamNames.LOOK_AND_FEEL_NAME, 
                            this.getLookAndFeel(config)));

            uiLog.log("Initializing database");

            final String persistenceFile = this.getPersistenceFile(config);
            logger.log(Level.INFO, "Peristence file: {0}", persistenceFile);
            final URI persistenceURI = resourceContext.getResource(persistenceFile).toURI();
            final JpaContext jpaContext = new JpaContextImpl(persistenceURI, null);
            this.validateJpaContext(jpaContext);

            final SlaveUpdates slaveUpdates = slaveUpdatesDir == null ? SlaveUpdates.NO_OP :
                    new SlaveUpdatesImpl(
                            slaveUpdatesDir, 
                            new RemoteEntityUpdaterImpl(jpaContext, new MasterPersistenceUnitTest(), new SlavePersistenceUnitTest()),
                            new PersistenceCommunicationsLinkFailureTest());

            final JpaSync jpaSync = !enableSync ? JpaSync.NO_OP :
                    new JpaSyncImpl(jpaContext, 
                            new RemoteEntityUpdaterImpl(jpaContext, new MasterPersistenceUnitTest(), new SlavePersistenceUnitTest()), 
                            20, 
                            new PersistenceCommunicationsLinkFailureTest());

            uiLog.log("Initializing application context");
            
            final Properties settingsMetaData = new Properties();
            
            try(Reader reader = new InputStreamReader(
                    resourceContext.getResourceAsStream(this.settingsFile))) {
                settingsMetaData.load(reader);
                logger.log(Level.FINE, "Loaded settings from: {0}", 
                        resourceContext.getResource(this.settingsFile));
            }

            final ExpirableCache expirableCache = new ExpirableCacheImpl(10, TimeUnit.MINUTES);
            
            final A app = this.createApplication(
                    filenames, configService, config, settingsMetaData, 
                    jpaContext, slaveUpdates, jpaSync, expirableCache 
            );

            uiLog.log("Creating user interface");
            
            /* Create and display the UIContextBase */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try{

                        app.init();

                        uiLog.log("Configuring user interface");

                        final UIContext uiContext = app.getUIContext();
                        
                        final AppLauncher ref = AppLauncher.this;
                        
                        ref.configureUI(uiContext);
                                                                    
                        uiLog.log("Loading search results");
                        
                        ref.loadSearchResults(app, entityType);
                        
                        final JFrame mainFrame = uiContext.getMainFrame();

                        mainFrame.pack();

                        uiLog.log("Displaying user interface");

                        mainFrame.setVisible(true);

                        ref.setInstalled(config, true);
                        
                        app.getConfigService().store();

                        logger.log(Level.INFO, "Was installed: {0}, now installed: {1}",
                                new Object[]{WAS_INSTALLED, ref.isInstalled(config)});
                        
                        uiLog.log(WAS_INSTALLED ? "App Launch Successful" : "Installation Successful");
                        
                        if(!WAS_INSTALLED) {
                            
                            uiLog.querySaveLogThenSave("installation");
                        }

                        Runtime.getRuntime().addShutdownHook(new Thread("App_ShutdownHook_Thread") {
                            @Override
                            public void run() {
                                try{
                                    if(!app.isShutdown()) {
                                        app.shutdown();
                                    }

                                    AppLauncher.this.onShutdown(app);
                                    
                                }catch(RuntimeException e) {
                                    logger.log(Level.WARNING, "Error running shut down hook: "+Thread.currentThread().getName(), e);
                                }
                            }
                        });

                        new Thread(this.getClass().getName()+"#onLaunchCompleted_WorkerThread"){
                            @Override
                            public void run() {
                                try{
                                                                                   
                                    AppLauncher.this.onLaunchCompleted(app);
                                    
                                }catch(RuntimeException e) {
                                    logger.log(Level.WARNING, "Exception executing method " + 
                                            this.getClass().getName()+"#onLaunchCompleted() in thread " +
                                            Thread.currentThread().getName(), e);
                                }
                            }
                        }.start();
                        
                    }catch(Exception e) {
                        
                        uiLog.log("Error");
                        uiLog.log(e);
                        
                        showErrorMessageAndExit(e);
                        
                    }finally{
                        
                        uiLog.hideAndDispose();
                        
                        busy.set(false);
                    }
                }
            });
            
            return app;
            
        }catch(Exception e) {
            
            showErrorMessageAndExit(e);
            
            return null;
        }
    }
    
    public <T> void loadSearchResults(A app, Class<T> entityType) {

        final SearchContext<T> searchContext = app.getSearchContext(entityType);

        final SearchResults<T> searchResults = searchContext.getSearchResults();

        final UIContext uiContext = app.getUIContext();
        
        final JFrame frame = uiContext.getMainFrame();
        
        uiContext.positionFullScreen(frame);

        if(frame instanceof MainFrame) {
            
            final SearchResultsPanel resultsPanel = ((MainFrame)frame).getSearchResultsPanel();
            
            resultsPanel.loadSearchResultsUI(uiContext, searchContext, 
                    searchResults, "AppMainFrame", 0, 1, true);
        }
    }
    
    public void showErrorMessageAndExit(Throwable t) {
        showErrorMessageAndExit(t, "Failed to start application", 0);
    }
    
    public void showErrorMessageAndExit(Throwable t, String description, int exitCode) {
        
        logger.log(Level.SEVERE, description, t);
        
        new PopupImpl(null, new SimpleErrorOptions()).showErrorMessage(t, description);

        System.exit(exitCode);
    }
}
