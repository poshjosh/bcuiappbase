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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 2:12:25 PM
 */
public interface SheetProcessorContext {

    Consumer<Sheet> getSheetRowToEntityProcessor(
            Sheet sheet, BiConsumer<Cell[], List> rowResultHandler,
            BiConsumer<Cell, Exception> cellExceptionHandler, 
            BiConsumer<Cell[], Exception> rowExceptionHandler,
            Consumer<Sheet> outputIfNone);
    
    MatchExcelToDatabaseColumnsPrompt getMatchExcelToDatabaseColumnsPrompt();
    
    Function<List<CellResult>, List> getEntityListFromRowResultsBuilder(Class entityType);

    SelectFileThenWorksheetPrompt getSelectFileThenWorksheetPrompt();
    
    SheetToDatabaseMetaDataBuilder getSheetToDatabaseMetaDataBuilder();
  
    <R> SheetProcessorBuilder<R> getSheetProcessorBuilder(Class<R> rowResultType);
}
