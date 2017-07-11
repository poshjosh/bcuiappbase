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

package com.bc.appbase.ui.builder.impl;

import com.bc.appbase.ui.builder.PromptUserSelectOrCreateNew;
import com.bc.appbase.App;
import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.VerticalLayout;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.jpa.dao.Criteria;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.search.TextSearch;
import java.awt.Container;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 13, 2017 11:20:51 AM
 */
public class PromptUserSelectOrCreateNewSelectionType 
        extends PromptUserCreateNewSelectionType 
        implements PromptUserSelectOrCreateNew{

    private static final Logger logger = Logger.getLogger(PromptUserSelectOrCreateNewSelectionType.class.getName());

    private final String OK = "OK";
    private final String CREATE_NEW = "Create New";
    private final String [] OPTIONS = {OK, CREATE_NEW};
    
    private final Map<String, Object> cache;
    
    public PromptUserSelectOrCreateNewSelectionType(App app) {
        super(app);
        this.cache = new HashMap<>();
    }
    
    @Override
    public boolean isValidForSelectOption(Class entityType, String column, Object value) {
        final Class columnType = this.getTypeProvider().getType(entityType, column, null, null);
        return this.isValidForSelectOption(entityType, columnType, column, value);
    }
    
    @Override
    public boolean isValidForSelectOption(Class entityType, Class columnType, String column, Object value) {
        final boolean accept;
        if(value == null) {
            accept = false;
        }else if(value.getClass().getAnnotation(Entity.class) != null) {    
            accept = false;
        }else{
            accept = this.isValidForCreateNewOption(columnType);
        }
//System.out.println("Valid for selection: "+accept+", entity type: "+entityType.getName()+", "+columnType.getName()+" "+column+" = "+value);        
        return accept;
    }
    
    @Override
    public <T> T execute(Class entityType, Class<T> columnType, 
            String column, Object value, T outputIfNone) 
            throws EntityInstantiationException {
        
        if(!this.isValidForSelectOption(entityType, columnType, column, value)) {
        
            throw new UnsupportedOperationException();
        }
        
        final T output;
        
        final String KEY = this.getCacheKey(columnType, value);
        
        final T cached = (T)this.cache.get(KEY);
        
        if(cached != null) {
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Loaded from cache. {0} = {1}", new Object[]{KEY, cached});
            }
            
            output = cached;
            
        }else{
        
            final List<T> found = this.getApp().getJpaContext().getBuilderForSelect(columnType).getResultsAndClose();

            if(found == null || found.isEmpty()) {

                output = this.promptCreateNew(columnType, outputIfNone);

            }else{

                output = this.promptSelectOrCreateNew(entityType, columnType, column, value, outputIfNone);
            }
            
            if(output != null) {
                
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Adding to cache. {0} = {1}", new Object[]{KEY, output});
                }

                this.cache.put(KEY, output);
            }
        }
        
        return output;
    }
    
    public String getCacheKey(Class columnType, Object value) {
        Objects.requireNonNull(value);
        return columnType.getName() + value;
    }
    
    public <T> T promptSelectOrCreateNew(Class entityType, Class<T> columnType, 
            String column, Object value, T outputIfNone) 
            throws EntityInstantiationException {
        
        if(!this.isValidForSelectOption(entityType, columnType, column, value)) {
        
            throw new UnsupportedOperationException();
        }
        
        final App app = this.getApp();

        final T searchResult = this.search(columnType, column, value, null);
        
        if(searchResult != null) {
            
            return searchResult;
        }
        
        final Map inputMap = Collections.singletonMap(column, null);

        final UIBuilderFromMap mapUIBuilder = app.getOrException(UIBuilderFromMap.class);

        final String columnTypeSimpleName = columnType.getSimpleName();
        final JLabel selectionMessage = new JLabel("<html><div style=\"font-size:1.2em;\"><tt>Select "
                +columnTypeSimpleName+"</tt> matching: <b>"+value+
                "</b><br/>Or click <tt>"+CREATE_NEW+"</tt> to add a new "+columnTypeSimpleName+"</div></html>");
        
        final Container selectionUI = mapUIBuilder
                .sourceType(entityType)
                .sourceData(inputMap)
                .build();
        final JPanel selectionDisplay = new JPanel();
        new VerticalLayout().addComponents(selectionDisplay, Arrays.asList(selectionMessage, selectionUI));

        final String TITLE = "Select "+columnTypeSimpleName+" or Create New";
        final int SELECTED_OPTION = JOptionPane.showOptionDialog(
                app.getUIContext().getMainFrame(), selectionDisplay, TITLE, 
                -1, JOptionPane.PLAIN_MESSAGE, null, OPTIONS, value);
        
        if(SELECTED_OPTION == JOptionPane.CLOSED_OPTION) {
            return null;
        }
        
        final T output;
   
        final String SELECTED = OPTIONS[SELECTED_OPTION];
        
        if(SELECTED.equals(OK)) {
            
            final LinkedHashMap<String, String> selections = (LinkedHashMap<String, String>)app.getOrException(FromUIBuilder.class)
                    .componentModel(app.getOrException(ComponentModel.class))
                    .ui(selectionUI)
                    .source(inputMap)
                    .target(new LinkedHashMap<>())
                    .build();
            
            final Object selection = selections.get(column);
            
            if(selection == null) {
                
                output = outputIfNone;
                
            }else{
                
                output = (T)selection;
            }
            
        }else if(SELECTED.equals(CREATE_NEW)) {
            
            output = this.promptCreateNew(columnType, outputIfNone);
            
        }else{
            
            output = outputIfNone;
        }
        
        return output;
    }
    
    public <T> T search(Class valueType, String column, Object value, T outputIfNone) {
        
        final List<T> searchResults = this.search(valueType, column, value);
        
        final T output;
        
        if(searchResults != null && searchResults.size() == 1) {
            
            output = searchResults.get(0);
            
        }else{
            
            output = null;
        }
        
        return output == null ? outputIfNone : output;
    }

    public <T> List<T> search(Class valueType, String column, Object value) {
        
        final App app = this.getApp();
        
        final TextSearch textSearch = app.getJpaContext().getTextSearch();
        
        final List<T> equals = textSearch.search(valueType, value.toString(), Criteria.ComparisonOperator.EQUALS);
        
        final List<T> output;
        
        if(equals != null && !equals.isEmpty()) {
//System.out.println("Value: "+value+", found using EQUALS: "+found);                    
            output = equals;
            
        }else{
        
            final List<T> like = textSearch.search(valueType, value, 2, 0.67f);
//System.out.println("Value: "+value+", found using LIKE: "+found);        
            if(like != null && !like.isEmpty()) {

                output = like;
                
            }else{
                
                output = Collections.EMPTY_LIST;
            }
        }
        
        return output;
    }
}
