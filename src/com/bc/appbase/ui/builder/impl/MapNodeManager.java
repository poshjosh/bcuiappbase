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

import com.bc.appbase.ui.builder.impl.FromUIBuilderImpl.NodeManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 10, 2017 7:53:46 PM
 */
public class MapNodeManager implements NodeManager<Map> {

    @Override
    public Set getKeys(Map parent, Object key, Map value) {
        return value.keySet();
    }

    @Override
    public Object getValue(Map node, Object key) {
        return node.get(key);
    }

    @Override
    public boolean isParentKey(Map node, Object key) {
        return node.get(key) instanceof Map;
    }

    @Override
    public Map createContainerFor(Set keys) {
        return new LinkedHashMap(keys.size(), 1.0f);
    }

    @Override
    public Object add(Map node, Object key, Object value) {
        return node.put(key, value);
    }
}
