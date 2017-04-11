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
import com.bc.appcore.util.Util;
import com.bc.appbase.ui.DialogManager;
import com.bc.appbase.ui.DialogManager.PageSelection;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.bc.appbase.ui.model.PageSelectionTableModel;
import com.bc.appbase.App;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 13, 2017 9:05:28 AM
 */
public class SaveTableAs implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) {

        final JTable table = Objects.requireNonNull((JTable)params.get(JTable.class.getName()));
        
        final DialogManager dialogManager = app.get(DialogManager.class);
        
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
        
        File file = dialogManager.showDialog(
                JFileChooser.SAVE_DIALOG, "Specify Location to Save", 
                fileFilter, JFileChooser.DIRECTORIES_ONLY);
        
        if(file == null) {
            
            return null;
        }
        
        file = new File(Util.convertToExtension(file.getPath(), "xls"));

        try{

            final PageSelection pageSelection = 
                    dialogManager.promptSelectPages("Which pages do you want to save?");
            
            if(pageSelection == null) {
                return null;
            }
            
            final TableModel tableModel = new PageSelectionTableModel(
                    app, table, app.getSearchContext(null).getResultModel(), pageSelection);

            final Map<String, Object> saveTableParams = new HashMap<>();
            saveTableParams.put(java.io.File.class.getName(), file);
            saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
            saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
            saveTableParams.put(java.awt.Font.class.getName(), app.getUIContext().getFont(table));
            
            app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);

            app.getUIContext().showSuccessMessage("Table saved to: "+file);

        }catch(ParameterException | TaskExecutionException e) {

            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error saving table to file: "+file, e);

            app.getUIContext().showErrorMessage(e, "Error saving table to file: "+file);
        }
        
        return file;
    }
}
