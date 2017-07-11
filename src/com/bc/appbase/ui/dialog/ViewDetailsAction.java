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

package com.bc.appbase.ui.dialog;

import com.bc.appbase.ui.JEditorPaneFrame;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.BlockWindowUntilCloseButtonClick;
import com.bc.appcore.content.Content;
import com.bc.appcore.content.StackTraceTextContent;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 2:23:26 PM
 */
public class ViewDetailsAction implements PopupImpl.OptionAction<Boolean>{
    private final UIContext uiContext;
    private final String title;
    public ViewDetailsAction(UIContext uiContext, String title) { 
        this.uiContext = uiContext; 
        this.title = title;
    }
    @Override
    public Boolean execute(Object message, Throwable t) {
        
        final Content<String> content = new StackTraceTextContent(
                message==null?null:message.toString(), t);
        
        try{
            
            final JFrame frame = new JEditorPaneFrame(uiContext, 
                    content.getContentType(), content.getContent(), null, this.title);
            
            frame.setPreferredSize(new Dimension(500, 400));
            
            if(uiContext != null) {
                uiContext.positionCenterScreen(frame);
            }
            
            frame.pack();
            
            frame.setVisible(true);
            
            new BlockWindowUntilCloseButtonClick().execute(frame);
            
        }catch(HeadlessException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Boolean.TRUE;
    }
}
