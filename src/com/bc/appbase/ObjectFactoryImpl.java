/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance source the License.
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
import com.bc.appbase.ui.ComponentModelImpl;
import com.bc.appcore.exceptions.ObjectFactoryException;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.bc.appbase.ui.DateFromUIBuilderImpl;
import com.bc.appbase.ui.DateUIUpdater;
import com.bc.appbase.ui.DateUIUpdaterImpl;
import com.bc.appbase.ui.DialogManager;
import com.bc.appbase.ui.DialogManagerImpl;
import com.bc.appbase.ui.EntryPanel;
import com.bc.appbase.ui.Popup;
import com.bc.appbase.ui.PopupImpl;
import com.bc.appbase.ui.builder.UIBuilder;
import com.bc.appbase.ui.builder.impl.EntryUIProviderImpl;
import com.bc.appbase.ui.builder.impl.UIBuilderFromMap;
import com.bc.appbase.ui.builder.EntryUIProvider;
import com.bc.appbase.ui.builder.FromUIBuilder;
import com.bc.appbase.ui.builder.impl.FromUIBuilderImpl;
import com.bc.appbase.ui.builder.impl.MapNodeManager;
import com.bc.appcore.TypeProvider;
import com.bc.appcore.jpa.SelectionContext;
import java.awt.Font;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 29, 2017 4:24:26 PM
 */
public class ObjectFactoryImpl extends com.bc.appcore.ObjectFactoryImpl {
    
    public ObjectFactoryImpl(App app) {
        super(app);
    }

    @Override
    public <T> T get(Class<T> type) throws ObjectFactoryException {
        Object output;
        final App app = this.getApp();
        //@todo convert all to property
        final int width = 480;
        final int height = 40;
        final int contentLengthAboveWhichTextAreaIsUsed = width / 6;
        final Font font = app.getUIContext().getFont().deriveFont((float)EntryPanel.deriveFontSize(height));
        try{
            if(type.equals(ComponentModel.class)){
                output = new ComponentModelImpl(app, get(SelectionContext.class),
                        get(DateFromUIBuilder.class), get(DateUIUpdater.class),
                        font, width, height, contentLengthAboveWhichTextAreaIsUsed
                );
            }else if(type.equals(DateFromUIBuilder.class)){
                output = new DateFromUIBuilderImpl();
            }else if(type.equals(DateUIUpdater.class)){
                output = new DateUIUpdaterImpl();
            }else if(type.equals(DialogManager.class)){
                output = new DialogManagerImpl(app.getUIContext().getMainFrame());
            }else if(type.equals(Popup.class)){
                output = new PopupImpl(app.getUIContext().getMainFrame());
            }else if(type.equals(EntryUIProvider.class)){
                output = new EntryUIProviderImpl(app.get(ComponentModel.class), width);
            }else if(type.equals(FromUIBuilder.class)){
                output = new FromUIBuilderImpl(new MapNodeManager());
            }else if(type.equals(UIBuilder.class)){
                final UIBuilder uiBuilder = new UIBuilderFromMap()
                        .typeProvider(this.get(TypeProvider.class))
                        .entryUIProvider(this.get(EntryUIProvider.class));
                output = uiBuilder;
            }else{
                output = super.get(type);
            }
        }catch(UnsupportedOperationException e) {
            throw new ObjectFactoryException(e);
        }
        return (T)output;
    }

    @Override
    public App getApp() {
        return (App)super.getApp();
    }
}
