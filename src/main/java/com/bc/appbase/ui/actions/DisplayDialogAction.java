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
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.JFileChooser;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 8:13:44 PM
 */
public class DisplayDialogAction implements Action<App,File> {
    
    public static final String TITLE = ParamNames.TITLE;
    public static final String CURRENT_DIR = "currentDir";
    public static final String DESCRIPTION_OF_FILE_TYPE = "fileTypeDescription";
    
    private final int dialogType;
    private final int fileSelectionMode;

    public DisplayDialogAction(int dialogType) {
        this(dialogType, JFileChooser.FILES_AND_DIRECTORIES);
    }
    
    public DisplayDialogAction(int dialogType, int fileSelectionMode) {
        this.dialogType = dialogType;
        this.fileSelectionMode = fileSelectionMode;
    }
    
    public String getDefaultDescription(int fileSelectionMode) {
        final String description;
        switch(fileSelectionMode) {
            case JFileChooser.FILES_ONLY: 
                description = "Files Only"; break;
            case JFileChooser.DIRECTORIES_ONLY:
                description = "Folders Only"; break;
            default:
                description = "Files/Folders"; break;
        }
        return description;
    }

    public boolean accept(File f) {
        final boolean accept;
        switch(fileSelectionMode) {
            case JFileChooser.FILES_ONLY: 
                accept = f.isFile(); break;
            case JFileChooser.DIRECTORIES_ONLY:
                accept = f.isDirectory(); break;
            default:
                accept = f.isFile() || f.isDirectory(); break;
        }
        return accept;
    }
    
    @Override
    public File execute(App app, Map<String, Object> params) throws ParameterException, TaskExecutionException {
        
        final String description = (String)params.getOrDefault(
                DESCRIPTION_OF_FILE_TYPE, this.getDefaultDescription(fileSelectionMode));
        
        javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return DisplayDialogAction.this.accept(f);
            }
            @Override
            public String getDescription() {
                return description;
            }
        };
        
        final String title = (String)params.getOrDefault(TITLE, "");
        final DialogManager dialogManager = app.getOrException(DialogManager.class);
        final JFileChooser fileChooser = dialogManager.getFileChooser(
                this.dialogType, title, fileFilter, this.fileSelectionMode);
        
        final File currentDir = this.getCurrentDir(params, null);
        
        if(currentDir != null) {
            fileChooser.setCurrentDirectory(currentDir);
        }
        
        final int selection = fileChooser.showOpenDialog(app.getUIContext().getMainFrame());
        
        final File selectedFile;
        if(selection == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }else{
            selectedFile = null;
        }
        
        return selectedFile;
    }
    
    public File getCurrentDir(Map params, File outputIfNone) throws InvalidParameterException {
        final Object oval = params.get(CURRENT_DIR);
        final File currentDir;
        if(oval == null) {
            currentDir = outputIfNone;
        }else if(oval instanceof String) {
            currentDir = Paths.get((String)oval).toFile();
        }else if(oval instanceof Path) {
            currentDir = ((Path)oval).toFile();
        }else if(oval instanceof File) {
            currentDir = (File)oval;
        }else{
            throw new InvalidParameterException(CURRENT_DIR);
        }

        return currentDir;
    }
}

