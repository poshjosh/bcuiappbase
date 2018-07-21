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

package com.bc.appbase;

import com.bc.appbase.ui.UIContext;
import com.bc.appcore.AppContext;
import com.bc.appcore.actions.Action;
import com.bc.appcore.AppCore;
import com.bc.appcore.ResultHandler;
import com.bc.appbase.parameter.ParametersBuilder;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:58 PM
 */
public interface App extends AppCore, AppContext {
    
    @Override
    Action<App, ?> getAction(String actionCommand);

    @Override
    default ResultHandler getResultHandler(String name) {
        return new ResultHandlerWithUserPrompt(this.getUIContext(), Objects.requireNonNull(name));
    }
    
    UIContext getUIContext();

    <S> ParametersBuilder<S> getParametersBuilder(S source, String actionCommand);
    
}
