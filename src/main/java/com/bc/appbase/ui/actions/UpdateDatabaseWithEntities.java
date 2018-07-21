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
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.RelationAccess;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.Dao;
import com.bc.util.JsonFormat;
import com.bc.util.MapBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import com.bc.jpa.EntityMemberAccess;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 23, 2017 8:49:29 PM
 */
public class UpdateDatabaseWithEntities implements Action<App, Collection> {

    private static final Logger LOG = Logger.getLogger(UpdateDatabaseWithEntities.class.getName());

    @Override
    public Collection execute(App app, Map<String, Object> params) throws ParameterException, TaskExecutionException {
       
        final ParameterExtractor parameterExtractor = app.getOrException(ParameterExtractor.class);
        
        final Collection entities = (Collection)parameterExtractor.getFirstValue(params, Collection.class);
        
        final List entityList = new ArrayList(entities);
        
        Collections.sort(entityList, app.getEntityOrderComparator());
        
        return this.updateDatabase(app, entityList);
    }
    
    public List updateDatabase(App app, List entities) {
        
        final RelationAccess relationAccess = app.getOrException(RelationAccess.class);
      
        relationAccess.update(entities, false);
        
        final PersistenceUnitContext jpaContext = app.getActivePersistenceUnitContext();
        
        try(Dao dao = jpaContext.getDao()) {
        
            dao.begin();
            
            for(Object entity : entities) {
                
                final Object existing = this.find(jpaContext, dao, entity, null);
                
                final boolean persistNotMerge = existing == null;
                
                if(LOG.isLoggable(Level.FINER)) {
                    final String json = new JsonFormat(true, true, "  ").toJSONString(
                            app.getOrException(MapBuilder.class).maxDepth(1).nullsAllowed(true).source(entity).build()
                    );
                    LOG.log(Level.FINER, persistNotMerge ? "Persisting: {0}" : "Merging: {0}", json); 
                }else{
                    LOG.log(Level.FINE, persistNotMerge ? "Persisting: {0}" : "Merging: {0}", entity); 
                }

                if(persistNotMerge) {

                    dao.persist(entity);
                    
                }else{
                    
                    this.edit(jpaContext, entity);
//                    dao.merge(entity); 
                }
            }
  
            dao.commit();
        }
        
        return entities;
    }
    
    public void edit(PersistenceUnitContext puContext, Object entity) {
        puContext.getDao().begin().mergeAndClose(entity);
//        final EntityManager em = puContext.getEntityManager();
//        this.edit(em, entity);
    }
    
    public Object find(PersistenceUnitContext puContext, Dao dao, Object entity, Object outputIfNone) {
        
        final Class entityType = entity.getClass();

        final EntityMemberAccess updater = puContext.getEntityMemberAccess(entityType);

        final Object id = updater.getId(entity);
        
        final Object found;
        
        if(id == null) {
            
            found = null;
            
        }else {
            
            found = dao.find(entityType, id);
        }

        return found == null ? outputIfNone : found;
    }

    private void edit(EntityManager em, Object entity) {
        try {
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                entity = em.merge(entity); 
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
