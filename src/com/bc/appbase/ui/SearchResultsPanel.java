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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import com.bc.appbase.App;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.table.TableColumnManager;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.jpa.LoadPageThread;
import com.bc.appcore.jpa.model.ResultModel;
import java.awt.Window;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

/**
 * @author Josh
 */
public class SearchResultsPanel extends javax.swing.JPanel {
    
    private transient static final Logger logger = Logger.getLogger(SearchResultsPanel.class.getName());

    private final TableColumnManager tableColumnManager;
    
    public SearchResultsPanel() {
        initComponents();
        this.tableColumnManager = new TableColumnManager(this.getSearchResultsTable(), true);
    }

    public void init(UIContext uiContext) {
        
        final JTable table = this.getSearchResultsTable();
        
        new TableFormat(uiContext).format(table);

        this.getNextPageButton().setActionCommand(ActionCommands.NEXT_RESULT);
        this.getPreviousPageButton().setActionCommand(ActionCommands.PREVIOUS_RESULT);
        this.getLastPageButton().setActionCommand(ActionCommands.LAST_RESULT);
        this.getFirstPageButton().setActionCommand(ActionCommands.FIRST_RESULT);
        
        uiContext.addActionListeners(table, this.getAddButton(),
                this.getNextPageButton(), this.getPreviousPageButton(),
                this.getLastPageButton(), this.getFirstPageButton());

        table.addMouseListener(uiContext.getMouseListener(this));
    }

    public void reset(App app, Class entityType) {
        
        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(this, null);
        
        if(searchResults != null) {
            this.reset(app, app.getSearchContext(entityType), searchResults);
        }
    }
    
    public void reset(App app, SearchContext searchContext, SearchResults searchResults) {
        
        Objects.requireNonNull(app);
        Objects.requireNonNull(searchContext);
        Objects.requireNonNull(searchResults);
        
        final String KEY = this.getTopLevelAncestor().getName();
        
        final SearchResults previous = (SearchResults)app.getUIContext().getLinkedSearchResults(KEY, null);
        
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
        
        this.loadSearchResultsUI(
                app.getUIContext(), searchContext, searchResults, KEY, 0, 1, true);
    }

    public <T> Boolean loadSearchResultsUI(
            UIContext uiContext, SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "#loadSearchResults(...) Page: {0}, ID: {1}", 
                    new Object[]{firstPage, ID});
        }
        
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            output = this.doLoadSearchResultsUI(uiContext, searchContext, searchResults, ID, firstPage, numberOfPages, true);
        }
        
        return output;
    }
    private <T> Boolean doLoadSearchResultsUI(
           UIContext uiContext, SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            
            this.loadSearchResultsPages(uiContext, searchContext, searchResults, firstPage, numberOfPages);
            
            final Window resultsWindow = (Window)this.getTopLevelAncestor();

            if(ID != null) {
                uiContext.linkWindowToSearchResults(resultsWindow, searchResults, ID);
            }
            
            output = Boolean.TRUE;
        }
        
        return output;
    }
    
    public void loadSearchResultsPages(UIContext uiContext, SearchContext searchContext,
            int firstPage, int numberOfPages) throws SearchResultsNotFoundException {

        final SearchResults searchResults = uiContext.getLinkedSearchResults(this);
        
        this.loadSearchResultsPages(uiContext, searchContext, searchResults, firstPage, numberOfPages);
    }
    
    private void loadSearchResultsPages(UIContext uiContext, SearchContext searchContext, 
            SearchResults searchResults, int firstPage, int numberOfPages) {
        
        final ResultModel resultModel = searchContext.getResultModel();

        final JTable table = this.getSearchResultsTable();
        
        if(firstPage == 0 && searchResults.getSize() == 0) {
            
            final TableModel tableModel = uiContext.getTableModel(searchResults, resultModel, firstPage, 0);
            
            table.setModel(tableModel);
            
        }else{
            
            if(firstPage < 0 || firstPage >= searchResults.getPageCount()) {
                return;
            }

            logger.log(Level.FINE, "Setting page number to: {0}", firstPage);
            searchResults.setPageNumber(firstPage);

            final TableModel tableModel = uiContext.getTableModel(searchResults, resultModel, firstPage, numberOfPages);

            table.setModel(tableModel);
            
            uiContext.updateTableUI(table, resultModel.getEntityType(), resultModel.getSerialColumnIndex());
            
            new LoadPageThread(searchResults, firstPage + numberOfPages).start();
        }

        final String paginationMessage = searchContext.getPaginationMessage(searchResults, numberOfPages, true, false);
        
        this.getPaginationLabel().setText(paginationMessage);
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
        previousPageButton = new javax.swing.JButton();
        nextPageButton = new javax.swing.JButton();
        paginationLabel = new javax.swing.JLabel();
        firstPageButton = new javax.swing.JButton();
        lastPageButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

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

        previousPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        previousPageButton.setText("<");

        nextPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        nextPageButton.setText(">");

        paginationLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        paginationLabel.setPreferredSize(new java.awt.Dimension(186, 31));

        firstPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        firstPageButton.setText("<<");

        lastPageButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lastPageButton.setText(">>");

        addButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        addButton.setText("Add");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firstPageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previousPageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(paginationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nextPageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastPageButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nextPageButton)
                            .addComponent(lastPageButton))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(previousPageButton)
                            .addComponent(firstPageButton)
                            .addComponent(addButton)))
                    .addComponent(paginationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton firstPageButton;
    private javax.swing.JButton lastPageButton;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JLabel paginationLabel;
    private javax.swing.JButton previousPageButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable searchResultsTable;
    // End of variables declaration//GEN-END:variables

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTable getSearchResultsTable() {
        return searchResultsTable;
    }

    public JButton getNextPageButton() {
        return nextPageButton;
    }

    public JLabel getPaginationLabel() {
        return paginationLabel;
    }

    public JButton getPreviousPageButton() {
        return previousPageButton;
    }

    public JButton getFirstPageButton() {
        return firstPageButton;
    }

    public JButton getLastPageButton() {
        return lastPageButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public TableColumnManager getTableColumnManager() {
        return tableColumnManager;
    }
}
