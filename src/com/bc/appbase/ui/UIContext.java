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

import com.bc.appbase.ui.table.model.TableModelDisplayFormatImpl;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.table.model.TableModelDisplayFormat;
import com.bc.jpa.search.SearchResults;
import com.bc.ui.table.cell.ColumnWidths;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Component;
import javax.swing.JComponent;
import com.bc.ui.table.cell.TableCellUIFactory;
import com.bc.ui.table.cell.TableCellUIUpdater;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import java.util.concurrent.Callable;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 3:17:53 PM
 */
public interface UIContext {
    
    void dispose();
    
    void showProgressBarPercent(String msg, int val);
    
    void showProgressBar(String msg, int min, int val, int max);
    
    Font getFont();
    
    Font getFont(Class<? extends Component> componentType);
    
    Font getFont(Class<? extends Component> componentType, Font outputIfNone);
    
    ImageIcon getImageIcon();
    
    UIDisplayHandler getDisplayHandler();
    
    void setTableFont(JTable table);
        
    void scrollTo(JTable table, int start, int end);
    
    TableCellUIFactory getTableCellUIFactory(TableModel tableModel, Class entityType, int serialColumnIndex);
    
    default TableModelDisplayFormat getTableModelDisplayFormat(int serialColumnIndex) {
        return new TableModelDisplayFormatImpl(this.getTableCellDisplayFormat(serialColumnIndex));
    }
    
    TableCellDisplayFormat getTableCellDisplayFormat(int serialColumnIndex);
        
    TableCellUIUpdater getTableCellUIUpdater();
    
    ColumnWidths getColumnWidths();
    
    TableModel getTableModel(SearchResults searchResults, 
            ResultModel resultModel, int firstPage, int numberOfPages);
    
    MouseListener getMouseListener(Container container);
    
    void updateTableUI(JTable table, Class entityType, int serialColumnIndex);
    
    void addActionListeners(Container container, AbstractButton... buttons);
    
    ActionListener getActionListener(Container container, String actionCommand);
    
    ActionListener getActionListener(Callable action, String actionCommand, boolean async);
    
    ActionListener getActionListener(
            Callable action, String actionCommand, int progressBarDelay, boolean async);
    
    boolean positionFullScreen(Component component);
    
    boolean positionHalfScreenLeft(Component component);
    
    boolean positionHalfScreenRight(Component component);
    
    boolean positionCenterScreen(Component component);
    
    JFrame getMainFrame();
    
    void linkWindowToSearchResults(Window window, SearchResults searchResults, String key);
    
    Container getLinkedComponent(SearchResults searchResults, Container outputIfNone);
    
    SearchResults getLinkedSearchResults(JComponent component) throws SearchResultsNotFoundException;
    
    SearchResults getLinkedSearchResults(JComponent component, SearchResults outputIfNone);
    
    SearchResults getLinkedSearchResults(String key) throws SearchResultsNotFoundException;
    
    SearchResults getLinkedSearchResults(String key, SearchResults outputIfNone);
    
    void showErrorMessage(Throwable t, Object message);
    
    void showSuccessMessage(Object message);
}
