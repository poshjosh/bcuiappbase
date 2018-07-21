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

package com.bc.appbase.xls;

import com.bc.appcore.ObjectFactory;
import com.bc.reflection.TypeProvider;
import com.bc.appcore.util.TextHandler;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 8:42:22 PM
 */
public interface SheetProcessorBuilder<R> {

    Consumer<Sheet> build();

    boolean isBuilt();
    
    SheetProcessorBuilder<R> dataRowOffset(int dataRowOffset);
    
    SheetProcessorBuilder<R> dataRowLimit(int dataRowLimit);

    SheetProcessorBuilder<R> headerRowIndex(int headerRowIndex);
    
    SheetProcessorBuilder<R> rowEntityType(Class entityType);

    SheetProcessorBuilder<R> rowCellToResultProcessor(Function<Cell[], List<CellResult>> rowProcessor);
    
    SheetProcessorBuilder<R> rowCellResultProcessor(Function<List<CellResult>, R> rowProcessor);
    
    SheetProcessorBuilder<R> rowProcessor(Function<Cell[], R> rowProcessor);
    
    SheetProcessorBuilder<R> rowOutputOnNoResult(List<CellResult> outputIfNoResult);

    SheetProcessorBuilder<R> objectFactory(ObjectFactory objectFactory);

    SheetProcessorBuilder<R> cellProcessorFactory(CellProcessorFactory cellProcessorFactory);
    
    SheetProcessorBuilder<R> cellSplitValidator(CellSplitValidator cellSplitValidator);

    SheetProcessorBuilder<R> columnMappings(Map<Integer, List<String>> columnMappings);

    SheetProcessorBuilder<R> cellExceptionHandler(BiConsumer<Cell, Exception> cellExceptionHandler);

    SheetProcessorBuilder<R> metaData(SheetToDatabaseMetaData metaData);
    
    SheetProcessorBuilder<R> rowExceptionHandler(BiConsumer<Cell[], Exception> rowExceptionHandler);
    
    SheetProcessorBuilder<R> spliters(Map<Integer, Function<Cell, List<Cell>>> spliters);

    SheetProcessorBuilder<R> rowResultHandler(BiConsumer<Cell[], R> rowResultHandler);

    SheetProcessorBuilder<R> textHandler(TextHandler textHandler);
    
    SheetProcessorBuilder<R> typeProvider(TypeProvider typeProvider);
}
