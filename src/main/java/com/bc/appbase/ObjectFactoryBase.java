/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance sourceData the License.
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

import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.date.DateFromUIBuilder;
import com.bc.ui.date.DateFromUIBuilderImpl;
import com.bc.ui.date.DateUIUpdater;
import com.bc.ui.date.DateUIUpdaterImpl;
import com.bc.appbase.ui.dialog.DialogManager;
import com.bc.appbase.ui.dialog.DialogManagerImpl;
import com.bc.appbase.ui.dialog.Popup;
import com.bc.ui.builder.impl.UIBuilderFromEntityMap;
import com.bc.ui.builder.FromUIBuilder;
import com.bc.appbase.jpa.EntityFromMapBuilderDataFormatter;
import com.bc.appbase.parameter.ParametersBuilder;
import com.bc.appbase.parameter.ParametersBuilderImpl;
import com.bc.appcore.jpa.EntityStructureFactory;
import com.bc.appcore.jpa.EntityStructureFactoryImpl;
import com.bc.ui.builder.model.ComponentModel.ComponentProperties;
import com.bc.appbase.ui.components.ComponentModelWithTableAsEntityListUI;
import com.bc.ui.builder.model.impl.ComponentPropertiesImpl;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.builder.AppUIBuilderFromEntity;
import com.bc.ui.builder.impl.MapFromUIBuilder;
import com.bc.appbase.ui.builder.PromptUserSelectOrCreateNew;
import com.bc.appbase.ui.builder.impl.PromptUserSelectOrCreateNewSelectionType;
import com.bc.appbase.ui.dialog.SimpleErrorOptions;
import com.bc.reflection.TypeProvider;
import com.bc.selection.SelectionContext;
import com.bc.jpa.util.EntityFromMapBuilder;
import java.awt.Font;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.ui.builder.UIBuilder;
import com.bc.ui.builder.UIBuilderFromEntity;
import com.bc.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.components.DefaultFormEntryComponentModel;
import com.bc.appbase.ui.builder.impl.ThirdComponentProviderImpl;
import java.awt.Container;
import java.util.Map;
import com.bc.appbase.ui.builder.PromptUserCreateNew;
import com.bc.appbase.ui.builder.impl.PromptUserCreateNewSelectionType;
import com.bc.appbase.xls.impl.SheetProcessorContextImpl;
import com.bc.appbase.xls.SheetProcessorContext;
import com.bc.appcore.jpa.model.ColumnLabelProvider;
import java.awt.Component;
import com.bc.appbase.ui.components.FormEntryComponentModel;
import com.bc.appbase.ui.components.FormEntryWithThreeColumnsComponentModel;
import com.bc.appbase.ui.builder.MatchEntries;
import com.bc.appbase.ui.builder.impl.AppUIBuilderFromEntityImpl;
import com.bc.appbase.ui.builder.impl.MatchEntriesImpl;
import com.bc.ui.builder.impl.UIBuilderFromEntityImpl;
import com.bc.ui.builder.model.ComponentWalker;
import com.bc.ui.builder.model.impl.ComponentWalkerImpl;
import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypesImpl;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.ResultHandler;
import com.bc.appcore.functions.BuildEntityStructure;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appcore.parameter.ParameterExtractorImpl;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2017 4:24:26 PM
 */
public class ObjectFactoryBase extends com.bc.appcore.ObjectFactoryImpl {
    
    private final DialogManager dialogManager;
    
    private final ComponentProperties componentProperties;
    
    private final int width = 360;//480;
    
    private final App app;
    
    public ObjectFactoryBase(App app) {
        this(null, app);
    }
    
    public ObjectFactoryBase(ObjectFactory parent, App app) {
        super(parent, app);
        this.app = Objects.requireNonNull(app);
        final UIContext uiContext = app.getUIContext();
        Objects.requireNonNull(uiContext);
        this.dialogManager = new DialogManagerImpl(uiContext, new SimpleErrorOptions(app, uiContext));
        this.componentProperties = new ComponentPropertiesImpl() {
            @Override
            public int getWidth(Component component) {
                return width;
            }
            @Override
            public Font getFont(Component component) {
                Class componentClass = component.getClass();
                if(componentClass.isAnonymousClass() || componentClass.isMemberClass()) {
                    componentClass = componentClass.getSuperclass();
                }
                final Font font = uiContext.getFont(componentClass);
                return font.deriveFont((float)this.getFontSize(component));
            }
        };
    }

    @Override
    public <T> T doGetOrException(Class<T> type) throws Exception {
        Object output;

        if(type.equals(ResultHandler.class)){

            output = new ResultHandlerWithUserPrompt(this.getUiContext());
            
        }else if(type.equals(ParametersBuilder.class)){

            output = new ParametersBuilderImpl();

        }else if(type.equals(EntityFromMapBuilder.Formatter.class)){

            output = new EntityFromMapBuilderDataFormatter(
                    this, this.getContext().getActivePersistenceUnitContext(), this.getUiContext());

        }else if(type.equals(EntityStructureFactory.class)){

            output = new EntityStructureFactoryImpl(this.getContext(), this);

        }else if(type.equals(ComponentModel.class)){

            final int contentLengthAboveWhichTextAreaIsUsed = width / 6;
            
            output = new ComponentModelWithTableAsEntityListUI(
                    this.app, this.componentProperties, contentLengthAboveWhichTextAreaIsUsed
            );

        }else if(type.equals(ComponentProperties.class)){
            
            output = this.componentProperties;
            
        }else if(type.equals(DateFromUIBuilder.class)){

            output = new DateFromUIBuilderImpl();

        }else if(type.equals(DateUIUpdater.class)){

            output = new DateUIUpdaterImpl();

        }else if(type.equals(DialogManager.class)){

            output = this.dialogManager;

        }else if(type.equals(Popup.class)){

            output = this.dialogManager;

        }else if(type.equals(ComponentWalker.class)){

            output = new ComponentWalkerImpl();

        }else if(type.equals(FormEntryComponentModel.class)){

            output = new DefaultFormEntryComponentModel(
                    this.getContext().getActivePersistenceUnitContext(), 
                    this.getOrException(TypeProvider.class), 
                    this.getOrException(ComponentModel.class), width,
                    this.getOrException(ColumnLabelProvider.class), 
                    ThirdComponentProvider.PROVIDE_NONE
            );

        }else if(type.equals(FormEntryWithThreeColumnsComponentModel.class)){

            output = new DefaultFormEntryComponentModel(
                    this.getContext().getActivePersistenceUnitContext(), 
                    this.getOrException(TypeProvider.class), 
                    this.getOrException(ComponentModel.class), width,
                    this.getOrException(ColumnLabelProvider.class), 
                    this.getOrException(ThirdComponentProvider.class)
            );

        }else if(type.equals(FromUIBuilder.class)){

            output = new MapFromUIBuilder();

        }else if(type.equals(ParameterExtractor.class)){
            
            output = new ParameterExtractorImpl();
            
        }else if(type.equals(PromptUserCreateNew.class)){

            output = new PromptUserCreateNewSelectionType(this.getContext(), this, this.getUiContext());

        }else if(type.equals(PromptUserSelectOrCreateNew.class)){

            output = new PromptUserSelectOrCreateNewSelectionType(this.getContext(), this, this.getUiContext());

        }else if(type.equals(ThirdComponentProvider.class)){

            output = new ThirdComponentProviderImpl(this.getUiContext(), this);

        }else if(type.equals(MatchEntries.class)){
            
            output = new MatchEntriesImpl().objectFactory(this).uiContext(this.getUiContext());
            
        }else if(type.equals(UIBuilderFromEntity.class)){
            
//            final UIBuilderFromEntity uiBuilder = new UIBuilderFromEntityImpl2(
//                    this.getOrException(BuildMap.class), new VerticalLayout()
//            )
            final BiFunction<Class, Object, Map> entityStructureBuilder = this.getOrException(BuildEntityStructure.class);
            final UIBuilderFromMap uiFromMapBuilder = this.getOrException(UIBuilderFromMap.class);
            final UIBuilderFromEntity uiBuilder = new UIBuilderFromEntityImpl(
                    entityStructureBuilder, uiFromMapBuilder
            ).typeProvider(this.getOrException(TypeProvider.class))
                    .selectionContext(this.getOrException(SelectionContext.class))
                    .componentModel(this.getOrException(FormEntryComponentModel.class));
            output = uiBuilder;

        }else if(type.equals(AppUIBuilderFromEntity.class)){
            
//            final UIBuilderFromEntity uiBuilder = new UIBuilderFromEntityImpl2(
//                    this.getOrException(BuildMap.class), new VerticalLayout()
//            )
            final BiFunction<Class, Object, Map> entityStructureBuilder = this.getOrException(BuildEntityStructure.class);
            final UIBuilderFromMap uiFromMapBuilder = this.getOrException(UIBuilderFromMap.class);
            final UIBuilderFromEntity uiBuilder = new AppUIBuilderFromEntityImpl(
                    entityStructureBuilder, uiFromMapBuilder
            ).relatedTypesAccess(this.getOrException(AddAccessToViewRelatedTypes.class))
                    .typeProvider(this.getOrException(TypeProvider.class))
                    .selectionContext(this.getOrException(SelectionContext.class))
                    .componentModel(this.getOrException(FormEntryComponentModel.class));
            output = uiBuilder;
            
        }else if(type.equals(UIBuilderFromMap.class)){

            final UIBuilder<Map, Container> uiBuilder = new UIBuilderFromEntityMap()
                    .typeProvider(this.getOrException(TypeProvider.class))
                    .selectionContext(SelectionContext.NO_OP)
                    .componentModel(this.getOrException(FormEntryComponentModel.class));
            output = uiBuilder;

        }else if(type.equals(AddAccessToViewRelatedTypes.class)){

            output = new AddAccessToViewRelatedTypesImpl(this, this.getUiContext());

        }else if(type.equals(SheetProcessorContext.class)){

            output = new SheetProcessorContextImpl(this.getContext(), this, this.getUiContext());

        }else{

            output = super.doGetOrException(type);
        }
        
        return (T)output;
    }

    public UIContext getUiContext() {
        return app.getUIContext();
    }
    
    public App getApp() {
        return this.app;
    }
}
