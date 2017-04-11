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

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2017 1:15:01 AM
 */
public class MessageUI extends JTextArea{

    public MessageUI() {
        super(5, 20);
        this.setEditable(false);
        this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.setLineWrap(true);
        final Dimension dim = new Dimension(300, 185);
        this.setMaximumSize(dim);
        this.setMinimumSize(dim);
        this.setPreferredSize(dim);
        this.setWrapStyleWord(true);
    }
}
