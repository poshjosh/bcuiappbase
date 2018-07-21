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

package com.bc.appbase.ui.dialog;

import com.bc.appbase.ui.UIContext;
import com.bc.appcore.AppContext;
import java.util.LinkedHashMap;
import com.bc.config.Config;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 2:49:44 PM
 */
public class SimpleErrorOptions extends LinkedHashMap<Object, PopupImpl.OptionAction> {

    public SimpleErrorOptions() {
        this(null, null);
    }

    public SimpleErrorOptions(AppContext context, UIContext uiContext) {
        this.put("OK", null);
        this.put("View Details", new ViewDetailsAction(uiContext, "Error Details"));
        if(context != null) {
            final Config config = context.getConfig();
            final String prefix = "application.email.";
            final String email = config.get(prefix + "address");
            if(email != null) {
                final String host = config.get(prefix + "host", "smtp.googlemail.com");
                final int port =  config.getInt(prefix + "port", 465); 
                final String pass = config.get(prefix + "password");
                final String appName = config.get("app.name", "App");
                final String subj = "Application Error Report from: " + appName;
                this.put("Send Report", new SendReportAction(host, port, email, pass, subj));
            }
        }
    }
}
