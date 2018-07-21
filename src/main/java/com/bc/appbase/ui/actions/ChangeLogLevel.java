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
import com.bc.appcore.parameter.ParameterException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.bc.appcore.util.LoggingConfigManager;

/**
 * @author Chinomso Bassey Ikwuagwu on May 2, 2017 6:31:16 PM
 */
public class ChangeLogLevel implements Action<App, Level> {

    private static final Logger logger = Logger.getLogger(ChangeLogLevel.class.getName());
    
    @Override
    public Level execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Level output;
        
        final Level [] levels = {Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF};
        
        final Level currentLevel = Logger.getLogger("").getLevel();
        
        logger.log(Level.FINE, "Current log level: {0}", currentLevel);
        
        final JFrame mainFrame = app.getUIContext().getMainFrame();
        
        final JComboBox<Level> comboBox = new JComboBox<>(levels);
        comboBox.setFont(app.getUIContext().getFont(JComboBox.class));
        
        final int selection = JOptionPane.showConfirmDialog(mainFrame, comboBox, 
                "Select New Log Level", JOptionPane.OK_CANCEL_OPTION);
        
        if(selection == JOptionPane.OK_OPTION) {
            
            final Level selectedLevel = (Level)comboBox.getSelectedItem();
            
            logger.log(Level.FINE, "Selected log level: {0}", selectedLevel);
        
            if(!selectedLevel.equals(currentLevel)) {

                final LoggingConfigManager logConfigMgr = app.getOrException(LoggingConfigManager.class);

                try{
                    
                    final String loggingConfig = app.getPropertiesContext().getLogging().toString();
                    
                    logger.log(Level.FINE, "Logging config file: {0}", loggingConfig);
                    
                    logConfigMgr.updateLevel(loggingConfig, selectedLevel);
                    logConfigMgr.read(loggingConfig);

                    logger.fine("Successfully read new log level");
                    
                    output = selectedLevel;
                    
                }catch(IOException e) {
                    throw new TaskExecutionException(e);
                }
            }else{
                
                output = currentLevel;
            }
        }else{
            
            output = currentLevel;
        } 
        
        return output;
    }
}
