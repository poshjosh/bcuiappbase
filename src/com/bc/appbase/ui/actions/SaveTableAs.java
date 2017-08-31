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
import com.bc.appcore.util.FileUtil;
import com.bc.appbase.ui.dialog.DialogManager;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.bc.appbase.App;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 13, 2017 9:05:28 AM
 */
public class SaveTableAs implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "Folders";
            }
        };
        
        final DialogManager dialogManager = app.getOrException(DialogManager.class);
        
        File file = dialogManager.showDialog(
                JFileChooser.SAVE_DIALOG, "Specify Location to Save", 
                fileFilter, JFileChooser.DIRECTORIES_ONLY);
        
        if(file == null) {
            
            return null;
        }
        
        file = new File(FileUtil.convertToExtension(file.getPath(), "xls"));
        
        params = new HashMap(params);
        params.put(ResultModel.class.getName(), app.getSearchContext(null).getResultModel());
        
        final TableModel tableModel = (TableModel)app.getAction(
                ActionCommands.PROMPT_SELECT_ROWS_AS_TABLE_MODEL).execute(app, params);

        final Map<String, Object> saveTableParams = new HashMap<>();
        saveTableParams.put(java.io.File.class.getName(), file);
        saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
        saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
        saveTableParams.put(java.awt.Font.class.getName(), app.getUIContext().getFont(JTable.class));

        app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);

        app.getUIContext().showSuccessMessage("Table saved to: "+file);
        
        return file;
    }
}
