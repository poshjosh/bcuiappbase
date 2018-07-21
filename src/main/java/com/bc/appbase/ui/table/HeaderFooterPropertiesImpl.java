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

package com.bc.appbase.ui.table;

import java.awt.Font;
import java.util.Objects;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 30, 2017 12:38:19 AM
 */
public class HeaderFooterPropertiesImpl implements TablePrintProperties.HeaderFooterProperties {

    private final Font font;
    
    private final int verticalSpaceToTable;

    public HeaderFooterPropertiesImpl(JTable table, int fontStyle, float fontSize, int verticalSpaceToTable) {
        this(table.getFont().deriveFont(fontStyle, fontSize), verticalSpaceToTable);
    }
    
    public HeaderFooterPropertiesImpl(Font font, int verticalSpaceToTable) {
        this.font = Objects.requireNonNull(font);
        this.verticalSpaceToTable = verticalSpaceToTable;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public int getVerticalSpaceToTable() {
        return verticalSpaceToTable;
    }
}
