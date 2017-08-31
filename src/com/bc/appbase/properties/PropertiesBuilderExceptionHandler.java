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

import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2017 8:15:54 PM
 */
public class PropertiesBuilderExceptionHandler implements Consumer<Exception> {

    private static final Logger logger = Logger.getLogger(PropertiesBuilderExceptionHandler.class.getName());

    private final PropertiesBuilder propertiesBuilder;
    
    private final Function<Exception, String> getExceptionMessage;

    public PropertiesBuilderExceptionHandler(
            PropertiesBuilder propertiesBuilder, 
            Function<Exception, String> getExceptionMessage) {
        this.propertiesBuilder = Objects.requireNonNull(propertiesBuilder);
        this.getExceptionMessage = Objects.requireNonNull(getExceptionMessage);
    }
    
    @Override
    public void accept(Exception e) {
        final int attempt = (propertiesBuilder.getValidationAttempts() + 1);
        final String prefix = "Attempt: " + attempt + " / " + propertiesBuilder.getMaxTrials() + 
                " encountered exception: ";
        logger.warning(() -> prefix + '\n' + e);
        final String userMessage = "<html>" + prefix + "<br/>" + getExceptionMessage.apply(e) + "</html>";
        final int width = userMessage.length() * 2;
        final int height = userMessage.length() * 1;
        final Dimension size = new Dimension(width, height);
        final JLabel label = new JLabel();
        label.setPreferredSize(size);
        label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        label.setText(userMessage);
        JOptionPane.showMessageDialog(null, new JScrollPane(label));
    }
}
