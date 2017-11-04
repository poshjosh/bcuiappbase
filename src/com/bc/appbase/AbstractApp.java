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
import java.util.logging.Logger;
import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.PromptException;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.UIContextBase;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.AbstractAppCore;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.jpa.model.EntityResultModel;
import com.bc.appcore.jpa.model.EntityResultModelImpl;
import javax.swing.JFrame;
import com.bc.appcore.parameter.ParametersBuilder;
import java.net.URL;
import java.util.Arrays;
import javax.swing.ImageIcon;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:26:00 PM
 */
public abstract class AbstractApp extends AbstractAppCore implements App {
    
    private transient static final Logger logger = Logger.getLogger(AbstractApp.class.getName());
    
    private UIContext uiContext;
    
    public AbstractApp(AppContext appContext) {
        super(appContext);
    }
    
    @Override
    public void init() {
        
        super.init();
        
        this.uiContext = this.createUIContext();
        final JFrame frame = this.uiContext.getMainFrame();
        if(frame instanceof MainFrame) {
            ((MainFrame)frame).init(this);
        }
    }
    
    @Override
    protected ObjectFactory createObjectFactory() {
        return new ObjectFactoryBase(this);
    }
    
    protected UIContext createUIContext() {
        final MainFrame mainFrame = this.createMainFrame();
        final URL iconURL = this.getIconURL();
        final ImageIcon imageIcon = iconURL == null ? null : new ImageIcon(
                iconURL, this.getImageIconDescription(iconURL));
        return this.createUIContext(this, imageIcon, mainFrame);
    }
    
    protected MainFrame createMainFrame() {
        return new MainFrame();
    }
    
    protected UIContext createUIContext(App app, ImageIcon imageIcon, JFrame mainFrame) {
        return new UIContextBase(this, imageIcon, mainFrame);
    }

    protected String getImageIconDescription(URL url) {
        return url == null ? "" : url.toExternalForm();
    }
    
    protected URL getIconURL() {
        return null;
    }
    
    @Override
    public UIContext getUIContext() {
        return uiContext;
    }

    @Override
    public EntityResultModel createResultModel(
            Class entityType, String[] columnNames) {
        
        if(entityType == null) {
            entityType = this.getDefaultEntityType();
        }
        
        return new EntityResultModelImpl(this, entityType, 
                Arrays.asList(columnNames), 
                (col, val) -> true, new PromptException(this.getUIContext()));
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
