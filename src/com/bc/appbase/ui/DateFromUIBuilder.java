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
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:37:19 PM
 */
public interface DateFromUIBuilder<UI> {
    
    DateFromUIBuilder ui(UI ui);
    
    DateFromUIBuilder<UI> calendar(Calendar calendar);

    DateFromUIBuilder<UI> defaultHousrs(int hours);

    DateFromUIBuilder<UI> defaultMinutes(int minutes);

    DateFromUIBuilder<UI> hoursTextField(JTextComponent hoursTextField);
    
    DateFromUIBuilder<UI> minutesTextField(JTextComponent minutesTextField);

    DateFromUIBuilder<UI> dayTextField(JTextComponent dayTextField);
    
    DateFromUIBuilder<UI> monthComboBox(JComboBox monthComboBox);
    
    DateFromUIBuilder<UI> yearComboBox(JComboBox yearComboBox);
    
    Date build(Date outputIfNone);
}
