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

package com.bc.appbase;

import java.util.Objects;
import com.bc.appcore.Filenames;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2017 9:23:03 AM
 */
public class FilenamesDefault implements Filenames {

    private final String workingDir;
    private final String loggingConfigFile;
    private final String propertiesFile;

    public FilenamesDefault(String workingDir) {
        this.workingDir = Objects.requireNonNull(workingDir);
        this.loggingConfigFile = "META-INF/properties/logging.properties";
        this.propertiesFile = "META-INF/properties/app.properties";
    }

    @Override
    public String getWorkingDir() {
        return this.workingDir;
    }

    @Override
    public String getLoggingConfigFile() {
        return this.loggingConfigFile;
    }

    @Override
    public String getPropertiesFile() {
        return this.propertiesFile;
    }
}
