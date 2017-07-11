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

import com.bc.appbase.xls.SheetToDatabaseData;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.appcore.typeprovider.TypeProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2017 6:20:28 PM
 */
public class SheetToDatabaseMetaDataImpl implements SheetToDatabaseMetaData{

    private static final Logger logger = Logger.getLogger(SheetToDatabaseMetaDataImpl.class.getName());

    private final TypeProvider typeProvider;
    
    private final int headerRowIndex;
    
    private final int dataRowOffset;
    
    private final int dataRowLimit;
    
    private final Class rowEntityType;
    
    private final SheetToDatabaseData sheetToDataabseData;

    public SheetToDatabaseMetaDataImpl(
            Map<Integer, List<String>> excelToDbColumns, 
            Map<Integer, Function<Cell, List<Cell>>> spliters,
            TypeProvider typeProvider, Class rowEntityType) {
        
        this(new SheetToDatabaseDataImpl(excelToDbColumns, spliters), 
                typeProvider, rowEntityType, 0, 1, Integer.MAX_VALUE);
    }
    
    public SheetToDatabaseMetaDataImpl(
            SheetToDatabaseData sheetToDatabaseData,
            TypeProvider typeProvider, Class rowEntityType) {
        
        this(sheetToDatabaseData, typeProvider, rowEntityType, 0, 1, Integer.MAX_VALUE);
    }
    public SheetToDatabaseMetaDataImpl(
            Map<Integer, List<String>> excelToDbColumns, 
            Map<Integer, Function<Cell, List<Cell>>> spliters,
            TypeProvider typeProvider, Class rowEntityType, 
            int headerRowIndex, int dataRowOffset, int dataRowLimit) {
        this(new SheetToDatabaseDataImpl(excelToDbColumns, spliters),
                typeProvider, rowEntityType, headerRowIndex, dataRowOffset, dataRowLimit);
    }

    public SheetToDatabaseMetaDataImpl(
            SheetToDatabaseData sheetToDatabaseData,
            TypeProvider typeProvider, Class rowEntityType, 
            int headerRowIndex, int dataRowOffset, int dataRowLimit) {
        this.sheetToDataabseData = Objects.requireNonNull(sheetToDatabaseData);
        this.typeProvider = Objects.requireNonNull(typeProvider);
        this.rowEntityType = Objects.requireNonNull(rowEntityType);
        this.headerRowIndex = headerRowIndex;
        this.dataRowOffset = dataRowOffset;
        this.dataRowLimit = dataRowLimit;
    }
    
    @Override
    public int getHeaderRowIndex() {
        return headerRowIndex;
    }

    @Override
    public int getDataRowOffset() {
        return dataRowOffset;
    }

    @Override
    public int getDataRowLimit() {
        return dataRowLimit;
    }

    @Override
    public Class getRowEntityType() {
        return rowEntityType;
    }
    
    @Override
    public List<Class> getClassList(Cell cell, List<Cell> splitParts) {
        
        final List<String> columnNameList = sheetToDataabseData.getColumnNameList(cell);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Excel column index: {0} = database column names: {1}", 
                    new Object[]{cell, columnNameList});
        }
        
        final String errorMsg = "Unable to resolve column name(s) for excel column index: " + cell;
        Objects.requireNonNull(columnNameList, errorMsg);
        if(columnNameList.isEmpty()) { throw new IllegalArgumentException(errorMsg); }
        
        final int MIN_SIZE = Math.min(splitParts.size(), columnNameList.size());
        if(MIN_SIZE < 1) {
            throw new UnsupportedOperationException("@["+cell.getRow()+':'+cell.getColumn()+"] encountered length mis-match. " +
                    columnNameList.size()+" columns split among "+splitParts.size()+" parts");
        }
        
        final List<Class> output = new ArrayList(MIN_SIZE);
        
        for(int i=0; i<MIN_SIZE; i++) {
            
            final String columnName = columnNameList.get(i);
            
            output.addAll(this.getClassList(cell, columnName));
        }
        
        return output;
    }

    @Override
    public Class getClass(Cell cell) {
        
        final String columnName = sheetToDataabseData.getColumnName(cell);
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "@ = database column name: {1}", 
                    new Object[]{cell, columnName});
        }
        
        final String errorMsg = "Unable to resolve column name for excel column index: " + cell;
        Objects.requireNonNull(columnName, errorMsg);
        
        final List<Class> output = this.getClassList(cell, columnName);
        
        this.throwExceptionIfNullOrEmpty(output, cell.getColumn(), columnName);
        
        if(output.size() == 1) {
            return output.get(0);
        }else{
            throw new IllegalArgumentException("Expected One class but found " + output.size() + " for excel column: "+cell + " with matching database column: "+columnName);
        }
    }

    public List<Class> getClassList(Cell cell, String columnName) {
        
        final int columnIndex = cell.getColumn();
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Excel column index: {0} = database column name: {1}", 
                    new Object[]{columnIndex, columnName});
        }
        
        final String errorMsg = "Unable to resolve column name for excel column index: " + columnIndex;
        Objects.requireNonNull(columnName, errorMsg);
        
        final List<Class> output = new ArrayList();
        
        final List<Class> columnTypes = this.typeProvider.getTypeList(columnName, null);
        this.throwExceptionIfNullOrEmpty(columnTypes, columnIndex, columnName);
        
        if(columnTypes.size() > 1) {

            final Predicate<Class> acceptNonEntityType = (cls) -> cls.getAnnotation(Entity.class) == null;

            columnTypes.stream().filter(acceptNonEntityType).forEach((cls) -> { output.add(cls); } );
            
        }else{
            
            output.add(columnTypes.get(0));
        }
        
        return output;
    }
    
    public void throwExceptionIfNullOrEmpty(List<Class> columnTypes, int columnIndex, String columnName) {
        final String errorMessage = this.getTypeResolutionExceptionMessage(columnIndex, columnName);
        Objects.requireNonNull(columnTypes, errorMessage);
        if(columnTypes.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    public String getTypeResolutionExceptionMessage(int columnIndex, String columnName) {
        return "Unable to resolve column types for excel column index: " + 
                columnIndex + " with database column name: "+columnName;
    }

    @Override
    public boolean isMultiple(Cell cell) {
        return sheetToDataabseData.isMultiple(cell);
    }

    @Override
    public List<String> getColumnNameList(Cell cell) {
        return sheetToDataabseData.getColumnNameList(cell);
    }

    @Override
    public Function<Cell, List<Cell>> getCellSpliter(Cell cell) {
        return sheetToDataabseData.getCellSpliter(cell);
    }

    @Override
    public String getColumnName(Cell cell) {
        return sheetToDataabseData.getColumnName(cell);
    }

    @Override
    public List<Integer> getExcelColumnIndices() {
        return sheetToDataabseData.getExcelColumnIndices();
    }

    @Override
    public List<Integer> getMultiValueCellIndices() {
        return sheetToDataabseData.getMultiValueCellIndices();
    }

    @Override
    public Collection<List<String>> getColumnNames() {
        return sheetToDataabseData.getColumnNames();
    }

    @Override
    public Collection<Function<Cell, List<Cell>>> getCellSpliters() {
        return sheetToDataabseData.getCellSpliters();
    }
}
