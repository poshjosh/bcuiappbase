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

import com.bc.ui.date.DateFromUIBuilder;
import com.bc.ui.date.DateUIUpdater;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.appbase.ui.table.cell.DefaultTableCellComponentModel;
import com.bc.appbase.ui.dialog.DialogManager;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.jpa.search.SearchResults;
import com.bc.ui.table.cell.ColumnWidths;
import com.bc.ui.table.cell.TableCellUIUpdater;
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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.JFrame;
import java.awt.Component;
import javax.swing.JComponent;
import com.bc.appbase.App;
import com.bc.appbase.ui.actions.ActionListenerImpl;
import com.bc.appbase.ui.table.cell.DefaultTableCellDisplayFormat;
import com.bc.appbase.ui.table.cell.DefaultTableCellUIFactory;
import com.bc.appbase.ui.table.cell.DefaultTableCellUIState;
import com.bc.appcore.actions.Action;
import com.bc.selection.SelectionContext;
import com.bc.appbase.parameter.ParametersBuilder;
import com.bc.ui.table.cell.ColumnWidthsImpl;
import com.bc.ui.table.cell.TableCellSize;
import com.bc.ui.table.cell.TableCellSizeImpl;
import com.bc.ui.table.cell.TableCellUIFactory;
import com.bc.ui.table.cell.TableCellUIState;
import com.bc.ui.table.cell.TableCellUIUpdaterImpl;
import java.awt.Rectangle;
import java.util.Map;
import javax.swing.JScrollPane;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import com.bc.ui.table.cell.TableCellSizeManager;
import com.bc.ui.table.cell.TableCellSizeManagerImpl;
import java.util.concurrent.Callable;
import javax.swing.JProgressBar;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 4:51:32 PM
 */
public class UIContextBase implements UIContext {
    
    private transient final Logger logger = Logger.getLogger(UIContextBase.class.getName());

    private final App app;
    
    private final JFrame mainFrame;
    
    private final ImageIcon imageIcon;
    
    private final JFrame pbarFrame;
    private final ProgressbarPanel pbarPanel;
    
    public UIContextBase(App app, ImageIcon imageIcon, JFrame mainFrame) {
        this.app = Objects.requireNonNull(app);
        this.mainFrame = Objects.requireNonNull(mainFrame);
        this.imageIcon = imageIcon;

        this.pbarPanel = new ProgressbarPanel(); 
        
        this.pbarFrame = new JFrame();
        this.pbarFrame.setSize(new Dimension(512, 32));
        this.pbarFrame.setPreferredSize(this.pbarPanel.getPreferredSize());
        final boolean setAlwaysOnTopIsOk = false;
        this.pbarFrame.setAlwaysOnTop(setAlwaysOnTopIsOk);
        this.pbarFrame.setUndecorated(true);
        this.pbarFrame.setType(Window.Type.UTILITY);
        this.pbarFrame.getContentPane().add(pbarPanel);
    }
    
    @Override
    public void dispose() {
        if(this.mainFrame.isVisible()) {
            this.mainFrame.setVisible(false);
        }
        this.mainFrame.dispose();
    }

    @Override
    public void addProgressBarPercent(int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(""+val+'%', 0, val, 100);
    }
    
    @Override
    public void addProgressBarPercent(String msg, int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(msg, 0, val, 100);
    }
    
    @Override
    public void showProgressBarPercent(int val) {
        this.showProgressBar(""+val+'%', 0, val, 100);
    }

    @Override
    public void showProgressBarPercent(String msg, int val) {
        this.showProgressBar(msg, 0, val, 100);
    }
    
    @Override
    public void showProgressBar(String msg, int min, int val, int max) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            
            showProgress(msg, min, val, max);
            
        }else{
            
            java.awt.EventQueue.invokeLater(() -> {
                showProgress(msg, min, val, max);
            });
        }
    }

    public void showProgress(String msg, int min, int val, int max) {    
        
        final JProgressBar pbar = pbarPanel.getProgressBar();
        
        if(val >= max) {
            if(pbarFrame.isVisible()) {
                pbarFrame.setVisible(false);
            }
        }else{
            
            final boolean indeterminate =  val < min;
            if(pbar.isIndeterminate() != indeterminate) {
                pbar.setIndeterminate(indeterminate);
            }
            
            if(!pbarFrame.isVisible()) {
                
                pbar.setStringPainted(msg != null);
                
                this.positionCenterScreen(pbarFrame);
                
                pbarFrame.setVisible(true);
            }
        }
        if(msg != null) {
            pbar.setString(msg);
        }
        if(min != pbar.getMinimum()) {
            pbar.setMinimum(min);
        }
        if(val != pbar.getValue()) {
            pbar.setValue(val);
        }
        if(max != pbar.getMaximum()) {
            pbar.setMaximum(max);
        }
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
    public Font getFont(Class<? extends Component> componentType) {
        return this.getFont(componentType, this.getFont());
    }
    
    @Override
    public Font getFont(Class<? extends Component> componentType, Font outputIfNone) {
        final Font font;
        final String fontPropKey = componentType.getName()+".font";
        final String fontPropVal = app.getConfig().getString(fontPropKey);
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} = {1}", new Object[]{fontPropKey, fontPropVal});
        }
     
        if(fontPropVal == null || fontPropVal.isEmpty()) {
            font = outputIfNone;
        }else{
            font = Font.decode(fontPropVal);
        }
        
        return font;
    }

    @Override
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    @Override
    public UIDisplayHandler getDisplayHandler() {
        return new UIDisplayHandlerImpl(this);
    }
    
    @Override
    public void setTableFont(JTable table) {
        final Font font = this.getFont(JTable.class);
        table.setFont(font);
        table.getTableHeader().setFont(font.deriveFont(Font.BOLD));
    }
    
    @Override
    public void scrollTo(JTable table, int start, int end) {
        table.setRowSelectionInterval(start, end);
        final Rectangle rect = table.getCellRect(start, 0, true);
        final JScrollPane scrolls = this.getScrolls(table, null);
        if(scrolls != null) {
            scrolls.scrollRectToVisible(rect);
        }
        table.scrollRectToVisible(rect); 
    }
    
    public JScrollPane getScrolls(JTable table, JScrollPane outputIfNone) {
        JScrollPane output = null;
        Container parent = table.getParent(); 
        while(true) {
            if(parent instanceof JScrollPane) {
                output = (JScrollPane)parent;
                break;
            }
            if(parent == null) {
                break;
            }
            parent = parent.getParent();
        }
        return output == null ? outputIfNone : output;
    }

    @Override
    public TableCellUIFactory getTableCellUIFactory(
            TableModel tableModel, Class entityType, int serialColumnIndex) {
        
        final TableCellUIState cellUIState = new DefaultTableCellUIState();
        
        final TableCellDisplayFormat cellDisplayFormat = 
                this.getTableCellDisplayFormat(serialColumnIndex);
        
        final TableCellSize cellSize = new TableCellSizeImpl(cellDisplayFormat, 0, Integer.MAX_VALUE); 
      
        final ColumnWidths columnWidths = this.getColumnWidths();
        
        final TableCellSizeManager cellSizeManager = new TableCellSizeManagerImpl(columnWidths);
        
        final ComponentModel componentModel = new DefaultTableCellComponentModel(
                app.getOrException(SelectionContext.class),
                app.getOrException(DateFromUIBuilder.class), app.getOrException(DateUIUpdater.class),
                ComponentModel.ComponentProperties.DEFAULT
        );
//        final ComponentModel componentModel = new DefaultTableCellComponentModel(app);
        
        final TableCellUIFactory cellUIFactory = new DefaultTableCellUIFactory(
                cellUIState, cellSize, cellDisplayFormat, cellSizeManager, 
                componentModel, entityType, tableModel
        );
        
        return cellUIFactory;
    }
    
    @Override
    public TableCellDisplayFormat getTableCellDisplayFormat(int serialColumnIndex) {
        final TableCellDisplayFormat cellDisplayValue = new DefaultTableCellDisplayFormat(
                app.getOrException(SelectionContext.class),
                app.getDateFormat(), serialColumnIndex);
        return cellDisplayValue;
    }

    @Override
    public TableCellUIUpdater getTableCellUIUpdater() {
        
        final TableCellUIUpdater cellUIUpdater = new TableCellUIUpdaterImpl();
        
        return cellUIUpdater;
    }

    @Override
    public ColumnWidths getColumnWidths() {
        return new ColumnWidthsImpl();
    }

    @Override
    public MouseListener getMouseListener(Container container) {
        return new MouseAdapter() {};
    }
    
    @Override
    public void showErrorMessage(Throwable t, Object message) {
        app.getOrException(DialogManager.class).showErrorMessage(t, message);
    }

    @Override
    public void showSuccessMessage(Object message) {
        app.getOrException(DialogManager.class).showSuccessMessage(message);
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
        
        final Callable callable = (Callable) () -> {
//            try{
            final ParametersBuilder paramsBuilder =
                    app.getParametersBuilder(container, actionCommand);
            final Map<String, Object> params = paramsBuilder.context(app).with(container).build();
            final Action<App, ?> action = app.getAction(actionCommand);
            return action.execute(app, params);
//            }catch(Throwable e) {
//                logger.log(Level.WARNING, null, e);
//                return null;
//            }    
            
        };

        return getActionListener(callable, actionCommand, 500, true);
    }

    @Override
    public ActionListener getActionListener(Callable action, String actionCommand, boolean async) {
        
        return getActionListener(action, actionCommand, 500, async);
    }
    
    @Override
    public ActionListener getActionListener(
            Callable action, String actionCommand, int progressBarDelay, boolean async) {
        
        return new ActionListenerImpl(app, action, actionCommand, progressBarDelay, async);
    }
    
    @Override
    public boolean positionFullScreen(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height - 50);
            c.setLocation(0, 0);
            c.setSize(custom);
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
            c.setSize(custom); 
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
            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionCenterScreen(Component c) {
        try{
            final Dimension dim = c.getSize(); // c.getPreferredSize();
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int left = screenSize.width/2 - dim.width/2;
            final int top = screenSize.height/2 - dim.height/2;
            c.setLocation(left, top);
            return true;
        }catch(Exception ignored) {
            return false;
        }
    }

    public String getSearchResultsWindowKey(String key) {
        return key + ".searchResultsWindow";
    }
    
    public String getSearchResultsKey(String key) {
        return key + ".searchResults";
    }
    
    public String getKeyFromSearchResultsKey(String searchResultsKey) {
        final int n = searchResultsKey.indexOf(".searchResults");
        if(n  == -1) {
            throw new IllegalArgumentException(searchResultsKey+"#indexOf('.searchResults')");
        }
        return searchResultsKey.substring(0, n);
    }
    
    @Override
    public Container getLinkedComponent(SearchResults searchResults, Container outputIfNone) {
        final Map<String, Object> attributes = this.app.getAttributes();
        JComponent output = null; 
        for(String key : attributes.keySet()) {
            final Object val = attributes.get(key);
            if(searchResults.equals(val)) {
                final String windowKey = this.getSearchResultsWindowKey(this.getKeyFromSearchResultsKey(key));
                output = (JComponent)attributes.get(windowKey);
                break;
            }
        }
        return output == null ? outputIfNone : output;
    }

    @Override
    public SearchResults getLinkedSearchResults(JComponent component)  throws SearchResultsNotFoundException{
        final SearchResults searchResults = this.getLinkedSearchResults(component, null);
        if(searchResults == null) {
            throw new SearchResultsNotFoundException();
        }
        return searchResults;
    }

    @Override
    public SearchResults getLinkedSearchResults(JComponent component, SearchResults outputIfNone) {
        final Container topAncestor = component.getTopLevelAncestor();
        final String KEY = topAncestor == null ? component.getName() : topAncestor.getName();
        return this.getLinkedSearchResults(KEY, outputIfNone);
    }

    @Override
    public SearchResults getLinkedSearchResults(String KEY)  throws SearchResultsNotFoundException{
        final SearchResults searchResults = this.getLinkedSearchResults(KEY, null);
        if(searchResults == null) {
            throw new SearchResultsNotFoundException();
        }
        return searchResults;
    }

    @Override
    public SearchResults getLinkedSearchResults(String KEY, SearchResults outputIfNone) {
        final SearchResults searchResults = (SearchResults)app.getAttributes().get(this.getSearchResultsKey(KEY));
        if(searchResults == null) {
            return outputIfNone;
        }
        return searchResults;
    }
    
    @Override
    public void linkWindowToSearchResults(Window window, SearchResults searchResults, String KEY) {
        
        logger.log(Level.FINE, "#linkWindowToSearchResults(...) ID: {0}", KEY);
       
        final Map<String, Object> attrs = app.getAttributes();
        attrs.put(this.getSearchResultsKey(KEY), searchResults);
        attrs.put(this.getSearchResultsWindowKey(KEY), window);
       
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
                    logger.log(Level.WARNING, "Error closing dao", exception);
                }finally{
                    attrs.remove(UIContextBase.this.getSearchResultsKey(KEY));
                    attrs.remove(UIContextBase.this.getSearchResultsWindowKey(KEY));
                }
            }
        });
    }
    
    @Override
    public void updateTableUI(JTable table, Class entityType, int serialColumnIndex) {
        
        final TableCellUIUpdater tableUIUpdater = this.getTableCellUIUpdater();
        
        tableUIUpdater
                .cellUIFactory(this.getTableCellUIFactory(table.getModel(), entityType, serialColumnIndex))
                .update(table);
    }
    
    @Override
    public JFrame getMainFrame() {
        return mainFrame;
    }

    public App getApp() {
        return app;
    }
}
