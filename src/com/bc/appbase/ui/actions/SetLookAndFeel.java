/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appbase.App;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2017 8:28:24 PM
 */
public class SetLookAndFeel implements Action<App, Boolean> {

    public static final String LOOK_AND_FEEL_NAME = "lookAndFeelName";
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final String lookAndFeelName = (String)params.get(LOOK_AND_FEEL_NAME);
        
        if(lookAndFeelName == null) {
            throw new ParameterNotFoundException(LOOK_AND_FEEL_NAME);
        }
        
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        
            return Boolean.TRUE;
            
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            
            Logger.getLogger(this.getClass().getName()).log(
                    Level.WARNING, "Failed to change look and feel to: "+lookAndFeelName, ex);
                    
            return Boolean.FALSE;
        }
    }
}
