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

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 9:10:10 PM
 */
import com.bc.table.cellui.ColumnWidths;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class WriteExcel {
    
    private transient final Logger logger = Logger.getLogger(WriteExcel.class.getName());

    private final WritableFont dataFont;

    private final WritableFont headerFont;

    private final DateFormat dateFormat;

    private WritableCellFormat textCellFormatBold;
    private WritableCellFormat textCellFormat;
    private WritableCellFormat dateCellFormat;
    private WritableCellFormat numberCellFormat;

    public WriteExcel(DateFormat dateFormat, WritableFont headerFont, WritableFont dataFont) {  
        this.dateFormat = Objects.requireNonNull(dateFormat);
        this.headerFont = Objects.requireNonNull(headerFont);
        this.dataFont = Objects.requireNonNull(dataFont);
    }

    public Map<String, Integer> write(
            File file, Map<String, TableModel> data, 
            ColumnWidths columnWidths, boolean append) throws IOException, WriteException {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Append: {0}, file: {1}", 
                    new Object[]{append, file});
        }
        
        this.textCellFormat = new WritableCellFormat(dataFont);

        this.textCellFormatBold = new WritableCellFormat(headerFont);

        this.dateCellFormat = new WritableCellFormat(dataFont, DateFormats.FORMAT2);

        this.numberCellFormat = new WritableCellFormat(dataFont, NumberFormats.INTEGER);

        final WritableCellFormat [] cfs = {textCellFormat, textCellFormatBold, dateCellFormat, numberCellFormat};

        for(WritableCellFormat cf : cfs) {
            cf.setBorder(Border.ALL, BorderLineStyle.THIN);
            cf.setWrap(true);
//            cf.setShrinkToFit(true);
        }

        final WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));
        
        Map<String, Integer> output = new HashMap();

        WritableWorkbook workbook = null;
        try{

            workbook = Workbook.createWorkbook(file, wbSettings);
            
            final Set<String> sheetNames = data.keySet();

            logger.log(Level.FINE, "Sheet names: {0}", sheetNames);
            
            for(String sheetName : sheetNames) {
            
                final Integer written = this.write(workbook, sheetName, 
                        data.get(sheetName), columnWidths, append);
                
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
    
    public int write(
            WritableWorkbook workbook, String sheetName, TableModel tableModel, 
            ColumnWidths columnWidths, boolean append) throws IOException, WriteException {
        
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Append: {0}, sheet: {1}", 
                    new Object[]{append, sheetName});
        }
        
        WritableSheet sheet = workbook.getSheet(sheetName);

        if(sheet == null) {

            sheet = workbook.createSheet(sheetName, workbook.getNumberOfSheets() + 1);  

        }else{

            if(!append) {
                for(int row = 0; row < sheet.getRows(); row++) {
                    sheet.removeRow(row);
                }
            }
        }

        final int previousRowCount = sheet.getRows();

        final int headerRowCount;

        if(previousRowCount == 0) {

            for(int col=0; col<tableModel.getColumnCount(); col++) {

                sheet.setColumnView(col, columnWidths.getColumnPreferredWidthInChars(col));

                final String colName = tableModel.getColumnName(col);

                logger.log(Level.FINER, "Column: {0}, class: {1}", new Object[]{colName, tableModel.getColumnClass(col).getName()});

                this.addHeader(sheet, col, 0, colName);
            }

            headerRowCount = 1;

        }else{
            headerRowCount = 0;
        }

        int written = 0;

        for(int row=0; row<tableModel.getRowCount(); row++) {

            for(int col=0; col<tableModel.getColumnCount(); col++) {

//                            final String colName = tableModel.getColumnName(col);

                final Class colClass = tableModel.getColumnClass(col);

                final Object value = tableModel.getValueAt(row, col);

                final int actualRow = previousRowCount + row + headerRowCount;

                if(value == null) {
                    this.addText(sheet, col, actualRow, null);
                }else if(colClass.getSuperclass() == Number.class) {
                    this.addNumber(sheet, col, actualRow, Long.valueOf(value.toString()).intValue());
                }else if(colClass == Date.class) {
                    Date date;
                    if(value instanceof Date) {
                        date = (Date)value;
                    }else{
                        try{
                            date = this.dateFormat.parse(String.valueOf(value));
                            if(logger.isLoggable(Level.FINER)) {
                                logger.log(Level.FINER, "Parsed value: {0} to date: {1}", 
                                        new Object[]{value, date});
                            }        
                        }catch(ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    this.addDateTime(sheet, col, actualRow, date);

                }else{
                    this.addText(sheet, col, actualRow, String.valueOf(value));
                }
            }

            ++written;
        }  
            
        return written;    
    }

    private void addHeader(WritableSheet sheet, int column, int row, String s)
                    throws RowsExceededException, WriteException {
        final Label label = new Label(column, row, s, this.textCellFormatBold);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                    Integer integer) throws WriteException, RowsExceededException {
        final Number number = new Number(column, row, integer, this.numberCellFormat);
        sheet.addCell(number);
    }

    private void addText(WritableSheet sheet, int column, int row, String s)
                    throws WriteException, RowsExceededException {
        final Label label = new Label(column, row, s, this.textCellFormat);
        sheet.addCell(label);
    }

    private void addDateTime(WritableSheet sheet, int column, int row, Date date)
                    throws WriteException, RowsExceededException {
        final DateTime dateTime = new DateTime(column, row, date, this.dateCellFormat);
        sheet.addCell(dateTime);
    }
}