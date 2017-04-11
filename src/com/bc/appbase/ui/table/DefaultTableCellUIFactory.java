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

import com.bc.appbase.App;
import com.bc.appbase.ui.ComponentModel;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.table.cellui.TableCellDisplayValue;
import com.bc.table.cellui.TableCellSize;
import com.bc.table.cellui.TableCellUIFactoryImpl;
import com.bc.table.cellui.TableCellUIState;
import java.awt.Component;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 2:41:58 PM
 */
public class DefaultTableCellUIFactory extends TableCellUIFactoryImpl {

    private final App app;
    
    private final ComponentModel componentModel;
    
    private final ResultModel resultModel;
    
    public DefaultTableCellUIFactory(App app, 
            TableCellUIState uiState, TableCellSize size, TableCellDisplayValue displayValue,
            ComponentModel componentModel, ResultModel resultModel) {
        super(uiState, size, displayValue);
        this.app = Objects.requireNonNull(app);
        this.componentModel = Objects.requireNonNull(componentModel);
        this.resultModel = Objects.requireNonNull(resultModel);
    }

    @Override
    public Component getEditorComponent(int columnIndex) {
        final Class type = resultModel.getColumnClass(columnIndex);
        final String name = resultModel.getColumnName(columnIndex);
        return componentModel.getComponent(type, name, null);
    }

    @Override
    public Component getRendererComponent(int columnIndex) {
        return super.getRendererComponent(columnIndex); 
    }
}
