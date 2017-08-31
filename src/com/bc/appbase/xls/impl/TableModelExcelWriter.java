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

package com.bc.appbase.xls.impl;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 9:10:10 PM
 */
import com.bc.ui.table.cell.ColumnWidths;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

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

public class TableModelExcelWriter {
    
    private transient final Logger logger = Logger.getLogger(TableModelExcelWriter.class.getName());

    private final WritableFont dataFont;

    private final WritableFont headerFont;

    private final DateFormat dateFormat;

    private final WritableCellFormat textCellFormatBold;
    private final WritableCellFormat textCellFormat;
    private final WritableCellFormat dateCellFormat;
    private final WritableCellFormat numberCellFormat;

    public TableModelExcelWriter(DateFormat dateFormat, WritableFont headerFont, WritableFont dataFont) {  
        
        this.dateFormat = Objects.requireNonNull(dateFormat);
        this.headerFont = Objects.requireNonNull(headerFont);
        this.dataFont = Objects.requireNonNull(dataFont);

        this.textCellFormat = new WritableCellFormat(dataFont);

        this.textCellFormatBold = new WritableCellFormat(headerFont);

        this.dateCellFormat = new WritableCellFormat(dataFont, DateFormats.FORMAT2);

        this.numberCellFormat = new WritableCellFormat(dataFont, NumberFormats.INTEGER);
        
        final WritableCellFormat [] cfs = {textCellFormat, textCellFormatBold, dateCellFormat, numberCellFormat};

        for(WritableCellFormat cf : cfs) {
            try{
                cf.setBorder(Border.ALL, BorderLineStyle.THIN);
                cf.setWrap(true);
                cf.setShrinkToFit(true);
            }catch(WriteException e) {
                logger.log(Level.WARNING, "Exception customizing instance of: "+cf.getClass().getName(), e);
            }
        }
    }

    public int write(
            WritableWorkbook workbook, String sheetName, TableModel tableModel, 
            ColumnWidths columnWidths, boolean append) throws IOException, WriteException {
        
        logger.log(Level.FINER, () -> "Append: "+append+", sheet: "+sheetName);
        
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

            final float tableWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 50;
            
            final int [] preferredChars = columnWidths.getPreferredChars(tableModel, tableWidth, dataFont.getPointSize()/2);
            
            for(int col=0; col<tableModel.getColumnCount(); col++) {

                sheet.setColumnView(col, preferredChars[col]);

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