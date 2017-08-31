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
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:56:29 PM
 */
public class DateFromUIBuilderImpl implements DateFromUIBuilder<DatePanel> {

    private Calendar calendar;
    private int defaultHours = -1;
    private int defaultMinutes = -1;
    private JTextComponent hoursTextField;
    private JTextComponent minutesTextField;
    private JTextComponent dayTextField;
    private JComboBox monthComboBox;
    private JComboBox yearComboBox;

    public DateFromUIBuilderImpl() { }
    
    public void reset() {
        this.calendar = null;
        this.hoursTextField = null;
        this.minutesTextField = null;
        this.dayTextField = null;
        this.monthComboBox = null;
        this.yearComboBox = null;
    }

    @Override
    public DateFromUIBuilder<DatePanel> ui(DatePanel datePanel) {
        this.dayTextField(datePanel.getDayTextfield());
        if(datePanel instanceof DateTimePanel) {
            final DateTimePanel dateTimePanel = (DateTimePanel)datePanel;
            this.hoursTextField(dateTimePanel.getHoursTextfield());
            this.minutesTextField(dateTimePanel.getMinutesTextfield());
        }
        this.monthComboBox(datePanel.getMonthCombobox());
        this.yearComboBox(datePanel.getYearCombobox());
        return this;
    }
    
    @Override
    public DateFromUIBuilder<DatePanel> calendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> defaultHousrs(int hours) {
        this.defaultHours = hours;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> defaultMinutes(int minutes) {
        this.defaultMinutes = minutes;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> hoursTextField(JTextComponent hoursTextField) {
        this.hoursTextField = hoursTextField;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> minutesTextField(JTextComponent minutesTextField) {
        this.minutesTextField = minutesTextField;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> dayTextField(JTextComponent dayTextField) {
        this.dayTextField = dayTextField;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> monthComboBox(JComboBox monthComboBox) {
        this.monthComboBox = monthComboBox;
        return this;
    }

    @Override
    public DateFromUIBuilder<DatePanel> yearComboBox(JComboBox yearComboBox) {
        this.yearComboBox = yearComboBox;
        return this;
    }

    @Override
    public Date build(Date outputIfNone) {
        
        try{

            final String hoursText = this.init(hoursTextField, defaultHours);
            
            if(!this.isNullOrEmpty(hoursText)) {
                
                final String minutesText = this.init(minutesTextField, defaultMinutes);
                
                if(!this.isNullOrEmpty(minutesText)) {
                    final String dayText = dayTextField.getText();
                    if(!this.isNullOrEmpty(dayText)) {
                        final DateUIUpdater.Month month = (DateUIUpdater.Month)monthComboBox.getSelectedItem();
                        if(month != null) {
                            final Object year = yearComboBox.getSelectedItem();
                            if(year != null) {
                                if(calendar == null) {
                                    calendar = Calendar.getInstance();
                                }
                                calendar.set(
                                        (Integer)year, 
                                        month.ordinal(), 
                                        Integer.parseInt(dayText), 
                                        Integer.parseInt(hoursText), 
                                        Integer.parseInt(minutesText), 
                                        0);
                                return calendar.getTime();
                            }        
                        }
                    }
                }
            }
        }finally{
            this.reset();
        }
        return outputIfNone;
    }
    
    private String init(JTextComponent textField, int defaultValue) {
        final String output;
        if(textField == null || this.isNullOrEmpty(textField.getText())) {
            if(defaultValue != -1) {
                output = ""+defaultValue;
            }else{
                output = null;
            }
        }else{
            output = textField.getText();
        }
        return output;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
