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
import com.bc.appbase.xls.SheetProcessorBuilder;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.appcore.ObjectFactory;
import com.bc.reflection.TypeProvider;
import com.bc.appcore.util.TextHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 8:35:14 PM
 */
public class SheetProcessorBuilderImpl<R> implements SheetProcessorBuilder<R> {

    private boolean built;
    
    private int headerRowIndex;
    
    private int dataRowOffset;
    
    private int dataRowLimit;
    
    private Class rowEntityType;
    
    private Function<Cell[], List<CellResult>> rowCellToResultProcessor;
    
    private Function<List<CellResult>, R> rowCellResultProcessor;
    
    private Function<Cell[], R> rowProcessor;
    
    private List<CellResult> rowOutputOnNoResult;
    
    private ObjectFactory objectFactory;
    
    private Map<Integer, List<String>> columnMappings;
    
    private Map<Integer, Function<Cell, List<Cell>>> spliters;
    
    private TypeProvider typeProvider;
    
    private SheetToDatabaseMetaData metaData;
    
    private TextHandler textHandler;
    
    private BiConsumer<Cell, Exception> cellExceptionHandler;
    
    private BiConsumer<Cell[], R> rowResultHandler;
    
    private CellProcessorFactory cellProcessorFactory;
    
    private CellSplitValidator cellSplitValidator;
    
    private BiConsumer<Cell[], Exception> rowExceptionHandler;

    public SheetProcessorBuilderImpl() {
        this.built = false;
        this.headerRowIndex = 0;
        this.dataRowLimit = Integer.MAX_VALUE;
        this.cellSplitValidator = CellSplitValidator.NO_OP;
    }
    
    @Override
    public Consumer<Sheet> build() {
        
        this.throwExceptionIfBuilt();
        
        this.built = true;
        
        if(this.rowProcessor == null) {
            
            if(this.metaData == null) {
                
                if(this.typeProvider == null) {
                    this.typeProvider = this.createTypeProvider(this.objectFactory);
                }
                
                this.metaData = this.createExcelDatabaseMetaData(
                        this.columnMappings, this.spliters, this.typeProvider,
                        this.rowEntityType, this.headerRowIndex,
                        this.dataRowOffset, this.dataRowLimit);
            }
            
            if(this.cellProcessorFactory == null) {
                
                if(this.textHandler == null) {
                    this.textHandler = this.createTextHandler(this.objectFactory);
                }
                
                this.cellProcessorFactory = this.createCellProcessorFactory(
                        this.metaData, this.textHandler, this.cellExceptionHandler);
            }
            
            if(this.rowProcessor == null) {
                
                if(this.rowCellToResultProcessor == null) {
                    this.rowCellToResultProcessor = this.createRowCellToResultProcessor(
                            this.cellProcessorFactory, this.metaData, this.cellSplitValidator, 
                            this.rowOutputOnNoResult, this.rowExceptionHandler);
                }
                
                Objects.requireNonNull(this.rowCellResultProcessor);

                this.rowProcessor = this.rowCellToResultProcessor.andThen(this.rowCellResultProcessor);
            }
        }
        
        Objects.requireNonNull(this.rowProcessor);
        
        final SheetProcessor sheetProcessor;
        
        if(this.metaData != null) {
            sheetProcessor = new SheetProcessor(
                    this.rowProcessor, this.rowResultHandler, this.rowExceptionHandler,
                    this.metaData.getDataRowOffset(), this.metaData.getDataRowLimit());
        }else{
            sheetProcessor = new SheetProcessor(
                    this.rowProcessor, this.rowResultHandler, this.rowExceptionHandler,
                    this.dataRowOffset, this.dataRowLimit); 
        } 
        
        return sheetProcessor;
    }

    public SheetToDatabaseMetaData createExcelDatabaseMetaData(
            Map<Integer, List<String>> mappings, 
            Map<Integer, Function<Cell, List<Cell>>> spliters,
            TypeProvider typeProvider, Class entityType, int headerIndex, int dataOffset, int dataLimit) {
        return new SheetToDatabaseMetaDataImpl(mappings, spliters, typeProvider, 
                entityType, headerIndex, dataOffset, dataLimit);
    }
    
    public TypeProvider createTypeProvider(ObjectFactory objectFactory) {
        return objectFactory.getOrException(TypeProvider.class);
    }
    
    public TextHandler createTextHandler(ObjectFactory objectFactory) {
        return objectFactory.getOrException(TextHandler.class);
    }

    public CellProcessorFactory createCellProcessorFactory(SheetToDatabaseMetaData meta, 
            TextHandler txtHandler, BiConsumer<Cell, Exception> errorHandler) {
        return new CellProcessorFactoryImpl(
                        meta, txtHandler, errorHandler);
    }
    
    public Function<Cell[], List<CellResult>> createRowCellToResultProcessor(
            CellProcessorFactory factory, SheetToDatabaseMetaData meta, CellSplitValidator splitValidator, 
            List<CellResult> outputIfNoResult, BiConsumer<Cell[], Exception> rowExceptionHandler) {
        
        return new RowProcessorImpl(factory, meta, splitValidator, 
                outputIfNoResult, rowExceptionHandler);
    }
    
    @Override
    public SheetProcessorBuilder<R> rowEntityType(Class rowEntityType) {
        this.rowEntityType = rowEntityType;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> headerRowIndex(int headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> dataRowOffset(int dataRowOffset) {
        this.dataRowOffset = dataRowOffset;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> dataRowLimit(int dataRowLimit) {
        this.dataRowLimit = dataRowLimit;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> rowCellToResultProcessor(Function<Cell[], List<CellResult>> rowCellToResultProcessor) {
        this.rowCellToResultProcessor = rowCellToResultProcessor;
        return this;
    }
    
    @Override
    public SheetProcessorBuilder<R> rowCellResultProcessor(Function<List<CellResult>, R> rowCellResultProcessor) {
        this.rowCellResultProcessor = rowCellResultProcessor;
        return this;
    }
    
    @Override
    public SheetProcessorBuilder<R> rowProcessor(Function<Cell[], R> rowProcessor) {
        this.rowProcessor = rowProcessor;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> rowOutputOnNoResult(List<CellResult> outputOnNoResult) {
        this.rowOutputOnNoResult = outputOnNoResult;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> objectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> columnMappings(Map<Integer, List<String>> columnMappings) {
        this.columnMappings = columnMappings;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> metaData(SheetToDatabaseMetaData metaData) {
        this.metaData = metaData;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> spliters(Map<Integer, Function<Cell, List<Cell>>> spliters) {
        this.spliters = spliters;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> typeProvider(TypeProvider typeProvider) {
        this.typeProvider = typeProvider;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> textHandler(TextHandler textHandler) {
        this.textHandler = textHandler;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> rowResultHandler(BiConsumer<Cell[], R> rowResultHandler) {
        this.rowResultHandler = rowResultHandler;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> cellExceptionHandler(BiConsumer<Cell, Exception> cellExceptionHandler) {
        this.cellExceptionHandler = cellExceptionHandler;
        return this;
    }

    @Override
    public SheetProcessorBuilder<R> rowExceptionHandler(BiConsumer<Cell [], Exception> rowExceptionHandler) {
        this.rowExceptionHandler = rowExceptionHandler;
        return this;
    }
    
    @Override
    public SheetProcessorBuilder<R> cellProcessorFactory(CellProcessorFactory cellProcessorFactory) {
        this.cellProcessorFactory = cellProcessorFactory;
        return this;
    }
    
    @Override
    public SheetProcessorBuilder<R> cellSplitValidator(CellSplitValidator cellSplitValidator) {
        this.cellSplitValidator = cellSplitValidator;
        return this;
    }
    
    @Override
    public boolean isBuilt() {
        return this.built;
    }
    
    public void throwExceptionIfBuilt() throws IllegalStateException {
        if(this.built) {
            throw new IllegalStateException("build() method may only be called once");
        }
    }
}
