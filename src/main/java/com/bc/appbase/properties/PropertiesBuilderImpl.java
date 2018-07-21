/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this sourceFile except in compliance with the License.
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

package com.bc.appbase.properties;

import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.layout.SequentialLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.properties.OptionsProvider;
import com.bc.appcore.properties.PropertyNamesValidator;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2017 9:29:05 PM
 */
public class PropertiesBuilderImpl implements PropertiesBuilder {

    private static final Logger LOG = Logger.getLogger(PropertiesBuilderImpl.class.getName());

    private boolean displayPromptAtLeastOnce;
    private File sourceFile; 
    private Class type;
    private String charsetName;
    private Predicate<Properties> validator;
    private int maxTrials;
    private Set<String> propertyNames;
    private OptionsProvider optionsProvider;
    private Map defaultValues;

    private boolean built;
    private boolean lastValidationSuccessful;
    private int validationAttempts;
    private int numberOfTimesPromptShown;
    private int updateCount;

    public PropertiesBuilderImpl() {
        this.displayPromptAtLeastOnce = false;
        this.maxTrials = 3;
        this.charsetName = "utf-8";
        this.defaultValues = Collections.EMPTY_MAP;
        this.optionsProvider = OptionsProvider.NO_OP;
    }

    @Override
    public Properties build() throws IOException {
        
        if(this.built) {
            throw new IllegalStateException("build() method may only be called once");
        }
        this.built = true;
        
        Objects.requireNonNull(this.sourceFile);
        Objects.requireNonNull(this.charsetName);
        Objects.requireNonNull(this.defaultValues);
        Objects.requireNonNull(this.optionsProvider);

        if(this.validator == null && this.type != null) {
            this.validator = new PropertyNamesValidator(type); 
        }
        Objects.requireNonNull(this.validator);
        
        if(this.propertyNames == null && this.type != null) {
            this.propertyNames = this.getPropertyNames(type);
        }
        Objects.requireNonNull(this.propertyNames);
        
        final Properties props = this.load(defaultValues);
        
        this.lastValidationSuccessful = false;
        
        this.updateCount = 0;
        
        for(validationAttempts = 0; validationAttempts < maxTrials; validationAttempts++) {
            
            lastValidationSuccessful = validator.test(props);
            
            LOG.fine(() -> "Validation successful: " + this.lastValidationSuccessful + 
                    ", validation attempts: " + this.validationAttempts + 
                    ", propterty names: " + this.propertyNames);

            if(!lastValidationSuccessful) {
                
                updateCount = this.updateWithValuesFromUserPrompt(props);
                
                ++numberOfTimesPromptShown;
                
            }else{
                
                break;
            }
        } 
        
        LOG.fine(() -> "Display prompt at least once: " + this.displayPromptAtLeastOnce + 
                ", display attempts: " + numberOfTimesPromptShown + 
                    ", propterty names: " + this.propertyNames);
        
        if(this.displayPromptAtLeastOnce && numberOfTimesPromptShown < 1) {
        
            updateCount = this.updateWithValuesFromUserPrompt(props);
        }
        
        if(lastValidationSuccessful && updateCount > 0) {
            
            this.store(props);
        }
        
        return props;
    }    
    
    public Properties load(Map defaultValues) throws IOException {

        final Properties props = new Properties();
        LOG.log(Level.FINE, "Loading properties from: {0}", sourceFile);
        try{
            try(Reader reader = this.createReader(sourceFile, charsetName)) {
                props.load(reader);
            }
        }catch(FileNotFoundException e) {
            LOG.warning(e.toString());
        }
        
        LOG.log(Level.FINER, "Loaded properties: {0}", props);
        
        for(String propertyName : propertyNames) {
            final String prop = props.getProperty(propertyName);
            if(prop == null) {
                final Object val = defaultValues.get(propertyName);
                if(val != null) {
                    props.setProperty(propertyName, this.toString(propertyName, val));
                }
            }
        }
        
        LOG.log(Level.FINER, "After updating loaded properties with defaults: {0}", props);
        
        return props;
    }
    
    public void store(Properties props) throws IOException {
        try(Writer writer = this.createWriter(sourceFile, charsetName)) {
            props.store(writer, "Saved by " + System.getProperty("user.name") + " on " + ZonedDateTime.now());
        }
    }
    
    public Reader createReader(File file, String charsetName) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
    }
    
    public Writer createWriter(File file, String charsetName) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charsetName));
    }
    
    public int updateWithValuesFromUserPrompt(Properties props) {
        
        final Map options = new LinkedHashMap(props.size(), 1.0f);
        final Set<String> names = props.stringPropertyNames();
        for(String name : names) {
            final String value = props.getProperty(name);
            options.put(name, this.fromString(name, value));
        }
        
        this.propertyNames.stream().forEach((name) -> options.putIfAbsent(name, null));
        
        LOG.log(Level.FINER, "Displaying prompt for: {0}", options);

        final Map<String, Object> userInput = optionsProvider.get(options);

        LOG.log(Level.FINE, "User input: {0}", userInput);

        final AtomicInteger atomicUpdateCount = new AtomicInteger(0);
        
        userInput.forEach((k, v) -> {
            if(v != null) {
                final String current = props.getProperty(k);
                final String update = toString(k, v);
                if(!Objects.equals(current, update)) {
                    props.setProperty(k, update);
                    atomicUpdateCount.incrementAndGet();
                }
            }
        });
        
        LOG.fine(() -> "Updated " + atomicUpdateCount.intValue() + 
                " properties. Update: " + props.stringPropertyNames());
        
        return atomicUpdateCount.intValue();
    }

    public Object fromString(String key, String val) {
        final Object output;
        if(this.isPasswordName(key)) {
            output = val.toCharArray();
        }else {
            output = val;
        }
        this.logOutput(key, val, output);
        return output;
    }
    
    public String toString(String key, Object val) {
        final String output;
        if(this.isPasswordName(key) && val instanceof char[]) {
            output = new String((char[])val);
        }else {
            output = val.toString();
        }
        this.logOutput(key, val, output);
        return output;
    }

    protected void logOutput(String key, Object val, Object output) {
        if(!this.isPasswordName(key)) {
            LOG.finer(() -> "Output: "+output+", inputs: ("+key+", "+val+')');
        }
    }
    
    protected boolean isPasswordName(String name) {
        if(this.optionsProvider instanceof GetOptionsViaUserPrompt) {
            return (((GetOptionsViaUserPrompt)this.optionsProvider).getComponentModel()).isPasswordName(name);
        }else{
            return name.toLowerCase().contains("password");
        }
    }
    
    private Set<String> getPropertyNames(Class type) {
        final Field [] fields = type.getFields(); 
        final Set<String> output = new LinkedHashSet(fields.length);
        for(Field field : fields) {
            try{
                final String name = (String)field.get(null);
                output.add(name);
            }catch(ClassCastException | IllegalArgumentException | IllegalAccessException ignored) { }
        }
        return output.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(output);
    }

//////////////////////////// Setters ////////////////////////////////

    @Override
    public PropertiesBuilder displayPromptAtLeastOnce(boolean displayPromptAtLeastOnce) {
        this.displayPromptAtLeastOnce = displayPromptAtLeastOnce;
        return this;
    }

    @Override
    public PropertiesBuilder sourceFile(File file) {
        this.sourceFile = file;
        return this;
    }

    @Override
    public PropertiesBuilder type(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public PropertiesBuilder charsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    @Override
    public PropertiesBuilder validator(Predicate<Properties> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public PropertiesBuilder maxTrials(int maxTrials) {
        this.maxTrials = maxTrials;
        return this;
    }

    @Override
    public PropertiesBuilder propertyNames(Set<String> propertyNames) {
        this.propertyNames = propertyNames;
        return this;
    }
    
    @Override
    public PropertiesBuilder optionsProvider(String dialogTitle, Function<String, String> nameToLabel) {
        this.optionsProvider(new GetOptionsViaUserPrompt(dialogTitle, nameToLabel));
        return this;
    }
    
    @Override
    public PropertiesBuilder optionsProvider(
            Component parent, ComponentModel componentModel, SequentialLayout layout, 
            String dialogTitle, Function<String, String> nameToLabel, int width) {
        this.optionsProvider(new GetOptionsViaUserPrompt(
                parent, componentModel, layout, dialogTitle, nameToLabel, width
            )
        );
        return this;
    }
    
    @Override
    public PropertiesBuilder optionsProvider(OptionsProvider optionsProvider) {
        this.optionsProvider = optionsProvider;
        return this;
    }

    @Override
    public PropertiesBuilder defaultValues(Map defaultValues) {
        this.defaultValues = defaultValues;
        return this;
    }

//////////////////////////// Getters ////////////////////////////////    
    
    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public boolean isLastValidationSuccessful() {
        return lastValidationSuccessful;
    }
    
    @Override
    public int getValidationAttempts() {
        return validationAttempts;
    }

    public int getNumberOfTimesPromptShown() {
        return numberOfTimesPromptShown;
    }

    @Override
    public int getUpdateCount() {
        return updateCount;
    }
    
    @Override
    public File getSourceFile() {
        return sourceFile;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public String getCharsetName() {
        return charsetName;
    }

    @Override
    public int getMaxTrials() {
        return maxTrials;
    }

    @Override
    public Set<String> getPropertyNames() {
        return propertyNames;
    }

    @Override
    public OptionsProvider getOptionsProvider() {
        return optionsProvider;
    }

    @Override
    public boolean isDisplayPromptAtLeastOnce() {
        return displayPromptAtLeastOnce;
    }

    @Override
    public Predicate<Properties> getValidator() {
        return validator;
    }

    @Override
    public Map getDefaultValues() {
        return defaultValues;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + 
                "sourceFile=" + sourceFile + 
                ", type=" + (type==null?null:type.getName()) + 
                "\npropertyNames=" + propertyNames + 
                "\ndefaultValues=" + defaultValues + 
                "\ncharsetName=" + charsetName + 
//                ", validator=" + validator + 
                ", maxTrials=" + maxTrials + 
//                ", optionsProvider=" + optionsProvider + 
                ", built=" + built + 
                ", lastValidationSuccessful=" + lastValidationSuccessful + 
                ", validationAttempts=" + validationAttempts + 
                ", updateCount=" + updateCount + 
                '}';
    }
}
