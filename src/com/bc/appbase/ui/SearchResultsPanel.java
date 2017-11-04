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

package com.bc.appbase.ui;

import com.bc.appbase.ui.table.TableFormat;
import com.bc.jpa.search.SearchResults;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.jpa.LoadPageThread;
import java.awt.Window;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;
import com.bc.appcore.jpa.model.EntityResultModel;
import com.bc.appcore.table.model.EntityTableModelImpl;
import com.bc.appcore.table.model.SearchResultsTableModel;
import java.util.Collections;

/**
 * @author Josh
 */
public class SearchResultsPanel extends javax.swing.JPanel {
    
    private transient static final Logger logger = Logger.getLogger(SearchResultsPanel.class.getName());
    
    private UIContext uiContext;
    
    private SearchContext searchContext;

    public SearchResultsPanel() {
        initComponents();
    }

    public void init(UIContext uiContext) {
        
        this.uiContext = uiContext;
        
        new TableFormat(uiContext).format(searchResultsTable);

        searchResultsPanelToolBar.init(uiContext, searchResultsTable);
    }

    public void reset() {
        
        final SearchResults searchResults = uiContext.getLinkedSearchResults(this, null);
        
        if(searchResults != null) {
            this.reset(searchResults);
        }
    }
    
    public void reset(SearchResults searchResults) {
        
        Objects.requireNonNull(searchContext);
        Objects.requireNonNull(searchResults);
        
        final String KEY = this.getTopLevelAncestor().getName();
        
        final SearchResults previous = (SearchResults)uiContext.getLinkedSearchResults(KEY, null);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Previous search results. Name: {0} ", previous);
        }
        
        if(previous instanceof AutoCloseable && !previous.equals(searchResults)) {
           
            logger.log(Level.FINE, "Closing previous search results: {0}", previous);
            
            try{
                ((AutoCloseable)previous).close();
            }catch(Exception e) {
                logger.log(Level.WARNING, "Error closing search results: "+previous, e);
            }
        }
        
        final int previousPageNumber = previous.getPageNumber();
        
        final int pageNumber;
        if(previousPageNumber <= searchResults.getPageCount()) {
            pageNumber = searchResults.getPageCount();
        }else{
            pageNumber = previousPageNumber;
        }
        
        this.load(searchContext, searchResults, KEY, pageNumber, 1, true);
    }

    public <T> Boolean load(SearchContext<T> searchContext, 
            SearchResults<T> searchResults, String ID) {
        
        return this.load(searchContext, searchResults, ID, 0, 1, true);
    }
    
    public <T> Boolean load(
            SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "#loadSearchResults(...) Page: {0}, ID: {1}", 
                    new Object[]{firstPage, ID});
        }
        
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            output = this.doLoad(searchContext, searchResults, ID, firstPage, numberOfPages, true);
        }
        
        return output;
    }
    private <T> Boolean doLoad(
           SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            
            this.load(searchContext, searchResults, firstPage, numberOfPages);
            
            final Window resultsWindow = (Window)this.getTopLevelAncestor();

            if(ID != null) {
                uiContext.linkWindowToSearchResults(resultsWindow, searchResults, ID);
            }
            
            output = Boolean.TRUE;
        }
        
        return output;
    }
    
    public void loadNext(int offset, int limit) throws SearchResultsNotFoundException {

        final SearchResults searchResults = uiContext.getLinkedSearchResults(this);
        
        this.load(searchContext, searchResults, offset, limit);
    }
    
    private void load(SearchContext searchContext, 
            SearchResults searchResults, int offset, int limit) {
        
        final EntityResultModel resultModel = searchContext.getResultModel();
        
        final TableModel tableModel = this.getTableModel(resultModel, searchResults, offset, limit);
        
        this.load(searchContext, tableModel);
        
        final int nextPage = offset + limit;

        if(nextPage < searchResults.getPageCount()) {

            new LoadPageThread(searchResults, offset + limit).start();
        }
        
        final String paginationMessage = searchContext.getPaginationMessage(searchResults, limit);
        
        this.searchResultsPanelToolBar.getPaginationLabel().setText(paginationMessage);
    }    
        
    public void load(SearchContext searchContext, TableModel tableModel) {
        
        final EntityResultModel resultModel = searchContext.getResultModel();
        
        this.searchResultsTable.setModel(tableModel);
        
        this.searchContext = searchContext;
        
        if(tableModel.getRowCount() > 0) {
            
            uiContext.updateTableUI(searchResultsTable, resultModel.getEntityType(), resultModel.getSerialColumnIndex());
        }
    }
    
    private TableModel getTableModel(EntityResultModel resultModel, 
            SearchResults searchResults, int offset, int limit) {
        
        final TableModel tableModel;
        
        if(offset == 0 && searchResults.getSize() == 0) {
            
            tableModel = new SearchResultsTableModel(searchResults, resultModel, offset, 0);
            
        }else{
            
            if(offset < 0 || offset >= searchResults.getPageCount()) {
                
                tableModel = new EntityTableModelImpl(Collections.EMPTY_LIST, resultModel);
                
            }else{

                logger.log(Level.FINE, "Setting page number to: {0}", offset);
                searchResults.setPageNumber(offset);

                tableModel = new SearchResultsTableModel(searchResults, resultModel, offset, limit);
            }
        }

        return tableModel;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        searchResultsTable = new javax.swing.JTable();
        searchResultsPanelToolBar = new com.bc.appbase.ui.SearchResultsPanelToolBar();

        setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        setPreferredSize(new java.awt.Dimension(570, 550));

        scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));

        searchResultsTable.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        searchResultsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
        searchResultsTable.setPreferredSize(new java.awt.Dimension(580, 520));
        scrollPane.setViewportView(searchResultsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchResultsPanelToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(searchResultsPanelToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private com.bc.appbase.ui.SearchResultsPanelToolBar searchResultsPanelToolBar;
    private javax.swing.JTable searchResultsTable;
    // End of variables declaration//GEN-END:variables

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTable getSearchResultsTable() {
        return searchResultsTable;
    }

    public SearchContext getSearchContext() {
        return searchContext;
    }

    public UIContext getUiContext() {
        return uiContext;
    }

    public SearchResultsPanelToolBar getSearchResultsPanelToolBar() {
        return searchResultsPanelToolBar;
    }
}
