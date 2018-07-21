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
import com.bc.ui.builder.model.ComponentWalker;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.exceptions.TaskExecutionException;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 11:49:04 PM
 */
public class LoadSearchResultsPanel implements Action<App, JTable> {

    private static final Logger logger = Logger.getLogger(LoadSearchResultsPanel.class.getName());
    
    private final BiFunction<App, JTable, Integer> getOffset;

    public LoadSearchResultsPanel(BiFunction<App, JTable, Integer> getOffset) {
        this.getOffset = Objects.requireNonNull(getOffset);
    }

    @Override
    public JTable execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.exceptions.TaskExecutionException {

        final JTable table = (JTable)params.get(JTable.class.getName());

        logger.finer(() -> "Table: " + table);
        
        final ComponentWalker components = app.getOrException(ComponentWalker.class);
        
        final SearchResultsPanel resultsPanel = (SearchResultsPanel)
                components.findFirst(table, (comp) -> comp instanceof SearchResultsPanel, false, null);
        Objects.requireNonNull(resultsPanel);
        
        try{
            
            final int nextPage = getOffset.apply(app, table);
            
            app.getUIContext().getLinkedSearchResults(table);

            resultsPanel.loadNext(nextPage, 1);
            
        }catch(SearchResultsNotFoundException e) {
            throw new TaskExecutionException(e);
        }
        
        return table;
    }
}

