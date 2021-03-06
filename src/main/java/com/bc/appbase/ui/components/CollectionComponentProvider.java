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

package com.bc.appbase.ui.components;

import java.awt.Component;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 9, 2017 6:19:36 PM
 */
public interface CollectionComponentProvider {

    Component execute(Class parentType, Class valueType, String name, Collection value);

}
