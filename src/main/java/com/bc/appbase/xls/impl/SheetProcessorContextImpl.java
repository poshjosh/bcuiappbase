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

import com.bc.appbase.App;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.xls.CellResult;
import com.bc.appbase.xls.MatchExcelToDatabaseColumnsPrompt;
import com.bc.appbase.xls.SelectFileThenWorksheetPrompt;
import com.bc.appbase.xls.SheetProcessorBuilder;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.appbase.xls.SheetToDatabaseMetaDataBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;
import com.bc.appbase.xls.SheetProcessorContext;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 6:24:21 PM
 */
public class SheetProcessorContextImpl implements SheetProcessorContext {

    private final AppContext appContext;
    private final ObjectFactory objectFactory;
    private final UIContext uiContext;

    public SheetProcessorContextImpl(App app) {
        this(app, app, app.getUIContext());
    }

    public SheetProcessorContextImpl(
            AppContext appContext, ObjectFactory objectFactory, UIContext uiContext) {
        this.appContext = Objects.requireNonNull(appContext);
        this.objectFactory = Objects.requireNonNull(objectFactory);
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    @Override
    public Consumer<Sheet> getSheetRowToEntityProcessor(
            Sheet sheet, BiConsumer<Cell[], List> rowResultHandler, 
            BiConsumer<Cell, Exception> cellExceptionHandler, 
            BiConsumer<Cell[], Exception> rowExceptionHandler,
            Consumer<Sheet> outputIfNone) {
        
        final Consumer<Sheet> output;
        
        final SheetToDatabaseMetaData metaData = this.getSheetToDatabaseMetaDataBuilder().build(sheet, null);
        
        if(metaData == null) {
            
            output = outputIfNone;
            
        }else{
            
            final Function<List<CellResult>, List> rowCellResultProcessor = 
                    this.getEntityListFromRowResultsBuilder(metaData.getRowEntityType());

            output = this.getSheetProcessorBuilder(List.class)
                    .objectFactory(objectFactory)
                    .metaData(metaData)
                    .rowCellResultProcessor(rowCellResultProcessor)
                    .rowResultHandler(rowResultHandler)
                    .rowExceptionHandler(rowExceptionHandler)
    //                .cellSplitValidator(CellSplitValidator.STRICT_SIZE_MATCH)
                    .cellExceptionHandler(cellExceptionHandler)
                    .build();
        }
        
        return output;
    }

    @Override
    public MatchExcelToDatabaseColumnsPrompt getMatchExcelToDatabaseColumnsPrompt() {
        return new MatchExcelToDatabaseColumnsPromptImpl();
    }

    @Override
    public Function<List<CellResult>, List> getEntityListFromRowResultsBuilder(Class entityType) {
        return new BuildEntitiesFromSheetRow(this.objectFactory, entityType);
    }

    @Override
    public SelectFileThenWorksheetPrompt getSelectFileThenWorksheetPrompt() {
        return new SelectFileThenWorksheetPromptImpl();
    }

    @Override
    public SheetToDatabaseMetaDataBuilder getSheetToDatabaseMetaDataBuilder() {
        return new SheetToDatabaseMetaDataBuilderFromUserInputsImpl(
                this.appContext, this.objectFactory, this.uiContext
        );
    }

    @Override
    public <R> SheetProcessorBuilder<R> getSheetProcessorBuilder(Class<R> rowResultType) {
        return new SheetProcessorBuilderImpl<>();
    }

    public AppContext getAppContext() {
        return appContext;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public UIContext getUiContext() {
        return uiContext;
    }
}
