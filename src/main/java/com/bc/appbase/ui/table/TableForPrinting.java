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

package com.bc.appbase.ui.table;

import java.awt.print.Printable;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 7, 2017 8:45:47 PM
 */
public class TableForPrinting extends JTable {
    
    @FunctionalInterface
    public static interface PrintableSupplier{
        Printable getPrintable(JTable.PrintMode printMode, 
                            MessageFormat headerFormat, MessageFormat footerFormat);
    }
    
    public static class DefaultPrintableSupplier implements PrintableSupplier {

        private final JTable table;
        
        private final TablePrintProperties tablePrintProperties;

        public DefaultPrintableSupplier(JTable table, int spaceFromTableToHeaderFooter) {
            this(table, new TablePrintPropertiesImpl(table, spaceFromTableToHeaderFooter));
        }
        
        public DefaultPrintableSupplier(JTable table, TablePrintProperties tablePrintProperties) {
            this.table = Objects.requireNonNull(table);
            this.tablePrintProperties = Objects.requireNonNull(tablePrintProperties);
        }
        
        @Override
        public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
            return new TablePrintableWithNewLineSupport(
                    table, printMode, headerFormat, footerFormat, tablePrintProperties);
        }
    }

    private final PrintableSupplier printableSupplier;

    public TableForPrinting(PrintableSupplier printableSupplier) {
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, TableModel dm) {
        super(dm);
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, int numRows, int numColumns) {
        super(numRows, numColumns);
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        this.printableSupplier = printableSupplier;
    }

    public TableForPrinting(PrintableSupplier printableSupplier, Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        this.printableSupplier = printableSupplier;
    }
    
    @Override
    public Printable getPrintable(JTable.PrintMode printMode, 
            MessageFormat headerFormat, MessageFormat footerFormat) {

        final Printable printable;
        
        if(printableSupplier != null) {
            
            printable = printableSupplier.getPrintable(printMode, headerFormat, footerFormat);
            
        }else{
            
            printable = new DefaultPrintableSupplier(this, 8).getPrintable(printMode, headerFormat, footerFormat);
        }
        Objects.requireNonNull(printable);
        
        return printable;
    }
}
