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
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Objects;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 4:31:59 PM
 */
public class BlockWindowUntilCloseButtonClick implements Action<App, Boolean> {

    private final AtomicBoolean disposed = new AtomicBoolean(false);
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
    
        final Window window = app.getOrException(ParameterExtractor.class).getFirstValue(params, Window.class);
        
        try{
            return this.execute(window);
        }catch(InterruptedException e) {
            throw new TaskExecutionException(e);
        }
    }
    
    public Boolean execute(Window window) throws InterruptedException {
         
        Objects.requireNonNull(window);
        
        window.setVisible(true);
        
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized(window) {
                    notifyAllAndDispose(window);
                }
            }
        });

        this.wait(window);

        return Boolean.TRUE;
    }

    public void wait(Window window) throws InterruptedException {
        synchronized(window) {
            try{
                window.wait();
            }finally{
                this.notifyAllAndDispose(window);
            }
        }
    }
    
    public boolean notifyAllAndDispose(Window window) {
        final boolean wasDisposed = disposed.getAndSet(true);
        if(wasDisposed) {
            return false;
        }else{
            synchronized(window) { 
                window.notifyAll();
                window.setVisible(false);
                window.dispose();
            }
            
            return true;
        }
    }

}

