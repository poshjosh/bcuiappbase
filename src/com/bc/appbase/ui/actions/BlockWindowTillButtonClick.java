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
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractButton;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2017 8:49:04 PM
 */
public class BlockWindowTillButtonClick extends BlockWindowUntilCloseButtonClick {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
    
        final ParameterExtractor parameterExtractor = app.getOrException(ParameterExtractor.class);
        final Window window = parameterExtractor.getFirstValue(params, Window.class);
        Objects.requireNonNull(window);

        final AbstractButton button = parameterExtractor.getFirstValue(params, AbstractButton.class);
        Objects.requireNonNull(button);

        try{
            
            return this.execute(window, button);
            
        }catch(InterruptedException e) {
            throw new TaskExecutionException(e);
        }
    }
    
    public Boolean execute(Window window, AbstractButton... buttons) throws InterruptedException {
        
        this.addActionListener(window, buttons);

        final Boolean output = this.execute(window);
        
        return output;
    }
    
    public void addActionListener(Window window, AbstractButton... buttons) {
        final ActionListener actionListener = (ActionEvent e) -> {
            synchronized(window) {
                notifyAllAndDispose(window);
            }
        };
        for(AbstractButton button : buttons) {
            button.addActionListener(actionListener);
        }
    }
}
