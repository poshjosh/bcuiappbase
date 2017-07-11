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

package com.bc.appbase.ui.table;

import com.bc.appbase.ui.UIContext;
import java.awt.Color;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 7, 2017 8:18:03 PM
 */
public class TableFormat {
    
    private final UIContext uiContext;

    public TableFormat() {
        this(null);
    }
    
    public TableFormat(UIContext uiContext) {
        this.uiContext = uiContext;
    }

    public void format(JTable table) {
        
        if(uiContext != null) {
            uiContext.setTableFont(table);
        }
        
//        table.setIntercellSpacing(new Dimension(4, 4));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setShowGrid(true);
        table.setGridColor(Color.DARK_GRAY);

        table.setAutoCreateRowSorter(true);
    }
}
