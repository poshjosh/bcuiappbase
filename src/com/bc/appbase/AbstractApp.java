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

import com.bc.appbase.parameter.SelectedRecordsParametersBuilder;
import com.bc.appcore.jpa.SearchContextImpl;
import com.bc.appcore.parameter.ParametersBuilder;
import com.bc.appbase.ui.UIContexBase;
import com.bc.appcore.jpa.model.ResultModel;
import java.util.Objects;
import java.util.logging.Logger;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.AbstractAppCore;
import com.bc.appcore.util.ExpirableCache;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.SlaveUpdates;
import java.util.Properties;
import com.bc.appcore.Filenames;
import com.bc.appcore.ObjectFactory;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:26:00 PM
 */
public abstract class AbstractApp extends AbstractAppCore implements App {
    
    private transient static final Logger logger = Logger.getLogger(AbstractApp.class.getName());
    
    private UIContext ui;

    public AbstractApp(
            Filenames filenames, ConfigService configService, 
            Config config, Properties settingsConfig, JpaContext jpaContext, 
            SlaveUpdates slaveUpdates, JpaSync jpaSync, ExpirableCache expirableCache) {
        super(filenames, configService, config, settingsConfig, 
                jpaContext, slaveUpdates, jpaSync, expirableCache);
    }
    
    @Override
    public void init() {
        super.init();
        this.ui = this.createUIContext();
        final JFrame frame = this.ui.getMainFrame();
        if(frame instanceof MainFrame) {
            ((MainFrame)frame).init(this);
        }
    }
    
    @Override
    protected ObjectFactory createObjectFactory() {
        return new ObjectFactoryBase(this);
    }
    
    protected UIContext createUIContext() {
        final MainFrame mainFrame = new MainFrame();
        return new UIContexBase(this, null, mainFrame);
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
        
        if(source instanceof SearchResultsPanel && 
                ActionCommands.DELETE_SELECTED_RECORDS.equals(actionCommand)) {    
            
            builder = new SelectedRecordsParametersBuilder();
            
        }else{
            
            builder = this.getOrException(ParametersBuilder.class);

            builder.context(this).with(source);
        }        

        return builder;
    }
}
