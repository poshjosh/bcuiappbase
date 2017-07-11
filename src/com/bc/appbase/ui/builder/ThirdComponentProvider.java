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

import java.awt.Component;
import javax.swing.JLabel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 31, 2017 7:09:19 PM
 */
public interface ThirdComponentProvider {
    
    ThirdComponentProvider PROVIDE_NONE = (valueType, name, value, label, component, outputIfNone) -> {
        return null;
    };

    Component get(Class valueType, String name, Object value, 
            JLabel label, Component component, Component outputIfNone);
}
