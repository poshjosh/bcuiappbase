/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance source the License.
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
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 3:51:43 PM
 */
public class DisplaySelectedRecords extends DisplaySelectedEntitiesUIs {

    @Override
    public Container execute(App app, Map<String, Object> params) {
        
        params = new HashMap(params);
        params.put(ParamNames.TITLE, "Displaying Selected Record");
        params.put(ParamNames.EDITABLE, Boolean.FALSE);
        
        return super.execute(app, params); 
    }

    @Override
    public void saveReference(App app, Class selectedEntityType, Object id, String name) { }
}
