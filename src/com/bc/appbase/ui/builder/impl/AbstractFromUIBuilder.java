/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance ui the License.
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

import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.builder.FromUIBuilder;
import java.awt.Component;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 9:13:25 PM
 */
public abstract class AbstractFromUIBuilder<I extends Component, O> implements FromUIBuilder<I, O> {

    private boolean built;
    private I ui;
    private O source;
    private O target;
    private FromUIBuilder.Filter filter;
    private FromUIBuilder.Formatter formatter;
    private ComponentModel componentModel;
//    private SelectionContext selectionContext;

    public AbstractFromUIBuilder() {
        this.initDefaults();
    }
    
    private void initDefaults() {
        this.filter = FromUIBuilder.Filter.ACCEPT_ALL;
        this.formatter = FromUIBuilder.Formatter.NO_OP;
    }
    
    protected abstract O doBuild();
    
    @Override
    public O build() {
        
        if(this.isBuilt()) {
            throw new IllegalStateException("build() method may only be called once");
        }
        
        this.built = true;

        return this.doBuild();
    }
    
    @Override
    public FromUIBuilder<I, O> ui(I ui) {
        this.ui = ui;
        return this;
    }

    @Override
    public FromUIBuilder<I, O> source(O source) {
        this.source = source;
        return this;
    }

    @Override
    public FromUIBuilder<I, O> target(O target) {
        this.target = target;
        return this;
    }

    @Override
    public FromUIBuilder<I, O> filter(Filter filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public FromUIBuilder<I, O> filter(Formatter formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public FromUIBuilder<I, O> componentModel(ComponentModel cm) {
        this.componentModel = cm;
        return this;
    }

//    @Override
//    public FromUIBuilder<I, O> selectionContext(SelectionContext selectionContext) {
//        this.selectionContext = selectionContext;
//        return this;
//    }
    
    @Override
    public boolean isBuilt() {
        return this.built;
    }

    public I getUi() {
        return ui;
    }

    public O getSource() {
        return source;
    }

    public O getTarget() {
        return target;
    }

    public Filter getFilter() {
        return filter;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public ComponentModel getComponentModel() {
        return componentModel;
    }

//    public SelectionContext getSelectionContext() {
//        return selectionContext;
//    }
}
