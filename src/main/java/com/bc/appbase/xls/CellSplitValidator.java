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
 * @author Chinomso Bassey Ikwuagwu on May 16, 2017 10:48:51 AM
 */
public interface CellSplitValidator {
    
    CellSplitValidator NO_OP = (cell, cellParts, cellProcessors) -> {};
    
    CellSplitValidator STRICT_SIZE_MATCH = (cell, cellParts, cellProcessors) -> {
        if(cellParts.size() != cellProcessors.size()) {
            throw new RuntimeException("Size mis-match. Cell parts: "+cellParts.size()+", processors: "+cellProcessors.size());
        }
    };

    void validate(Cell cell, List<Cell> cellParts, List<Function<Cell, Object>> cellProcessors);
}
