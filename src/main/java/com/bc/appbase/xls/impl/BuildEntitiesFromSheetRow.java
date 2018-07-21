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

import com.bc.appcore.jpa.EntityStructureFactory;
import com.bc.appbase.xls.CellResult;
import com.bc.appcore.ObjectFactory;
import com.bc.util.JsonFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 2, 2017 3:54:01 PM
 */
public class BuildEntitiesFromSheetRow implements Function<List<CellResult>, List>{

    private static final Logger logger = Logger.getLogger(BuildEntitiesFromSheetRow.class.getName());
    
    private final ObjectFactory objectFactory;
    private final Class entityType;
    private final EntityStructureFactory entityStructureFactory;
    
    public BuildEntitiesFromSheetRow(ObjectFactory objectFactory, Class entityType) {
        this.objectFactory = Objects.requireNonNull(objectFactory);
        this.entityType = Objects.requireNonNull(entityType);
        this.entityStructureFactory = objectFactory.getOrException(EntityStructureFactory.class);
    }

    @Override
    public List apply(List<CellResult> rowResults) {
//System.out.println("Results: " + rowResults.size() + ". @"+this.getClass());   
        final Map entityDataBuffer = entityStructureFactory.getNested(entityType);
        
        for(CellResult cellResult : rowResults) {

            final Cell cell = cellResult.getCell();
            
            final String [] databaseColumnNames = cellResult.getDatabaseColumnNames();
            
            final Object [] results = cellResult.isMultiResultCell() ? 
                    cellResult.getMultipleResults() : new Object[]{cellResult.getSingleResult()};
            
            final int SIZE = Math.min(databaseColumnNames.length, results.length);
            
            for(int i=0; i<SIZE; i++) {
                
                this.addToBuffer(cell.getRow(), cell.getColumn(), databaseColumnNames[i], results[i], entityDataBuffer);
            }
        }

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Entity type: {0}, structure:\n{1}",
                    new Object[]{entityType.getName(), new JsonFormat(true, true, "  ").toJSONString(entityDataBuffer)});
        }

        final List entities = this.build(this.entityType, entityDataBuffer);
        
        return entities;
    }

    public void addToBuffer(int row, int col, String dbCol, Object result, Map collectInto) {
//System.out.println("@["+row+':'+col+"]. @"+this.getClass());  
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "[{0}:{1}].{2} = {3}", new Object[]{row, col, dbCol, result});
        }

        this.entityStructureFactory.add(collectInto, dbCol, result);
    }
    
    public List build(Class entityType, Map<String, Object> data) {
        return this.entityStructureFactory.buildEntities(entityType, data);
    }
    
    public Class getEntityType() {
        return entityType;
    }

    public EntityStructureFactory getEntityStructureFactory() {
        return entityStructureFactory;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
