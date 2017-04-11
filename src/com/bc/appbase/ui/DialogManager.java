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

package com.bc.appbase.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 8:16:50 AM
 */
public interface DialogManager extends Popup {

    enum PageSelection{
        CurrentPage("Current Page"), 
        AllPages("All Pages"), 
        FirstPage("First Page");
//        PageRange("Page Range");
        
        private final String label;
        private PageSelection() {
            this.label = this.name();
        }
        private PageSelection(String label) {
            this.label = label;
        }
        public String getLabel() {
            return this.label;
        }
    }
    
    JFileChooser getFileChooser(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode);
    
    JFileChooser getFileChooser(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode);
    
    File showDialog(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode);
    
    File [] showDialog(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode);

    PageSelection promptSelectPages(String msg);
}
