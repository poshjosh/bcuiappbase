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
import java.util.function.Function;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 1:40:04 PM
 */
public interface CellProcessorFactory {

    List<Function<Cell, Object>> getProcessorList(Cell cell, List<Cell> splitParts);
    
    Function<Cell, Object> getProcessor(Cell cell);
    
    Function<Cell, Object> getProcessor(Class columnType);
}
