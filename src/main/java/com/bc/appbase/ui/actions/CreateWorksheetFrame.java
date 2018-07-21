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
import com.bc.appbase.ui.table.model.WorksheetTableModel;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2017 7:32:04 PM
 */
public class CreateWorksheetFrame implements Action<App, JFrame> {

    @Override
    public JFrame execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Sheet sheet = this.getWorksheet(params);
        
        final JTable table = this.getTable(params);
        
        app.getUIContext().setTableFont(table);
        
        final Integer offset = (Integer)params.get(ParamNames.OFFSET);
        final Integer limit = (Integer)params.get(ParamNames.LIMIT);
        
        final WorksheetTableModel tableModel = new WorksheetTableModel(sheet, offset, limit);
        table.setModel(tableModel);

        final String frameTitle = params.get(ParamNames.TITLE) == null ? "" : (String)params.get(ParamNames.TITLE);
        final JFrame frame = new JFrame(frameTitle);
        frame.getContentPane().add(new JScrollPane(table));
        
        frame.pack();
        
        return frame;
    }
    
    public Sheet getWorksheet(Map<String, Object> params) throws TaskExecutionException {
        final Sheet sheet;
        final File file = (File)params.get(java.io.File.class.getName());
        if(file == null) {
            sheet = (Sheet)params.get(ParamNames.SHEET);
        }else{
            final String sheetName = Objects.requireNonNull((String)params.get(ParamNames.SHEET_NAME));
            try{
                final Workbook workbook = Workbook.getWorkbook(file);
                sheet = workbook.getSheet(sheetName);
            }catch(IOException | BiffException e) {
                throw new TaskExecutionException(e);
            }
        }   
        Objects.requireNonNull(sheet);
        return sheet;
    }

    public JTable getTable(Map<String, Object> params) {
        final JTable table = params.get(JTable.class.getName()) == null ?
                new JTable() : (JTable)params.get(JTable.class.getName());
        return table;
    }
}
