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

package com.bc.appbase.ui.functions;

import com.bc.appbase.ui.UIContext;
import com.bc.ui.layout.VerticalLayout;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.functions.GetRelatedTypes;
import com.bc.appcore.util.RelationAccess;
import com.bc.reflection.ReflectionUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 8:02:05 PM
 */
public class AddAccessToViewRelatedTypesImpl implements AddAccessToViewRelatedTypes {

    private static final Logger logger = Logger.getLogger(AddAccessToViewRelatedTypesImpl.class.getName());
    
    private final ObjectFactory objectFactory;
    
    private final UIContext uiContext;
    
    private BiConsumer<String, Collection> consumer;

    private boolean built;
    
    public AddAccessToViewRelatedTypesImpl(ObjectFactory objectFactory, UIContext uiContext) {
        this.objectFactory = Objects.requireNonNull(objectFactory);
        this.uiContext = Objects.requireNonNull(uiContext);
        this.consumer = (x, y) -> {};
    }
    
    public AddAccessToViewRelatedTypes consumer(BiConsumer<String, Collection> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Override
    public Container apply(Object instance, Container ui) {
        
        if(built) {
            throw new IllegalStateException("apply method may only be called once");
        }
        this.built = true;
        
        final Set<Class> relatedTypes = objectFactory.getOrException(GetRelatedTypes.class).apply(instance.getClass());
        
        final Container output;
        
        if(relatedTypes.isEmpty()) {
            
            output = ui;
            
        }else{
            
            final RelationAccess relationAccess = objectFactory.getOrException(RelationAccess.class);

            final JPanel top = new JPanel();

            final JPanel bottom = new JPanel();

            final Font font = this.getFont(ui, null);
            
            final ReflectionUtil reflection = new ReflectionUtil();
//System.out.println("Entity: "+entity+". @"+this.getClass());
            for(Class relatedType : relatedTypes) {

                final Map<Method, Collection> selectedMethods = relationAccess.getOneToManyGetterMethods(instance, relatedType);
                
                if(selectedMethods.isEmpty()) {
                    continue;
                }
//System.out.println("Related: "+relatedType+". @"+this.getClass());
                for(Method method : selectedMethods.keySet()) {
                    
                    final Collection returnValue = selectedMethods.get(method);
//System.out.println("Method: "+method.getName()+", "+returnValue+". @"+this.getClass());                    
                    if(returnValue == null || returnValue.isEmpty()) {
                        continue;
                    }

                    final Class returnTypeArg = (Class)reflection.getGenericReturnTypeArguments(method)[0];
                    final String text = "View "+returnTypeArg.getSimpleName()+"s";

                    final AbstractButton topButton = this.createButton(text, font);
                    top.add(topButton);

                    final AbstractButton bottomButton = this.createButton(text, font);
                    bottom.add(bottomButton);

                    this.consumer.accept(text, returnValue);
                }
            }

            final JPanel main = new JPanel();
            final List<Component> components = Arrays.asList(top, ui, bottom);
            final VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.addComponents(main, components);

            output = main;
        }
        
        return output;
    }

    public Font getFont(Component c, Font outputIfNone) {
        
//        if(logger.isLoggable(Level.FINER)) {
//            logger.log(Level.FINER, "Component type: {0}, name: {1}", 
//                    new Object[]{c.getClass().getName(), c.getName()});
//        }
        
        Font output; 
        final Font font = c.getFont();
        if(font != null && !(font instanceof FontUIResource)) {
            output = font;
        }else {
            output = outputIfNone;
            if(c instanceof Container) {
                final Container parent = (Container)c;
                for(int i=0; i < parent.getComponentCount(); i++) {
                    final Component child = parent.getComponent(i);
                    final Font childFont = this.getFont(child, null);
                    if(childFont != null && !(childFont instanceof FontUIResource)) {
                        output = childFont;
                        break;
                    }
                }
            }
        }

        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Component type: {0}, name: {1}, extracted font: {2}", 
                    new Object[]{c.getClass().getName(), c.getName(), output});
        }
        
        return output;
    }
    
    public AbstractButton createButton(String text, Font font) {
        final JButton button = new JButton(text);
        button.setName(text);
        button.setActionCommand(ActionCommands.DISPLAY_RECORD_LIST);
        button.setFont(font);
        this.uiContext.addActionListeners(button, button);
        return button;
    }
}
