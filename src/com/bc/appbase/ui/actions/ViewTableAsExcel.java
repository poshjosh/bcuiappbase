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

import com.bc.appcore.actions.Action;
import com.bc.appbase.ui.DialogManager;
import com.bc.appbase.ui.model.PageSelectionTableModel;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.bc.appbase.App;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.search.SearchResults;
import java.awt.Font;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2017 9:35:02 PM
 */
public class ViewTableAsExcel implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Desktop desktop;
        final File file;
        final String errMsg;
        
        if(!Desktop.isDesktopSupported()) {
            
            desktop = null;
            file = null;
            errMsg = "Operation not supported";
            
        }else{
            
            desktop = Desktop.getDesktop();
            
            if(!desktop.isSupported(Desktop.Action.OPEN)) {
                
                file = null;
                errMsg = "Operation not supported";
                
            }else{
                
                final Class resultType = (Class)params.get(ParamNames.RESULT_TYPE);
                final JTable table = (JTable)params.get(JTable.class.getName());
                final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table);
                
                final TableModel tableModel;
                
                if(searchResults.getPageCount() <= 1) {
                    
                    errMsg = null;
                    tableModel = table.getModel();
                    
                }else{
                    
                    final DialogManager dialogManager = app.get(DialogManager.class);

                    final DialogManager.PageSelection pageSelection = 
                            dialogManager.promptSelectPages("Which page(s) do you want to view?");

                    if(pageSelection == null) {

                        errMsg = "You did not make any selection";
                        tableModel = null;

                    }else{

                        errMsg = null;
                        tableModel = new PageSelectionTableModel(app, table, 
                                app.getResultModel(resultType, null), pageSelection);
                    }
                }
                
                if(tableModel == null) {
                    file = null;
                }else{    
                    
                    final Map<String, Object> saveTableParams = this.getSaveTableParams(
                            app, tableModel, app.getUIContext().getFont(table));

                    file = (File)app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);
                }
            }
        }
        
        if(desktop != null && file != null) {
            try{
                desktop.open(file);
            }catch(IOException e) {
                throw new TaskExecutionException("Error opening file: "+file, e);
            }
        }else {
            if(errMsg != null) {
                app.getUIContext().showErrorMessage(null, errMsg);
            }
        }
        
        return file;
    }
    
    public Map<String, Object> getSaveTableParams(App app, TableModel tableModel, Font font) {
        final Map<String, Object> saveTableParams = new HashMap<>();
        final String workingDir = app.getWorkingDir().toString();
        final String filename = Long.toHexString(System.currentTimeMillis()) + "_temp.xls";
        saveTableParams.put(java.io.File.class.getName(), 
                Paths.get(workingDir, filename).toFile());
        saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
        saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
        saveTableParams.put(java.awt.Font.class.getName(), font);
        return saveTableParams;
    }
}
