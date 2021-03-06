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

package com.bc.appbase.ui.table.model;

import com.bc.appbase.App;
import com.bc.appcore.table.model.TableModelDisplayFormat;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 8, 2017 10:11:04 AM
 */
public class TableModelDisplayFormatImpl<T> implements TableModelDisplayFormat<T> {

    private final TableCellDisplayFormat<T> tableCellDisplayFormat;

    public TableModelDisplayFormatImpl(App app, int serialColumnIndex) {
        this(app.getUIContext().getTableCellDisplayFormat(serialColumnIndex));
    }
    
    public TableModelDisplayFormatImpl(TableCellDisplayFormat<T> tableCellDisplayFormat) {
        this.tableCellDisplayFormat = Objects.requireNonNull(tableCellDisplayFormat);
    }
    
    @Override
    public Object fromDisplayValue(Class columnClass, T displayValue, int row, int column) {
        return tableCellDisplayFormat.fromDisplayValue(columnClass, displayValue, row, column);
    }

    @Override
    public T toDisplayValue(Class columnClass, Object value, int row, int column) {
        return tableCellDisplayFormat.toDisplayValue(columnClass, value, row, column);
    }
}
