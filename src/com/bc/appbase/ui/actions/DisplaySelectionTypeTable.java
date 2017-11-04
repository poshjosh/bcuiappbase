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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appbase.ui.SearchResultsFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.util.List;
import java.util.Map;
import com.bc.appcore.table.model.EntityTableModel;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.search.ListSearchResults;
import com.bc.jpa.search.SearchResults;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 8, 2017 9:43:12 PM
 */
public class DisplaySelectionTypeTable implements Action<App, Object> {

    private static final Logger logger = Logger.getLogger(DisplaySelectionTypeTable.class.getName());
    
    @Override
    public Object execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Class<?> entityType;
        if(params.get(ParamNames.ENTITY_TYPE) == null) {
            entityType = (Class)app.getAction(ActionCommands.PROMPT_SELECT_SELECTION_TYPE).execute(app, params);
        }else{
            entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        }
        
        logger.fine(() -> "Entity type: " +  entityType);
        
        if(entityType == null) {
            
            return null;
        }
        
        final PersistenceUnitContext puContext = app.getActivePersistenceUnitContext();
        
        final List results = puContext
                .getDao().forSelect(entityType).from(entityType)
                .getResultsAndClose();
       
        logger.fine(() -> "Search results size: " +  (results == null ? "null" : results.size()));
        
        final SearchResultsFrame frame = new SearchResultsFrame();
        
        final SearchResultsPanel panel = frame.getSearchResultsPanel();
        final String panelName = this.getClass().getName()+'_'+panel.getClass().getSimpleName(); 
        panel.setName(panelName);
        
        final UIContext uiContext = app.getUIContext();
        
        panel.init(uiContext);
        
        final SearchResults searchResults = new ListSearchResults(results, 10);
        
        panel.load(app.getSearchContext(entityType), searchResults, app.getRandomId());
        
        uiContext.positionFullScreen(frame);
        
        final JTable table = panel.getSearchResultsTable();
        table.setName(panelName + '_' + table.getClass().getSimpleName());
        
        final int serialColumnIndex = ((EntityTableModel)table.getModel()).getResultModel().getSerialColumnIndex();
        
        uiContext.updateTableUI(table, entityType, serialColumnIndex);

        frame.setVisible(true);
        
        return Boolean.TRUE;
    }
}
/**
 * 

//    @Override
    public Object execute_old(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Class entityType;
        if(params.get(ParamNames.ENTITY_TYPE) == null) {
            entityType = (Class)app.getAction(ActionCommands.PROMPT_SELECT_SELECTION_TYPE).execute(app, params);
        }else{
            entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        }
        
        if(entityType == null) {
            
            return null;
        }
        
        final JpaContext jpaContext = app.getJpaContext();
        
        final List results = jpaContext
                .getBuilderForSelect(entityType)
                .getResultsAndClose();
        
        final int serialColumnIndex = -1;
        
        final EntityResultModel resultModel = app.getResultModel(entityType, serialColumnIndex, null);
        
        
//        final List<String> columnNames = Arrays.asList(jpaContext.getMetaData().getColumnNames(entityType));
        
//        final EntityResultModel resultModel = new EntityResultModelImpl(
//                app, entityType, columnNames, serialColumnIndex,
//                new PromptUserUpdate(app.getUIContext()), 
//                new PromptException(app.getUIContext())
//        );
        
        final String idColumnName = jpaContext.getMetaData().getIdColumnName(entityType);
        
        final TableModel tableModel = new EntityTableModelImpl(results, resultModel){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                final String columnName = this.getColumnName(columnIndex);
                if(columnName.equals(idColumnName)) {
                    return false;
                }else{
                    return super.isCellEditable(rowIndex, columnIndex); 
                }
            }
        };
        
        final String FRAME_TITLE = "Displaying " + entityType.getSimpleName() + 's';
        
        final FrameBuilder frameBuilder = new FrameBuilder(app.getUIContext()){
            @Override
            public JFrame newFrame() {
                return new JFrame(FRAME_TITLE);
            }
        };
        
        final JFrame frame = frameBuilder.build(tableModel, entityType, serialColumnIndex);
        
        frame.setVisible(true);
        
        return Boolean.TRUE;
    }
 * 
 */