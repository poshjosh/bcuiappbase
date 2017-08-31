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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appbase.ui.FileMenu;
import com.bc.appbase.ui.FrameForTable;
import com.bc.appbase.ui.SelectAxisTypesPanel;
import com.bc.appbase.ui.table.TableCellSelectionHandler;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.table.TableColumnFilter;
import com.bc.appbase.ui.table.TableColumnManager;
import com.bc.appbase.ui.table.TableRowFilter;
import com.bc.appbase.ui.table.XYCountTableCellSelectionHandler;
import com.bc.appbase.ui.table.XYCountTableEmptyColumnFilter;
import com.bc.appbase.ui.table.XYCountTableEmptyRowFilter;
import com.bc.appcore.table.model.XYCountTableModel;
import com.bc.appcore.table.model.XYCountTableModelBuilder;
import com.bc.appcore.table.model.XYCountTableModelBuilderImpl;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.exceptions.ObjectFactoryException;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.predicates.AcceptAll;
import com.bc.jpa.search.SearchResults;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2017 11:41:33 AM
 */
public class ViewSummaryReport implements Action<App, TableModel> {

    private static final Logger logger = Logger.getLogger(ViewSummaryReport.class.getName());

    @Override
    public TableModel execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        try{
            
            final UIContext uiContext = app.getUIContext();
            
            final ResultModel resultModel = this.getResultModel(app, params);
            
            final SearchResults searchResults = this.getSearchResults(app, params, resultModel.getEntityType());
            
            if(searchResults.getSize() < 2) {
                app.getUIContext().showErrorMessage(null, "Not enough data to display Summary Report");
                return null;
            }
            
            final SelectAxisTypesPanel panel = new SelectAxisTypesPanel(app);
            
            JOptionPane.showMessageDialog(app.getUIContext().getMainFrame(), panel, 
                    "Select X and Y Axis Types", JOptionPane.PLAIN_MESSAGE);

            final Class xEntityType = panel.getSelectedXAxisType();
            final Class yEntityType = panel.getSelectedYAxisType();

            if(xEntityType == null || yEntityType == null) {
                app.getUIContext().showErrorMessage(null, "You did not make any selections");
                return null;
            }
            
            final boolean useCache = true;

            final XYCountTableModelBuilder tableModelBuilder = new XYCountTableModelBuilderImpl();
            final XYCountTableModel tableModel = (XYCountTableModel)tableModelBuilder
                    .app(app)
                    .displayFormat(app.getUIContext().getTableModelDisplayFormat(-1))
                    .resultModel(resultModel)
                    .searchResults(searchResults)
                    .xEntityType(xEntityType)
                    .yEntityType(yEntityType)
                    .useCache(useCache)
                    .build();

            final JTable table = new JTable(tableModel);
            
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setCellSelectionEnabled(true);

            final TableCellSelectionHandler cellSelectionHandler = 
                    new XYCountTableCellSelectionHandler(app, tableModelBuilder.getMetaData());

            table.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    cellSelectionHandler.onCellSelected(table, e);
                }
            });

            final FrameForTable frame = new FrameForTable(yEntityType.getSimpleName() + " vs " + xEntityType.getSimpleName());
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            frame.init(uiContext, table);
            
            final FileMenu fileMenu = frame.getFileMenu();
            final JMenuItem collapseMenuItem = new JMenuItem();
            final String COLLAPSE = "Collpase";
            final String UNCOLLAPSE = "Un-collapse";
            collapseMenuItem.setText(COLLAPSE);
            collapseMenuItem.setFont(fileMenu.getMenuItemsFont());
            
            final Optional<TableColumnManager> optionalColMgr = frame.getTableColumnManager();
            final Consumer<Predicate<Integer>> columnFilter = optionalColMgr.isPresent() ?
                    new TableColumnFilter(optionalColMgr.get(), table.getColumnCount()) : new TableColumnFilter(table);
            
            final Consumer<Predicate<Integer>> rowFilter = new TableRowFilter(table);
            
            final Callable action = () -> {
                
                final boolean collapse = COLLAPSE.equals(collapseMenuItem.getText());
                
                final Predicate<Integer> rowTest = !collapse ? new AcceptAll() :
                        new XYCountTableEmptyRowFilter(tableModel);
                final Predicate<Integer> colTest = !collapse ? new AcceptAll() :
                        new XYCountTableEmptyColumnFilter(tableModel);
                
                try{
                    frame.setVisible(false);
                    columnFilter.accept(colTest);
                    rowFilter.accept(rowTest);
                    frame.pack();
                }finally{
                    collapseMenuItem.setText(collapse ? UNCOLLAPSE : COLLAPSE);
                    frame.setVisible(true);
                }
                
                return Boolean.TRUE;
            };
            
            final String ACTION_COMMAND = COLLAPSE + "_OR_" + UNCOLLAPSE;
            collapseMenuItem.setActionCommand(ACTION_COMMAND);
            collapseMenuItem.addActionListener(uiContext.getActionListener(action, ACTION_COMMAND, true));
            
            fileMenu.add(collapseMenuItem);
            
            final Container linkedComponent = uiContext.getLinkedComponent(searchResults, null);
            
            logger.log(Level.FINE, 
                    () -> "Entity type: "+resultModel.getEntityType()+
                    ", number of results: "+searchResults.getSize()+
                    ", linked component name: "+(linkedComponent==null?null:linkedComponent.getName())
            );
//            if(linkedComponent == null) {
//                uiContext.linkWindowToSearchResults(frame, searchResults, 
//                        "SummaryReport_SearchResults_"+Long.toHexString(System.currentTimeMillis()));
//            }

            uiContext.positionFullScreen(frame);

            final boolean veryTimeConsuming = true;
            if(!veryTimeConsuming) {
                uiContext.updateTableUI(table, resultModel.getEntityType(), -1);
            }    

            frame.pack();

            frame.setVisible(true);
            
            return tableModel;
            
        }catch(HeadlessException | ObjectFactoryException e) {
            throw new TaskExecutionException(e);
        }
    }
    
    public ResultModel getResultModel(App app, Map<String, Object> params) {
        final ResultModel output;
        final ResultModel param = (ResultModel)params.get(ParamNames.RESULT_MODEL);
        if(param != null) {
            output = param;
        }else{
            output = app.getResultModel((Class)app.getAttributes().get(ParamNames.ENTITY_TYPE), null);
        }
        Objects.requireNonNull(output);
        return output;
    }
    
    public SearchResults getSearchResults(App app, Map<String, Object> params, Class entityType) {
        final SearchResults output;
        final SearchResults param = (SearchResults)params.get(ParamNames.SEARCH_RESULTS);
        if(param != null) {
            output = param;
        }else{
            output = app.getSearchContext(entityType).getSearchResults();
        }
        Objects.requireNonNull(output);
        return output;
    }
}
/**
 * 
    public void cacheDataAndCloseSearchResults(boolean useCache, SearchResults searchResults, TableModel tableModel) {
        if(useCache) {
            try{
                for(int row=0; row<tableModel.getRowCount(); row++) {
                    for(int col=0; col<tableModel.getColumnCount(); col++) {
                        tableModel.getValueAt(row, col);
                    }
                }
            }finally{
                if(searchResults instanceof AutoCloseable) {
                    try{
                        ((AutoCloseable)searchResults).close();
                    }catch(Exception e) {
                        logger.log(Level.WARNING, "Error closing search results", e);
                    }
                }
            }
        }
    }
    
 * 
 */