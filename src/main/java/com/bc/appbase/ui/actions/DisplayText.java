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
import static com.bc.appbase.ui.actions.ParamNames.TEXT;
import static com.bc.appbase.ui.actions.ParamNames.TITLE;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2017 7:11:33 PM
 */
public class DisplayText implements Action<App, Boolean> {
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final String contentType = (String)params.get(CONTENT_TYPE);
        final String text = (String)params.get(TEXT);
        if(text == null) {
            throw new ParameterNotFoundException(TEXT);
        }
        final String title = (String)params.get(TITLE);

        try{
            
            final JEditorPaneFrame frame = new JEditorPaneFrame(app.getUIContext(), contentType, title);
            
            frame.setText(text);

            app.getUIContext().positionHalfScreenRight(frame);

            frame.pack();
            frame.setVisible(true);
            
            return Boolean.TRUE;
            
        }catch(IOException e) {
            
            throw new TaskExecutionException(e);
        }
    }
}

