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

import com.bc.appbase.ui.builder.impl.*;
import com.bc.appcore.TypeProvider;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.JpaContext;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 28, 2017 9:52:45 PM
 */
public class EntityTypeProvider<E> implements TypeProvider {
    
    private final EntityUpdater<E, ?> updater;
    
    public EntityTypeProvider(JpaContext jpaContext, Class<E> entityType) {
        this(jpaContext.getEntityUpdater(entityType));
    }
    
    public EntityTypeProvider(EntityUpdater<E, ?> updater) {
        this.updater = Objects.requireNonNull(updater);
    }
    
    @Override
    public Class getType(String name, Object value, Class outputIfNone) {
        final Method getter = updater.getMethod(false, name);
        return getter.getReturnType();
    }
}
