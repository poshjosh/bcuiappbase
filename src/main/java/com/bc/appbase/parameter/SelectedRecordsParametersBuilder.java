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

package com.bc.appbase.parameter;

import com.bc.appbase.App;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.jpa.search.SearchResults;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import com.bc.appcore.AppCore;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.EntityMemberAccess;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 4:19:41 PM
 */
public class SelectedRecordsParametersBuilder implements ParametersBuilder<SearchResultsPanel> {
    
    private transient final Logger logger = Logger.getLogger(SelectedRecordsParametersBuilder.class.getName());

    private AppCore app;
    
    private SearchResultsPanel searchResultsPanel;
    
    @Override
    public ParametersBuilder<SearchResultsPanel> context(AppCore app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<SearchResultsPanel> with(SearchResultsPanel searchResultsPanel) {
        this.searchResultsPanel = searchResultsPanel;
        return this;
    }
    
    @Override
    public Map<String, Object> build() {
        
        final Map<String, Object> params;
        
        final JTable table = searchResultsPanel.getSearchResultsTable();

        final int [] selectedRowIndices = table.getSelectedRows();
      
        if(selectedRowIndices == null || selectedRowIndices.length == 0) {
            
            params = Collections.EMPTY_MAP;
            
        }else{
            
            params = new HashMap();
            
            final Integer [] selectedTaskids = new Integer[selectedRowIndices.length];
            
            Class entityType = null;
            
            EntityMemberAccess updater = null;
            
            String idColumnName = null;
            
            for(int i = 0; i < selectedRowIndices.length; i++) {
                
                final int rowIndex = selectedRowIndices[i];
                
                final Object entity = this.getEntity(rowIndex);
                
                if(entityType == null) {
                    entityType = entity.getClass();
                    final PersistenceUnitContext puContext = app.getActivePersistenceUnitContext();
                    updater = puContext.getEntityMemberAccess(entityType);
                    idColumnName = puContext.getMetaData().getIdColumnName(entityType);
                }
                
                final Integer id = (Integer)updater.getValue(entity, idColumnName);
                
                final String idCol = idColumnName;
                logger.fine(() -> "[" + rowIndex + ':' + idCol + "] = " + id);
                
                selectedTaskids[i] = id;
            }
            
            params.put(ParamNames.ENTITY_TYPE, entityType);
            
            params.put(idColumnName+"List", Arrays.asList(selectedTaskids));
            
            params.put(SearchResultsPanel.class.getName(), this.searchResultsPanel);
            
            return params;
        }
        
        return params;
    }
    
    private Object getEntity(int tableRowIndex) {
        final SearchResults searchResults;
        try{
            searchResults = ((App)app).getUIContext().getLinkedSearchResults(searchResultsPanel);
        }catch(SearchResultsNotFoundException e) {
            throw new RuntimeException(e);
        }
        final List currentpage = searchResults.getCurrentPage();
        if(tableRowIndex < currentpage.size()) {
            return currentpage.get(tableRowIndex);
        }else{
            final int resultsRowIndex = (searchResults.getPageNumber() * searchResults.getPageSize()) + tableRowIndex;
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Page: {0}, table row index: {1}, result row index: {2}", 
                        new Object[]{searchResults.getPageNumber(), tableRowIndex, resultsRowIndex});
            }
            return searchResults.get(resultsRowIndex);
        }
    }
}
