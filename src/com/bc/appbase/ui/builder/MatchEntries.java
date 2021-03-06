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

package com.bc.appbase.ui.builder;

import com.bc.appbase.App;
import java.awt.Container;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 5, 2017 8:37:19 PM
 */
public interface MatchEntries {

    MatchEntries app(App app);

    Map build();

    MatchEntries dialogTitle(String dialogTitle);
    
    Container getUi();

    boolean isBuildAttempted();

    MatchEntries lhs(Set lhs);

    MatchEntries noSelectionName(String noSelectionName);

    MatchEntries rhs(Set rhs);

    MatchEntries thirdComponentProvider(ThirdComponentProvider thirdComponentProvider);

}
