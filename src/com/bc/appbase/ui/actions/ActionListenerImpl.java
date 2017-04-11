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
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParametersBuilder;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Objects;
import com.bc.appbase.App;
import com.bc.appcore.actions.TaskExecutionException;


/**
 * @author Chinomso Bassey Ikwuagwu on Feb 8, 2017 9:59:13 PM
 */
public class ActionListenerImpl implements ActionListener {

    private final App app;
    
    private final Container container;
    
    private final String actionCommand;
    
    public ActionListenerImpl(App app, Container container, String actionCommand) {
        this.app = Objects.requireNonNull(app);
        this.container = Objects.requireNonNull(container);
        this.actionCommand = Objects.requireNonNull(actionCommand);
    }
    
    private final class WorkerThread extends Thread{
        private final String ACTION_COMMAND;
        public WorkerThread(String actionCommand) {
            super(actionCommand + "_Thread");
            this.ACTION_COMMAND = actionCommand;
        }
        @Override
        public void run() {
            try {

                final ParametersBuilder paramsBuilder = 
                        app.getParametersBuilder(container, ACTION_COMMAND);

                final Map<String, Object> params = paramsBuilder.context(app).with(container).build();

                final Action<App, ?> action = app.getAction(ACTION_COMMAND);

                action.execute(app, params);

            }catch(ParameterException | TaskExecutionException | RuntimeException e) {

                handleException(e, "Exception executing action command: " + getLabel(ACTION_COMMAND, ACTION_COMMAND));
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        
        try{
            
            final String ACTION_COMMAND = actionEvent.getActionCommand();
            
            if(!this.actionCommand.equals(ACTION_COMMAND)) {
                return;
            }
            
            new WorkerThread(ACTION_COMMAND).start();
            
        }catch(RuntimeException e) {
            
            handleException(e, "An unexpected error occured");
        }
    }
    
    public String getLabel(String actionCommand, String outputIfNone) {
        final int beforeFirst = actionCommand.lastIndexOf('.');
        final String label;
        if(beforeFirst == -1) {
            label = outputIfNone;
        }else{
            label = actionCommand.substring(beforeFirst + 1);
        }
        return label;
    }
    
    public void handleException(Throwable e, String message) {
        Logger.getLogger(this.getClass().getName()).log(
                Level.WARNING, message, e);
        app.getUIContext().showErrorMessage(e, message);
    }
}
