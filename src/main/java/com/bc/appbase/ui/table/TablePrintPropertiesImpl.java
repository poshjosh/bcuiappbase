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
 * @author Chinomso Bassey Ikwuagwu on Jun 30, 2017 12:34:22 AM
 */
public class TablePrintPropertiesImpl implements TablePrintProperties {

    private final TablePrintProperties.HeaderFooterProperties headerProperties;
    
    private final TablePrintProperties.HeaderFooterProperties footerProperties;

    public TablePrintPropertiesImpl(JTable table, int verticalSpaceToTable) {
        this(table.getFont().deriveFont(table.getFont().getSize2D() * 0.6f), verticalSpaceToTable);
    }
    
    public TablePrintPropertiesImpl(JTable table, int fontStyle, float fontSize, int verticalSpaceToTable) {
        this(table.getFont().deriveFont(fontStyle, fontSize), verticalSpaceToTable);
    }
    
    public TablePrintPropertiesImpl(Font font, int verticalSpaceToTable) {
        this(new HeaderFooterPropertiesImpl(font, verticalSpaceToTable), new HeaderFooterPropertiesImpl(font, verticalSpaceToTable));
    }
    
    public TablePrintPropertiesImpl(
            TablePrintProperties.HeaderFooterProperties headerProperties, 
            TablePrintProperties.HeaderFooterProperties footerProperties) {
        this.headerProperties = Objects.requireNonNull(headerProperties);
        this.footerProperties = Objects.requireNonNull(footerProperties);
    }

    @Override
    public TablePrintProperties.HeaderFooterProperties getHeaderProperties() {
        return headerProperties;
    }

    @Override
    public TablePrintProperties.HeaderFooterProperties getFooterProperties() {
        return footerProperties;
    }
}
