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

import java.util.Calendar;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:50:31 PM
 */
public interface DateUIUpdater<UI> {

    enum Month{Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}

    void update(UI ui, Calendar cal);    
    
    /**
     * Only Calendar.HOUR, Calendar.MINUTE and Calendar.DAY_OF_MONTH/Calendar.DATE
     * are supported.
     * @param tf
     * @param cal
     * @param field 
     */
    void updateField(JTextComponent tf, Calendar cal, int field);
    
    void updateMonth(JComboBox cb, Calendar cal);
    
    void updateYear(JComboBox cb, Calendar cal);
}
