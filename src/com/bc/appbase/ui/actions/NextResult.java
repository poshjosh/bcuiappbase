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


import com.bc.appcore.actions.Action;
import com.bc.jpa.search.SearchResults;
import com.bc.appbase.ui.SearchResultsPanel;
import java.awt.Container;
import java.util.Map;
import javax.swing.JTable;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 22, 2017 11:59:03 AM
 */
public class NextResult implements Action<App, JTable> {

    @Override
    public JTable execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {

        final JTable table = (JTable)params.get(JTable.class.getName());
        
        Container parent = table.getParent();
        while( ! (parent instanceof SearchResultsPanel) ) {
            parent = parent.getParent();
        }

        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table);
        
        final int nextPage = searchResults.getPageNumber() + 1;
        
        app.getUIContext().loadSearchResults((SearchResultsPanel)parent, app.getSearchContext(null), nextPage, 1);
        
        return table;
    }
}
