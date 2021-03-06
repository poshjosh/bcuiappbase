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

import com.bc.appbase.ui.UIContext;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 7:32:02 PM
 */
public interface MatchExcelToDatabaseColumnsPrompt {
    
    SheetToDatabaseData execute(
            AppContext appContext, ObjectFactory objectFactory, UIContext uiContext,
            Class entityType, Sheet sheet, int headerRowIndex, int[] columnIndexes);
}
