/*
 * Copyright 2018 NUROX Ltd.
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

package com.bc.appbase;

import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.AppContext;
import com.bc.appcore.AppContextBuilder;
import com.bc.appcore.properties.PropertiesContext;
import com.bc.config.ConfigImpl;
import com.bc.jpa.context.PersistenceContext;
import com.bc.jpa.context.PersistenceContextEclipselinkOptimized;
import com.bc.sql.MySQLDateTimePatterns;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.bc.config.Config;

/**
 * @author Chinomso Bassey Ikwuagwu on May 16, 2018 12:40:47 PM
 */
public class Main {

    public static void main(String... args) {
        final Properties properties = new Properties();
        final String user_home = System.getProperty("user.home");
        final URI persistenceConfigUri = Paths.get(user_home, "Documents", 
                "NetBeansProjects", "idiscpu", "src", "test", "resources", 
                "META-INF", "persistence.xml").toUri();
        properties.setProperty("persistenceFile", persistenceConfigUri.toASCIIString());
        properties.setProperty("charsetName", StandardCharsets.UTF_8.name());
//        properties.setProperty("persistenceUnit.master.authenticationRequired", "true");
//        properties.setProperty("persistenceUnit.slave.authenticationRequired", "false");
        properties.setProperty("lookAndFeel", "Windows");
        properties.setProperty(".font", "MONSPACED-PLAIN-16");
        properties.setProperty("datePattern", "dd MMM yy");
        properties.setProperty("dateTimePattern", "dd MMM yy HH:mm");
//        properties.setProperty("defaultEmailHost", "looseboxes.com");
        final Config config = new ConfigImpl(properties);
        final PersistenceContext persistenceCtx = new PersistenceContextEclipselinkOptimized(
                persistenceConfigUri, new MySQLDateTimePatterns()
        );
        final String workingDirPath = Paths.get(user_home, "Desktop", "bcuiappbasetest").toString();
        final PropertiesContext propsCtx = PropertiesContext.builder().workingDirPath(workingDirPath).build();
        final AppContextBuilder builder = new AppContextBuilder();
        final AppContext appCtx = builder.classLoader(Thread.currentThread().getContextClassLoader())
                .config(config)
//                .expirableCache(???)
                .masterPersistenceUnitTest((puName) -> true)
                .persistenceContext(persistenceCtx)
                .propertiesContext(propsCtx)
                .settingsConfig(new Properties())
                .slavePersistenceUnitTest((puName) -> false)
                .syncEnabled(false)
                .build();
        final App app = new AbstractApp(appCtx) {
            @Override
            public Class getUserEntityType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public List<Class> getEntityTypeOrderList() { return Collections.EMPTY_LIST; }
            @Override
            public Class getDefaultEntityType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        final Map params = new HashMap();
        
        app.getAction(ActionCommands.LOGIN_VIA_USER_PROMPT).executeSilently(app, params);
    }
}
