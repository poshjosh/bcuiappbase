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
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2017 8:28:24 PM
 */
public class SetLookAndFeel implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        if(params.isEmpty()) {
            
            return Boolean.FALSE;
            
        }else{

            try{
                
                final String lookAndFeelName = (String)params.values().iterator().next();
                
                final Boolean output = this.execute(lookAndFeelName);
                
                if(output && app != null) {
                    java.awt.EventQueue.invokeLater(() -> {
                        final JFrame frame = app.getUIContext().getMainFrame();
                        frame.setVisible(false);
                        frame.setVisible(true);
                    });
                }
                
                return output;
                
            }catch(ClassCastException e) {
                
                throw new InvalidParameterException("Look and feel name", e);
            }
        }
    }

    public Boolean execute(String lookAndFeelName) 
            throws ParameterException, TaskExecutionException {
        
        final Logger logger = Logger.getLogger(SetLookAndFeel.class.getName());
    
        logger.log(Level.FINE, "Target look and feel name: {0}", lookAndFeelName);
        
        final LookAndFeel laf = javax.swing.UIManager.getLookAndFeel();
        
        logger.log(Level.FINE, "Current look and feel name: {0}", laf==null?null:laf.getName());

        if(laf != null && laf.getName().equals(lookAndFeelName)) {
        
            return Boolean.FALSE;
        }
        
        if(lookAndFeelName == null) {
            throw new ParameterNotFoundException("Look and feel name");
        }
        
        try {
            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                
                if (lookAndFeelName.equals(info.getName())) {
                    
                    logger.log(Level.FINE, "Setting look and feel to: {0}", info);

                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    
                    break;
                }
            }
        
            return Boolean.TRUE;
            
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            
            throw new TaskExecutionException(ex);
        }
    }
}
