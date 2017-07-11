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
import com.bc.appbase.ui.dialog.DialogManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import jxl.read.biff.BiffException;
import com.bc.appbase.App;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 9:52:10 PM
 */
public class PromptSelectExcelFile implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {
        try{
            return this.execute(app.getOrException(DialogManager.class));
        }catch(IOException | BiffException e) {
            throw new com.bc.appcore.actions.TaskExecutionException(e);
        }
    }

    public File execute(DialogManager dialogManager) throws IOException, BiffException {
        
        final javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xls");
            }
            @Override
            public String getDescription() {
                return "Excel (.xls) Files";
            }
        };
        
        final File file = dialogManager.showDialog(
                JFileChooser.OPEN_DIALOG, "Select Excel File To Import Data From", 
                fileFilter, JFileChooser.FILES_ONLY);
        
        if(file != null) {

            if(!file.exists()) {
                
                JOptionPane.showMessageDialog(null, 
                        "The file you selected does not exist: " + file, 
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
                
                this.execute(dialogManager);
            }
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Selected file: {0}", file);
        
        return file;
    }
}
