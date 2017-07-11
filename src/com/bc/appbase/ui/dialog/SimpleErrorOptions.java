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

import com.bc.appbase.App;
import com.bc.config.Config;
import java.util.LinkedHashMap;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 2:49:44 PM
 */
public class SimpleErrorOptions extends LinkedHashMap<Object, PopupImpl.OptionAction> {

    private final App app;

    public SimpleErrorOptions() {
        this(null);
    }

    public SimpleErrorOptions(App app) {
        this.app = app;
        this.init();
    }

    private void init() {
        this.put("OK", null);
        this.put("View Details", new ViewDetailsAction(app == null ? null : app.getUIContext(), "Error Details"));
        if(this.app != null) {
            final Config config = app.getConfig();
            final String prefix = "application.email.";
            final String email = config.getProperty(prefix + "address");
            if(email != null) {
                final String host = config.getProperty(prefix + "host", "smtp.googlemail.com");
                final int port =  config.getInt(prefix + "port", 465); 
                final String pass = app.getConfig().getProperty(prefix + "password");
                final String subj = "Application Error Report from: "+app.getName();
                this.put("Send Report", new SendReportAction(host, port, email, pass, subj));
            }
        }
    }
}
