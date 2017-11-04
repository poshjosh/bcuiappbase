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

package com.bc.appbase.ui.components;

import com.bc.appbase.App;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.jpa.search.ListSearchResults;
import com.bc.jpa.search.SearchResults;
import java.awt.Component;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 9, 2017 6:03:07 PM
 */
public class CollectionAsSearchResultsPanelProvider extends AbstractCollectionComponentProvider {

    private static final Logger logger = Logger.getLogger(CollectionAsSearchResultsPanelProvider.class.getName());

    public CollectionAsSearchResultsPanelProvider(App app, ComponentModel.ComponentProperties props, int batchSize) {
        super(app, props, batchSize);
    }

    @Override
    public Component execute(Class parentType, Class valueType, String name, Collection value) {
        
        logger.finer(() -> MessageFormat.format("Type: {0}, Field: {1} {2} = {3}", 
                parentType.getName(), valueType.getSimpleName(), name, value));
              
        final Component component;
            
        final int serialColumnIndex = -1; 

        final Class collectionValueType = this.getCollectionValueType(parentType, name);

        final SearchResultsPanel panel = new SearchResultsPanel();

        final UIContext uiContext = this.getApp().getUIContext();

        panel.init(uiContext);

        final SearchResults searchResults = new ListSearchResults(
                this.toEntitiesList((Collection)value, collectionValueType), this.getBatchSize()
        );
        panel.load(this.getApp().getSearchContext(collectionValueType), searchResults, null);

        final JTable table = panel.getSearchResultsTable();
        table.setName(name);

        uiContext.setTableFont(table);

        component = new JScrollPane(panel);

        final Dimension dim = this.getCollectionDimension(table, value);

        logger.finer(() -> MessageFormat.format("For {0}, dimension = {1} of field: {2} {3} = {4}", 
                parentType.getName(), dim, valueType.getSimpleName(), name, value));

        component.setPreferredSize(dim);
        panel.setPreferredSize(dim);
        table.setPreferredSize(dim);

        uiContext.updateTableUI(table, collectionValueType, serialColumnIndex);

//            component.setName(name);
        
        return component;
    }
}
