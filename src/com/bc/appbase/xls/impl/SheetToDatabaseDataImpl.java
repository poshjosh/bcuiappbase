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

import com.bc.appbase.xls.SheetToDatabaseData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 3, 2017 2:56:46 PM
 */
public class SheetToDatabaseDataImpl implements SheetToDatabaseData {

    private static final Logger logger = Logger.getLogger(SheetToDatabaseDataImpl.class.getName());

    private final Map<Integer, List<String>> excelToDbColumns;
    
    private final Map<Integer, Function<Cell, List<Cell>>> spliters;
    
    public SheetToDatabaseDataImpl(
            Map<Integer, List<String>> excelToDbColumns, 
            Map<Integer, Function<Cell, List<Cell>>> spliters) {
        this.excelToDbColumns = Objects.requireNonNull(excelToDbColumns);
        this.spliters = Objects.requireNonNull(spliters);
    }

    @Override
    public boolean isMultiple(Cell cell) {
        return this.spliters.get(cell.getColumn()) != null;
    }

    @Override
    public List<String> getColumnNameList(Cell cell) {
        return Collections.unmodifiableList(this.excelToDbColumns.get(cell.getColumn()));
    }
    
    @Override
    public Function<Cell, List<Cell>> getCellSpliter(Cell cell) {
        return this.spliters.get(cell.getColumn());
    }
    
    @Override
    public String getColumnName(Cell cell) {
        if(this.isMultiple(cell)) {
            throw new IllegalArgumentException("@["+cell.getRow()+':'+cell.getColumn()+
                    "], expected One column, but found "+this.getColumnNameList(cell).size()+" columns");
        }
        return this.getColumnNameList(cell).get(0);
    }

    @Override
    public List<Integer> getExcelColumnIndices() {
        return Collections.unmodifiableList(new ArrayList(this.excelToDbColumns.keySet()));
    }
    
    @Override
    public List<Integer> getMultiValueCellIndices() {
        return Collections.unmodifiableList(new ArrayList(this.spliters.keySet()));
    }

    @Override
    public Collection<List<String>> getColumnNames() {
        return Collections.unmodifiableList(new ArrayList(this.excelToDbColumns.values()));
    }
    
    @Override
    public Collection<Function<Cell, List<Cell>>> getCellSpliters() {
        return Collections.unmodifiableCollection(new ArrayList(this.spliters.values()));
    }
}
