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

import java.text.MessageFormat;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 30, 2017 12:58:39 AM
 */
public class TablePrintableWithNewLineSupport extends TablePrintableCustom {

    private final String NEW_LINE = System.getProperty("line.separator");
    
    public TablePrintableWithNewLineSupport(
            JTable table, JTable.PrintMode printMode, 
            MessageFormat headerFormat, MessageFormat footerFormat, 
            TablePrintProperties printProperties) {
        
        super(table, printMode, headerFormat, footerFormat, printProperties);
    }

    @Override
    public String [] getHeaderText(MessageFormat headerFormat, Object[] pageNumber, String [] outputIfNone) {
        final String headerText = headerFormat == null ? null : headerFormat.format(pageNumber);
        return this.lines(headerText, outputIfNone);
    }

    @Override
    public String [] getFooterText(MessageFormat footerFormat, Object[] pageNumber, String[] outputIfNone) {
        final String footerText = footerFormat == null ? null : footerFormat.format(pageNumber);
        return this.lines(footerText, outputIfNone);
    }
    
    public String [] lines(String text, String [] outputIfNone) {
        final String [] lines;
        if(text == null) {
            lines = null;
        }else{
            if(text.contains(NEW_LINE)) {
                lines = text.split(NEW_LINE);
            }else{
                lines = new String[]{text};
            }
        }
        return lines == null ? outputIfNone : lines;
    }
}
