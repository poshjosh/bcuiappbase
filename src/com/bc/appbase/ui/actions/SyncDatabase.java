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

import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.jpa.sync.JpaSync;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.jpa.sync.MasterSlaveTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 10:29:57 PM
 */
public class SyncDatabase implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final JpaSync jpaSync = app.getJpaSync();
        
        if(jpaSync.isRunning()) {
            
            app.getUIContext().showSuccessMessage("Sync already running");
            
            return Boolean.FALSE;
            
        }else{
            
            app.getUIContext().showSuccessMessage("Running sync in background. You will be notified on completion");
            
            new Thread(this.getClass().getName()+"_Thread") {
                @Override
                public void run() {
                    
                    try{
                        
                        final List<Class> masterTypes = new ArrayList(
                                app.getOrException(MasterSlaveTypes.class).getMasterTypes()
                        );
                        Collections.sort(masterTypes, app.getEntityOrderComparator());
                        
                        app.getPendingMasterUpdatesManager().pause();
                        app.getPendingSlaveUpdatesManager().pause();

                        if(!jpaSync.isRunning()) {

                            jpaSync.sync(masterTypes.toArray(new Class[0]));

                            app.getUIContext().showSuccessMessage("Sync successful");
                            
                        }else{
                            
                            app.getUIContext().showSuccessMessage("Sync already running");
                        }
                    }catch(RuntimeException e) {
                        
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception syncing", e);
                        
                        app.getUIContext().showErrorMessage(e, "Sync failed");
                        
                    }finally{
                        app.getPendingMasterUpdatesManager().pause();
                        app.getPendingSlaveUpdatesManager().resume();
                    }
                }
            }.start();

            return Boolean.TRUE;
        }
    }
}
