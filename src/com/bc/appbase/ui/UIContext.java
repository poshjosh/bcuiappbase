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

import com.bc.appcore.jpa.model.ResultModel;
import com.bc.jpa.search.SearchResults;
import com.bc.table.cellui.ColumnWidths;
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
import com.bc.appcore.jpa.SearchContext;
import com.bc.table.cellui.TableCellUIFactory;
import com.bc.table.cellui.TableCellUIUpdater;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 3:17:53 PM
 */
public interface UIContext { 
    
    void dispose();
    
    Font getFont();
    
    Font getFont(Object component);
    
    Font getFont(Object component, Font outputIfNone);
    
    ImageIcon getImageIcon();
    
    TableCellUIFactory getTableCellUIFactory(ResultModel resultModel);
    
    TableCellUIUpdater getTableCellUIUpdater(ResultModel resultModel);
    
    ColumnWidths getColumnWidths(Class entityType, ColumnWidths outputIfNone);
    
    ColumnWidths getColumnWidths(ResultModel resultModel);
    
    TableModel getTableModel(SearchResults searchResults, 
            ResultModel resultModel, int firstPage, int numberOfPages);
    
    MouseListener getMouseListener(Container container);
    
    void updateTableUI(JTable table, TableModel tableModel, ResultModel resultModel);
    
    void addActionListeners(Container container, AbstractButton... buttons);
    
    ActionListener getActionListener(Container container, String actionCommand);
    
    boolean positionFullScreen(Component component);
    
    boolean positionHalfScreenLeft(Component component);
    
    boolean positionHalfScreenRight(Component component);
    
    boolean positionCenterScreen(Component component);
    
    JFrame getMainFrame();
    
    <T> SearchResultsFrame createSearchResultsFrame(
            SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages,
            String msg, boolean emptyResultsAllowed);
    
    <T> Boolean loadSearchResultsUI(
            SearchResultsPanel resultsPanel, SearchContext<T> searchContext, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, boolean emptyResultsAllowed);
    
    void linkWindowToSearchResults(Window window, SearchResults searchResults, String key);
    
    SearchResults getLinkedSearchResults(JComponent component);
    
    SearchResults getLinkedSearchResults(JComponent component, SearchResults outputIfNone);
    
    SearchResults getLinkedSearchResults(String key);
    
    SearchResults getLinkedSearchResults(String key, SearchResults outputIfNone);
    
    void loadSearchResultsPages(SearchResultsPanel resultsPanel, SearchContext searchContext, int firstPage, int numberOfPages);    
    
    void showErrorMessage(Throwable t, Object message);
    
    void showSuccessMessage(Object message);
}
