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

package com.bc.appbase.ui.table;

import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 9, 2017 3:37:54 PM
 */
public class ToInteger implements BiFunction<Object, Integer, Integer> {
    
    @Override
    public Integer apply(Object value, Integer defaultValue) {
        
        return value instanceof Integer ? (Integer)value : value == null ? defaultValue : this.valueOf(value, defaultValue);
    }
    
    public Integer valueOf(Object value, Integer defaultValue) {
        
        try{
            
            return Integer.valueOf(value.toString());
            
        }catch(NumberFormatException ignored) {
            
            return defaultValue;
        }
    }
}
