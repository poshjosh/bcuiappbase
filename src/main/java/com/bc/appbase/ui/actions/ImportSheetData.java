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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appbase.ui.ScreenLog;
import com.bc.appbase.xls.SheetProcessorContext;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 3, 2017 1:35:47 PM
 */
public class ImportSheetData implements Action<App, Integer> {

    private static final Logger logger = Logger.getLogger(ImportSheetData.class.getName());
    
    private final ScreenLog screenLog = new ScreenLog("Excel-Import", "Importing Excel Data");
        
    private final AtomicInteger updateCount = new AtomicInteger(0);
    
    private class InsertEntityListToDatabase implements BiConsumer<Cell[], List> {
        
        private final App app;
        
        public InsertEntityListToDatabase(App app) {
            this.app = Objects.requireNonNull(app);
        }
        
        @Override
        public void accept(Cell [] row, List entities) {
            try{

                final Map<String, Object> insertParams = new HashMap(4, 0.75f);
                insertParams.put("entities", entities);
                this.app.getAction(ActionCommands.UPDATE_DATABASE_WITH_ENTITIES).execute(this.app, insertParams);
                
                updateCount.incrementAndGet();
                
                final String success = "@row: "+row[0].getRow()+", SUCCESS";
                logger.fine(success);
                screenLog.log(success);
                
            }catch(ParameterException | TaskExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Integer execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        try{
            
            app.getUIContext().positionHalfScreenLeft(screenLog.getFrame());
            
            screenLog.log("...Beginning import");
            
            updateCount.set(0);

//        app.registerDefault(SheetProcessorContext.class, () -> new PdmSheetProcessorContextDevMode(app));

            final SheetProcessorContext context = app.getOrException(SheetProcessorContext.class);

            final Sheet sheet = context.getSelectFileThenWorksheetPrompt().execute(app, null);

            if(sheet != null) {
                
                screenLog.log("Selected worksheet: " + sheet.getName());

                final BiConsumer<Cell[], List> insertEntitiesToDatabase = new InsertEntityListToDatabase(app);

                final BiConsumer<Cell, Exception> cellExceptionHandler = (cell, exception) -> {
                    final String warningMessage = "@["+cell.getRow()+':'+cell.getColumn()+"] encountered exception: " + exception;
                    logger.warning(warningMessage);
                    screenLog.log(warningMessage);
                };
                
                final BiConsumer<Cell[], Exception> rowExceptionHandler = (cells, exception) -> {
                    final String msgPrefix = "@row: "+cells[0].getRow();
                    logger.log(Level.WARNING, msgPrefix, exception);
                    final StringWriter strwriter = new StringWriter();
                    try(PrintWriter prtwriter = new PrintWriter(strwriter)) {
                        exception.printStackTrace(prtwriter);
                        prtwriter.flush();
                        screenLog.log(strwriter.getBuffer().toString());
                    }
                };

                final Consumer<Sheet> sheetProcessor = context.getSheetRowToEntityProcessor(
                        sheet, insertEntitiesToDatabase, cellExceptionHandler, rowExceptionHandler, null);

                if(sheetProcessor != null) {
                    
                    screenLog.init();

                    sheetProcessor.accept(sheet);

                    screenLog.log("\nCOMPLETED");
                }
            }

            return updateCount.get();
            
        }finally{
            if(screenLog.getFrame().isVisible()) {
                try{
                    screenLog.querySaveLogThenSave("import");
                }finally{
                    screenLog.destroy();
                }
            }
        }
    }
}
