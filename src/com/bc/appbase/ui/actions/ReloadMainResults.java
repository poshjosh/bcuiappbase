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
import com.bc.appbase.App;
import com.bc.appbase.ui.MainFrame;
import com.bc.appcore.jpa.SearchContext;
import com.bc.jpa.search.SearchResults;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 10:38:08 AM
 */
public class ReloadMainResults extends RefreshResults {

    @Override
    public Boolean execute(App app, Map<String, Object> params) {
        
        final JFrame frame = app.getUIContext().getMainFrame();
        
        if(frame instanceof MainFrame) {
            
            final SearchResultsPanel resultsPanel = ((MainFrame)frame).getSearchResultsPanel();
            
            final Class entityType = null;

            final SearchContext searchContext = app.getSearchContext(entityType);

            final SearchResults searchResults = searchContext.getSearchResults();
            
            this.execute(app, resultsPanel, searchContext, searchResults);
            
            return Boolean.TRUE;
            
        }else{
            
            return Boolean.FALSE;
        }
    }
}
