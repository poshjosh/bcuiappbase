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

import com.bc.appbase.App;
import com.bc.appbase.ui.FrameForTable;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.table.model.EntityTableModelImpl;
import com.bc.appcore.util.RelationAccess;
import com.bc.jpa.paging.PaginatedList;
import com.bc.jpa.search.SearchResults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.bc.appcore.table.model.XYCountTableMetaData;
import com.bc.appcore.jpa.model.EntityResultModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 7, 2017 5:46:30 PM
 */
public class XYCountTableCellSelectionHandler implements TableCellSelectionHandler {
    
    private static class EntityFilter implements Predicate {

        private final Predicate<Class> recursionFilter;
        private final RelationAccess relationAccess;
        private final Class valueTypeToFind;
        private final List valuesToFind;
        
        private EntityFilter(RelationAccess relationAccess, Class valueTypeToFind, List valuesToFind) {
            this.recursionFilter = (cls) -> cls.getAnnotation(Entity.class) != null;   
            this.relationAccess = Objects.requireNonNull(relationAccess);
            this.valueTypeToFind = Objects.requireNonNull(valueTypeToFind);
            this.valuesToFind = Objects.requireNonNull(valuesToFind);
        }
        
        @Override
        public boolean test(Object entity) {

            final boolean accept;

            final Set valuesFromEntity = this.relationAccess.getDistinctChildren(
                    entity, this.valueTypeToFind, this.recursionFilter, true);

            if(valuesFromEntity == null || valuesFromEntity.isEmpty()) {

                accept = false;

            }else{

                accept = valuesFromEntity.containsAll(this.valuesToFind);
            }

            return accept;
        }
    }

    private static final Logger logger = Logger.getLogger(XYCountTableCellSelectionHandler.class.getName());
    
    private int previousRow = -1;
    private int previousCol = -1;
    
    private final AtomicBoolean busy;
    
    private final App app;
    
    private final XYCountTableMetaData tableMetaData;
    
    private final RelationAccess relationAccess;
    
    public XYCountTableCellSelectionHandler(App app, XYCountTableMetaData tableMetaData) {
        this.app = Objects.requireNonNull(app);
        this.tableMetaData = Objects.requireNonNull(tableMetaData);
        this.busy = new AtomicBoolean();
        this.relationAccess = app.getOrException(RelationAccess.class);
    }
    
    @Override
    public void onCellSelected(JTable table, int row, int column) {
        
        try{
            
            if(busy.get()) {
                return;
            }
            
            busy.set(true);
            
            if(row == -1 || column == -1) {
                return;
            }
            
            if(row == previousRow && column == previousCol) {
                return;
            }
            
            previousRow = row;
            previousCol = column;
            
            row = table.convertRowIndexToModel(row);
            column = table.convertColumnIndexToModel(column);
            
            final Object selectedValue = table.getModel().getValueAt(row, column);

            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "- - - - - - - Selected [{0}:{1}] with value: {2}\nX-axis values count: {3}\nY-axis values count: {4}", 
                        new Object[]{row, column, selectedValue, 
                        this.tableMetaData.getXValues().size(),
                        this.tableMetaData.getYValues().size()});
            }

            final boolean isSumRow = row == tableMetaData.getSumRowIndex();
            final boolean isSumCol = column == tableMetaData.getSumColumnIndex();
            
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Sum row: {0}, sum col: {1}", new Object[]{isSumRow, isSumCol});
            }
            
            if(isSumRow && isSumCol) {
                
                return;
            }
                
            final Predicate rowTest = isSumRow ? this.getTest(this.tableMetaData.getyEntityType()) : 
                        this.getTest(this.tableMetaData.getyEntityType(), this.tableMetaData.getYValues(), row);
                        
            final Predicate colTest = isSumCol ? this.getTest(this.tableMetaData.getxEntityType()) :
                        this.getTest(this.tableMetaData.getxEntityType(), this.tableMetaData.getXValues(), column - 1);

            final List resultList = this.find(this.tableMetaData.getSearchResults(), colTest.and(rowTest));

            final EntityResultModel resultModel = tableMetaData.getResultModel();
            
            final TableModel tableModel = new EntityTableModelImpl(resultList, resultModel);
            final JTable resultsTable = new JTable(tableModel);
            
            final FrameForTable frame = new FrameForTable(tableModel.getRowCount() + " results");
            
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            final UIContext uiContext = app.getUIContext();
            
            frame.init(uiContext, resultsTable);
            
            uiContext.positionHalfScreenRight(frame);
            
            uiContext.updateTableUI(resultsTable, resultModel.getEntityType(), resultModel.getSerialColumnIndex());
           
            frame.pack();
            
            frame.setVisible(true);
            
        }finally{
            
            busy.set(false);
        }
    }
    
    public Predicate getTest(Class entityType) {
        
        return (val) -> true;
    }
    
    public Predicate getTest(Class entityType, List axisValues, int index) {
        
        final List results = this.getResultList(axisValues, index);
        
        return new EntityFilter(this.relationAccess, entityType, results);
    }
    
    public List getResultList(List axisValues, int index) {
        
        List resultList;
        
        final Object value = index == -1 ? null : axisValues.get(index);
        
        if(value == null) {
            
            resultList = Collections.EMPTY_LIST;
            
        }else{
            
            resultList = Collections.singletonList(value);
        }
        
        return resultList;
    }
    
    public List find(SearchResults searchResults, Predicate test) {
    
        final List output = new ArrayList();
        
        final PaginatedList pages = searchResults.getPages();
        
        for(Object entity : pages) {
            
            final boolean accepted = test.test(entity);
            
            if(accepted) {
                
                output.add(entity);
            }
        }
        
        return output;
    }
}
/**
 * 
            
            if(isSumRow && isSumCol) {
                return;
            }else if(isSumRow) {
                rowTest = this.getTest(this.tableMetaData.getyEntityType());
                colTest = this.getTest(this.tableMetaData.getxEntityType(), 
                        this.tableMetaData.getXValues(), column - 1);
            }else if(isSumCol) {
                rowTest = this.getTest(this.tableMetaData.getyEntityType(), 
                        this.tableMetaData.getYValues(), row);
                colTest = this.getTest(this.tableMetaData.getxEntityType());
            }else{
                rowTest = this.getTest(this.tableMetaData.getyEntityType(), 
                        this.tableMetaData.getYValues(), row);
                colTest = this.getTest(this.tableMetaData.getxEntityType(), 
                        this.tableMetaData.getXValues(), column - 1);
            }
 * 
 */