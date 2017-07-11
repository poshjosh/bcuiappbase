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

import com.bc.appbase.ui.actions.BlockWindowTillButtonClick;
import com.bc.appbase.ui.actions.BlockWindowUntilCloseButtonClick;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 27, 2017 8:35:47 AM
 */
public class UIDisplayHandlerImpl implements UIDisplayHandler {

    private static final Logger logger = Logger.getLogger(UIDisplayHandlerImpl.class.getName());
    
    private final UIContext uiContext;

    public UIDisplayHandlerImpl(UIContext uiContext) {
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    @Override
    public void displayWithTopAndBottomActionButtons(
            Container ui, String title, String buttonText, String actionCommand, boolean block) {
        
        final JButton top = new JButton(buttonText);
        final JButton bottom = new JButton(buttonText);
        
        final SimpleFrame frame = new SimpleFrame(
                uiContext, ui, title, new Font(Font.MONOSPACED, Font.BOLD, 24),
                top, bottom, actionCommand
        );
//        app.getUIContext().positionHalfScreenLeft(frame);
        
        frame.pack();
        frame.setVisible(true);
        
        if(block) {
            if(actionCommand == null) {
                this.blockWindowTillButtonClick(frame, top, bottom);
            }else{
                this.blockWindowTillCloseButtonClick(frame);
            }
        }
    }
    
    @Override
    public void displayUI(Container ui, String title, boolean scrolls, boolean block) {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(scrolls ? new JScrollPane(ui) : ui);
//        app.getUIContext().positionHalfScreenRight(frame);
        frame.pack();
        frame.setVisible(true);
        if(block) {
            this.blockWindowTillCloseButtonClick(frame);
        }
    }
    
    @Override
    public void blockWindowTillCloseButtonClick(Window window) {
        try{
            new BlockWindowUntilCloseButtonClick().execute(window);
        }catch(InterruptedException e) {
            logger.log(Level.WARNING, "Window 'block' distrupted", e);
        }
    }

    @Override
    public void blockWindowTillButtonClick(Window window, AbstractButton... button) {
        try{
            new BlockWindowTillButtonClick().execute(window, button);
        }catch(InterruptedException e) {
            logger.log(Level.WARNING, "Window 'block' distrupted", e);
        }
    }
}
