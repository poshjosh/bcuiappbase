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
import com.bc.appbase.ui.FrameForTable;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.Font;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2017 9:35:02 PM
 */
public class ViewTableAsExcel implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Desktop desktop;
        final File file;
        
        if(!Desktop.isDesktopSupported()) {
            
            throw new TaskExecutionException("Operation not supported");
            
        }else{
            
            desktop = Desktop.getDesktop();
            
            if(desktop == null || !desktop.isSupported(Desktop.Action.OPEN)) {
                
                throw new TaskExecutionException("Operation not supported");
                
            }else{
                
//                final JTable table = (JTable)params.get(JTable.class.getName());
//                showTable(app, table);
                
                final TableModel tableModel = (TableModel)app.getAction(
                        ActionCommands.PROMPT_SELECT_ROWS_AS_TABLE_MODEL).execute(app, params);
                
                if(tableModel == null) {
                    file = null;
                }else{    
                    
                    final Map<String, Object> saveTableParams = this.getSaveTableParams(
                            app, tableModel, app.getUIContext().getFont(JTable.class));

                    file = (File)app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);
                }
            }
        }
        
        if(file != null) {
            try{
                desktop.open(file);
            }catch(IOException e) {
                throw new TaskExecutionException("Error opening file: "+file, e);
            }
        }
        
        return file;
    }
    
    public Map<String, Object> getSaveTableParams(App app, TableModel tableModel, Font font) {
        final Map<String, Object> saveTableParams = new HashMap<>();
        final String workingDir = app.getWorkingDir();
        final String filename = Long.toHexString(System.currentTimeMillis()) + "_temp.xls";
        saveTableParams.put(java.io.File.class.getName(), 
                Paths.get(workingDir, filename).toFile());
        saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
        saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
        saveTableParams.put(java.awt.Font.class.getName(), font);
        return saveTableParams;
    }
    
    public void showTable(App app, JTable table) {
        
        FrameForTable frame = new FrameForTable("Hmmm");
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.init(app.getUIContext(), table);

//        app.getUIContext().linkWindowToSearchResults(frame, searchResults, 
//                "SummaryReport_SearchResults_"+Long.toHexString(System.currentTimeMillis()));

        app.getUIContext().positionFullScreen(frame);

//        app.getUIContext().updateTableUI(table, resultModel.getEntityType(), -1);

        frame.pack();

        frame.setVisible(true);
    }
}
