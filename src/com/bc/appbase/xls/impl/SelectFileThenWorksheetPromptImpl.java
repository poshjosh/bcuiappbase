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
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.bc.appbase.xls.SelectFileThenWorksheetPrompt;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 9:51:49 AM
 */
public class SelectFileThenWorksheetPromptImpl implements SelectFileThenWorksheetPrompt {
    
    private final App app;

    public SelectFileThenWorksheetPromptImpl(App app) {
        this.app = Objects.requireNonNull(app);
    }
    
    @Override
    public Sheet execute(Sheet outputIfNone) {
        
        try{
            
            final File file = this.promptSelectExcelFile();

            if(file == null) {
                return outputIfNone;
            }

            final Workbook workbook = Workbook.getWorkbook(file);

            final String sheetName = this.promptSelectSheetName(workbook);

            if(sheetName == null) {
                return outputIfNone;
            }

            final Sheet sheet = workbook.getSheet(sheetName);
            
            return sheet;
            
        }catch(ParameterException | TaskExecutionException | IOException | BiffException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File promptSelectExcelFile() throws ParameterException, TaskExecutionException {
        final File file = (File)app.getAction(
        ActionCommands.PROMPT_SELECT_EXCEL_FILE).execute(app, Collections.EMPTY_MAP);
        return file;
    }
    
    @Override
    public String promptSelectSheetName(Workbook workbook) 
            throws ParameterException, TaskExecutionException {
        final String sheetName = (String)app.getAction(ActionCommands.PROMPT_SELECT_SHEETNAME).execute(
                app, Collections.singletonMap(Workbook.class.getName(), workbook));
        return sheetName;
    }

}
