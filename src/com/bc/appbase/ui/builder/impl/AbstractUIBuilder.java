/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance source the License.
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

import com.bc.appbase.ui.builder.UIBuilder;
import java.awt.Component;
import com.bc.appbase.ui.builder.EntryUIProvider;
import com.bc.appcore.TypeProvider;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 25, 2017 10:00:57 PM
 */
public abstract class AbstractUIBuilder<I, O extends Component> 
        implements UIBuilder<I, O> {
    
    private boolean built;
    private I source;
    private O target;
    private TypeProvider typeProvider;
    private EntryUIProvider entryUIProvider;
    
    protected abstract O doBuild();

    @Override
    public UIBuilder<I, O> source(I source) {
        this.source = source;
        return this;
    }

    @Override
    public UIBuilder<I, O> target(O target) {
        this.target = target;
        return this;
    }

    @Override
    public UIBuilder<I, O> typeProvider(TypeProvider typeProvider) {
        this.typeProvider = typeProvider;
        return this;
    }

    @Override
    public UIBuilder<I, O> entryUIProvider(EntryUIProvider labelProvider) {
        this.entryUIProvider = labelProvider;
        return this;
    }

    @Override
    public boolean isBuilt() {
        return this.built;
    }

    @Override
    public O build() {
        
        if(this.isBuilt()) {
            throw new IllegalStateException("build() method may only be called once");
        }
        
        this.built = true;
        
        return this.doBuild();
    }

    public TypeProvider getTypeProvider() {
        return typeProvider;
    }
    
    public EntryUIProvider getEntryUIProvider() {
        return entryUIProvider;
    }

    public I getSource() {
        return source;
    }

    public O getTarget() {
        return target;
    }
}
