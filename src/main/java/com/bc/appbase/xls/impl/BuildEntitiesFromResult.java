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

import com.bc.appcore.ObjectFactory;
import com.bc.jpa.context.JpaContext;
import com.bc.appcore.jpa.EntityStructureFactory;
import com.bc.appbase.xls.SheetToDatabaseMetaData;
import com.bc.util.JsonFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;

/**
 * @author Chinomso Bassey Ikwuagwu on May 11, 2017 9:41:51 PM
 */
public class BuildEntitiesFromResult<T> implements BiConsumer<Cell, Object> {

    private static final Logger logger = Logger.getLogger(BuildEntitiesFromResult.class.getName());
    
    private final ObjectFactory objectFactory;
    private final JpaContext jpaContext;
    private final Class<T> entityType;
    private final SheetToDatabaseMetaData metaData;
    private final Consumer<Collection> resultHandler;
    private final EntityStructureFactory entityStructureFactory;
    private final Integer lastColumn;
    private int previousColumn = -1;
    private int indexInCurrentColumn = 0;
    private Map currentEntityData;
    
    public BuildEntitiesFromResult(ObjectFactory objectFactory, JpaContext jpaContext, 
            Class<T> entityType, SheetToDatabaseMetaData metaData, Consumer<Collection> resultHandler) {
        this.objectFactory = Objects.requireNonNull(objectFactory);
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.entityType = Objects.requireNonNull(entityType);
        this.metaData = Objects.requireNonNull(metaData);
        this.resultHandler = Objects.requireNonNull(resultHandler);
        this.entityStructureFactory = objectFactory.getOrException(EntityStructureFactory.class);
        this.lastColumn = metaData.getExcelColumnIndices().get(metaData.getExcelColumnIndices().size() - 1);
        this.currentEntityData = entityStructureFactory.getNested(entityType);
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Entity type: {0}, structure:\n{1}",
                    new Object[]{entityType.getName(), new JsonFormat(true, true, "  ").toJSONString(this.currentEntityData)});
        }
    }

    @Override
    public void accept(Cell cell, Object result) {
                
            final int row = cell.getRow();
            final int col = cell.getColumn();
//System.out.println("@["+row+':'+col+"]. @"+this.getClass());  
            if(col != this.previousColumn) {
                this.indexInCurrentColumn = 0;
            }
           
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "[{0}:{1}] = {1}", new Object[]{row, col, result});
            }

            final List<String> dbCols = this.metaData.getColumnNameList(cell);
            
            final String dbCol = dbCols.get(this.indexInCurrentColumn);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "[{0}:{1}].{2} = {3}", new Object[]{row, col, dbCol, result});
            }
            
            this.entityStructureFactory.add(this.currentEntityData, dbCol, result);

            if(col == lastColumn) {
System.out.println("Processing row: " + row + ", type: " + this.entityType.getSimpleName() + "\nData: " + this.currentEntityData + ". @" + this.getClass());
                try{

                    final List entities = this.build(this.entityType, this.currentEntityData);

                    this.resultHandler.accept(entities);

                }finally{
//System.out.println("@["+row+':'+col+"]."+dbCol+" = " + result+". CLEARING BUFFERS @"+this.getClass()); 
                    this.currentEntityData = this.entityStructureFactory.getNested(entityType);
                }
            }
            
            ++this.indexInCurrentColumn;
            this.previousColumn = col;
    }
    
    public List build(Class entityType, Map<String, Object> data) {
        return this.entityStructureFactory.buildEntities(entityType, data);
    }
    
    public Class<T> getEntityType() {
        return entityType;
    }

    public SheetToDatabaseMetaData getSheetToDatabaseMetaData() {
        return metaData;
    }

    public EntityStructureFactory getEntityStructureFactory() {
        return entityStructureFactory;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public JpaContext getJpaContext() {
        return jpaContext;
    }
}
