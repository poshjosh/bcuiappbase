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
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
import com.bc.appcore.functions.BuildEntityStructure;
import java.awt.Container;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2017 9:38:51 PM
 */
public class UIBuilderFromEntityImpl extends AbstractUIBuilder<UIBuilderFromEntity, Object> 
        implements UIBuilderFromEntity {

    private static final Logger logger = Logger.getLogger(UIBuilderFromEntityImpl.class.getName());
    
    private App app;
    private Boolean addOptionToViewRelated;
    
    public UIBuilderFromEntityImpl() { }
    
    @Override
    public UIBuilderFromEntity app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public UIBuilderFromEntity addOptionToViewRelated(boolean b) {
        this.addOptionToViewRelated = b;
        return this;
    }

    @Override
    public Container build() {
        
        if(this.addOptionToViewRelated == null) {
            this.addOptionToViewRelated = this.getSourceData() != null;
        }

        Container container = super.build();
        
        if(this.addOptionToViewRelated) {
            
            container = this.app.getOrException(AddAccessToViewRelatedTypes.class).apply(
                    this.getSourceData(), container);
        }
        
        return container;
    }
    
    @Override
    public boolean build(Class entityType, Object entity, Container container) {
        
        final Map structure = app.getOrException(BuildEntityStructure.class).apply(entityType, entity);

        logger.finer(() -> app.getJsonFormat().toJSONString(structure));

        this.build(structure, container);
        
        return true;
    }
    
    public void build(Map structure, Container container) {
    
        final UIBuilder mapUIBuilder = app.getOrException(UIBuilderFromMap.class);
        
        final Container ui = (Container)mapUIBuilder
                .sourceType(this.getSourceType())
                .sourceData(structure)
                .targetUI(container)
                .selectionContext(this.getSelectionContext())
                .typeProvider(this.getTypeProvider()) 
                .componentModel(this.getComponentModel())
                .editable(this.isEditable())      
                .build();
        
        app.getExpirableAttributes().putFor(ui, structure);
    }
}
