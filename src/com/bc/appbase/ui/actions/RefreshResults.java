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


import com.bc.appbase.ui.SearchResultsPanel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appbase.ui.MainFrame;
import java.awt.Container;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:44:57 AM
 */
public class RefreshResults implements Action<App, Object> {
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {
        
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run() {
                try{
                    
                    final SearchResultsPanel resultsPanel = (SearchResultsPanel)params.get(SearchResultsPanel.class.getName());
                    
                    final Container c = resultsPanel.getTopLevelAncestor();
                    
                    if(c instanceof MainFrame) {
                        ((MainFrame)c).reset(app);
                    }
                    
                }catch(RuntimeException e) {
                    
                    final String message = "An unexpected error occured";
                    
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, message, e);
                    
                    app.getUIContext().showErrorMessage(e, message);
                }
            }
        });
        
        return Boolean.TRUE;
    }
}


