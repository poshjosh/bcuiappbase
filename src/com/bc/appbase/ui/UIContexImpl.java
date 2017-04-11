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

import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.jpa.LoadPageThread;
import com.bc.appbase.ui.actions.ActionListenerImpl;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appbase.ui.model.SearchResultsTableModel;
import com.bc.jpa.search.SearchResults;
import com.bc.table.cellui.ColumnWidths;
import com.bc.table.cellui.TableCellUIUpdater;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.JFrame;
import java.awt.Component;
import javax.swing.JComponent;
import com.bc.appbase.App;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.table.DefaultTableCellUIFactory;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.table.cellui.TableCellDisplayValue;
import com.bc.table.cellui.TableCellDisplayValueImpl;
import com.bc.table.cellui.TableCellSize;
import com.bc.table.cellui.TableCellSizeImpl;
import com.bc.table.cellui.TableCellUIFactory;
import com.bc.table.cellui.TableCellUIState;
import com.bc.table.cellui.TableCellUIStateImpl;
import com.bc.table.cellui.TableCellUIUpdaterImpl;
import java.text.DateFormat;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 4:51:32 PM
 */
public class UIContexImpl implements UIContext {
    
    private final class CellDisplayValue extends TableCellDisplayValueImpl{
        private final int serialColumnIndex;
        public CellDisplayValue(ResultModel resultModel, DateFormat dateFormat) {
            super(dateFormat);
            this.serialColumnIndex = resultModel.getSerialColumnIndex();
        }
        @Override
        public Object toDisplayValue(JTable table, Component component, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(column == this.serialColumnIndex) {
                return value + ".";
            }else{
                return super.toDisplayValue(table, component, value, isSelected, hasFocus, row, column);
            }
        }
        @Override
        public Object fromDisplayValue(JTable table, Component component, Object displayValue, int row, int column) {
            if(column == this.serialColumnIndex && displayValue != null) {
                final String sval = displayValue.toString();
                displayValue = sval.substring(0, sval.length()-1);
            }
            return super.fromDisplayValue(table, component, displayValue, row, column);
        }
    };
    
    private transient final Logger logger = Logger.getLogger(UIContexImpl.class.getName());

    private final App app;
    
    private final JFrame mainFrame;
    
    private final ImageIcon imageIcon;
    
    public UIContexImpl(App app, ImageIcon imageIcon, JFrame mainFrame) {
        this.app = Objects.requireNonNull(app);
        this.mainFrame = Objects.requireNonNull(mainFrame);
        this.imageIcon = imageIcon;
    }
    
    @Override
    public void dispose() {
        if(this.mainFrame.isVisible()) {
            this.mainFrame.setVisible(false);
        }
        this.mainFrame.dispose();
    }

    @Override
    public Font getFont() {
        final String fontString = app.getConfig().getString(".font");
        if(fontString == null || fontString.isEmpty()) {
            throw new NullPointerException("Property: '.font' is NULL");
        }
        return Font.decode(fontString);
    }
    
    @Override
    public Font getFont(Object component) {
        return this.getFont(component, this.getFont());
    }
    
    @Override
    public Font getFont(Object component, Font outputIfNone) {
        final Class componentClass;
        if(component instanceof Class) {
            componentClass = (Class)component;
        }else{
            componentClass = component.getClass();
        }
        final Font font;
        String fontString = app.getConfig().getString(componentClass.getName()+".font");
        if(fontString == null || fontString.isEmpty()) {
            font = outputIfNone;
        }else{
            font = Font.decode(fontString);
        }
        return font;
    }

    @Override
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    @Override
    public TableCellUIFactory getTableCellUIFactory(ResultModel resultModel) {
        
        Objects.requireNonNull(resultModel);
        
        final TableCellUIState cellUIState = new TableCellUIStateImpl();
        
        final TableCellSize cellSize = new TableCellSizeImpl(36, 720); 
        
//        final TableCellDisplayValue cellDisplayValue = new TableCellDisplayValueImpl(app.getDateFormat());
        final TableCellDisplayValue cellDisplayValue = new CellDisplayValue(resultModel, app.getDateFormat());
        
        final int height = 40;
        final ComponentModel componentModel = new DtbTableCellComponentModel(
                app,
                app.get(SelectionContext.class),
                app.get(DateFromUIBuilder.class), app.get(DateUIUpdater.class),
                new Font(Font.MONOSPACED, Font.PLAIN, EntryPanel.deriveFontSize(height)), 
                -1, height
        );
        final TableCellUIFactory cellUIFactory = new DefaultTableCellUIFactory(
                app, cellUIState, cellSize, cellDisplayValue, componentModel, resultModel
        );
        
        return cellUIFactory;
    }

    @Override
    public TableCellUIUpdater getTableCellUIUpdater(ResultModel resultModel) {
        
        final TableCellUIUpdater cellUIUpdater = new TableCellUIUpdaterImpl();
        
        return cellUIUpdater;
    }

    @Override
    public ColumnWidths getColumnWidths(Class entityType, ColumnWidths outputIfNone) {
        final ColumnWidths columnWidths = this.getColumnWidths(app.getResultModel(entityType, null));
        return columnWidths == null ? outputIfNone : columnWidths;
    }
    
    @Override
    public ColumnWidths getColumnWidths(ResultModel resultModel) {
        return new ColumnWidthsImpl(resultModel);
    }

    @Override
    public TableModel getTableModel(SearchResults searchResults, 
            ResultModel resultModel, int firstPage, int numberOfPages) {
        return new SearchResultsTableModel(app, searchResults, resultModel, firstPage, numberOfPages);
    }

    @Override
    public MouseListener getMouseListener(Container container) {
        return new MouseAdapter() {};
    }
    
    @Override
    public void showErrorMessage(Throwable t, Object message) {
        app.get(DialogManager.class).showErrorMessage(t, message);
    }

    @Override
    public void showSuccessMessage(Object message) {
        app.get(DialogManager.class).showSuccessMessage(message);
    }

    @Override
    public void addActionListeners(Container container, AbstractButton... buttons) {
        for(AbstractButton button : buttons) {
            final String actionCommand = Objects.requireNonNull(button.getActionCommand());
            if(actionCommand.equals(button.getName())) {
                throw new UnsupportedOperationException("Action command not set for button with name: " + button.getName());
            }
            button.addActionListener(this.getActionListener(container, actionCommand));
        }
    }



    @Override
    public ActionListener getActionListener(Container container, String actionCommand) {
        return new ActionListenerImpl(app, container, actionCommand);
    }

    @Override
    public boolean positionFullScreen(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height - 50);
            c.setLocation(0, 0);
//            c.setSize(custom);
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }
    
    @Override
    public boolean positionHalfScreenLeft(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(0, 0);
//            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionHalfScreenRight(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(custom.width, 0);
//            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionCenterScreen(Component c) {
        try{
            final Dimension dim = c.getPreferredSize();
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int left = screenSize.width/2 - dim.width/2;
            final int top = screenSize.height/2 - dim.height/2;
            c.setLocation(left, top);
            return true;
        }catch(Exception ignored) {
            return false;
        }
    }

    @Override
    public <T> SearchResultsFrame createSearchResultsFrame(
            SearchContext searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages,
            String msg, boolean emptyResultsAllowed) {
        
        logger.log(Level.FINE, "#createSearchResultsFrame(...) Message: {0}", msg);
        
        final SearchResultsFrame resultsFrame;
        
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            resultsFrame = null;
        }else{
            resultsFrame = new SearchResultsFrame();
            if(this.getImageIcon() != null) {
                resultsFrame.setIconImage(this.getImageIcon().getImage());
            }
            final JMenuItem viewExcel = resultsFrame.getViewTableAsExcelMenuItem();
            viewExcel.setActionCommand(ActionCommands.VIEW_TABLE_AS_EXCEL);
            final JMenuItem saveAs = resultsFrame.getSaveAsMenuItem();
            saveAs.setActionCommand(ActionCommands.SAVE_TABLE_AS);
            final JMenuItem print = resultsFrame.getPrintMenuItem();
            print.setActionCommand(ActionCommands.PRINT);
            final JTable table = resultsFrame.getSearchResultsPanel().getSearchResultsTable();
            this.addActionListeners(table, viewExcel, saveAs, print);

            final SearchResultsPanel resultsPanel = resultsFrame.getSearchResultsPanel();

            resultsFrame.getSearchResultsLabel().setText(msg);

            resultsPanel.init(app);
            
            this.loadSearchResultsUI(
                    resultsPanel, searchContext, searchResults, ID, firstPage, numberOfPages, emptyResultsAllowed);
        }
        
        return resultsFrame;
    }
    
    @Override
    public <T> Boolean loadSearchResultsUI(
            SearchResultsPanel resultsPanel, SearchContext searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "#loadSearchResults(...) Page: {0}, ID: {1}", 
                    new Object[]{firstPage, ID});
        }
        
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            if(SwingUtilities.isEventDispatchThread()) {
                output = this.doLoadSearchResultsUI(resultsPanel, searchContext, searchResults, ID, firstPage, numberOfPages, true);
            }else{
                java.awt.EventQueue.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            doLoadSearchResultsUI(resultsPanel, searchContext, searchResults, ID, firstPage, numberOfPages, true);
                        }catch(RuntimeException e) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                        }
                    }
                });
                output = Boolean.TRUE;
            }
        }
        
        return output;
    }
    private <T> Boolean doLoadSearchResultsUI(
           SearchResultsPanel resultsPanel, SearchContext searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed) {
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            
            this.loadSearchResults(resultsPanel, searchContext, searchResults, firstPage, numberOfPages);
            
            final Window resultsWindow = (Window)resultsPanel.getTopLevelAncestor();

            if(ID != null) {
                this.linkWindowToSearchResults(resultsWindow, searchResults, ID);
            }
            
            output = Boolean.TRUE;
        }
        
        return output;
    }

    @Override
    public SearchResults getLinkedSearchResults(JComponent component) {
        final Container topAncestor = component.getTopLevelAncestor();
        final String key = topAncestor == null ? component.getName() : topAncestor.getName();
        final SearchResults searchResults = (SearchResults)app.getAttributes().get(key);
        if(searchResults == null) {
            throw new SearchResultsNotFoundException();
        }
        return searchResults;
    }

    @Override
    public void linkWindowToSearchResults(Window window, SearchResults searchResults, String KEY) {
        
        logger.log(Level.FINE, "#linkWindowToSearchResults(...) ID: {0}", KEY);
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.doLinkWindowToSearchResults(window, searchResults, KEY);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    doLinkWindowToSearchResults(window, searchResults, KEY);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    private void doLinkWindowToSearchResults(Window window, SearchResults searchResults, String KEY) {
        
        app.getAttributes().put(KEY, searchResults);
        
        window.setName(KEY);

        window.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                logger.log(Level.FINE, "Window closing: {0}. Closing linked search results and removing attribute", window.getName());
                try{
                    if(searchResults instanceof AutoCloseable) {
                        ((AutoCloseable)searchResults).close();
                    }
                }catch(Exception exception) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error closing dao", exception);
                }finally{
                    app.getAttributes().remove(KEY);
                }
            }
        });
    }

    @Override
    public void loadSearchResults(
            SearchResultsPanel resultsPanel, SearchContext searchContext,
            int firstPage, int numberOfPages) {
        if(SwingUtilities.isEventDispatchThread()) {
            this.doLoadSearchResults(resultsPanel, searchContext, firstPage, numberOfPages);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    this.doLoadSearchResults(resultsPanel, searchContext, firstPage, numberOfPages);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    private void doLoadSearchResults(
            SearchResultsPanel resultsPanel, SearchContext searchContext,
            int firstPage, int numberOfPages) {

        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(resultsPanel);

        this.loadSearchResults(resultsPanel, searchContext, searchResults, firstPage, numberOfPages);
    }
    
    private void loadSearchResults(
            SearchResultsPanel resultsPanel, SearchContext searchContext, 
            SearchResults searchResults, int firstPage, int numberOfPages) {
        
        final ResultModel resultModel = searchContext.getResultModel();

        final JTable table = resultsPanel.getSearchResultsTable();
        
        if(firstPage == 0 && searchResults.getSize() == 0) {
            
            final TableModel tableModel = this.getTableModel(searchResults, resultModel, firstPage, 0);

            table.setModel(tableModel);
            
        }else{
            
            if(firstPage < 0 || firstPage >= searchResults.getPageCount()) {
                return;
            }

            logger.log(Level.FINE, "Setting page number to: {0}", firstPage);
            searchResults.setPageNumber(firstPage);

            final TableModel tableModel = this.getTableModel(searchResults, resultModel, firstPage, numberOfPages);

            this.updateTableUI(table, tableModel, resultModel);

            new LoadPageThread(searchResults, firstPage + numberOfPages).start();
        }

        final String paginationMessage = searchContext.getPaginationMessage(searchResults, numberOfPages, true, false);
        resultsPanel.getPaginationLabel().setText(paginationMessage);
    }
    
    @Override
    public void updateTableUI(JTable table, TableModel tableModel, ResultModel resultModel) {
        
        table.setModel(tableModel);
        
        final TableCellUIUpdater tableUIUpdater = this.getTableCellUIUpdater(resultModel);

        tableUIUpdater
                .cellUIFactory(this.getTableCellUIFactory(resultModel))
                .columnWidths(this.getColumnWidths(resultModel))
                .table(table)
                .tableModel(tableModel)
                .update();
    }
    
    @Override
    public JFrame getMainFrame() {
        return mainFrame;
    }

    public App getApp() {
        return app;
    }
}
