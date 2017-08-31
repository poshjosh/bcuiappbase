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
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.table.TableModel;
import jxl.write.WriteException;
import com.bc.appbase.ui.WritableAwtFont;
import com.bc.appbase.xls.impl.TableModelExcelWriter;
import java.awt.Font;
import com.bc.appbase.App;
import com.bc.appcore.table.model.DisplayTableModelFromModel;
import com.bc.appcore.table.model.TableModelDisplayFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 2:06:21 PM
 */
public class SaveTableModel implements Action<App, File> {

    private static final Logger logger = Logger.getLogger(SaveTableModel.class.getName());
    
    @Override
    public File execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.exceptions.TaskExecutionException {

        final Map<String, TableModel> data = (Map<String, TableModel>)params.get(ParamNames.DATA);
        
        if(data == null || data.isEmpty()) {
            
            return null;
        }

        final File file = (File)params.get(java.io.File.class.getName());
        
        try{
            
            final File parent = file.getParentFile();
            if(!parent.exists()) {
                parent.mkdirs();
            }
            if(!file.exists()) {
                file.createNewFile();
            }
            
            final Font font = (Font)params.get(Font.class.getName());
            final Font headerFont = font.deriveFont(Font.BOLD);
            
            final Boolean append = params.get(ParamNames.APPEND) == null ? Boolean.FALSE : (Boolean)params.get(ParamNames.APPEND);
            
            final TableModelExcelWriter writeExcel = new TableModelExcelWriter(
                    app.getDateTimeFormat(), 
                    new WritableAwtFont(headerFont), 
                    new WritableAwtFont(font));
            
            this.write(app, writeExcel, file, data, append);

        }catch(IOException | WriteException e) {

            throw new com.bc.appcore.exceptions.TaskExecutionException(e);
        }
        
        return file;
    }

    public Map<String, Integer> write(
            App app, TableModelExcelWriter writeExcel, File file, Map<String, TableModel> data, boolean append) 
            throws IOException, WriteException {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Append: {0}, file: {1}", 
                    new Object[]{append, file});
        }
        
        final WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));
        
        Map<String, Integer> output = new HashMap();

        WritableWorkbook workbook = null;
        try{

            workbook = Workbook.createWorkbook(file, wbSettings);
            
            final Set<String> sheetNames = data.keySet();

            logger.log(Level.FINE, "Sheet names: {0}", sheetNames);

            final TableModelDisplayFormat modelDisplayFormat = app.getUIContext().getTableModelDisplayFormat(-1);
            
            for(String sheetName : sheetNames) {
            
                final TableModel tableModel = data.get(sheetName);
                
                final Integer written = writeExcel.write(workbook, sheetName, 
                        new DisplayTableModelFromModel(tableModel, modelDisplayFormat), 
                        app.getUIContext().getColumnWidths(), append);
                
                output.put(sheetName, written);
            }        

            workbook.write();

        }finally{
            if(workbook != null) {
                workbook.close();
            }
        }

        return output;
    }
    
}
