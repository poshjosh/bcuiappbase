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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 3:26:22 PM
 */
public class TableRowFilter implements Consumer<Predicate<Integer>> {

    private final TableRowSorter rowSorter; 

    public TableRowFilter(JTable table) {
        
        if(table.getRowSorter() == null) {
            
            rowSorter = new TableRowSorter(Objects.requireNonNull(table.getModel()));
            
            table.setRowSorter(rowSorter);
            
        }else{
            
            rowSorter = (TableRowSorter)table.getRowSorter();
        }
    }
    
    @Override
    public void accept(Predicate<Integer> rowTest) {
        
        Objects.requireNonNull(rowTest);
        
        rowSorter.setRowFilter(this.createRowFilter(rowTest));
    }
    
    public RowFilter createRowFilter(Predicate<Integer> rowTest) {
        
        final RowFilter<TableModel, Integer> filter = new RowFilter<TableModel, Integer>() {

            @Override
            public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                 
                final int rowIndex = entry.getIdentifier();
                 
                return rowTest.test(rowIndex);
            }
        };
        
        return filter;
    } 
}
