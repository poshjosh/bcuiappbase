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
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.search.SearchResults;
import java.awt.Container;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.Cache;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:44:57 AM
 */
public class RefreshResults implements Action<App, Object> {
    
    private transient static final Logger logger = Logger.getLogger(RefreshResults.class.getName());
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        final ParameterExtractor paramExtractor =  app.getOrException(ParameterExtractor.class);
        final SearchResultsPanel resultsPanel = paramExtractor.getFirstValue(params, SearchResultsPanel.class);
        final Set toRefresh = paramExtractor.getFirstValue(params, Set.class, Collections.EMPTY_SET);
       
        this.execute(app, resultsPanel, toRefresh);
        
        return Boolean.TRUE;
    }
    
    public void execute(App app, SearchResultsPanel resultsPanel, Set updatesToRefresh) 
            throws TaskExecutionException {
        
        final SearchResults searchResults;
        try{
            searchResults = app.getUIContext().getLinkedSearchResults(resultsPanel);
        }catch(SearchResultsNotFoundException e) {
            throw new TaskExecutionException(e);
        }

        this.execute(app, resultsPanel, searchResults, updatesToRefresh);
    }    
        
    public void execute(App app, SearchResultsPanel resultsPanel, 
            SearchResults searchResults, Set updatesToRefresh) 
            throws TaskExecutionException {
        
        final Class resultType = resultsPanel.getSearchContext().getResultType();
        
        final Cache cache = app.getActivePersistenceUnitContext().getEntityManagerFactory().getCache();
        
        if(!updatesToRefresh.isEmpty()) {
        
            final EntityUpdater updater = app.getActivePersistenceUnitContext().getEntityUpdater(resultType);
            
            for(Object toRefresh : updatesToRefresh) {

                searchResults.load(toRefresh);

                final Object id = updater.getId(toRefresh);
                
                if(id != null) {
                    
                    cache.evict(toRefresh.getClass(), id);
                }
            }
        }else{
            
            cache.evict(resultType);
        }
        
        final Container c = resultsPanel.getTopLevelAncestor();

        logger.log(Level.FINER, "Refreshing window named: {0}", c == null ? null : c.getName());
        
        resultsPanel.reset(searchResults);
    }
}


