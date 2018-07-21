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


import com.bc.appcore.actions.Action;
import java.util.Map;
import javax.swing.JOptionPane;
import com.bc.appbase.App;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 7:33:54 PM
 */
public class ExitUIThenExit implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final int selection = JOptionPane.showConfirmDialog(
                app.getUIContext().getMainFrame(), "Are you sure you want to exit?", 
                "Exit?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if(selection == JOptionPane.YES_OPTION) {
            
            return (Boolean)app.getAction(ActionCommands.EXIT).execute(app, params);
            
        }else{
                                      
            return Boolean.FALSE;
        }
    }
}
