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
import com.bc.appbase.ui.MainFrame;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import com.bc.appcore.user.User;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 5, 2017 5:10:23 PM
 */
public class UpdateLoginButtonText implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final JFrame frame = app.getUIContext().getMainFrame();
        
        if(!(frame instanceof MainFrame)) {
            
            throw new UnsupportedOperationException();
            
        }else{
            
            final User user = app.getUser();
            
            final String buttonText = !user.isLoggedIn() ? "Login" :
                    "Logout (" + user.getName() + ')';
            
            final AbstractButton button = ((MainFrame)frame).getLoginMenuItem();
            
            Logger.getLogger(this.getClass().getName()).log(Level.FINE,
                    () -> "Button text. Current: "+button.getText()+", update: "+buttonText);

            if(buttonText.equals(button.getText())) {
                
                return Boolean.FALSE;
                
            }else{
                
                button.setText(buttonText);
                
                return Boolean.TRUE;
            }
        }
    }
}
