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

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 3, 2017 3:00:35 PM
 */
public interface SheetToDatabaseData {

    boolean isMultiple(Cell cell);

    List<String> getColumnNameList(Cell cell);
    
    Function<Cell, List<Cell>> getCellSpliter(Cell cell);
    
    String getColumnName(Cell cell);

    List<Integer> getExcelColumnIndices();
    
    List<Integer> getMultiValueCellIndices();

    Collection<List<String>> getColumnNames();
    
    Collection<Function<Cell, List<Cell>>> getCellSpliters();
}
