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
import jxl.write.WritableFont;
import jxl.write.WriteException;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 3:21:16 PM
 */
public class WritableAwtFont extends WritableFont {
    
    public static final int STYLE_NOT_BOLD = 0x190;
    public static final int STYLE_BOLD = 0x2bc;

    public WritableAwtFont(java.awt.Font font) {
        super(WritableFont.createFont(font.getName()), font.getSize());
        final int style = font.getStyle();
        try{
            switch(style) {
                case Font.BOLD:
                    this.setBoldStyle(STYLE_BOLD); break;
                case Font.ITALIC:
                    this.setItalic(true); break;
                case Font.PLAIN:
                    this.setBoldStyle(STYLE_NOT_BOLD); break;
            }
        }catch(WriteException e) {
            throw new RuntimeException(e);
        }
    }
}
