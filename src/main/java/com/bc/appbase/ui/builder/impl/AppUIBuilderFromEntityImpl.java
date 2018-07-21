/*
 * Copyright 2018 NUROX Ltd.
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

import com.bc.appbase.ui.builder.AppUIBuilderFromEntity;
import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
import com.bc.ui.builder.UIBuilderFromMap;
import com.bc.ui.builder.impl.UIBuilderFromEntityImpl;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2018 8:47:44 PM
 */
public class AppUIBuilderFromEntityImpl extends UIBuilderFromEntityImpl implements AppUIBuilderFromEntity {

    private Boolean addOptionToViewRelated;
    private BiConsumer<String, Collection> relatedTypeConsumer;
    private AddAccessToViewRelatedTypes relatedTypeAccess;

    public AppUIBuilderFromEntityImpl(
            BiFunction<Class, Object, Map> buildEntityStructure, 
            UIBuilderFromMap uiFromMapBuilder) {
        super(buildEntityStructure, uiFromMapBuilder);
    }
    
    @Override
    public Container build() {
        
        if(this.addOptionToViewRelated == null) {
            this.addOptionToViewRelated = this.getSourceData() != null;
        }
        
        Container container = super.build();
        
        if(this.addOptionToViewRelated) {
            
            container = this.relatedTypeAccess
                    .consumer(relatedTypeConsumer)
                    .apply(this.getSourceData(), container);
        }
        
        return container;
    }
    
    @Override
    public AppUIBuilderFromEntity addOptionToViewRelated(boolean b) {
        this.addOptionToViewRelated = b;
        return this;
    }
    
    @Override
    public AppUIBuilderFromEntity relatedTypesAccess(AddAccessToViewRelatedTypes relatedTypeAccess) {
        this.relatedTypeAccess = relatedTypeAccess;
        return this;
    }
    
    @Override
    public AppUIBuilderFromEntity relatedTypeConsumer(BiConsumer<String, Collection> relatedTypeConsumer) {
        this.relatedTypeConsumer = relatedTypeConsumer;
        return this;
    }
}
