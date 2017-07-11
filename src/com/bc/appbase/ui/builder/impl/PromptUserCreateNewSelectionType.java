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

import com.bc.appbase.App;
import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.builder.PromptUserCreateNew;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.util.MapBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 4, 2017 10:12:07 PM
 */
public class PromptUserCreateNewSelectionType implements PromptUserCreateNew {

    private static final Logger logger = Logger.getLogger(PromptUserCreateNewSelectionType.class.getName());

    private final App app;
    private final TypeProvider typeProvider;
    private final SelectionContext selectionContext;

    public PromptUserCreateNewSelectionType(App app) {
        this.app = Objects.requireNonNull(app);
        this.typeProvider = app.getOrException(TypeProvider.class);
        this.selectionContext = app.getOrException(SelectionContext.class);
    }
    
    @Override
    public boolean isValidForCreateNewOption(Class entityType, String column) {
        final Class columnType = this.typeProvider.getType(entityType, column, null, null);
        return this.isValidForCreateNewOption(columnType);
    }
    
    @Override
    public boolean isValidForCreateNewOption(Class columnType) {
//System.out.println("Parent type: "+entityType.getSimpleName()+", valueType: "+columnType.getSimpleName()+", name: "+column);                        
        final boolean valid;
        if(columnType.getAnnotation(Entity.class) != null) {
            valid = this.selectionContext.isSelectionType(columnType);
        }else{
            valid = false;
        }
        
        logger.log(Level.FINE, () -> "Valid for create new: "+valid+", entity type: "+columnType.getName());                
        
        return valid;
    }
    
    @Override
    public <T> T promptCreateNew(Class<T> columnType, T outputIfNone) 
            throws EntityInstantiationException {
        
        if(!this.isValidForCreateNewOption(columnType)) {
        
            throw new UnsupportedOperationException();
        }

        final Map structure = app.getOrException(MapBuilder.class).maxCollectionSize(0).sourceType(columnType).build();
        
        logger.log(Level.FINER, () -> "Structure:\n" + app.getJsonFormat().toJSONString(structure));
        
        final JPanel creationUI = new JPanel();
        
        final UIBuilderFromEntity uiBuilder = app.getOrException(UIBuilderFromEntity.class).sourceType(columnType);
        
        uiBuilder.build(structure, creationUI);
        
        final String columnTypeSimpleName = columnType.getSimpleName();
        
        final int option = JOptionPane.showConfirmDialog(app.getUIContext().getMainFrame(), 
                creationUI, "Enter Details of "+columnTypeSimpleName, 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if(option != JOptionPane.OK_OPTION) {
            
            return outputIfNone;
        }

        final Map data = (Map)app.getOrException(FromUIBuilder.class)
                    .componentModel(app.getOrException(ComponentModel.class))
                .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                .ui(creationUI)
                .source(structure)
//                .targetUI(new LinkedHashMap())
                .build();
        
        logger.log(Level.FINE, "Data: {0}", data);

        final EntityUpdater<T, ?> updater = app.getJpaContext().getEntityUpdater(columnType);

        final T columnEntity = this.newInstance(columnType);

        updater.update(columnEntity, data, true);

        app.getDao(columnType).begin().persistAndClose(columnEntity);
        app.getSlaveUpdates().addPersist(columnEntity);

        app.getUIContext().showSuccessMessage("Successfully added "+columnTypeSimpleName);

        logger.log(Level.FINE, "Persisted: {0}", columnEntity);

        return columnEntity;
    }
    
    public <T> T newInstance(Class<T> entityType) {
        try{
            return entityType.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public App getApp() {
        return app;
    }

    public TypeProvider getTypeProvider() {
        return typeProvider;
    }

    public SelectionContext getSelectionContext() {
        return selectionContext;
    }
}
