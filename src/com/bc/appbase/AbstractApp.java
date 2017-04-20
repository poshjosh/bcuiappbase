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

import com.bc.appcore.jpa.SearchContextImpl;
import com.bc.appcore.parameter.ParametersBuilder;
import com.bc.appbase.ui.UIContexImpl;
import com.bc.appcore.jpa.model.ResultModel;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JTable;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.AbstractAppCore;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.SlaveUpdates;
import java.awt.Container;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:26:00 PM
 */
public abstract class AbstractApp extends AbstractAppCore implements App {
    
    private transient static final Logger logger = Logger.getLogger(AbstractApp.class.getName());
    
    private UIContext ui;

    public AbstractApp(Path workingDir, ConfigService configService, Config config, Properties settingsConfig,
            JpaContext jpaContext, ExecutorService dataOutputService, SlaveUpdates slaveUpdates, JpaSync jpaSync) {
        super(workingDir, configService, config, settingsConfig, jpaContext, dataOutputService, slaveUpdates, jpaSync);
    }
    
    @Override
    public void init() {
        this.init(new com.bc.appbase.ObjectFactoryImpl(this));
        final MainFrame mainFrame = new MainFrame();
        this.init(new UIContexImpl(this, null, mainFrame));
        mainFrame.init(this);
    }
    
    protected void init(UIContext ui) {
        this.ui = Objects.requireNonNull(ui);
    }
    
    @Override
    public <T> SearchContext<T> getSearchContext(Class<T> entityType) {
        final ResultModel<T> resultModel = this.getResultModel(entityType, null);
        return new SearchContextImpl<>(this, Objects.requireNonNull(resultModel), 20, true);
    }

    @Override
    public UIContext getUIContext() {
        return ui;
    }

    @Override
    public <T> ParametersBuilder<T> getParametersBuilder(T source, String actionCommand) {
        
        final ParametersBuilder builder;
        if(source instanceof JTable && 
                ActionCommands.VIEW_TABLE_AS_EXCEL.equals(actionCommand) ||
                (ActionCommands.SAVE_TABLE_AS.equals(actionCommand) || ActionCommands.PRINT.equals(actionCommand) ||
                ActionCommands.NEXT_RESULT.equals(actionCommand) || ActionCommands.PREVIOUS_RESULT.equals(actionCommand)) ||
                ActionCommands.FIRST_RESULT.equals(actionCommand) || ActionCommands.LAST_RESULT.equals(actionCommand)) {    
            builder = this.get(ParametersBuilder.class);
        } else if(source instanceof Container && ActionCommands.UPDATE_SETTINGS_FROM_UI.equals(actionCommand)) {    
            builder = this.get(ParametersBuilder.class);
        }else {
            builder = ParametersBuilder.NO_OP;
        }
        
        builder.context(this).with(source);

        return builder;
    }
}
