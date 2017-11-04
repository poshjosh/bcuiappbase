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
import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.typeprovider.MemberTypeProvider;
import java.awt.Container;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 8:33:51 PM
 */
public class UIBuilderFromEntityImpl2 extends UIBuilderImpl<Object> 
        implements UIBuilderFromEntity {

    private static final Logger logger = Logger.getLogger(UIBuilderFromEntityImpl2.class.getName());
    
    private App app;
    
    private Boolean addOptionToViewRelated;
    
    public UIBuilderFromEntityImpl2(
            BiFunction<Class, Object, Map> toMap, SequentialLayout sequentialLayout) { 
        super(toMap, sequentialLayout);
    }
    
    @Override
    public boolean isParent(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType) {
        throw new UnsupportedOperationException("Please implement me!");
//        return valueType.getName().endsWith("Personneldata") || valueType.getName().endsWith("Personnelposting");
    }

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
    public UIBuilderFromEntity sourceType(Class entityType) {
        super.sourceType(entityType);
        return this;
    }

    @Override
    public UIBuilderFromEntity sourceData(Object source) {
        super.sourceData(source);
        return this;
    }

    @Override
    public UIBuilderFromEntity targetUI(Container target) {
        super.targetUI(target);
        return this;
    }

    @Override
    public UIBuilderFromEntity typeProvider(MemberTypeProvider typeProvider) {
        super.typeProvider(typeProvider);
        return this;
    }

    @Override
    public UIBuilderFromEntity selectionContext(SelectionContext selectionContext) {
        super.selectionContext(selectionContext);
        return this;
    }

    @Override
    public UIBuilderFromEntity componentModel(ComponentModel componentModel) {
        super.componentModel(componentModel);
        return this;
    }

    @Override
    public UIBuilderFromEntity editable(Boolean editable) {
        super.editable(editable);
        return this;
    }
}
