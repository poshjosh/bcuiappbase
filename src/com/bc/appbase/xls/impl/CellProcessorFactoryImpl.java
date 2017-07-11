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

import com.bc.appbase.xls.CellProcessorFactory;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.appcore.predicates.IsSubClass;
import com.bc.appcore.util.TextHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 2:06:19 PM
 */
public class CellProcessorFactoryImpl implements CellProcessorFactory{
    
    private final SheetToDatabaseMetaData metaData;
    
    private final TextHandler textHandler;
    
    private final IsSubClass isDateType;
    
    private final IsSubClass isNumberType;
    
    private final Function<Cell, String> textExtractor;
    
    private final Function<Cell, Double> numberExtractor;
    
    private final Function<Cell, Date> dateExtractor;
    
    public CellProcessorFactoryImpl(SheetToDatabaseMetaData metaData, 
            TextHandler textHandler, BiConsumer<Cell, Exception> errorHandler) {
        this.metaData = Objects.requireNonNull(metaData);
        this.textHandler = Objects.requireNonNull(textHandler);
        this.isDateType = new IsSubClass(Date.class);
        this.isNumberType = new IsSubClass(Number.class);
        this.textExtractor = new CellTextExtractor(null, errorHandler);
        this.numberExtractor = new CellNumberExtractor(null, errorHandler);
        this.dateExtractor = new CellDateExtractor(this.textHandler, null, errorHandler);
    }
    
    @Override
    public List<Function<Cell, Object>> getProcessorList(Cell cell, List<Cell> splitParts) {
        
        final List<Class> columnTypes = this.metaData.getClassList(cell, splitParts);
        
        final String errorMsg = "Failed to resolve `type` of excel cell @[" + cell.getRow()+':'+cell.getColumn()+']';
        Objects.requireNonNull(columnTypes, errorMsg);
        if(columnTypes.isEmpty()) { throw new IllegalArgumentException(errorMsg); }
        
        final List<Function<Cell, Object>> output = new ArrayList(columnTypes.size());
        
        for(Class columnType : columnTypes) {
            
            output.add(this.getProcessor(columnType));
        }
        
        return output;
    }
    
    @Override
    public Function<Cell, Object> getProcessor(Cell cell) {
        
        final Class columnType = this.metaData.getClass(cell);
        
        final String errorMsg = "Failed to resolve `type` of cell @[" + cell.getRow()+':'+cell.getColumn()+']';
        Objects.requireNonNull(columnType, errorMsg);
        
        return this.getProcessor(columnType);
    }

    @Override
    public Function<Cell, Object> getProcessor(Class columnType) {
        
        final Function output;
        
        Objects.requireNonNull(columnType);
        
        if(columnType == String.class) {
            output = this.textExtractor;
        }else if(this.isNumberType.test(columnType)) {
            output = this.numberExtractor;
        }else if(this.isDateType.test(columnType)) {
            output = this.dateExtractor;
        }else{
            output = this.textExtractor;
        }
        
        return output;
    }
}
