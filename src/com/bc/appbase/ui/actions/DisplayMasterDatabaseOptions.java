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
import com.bc.appbase.properties.JpaJdbcPropertiesBuilder;
import com.bc.appbase.properties.JpaJdbcPropertiesBuilderImpl;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 12, 2017 11:09:43 AM
 */
public class DisplayMasterDatabaseOptions implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Set<String> puNames = app.getPersistenceContext().getMetaData().getPersistenceUnitNames();

        for(String persistenceUnitName : puNames) {
            
            if(!app.getMasterPersistenceUnitTest().test(persistenceUnitName)) {
                continue;
            }
            
            try{
                
                final JpaJdbcPropertiesBuilder builder = new JpaJdbcPropertiesBuilderImpl(
                        app, persistenceUnitName 
                );
                
                final Properties puProps = builder.displayPromptAtLeastOnce(true).build();
                
                if(builder.getUpdateCount() > 0) {
                    
                    Logger.getLogger(this.getClass().getName()).fine(() -> "Updated properties to: " + puProps);

                    app.getUIContext().showSuccessMessage("Properties will take effect when you restart the application");
                }
                
            }catch(FileNotFoundException e) {
                
                Logger.getLogger(this.getClass().getName()).warning(() -> e.toString());
                
            }catch(IOException e) {
                
                throw new RuntimeException(e);
            }
        }
        
        return Boolean.TRUE;
    }
}
/**
 * 
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final String [] puNames = app.getJpaContext().getMetaData().getPersistenceUnitNames();

        for(String puName : puNames) {
            
            if(!app.getMasterPersistenceUnitTest().test(puName)) {
                continue;
            }
            
            final Properties puProps = new PropertiesProviderImpl(app, true).apply(puName);
            
            Logger.getLogger(this.getClass().getName()).fine(() -> "Updated properties to: " + puProps);
            
            app.getUIContext().showSuccessMessage("Properties will take effect when you restart the application");
        }
        
        return Boolean.TRUE;
    }
 * 
 */

