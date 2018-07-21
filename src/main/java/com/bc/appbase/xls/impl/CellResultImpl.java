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

import com.bc.appbase.xls.CellResult;
import java.util.Arrays;
import java.util.Objects;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 4:07:11 PM
 */
public class CellResultImpl implements CellResult {

    private final Cell cell;
    private final Cell [] cellParts;
    private final String [] columnNames;
    private final Object [] multipleResults;

    public CellResultImpl(Cell cell, Cell[] cellParts, String [] columnNames, Object[] resultParts) {
        this.cell = Objects.requireNonNull(cell);
        this.cellParts = Objects.requireNonNull(cellParts);
        this.columnNames = Objects.requireNonNull(columnNames);
        this.multipleResults = Objects.requireNonNull(resultParts);
    }

    @Override
    public boolean isMultiResultCell() {
        return cellParts != null && this.cellParts.length > 1;
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    public String getDatabaseColumnName() {
        if(this.isMultiResultCell()) {
            throw new UnsupportedOperationException("Method not supported for Multi-result " + CellResult.class.getSimpleName());
        }
        return this.columnNames[0];
    }

    @Override
    public Object getSingleResult() {
        if(this.isMultiResultCell()) {
            throw new UnsupportedOperationException("Method not supported for Multi-result " + CellResult.class.getSimpleName());
        }
        return this.multipleResults[0];
    }

    @Override
    public Cell[] getCellParts() {
        if(!this.isMultiResultCell()) {
            throw new UnsupportedOperationException("Method not supported for Single-result " + CellResult.class.getSimpleName());
        }
        return this.cellParts;
    }

    @Override
    public String[] getDatabaseColumnNames() {
        return this.columnNames;
    }

    @Override
    public Object[] getMultipleResults() {
        return this.multipleResults;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{[" + cell.getRow() + ':' + cell.getColumn() + 
                "] database columns: " + Arrays.toString(this.columnNames) + 
                ", results: " + this.multipleResults.length + '}';
    }
}
