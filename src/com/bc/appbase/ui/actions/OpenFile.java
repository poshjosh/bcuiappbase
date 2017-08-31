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
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 18, 2017 12:24:22 PM
 */
public class OpenFile implements Action<App, Boolean> {
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final File file;
        final Object oval = params.get(java.io.File.class.getName());
        if(oval == null) {
            throw new ParameterNotFoundException(java.io.File.class.getName());
        }else if(oval instanceof File) {
            file = (File)oval;
        }else {
            throw new InvalidParameterException(java.io.File.class.getName() + " = " + oval);
        }
        
        final String msg = "Openning of selected file not supported";
        
        if(Desktop.isDesktopSupported()) {
            
            final Desktop desktop = Desktop.getDesktop();
            
            if(desktop.isSupported(Desktop.Action.OPEN)) {
                try{
                    
                    desktop.open(file);
                    
                    return Boolean.TRUE;
                    
                }catch(IOException e) {
                    
                    app.getUIContext().showErrorMessage(e, "Error opening file: "+file);
                }
            }else{
                app.getUIContext().showErrorMessage(null, msg);
            }
        }else{
            app.getUIContext().showErrorMessage(null, msg);
        }
        
        return Boolean.FALSE;
    }
}
