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

package com.bc.appbase.ui.functions;

import java.awt.Container;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 8:15:21 PM
 */
public interface AddAccessToViewRelatedTypes extends BiFunction<Object, Container, Container> {

    AddAccessToViewRelatedTypes consumer(BiConsumer<String, Collection> consumer);
    
    @Override
    Container apply(Object t, Container u);
}
