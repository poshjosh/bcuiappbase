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

import com.bc.appbase.xls.MatchExcelToDatabaseColumnsPrompt;
import com.bc.appbase.App;
import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appbase.ui.Components;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.JCheckBoxMenuItemListComboBox;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.builder.impl.FormEntryComponentModelImpl;
import com.bc.appbase.xls.SheetToDatabaseData;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.util.RelationAccess;
import com.bc.appcore.util.Selection;
import com.bc.appcore.util.SelectionValues;
import com.bc.appcore.util.SingleElementFixedSizeList;
import com.bc.jpa.JpaMetaData;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 7:25:06 PM
 */
public class MatchExcelToDatabaseColumnsPromptImpl implements MatchExcelToDatabaseColumnsPrompt {

    private static final Logger logger = Logger.getLogger(MatchExcelToDatabaseColumnsPromptImpl.class.getName());

    @Override
    public SheetToDatabaseData execute(App app, 
            Class rowEntityType, Sheet sheet, int rowOffset, int [] columnIndexes) {

        Set<Class> entityTypes = this.getRelatedTypes(app, rowEntityType);
        
        final JpaMetaData metaData = app.getJpaContext().getMetaData();
        entityTypes = this.getOptions(app, entityTypes);
        
        final Set<String> databaseCols = new TreeSet();
        entityTypes.stream().forEach((type) -> {
            final String idColumnName = metaData.getIdColumnName(type);
            final List<String> columnNamesList = new ArrayList<>(Arrays.asList(metaData.getColumnNames(type)));
            columnNamesList.removeIf((name) -> name.equalsIgnoreCase(idColumnName));
            databaseCols.addAll(columnNamesList);
        });
    
        final Map<String, Integer> excelColMappings = this.getSheetColumns(sheet, rowOffset, columnIndexes);
        final Set excelCols = excelColMappings.keySet();
        
        final Function<String, String> comboNameFmt = (name) -> name+'.'+JCheckBox.class.getSimpleName();
        
        final ThirdComponentProvider thirdComponentProvider = (parentType, valueType, name, value, label, component, outputIfNone) -> {
            final JCheckBox checkBox = new JCheckBox("Cell contains multiple values");
            checkBox.setName(comboNameFmt.apply(name));
            return checkBox;
        };
        
        final String noSelectionName = "Select matching name(s)";
        
        final Container ui = this.buildNameMatchUI(app, 
                excelCols, databaseCols, noSelectionName, thirdComponentProvider);
        
        if(ui instanceof JComponent) {
            app.getUIContext().positionHalfScreenRight(((JComponent)ui).getTopLevelAncestor());
        }else{
            app.getUIContext().positionHalfScreenRight(ui);
        }
          
        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                ui, "Select Matching Name(s)", " OK ", (String)null, true);

        final Map selections = this.getSelectionsFromUI(app, ui, excelCols, noSelectionName);

        final Predicate isNullOrEmpty = (e) -> e == null || 
                (e instanceof Collection && ((Collection)e).isEmpty());
        selections.values().removeIf(isNullOrEmpty);

        logger.log(Level.FINE, "Output: {0}", selections);
        
        final Map<Integer, List<String>> excelToDbCols = new LinkedHashMap();
        final Map<Integer, Function<Cell, List<Cell>>> spliters = new HashMap();
        
        final Components components = new Components();
        final BiConsumer populateOutput = (key, val) -> {
            
            final String excelCol = key.toString();
            
            final Integer excelColIndex = excelColMappings.get(excelCol);    

            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Excel column index: {0}, excel column name: {1}, database column name(s): {2}", 
                        new Object[]{excelColIndex, excelCol, val});
            }
            
            final Predicate<Component> test = (comp) -> (comboNameFmt.apply(excelCol)).equals(comp.getName());
            
            final JCheckBox checkBox = (JCheckBox)components.findFirstChild(ui, test, false, null);
            
            final MatchExcelToDatabaseColumnsPromptImpl ref = MatchExcelToDatabaseColumnsPromptImpl.this;
            
            final Function spliter = getSpliter(excelColIndex, key, val, checkBox, null);
            
            if(spliter != null) {
                spliters.put(excelColIndex, spliter);
            }
            
            final boolean isMultiValueCell = spliters.get(excelColIndex) != null;
            
            final List dbCols;
            if(isMultiValueCell) {
                dbCols = val instanceof Collection ? new ArrayList((Collection)val) : new SingleElementFixedSizeList(val, 100);
            }else{
                if(val instanceof Collection) {
                    throw new IllegalArgumentException();
                }else{
                    dbCols = Collections.singletonList(val);
                }
            }
              
            excelToDbCols.put(excelColIndex, dbCols);
        };
        
        selections.forEach(populateOutput);
        
        return new SheetToDatabaseDataImpl(excelToDbCols, spliters);
    }
    
    public Map getSelectionsFromUI(App app, Container ui, Set excelCols, String noSelectionName) {
        final Map selections = (Map)app.getOrException(FromUIBuilder.class)
                .componentModel(this.getComponentModel(app, excelCols, noSelectionName))
                .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                .ui(ui)
                .source(this.getUIParameters(excelCols))
                .target(new LinkedHashMap())
                .build();
        return selections;
    }
    
    public Function<Cell, List<Cell>> getSpliter(int excelColIndex, Object key, Object val, JCheckBox checkBox, Function<Cell, List<Cell>> outputIfNone) {
        final String excelCol = key.toString();
        if(val instanceof Collection) {
//System.out.println("Excel column: "+excelColIndex+'='+excelCol+", has text spliter with SPACE as separator. @"+this.getClass());                
            return new TextCellSpliter("\\s{1,}");

        }else{
            Objects.requireNonNull(checkBox, "JCheckBox is null for database column(s): "+val+", excel column: "+excelCol);
            if(checkBox.isSelected()) {
//System.out.println("Excel column: "+excelColIndex+'='+excelCol+", has text spliter with COMMA as separator. @"+this.getClass());                    
                return new TextCellSpliter(",\\s{0,}");
            }
        }
        return outputIfNone;
    }
    
    public Set<Class> getRelatedTypes(App app, Class entityType) {
        
        final RelationAccess relationAccess = app.getOrException(RelationAccess.class);

        final Set<Class> childTypes = relationAccess.getChildTypes(entityType, true, 
                this.getTypeFilter(), this.getRecursionFilter(), true);

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Entity type: {0}, child types: {1}", 
                    new Object[]{entityType.getName(), childTypes});
        }
        return childTypes;
    }
    
    public Predicate<Class> getRecursionFilter() {
        return this.getTypeFilter();
    }
    
    public Predicate<Class> getTypeFilter() {
        final Predicate<Class> acceptEntityType = (cls) -> cls.getAnnotation(Entity.class) != null;
        return acceptEntityType;
    }
    
    public Set<Class> getOptions(App app, Collection<Class> optionsParam) {
        final Set<Class> output;
        if(optionsParam == null) {
            final Set<String> puNames = app.getPersistenceUnitNames();
            output = app.getJpaContext().getMetaData().getEntityClasses(puNames);
        }else{
            output = optionsParam instanceof Set ?
                    (Set<Class>)optionsParam : new LinkedHashSet(optionsParam);
        }
        return output;
    }
    
    public Map<String, Integer> getSheetColumns(Sheet sheet, int rowOffset, int [] columns) {
        final Cell [] cells = sheet.getRow(rowOffset);
        final boolean columnsNotSpecified = columns == null || columns.length == 0;
        final int size = columnsNotSpecified ? cells.length : columns.length;
        final Map<String, Integer> sheetCols = new HashMap(size, 1.0f);
        for(int i=0; i<size; i++) {
            final int cellIndex = columnsNotSpecified ? i : columns[i];
            final String sheetCol = cells[cellIndex].getContents();
            sheetCols.put(sheetCol.trim(), cellIndex);
        }
        logger.log(Level.FINE, "Excel columns: {0}", sheetCols);
        return sheetCols;
    }
    
    public Container buildNameMatchUI(App app, Set<String> lhs, Set<String> rhs, 
            String noSelectionName, ThirdComponentProvider thirdComponentProvider) {
        
        final ComponentModel componentModel = this.getComponentModel(app, rhs, noSelectionName);
        
        return this.buildNameMatchUI(app, lhs, componentModel, thirdComponentProvider);
    }
    
    public Container buildNameMatchUI(App app, Set<String> lhs, 
            ComponentModel componentModel, ThirdComponentProvider thirdComponentProvider) {
    
        final Map<String, Object> uiParams = this.getUIParameters(lhs);
        
        logger.log(Level.FINE, "UI params: {0}", uiParams);
        
        final int labelWidth = this.getLeftColumnTextLength(lhs);
        
        final Container ui = app.getOrException(UIBuilderFromMap.class)
                .typeProvider(TypeProvider.from(Object.class, String.class))
                .entryUIProvider(new FormEntryComponentModelImpl(componentModel, labelWidth, thirdComponentProvider))
                .sourceData(uiParams)
                .build();
        
        return ui;
    }
    
    public ComponentModel getComponentModel(App app, Set<String> rhs, String noSelectionName) {
        final Selection noSelection = Selection.from(noSelectionName, null);
        final SelectionValues selectionValues = SelectionValues.from(noSelection, new LinkedHashSet(rhs));
        return this.getComponentModel(app, selectionValues);
    }
    
    public ComponentModel getComponentModel(App app, SelectionValues selectionValues) {
        final ComponentModel componentModel = new ComponentModelImpl(
                selectionValues, 
                app.getOrException(DateFromUIBuilder.class), 
                app.getOrException(DateUIUpdater.class)){
            @Override
            public Component getSelectionComponent(Class valueType, 
                    String name, Object value, List<Selection> selectionList) {
                final JCheckBoxMenuItemListComboBox checkMenuListCombo = new JCheckBoxMenuItemListComboBox(selectionList);
                return checkMenuListCombo;
            }
        };
        return componentModel;
    }
    
    private int getLeftColumnTextLength(Set<String> lhs) {
        final Comparator<String> comparator = (s0, s1) -> Integer.compare(s0.length(), s1.length());
        final Optional<String> colWithMaxLen = lhs.stream().collect(Collectors.maxBy(comparator));
        logger.log(Level.FINER, "Collected column with max length, in optional: {0}", colWithMaxLen);
        if(!colWithMaxLen.isPresent()) {
            throw new IllegalArgumentException();
        }
        final int labelLength = colWithMaxLen.get().length() * 15;
        return labelLength;
    }
    
    public Map<String, Object> getUIParameters(Set<String> lhs) {
        
        final Map<String, Object> uiParams = new LinkedHashMap();
        
        lhs.forEach((col) -> uiParams.put(col, null));
        
        return uiParams;
    }
}

