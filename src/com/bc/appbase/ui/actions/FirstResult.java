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

import com.bc.appcore.actions.Action;
import com.bc.appbase.ui.SearchResultsPanel;
import java.awt.Container;
import java.util.Map;
import javax.swing.JTable;
import com.bc.appbase.App;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.exceptions.SearchResultsNotFoundException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 24, 2017 8:32:10 AM
 */
public class FirstResult implements Action<App, JTable> {

    @Override
    public JTable execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final JTable table = (JTable)params.get(JTable.class.getName());
        
        Container parent = table.getParent();
        while( ! (parent instanceof SearchResultsPanel) ) {
            parent = parent.getParent();
        }
        
        try{
            ((SearchResultsPanel)parent).loadSearchResultsPages(app.getUIContext(), app.getSearchContext(null), 0, 1);
        }catch(SearchResultsNotFoundException e) {
            throw new TaskExecutionException(e);
        }
        
        return table;
    }
}
