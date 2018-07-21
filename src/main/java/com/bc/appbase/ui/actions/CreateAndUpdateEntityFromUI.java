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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appcore.jpa.EntityStructureFactory;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.builder.FromUIBuilder;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.exceptions.TargetNotFoundException;
import com.bc.selection.SelectionContext;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.RelationAccess;
import java.awt.Window;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.JComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 17, 2017 8:41:25 AM
 */
public class CreateAndUpdateEntityFromUI<E> implements Action<App, E> {

    private static final Logger logger = Logger.getLogger(CreateAndUpdateEntityFromUI.class.getName());
    
    public void formatUpdate(App app, Map entityData) { }
    
    @Override
    public E execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final ParameterExtractor parameterExtractor = app.getOrException(ParameterExtractor.class);
        final JComponent ui = parameterExtractor.getFirstValue(params, JComponent.class);
     
        final Class entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        
        return this.execute(app, entityType, ui);
    }    
                
    public E execute(App app, Class entityType, JComponent ui) 
            throws ParameterException, TaskExecutionException {
            
        final Window window = (Window)ui.getTopLevelAncestor();
        
        try{
            
            window.setVisible(false);
            
            final Map inputStructure = (Map)app.getExpirableCache().remove(ui).get().get();

            logger.log(Level.FINER, "Source structure: {0}", app.getJsonFormat().toJSONString(inputStructure));

            final Map entityUpdate = (Map)app.getOrException(FromUIBuilder.class)
                    .componentModel(app.getOrException(ComponentModel.class))
                    .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                    .ui(ui)
                    .source(inputStructure)
    //                .target(new LinkedHashMap())
                    .build();

            logger.log(Level.FINE, "Source data: {0}", app.getJsonFormat().toJSONString(entityUpdate));
            
            final Object selectedEntity = this.getSelectedEntity(app, ui.getName(), null);

            logger.log(Level.FINE, "Selected row: {0}", selectedEntity);
            
            final List entities = this.buildEntities(app, selectedEntity, entityType, entityUpdate);
            
            this.updateDatabase(app, entities);
            
            app.getUIContext().showSuccessMessage("Success");
            
            app.getAction(ActionCommands.REFRESH_ALL_RESULTS).executeSilently(app);

            E output = null;
            for(Object element : entities) {
                if(entityType.isAssignableFrom(element.getClass())) {
                    output = (E)element;
                    break;
                }
            }
            
            return output;
            
        }finally{
            
            window.dispose();
        }
    }
    
    public List buildEntities(App app, Object selectedEntity, Class entityClass, Map entityData) {
        
        final List entities = app.getOrException(EntityStructureFactory.class).buildEntities(entityClass, entityData);
        
        if(selectedEntity != null) {
            
            final SelectionContext selectionContext = app.getOrException(SelectionContext.class);
            final Predicate<Class> isNotSelectionType = (cls) -> !selectionContext.isSelectionType(cls);
        
            this.updateAllWith(app, entities, isNotSelectionType, selectedEntity);
        }
        
        return entities;
    }
    
    public void updateAllWith(App app, Collection entities, Predicate<Class> relatedTypeTest, Object update) {

        Objects.requireNonNull(entities);
        Objects.requireNonNull(relatedTypeTest);
        Objects.requireNonNull(update);
        
        final RelationAccess relationAccess = app.getOrException(RelationAccess.class);

        final Predicate<Class> acceptEntityType = (cls) -> cls.getAnnotation(Entity.class) != null;
        
        for(Object entity : entities) {

            final Set<Class> relatedTypes = relationAccess.getChildTypes(entity.getClass(), acceptEntityType);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Entity: {0}, related types: {1}", 
                        new Object[]{entity, relatedTypes});
            }

            for(Class relatedType : relatedTypes) {

                final Object related = relationAccess.getFirst(update, relatedType, null);

                if(related != null) {

                    if(logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "Found: {0}, for: {1}, from: {2}", 
                                new Object[]{related, entity, update});
                    }

                    relationAccess.setOrException(entity, relatedType, related);
                }
            }
        }
    }
    
    public Collection updateDatabase(App app, Collection entities) 
            throws ParameterException, TaskExecutionException {
        
        final Map<String, Object> params = new HashMap(4, 0.75f);
        params.put("entities", entities);
        
        return (Collection)app.getAction(ActionCommands.UPDATE_DATABASE_WITH_ENTITIES).execute(app, params);
    }

    public Object getSelectedEntity(App app, String name, Object outputIfNone) {

        Object output;

        try{
            
            
            final Map<Object, Class> singletonMap = app.removeExpirable(Map.class, name);
            
            Objects.requireNonNull(singletonMap, "{String id, Class selectedEtityType} entry is null for "+name);
            
            final Map.Entry<Object, Class> singletonEntry = (Map.Entry<Object, Class>)singletonMap.entrySet().iterator().next();

            final Class selectedEntityType = singletonEntry.getValue();

            if(selectedEntityType == null) {

                output = outputIfNone;

            }else{

                final Object selectedId = singletonEntry.getKey();

                final Object selectedEntity = app.getDao().find(selectedEntityType, selectedId);

                output = selectedEntity;
            }
        }catch(TargetNotFoundException e) {
            output = outputIfNone;
        }
        
        return output;
    }    
}
