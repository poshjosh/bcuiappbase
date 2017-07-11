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

package com.bc.appbase.ui.builder;

import com.bc.appbase.ui.ComponentModel;
import java.awt.Component;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 9:09:35 PM
 */
public interface FromUIBuilder<I extends Component, O> {
    
    interface Filter {
        Filter ACCEPT_ALL = new Filter() {
            @Override
            public boolean accept(Object container, Object key, Object oldValue, Object newValue) { return true; }
        };
        Filter ACCEPT_UPDATES_ONLY = new Filter() {
            @Override
            public boolean accept(Object container, Object key, Object oldValue, Object newValue) { 
                return !Objects.equals(oldValue, newValue); 
            }
        };
        boolean accept(Object container, Object key, Object oldValue, Object newValue);
    }
    
    interface Formatter {
        Formatter NO_OP = new Formatter() {
            @Override
            public Object format(Object container, Object key, Object oldValue, Object newValue) { return newValue; }
        };
        Object format(Object container, Object key, Object oldValue, Object newValue);
    }
    
    FromUIBuilder<I, O> filter(FromUIBuilder.Formatter formatter);
    
    FromUIBuilder<I, O> filter(FromUIBuilder.Filter filter);
    
    FromUIBuilder<I, O> source(O source);
    
    FromUIBuilder<I, O> ui(I ui);
    
    FromUIBuilder<I, O> target(O target);
    
    FromUIBuilder<I, O> componentModel(ComponentModel cm);
    
//    FromUIBuilder<I, O> selectionContext(SelectionContext selectionContext);
    
    boolean isBuilt();
    
    O build();
}
