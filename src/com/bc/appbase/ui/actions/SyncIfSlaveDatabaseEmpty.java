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
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 7, 2017 8:22:44 PM
 */
public class SyncIfSlaveDatabaseEmpty implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final JpaContext jpa = app.getJpaContext();
        
        final JpaMetaData metaData = jpa.getMetaData();
        
        final String [] puNames = metaData.getPersistenceUnitNames();
        
        final Predicate<String> slaveTest = app.getSlavePersistenceUnitTest();
        
        boolean sync = true;
        
        for(String puName : puNames) {
            
            if(!slaveTest.test(puName)) {
            
                continue;
            }
            
            final Class [] puTypes = metaData.getEntityClasses(puName);
            
            for(Class entityType : puTypes) {
                
                final Long count = this.count(jpa, entityType);
                
                if(count > 0) {
                    
                    sync = false;
                    
                    break;
                }
            }
        }
        
        final boolean output;
        
        if(sync) {
        
            output = (Boolean)app.getAction(ActionCommands.SYNC_DATABASE).execute(app, params);
            
        }else{
            
            output = false;
        }
        
        return output;
    }
    
    public Long count(JpaContext jpa, Class puClass) {
        return jpa.getBuilderForSelect(puClass, Long.class)
                .from(puClass).count().getSingleResultAndClose();
    }
}
