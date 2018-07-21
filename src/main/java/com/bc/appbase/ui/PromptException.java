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

package com.bc.appbase.ui;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 5, 2017 10:51:36 PM
 */
public class PromptException implements BiConsumer<String, Exception> {

    private final UIContext uiContext;
    
    public PromptException(UIContext uiContext) {
        this.uiContext = Objects.requireNonNull(uiContext);
    }

    @Override
    public void accept(String targetColumn, Exception exception) {
        final String msg = "Error updating "+targetColumn;
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, msg, exception);
        uiContext.showErrorMessage(exception, msg);
    }
}
