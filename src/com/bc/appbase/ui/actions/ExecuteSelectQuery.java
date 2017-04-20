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
//import com.bc.appbase.pu.entities.Task;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.bc.appbase.App;

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
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.createAndShowSearchResultsFrame(app, searchResults, resultType, KEY, msg);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    createAndShowSearchResultsFrame(app, searchResults, resultType, KEY, msg);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
        
        return SIZE;
    }
    
    private void createAndShowSearchResultsFrame(
            App app, SearchResults searchResults, Class resultType, String KEY, Object msg) {
        JFrame frame = app.getUIContext().createSearchResultsFrame(
                app.getSearchContext(resultType), searchResults, KEY, 0, 1, msg.toString(), false);
        app.getUIContext().positionHalfScreenRight(frame);
        frame.pack();
        frame.setVisible(true);
    }
    
    private SearchResults getSearchResults(App app, String KEY, String sql, Class resultType) {
        
        SearchResults searchResults = app.getUIContext().getLinkedSearchResults(KEY, null);
        if(searchResults == null) {
            searchResults = app.getSearchContext(resultType).getSearchResults(sql);
        }
        
        return searchResults;
    }
}
