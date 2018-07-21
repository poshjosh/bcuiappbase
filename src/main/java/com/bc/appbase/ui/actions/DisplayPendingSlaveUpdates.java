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
import com.bc.ui.layout.VerticalLayout;
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.jpa.model.EntityResultModelImpl;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.table.model.EntityTableModelImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.bc.appcore.jpa.model.EntityResultModel;
import com.bc.appcore.util.PendingDatabaseUpdate;
import com.bc.appcore.util.TargetQueue;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 12, 2017 9:53:16 AM
 */
public class DisplayPendingSlaveUpdates implements Action<App, Integer> {

    private static final Logger logger = Logger.getLogger(DisplayPendingSlaveUpdates.class.getName());
    
    protected TargetQueue getPendingUpdateQueue(App app) {
        return app.getPendingSlaveUpdateQueue();
    }
    
    protected Set<Class> getEntityTypes(App app) {
        return app.getMasterSlavePersistenceContext().getMaster().getMetaData().getEntityClasses();
    }

    @Override
    public Integer execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final TargetQueue<PendingDatabaseUpdate> updateQueue = this.getPendingUpdateQueue(app);
        
        final int size = updateQueue.getElementCount();
        
        if(size < 1) {
            
            app.getUIContext().showSuccessMessage("No pending updates available");
            
        }else{    
        
            final List<PendingDatabaseUpdate> pendingUpdates = updateQueue.getElements();
            
            final Set<Class> entityTypes = this.getEntityTypes(app);
            
            final VerticalLayout layout = new VerticalLayout();
            
            for(Class entityType : entityTypes) {
                
                final JTable table = this.getTable(app, pendingUpdates, entityType);
                
                layout.addComponent(table);
            }
            
            final JPanel tablesPanel = new JPanel();
            
            layout.addComponents(tablesPanel);
            
            final JFrame frame = new JFrame("Pending Updates");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JScrollPane(tablesPanel));
            
            app.getUIContext().positionHalfScreenRight(tablesPanel);
            app.getUIContext().positionHalfScreenRight(frame);
            
            frame.pack();
            
            frame.setVisible(true);
        }
        
        return size;
    }
    
    public JTable getTable(App app, List<PendingDatabaseUpdate> pendingUpdates, Class entityType) {
        
        final List entities = this.getEntities(pendingUpdates, entityType);

        final String [] columnNames = app.getTableColumnNames(entityType);

        final EntityResultModel resultModel = new EntityResultModelImpl(app, entityType, Arrays.asList(columnNames),
                (column, value) -> false, (column, exception) -> logger.log(Level.WARNING, "Error updating: " + column, exception)
        );
        
        final TableModel tableModel = new EntityTableModelImpl(entities, resultModel);

        final JTable table = new JTable(tableModel);
        
        return table;
    }
    
    public List getEntities(List<PendingDatabaseUpdate> pendingUpdates, Class entityType) {
        final Predicate<PendingDatabaseUpdate> isEntityType = (pu) -> entityType.isAssignableFrom(pu.getEntity().getClass());
        final List entities = pendingUpdates.stream().filter(isEntityType).map((pu) -> pu.getEntity()).collect(Collectors.toList());
        return entities;
    }
}
