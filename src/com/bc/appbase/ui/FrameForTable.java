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

import com.bc.appbase.ui.table.TableColumnManager;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.Optional;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 20, 2017 5:31:50 PM
 */
public class FrameForTable extends javax.swing.JFrame {

    private FileMenu fileMenu;
    private JMenuBar menuBar;
    
    private boolean initializationAttempted;
    
    private TableColumnManager tableColumnManager;
    
    public FrameForTable() throws HeadlessException {
    }

    public FrameForTable(GraphicsConfiguration gc) {
        super(gc);
    }

    public FrameForTable(String title) throws HeadlessException {
        super(title);
    }

    public FrameForTable(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }
    
    public void init(UIContext uiContext, JTable table) {
        
        if(initializationAttempted) {
            throw new IllegalStateException("init method may only be called once");
        }
        
        this.initializationAttempted = true;

        this.tableColumnManager = new TableColumnManager(table, true);

        menuBar = new javax.swing.JMenuBar();
        fileMenu = new FileMenu();
        final Font font = uiContext.getFont(javax.swing.JMenu.class);
        fileMenu.setFont(font);
        fileMenu.setMenuItemsFont(font);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        
        this.getContentPane().add(new JScrollPane(table));
        
        fileMenu.addActionListenerToDefaultMenuItems(uiContext, table);
    }
    
    public FileMenu getFileMenu() {
        return fileMenu;
    }

//    public JMenuBar getMenuBar() {
//        return menuBar;
//    }

    public Optional<TableColumnManager> getTableColumnManager() {
        return Optional.ofNullable(this.tableColumnManager);
    }
}
