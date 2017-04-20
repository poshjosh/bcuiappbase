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


import com.bc.appbase.ui.SearchResultsPanel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.jpa.SearchContext;
import com.bc.jpa.search.SearchResults;
import java.awt.Container;
import java.util.Objects;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:44:57 AM
 */
public class RefreshResults implements Action<App, Object> {
    
    private transient static final Logger logger = Logger.getLogger(RefreshResults.class.getName());
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) {
        
        final SearchResultsPanel resultsPanel = (SearchResultsPanel)params.get(SearchResultsPanel.class.getName());
        Objects.requireNonNull(resultsPanel);
        final Class entityType = (Class)params.get(ParamNames.RESULT_TYPE);
                    
        this.execute(app, resultsPanel, entityType);
        
        return Boolean.TRUE;
    }
    
    public void execute(App app, SearchResultsPanel resultsPanel, Class entityType) {

        final SearchContext searchContext = app.getSearchContext(entityType);
        
        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(resultsPanel);
        
        this.execute(app, resultsPanel, searchContext, searchResults);
    }
    
    public void execute(App app, SearchResultsPanel resultsPanel, 
            SearchContext searchContext, SearchResults searchResults) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            doExecute(app, resultsPanel, searchContext, searchResults);
        }else{
            java.awt.EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    try{

                        doExecute(app, resultsPanel, searchContext, searchResults);

                    }catch(RuntimeException e) {

                        final String message = "An unexpected error occured";

                        logger.log(Level.WARNING, message, e);

                        app.getUIContext().showErrorMessage(e, message);
                    }
                }
            });
        }
    }
    
    public void doExecute(App app, SearchResultsPanel resultsPanel, 
            SearchContext searchContext, SearchResults searchResults) {
        
        final Container c = resultsPanel.getTopLevelAncestor();

        logger.log(Level.FINER, "Refreshing window named: {0}", c == null ? null : c.getName());
        
        resultsPanel.reset(app, searchContext, searchResults);
    }
}


