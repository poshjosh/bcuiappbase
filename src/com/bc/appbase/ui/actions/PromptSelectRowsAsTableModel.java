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
import com.bc.appbase.ui.dialog.DialogManager;
import com.bc.appcore.table.model.SearchResultsTableModel;
import com.bc.appcore.table.model.TableModelFromView;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.search.SearchResults;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 22, 2017 3:41:43 PM
 */
public class PromptSelectRowsAsTableModel implements Action<App, TableModel>{

    @Override
    public TableModel execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final JTable table = (JTable)params.get(javax.swing.JTable.class.getName());
        Objects.requireNonNull(table);
        
        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table, null);

        final TableModel tableModel;
        
        if(searchResults == null || searchResults.getPageCount() < 2) {

//            tableModel = table.getModel();
            tableModel = new TableModelFromView(table);

        }else{

            final DialogManager dialogManager = app.getOrException(DialogManager.class);

            final DialogManager.PageSelection pageSelection = 
                    dialogManager.promptSelectPages(
                            "Which page(s) do you want?",
                            "Select Page(s)", JOptionPane.QUESTION_MESSAGE);

            if(pageSelection == null) {

                app.getUIContext().showErrorMessage(null, "You did not make any selection");
                tableModel = null;

            }else{

                try{
                    
                    ResultModel resultModel = (ResultModel)params.get(ResultModel.class.getName());
                    if(resultModel == null) {
                        final Class resultType = (Class)params.get(ParamNames.ENTITY_TYPE);
                        resultModel = app.getResultModel(resultType, null);
                    }
                    
                    switch(pageSelection) {
                        
                        case CurrentPage: 
//                            tableModel = table.getModel(); 
                            tableModel = new TableModelFromView(table);
                            break;
                            
                        case AllPages: 
                            tableModel = new SearchResultsTableModel(
                                    app.getUIContext().getLinkedSearchResults(table), 
                                    resultModel); 
                            break;
                            
                        case FirstPage:
                            tableModel = new SearchResultsTableModel(
                                    app.getUIContext().getLinkedSearchResults(table), 
                                    resultModel, 0, 1); 
                            break;
                            
                        default:
                            throw new IllegalArgumentException("Unexpected "+DialogManager.PageSelection.class.getName()+", found: "+pageSelection+", expected any of: " + Arrays.toString(DialogManager.PageSelection.values()));
                    }
                    
                }catch(SearchResultsNotFoundException e) {
                    throw new TaskExecutionException(e);
                }
            }
        }
        
        return tableModel;
    }
}
