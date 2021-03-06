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

import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 1:00:51 PM
 */
public interface SelectFileThenWorksheetPrompt {

    Sheet execute(Sheet outputIfNone);

    File promptSelectExcelFile() throws ParameterException, TaskExecutionException;

    String promptSelectSheetName(Workbook workbook) throws ParameterException, TaskExecutionException;

}
