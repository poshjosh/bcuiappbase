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

import com.bc.appbase.ui.dialog.Popup;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 8:16:50 AM
 */
public interface DialogManager extends Popup {
    
    void showError(String title, String message, Throwable t);
    
    JFileChooser getFileChooser(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode);
    
    JFileChooser getFileChooser(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode);
    
    File showDialog(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode);
    
    File [] showDialog(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode);
}
