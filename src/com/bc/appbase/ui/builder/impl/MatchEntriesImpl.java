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

package com.bc.appbase.ui.builder.impl;

import com.bc.appbase.ui.components.FormEntryComponentModelImpl;
import com.bc.appbase.App;
import com.bc.appbase.ui.components.ComponentModel;
import com.bc.appbase.ui.components.ComponentModelImpl;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.builder.MatchEntries;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.util.Selection;
import com.bc.appcore.util.SelectionValues;
import java.awt.Container;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 5, 2017 5:35:02 PM
 */
public class MatchEntriesImpl implements MatchEntries {

    private static final Logger logger = Logger.getLogger(MatchEntriesImpl.class.getName());
    
    private App app;
    
    private ThirdComponentProvider thirdComponentProvider;
    
    private String dialogTitle;
    
    private String noSelectionName;
    
    private Set lhs;
    
    private Set rhs;
    
    private Function<SelectionValues, ComponentModel> componentModelProvider;
    
    private boolean buildAttempted;
    
    private Container ui;

    public MatchEntriesImpl() {
        this.dialogTitle = "Match left to right hand sides";
        this.thirdComponentProvider = ThirdComponentProvider.PROVIDE_NONE;
        this.noSelectionName = "Select an option";
    }

    @Override
    public Map build() {
        
        if(buildAttempted) {
            throw new IllegalStateException("build() method may only be called once!");
        }
        this.buildAttempted = true;
        
        Objects.requireNonNull(app);
        Objects.requireNonNull(thirdComponentProvider);
        Objects.requireNonNull(dialogTitle);
        Objects.requireNonNull(noSelectionName);
        Objects.requireNonNull(lhs);
        Objects.requireNonNull(rhs);

        if(this.componentModelProvider == null) {
            this.componentModelProvider = (selectionValues) -> new ComponentModelImpl(
                    selectionValues, 
                    app.getOrException(DateFromUIBuilder.class), 
                    app.getOrException(DateUIUpdater.class)
            );
        }
        
        this.ui = this.buildNameMatchUI(app, 
                lhs, rhs, noSelectionName, thirdComponentProvider);
        
        if(ui instanceof JComponent) {
            app.getUIContext().positionHalfScreenRight(((JComponent)ui).getTopLevelAncestor());
        }else{
            app.getUIContext().positionHalfScreenRight(ui);
        }
          
        app.getUIContext().getDisplayHandler().displayWithTopAndBottomActionButtons(
                ui, dialogTitle, " OK ", (String)null, true);

        final Map selections = this.getSelectionsFromUI(app, ui, lhs, noSelectionName);
        
        return selections;
    }
    
    public Map getSelectionsFromUI(App app, Container ui, Set lhs, String noSelectionName) {
        
        final Map selections = (Map)app.getOrException(FromUIBuilder.class)
                .componentModel(this.getComponentModel(app, lhs, noSelectionName))
                .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                .ui(ui)
                .source(this.toMap(lhs, null))
                .target(new LinkedHashMap())
                .build();
        
        logger.fine(() -> "User selections: " + selections);
        
        return selections;
    }
    
    public Container buildNameMatchUI(App app, Set lhs, Set rhs, 
            String noSelectionName, ThirdComponentProvider thirdComponentProvider) {
        
        final ComponentModel componentModel = this.getComponentModel(app, rhs, noSelectionName);
        
        return this.buildNameMatchUI(app, lhs, componentModel, thirdComponentProvider);
    }
    
    public Container buildNameMatchUI(App app, Set lhs, 
            ComponentModel componentModel, ThirdComponentProvider thirdComponentProvider) {
    
        final Map<String, Object> uiParams = this.toMap(lhs, null);
        
        logger.log(Level.FINE, "UI params: {0}", uiParams);
        
        final int labelWidth = this.computeMaxLength(lhs) * 15;
        
        final Container userInterface = app.getOrException(UIBuilderFromMap.class)
                .typeProvider(TypeProvider.from(Object.class, String.class))
                .componentModel(new FormEntryComponentModelImpl(componentModel, labelWidth, thirdComponentProvider))
                .sourceData(uiParams)
                .build();
        
        return userInterface;
    }
    
    public ComponentModel getComponentModel(App app, Set rhs, String noSelectionName) {
        final Selection noSelection = Selection.from(noSelectionName, null);
        final SelectionValues selectionValues = SelectionValues.from(noSelection, new LinkedHashSet(rhs));
        return this.componentModelProvider.apply(selectionValues);
    }
    
    public <T> int computeMaxLength(Set lhs) {
        int maxLen = 0;
        for(Object o : lhs) {
            maxLen = Math.max(maxLen, o.toString().length());
        }
        if(maxLen <= 0) {
            throw new IllegalArgumentException();
        }
        return maxLen;
    }
    
    public Map toMap(Set lhs, Object defaultValue) {
        
        final Map uiParams = new LinkedHashMap();
        
        lhs.forEach((col) -> uiParams.put(col, defaultValue));
        
        return uiParams;
    }

    @Override
    public Container getUi() {
        if(!this.buildAttempted) {
            throw new IllegalStateException("build() method must be called first");
        }
        return ui;
    }

    @Override
    public MatchEntries app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public MatchEntries thirdComponentProvider(ThirdComponentProvider thirdComponentProvider) {
        this.thirdComponentProvider = thirdComponentProvider;
        return this;
    }

    @Override
    public MatchEntries dialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    @Override
    public MatchEntries noSelectionName(String noSelectionName) {
        this.noSelectionName = noSelectionName;
        return this;
    }

    @Override
    public MatchEntries lhs(Set lhs) {
        this.lhs = lhs;
        return this;
    }

    @Override
    public MatchEntries rhs(Set rhs) {
        this.rhs = rhs;
        return this;
    }

    public MatchEntries componentModelProvider(Function<SelectionValues, ComponentModel> componentModelProvider) {
        this.componentModelProvider = componentModelProvider;
        return this;
    }

    @Override
    public final boolean isBuildAttempted() {
        return this.buildAttempted;
    }

    public App getApp() {
        return app;
    }

    public ThirdComponentProvider getThirdComponentProvider() {
        return thirdComponentProvider;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public String getNoSelectionName() {
        return noSelectionName;
    }

    public Set getLhs() {
        return lhs;
    }

    public Set getRhs() {
        return rhs;
    }

    public Function<SelectionValues, ComponentModel> getComponentModelProvider() {
        return componentModelProvider;
    }
}
