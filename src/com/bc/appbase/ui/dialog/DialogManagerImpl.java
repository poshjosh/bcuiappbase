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

package com.bc.appbase.ui.dialog;

import com.bc.appbase.ui.UIContext;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 8:23:55 AM
 */
public class DialogManagerImpl extends PopupImpl implements DialogManager {
    
    private File lastSelectedFile;
    
    private final UIContext uiContext;

    public DialogManagerImpl(UIContext uiContext) {
        super(uiContext == null ? null : uiContext.getMainFrame());
        this.uiContext = uiContext;
    }

    public DialogManagerImpl(UIContext uiContext, Map<Object, OptionAction> errorOptions) {
        super(uiContext == null ? null : uiContext.getMainFrame(), errorOptions);
        this.uiContext = uiContext;
    }
    
    public DialogManagerImpl(UIContext uiContext, Map<Object, OptionAction> successOptions, Map<Object, OptionAction> errorOptions) {
        super(uiContext == null ? null : uiContext.getMainFrame(), successOptions, errorOptions);
        this.uiContext = uiContext;
    }
    
    @Override
    public void showError(String title, String message, Throwable t) {
        new ViewDetailsAction(this.uiContext, title).execute(message, t);
    }
    
    @Override
    public File showDialog(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode) {

        final File [] files = this.showDialog(dialogType, title, false, null, fileFilter, fileSelectionMode);
        
        return files.length == 0 ? null : files[0];
    }
    
    @Override
    public File [] showDialog(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode) {
        
        final JFileChooser fileChooser = this.getFileChooser(
                dialogType, title, multiSelectionEnabled, null, fileFilter, fileSelectionMode);
        
        fileChooser.setVisible(true);
        
        final int selection = fileChooser.showDialog(this.getParentComponent(), approveButtonText);
        
        File [] output;
        if(selection == JFileChooser.APPROVE_OPTION) {
            if(multiSelectionEnabled) {
                output = fileChooser.getSelectedFiles();
            }else{
                output = new File[]{fileChooser.getSelectedFile()};
            }
        }else{
            output = new File[]{};
        }
        return output;
    }

    @Override
    public JFileChooser getFileChooser(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode) {
        return this.getFileChooser(dialogType, title, false, null, fileFilter, fileSelectionMode);
    }
    
    @Override
    public JFileChooser getFileChooser(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode) {
        
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.getCurrentDirectory());
        fileChooser.setDialogType(dialogType);
        fileChooser.setDialogTitle(title);
        fileChooser.setMultiSelectionEnabled(multiSelectionEnabled);
        if(fileFilter != null) {
            fileChooser.setFileFilter(fileFilter);
        }
        if(fileSelectionMode != -1) {
            fileChooser.setFileSelectionMode(fileSelectionMode);
        }
        
        if(approveButtonText != null) {
            fileChooser.setApproveButtonText(approveButtonText);
        }
        
        return fileChooser;
    }

    private File getCurrentDirectory() {
        File output;
        if(lastSelectedFile == null) {
            output = Paths.get(System.getProperty("user.home")).toFile();
        }else{
            if(lastSelectedFile.isFile()) {
                output = lastSelectedFile.getParentFile();
            }else{
                output = lastSelectedFile;
            }
        }
        return output;
    }
}
