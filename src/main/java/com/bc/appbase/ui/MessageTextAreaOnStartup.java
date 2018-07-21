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

import java.awt.Font;
import javax.swing.text.Document;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 8, 2017 8:12:22 PM
 */
public class MessageTextAreaOnStartup extends MessageTextArea {

    public MessageTextAreaOnStartup() {
        init();
    }

    public MessageTextAreaOnStartup(String text) {
        super(text);
        init();
    }

    public MessageTextAreaOnStartup(int rows, int columns) {
        super(rows, columns);
        init();
    }

    public MessageTextAreaOnStartup(String text, int rows, int columns) {
        super(text, rows, columns);
        init();
    }

    public MessageTextAreaOnStartup(Document doc) {
        super(doc);
        init();
    }

    public MessageTextAreaOnStartup(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
        init();
    }

    private void init() {
        this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        this.setBackground(new java.awt.Color(51, 0, 204));
        this.setForeground(new java.awt.Color(255, 255, 255));
        this.setCaretColor(new java.awt.Color(255, 255, 255));
        this.setSelectionColor(new java.awt.Color(204, 255, 255));
    }
}
