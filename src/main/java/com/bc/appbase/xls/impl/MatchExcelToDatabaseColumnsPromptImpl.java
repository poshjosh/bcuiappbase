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

import com.bc.appbase.ui.UIContext;
import com.bc.appbase.xls.MatchExcelToDatabaseColumnsPrompt;
import com.bc.appbase.ui.builder.MatchEntries;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.ui.builder.model.ComponentWalker;
import com.bc.appbase.xls.SheetToDatabaseData;
import com.bc.appcore.AppContext;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.util.RelationAccess;
import com.bc.appcore.util.SingleElementFixedSizeList;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.JCheckBox;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 7:25:06 PM
 */
public class MatchExcelToDatabaseColumnsPromptImpl implements MatchExcelToDatabaseColumnsPrompt {

    private static final Logger logger = Logger.getLogger(MatchExcelToDatabaseColumnsPromptImpl.class.getName());

    @Override
    public SheetToDatabaseData execute(
            AppContext appContext, ObjectFactory objectFactory, UIContext uiContext,
            Class rowEntityType, Sheet sheet, int rowOffset, int [] columnIndexes) {

        Set<Class> entityTypes = this.getRelatedTypes(objectFactory, rowEntityType);
        
        final PersistenceUnitMetaData metaData = appContext.getActivePersistenceUnitContext().getMetaData();
        entityTypes = this.getOptions(appContext, entityTypes);
        
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
        
        final MatchEntries matchEntries = objectFactory.getOrException(MatchEntries.class);
        final Map selections = matchEntries
                .objectFactory(objectFactory)
                .uiContext(uiContext)
                .lhs(excelCols)
                .rhs(databaseCols)
                .noSelectionName(noSelectionName)
                .thirdComponentProvider(thirdComponentProvider)
                .dialogTitle("Select Matching Name(s)")
                .build();
        
        final Predicate isNullOrEmpty = (e) -> e == null || 
                (e instanceof Collection && ((Collection)e).isEmpty());
        selections.values().removeIf(isNullOrEmpty);

        logger.log(Level.FINE, "Output: {0}", selections);
        
        final Map<Integer, List<String>> excelToDbCols = new LinkedHashMap();
        final Map<Integer, Function<Cell, List<Cell>>> spliters = new HashMap();
        
        final ComponentWalker cx = objectFactory.getOrException(ComponentWalker.class);
        final BiConsumer populateOutput = (key, val) -> {
            
            final String excelCol = key.toString();
            
            final Integer excelColIndex = excelColMappings.get(excelCol);    

            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Excel column index: {0}, excel column name: {1}, database column name(s): {2}", 
                        new Object[]{excelColIndex, excelCol, val});
            }
            
            final Predicate<Component> test = (comp) -> (comboNameFmt.apply(excelCol)).equals(comp.getName());
            
            final Container ui = matchEntries.getUi();
            
            final JCheckBox checkBox = (JCheckBox)cx.findFirstChild(ui, test, false, null);
            
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
    
    public Set<Class> getRelatedTypes(ObjectFactory objectFactory, Class entityType) {
        
        final RelationAccess relationAccess = objectFactory.getOrException(RelationAccess.class);

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
    
    public Set<Class> getOptions(AppContext context, Collection<Class> optionsParam) {
        final Set<Class> output;
        if(optionsParam == null) {
            final Set<String> puNames = Collections.singleton(context.getActivePersistenceUnitContext().getName());
            output = context.getPersistenceContext().getMetaData().getEntityClasses(puNames);
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
}

