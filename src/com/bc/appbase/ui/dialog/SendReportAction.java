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

import com.bc.appcore.content.Content;
import com.bc.appcore.content.StackTraceTextContent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 2:32:37 PM
 */
public class SendReportAction implements PopupImpl.OptionAction<String>{

    private static final Logger logger = Logger.getLogger(SendReportAction.class.getName());
    
    private final Email email;

    public SendReportAction(String host, int port, String emailAddress, String password, String subject) {
        this(getEmail(host, port, emailAddress, password, subject));
    }
    
    public SendReportAction(Email email) {
        this.email = email;
    }
    
    @Override
    public String execute(Object message, Throwable t) {
        
        final Content<String> content = new StackTraceTextContent(
                message==null?null:message.toString(), t);
        
        email.setContent(content.getContent(), content.getContentType());
        
        try{
            return email.send(); 
        }catch(EmailException e) {
            logger.log(Level.WARNING, "Error sending email for message: "+message+", and exception: "+t, e);
            return null;
        }
    }

    private static Email getEmail(String host, int port, String emailAddress, String password, String subject) {
        
        final SimpleEmail email = new SimpleEmail();
        
        try{
            email.addTo(emailAddress);
        }catch(EmailException e) {
            logger.log(Level.WARNING, "Error add `to address`: "+emailAddress+" to email with subject: "+subject, e);
            return null;
        }
        
        if(password != null) {
            email.setAuthentication(emailAddress, password);
        }
        email.setDebug(logger.isLoggable(Level.FINE));
        
        try{
            email.setFrom(emailAddress);
        }catch(EmailException e) {
            logger.log(Level.WARNING, "Error setting `from address` = "+emailAddress+" to email with subject: "+subject, e);
            return null;
        }

        email.setHostName(host);
        email.setSmtpPort(port);
            
        if(password != null) {
            email.setSSLOnConnect(true);
        }

        email.setSubject(subject);
        
        return email;
    }
}
