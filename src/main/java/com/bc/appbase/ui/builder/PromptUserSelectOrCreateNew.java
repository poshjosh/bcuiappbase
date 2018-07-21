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
 * @author Chinomso Bassey Ikwuagwu on May 13, 2017 11:04:03 AM
 */
public interface PromptUserSelectOrCreateNew extends PromptUserCreateNew {
    
    boolean isValidForSelectOption(Class entityType, String column, Object value);
    
    boolean isValidForSelectOption(Class entityType, Class columnType, String column, Object value);    
    
    <T> T execute(Class entityType, Class<T> columnType, 
            String column, Object value, T outputIfNone)
            throws EntityInstantiationException;
}
