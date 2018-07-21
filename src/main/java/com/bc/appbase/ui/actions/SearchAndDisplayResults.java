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
import com.bc.appbase.ui.SearchResultsFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.ui.builder.model.ComponentWalker;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.jpa.search.SearchResults;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 31, 2017 11:57:53 PM
 */
public class SearchAndDisplayResults implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(SearchAndDisplayResults.class.getName());
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        logger.log(Level.FINE, "Params: {0}", params);
        
        final ParameterExtractor pe = app.getOrException(ParameterExtractor.class);

        final Component component = pe.getFirstValue(params, Component.class, null);

        final ComponentWalker cx = app.getOrException(ComponentWalker.class);

        final SearchResultsPanel resultsPanel = (SearchResultsPanel)cx
                .findFirst(component, (comp) -> comp instanceof SearchResultsPanel, true, null);
        logger.finer(() -> "Found SearchResultsPanel: " + resultsPanel);
        final SearchContext searchContext;
        if(resultsPanel == null) {
            final Class et = (Class)params.getOrDefault(ParamNames.ENTITY_TYPE, app.getDefaultEntityType());
            searchContext = app.getSearchContext(et);
        }else{
            searchContext = resultsPanel.getSearchContext();
        }
        logger.finer(() -> "SearchContext: " + searchContext);
        Objects.requireNonNull(searchContext);
        
        final JTextComponent textComp = (JTextComponent)cx
                .findFirstChild(component, (comp) -> comp instanceof JTextComponent, true, null);
        
        params = new HashMap(params);
        if(textComp != null) {
            params.put("query", textComp.getText());
        }
        params.put(SearchContext.class.getName(), searchContext);
        
        final SearchResults searchResults = (SearchResults)app
                .getAction(ActionCommands.SEARCH).execute(app, params);
        
        final SearchResultsFrame frame = new SearchResultsFrame();
        
        frame.init(app, "", false);

        frame.loadSearchResults(
                searchContext, searchResults, app.getRandomId(), true);

        frame.setVisible(true);
        
        return Boolean.TRUE;
    }
}
