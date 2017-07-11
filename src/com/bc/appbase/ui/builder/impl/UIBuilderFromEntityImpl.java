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

import com.bc.appbase.App;
import com.bc.appbase.jpa.EntityStructureFactory;
import com.bc.appbase.ui.SequentialLayout;
import com.bc.appbase.ui.VerticalLayout;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.builder.UIBuilderFromEntity;
import com.bc.appbase.ui.builder.UIBuilderFromMap;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.appcore.util.RelationAccess;
import com.bc.jpa.EntityUpdater;
import com.bc.util.ReflectionUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2017 9:38:51 PM
 */
public class UIBuilderFromEntityImpl extends AbstractUIBuilder<UIBuilderFromEntity, Object> 
        implements UIBuilderFromEntity {

    private static final Logger logger = Logger.getLogger(UIBuilderFromEntityImpl.class.getName());
    
    private App app;
    private Boolean addOptionToViewRelated;
    
    public UIBuilderFromEntityImpl() { 
        this(new VerticalLayout());
    }
    
    public UIBuilderFromEntityImpl(SequentialLayout sequentialLayout) { 
        super(sequentialLayout);
    }

    @Override
    public UIBuilderFromEntity app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public UIBuilderFromEntity addOptionToViewRelated(boolean b) {
        this.addOptionToViewRelated = b;
        return this;
    }

    @Override
    public Container build() {
        
        if(this.addOptionToViewRelated == null) {
            this.addOptionToViewRelated = this.getSourceData() != null;
        }

        final Container container = super.build();
        
        if(this.addOptionToViewRelated) {
            this.addAccessToViewRelated(this.getSourceData(), container, this.getRelatedTypes(this.getSourceType()));
        }
        
        return container;
    }
    
    @Override
    public Map buildStructure() {
        final Map structure = this.buildStructure(this.getSourceType(), this.getSourceData());
        return structure;
    }    
    
    @Override
    public boolean build(Class entityType, Object entity, Container container) {
        
        final Map structure = this.buildStructure(entityType, entity);

        this.build(structure, container);
        
        return true;
    }
    
    public Map buildStructure(Class entityType, Object entity) {
        
        final Map structure;
        
        final EntityStructureFactory esf = this.app.getOrException(EntityStructureFactory.class);
        
        if(entity == null) {
            
            structure = esf.getNested(entityType);
            
        }else{

            final EntityUpdater updater = this.app.getJpaContext().getEntityUpdater(entityType);

            final boolean existingEntity = updater.getId(entity) != null;

            final boolean nullsAllowed = !existingEntity;

            structure = esf.get(entity, nullsAllowed, nullsAllowed);
        }
        
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Entity type: {0}, Structure:\n{1}", 
                    new Object[]{entityType, this.app.getJsonFormat().toJSONString(structure)});
        }
        
        return structure;
    }
    
    public Set<Class> getRelatedTypes(Class entityType) {
        
        final Predicate<Class> isEntityType = (cls) -> cls.getAnnotation(Entity.class) != null;
        final SelectionContext selectionContext = this.app.getOrException(SelectionContext.class);
        final Predicate<Class> isNotSelectionType = (cls) -> !selectionContext.isSelectionType(cls);
        
        final RelationAccess relationAccess = app.getOrException(RelationAccess.class);
        
        final Set<Class> relatedTypes = relationAccess.getChildTypes(entityType, isEntityType.and(isNotSelectionType));
        
        return relatedTypes;
    }

    @Override
    public void build(Map structure, Container container) {
    
        final UIBuilderFromMap mapUIBuilder = app.getOrException(UIBuilderFromMap.class);
        
        final Container ui = mapUIBuilder
                .sourceType(this.getSourceType())
                .sourceData(structure)
                .targetUI(container)
                .selectionContext(this.getSelectionContext())
                .typeProvider(this.getTypeProvider()) 
                .entryUIProvider(this.getComponentModel())
                .editable(this.isEditable())      
                .build();
        
        app.getExpirableAttributes().putFor(ui, structure);
    }

    public Container addAccessToViewRelated(Object entity, Container ui, Set<Class> relatedTypes) {
        
        final Container output;
        
        if(relatedTypes.isEmpty()) {
            
            output = ui;
            
        }else{
            
            final RelationAccess relationAccess = app.getOrException(RelationAccess.class);

            final JPanel top = new JPanel();

            final JPanel bottom = new JPanel();

            final Font font = this.getFont(ui, null);
            
            final ReflectionUtil reflection = new ReflectionUtil();
//System.out.println("Entity: "+entity+". @"+this.getClass());
            for(Class relatedType : relatedTypes) {

                final Map<Method, Collection> selectedMethods = relationAccess.getOneToManyGetterMethods(entity, relatedType);
                
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

                    app.getExpirableAttributes().putFor(text, returnValue);
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
        this.app.getUIContext().addActionListeners(button, button);
        return button;
    }
}
