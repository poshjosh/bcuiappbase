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

import com.bc.appbase.App;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.exceptions.TargetNotFoundException;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appbase.ui.UIDisplayHandler;
import com.bc.appbase.ui.VerticalLayout;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2017 5:01:43 PM
 */
public class DisplayRecordList implements Action<App, Container> {
    
//    private static final Logger logger = Logger.getLogger(DisplayRecordList.class.getName());
    
    @Override
    public Container execute(final App app, final Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Component component = app.getOrException(ParameterExtractor.class).getFirstValue(params, Component.class);
        
        final String name = component.getName();
        
        try{
            
            final Collection entityList = app.getExpirable(Collection.class, name);
        
            return this.buildAndDisplayCombinedUI(app, entityList); 
            
        }catch(TargetNotFoundException e) {
            throw new TaskExecutionException(e);
        }
    }

    public Container buildAndDisplayCombinedUI(final App app, Collection entityList) {
        
        final List<Component> components = new ArrayList();
        
        for(Object entity : entityList) {
            
            final Container ui = app.getOrException(UIBuilderFromEntity.class)
                    .sourceData(entity)
                    .build();

            components.add(ui);
        }
        
        final JPanel main = new JPanel();
        
        final VerticalLayout verticalLayout = new VerticalLayout();
        
        verticalLayout.addComponents(main, components);
        
        final UIDisplayHandler displayHandler = app.getUIContext().getDisplayHandler();
        
        displayHandler.displayUI(main, null, true, false);
        
        return main;
    }
    
}
