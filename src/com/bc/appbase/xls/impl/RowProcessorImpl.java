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
import com.bc.appbase.xls.CellResult;
import com.bc.appbase.xls.CellSplitValidator;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 1:35:25 PM
 */
public class RowProcessorImpl<R> implements Function<Cell[], List<CellResult>> {

    private static final Logger logger = Logger.getLogger(RowProcessorImpl.class.getName());

    private final CellProcessorFactory cellProcessorFactory;
    
    private final SheetToDatabaseMetaData metaData;
    
    private final CellSplitValidator cellSplitValidator;
    
    private final List<CellResult> outputIfNoResult;
    
    private final BiConsumer<Cell[], Exception> rowExceptionHandler;

    public RowProcessorImpl(
            CellProcessorFactory processorFactory, 
            SheetToDatabaseMetaData metaData,
            CellSplitValidator cellSplitValidator,
            List<CellResult> outputIfNoResult,
            BiConsumer<Cell[], Exception> rowExceptionHandler) {
        this.cellProcessorFactory = Objects.requireNonNull(processorFactory);
        this.metaData = Objects.requireNonNull(metaData);
        this.cellSplitValidator = Objects.requireNonNull(cellSplitValidator);
        this.outputIfNoResult = outputIfNoResult;
        this.rowExceptionHandler = Objects.requireNonNull(rowExceptionHandler);
    }

    @Override
    public List<CellResult> apply(Cell[] cells) {
//System.out.println("Begin processing row:" + cells[0].getRow() + ". @" + this.getClass()); 
        try{
            
            this.validate(cells);
            
            return this.process(cells);
            
        }catch(Exception e) {
            
            this.rowExceptionHandler.accept(cells, e);
            
            return this.outputIfNoResult;
        }
    }

    public List<CellResult> process(Cell [] cells) {

        final List<Integer> columnIndices = this.metaData.getExcelColumnIndices();
        
        final List<CellResult> rowResults = new ArrayList<>(columnIndices.size());
        
//System.out.println("Columns: " + columnIndices + ". @"+this.getClass());        
        for(Integer columnIndex : columnIndices) {

            final Cell cell = cells[columnIndex];

            final String [] columnNames = this.metaData.getColumnNameList(cell).toArray(new String[0]);

            final Cell [] cellParts;
            
            final Object [] cellResultParts;
            
            final Function<Cell, List<Cell>> cellSpliter = this.metaData.getCellSpliter(cell);
            
            if(cellSpliter == null) {
                
                if(this.metaData.isMultiple(cell)) {
                    
                    throw new NullPointerException("No spliter configured for cell with multiple parts");
                    
                }else{

                    final Function<Cell, Object> cellProcessor = this.cellProcessorFactory.getProcessor(cell);

                    final Object cellResult = cellProcessor.apply(cell);

                    cellParts = new Cell[0];
                    
                    cellResultParts = new Object[]{cellResult};
                }
            }else{
                
                final List<Cell> splitParts = cellSpliter.apply(cell);
                cellParts = splitParts.toArray(new Cell[0]);

                if(this.metaData.isMultiple(cell)) {

                    final List<Function<Cell, Object>> cellProcessorList = 
                            this.cellProcessorFactory.getProcessorList(cell, splitParts);
                    
                    final int MIN_SIZE = Math.min(splitParts.size(), cellProcessorList.size());
                    if(MIN_SIZE < 1) {
                        throw new UnsupportedOperationException();
                    }
                    
                    cellResultParts = new Object[splitParts.size()];

                    for(int i=0; i<MIN_SIZE; i++) {

                        cellResultParts[i] = cellProcessorList.get(i).apply(splitParts.get(i));
                    }
                }else{

                    final Function<Cell, Object> cellProcessor = this.cellProcessorFactory.getProcessor(cell);

                    cellResultParts = new Object[splitParts.size()];
                    
                    for(int i=0; i<splitParts.size(); i++) {

                        cellResultParts[i] = cellProcessor.apply(splitParts.get(i));
                    }
                }
            }

            final CellResult cellResult = new CellResultImpl(cell, cellParts, columnNames, cellResultParts);
//System.out.println("Added: " + cellResult);                        
            rowResults.add(cellResult);
        }
//System.out.println("Cells: " + cells.length + ", results: " + rowResults.size() + ". @" + this.getClass());        
        return rowResults;
    }

    public void validate(Cell [] cells) {
        
        final Collection<Integer> columnIndices = this.metaData.getExcelColumnIndices();
        
        for(Integer columnIndex : columnIndices) {
            
            final Cell cell = cells[columnIndex];
            
            final Function<Cell, List<Cell>> cellSpliter;
            
            if(this.metaData.isMultiple(cell) && 
                    (cellSpliter = this.metaData.getCellSpliter(cell)) != null) {
                
                final List<Cell> splitParts = cellSpliter.apply(cell);
                
                final List<Function<Cell, Object>> cellProcessorList = 
                        this.cellProcessorFactory.getProcessorList(cell, splitParts);
                
                this.cellSplitValidator.validate(cell, splitParts, cellProcessorList);
            }
        }
    }
}
