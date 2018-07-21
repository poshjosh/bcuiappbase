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
import com.bc.ui.builder.model.ComponentModel;
import com.bc.appbase.ui.builder.ThirdComponentProvider;
import com.bc.appcore.util.Settings;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 3:42:56 PM
 */
public class SettingsFormEntryComponentModel extends FormEntryComponentModelImpl {

    private final Settings settings;

    public SettingsFormEntryComponentModel(App app, int labelWidth) {
        this(app.getSettings(), app.getOrException(ComponentModel.class), labelWidth);
    }

    public SettingsFormEntryComponentModel(Settings settings, ComponentModel componentModel, int labelWidth) {
        super(componentModel, labelWidth, ThirdComponentProvider.PROVIDE_NONE);
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new SettingsFormEntryComponentModel(this.settings, 
                this.getComponentModel().deriveNewFrom(properties), this.getLabelWidth());
    }
    
    @Override
    public String getLabelText(Class valueType, String name, Object value) {
        final String label = settings.getLabel(name, super.getLabelText(valueType, name, value));
        final String desc = settings.getDescription(name, "");
        final StringBuilder b = new StringBuilder();
        b.append("<html><span style=\"font-size:1.0em;\">").append(label)
                .append("</span><br/><span style=\"font-size:0.6em;\">").append(desc)
                .append("</span></html>");
        return b.toString();
    }
}
