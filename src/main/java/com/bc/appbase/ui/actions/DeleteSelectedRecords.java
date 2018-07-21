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
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.dao.Dao;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 3:37:57 PM
 */
public class DeleteSelectedRecords implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(DeleteSelectedRecords.class.getName());

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Boolean output;
        
        final Class entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        Objects.requireNonNull(entityType);

        final String idColumnName = app.getActivePersistenceUnitContext().getMetaData().getIdColumnName(entityType);
        final List idsList = (List)params.get(idColumnName+"List");
        
        if(idsList == null || idsList.isEmpty()) {
            
            app.getUIContext().showErrorMessage(null, "You did not select anything");
            
            output = Boolean.FALSE;
            
        }else{
            
            final int selection = JOptionPane.showConfirmDialog(app.getUIContext().getMainFrame(), 
                    "Are you sure you want to delete the selected records(s)?", "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(selection == JOptionPane.YES_OPTION) {

                for(Object id : idsList) {
                    
                    logger.fine(() -> "Deleting: entity with ID: " + id + ", of type: " + entityType.getName());

                    try(Dao dao = app.getDao()) {

                        final Object managedEntity = dao.find(entityType, id);
                        Objects.requireNonNull(managedEntity);
                        
                        logger.fine(() -> "Deleting: managed entity: " + managedEntity);

                        dao.begin().remove(managedEntity).commit();
                    }
                }

                app.getUIContext().showSuccessMessage("Success");
                
                app.getAction(ActionCommands.REFRESH_ALL_RESULTS).executeSilently(app);

                output = Boolean.TRUE;

            }else{

                output = Boolean.FALSE;
            }
        }
        
        return output;
    }
}
