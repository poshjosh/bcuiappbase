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
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import javax.swing.JOptionPane;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 9:47:42 PM
 */
public class PromptSelectSheetName implements Action<App, String> {

    @Override
    public String execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {
        try{
            final Workbook workbook = Objects.requireNonNull((Workbook)params.get(Workbook.class.getName()));
            return this.execute(app, workbook);
        }catch(IOException | BiffException e) {
            throw new com.bc.appcore.actions.TaskExecutionException(e);
        }
    }

    public String execute(App app, Workbook workbook) throws IOException, BiffException {
        
        final String [] sheetNames = workbook.getSheetNames();

        if(sheetNames == null || sheetNames.length == 0) {
            JOptionPane.showMessageDialog(null, 
                    "The workbook you selected contains No worksheets", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        final Object oval = JOptionPane.showInputDialog(null, "Select the worksheet to import data from", "Select Worksheet", JOptionPane.PLAIN_MESSAGE, null, sheetNames, sheetNames[0]);

        final String sheetName = oval == null ? null : oval.toString();

        if(sheetName == null || sheetName.isEmpty()) {

            JOptionPane.showMessageDialog(null, 
                    "You did not select any sheet name to import data from",
                    "Nothing Selected", JOptionPane.WARNING_MESSAGE);
        }
        
        return sheetName;
    }
}
