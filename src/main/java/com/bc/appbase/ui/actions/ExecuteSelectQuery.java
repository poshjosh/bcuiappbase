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

import com.bc.jpa.search.SearchResults;
import com.bc.appbase.App;
import com.bc.appbase.ui.SearchResultsFrame;
import com.bc.appcore.jpa.SearchContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 1:40:15 PM
 */
public class ExecuteSelectQuery extends AbstractExecuteQuery {

    @Override
    public Integer execute(App app, String sql, Class resultType) {
        
        if(!sql.startsWith("SELECT") && !sql.startsWith("select")) {

            app.getUIContext().showErrorMessage(null, "Only SELECT queries are allowed for this request");

            return -1;
        }
            
        final String KEY = sql;
        
        final SearchResults searchResults = this.getSearchResults(app, KEY, sql, resultType);
        
        final StringBuilder msg = new StringBuilder();
        msg.append("<html>");
        msg.append(sql);

        final int SIZE = searchResults.getSize();
        final String RESULTS_STR = SIZE == 1 ? "result" : "results";
        msg.append("<br/><tt>").append(SIZE).append(' ').append(RESULTS_STR).append("</tt>");
        
        msg.append("</html>");
        
        createAndShowSearchResultsFrame(app, searchResults, resultType, KEY, msg);
        
        return SIZE;
    }
    
    private void createAndShowSearchResultsFrame(
            App app, SearchResults searchResults, Class resultType, String KEY, Object msg) {
        
        final SearchResultsFrame frame = new SearchResultsFrame();
        
        frame.init(app, msg==null?null:msg.toString(), false);
        
        final SearchContext searchContext = app.getSearchContext(resultType);
        
        frame.loadSearchResults(searchContext, searchResults, KEY, false);
        
        frame.setVisible(true);
    }
    
    private SearchResults getSearchResults(App app, String KEY, String sql, Class resultType) {
        
        SearchResults searchResults = app.getUIContext().getLinkedSearchResults(KEY, null);
        if(searchResults == null) {
            searchResults = app.getSearchContext(resultType).executeNativeQuery(sql);
        }
        
        return searchResults;
    }
}
