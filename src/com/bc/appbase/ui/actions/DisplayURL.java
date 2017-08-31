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
import com.bc.appbase.ui.JEditorPaneFrame;
import static com.bc.appbase.ui.actions.ParamNames.CONTENT_TYPE;
import static com.bc.appbase.ui.actions.ParamNames.TITLE;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2017 3:47:45 PM
 */
public class DisplayURL implements Action<App, Boolean> {
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final String contentType = (String)params.get(CONTENT_TYPE);
        final URL url = (URL)params.get(java.net.URL.class.getName());
        if(url == null) {
            throw new ParameterNotFoundException(java.net.URL.class.getName());
        }
        final String title = (String)params.get(TITLE);
        
        try{
            
            final JFrame frame = new JEditorPaneFrame(app.getUIContext(), contentType, null, url, title);
            
            app.getUIContext().positionHalfScreenRight(frame);
            
            frame.pack();
            frame.setVisible(true);
            
        }catch(IOException e) {
            throw new TaskExecutionException(e);
        }
        
        return Boolean.TRUE;
    }
}

