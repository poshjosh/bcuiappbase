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

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 10:43:38 AM
 */
public class JEditorPaneFrame extends JFrame {

    private final UIContext uiContext;
    
    private final String contentType;
    
    private final String text;
    
    private final URL url;
    
    private final JEditorPane editorPane;
    
    private final JScrollPane scrollPane;

    public JEditorPaneFrame(UIContext uiContext, String contentType, 
            String text, URL url, String title) 
            throws HeadlessException, IOException {
        super(title==null?"":title);
        this.uiContext = uiContext;
        this.contentType = contentType;
        this.text = text;
        this.url = url;
        this.editorPane = new JEditorPane();
        this.scrollPane = new JScrollPane(this.editorPane);
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
        }
        
        this.editorPane.setText(this.text); 
        
        if(this.url != null) {
            this.editorPane.setPage(this.url);
        }
        
        this.getContentPane().add(this.scrollPane);
    }

    public UIContext getUiContext() {
        return uiContext;
    }

    public String getContentType() {
        return contentType;
    }

    public String getText() {
        return text;
    }

    public URL getUrl() {
        return url;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
