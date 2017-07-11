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

import java.util.function.BiConsumer;
import jxl.Cell;
import jxl.write.biff.NumberRecord;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 2:11:48 PM
 */
public class CellNumberExtractor extends AbstractProcessor<Cell, Double> {

    public CellNumberExtractor(Double outputIfNoResult, BiConsumer<Cell, Exception> errorHandler) {
        super(outputIfNoResult, errorHandler);
    }

    @Override
    public Double process(Cell cell) {
        
        return this.getNumber(cell);
    }

    public double getNumber(Cell cell) {
        
        final Double output;
        
        if(cell instanceof NumberRecord) {
            output = ((NumberRecord)cell).getValue();
        }else{
            final String cellContents = cell.getContents();
            output = cellContents == null || cellContents.isEmpty() ? null : Double.valueOf(cellContents);
        }
        
        return output;
    }
}

