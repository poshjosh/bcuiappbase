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

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 10:43:38 AM
 */
public class JEditorPaneFrame extends JFrame {

    private final UIContext uiContext;
    
    private final String contentType;
    
    private final JEditorPane editorPane;
    
    private final JLabel statusBar;
    
    private final JPanel mainPanel;
    
    private final JScrollPane scrollPane;

    public JEditorPaneFrame(UIContext uiContext, String contentType, String title) 
            throws HeadlessException, IOException {
        super(title==null?"":title);
        this.uiContext = uiContext;
        this.contentType = contentType;
        this.editorPane = new JEditorPane();
        this.statusBar = new JLabel();
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanel.add(this.editorPane, BorderLayout.CENTER);
        this.mainPanel.add(this.statusBar, BorderLayout.SOUTH);
        this.scrollPane = new JScrollPane(this.mainPanel);
        this.init();
    }
    
    private void init() throws IOException {
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if(this.uiContext != null) {
            if(this.uiContext.getImageIcon() != null) {
                this.setIconImage(this.uiContext.getImageIcon().getImage()); 
            }
            this.editorPane.setFont(this.uiContext.getFont(JEditorPane.class));
        }
        
        if(this.contentType != null) {
            this.editorPane.setContentType(this.contentType);
            if(this.contentType.toLowerCase().contains("html")) {
                this.editorPane.setEditable(false);
            }
        }
        
        this.getContentPane().add(this.scrollPane);
    }
    
    public void addDefaultHyperlinkListener() {
        editorPane.addHyperlinkListener((HyperlinkEvent event) -> {
            if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try{
                    final URL target = event.getURL();
                    JEditorPaneFrame.this.setPage(target);
                    statusBar.setText(target.toExternalForm());
                }catch(IOException ioe) {
                    final String errorMsg = ioe.getLocalizedMessage() == null ? "Unexpected Error" :
                            ioe.getLocalizedMessage();
                    statusBar.setText(errorMsg);
                }
            }            
        });
    }
    
    public void setPage(URL page) throws IOException {
        editorPane.setPage(page);
    }

    public void read(InputStream in, Object desc) throws IOException {
        editorPane.read(in, desc);
    }

    public URL getPage() {
        return editorPane.getPage();
    }

    public void setPage(String url) throws IOException {
        editorPane.setPage(url);
    }

    public void setText(String t) {
        editorPane.setText(t);
    }

    public String getText() {
        return editorPane.getText();
    }

    public UIContext getUiContext() {
        return uiContext;
    }

    public String getContentType() {
        return contentType;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
