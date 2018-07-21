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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 21, 2017 10:32:48 PM
 */
public class ExecuteUpdateQuery extends AbstractExecuteQuery {

    @Override
    public Integer execute(App app, String sql, Class resultType) {
        
        final EntityManager em = app.getActivePersistenceUnitContext().getEntityManager();
        final Query query = em.createNativeQuery(sql, Integer.class);
        final int UPDATE_COUNT = query.executeUpdate();
        
        final StringBuilder msg = new StringBuilder();
        msg.append("<html>");
        msg.append(sql);
        
        final String PREFIX = UPDATE_COUNT == 1 ? "row afftected" : "rows affected";
        msg.append("<br/><tt>").append(UPDATE_COUNT).append(' ').append(PREFIX).append("</tt>");
        
        msg.append("</html>");
        
        return UPDATE_COUNT;
    }
}
