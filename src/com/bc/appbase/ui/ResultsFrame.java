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

import com.bc.appcore.jpa.SearchContext;
import com.bc.jpa.search.SearchResults;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 29, 2017 3:55:32 PM
 */
public class ResultsFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(ResultsFrame.class.getName());

    private com.bc.appbase.ui.FileMenu fileMenu;
    private javax.swing.JMenuBar menuBar;
    private JLabel searchResultsLabel;
    private com.bc.appbase.ui.SearchResultsPanel searchResultsPanel;

    public ResultsFrame() throws HeadlessException {
        this.initComponents();
    }

    public ResultsFrame(GraphicsConfiguration gc) {
        super(gc);
        this.initComponents();
    }

    public ResultsFrame(String title) throws HeadlessException {
        super(title);
        this.initComponents();
    }

    public ResultsFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        this.initComponents();
    }

    private void initComponents() {

        searchResultsLabel = new JLabel();
        searchResultsPanel = new com.bc.appbase.ui.SearchResultsPanel();
        menuBar = new JMenuBar();
        fileMenu = new FileMenu();

        final Font font = new java.awt.Font("Tahoma", 0, 18); // NOI18N
        searchResultsLabel.setFont(font);
        searchResultsLabel.setText("You searched for: ");

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setFont(font);
        fileMenu.setMenuItemsFont(font);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(searchResultsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchResultsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    public void loadSearchResults(
            UIContext uiContext, SearchContext searchContext, SearchResults searchResults, String KEY, 
            Object msg, boolean emptyResultsAllowed, boolean fullScreen) throws HeadlessException {

        this.init(uiContext, msg.toString());
        
        this.pack();
            
        if(fullScreen) {
            uiContext.positionFullScreen(this);
        }else{
            uiContext.positionHalfScreenRight(this);
        }
        
        this.getSearchResultsPanel().loadSearchResultsUI(
                uiContext, searchContext, searchResults, KEY, 0, 1, emptyResultsAllowed);
    }
    
    public void init(UIContext uiContext, String message) {
        
        logger.log(Level.FINE, "#createSearchResultsFrame(...) Message: {0}", message);
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        if(uiContext.getImageIcon() != null) {
            this.setIconImage(uiContext.getImageIcon().getImage());
        }

        this.getSearchResultsLabel().setText(message);

        final SearchResultsPanel resultsPanel = this.getSearchResultsPanel();
        
        final JTable table = resultsPanel.getSearchResultsTable();
        this.getFileMenu().addActionListenerToDefaultMenuItems(uiContext, table);

        resultsPanel.init(uiContext);
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

//    public JMenuBar getMenuBar() {
//        return menuBar;
//    }

    public JLabel getSearchResultsLabel() {
        return searchResultsLabel;
    }

    public SearchResultsPanel getSearchResultsPanel() {
        return searchResultsPanel;
    }
}
