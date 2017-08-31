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

package com.bc.appbase.ui;

import com.bc.appcore.ProcessLog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 11:10:45 AM
 */
public class ScreenLog implements ProcessLog {
    
    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private StringBuilder messageBuffer;
    
    private JFrame frame;
    
    private final String processName;
    
    private final TextAreaPanel textAreaPanel;
    
    public ScreenLog(String processName, String frameTitle, JTextArea textArea, int preferredWidth, int preferredHeight) {
        this(processName, frameTitle, new TextAreaPanel(textArea, preferredWidth, preferredHeight));
    }
    
    public ScreenLog(String processName, String frameTitle) {
        this(processName, frameTitle, new TextAreaPanel(new MessageTextArea(), 800, 700));
    }
    
    public ScreenLog(String processName, String frameTitle, TextAreaPanel textAreaPanel) {
        
        this.messageBuffer = new StringBuilder();
        this.processName = Objects.requireNonNull(processName);
        this.textAreaPanel = Objects.requireNonNull(textAreaPanel);
        
        java.awt.EventQueue.invokeLater(() -> {
            
            frame = new JFrame(frameTitle);
            frame.setUndecorated(true);
            frame.setType(java.awt.Window.Type.UTILITY);
            
            frame.getContentPane().add(textAreaPanel);

            final Dimension dim = frame.getPreferredSize();
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int left = screenSize.width/2 - dim.width/2;
            final int top = screenSize.height/2 - dim.height/2;
            frame.setLocation(left, top);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
        });
    }
    
    @Override
    public void init() {
        log("");
        log(processName);
        log("");
        java.awt.EventQueue.invokeLater(() -> {
            frame.setVisible(true);
        });
    }
    
    public void querySaveLogThenSave(String key) {
        if(messageBuffer != null && messageBuffer.length() > 0) {
            key = key.toLowerCase();
            final int option = JOptionPane.showConfirmDialog(
                    null, "Do you want to save "+key+" log?", 
                    "Save Log?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option == JOptionPane.YES_OPTION) {
                final File file = Paths.get(System.getProperty("user.home"), "Tasktracker_"+key+"log_"+System.currentTimeMillis()+".txt").toFile();
                try{
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    try(Writer out = new FileWriter(file)){
                        out.write(messageBuffer.toString());
                    }
                    JOptionPane.showMessageDialog(frame, "Installation log saved to: "+file, "Saved Log to File", JOptionPane.INFORMATION_MESSAGE);
                }catch(IOException e) {
                    JOptionPane.showMessageDialog(frame, "Error saving installation log: "+e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }    
        
    @Override
    public void destroy() {   
        if(messageBuffer != null) {
            messageBuffer.setLength(0);
            messageBuffer = null;
        }
        if(frame != null) {
            frame.setVisible(false); 
            frame.dispose();
            frame = null;
        }
    }
    
    @Override
    public void log(Throwable t) {
        log(t.getLocalizedMessage());
    }    
    
    private volatile long ld;
    @Override
    public void log(Object msg) {
        if(msg == null) {
            msg = "null";
        }
        if(messageBuffer != null) {
            messageBuffer.append(' ').append(' ').append(msg).append(this.LINE_SEPARATOR);
            if((System.currentTimeMillis() - ld) > TimeUnit.SECONDS.toMillis(1)) {
                ld = System.currentTimeMillis();
                if(SwingUtilities.isEventDispatchThread()) {
                    this.log();
                }else{
                    java.awt.EventQueue.invokeLater(() -> {
                        try{
                            log();
                        }catch(RuntimeException e) {
                            Logger.getLogger(ScreenLog.class.getName()).log(Level.WARNING, "Error logging to installation log", e);
                        }
                    });
                }
            }
        }
    }
    
    private synchronized void log() {
        if(textAreaPanel != null && messageBuffer != null) {
            textAreaPanel.setText(messageBuffer.toString());
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
