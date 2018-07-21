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

import com.bc.appbase.ui.MessageUI;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import com.bc.appcore.exceptions.HasUserMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2017 10:50:15 AM
 */
public class PopupImpl implements Popup {
    
    public static interface OptionAction<R> {
        R execute(Object message, Throwable t);
    }
    
    private final Component parentComponent;

    private final Map<Object, OptionAction> successOptions;
    
    private final Map<Object, OptionAction> errorOptions;
    
    public PopupImpl(Component parentComponent) {        
        this(parentComponent, Collections.singletonMap("OK", null));
    }
    
    public PopupImpl(Component parentComponent, Map<Object, OptionAction> errorOptions) {        
        this(parentComponent, Collections.singletonMap("OK", null), errorOptions);
    }
    
    public PopupImpl(Component parentComponent, 
            Map<Object, OptionAction> successOptions,Map<Object, OptionAction> errorOptions) {        
        this.parentComponent = parentComponent;
        this.successOptions = new LinkedHashMap(Objects.requireNonNull(successOptions));
        this.errorOptions = new LinkedHashMap(Objects.requireNonNull(errorOptions));
    }
    
    @Override
    public PageSelection promptSelectPages(Object message, String title, int messageType) {
        
        final Object [] selectionValues = PageSelection.values();
        
        final Object selection = JOptionPane.showInputDialog(
                this.getParentComponent(), message, title, messageType, 
                null, selectionValues, selectionValues[1]);
        
        return selection == null ? null : (PageSelection)selection;
    }  
    
    @Override
    public void showSuccessMessage(Object message) {
        this.showMessage(null, message, JOptionPane.PLAIN_MESSAGE, this.successOptions);
    }
    
    @Override
    public void showErrorMessage(Throwable t, Object description) {
        this.showMessage(t, description, JOptionPane.WARNING_MESSAGE, errorOptions);
    }
    
    public void showMessage(Throwable t, Object description, int messageType, 
            Map<Object, OptionAction> optionActions) {
        
        final String userMsg = this.getUserMessage(t, null, false);
        
        final int selection = this.doShowMessage(userMsg, description, messageType, optionActions);
        
        if(selection == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        final Object [] options = optionActions.keySet().toArray();
        final Object SELECTED_OPTION = options[selection];
        
        final OptionAction action = this.errorOptions.get(SELECTED_OPTION);
        
        if(action == null) {
            return;
        }
        
        action.execute(description==null?null:description.toString(), t);
    }

    public int doShowMessage(String userMsg, Object description, int messageType, 
            Map<Object, OptionAction> optionActions) {
        
        final JTextComponent messageUI = this.getMessageUI(description, userMsg);
        
        final int dialogType = -1;
        final Object [] options = optionActions.keySet().toArray();
        
        final int selection = JOptionPane.showOptionDialog(
                this.getParentComponent(), messageUI, this.getMessageTitle(description, userMsg), 
                dialogType, messageType, 
                null, options, options[0]);
        
        return selection;
    }

    public JTextComponent getMessageUI(Object description, String userMessage) {
        
        final JTextComponent messageUI = this.createMessageUI();
        
        final String msg = this.getMessageText(description, userMessage);
        
        messageUI.setText(msg == null ? null : msg);
        
        return messageUI;
    }    
    
    public JTextComponent createMessageUI() {
        return new MessageUI();
    }
    
    public String getMessageTitle(Object description, String userMessage) {
        return this.getTitleAndText(description, userMessage)[0];
    }
    
    public String getMessageText(Object description, String userMessage) {
        return this.getTitleAndText(description, userMessage)[1];
    }
    
    private String [] getTitleAndText(Object description, String userMessage) {
        
        String title;
        String message;
        
        if(userMessage != null) {
            title = description==null ? null : description.toString();
            message = userMessage;
        }else{
            title = null;
            message = description==null ? null : description.toString();
        }
        
        return new String[]{title, message};
    }    

    public String getUserMessage(Throwable t, String outputIfNone, boolean firstNotLast) {
        String output = outputIfNone;
        do{
            if(t == null) {
                break;
            }else if(t instanceof HasUserMessage) {
                final HasUserMessage hasUserMsg = (HasUserMessage)t;
                output = hasUserMsg.getUserMessage();
                if(firstNotLast) {
                    break;
                }
                t = t.getCause();
            }else{
                t = t.getCause();
            }    
        }while(true);
        return output;
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    public Map<Object, OptionAction> getSuccessOptions() {
        return new HashMap(successOptions);
    }

    public Map<Object, OptionAction> getErrorOptions() {
        return new HashMap(errorOptions);
    }
}
/**
 * 
    public void showErrorMessageOld2(Throwable t, Object description) {
        
        final String userMsg = this.getUserMessage(t, null, false);
        final String errorMsgTitle = this.getErrorMessageTitle(t, description, userMsg);
        final JTextComponent messageUI = this.getErrorMessageUI(t, description, userMsg);
        
        final int dialogType;
        final String [] options;
        final String VIEW_DETAILS = "View Details";
        final String SEND_REPORT = "Send Report";
        if(t == null) {
            dialogType = JOptionPane.YES_NO_OPTION;
            options = new String[]{"OK", SEND_REPORT};
        }else{
            dialogType = JOptionPane.YES_NO_CANCEL_OPTION;
            options = new String[]{"OK", VIEW_DETAILS, SEND_REPORT};
        }
        
        final int selection = JOptionPane.showOptionDialog(
                this.getParentComponent(), messageUI, errorMsgTitle, 
                dialogType, JOptionPane.WARNING_MESSAGE, 
                null, options, options[0]);

        if(selection == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        final String SELECTED_OPTION = options[selection];
        
        switch(SELECTED_OPTION) {
            case VIEW_DETAILS: 
                final String errorMsgText = this.getErrorMessageText(t, description, userMsg);
                this.showError(errorMsgTitle, errorMsgText, t);
                break;
            case SEND_REPORT: 
                SimpleEmail email = new SimpleEmail();
                break;
        }
    }
    
    public void showErrorMessageOld(Throwable t, Object description) {
        
        final String userMsg = this.getUserMessage(t, null, false);
        final String errorMsgTitle = this.getErrorMessageTitle(t, description, userMsg);
        final JTextComponent messageUI = this.getErrorMessageUI(t, description, userMsg);
        
        final int dialogType;
        final String [] options;
        final String VIEW_DETAILS = "View Details";
        if(t == null) {
            dialogType = JOptionPane.YES_OPTION;
            options = new String[]{"OK"};
        }else{
            dialogType = JOptionPane.YES_NO_OPTION;
            options = new String[]{"OK", VIEW_DETAILS};
        }
        
        final int selection = JOptionPane.showOptionDialog(
                this.parentComponent, messageUI, errorMsgTitle, 
                dialogType, JOptionPane.WARNING_MESSAGE, 
                null, options, options[0]);

        if(selection == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        final String SELECTED_OPTION = options[selection];
        
        switch(SELECTED_OPTION) {
            case VIEW_DETAILS: 
                final String errorMsgText = this.getErrorMessageText(t, description, userMsg);
                this.showError(errorMsgTitle, errorMsgText, t);
                break;
        }
    }
    
 * 
 */