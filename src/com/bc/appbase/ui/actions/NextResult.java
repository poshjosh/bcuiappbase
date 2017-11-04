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
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 22, 2017 11:59:03 AM
 */
public class NextResult extends LoadSearchResultsPanel {

    private static final Logger logger = Logger.getLogger(NextResult.class.getName());

    public NextResult() {
        super((app, table) -> {
            try{
                final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table);
                return searchResults.getPageNumber() + 1;
            }catch(SearchResultsNotFoundException e) {
                logger.log(Level.WARNING, "Error computing offset. Using default of 0", e);
                return 0;
            }    
        });
    }
}
