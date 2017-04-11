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

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2017 9:35:02 PM
 */
public class ViewTableAsExcel implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final File file;
        
        if(!Desktop.isDesktopSupported()) {
            
            file = null;
            
        }else{
            
            final Desktop desktop = Desktop.getDesktop();
            
            if(!desktop.isSupported(Desktop.Action.OPEN)) {
                
                file = null;
                
            }else{
                
                final DialogManager dialogManager = app.get(DialogManager.class);
                
                final DialogManager.PageSelection pageSelection = 
                        dialogManager.promptSelectPages("Which page(s) do you want to view?");
                
                if(pageSelection == null) {
                    
                    file = null;
                    
                }else{
                    
                    final String workingDir = app.getWorkingDir().toString();
                    final String filename = Long.toHexString(System.currentTimeMillis()) + "_temp.xls";
                    final JTable table = (JTable)params.get(JTable.class.getName());
                    final TableModel tableModel = new PageSelectionTableModel(app, table, 
                            app.getSearchContext(null).getResultModel(), pageSelection);

                    final Map<String, Object> saveTableParams = new HashMap<>();
                    saveTableParams.put(java.io.File.class.getName(), 
                            Paths.get(workingDir, filename).toFile());
                    saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
                    saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
                    saveTableParams.put(java.awt.Font.class.getName(), app.getUIContext().getFont(table));

                    file = (File)app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);

                    if(file != null) {
                        try{
                            desktop.open(file);
                        }catch(IOException e) {
                            throw new com.bc.appcore.actions.TaskExecutionException(e);
                        }
                    }
                }
            }
        }
        
        return file;
    }
}
