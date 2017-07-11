/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance sourceData the License.
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

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.ComponentModel.ComponentProperties;
import com.bc.appbase.ui.ComponentPropertiesImpl;
import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.typeprovider.MemberTypeProvider;
import java.awt.Component;
import java.awt.Container;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 25, 2017 10:00:57 PM
 */
public abstract class AbstractUIBuilder<U extends UIBuilder<I, Container>, I> 
        implements UIBuilder<I, Container> {

    private static final Logger logger = Logger.getLogger(AbstractUIBuilder.class.getName());
    
    private boolean built;
    private Class sourceType;
    private I sourceData;
    private Container targetUI;
    private MemberTypeProvider typeProvider;
    private SelectionContext selectionContext;
    private ComponentModel componentModel;
    private Boolean editable;
    
    private final SequentialLayout sequentialLayout;

    public AbstractUIBuilder(SequentialLayout sequentialLayout) {
        this.sequentialLayout = Objects.requireNonNull(sequentialLayout);
        this.editable = Boolean.TRUE;
    }
    
    public abstract boolean build(Class sourceType, I source, Container container);
    
    @Override
    public Container build() {
        
        if(this.isBuilt()) {
            throw new IllegalStateException("build() method may only be called once");
        }
        
        this.built = true;
        
        if(this.sourceType == null) {
            this.sourceType(this.sourceData.getClass());
        }

        if(this.targetUI == null) {
            this.targetUI(this.createContainer(this.sourceType, this.sourceData, null));
        }
        
        ComponentProperties componentProperties = this.componentModel.getComponentProperties();
        
        if(componentProperties.isEditable(targetUI) != this.editable) {
            
            componentProperties = new ComponentPropertiesImpl(componentProperties) {
                @Override
                public boolean isEditable(Component component) {
                    return editable;
                }
            };

            logger.log(Level.FINE, "Updating `editable` property of components to: {0}", editable);

            this.entryUIProvider(componentModel.deriveNewFrom(componentProperties));
        }
        
        if(this.build(this.sourceType, this.sourceData, this.targetUI)) {

            return this.targetUI;
            
        }else{
            
            throw new IllegalArgumentException("Build failed");
        }
    }

    public Container createContainer(Class sourceType, Object sourceData, String name) {
        final JPanel panel = new JPanel();
        return panel;
    }
    
    @Override
    public U sourceType(Class sourceType) {
        this.sourceType = sourceType;
        return (U)this;
    }
    
    @Override
    public U sourceData(I sourceData) {
        this.sourceData = sourceData;
        return (U)this;
    }

    @Override
    public U targetUI(Container target) {
        this.targetUI = target;
        return (U)this;
    }

    @Override
    public U typeProvider(MemberTypeProvider typeProvider) {
        this.typeProvider = typeProvider;
        return (U)this;
    }

    @Override
    public U selectionContext(SelectionContext selectionContext) {
        this.selectionContext = selectionContext;
        return (U)this;
    }
    
    @Override
    public U entryUIProvider(ComponentModel componentModel) {
        this.componentModel = componentModel;
        return (U)this;
    }

    @Override
    public U editable(Boolean editable) {
        this.editable = editable;
        return (U)this;
    }

    @Override
    public boolean isBuilt() {
        return this.built;
    }

    public SequentialLayout getSequentialLayout() {
        return sequentialLayout;
    }

    public MemberTypeProvider getTypeProvider() {
        return typeProvider;
    }

    public SelectionContext getSelectionContext() {
        return selectionContext;
    }

    public ComponentModel getComponentModel() {
        return componentModel;
    }

    public boolean isEditable() {
        return editable;
    }

    public Class getSourceType() {
        return sourceType;
    }

    public I getSourceData() {
        return sourceData;
    }

    public Container getTargetUI() {
        return targetUI;
    }
}
