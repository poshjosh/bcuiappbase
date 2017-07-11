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
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.LoggingConfigManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2017 6:38:10 PM
 */
public class ViewLog implements Action<App, Boolean> {
    
    private static class FileLastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            final int output = Long.compare(f1.lastModified(), f2.lastModified());
            return output;
        }
    }

    private static final Logger logger = Logger.getLogger(ViewLog.class.getName());
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final String errorMessage;
        
        try{
            
            final int maxLen = 10_000;

            final String logCfgFile = app.getFilenames().getLoggingConfigFile();
            
            logger.log(Level.FINER, "Logging config file: {0}", logCfgFile);
            
            final LoggingConfigManager logCfgMgr = app.getOrException(LoggingConfigManager.class);
            
            final Path logsDirPath = logCfgMgr.getLogsDir(logCfgFile, null);
            
            logger.log(Level.FINE, "Logs dir: {0}", logsDirPath);
            
            if(logsDirPath == null) {
                
                errorMessage = "No `logs folder` specified";
                
            }else{
                
                final File logsDir = logsDirPath.toFile();
                
                if(!logsDir.exists()) {
                    
                    errorMessage = "Specified `logs folder` does not exist: "+logsDir;
                    
                }else{
                    
                    
                    
                    final File [] arr = logsDir.listFiles();
                    
                    if(arr == null || arr.length == 0) {
                        
                        errorMessage = "`Logs folder` does not contain any log file(s)";
                        
                    }else{
                        
                        final StringBuilder builder = new StringBuilder(maxLen);
                        
                        final List<File> fileList = new ArrayList<>(Arrays.asList(arr));
                        
                        Collections.sort(fileList, new FileLastModifiedComparator().reversed());
                        
                        logger.log(Level.FINE, "Sorted files in logs dir: {0}", fileList);
                        
                        for(File file : fileList) {
                            
                            if(file.getName().endsWith(".lck")) {
                                continue;
                            }
                            
                            final Path path = file.toPath();

                            try{
                                
                                final byte[] bytes = Files.readAllBytes(path);

                                if(bytes == null || bytes.length == 0) {
                                    continue;
                                }

                                builder.append(new String(bytes));
                                
                            }catch(IOException e) {
                                
                                logger.log(Level.WARNING, "Error reading: "+path, e);
                            }

                            if(builder.length() >= (maxLen - 1)) {
                                break;
                            }
                        }
                        
                        if(builder.length() == 0) {
                            
                            errorMessage = "Log file is empty";
                            
                        }else{
                            
                            app.getAction(ActionCommands.DISPLAY_TEXT).execute(
                                    app, Collections.singletonMap(ParamNames.TEXT, builder.toString()));
                            
                            errorMessage = null;
                        }
                    }
                }
            }
            
            if(errorMessage != null) {
                app.getUIContext().showErrorMessage(null, errorMessage);
            }
        }catch(IOException e) {
            throw new TaskExecutionException(e);
        } 
        
        return errorMessage == null;
    }
}

