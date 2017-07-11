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

import java.awt.Container;
import java.awt.Window;
import javax.swing.AbstractButton;

/**
 * @author Chinomso Bassey Ikwuagwu on May 27, 2017 9:02:28 AM
 */
public interface UIDisplayHandler {

    void blockWindowTillCloseButtonClick(Window window);

    void blockWindowTillButtonClick(Window window, AbstractButton... button);
    
    void displayUI(Container ui, String title, boolean scrolls, boolean block);

    void displayWithTopAndBottomActionButtons(Container ui, String title, String buttonText, String actionCommand, boolean block);

}
