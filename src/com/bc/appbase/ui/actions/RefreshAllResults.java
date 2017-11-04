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
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.components.ComponentWalker;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterExtractor;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 12, 2017 10:21:57 PM
 */
public class RefreshAllResults extends RefreshResults implements Action<App, Object> {
    
    private transient static final Logger logger = Logger.getLogger(RefreshAllResults.class.getName());
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) 
            throws TaskExecutionException {
        
        final List<SearchResultsPanel> uiList = new ArrayList(this.getSearchResultUIs(app));
        
        final Object oval = params.get(SearchResultsPanel.class.getName());
        
        if(oval != null) {
            uiList.add(0, (SearchResultsPanel)oval);
        }
        
        logger.log(Level.FINE, "Refreshing {0} search results UI", uiList.size());
        
        final Set toRefresh = app.getOrException(ParameterExtractor.class)
                .getFirstValue(params, Set.class, Collections.EMPTY_SET);

        for(SearchResultsPanel ui : uiList) {
            
            this.execute(app, ui, toRefresh);
        }
        
        return Boolean.TRUE;
    }
    
    public Set<SearchResultsPanel> getSearchResultUIs(App app) {
        
        final Set<SearchResultsPanel> output = new HashSet<>();
        
        final Map<String, Object> attrs = app.getAttributes();
        
        final ComponentWalker cx = app.getOrException(ComponentWalker.class);
        
        final Predicate<Component> test = new Predicate<Component>() {
            @Override
            public boolean test(Component c) {
                boolean success = false;
                if(c instanceof SearchResultsPanel) {
                    final SearchResultsPanel ui = (SearchResultsPanel)c;
                    if(app.getUIContext().getLinkedSearchResults(ui, null) != null) {
                        success = true;
                    }
                }
                return success;
            }
        };
        
        for(Map.Entry<String, Object> entry : attrs.entrySet()) {
            
            final Object key = entry.getKey();
            final Object val = entry.getValue();
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0} = {1}", new Object[]{key, val});
            }
            
            if(val instanceof Container) {
                
                final Container parent = ((Container) val); 
                
                final SearchResultsPanel child = (SearchResultsPanel)cx.findFirstChild(parent, test, true, null);
                
                if(child != null) {
                    output.add(child);
                }
            }
        }
        return output;
    }
}


