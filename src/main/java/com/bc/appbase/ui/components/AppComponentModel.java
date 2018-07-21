package com.bc.appbase.ui.components;

import com.bc.appcore.ObjectFactory;
import com.bc.selection.SelectionContext;
import com.bc.selection.SelectionValues;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import com.bc.ui.date.DateFromUIBuilder;
import com.bc.ui.date.DateUIUpdater;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2018 8:40:48 PM
 */
public class AppComponentModel extends ComponentModelImpl {

    public AppComponentModel(ObjectFactory objectFactory) {
        this(objectFactory.getOrException(SelectionContext.class), 
                objectFactory.getOrException(DateFromUIBuilder.class), objectFactory.getOrException(DateUIUpdater.class));
    }

    public AppComponentModel(SelectionValues selectionValues, DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater) {
        super(selectionValues, dateFromUIBuilder, dateUIUpdater);
    }

    public AppComponentModel(SelectionValues selectionValues, DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater, ComponentProperties componentProperties, int contentLengthAboveWhichTextAreaIsUsed) {
        super(selectionValues, dateFromUIBuilder, dateUIUpdater, componentProperties, contentLengthAboveWhichTextAreaIsUsed);
    }
}
