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

package com.bc.appbase.ui;

import com.bc.appcore.jpa.model.ResultModel;
import com.bc.table.cellui.ColumnWidths;
import java.util.Date;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 1:51:59 PM
 */
public class ColumnWidthsImpl implements ColumnWidths {
    
    private final ResultModel resultModel;

    public ColumnWidthsImpl(ResultModel tableModel) {
        this.resultModel = Objects.requireNonNull(tableModel);
    }
    
    @Override
    public int getColumnPreferredWidthInChars(int columnIndex) {
        
        final Class aClass = resultModel.getColumnClass(columnIndex);

        final int widthInChars;
        if(aClass == Long.class || aClass == Integer.class || aClass == Short.class) {
            widthInChars = 4;
        }else if(aClass == Date.class) {
            widthInChars = 14;
        }else{
            final String colName = resultModel.getColumnName(columnIndex);
            if("Remarks".equalsIgnoreCase(colName)) {
                widthInChars = 11;
            }else{
                widthInChars = 30;
            }
        }

        return widthInChars;
    }
}
