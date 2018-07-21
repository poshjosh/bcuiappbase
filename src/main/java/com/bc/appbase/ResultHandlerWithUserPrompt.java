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
import com.bc.appcore.ResultHandlerImpl;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 12, 2017 11:56:35 AM
 */
public class ResultHandlerWithUserPrompt extends ResultHandlerImpl {

    private final UIContext uiContext;
    
    public ResultHandlerWithUserPrompt(UIContext uiContext) {
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    public ResultHandlerWithUserPrompt(UIContext uiContext, String actionName) {
        super(actionName);
        this.uiContext = Objects.requireNonNull(uiContext);
    }
    
    @Override
    public void logMessage(String msg) {
        uiContext.showSuccessMessage(msg);
    }

    @Override
    public void logException(Exception e, String msg) {
        uiContext.showErrorMessage(e, msg);
    }
}
