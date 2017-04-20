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

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.bc.appbase.App;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.Dimension;

/**
 * The help menu though created and set-up, is not added by default. Use 
 * {@link #addHelpMenu()} to add the help menu after adding all other menus 
 * to the menu bar.
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2017 11:43:38 AM
 */
public class MainFrame extends javax.swing.JFrame {

    private final javax.swing.JMenuItem aboutMenuItem;
    private final javax.swing.JMenuItem exitMenuItem;
    private final javax.swing.JMenu fileMenu;
    private final javax.swing.JMenu helpMenu;
    private final javax.swing.JMenuBar menuBar;
    private final javax.swing.JMenuItem printMenuItem;
    private final javax.swing.JMenuItem saveAsMenuItem;
    private final javax.swing.JPanel topPanel;
    private final javax.swing.JScrollPane topPanelScrollPane;
    private final javax.swing.JMenuItem viewTableAsExcelMenuItem;
    private final javax.swing.JMenu toolsMenu;
    private final javax.swing.JMenuItem settingsMenuItem;
    private final javax.swing.JMenuItem refreshMenuItem;
    
    private final com.bc.appbase.ui.SearchResultsPanel searchResultsPanel;
    private final Font menuFont;
    private final String aboutMenuItemActionCommand;
    
    public MainFrame() {
        this(null);
    }

    public MainFrame(App app) {
        this(app, new javax.swing.JPanel(), new java.awt.Font("Segoe UI", 0, 18), null);
    }
    
    public MainFrame(App app, javax.swing.JPanel topPanel, 
            Font menuFont, String aboutMenuItemActionCommand) {
        
        this.topPanel = Objects.requireNonNull(topPanel);
        this.menuFont = Objects.requireNonNull(menuFont);
        this.aboutMenuItemActionCommand = aboutMenuItemActionCommand;
        
        topPanelScrollPane = new javax.swing.JScrollPane();
        searchResultsPanel = new com.bc.appbase.ui.SearchResultsPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        toolsMenu = new javax.swing.JMenu(); 
        viewTableAsExcelMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem(); 
        refreshMenuItem = new javax.swing.JMenuItem();
        
        initComponents();
        
        if(app != null) {
            this.init(app);
        }
    }

    public void init(App app) {
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    app.getAction(ActionCommands.EXIT_UI_THEN_EXIT).execute(app, Collections.EMPTY_MAP);
                }catch(RuntimeException | ParameterException | TaskExecutionException exception) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected error", exception);
                    System.exit(0);
                }
            }
        });
        
        this.getAboutMenuItem().setActionCommand(this.aboutMenuItemActionCommand);
        if(this.aboutMenuItemActionCommand != null) {
            app.getUIContext().addActionListeners(this, this.getAboutMenuItem());
        }
        
        this.getExitMenuItem().setActionCommand(ActionCommands.EXIT_UI_THEN_EXIT);
        this.getSettingsMenuItem().setActionCommand(ActionCommands.DISPLAY_SETTINGS_UI);
        this.getRefreshMenuItem().setActionCommand(ActionCommands.RELOAD_MAIN_RESULTS);
        app.getUIContext().addActionListeners(this, 
                this.getExitMenuItem(), this.getSettingsMenuItem(), this.getRefreshMenuItem());
        
        this.getViewTableAsExcelMenuItem().setActionCommand(ActionCommands.VIEW_TABLE_AS_EXCEL);
        this.getSaveAsMenuItem().setActionCommand(ActionCommands.SAVE_TABLE_AS);
        this.getPrintMenuItem().setActionCommand(ActionCommands.PRINT);
        app.getUIContext().addActionListeners(this.getSearchResultsPanel().getSearchResultsTable(), 
                this.getViewTableAsExcelMenuItem(),
                this.getSaveAsMenuItem(), 
                this.getPrintMenuItem());

        this.init(app, this.topPanel);
        
        this.getSearchResultsPanel().init(app);
        
        if(app.getUIContext().getImageIcon() != null) {
            this.setIconImage(app.getUIContext().getImageIcon().getImage());
        }
    }
    
    public void init(App app, JPanel topPanel) { }

    public void reset(App app) {
        this.reset(app, this.topPanel);
        this.reset(app, this.searchResultsPanel);
    }
    
    public void reset(App app, JPanel topPanel) { }
    
    public void reset(App app, SearchResultsPanel resultsPanel) { 
        
        resultsPanel.reset(app, null);
    }

    @SuppressWarnings("unchecked")
    public void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        
        final Dimension preferredSize = topPanel.getPreferredSize();
        final int width = preferredSize.width < 1 ? 750 : preferredSize.width;
        final int height = preferredSize.width < 1 ? 250 : preferredSize.height;
        
        setPreferredSize(new java.awt.Dimension(width, 750));

        topPanelScrollPane.setViewportView(topPanel);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setFont(this.menuFont);

        viewTableAsExcelMenuItem.setFont(this.menuFont);
        viewTableAsExcelMenuItem.setText("View Excel");
        fileMenu.add(viewTableAsExcelMenuItem);

        saveAsMenuItem.setFont(this.menuFont);
        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        refreshMenuItem.setFont(menuFont);
        refreshMenuItem.setText("Refresh");
        fileMenu.add(refreshMenuItem);
        
        printMenuItem.setFont(this.menuFont);
        printMenuItem.setText("Print");
        fileMenu.add(printMenuItem);

        exitMenuItem.setFont(this.menuFont);
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        toolsMenu.setMnemonic('t');
        toolsMenu.setText("Tools");
        toolsMenu.setFont(this.menuFont);

        settingsMenuItem.setFont(this.menuFont);
        settingsMenuItem.setMnemonic('s');
        settingsMenuItem.setText("Settings");
        toolsMenu.add(settingsMenuItem);
        
        menuBar.add(toolsMenu); 
        
        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");
        helpMenu.setFont(this.menuFont);

        aboutMenuItem.setFont(this.menuFont);
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

//        menuBar.add(helpMenu); use addHelpMenu() to add the helpMenu after adding all others

        setJMenuBar(menuBar); 

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanelScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, width, Short.MAX_VALUE)
            .addComponent(searchResultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanelScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, height, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchResultsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    public void addHelpMenu() {
        boolean helpMenuAdded = false;
        for(int i=0; i<menuBar.getMenuCount(); i++) {
            helpMenuAdded = menuBar.getMenu(i).equals(helpMenu);
            if(helpMenuAdded) {
                break;
            }
        }
        if(!helpMenuAdded) {
            menuBar.add(helpMenu);
        }
    }

    public JMenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getHelpMenu() {
        return helpMenu;
    }

//    public JMenuBar getMenuBar() {
//        return menuBar;
//    }

    public JMenuItem getRefreshMenuItem() {
        return refreshMenuItem;
    }

    public JMenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    public SearchResultsPanel getSearchResultsPanel() {
        return searchResultsPanel;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public JScrollPane getTopPanelScrollPane() {
        return topPanelScrollPane;
    }

    public JMenuItem getPrintMenuItem() {
        return printMenuItem;
    }

    public JMenuItem getViewTableAsExcelMenuItem() {
        return viewTableAsExcelMenuItem;
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public JMenuItem getSettingsMenuItem() {
        return settingsMenuItem;
    }

    public Font getMenuFont() {
        return menuFont;
    }
}
