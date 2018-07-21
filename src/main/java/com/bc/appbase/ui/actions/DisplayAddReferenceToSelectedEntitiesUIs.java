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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 17, 2017 12:06:55 PM
 */
public class DisplayAddReferenceToSelectedEntitiesUIs extends DisplaySelectedEntitiesUIs {
    
    private static final Logger logger = Logger.getLogger(DisplayAddReferenceToSelectedEntitiesUIs.class.getName());
    
    private final Class referenceType;

    public DisplayAddReferenceToSelectedEntitiesUIs(Class referenceType) {
        this.referenceType = Objects.requireNonNull(referenceType);
    }

    @Override
    public String getUIName(App app, Class selectedEntityType, Object id) {
        final String name = this.getClass().getName() + '_' + this.referenceType.getSimpleName() + '_' +
                selectedEntityType.getSimpleName() + '_' + id + '_' + Long.toHexString(System.currentTimeMillis());
        return name;
    }

    @Override
    public Object getTarget(App app, Class selectedEntityType, Object id) {
        return this.referenceType;
    }
}
