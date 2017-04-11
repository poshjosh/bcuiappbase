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

package com.bc.appbase.excel;

import com.bc.appbase.ui.UILog;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 1, 2017 6:59:19 PM
 */
public class ExcelDataImporterImpl<T> 
        implements ExcelDataImporter, ExcelRowHandler<T> {

    private final App app;
    
    private final ExcelRowHandler<T> excelRowHandler;
    
    private final UILog uiLog;
    
    public ExcelDataImporterImpl(App app, ExcelRowHandler<T> excelRowHandler, UILog uiLog) {
        this.app = Objects.requireNonNull(app);
        this.excelRowHandler = Objects.requireNonNull(excelRowHandler);
        this.uiLog = uiLog;
    }

    @Override
    public T handleRow(T previous, Sheet sheet, Cell [] cells, int row, Set<Integer> failedRows) {
        return this.excelRowHandler.handleRow(previous, sheet, cells, row, failedRows);
    }

    @Override
    public void execute(File file, String sheetName) {
        this.execute(file, sheetName, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public void execute(File file, String sheetName, int rowOffset, int rowLimit) {
        
        try{
            
            final Workbook workbook = Workbook.getWorkbook(file);
            final Sheet sheet = workbook.getSheet(sheetName);
            
            final Set<Integer> failedRows = new TreeSet();
            
            T previousResult = null;
            for(int row = 0, executed = 0; row < sheet.getRows(); row++, executed++) {
                
                if(row < rowOffset) {
                    continue;
                }
                
                if(executed >= rowLimit) {
                    break;
                }
                
                final Cell [] cells = sheet.getRow(row);                
                
                if(cells == null || cells.length < 1) {
                    break;
                }
            
//                if(cells.length < minCols) {
                    
//                    log("ERRO ["+row+":]\tSKIPPING ROW. Insufficient cells");
                    
//                    failedRows.add(row);
                    
//                    continue;
//                }

                try{
                    
                    final T result = this.handleRow(previousResult, sheet, cells, row, failedRows);
                    
                    if(result != null) {
                        
                        previousResult = result;
                    }
                    
                }catch(Throwable t) {
                    this.log(t);
                    failedRows.add(row);
                }
            }
            
            final Set<Integer> toDisplay = new TreeSet<>();
            for(Integer row : failedRows) {
                toDisplay.add(row + 1);
            }
            
            final String msg = "The following rows were unsuccessful:\n"+toDisplay;
            JTextArea textArea = new JTextArea();
            textArea.setText(msg);
            Dimension dim = new Dimension(600, 150); 
            textArea.setPreferredSize(dim);
            JScrollPane scrolls = new JScrollPane(textArea);
            scrolls.setPreferredSize(dim);
            JFrame frame = new JFrame();
            frame.setPreferredSize(dim);
            frame.getContentPane().add(scrolls);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            
            log(msg);
            
        }catch(IOException | BiffException e) {
            log(e);
        }
    }
    
    public void log(Throwable t) {
        if(uiLog != null) uiLog.log(t);
    }
    
    public void log(String msg) {
        if(uiLog != null) uiLog.log(msg);
    }

    public final App getApp() {
        return app;
    }

    public final UILog getUiLog() {
        return uiLog;
    }

    public final ExcelRowHandler<T> getExcelRowHandler() {
        return excelRowHandler;
    }
}
