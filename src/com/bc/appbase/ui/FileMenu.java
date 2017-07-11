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

import com.bc.appbase.ui.actions.ActionCommands;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 20, 2017 5:15:57 PM
 */
public class FileMenu extends javax.swing.JMenu {
    
    private JMenuItem printMenuItem;
    private JMenuItem saveAsMenuItem;
    private JMenuItem viewTableAsExcelMenuItem;
    
    private Font menuItemsFont;

    public FileMenu() {
        this.initComponents();
    }

    public FileMenu(String s) {
        super(s);
        this.initComponents();
    }

    public FileMenu(Action a) {
        super(a);
        this.initComponents();
    }

    public FileMenu(String s, boolean b) {
        super(s, b);
        this.initComponents();
    }
    
    private void initComponents() {

        viewTableAsExcelMenuItem = new JMenuItem();
        saveAsMenuItem = new JMenuItem();
        printMenuItem = new JMenuItem();

        this.setMnemonic('f');
        this.setText("File");

        viewTableAsExcelMenuItem.setText("View Excel");
        this.add(viewTableAsExcelMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
//        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        this.add(saveAsMenuItem);

        printMenuItem.setText("Print");
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        this.add(printMenuItem);
    }
    
    public void setMenuItemsFont(Font font) {
        this.printMenuItem.setFont(font);
        this.saveAsMenuItem.setFont(font);
        this.viewTableAsExcelMenuItem.setFont(font);
        
        this.menuItemsFont = font;
    }

    public Font getMenuItemsFont() {
        return menuItemsFont;
    }
    
    public void addActionListenerToDefaultMenuItems(UIContext uiContext, JTable table) {
        viewTableAsExcelMenuItem.setActionCommand(ActionCommands.VIEW_TABLE_AS_EXCEL);
        saveAsMenuItem.setActionCommand(ActionCommands.SAVE_TABLE_AS);
        printMenuItem.setActionCommand(ActionCommands.PRINT);
        uiContext.addActionListeners(table, viewTableAsExcelMenuItem, saveAsMenuItem, printMenuItem);
    }
    
    public JMenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    public JMenuItem getPrintMenuItem() {
        return printMenuItem;
    }

    public JMenuItem getViewTableAsExcelMenuItem() {
        return viewTableAsExcelMenuItem;
    }
}
