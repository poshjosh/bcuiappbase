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

package com.bc.appbase.ui.actions;

import com.bc.appbase.App;
import com.bc.appcore.actions.Action;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterExtractor;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on May 6, 2017 9:50:58 PM
 */
public class PromptSelectEntityType implements Action<App, Class> {

    private transient static final Logger logger = Logger.getLogger(PromptSelectEntityType.class.getName());
    
    @Override
    public Class execute(App app, Map<String, Object> params) 
            throws ParameterException {
        
        final Set<Class> classSet = this.getOptions(app, params);
        
        final Predicate<Class> filter = app.getOrException(ParameterExtractor.class)
                .getFirstValue(params, Predicate.class);
        
        return this.execute(app, classSet, filter);
    }

    public Class execute(App app, Set<Class> classSet, Predicate<Class> filter) {
        
        final Function<Class, String> nameFn = (cls) -> cls.getSimpleName();
        
        final Set<String> nameSet = new LinkedHashSet();

        classSet.stream().filter(filter).forEach((cls) -> nameSet.add(nameFn.apply(cls)));
        
        final String [] names = nameSet.toArray(new String[0]);
        
        final JFrame frame = app.getUIContext().getMainFrame();
        final JLabel message = new JLabel("<html><p style=\"font-size:1.2em;\">Select the Data Type</p></html>");
        final Object oval = JOptionPane.showInputDialog(
                frame, message, "Select Data Type", 
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        final String name = oval == null ? null : oval.toString();

        if(name == null || name.isEmpty()) {

            JOptionPane.showMessageDialog(frame, 
                    "You did not select any Data Type",
                    "Nothing Selected", JOptionPane.WARNING_MESSAGE);
            
            return null;
            
        }else{
            
            final Optional<Class> optionalFirst = classSet.stream().filter((cls) -> name.equals(nameFn.apply(cls))).findFirst();
            
            if(!optionalFirst.isPresent()) {
                
                throw new IllegalStateException("Class with name: "+name+", not found in: "+classSet);
                
            }else{
                
                return optionalFirst.get();
            }
        }
    }
    
    public Set<Class> getOptions(App app, Map<String, Object> params) {
        final Set<Class> output;
        final Collection<Class> optionsParam = (Collection<Class>)params.get(ParamNames.ENTITY_TYPE+"List");
        if(optionsParam == null) {
            
            final Set<String> puNames = app.getPersistenceUnitNames();
            output = app.getJpaContext().getMetaData().getEntityClasses(puNames);
            
            logger.log(Level.FINER, "All classes: {0}", output);
        }else{
            output = optionsParam instanceof Set ?
                    (Set<Class>)optionsParam : new LinkedHashSet(optionsParam);
            logger.log(Level.FINER, "Seed classes: {0}", output);
        }
        return output;
    }
}
