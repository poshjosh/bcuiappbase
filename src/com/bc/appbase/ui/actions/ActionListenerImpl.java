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
import com.bc.util.Util;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 8:07:16 PM
 */
public class ActionListenerImpl implements ActionListener {

    private final App app;
    
    private final Callable action;
    
    private final String actionCommand;
    
    private final int progressBarDelay;
    
    private final boolean async;
    
    private final AtomicBoolean completed = new AtomicBoolean(false);
    
    public ActionListenerImpl(App app, Callable action) {
        this(app, action, action.getClass().getName(), 500, true);
    }
    
    public ActionListenerImpl(
            App app, Callable action, String actionCommand, boolean async) {
        this(app, action, actionCommand, 500, async);
    }
    
    public ActionListenerImpl(
            App app, Callable action, String actionCommand, int progressBarDelay, boolean async) {
        this.app = Objects.requireNonNull(app);
        this.action = Objects.requireNonNull(action);
        this.actionCommand = Objects.requireNonNull(actionCommand);
        this.progressBarDelay = progressBarDelay;
        this.async = async;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        
        try{
            
            final String ACTION_COMMAND = actionEvent.getActionCommand();
            
            if(!Objects.equals(this.actionCommand, ACTION_COMMAND)) {
                
                throw new IllegalStateException("Unexpected action command: " + ACTION_COMMAND);
            }
            
            try {
                
                if(async) {
                    new Thread(actionCommand + "_ActionCommand_Thread"){
                        @Override
                        public void run() {
                            try{
                                execute();
                            }catch(Exception e) {
                                handleException(e, "Exception executing action command: " + getLabel(actionCommand, actionCommand));
                            }
                        }
                    }.start();
                }else{
                    this.execute();
                }
                
            }catch(Exception e) {
                handleException(e, "Exception executing action command: " + getLabel(actionCommand, actionCommand));
            }
            
        }catch(RuntimeException e) {
            
            handleException(e, "An unexpected error occured");
        }
    }
    
    private void execute() throws Exception {
        final JFrame frame = app.getUIContext().getMainFrame();
        try{
            this.beginProgress(frame);
            action.call();
        }finally{
            this.completeProgress(frame);
        }
    }
    
    private void beginProgress(JFrame frame) {
        
        final ScheduledExecutorService execSvc = Executors.newSingleThreadScheduledExecutor();

        execSvc.schedule(() -> {

            execSvc.shutdown();

            synchronized(completed) {
                if(!completed.get()) {
                    app.getUIContext().showProgressBarPercent("Please wait", -1);
                }
                Util.shutdownAndAwaitTermination(execSvc, 500, TimeUnit.MILLISECONDS);
            }
        }, progressBarDelay, TimeUnit.MILLISECONDS);

        if(frame != null) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }
    
    private void completeProgress(JFrame frame) {
        
        if(frame != null) {
            frame.setCursor(null);
        }

        synchronized(completed) {
            completed.set(true);
            app.getUIContext().showProgressBarPercent("Done", 100);
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
