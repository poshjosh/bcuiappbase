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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:52:36 PM
 */
public class DateUIUpdaterImpl implements DateUIUpdater<DateTimePanel> {

    public DateUIUpdaterImpl() { }
    
    @Override
    public void update(DateTimePanel datePanel, Calendar cal) {
        this.updateYear(datePanel.getYearCombobox(), cal);
        this.updateMonth(datePanel.getMonthCombobox(), cal);
        this.updateField(datePanel.getDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        this.updateField(datePanel.getHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        this.updateField(datePanel.getMinutesTextfield(), cal, Calendar.MINUTE);
    }
    
    /**
     * Only Calendar.HOUR, Calendar.MINUTE and Calendar.DAY_OF_MONTH/Calendar.DATE
     * are supported.
     * @param tf
     * @param cal
     * @param field 
     */
    @Override
    public void updateField(JTextComponent tf, Calendar cal, int field) {
        final int val = cal.get(field);
        final String text;
        if(val < 10) {
            text = "0" + val;
        }else{
            text = String.valueOf(val);
        }
        tf.setText(text);
    }
    
    @Override
    public void updateMonth(JComboBox cb, Calendar cal) {
        final boolean firstMonthNull = true;
        final Month [] values;
        final int currMonth;
        if(firstMonthNull) {
            Month [] months = Month.values();
            values = new Month[1 + months.length];
            values[0] = null;
            System.arraycopy(months, 0, values, 1, months.length);
            currMonth = cal.get(Calendar.MONTH) + 1;
        }else{
            values = Month.values();
            currMonth = cal.get(Calendar.MONTH);
        }
        cb.setModel(new DefaultComboBoxModel<>(values));
        cb.setSelectedIndex(currMonth);
    }
    
    @Override
    public void updateYear(JComboBox cb, Calendar cal) {
        final int CURRENT_YEAR = cal.get(Calendar.YEAR); 
        final List<Integer> years = new ArrayList();
        years.add(null);
        for(int year=CURRENT_YEAR+1; year>=1900; year--) {
            years.add(year);
        }
        final Integer [] values = years.toArray(new Integer[0]);
        cb.setModel(new DefaultComboBoxModel<>(values));
        cb.setSelectedItem(CURRENT_YEAR);
    }
}
