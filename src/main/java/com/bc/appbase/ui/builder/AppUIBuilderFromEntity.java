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

package com.bc.appbase.ui.builder;

import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
import com.bc.ui.builder.UIBuilderFromEntity;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2018 8:46:37 PM
 */
public interface AppUIBuilderFromEntity extends UIBuilderFromEntity {
    
    AppUIBuilderFromEntity addOptionToViewRelated(boolean b);

    AppUIBuilderFromEntity relatedTypesAccess(AddAccessToViewRelatedTypes relatedTypeAccess);
    
    AppUIBuilderFromEntity relatedTypeConsumer(BiConsumer<String, Collection> relatedTypeConsumer);
}
