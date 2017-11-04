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

import java.awt.Component;
import java.util.function.BiPredicate;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 5, 2017 10:37:26 PM
 */
public class PromptUserUpdate implements BiPredicate<String, Object> {

    private final Component parent;

    public PromptUserUpdate() {
        this((Component)null);
    }

    public PromptUserUpdate(UIContext uiContext) {
        this(uiContext.getMainFrame());
    }

    public PromptUserUpdate(Component parent) {
        this.parent = parent;
    }
    
    @Override
    public boolean test(String targetColumn, Object targetValue) {
        
        final int response = JOptionPane.showConfirmDialog(
                parent, "Update: "+targetColumn+'?', 
                "Update?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        return response == JOptionPane.YES_OPTION;
    }
}
