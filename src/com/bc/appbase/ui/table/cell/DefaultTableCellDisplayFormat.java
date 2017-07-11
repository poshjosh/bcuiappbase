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

package com.bc.appbase.ui.table.cell;

import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.util.Selection;
import com.bc.ui.table.cell.TableCellDisplayFormatImpl;
import java.text.DateFormat;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 9, 2017 2:25:39 PM
 */
public class DefaultTableCellDisplayFormat extends TableCellDisplayFormatImpl{
    
    private final int serialColumnIndex;
    
    private final SelectionContext selectionContext;
    
    public DefaultTableCellDisplayFormat(SelectionContext  selectionContext, 
            DateFormat dateFormat, int serialColumnIndex) {
        super(dateFormat);
        this.selectionContext = Objects.requireNonNull(selectionContext);
        this.serialColumnIndex = serialColumnIndex;
    }
    
    @Override
    public Object toDisplayValue(Class columnClass, Object value, int row, int column) {
        final Object output;
        if(column == this.serialColumnIndex) {
            output = value + ".";
        }else if(value != null && this.selectionContext.isSelectionType(value.getClass())) {  
            output = this.selectionContext.getSelection(value);
        }else{
            output = super.toDisplayValue(columnClass, value, row, column);
        }
//System.out.println("["+row+":"+column+"] "+value+"\t output type: " + (output==null?null:output.getClass().getName())+", output: "+output+ "\t@"+this.getClass());                  
        return output;
    }
    
    @Override
    public Object fromDisplayValue(Class columnClass, Object displayValue, int row, int column) {
        if(column == this.serialColumnIndex) {
            if(displayValue != null) {
                final String sval = displayValue.toString();
                displayValue = sval.substring(0, sval.length()-1);
            }
        }
        if(displayValue instanceof Selection) {
            return ((Selection)displayValue).getValue();
        }else{
            return super.fromDisplayValue(columnClass, displayValue, row, column);
        }
    }
}
