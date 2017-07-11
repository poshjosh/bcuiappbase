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

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 8, 2017 8:02:48 PM
 */
public class MessageTextArea extends JTextArea {

    public MessageTextArea() {
        this.init();
    }

    public MessageTextArea(String text) {
        super(text);
        this.init();
    }

    public MessageTextArea(int rows, int columns) {
        super(rows, columns);
        this.init();
    }

    public MessageTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
        this.init();
    }

    public MessageTextArea(Document doc) {
        super(doc);
        this.init();
    }

    public MessageTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
        this.init();
    }

    private void init() {
        this.setEditable(false);
        this.setBorder(null);
        this.setDoubleBuffered(true);
    }
}
