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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import jxl.Cell;
import jxl.write.Label;

/**
 * @author Chinomso Bassey Ikwuagwu on May 14, 2017 9:27:28 PM
 */
public class TextCellSpliter implements Function<Cell, List<Cell>> {

    private final String separatorRegex;
    
    public TextCellSpliter(String separatorRegex) {
        this.separatorRegex = Objects.requireNonNull(separatorRegex);
    }

    @Override
    public List<Cell> apply(Cell cell) {
        
        final List<String> parts = this.getParts(cell);
        
        final List<Cell> output;
        
        if(parts.isEmpty()) {
            output = Collections.singletonList(cell);
        }else{
        
            final List<Cell> cells = new ArrayList(parts.size());

            for(String part : parts) {
                cells.add(new Label(cell.getColumn(), cell.getRow(), part));
            }

            output = Collections.unmodifiableList(cells);
        }
        
        return output;
    }
    
    public List<String> getParts(Cell cell) {
        final String contents = cell.getContents();
        final List<String> output = this.getParts(contents);
//System.out.println("Split cell @["+cell.getRow()+':'+cell.getColumn()+" into parts: "+output+". @"+this.getClass());        
        return output;
    }    
    
    public List<String> getParts(String text) {
        final String [] _arr = text == null ? null : text.split(this.separatorRegex);
        final List<String> _list = _arr == null ? Collections.EMPTY_LIST : Arrays.asList(_arr);
        final List<String> output;
        if(_list.isEmpty()) {
            output = Collections.EMPTY_LIST;
        }else{
            output = new ArrayList(_list.size());
            for(String str : _list) {
                if(str != null && !str.trim().isEmpty()) {
                    output.add(str);
                }
            }
        }
        return output;
    }
}
