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
import com.bc.appbase.ui.table.model.EntityTableModel;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.jpa.model.ResultModelImpl;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.JpaContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 8, 2017 9:43:12 PM
 */
public class DisplaySelectionTypeTable implements Action<App, Object> {
    
    @Override
    public Object execute(App app, Map<String, Object> params) 
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
        
        final List<String> columnNames = Arrays.asList(jpaContext.getMetaData().getColumnNames(entityType));
        
        final ResultModel resultModel = new ResultModelImpl(
                app, entityType, columnNames, serialColumnIndex);
        
        final String idColumnName = jpaContext.getMetaData().getIdColumnName(entityType);
        
        final TableModel tableModel = new EntityTableModel(results, resultModel){
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
}
