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

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 1:19:32 PM
 */
public class CellTextExtractor extends AbstractProcessor<Cell, String> {

    public CellTextExtractor(String outputIfNoResult, BiConsumer<Cell, Exception> errorHandler) {
        super(outputIfNoResult, errorHandler);
    }

    @Override
    public String process(Cell argument) {
        return argument.getContents();
    }
}
