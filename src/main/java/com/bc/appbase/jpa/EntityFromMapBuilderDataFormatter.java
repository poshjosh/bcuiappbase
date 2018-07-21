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

package com.bc.appbase.jpa;

import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.builder.PromptUserSelectOrCreateNew;
import com.bc.appcore.ObjectFactory;
import com.bc.reflection.TypeProvider;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.search.TextSearch;
import com.bc.jpa.util.EntityFromMapBuilder;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 13, 2017 10:22:50 AM
 */
public class EntityFromMapBuilderDataFormatter implements EntityFromMapBuilder.Formatter{

    private static final Logger logger = Logger.getLogger(EntityFromMapBuilderDataFormatter.class.getName());
    
    private final PersistenceUnitContext puContext;
    
    private final UIContext uiContext;
    
    private final PromptUserSelectOrCreateNew prompt;
    
    private final TypeProvider typeProvider;
    
    public EntityFromMapBuilderDataFormatter(
            ObjectFactory objectFactory, PersistenceUnitContext puContext, UIContext uiContext) {
        Objects.requireNonNull(objectFactory);
        this.puContext = Objects.requireNonNull(puContext);
        this.uiContext = uiContext;
        this.prompt = objectFactory.getOrException(PromptUserSelectOrCreateNew.class);
        this.typeProvider = objectFactory.getOrException(TypeProvider.class);
    }

    @Override
    public Object format(Object entity, String column, Object value) {
        
        return this.format(entity.getClass(), column, value);
    }    
        
    public Object format(Class entityType, String column, Object value) {        

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0}#{1} = {2}", 
                    new Object[]{entityType.getName(), column, value});
        }

        if(value != null) {
            
            if(value instanceof String && value.toString().isEmpty()) {
                
                value = null;
                
            }else {    
                
                if(this.prompt.isValidForSelectOption(entityType, column, value)) {    

                    final Class columnType = this.typeProvider.getType(entityType, column, null, null);
                    
                    if(value instanceof Map) {
                        
                        final Map map = (Map)value;
                        
                        Map copy = new HashMap(map);
                        copy.values().removeIf((val) -> val == null); 
                        
                        if(copy.size() == 1) {
                            value = copy.values().iterator().next();
                        }
                    }
                    
                    value = this.promptSelectOrCreateNew(entityType, columnType, column, value);   
                }
            }
        }
        
        return value;
    }
    
    public Object promptSelectOrCreateNew(Class entityType, Class columnType, String column, Object value) { 
        
//System.out.println(entityType.getSimpleName()+". "+columnType.getSimpleName()+" "+column+" = "+value+". @"+this.getClass());

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} has {1} {2} = {3}", new Object[]{entityType, columnType, column, value});
        }
        
        final TextSearch textSearch = this.puContext.getTextSearch();
        
        final List foundList = textSearch.search(columnType, String.valueOf(value).trim());
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} results for {1} searched in {2}",
                    new Object[]{foundList==null?null:foundList.size(), value, columnType.getName()});
        }
        
        if(foundList == null || foundList.isEmpty()) {

            value = this.doPromptSelectOrCreateNew(entityType, columnType, column, value);

        }else if(foundList.size() == 1) {

            final Object found = foundList.get(0);

            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Found: {0}. for {1} {2} = {3}", 
                        new Object[]{found, columnType.getName(), column, value});
            }

            value = found;

        }else{

            value = this.doPromptSelectOrCreateNew(entityType, columnType, column, value);
        }

        Objects.requireNonNull(value, "Could not generate value of appropriate type from " 
                + column + '=' + value + ", of type " + columnType + ", in " + entityType);
        
        return value;
    }
    
    public Object doPromptSelectOrCreateNew(Class entityType, Class columnType, String column, Object value) {
        Object result = null;
        do{
            try{
                result = prompt.execute(entityType, columnType, column, value, null);
            }catch(EntityInstantiationException e) {
                final String errorMessage = "Error adding new " + columnType.getSimpleName();
                logger.log(Level.WARNING, errorMessage, e);
                this.uiContext.showErrorMessage(e, errorMessage);
                break;
            }
            if(result != null) {
                break;
            }else{
                this.uiContext.showErrorMessage(null, "Please select a `" + columnType.getSimpleName()+"`, or select `"+CREATE_NEW+"`");
            }
        }while(true);
        return result;
    }
}
/**
 * 
//System.out.println("- - - - - Value: "+columnType.getSimpleName()+' '+value+". @"+this.getClass());                    
                    while(value instanceof Map) {
                        Object nonNullValue = null;
                        final Map map = (Map)value;
                        for(Object entryObj : map.entrySet()) {
                            final Map.Entry entry = (Map.Entry)entryObj;
                            final Object key = entry.getKey();
                            final Object val = entry.getValue();
                            if(val != null) {
                                nonNullValue = val;
                                if(val instanceof Map) {
                                    try{
                                        columnType = columnType.getDeclaredField(key.toString()).getType();
                                    }catch(NoSuchFieldException e) { 
                                        logger.log(Level.WARNING, "Program should continue working fine", e);
                                    }
                                }
                                break;
                            }
                        }
//System.out.println("- - - - - Updated "+columnType.getSimpleName()+' '+nonNullValue+". @"+this.getClass());                         
                        if(nonNullValue == null) {
                            throw new UnsupportedOperationException("Empty container not allowed");
                        }else{
                            value = nonNullValue;
                        }
                    }
 * 
 */