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

package com.bc.appbase.ui.builder;

import com.bc.jpa.exceptions.EntityInstantiationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 4, 2017 10:17:41 PM
 */
public interface PromptUserCreateNew {

    boolean isValidForCreateNewOption(Class entityType, String column);
    
    boolean isValidForCreateNewOption(Class columnType);    
    
    <T> T promptCreateNew(Class<T> columnType, T outputIfNone)
            throws EntityInstantiationException;
}
