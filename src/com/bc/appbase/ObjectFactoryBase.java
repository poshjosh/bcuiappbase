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

import com.bc.appbase.ui.ComponentModel;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DateFromUIBuilderImpl;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.DateUIUpdaterImpl;
import com.bc.appbase.ui.dialog.DialogManager;
import com.bc.appbase.ui.dialog.DialogManagerImpl;
import com.bc.appbase.ui.dialog.Popup;
import com.bc.appbase.ui.builder.impl.UIBuilderFromEntityMap;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.jpa.EntityFromMapBuilderDataFormatter;
import com.bc.appbase.jpa.EntityStructureFactory;
import com.bc.appbase.jpa.EntityStructureFactoryImpl;
import com.bc.appbase.ui.ComponentModel.ComponentProperties;
import com.bc.appbase.ui.ComponentModelWithTableAsEntityListUI;
import com.bc.appbase.ui.ComponentPropertiesImpl;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.builder.impl.MapFromUIBuilder;
import com.bc.appbase.ui.builder.PromptUserSelectOrCreateNew;
import com.bc.appbase.ui.builder.impl.PromptUserSelectOrCreateNewSelectionType;
import com.bc.appbase.ui.dialog.SimpleErrorOptions;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.jpa.util.EntityFromMapBuilder;
import java.awt.Font;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appbase.ui.builder.impl.DefaultFormEntryComponentModel;
import com.bc.appbase.ui.builder.impl.ThirdComponentProviderImpl;
import com.bc.appbase.ui.builder.impl.UIBuilderFromEntityImpl;
import java.awt.Container;
import java.util.Map;
import com.bc.appbase.ui.builder.PromptUserCreateNew;
import com.bc.appbase.ui.builder.impl.PromptUserCreateNewSelectionType;
import com.bc.appbase.xls.impl.SheetProcessorContextImpl;
import com.bc.appbase.xls.SheetProcessorContext;
import com.bc.appcore.jpa.model.ColumnLabelProvider;
import java.awt.Component;
import com.bc.appbase.ui.builder.FormEntryComponentModel;
import com.bc.appbase.ui.builder.FormEntryWithThreeColumnsComponentModel;
import com.bc.appcore.parameter.ParameterExtractor;
import com.bc.appcore.parameter.ParameterExtractorImpl;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2017 4:24:26 PM
 */
public class ObjectFactoryBase extends com.bc.appcore.ObjectFactoryImpl {
    
    private final DialogManager dialogManager;
    
    private final ComponentProperties componentProperties;
    
    private final int width = 480;
    
    public ObjectFactoryBase(App app) {
        super(app);
        this.dialogManager = new DialogManagerImpl(app.getUIContext(), new SimpleErrorOptions(app));
        this.componentProperties = new ComponentPropertiesImpl() {
            @Override
            public int getWidth(Component component) {
                return width;
            }
            @Override
            public Font getFont(Component component) {
                final UIContext uiContext = app.getUIContext();
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

        if(type.equals(EntityFromMapBuilder.Formatter.class)){

            output = new EntityFromMapBuilderDataFormatter(
                    this, this.getApp().getJpaContext(), 
                    this.getApp().getUIContext());

        }else if(type.equals(EntityStructureFactory.class)){

            output = new EntityStructureFactoryImpl(this.getApp());

        }else if(type.equals(ComponentModel.class)){

            final int contentLengthAboveWhichTextAreaIsUsed = width / 6;
            
            output = new ComponentModelWithTableAsEntityListUI(
                    this.getApp(), this.componentProperties, contentLengthAboveWhichTextAreaIsUsed
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

        }else if(type.equals(FormEntryComponentModel.class)){

            output = new DefaultFormEntryComponentModel(
                    this.getApp().getJpaContext(), 
                    this.getOrException(TypeProvider.class), 
                    this.getOrException(ComponentModel.class), width,
                    this.getOrException(ColumnLabelProvider.class), 
                    ThirdComponentProvider.PROVIDE_NONE
            );

        }else if(type.equals(FormEntryWithThreeColumnsComponentModel.class)){

            output = new DefaultFormEntryComponentModel(
                    this.getApp().getJpaContext(), 
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

            output = new PromptUserCreateNewSelectionType(this.getApp());

        }else if(type.equals(PromptUserSelectOrCreateNew.class)){

            output = new PromptUserSelectOrCreateNewSelectionType(this.getApp());

        }else if(type.equals(ThirdComponentProvider.class)){

            output = new ThirdComponentProviderImpl(this.getApp().getUIContext(), this.getApp());

        }else if(type.equals(UIBuilderFromEntity.class)){

            final UIBuilderFromEntity uiBuilder = new UIBuilderFromEntityImpl()
                    .typeProvider(this.getOrException(TypeProvider.class))
                    .selectionContext(this.getOrException(SelectionContext.class))
                    .entryUIProvider(this.getOrException(FormEntryComponentModel.class))
                    .app(this.getApp());
            output = uiBuilder;

        }else if(type.equals(UIBuilderFromMap.class)){

            final UIBuilder<Map, Container> uiBuilder = new UIBuilderFromEntityMap()
                    .typeProvider(this.getOrException(TypeProvider.class))
                    .selectionContext(SelectionContext.NO_OP)
                    .entryUIProvider(this.getOrException(FormEntryComponentModel.class));
            output = uiBuilder;

        }else if(type.equals(SheetProcessorContext.class)){

            output = new SheetProcessorContextImpl(this.getApp());

        }else{

            output = super.doGetOrException(type);
        }
        
        return (T)output;
    }

    @Override
    public App getApp() {
        return (App)super.getApp();
    }
}
