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

package com.bc.appbase.ui.builder;

import com.bc.appcore.TypeProvider;
import java.awt.Component;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 1:36:32 PM
 */
public interface UIBuilder<I, O extends Component> {
    
    UIBuilder<I, O> source(I source);
    
    UIBuilder<I, O> target(O target);
    
    UIBuilder<I, O> typeProvider(TypeProvider typeProvider);
    
    UIBuilder<I, O> entryUIProvider(EntryUIProvider entryUIProvider);
    
    boolean isBuilt();
    
    O build();
}
