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

package com.bc.appbase.properties;

import com.bc.appbase.ui.UIContext;
import com.bc.jpa.predicates.DatabaseCommunicationsFailureTest;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2017 8:15:54 PM
 */
public class PropertiesBuilderExceptionHandler implements BiConsumer<Exception, String> {

    private transient static final Logger LOG = 
            Logger.getLogger(PropertiesBuilderExceptionHandler.class.getName());

    private final PropertiesBuilder propertiesBuilder;
    
    private final UIContext uiContext;

    public PropertiesBuilderExceptionHandler(PropertiesBuilder propertiesBuilder) {
        this(propertiesBuilder, null);
    }
    
    public PropertiesBuilderExceptionHandler(
            PropertiesBuilder propertiesBuilder, UIContext uiContext) {
        this.propertiesBuilder = Objects.requireNonNull(propertiesBuilder);
        this.uiContext = uiContext;
    }
    
    @Override
    public void accept(Exception e, String exceptionMsg) {

        final int attempts = (propertiesBuilder.getValidationAttempts() + 1);

        final String prefix = "Attempt: " + attempts + " / " + propertiesBuilder.getMaxTrials();
        
        final String logMsg = prefix + '\n' + exceptionMsg + '\n' + propertiesBuilder;
        
        if(new DatabaseCommunicationsFailureTest().test(e)) {
            LOG.warning(logMsg);
        }else{
            LOG.log(Level.WARNING, logMsg, e);
        }
        
        final String userMessage = "<html>" + prefix + ". " + exceptionMsg + "</html>";

        final JLabel label = new JLabel();
        label.setText(userMessage);
        
        final int fontSize = 16;
        final Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        label.setFont(font);
        
        final FontMetrics fontMetrics = label.getFontMetrics(font);
        final Graphics graphics = label.getGraphics();
        final Rectangle bounds = fontMetrics.getStringBounds(userMessage, graphics).getBounds();
        final Dimension size = new Dimension(bounds.width, bounds.height);
        label.setPreferredSize(size); 
        
        JOptionPane.showMessageDialog(uiContext==null?null:uiContext.getMainFrame(), new JScrollPane(label));
    }
}
