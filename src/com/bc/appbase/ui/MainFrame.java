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
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.Dimension;
import javax.swing.AbstractButton;

/**
 * The help menu though created and set-up, is not added by default. Use 
 * {@link #addHelpMenu()} to add the help menu after adding all other menus 
 * to the menu bar.
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2017 11:43:38 AM
 */
public class MainFrame extends javax.swing.JFrame {

    private final javax.swing.JMenuItem aboutMenuItem;
    private final javax.swing.JMenuItem exitMenuItem;
    private final com.bc.appbase.ui.FileMenu fileMenu;
    private final javax.swing.JMenu helpMenu;
    private final javax.swing.JMenuBar menuBar;
    private final javax.swing.JPanel topPanel;
    private final javax.swing.JScrollPane topPanelScrollPane;
    private final javax.swing.JMenuItem viewSummaryReportMenuItem;
    
    private final javax.swing.JMenu toolsMenu;
    private final javax.swing.JMenuItem settingsMenuItem;
    private final javax.swing.JMenuItem databaseSettingsMenuItem;
    private final javax.swing.JMenuItem viewPendingUpdatesMenuItem;
    private final javax.swing.JMenuItem changeLogLevelMenuItem;
    private final javax.swing.JMenuItem viewLogMenuItem;
    private final javax.swing.JMenuItem refreshMenuItem;
    
    private final javax.swing.JMenu recordMenu;
    private final javax.swing.JMenuItem addNewRecordMenuItem;
    private final javax.swing.JMenuItem viewRecordMenuItem;
    
    private final javax.swing.JMenu userMenu;
    private final javax.swing.JMenuItem loginMenuItem;
    private final javax.swing.JMenuItem newUserMenuItem;
    
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
        
        fileMenu = new com.bc.appbase.ui.FileMenu();
        
        toolsMenu = new javax.swing.JMenu(); 
        settingsMenuItem = new javax.swing.JMenuItem(); 
        databaseSettingsMenuItem = new javax.swing.JMenuItem(); 
        viewPendingUpdatesMenuItem = new javax.swing.JMenuItem();  
        changeLogLevelMenuItem = new javax.swing.JMenuItem();  
        viewLogMenuItem = new javax.swing.JMenuItem();  
        refreshMenuItem = new javax.swing.JMenuItem();
        
        
        viewSummaryReportMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        
        recordMenu = new javax.swing.JMenu();
        addNewRecordMenuItem = new javax.swing.JMenuItem();        
        viewRecordMenuItem = new javax.swing.JMenuItem();
        
        userMenu = new javax.swing.JMenu();
        loginMenuItem = new javax.swing.JMenuItem();  
        newUserMenuItem = new javax.swing.JMenuItem();
        
        initComponents();
        
        this.addHelpMenu();
        
        if(app != null) {
            this.init(app);
        }
    }

    public void init(App app) {
        
        if(app.getAuthenticationSession() == null) {
            this.menuBar.remove(this.userMenu);
        }
        
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
        this.getDatabaseSettingsMenuItem().setActionCommand(ActionCommands.DISPLAY_DATABASE_OPTIONS);
        this.getViewPendingUpdatesMenuItem().setActionCommand(ActionCommands.DISPLAY_PENDING_MASTER_UPDATES);
        this.getChangeLogLevelMenuItem().setActionCommand(ActionCommands.CHANGE_LOG_LEVEL);
        this.getViewLogMenuItem().setActionCommand(ActionCommands.VIEW_LOG);
        this.getRefreshMenuItem().setActionCommand(ActionCommands.RELOAD_MAIN_RESULTS);
        this.getAddNewRecordMenuItem().setActionCommand(ActionCommands.DISPLAY_ADD_SELECTION_TYPE_UI);
        this.getViewRecordMenuItem().setActionCommand(ActionCommands.DISPLAY_SELECTION_TYPE_TABLE);
        this.getLoginMenuItem().setActionCommand(ActionCommands.LOGIN_OR_LOGOUT);
        this.getNewUserMenuItem().setActionCommand(ActionCommands.NEWUSER_VIA_USER_PROMPT);
        
        app.getUIContext().addActionListeners(this, 
                this.getExitMenuItem(), 
                this.getSettingsMenuItem(), this.getDatabaseSettingsMenuItem(), this.getViewPendingUpdatesMenuItem(),
                this.getChangeLogLevelMenuItem(), this.getViewLogMenuItem(),
                this.getRefreshMenuItem(), this.getAddNewRecordMenuItem(),
                this.getViewRecordMenuItem(),
                this.getLoginMenuItem(), this.getNewUserMenuItem());
        
        this.fileMenu.addActionListenerToDefaultMenuItems(app.getUIContext(),
                this.getSearchResultsPanel().getSearchResultsTable());
        
        this.getViewSummaryReportMenuItem().setActionCommand(ActionCommands.VIEW_SUMMARY_REPORT);
        
        app.getUIContext().addActionListeners(
                this.getSearchResultsPanel().getSearchResultsTable(), 
                this.getViewSummaryReportMenuItem());

        this.init(app, this.topPanel);
        
        this.getSearchResultsPanel().init(app.getUIContext());
        
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

        this.configure(fileMenu, "File", 'f');
        fileMenu.setMenuItemsFont(this.menuFont);
        fileMenu.add(this.configure(viewSummaryReportMenuItem, "View Summary Report"));
        fileMenu.add(this.configure(refreshMenuItem, "Refresh"));
        fileMenu.add(this.configure(exitMenuItem, "Exit", 'x'));
        menuBar.add(fileMenu);
        
        this.configure(this.userMenu, "User Profile");
        userMenu.add(this.configure(loginMenuItem, "Login"));
        userMenu.add(this.configure(newUserMenuItem, "New User"));
        menuBar.add(userMenu);
        
        this.configure(recordMenu, "Records");
        recordMenu.add(this.configure(addNewRecordMenuItem, "Add New Record"));
        recordMenu.add(this.configure(viewRecordMenuItem, "View Records"));
        menuBar.add(recordMenu);
        
        this.configure(toolsMenu, "Tools", 't');
        toolsMenu.add(this.configure(settingsMenuItem, "Settings"));
        toolsMenu.add(this.configure(databaseSettingsMenuItem, "Database Settings"));
        toolsMenu.add(this.configure(viewPendingUpdatesMenuItem, "View Pending Updates"));

        changeLogLevelMenuItem.setFont(this.menuFont);
        changeLogLevelMenuItem.setText("Change Log Level");
        toolsMenu.add(changeLogLevelMenuItem);
        
        viewLogMenuItem.setFont(this.menuFont);
        viewLogMenuItem.setText("View Log");
        toolsMenu.add(viewLogMenuItem);
        
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
    
    public AbstractButton configure(AbstractButton btn, String text) {
        return this.configure(btn, text, '\u0000');
    }
    
    public AbstractButton configure(AbstractButton btn, String text, char mnemonic) {
        if(mnemonic != '\u0000') {
            btn.setMnemonic(mnemonic);
        }
        btn.setText(text);
        btn.setFont(this.menuFont);
        return btn;
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

    public com.bc.appbase.ui.FileMenu getFileMenu() {
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
        return this.getFileMenu().getSaveAsMenuItem();
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

    public JMenu getUserMenu() {
        return userMenu;
    }

    public JMenuItem getLoginMenuItem() {
        return loginMenuItem;
    }

    public JMenuItem getNewUserMenuItem() {
        return newUserMenuItem;
    }

    public JMenuItem getPrintMenuItem() {
        return this.getFileMenu().getPrintMenuItem();
    }

    public JMenuItem getViewSummaryReportMenuItem() {
        return viewSummaryReportMenuItem;
    }

    public JMenuItem getViewTableAsExcelMenuItem() {
        return this.getFileMenu().getViewTableAsExcelMenuItem();
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public JMenuItem getDatabaseSettingsMenuItem() {
        return databaseSettingsMenuItem;
    }

    public JMenuItem getSettingsMenuItem() {
        return settingsMenuItem;
    }
    
    public JMenuItem getViewPendingUpdatesMenuItem() {
        return viewPendingUpdatesMenuItem;
    }

    public JMenuItem getChangeLogLevelMenuItem() {
        return changeLogLevelMenuItem;
    }

    public JMenuItem getViewLogMenuItem() {
        return viewLogMenuItem;
    }

    public JMenu getRecordMenu() {
        return recordMenu;
    }

    public JMenuItem getViewRecordMenuItem() {
        return viewRecordMenuItem;
    }

    public JMenuItem getAddNewRecordMenuItem() {
        return addNewRecordMenuItem;
    }

    public Font getMenuFont() {
        return menuFont;
    }
}
