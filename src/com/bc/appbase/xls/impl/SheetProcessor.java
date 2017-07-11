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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 1:49:24 PM
 */
public class SheetProcessor<R> implements Consumer<Sheet> {

    private final int rowOffset;
    
    private final int rowLimit;
    
    private final Function<Cell[], R> rowProcessor;
    
    private final BiConsumer<Cell[], R> rowResultHandler;

    private final BiConsumer<Cell[], Exception> rowExceptionHandler;

    public SheetProcessor(Function<Cell[], R> rowProcessor, 
            BiConsumer<Cell[], R> rowResultHandler, BiConsumer<Cell[], Exception> rowExceptionHandler) {
        this(rowProcessor, rowResultHandler, rowExceptionHandler, 0, Integer.MAX_VALUE);
    }
    
    public SheetProcessor(Function<Cell[], R> rowProcessor, 
            BiConsumer<Cell[], R> rowResultHandler, BiConsumer<Cell[], Exception> rowExceptionHandler, int rowOffset, int rowLimit) {
        this.rowProcessor = Objects.requireNonNull(rowProcessor);
        this.rowResultHandler = Objects.requireNonNull(rowResultHandler);
        this.rowExceptionHandler = Objects.requireNonNull(rowExceptionHandler);
        this.rowOffset = rowOffset;
        this.rowLimit = rowLimit;
    }

    @Override
    public void accept(Sheet sheet) {
   
        for(int row = rowOffset, executed = 0; row < sheet.getRows(); row++, executed++) {
//System.out.println("Limit: "+rowLimit+", executed: "+executed+". @"+this.getClass());
            if(executed >= rowLimit) {
                break;
            }

            final Cell [] cells = sheet.getRow(row);                

            if(cells == null || cells.length < 1) {
                break;
            }

            try{
                
                final R result = rowProcessor.apply(cells);

                this.rowResultHandler.accept(cells, result); 
                
            }catch(Exception e) {
                
                this.rowExceptionHandler.accept(cells, e);
            }
        }
    }
    
    public final Function<Cell[], R> getRowProcessor() {
        return rowProcessor;
    }

    public BiConsumer<Cell[], R> getRowResultHandler() {
        return rowResultHandler;
    }
}
