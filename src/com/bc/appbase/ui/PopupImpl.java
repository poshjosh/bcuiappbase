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

import com.bc.appcore.exceptions.UserException;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2017 10:50:15 AM
 */
public class PopupImpl implements Popup {
    
    private final Component parentComponent;

    public PopupImpl(Component parent) {
        this.parentComponent = parent;
    }

    @Override
    public void showErrorMessage(Throwable t, Object description) {
        
        final JTextComponent messageUI = new MessageUI();
        
        String title;
        String msg;
        if(t instanceof UserException) {
            final String userMsg = ((UserException)t).getUserMessage();
            if(userMsg == null) {
                title = "Error";
                msg = description==null?null:String.valueOf(description);
            }else{
                title = description==null?null:String.valueOf(description);
                msg = userMsg;
            }
        }else{
            title = "Error";
            msg = description==null?null:String.valueOf(description);
        }
        
        messageUI.setText(msg);
        
        JOptionPane.showMessageDialog(parentComponent, 
                messageUI, title, JOptionPane.ERROR_MESSAGE, null);
    }

    @Override
    public void showSuccessMessage(Object message) {
        final JTextComponent messageUI = new MessageUI();
        messageUI.setText(message==null?null:String.valueOf(message));
        JOptionPane.showMessageDialog(parentComponent, 
                messageUI, "Succes", JOptionPane.INFORMATION_MESSAGE, null);
    }

    public Component getParentComponent() {
        return parentComponent;
    }
}
