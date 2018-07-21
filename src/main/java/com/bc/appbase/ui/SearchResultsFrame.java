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

import com.bc.appbase.App;
import com.bc.appcore.jpa.SearchContext;
import com.bc.jpa.search.SearchResults;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 29, 2017 3:55:32 PM
 */
public class SearchResultsFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(SearchResultsFrame.class.getName());

    private JMenuBar menuBar;
    private FileMenu fileMenu;
    
    private JPanel topPanel;
    private SearchPanel searchPanel;
    private JLabel searchResultsLabel;
    
    private SearchResultsPanel searchResultsPanel;

    public SearchResultsFrame() throws HeadlessException {
        this.initComponents();
    }

    public SearchResultsFrame(GraphicsConfiguration gc) {
        super(gc);
        this.initComponents();
    }

    public SearchResultsFrame(String title) throws HeadlessException {
        super(title);
        this.initComponents();
    }

    public SearchResultsFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        this.initComponents();
    }

    private void initComponents() {

        final Font font = new java.awt.Font("Tahoma", 0, 18); // NOI18N

        menuBar = new JMenuBar();
        
        fileMenu = new FileMenu();
        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setFont(font);
        fileMenu.setMenuItemsFont(font);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        
        searchPanel = new SearchPanel();
        
        searchResultsLabel = new JLabel();
        searchResultsLabel.setFont(font);
        searchResultsLabel.setText("You searched for: ");
        
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(searchPanel);
        topPanel.add(searchResultsLabel);
        
        searchResultsPanel = new SearchResultsPanel();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    public void loadSearchResults(
            SearchContext searchContext, SearchResults searchResults, 
            String KEY, boolean emptyResultsAllowed) throws HeadlessException {

        searchResultsPanel.load(
                searchContext, searchResults, KEY, 0, 1, emptyResultsAllowed);
    }
    
    public void init(App app, String message, boolean fullScreen) {
        
        logger.log(Level.FINE, "#createSearchResultsFrame(...) Message: {0}", message);
        
        final UIContext uiContext = app.getUIContext();
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        if(uiContext.getImageIcon() != null) {
            this.setIconImage(uiContext.getImageIcon().getImage());
        }
        
        searchPanel.init(uiContext);

        if(message != null) {
            searchResultsLabel.setText(message);
        }

        final JTable table = searchResultsPanel.getSearchResultsTable();
        this.getFileMenu().addActionListenerToDefaultMenuItems(uiContext, table);

        searchResultsPanel.init(app);

        this.pack();
            
        if(fullScreen) {
            uiContext.positionFullScreen(this);
        }else{
            uiContext.positionHalfScreenRight(this);
        }
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

    public JPanel getTopPanel() {
        return topPanel;
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }
}
