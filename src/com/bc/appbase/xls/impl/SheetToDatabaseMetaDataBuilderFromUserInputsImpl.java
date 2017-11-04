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

package com.bc.appbase.xls.impl;

import com.bc.appbase.App;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appcore.table.model.WorksheetTableModel;
import com.bc.appbase.xls.MatchExcelToDatabaseColumnsPrompt;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.appbase.xls.SheetToDatabaseMetaDataBuilderFromUserInputs;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import jxl.Sheet;
import com.bc.appbase.xls.SheetProcessorContext;
import com.bc.appbase.xls.SheetToDatabaseData;
import com.bc.appcore.typeprovider.TypeProvider;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 9:25:10 AM
 */
public class SheetToDatabaseMetaDataBuilderFromUserInputsImpl 
        implements SheetToDatabaseMetaDataBuilderFromUserInputs {
    
    private static final Logger logger = Logger.getLogger(SheetToDatabaseMetaDataBuilderFromUserInputsImpl.class.getName());
    
    private final App app;
    
    public SheetToDatabaseMetaDataBuilderFromUserInputsImpl(App app) {
        this.app = Objects.requireNonNull(app);
    }
    
    @Override
    public SheetToDatabaseMetaData build(Sheet sheet, SheetToDatabaseMetaData outputIfNone) {
        
        final SheetToDatabaseMetaData output;
        
        final Class entityType = this.promptSelectEntityType((cls) -> true);
        
        if(entityType == null) {
            
            output = outputIfNone;
            
        }else{
            
            final int headerRowOffset = this.promptSelectRowOffset(app, sheet, entityType, 
                    "Select row containing Headers. Default is: " + 0, 0);

            final int [] columnIndexes = this.promptSelectColumns(app, sheet, entityType, 
                    "Select columns to extract data from. Default is all columns", null);

            final SheetProcessorContext context = app.getOrException(SheetProcessorContext.class);

            final MatchExcelToDatabaseColumnsPrompt prompt = context.getMatchExcelToDatabaseColumnsPrompt();

            final SheetToDatabaseData data = 
                    prompt.execute(app, entityType, sheet, headerRowOffset, columnIndexes);

            final int dataRowOffset = this.promptSelectRowOffset(app, sheet, entityType, 
                    "Select first row to extract. Default is: " + headerRowOffset + 1, headerRowOffset + 1);

            output = new SheetToDatabaseMetaDataImpl(data, app.getOrException(TypeProvider.class),
                    entityType, headerRowOffset, dataRowOffset, Integer.MAX_VALUE);
        }
        
        return output;
    }
    
    @Override
    public int promptSelectRowOffset(App app, Sheet sheet, Class entityType, 
            String title, int outputIfNone) {
        
        final JTable table = this.getTable(sheet);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        
        app.getUIContext().updateTableUI(table, entityType, -1);
        
        this.showDialog(app, table, title, "Select Row");

        final int output = table.getSelectedRow() == -1 ? outputIfNone : table.getSelectedRow();

        return output;
    }
    
    @Override
    public int [] promptSelectColumns(App app, Sheet sheet, Class entityType, 
            String title, int [] outputIfNone) {
        
        final JTable table = this.getTable(sheet);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(true);
        
        app.getUIContext().updateTableUI(table, entityType, -1);
        
        this.showDialog(app, table, title, "Select Columns");

        final int [] output = table.getSelectedColumns() == null ? outputIfNone : table.getSelectedColumns();

        return output;
    }
    
    public void showDialog(App app, Component component, String description, String title) {
        
        final JLabel message = new JLabel("<html><p style=\"font-size:1.3em;\"><br/>"+description+"<br/></p></html>");
        
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(message, BorderLayout.NORTH);
        panel.add(new JScrollPane(component), BorderLayout.CENTER);
        
        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                new JScrollPane(panel), title, "OK", (String)null, true);
    }
    
    public JTable getTable(Sheet sheet) {
        
        final int limit = Math.min(sheet.getRows(), 333);
        
        logger.log(Level.FINE, "Max sheet rows to display: {0}", limit);

        final WorksheetTableModel tableModel = new WorksheetTableModel(sheet, 0, limit-1);
        
        final JTable table = new JTable(tableModel);
        
        return table;
    }
    
    @Override
    public Class promptSelectEntityType(Predicate<Class> test) {

        final Set<Class> options = this.getOptions(test);
        
        return this.promptSelectEntityType(options);
    }

    @Override
    public Class promptSelectEntityType(Set<Class> options) {
        try{
            return (Class)app.getAction(
                    ActionCommands.PROMPT_SELECT_ENTITY_TYPE).execute(app, 
                            Collections.singletonMap(ParamNames.ENTITY_TYPE+"List", options));
        }catch(ParameterException | TaskExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Set<Class> getOptions(Predicate<Class> test) {
        final Set<Class> entityTypes = app.getActivePersistenceUnitContext().getMetaData().getEntityClasses();
        final Set<Class> options = new LinkedHashSet();
        entityTypes.stream().filter(test).forEach((cls) -> options.add(cls));
        return options;
    }
    
    public App getApp() {
        return app;
    }
}

