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

package com.bc.appbase.ui.components;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2017 1:05:14 PM
 */
public class ComponentWalkerImpl implements ComponentWalker {

    private static final Logger logger = Logger.getLogger(ComponentWalkerImpl.class.getName());

    public ComponentWalkerImpl() { }
    
    /**
     * <b>Culled from {@link javax.swing.JComponent#getTopLevelAncestor()}</b>
     * Returns the top-level ancestor of this component (either the
     * containing <code>Window</code> or <code>Applet</code>),
     * or <code>null</code> if this component has not
     * been added to any container.
     *
     * @param component
     * @return the top-level <code>Container</code> that this component is in,
     *          or <code>null</code> if not in any container
     * @see javax.swing.JComponent#getTopLevelAncestor() 
     */
    @Override
    public Container getTopLevelAncestor(Component component) {
        for(; component != null; component = component.getParent()) {
            if(component instanceof Window || component instanceof Applet) {
                return (Container)component;
            }
        }
        return null;
    }
    
    @Override
    public Container getTopmostParent(Component component, Container outputIfNone) {
        Container parent = component.getParent();
        if(parent == null) {
            parent = outputIfNone;
        }else{
            while( parent.getParent() != null) {
                parent = parent.getParent();
            }
        }
        return parent;
    }
    
    @Override
    public Component findFirst(Component c, Predicate<Component> test, Component outputIfNone) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        return this.findFirstChild(parent==null?c:parent, test, outputIfNone);
    }

    @Override
    public Component findFirstChild(Component c, Predicate<Component> test, Component outputIfNone) {
    
        return this.findFirstChild(c, test, false, outputIfNone);
    }
    
    @Override
    public Component findFirst(Component c, Predicate<Component> test, 
            boolean inclusive, Component outputIfNone) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        return this.findFirstChild(parent==null?c:parent, test, inclusive, outputIfNone);
    }
    
    @Override
    public Component findFirstChild(Component c, Predicate<Component> test, 
            boolean inclusive, Component outputIfNone) {
    
        final List<Component> collectInto = new ArrayList(1);
        
        this.findChildren(c, test, 1, inclusive, collectInto);
        
        return collectInto.isEmpty() ? outputIfNone : collectInto.get(0);
    }

    @Override
    public List<Component> find(Component c, Predicate<Component> test) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        return this.findChildren(parent==null?c:parent, test);
    }
    
    @Override
    public List<Component> findChildren(Component c, Predicate<Component> test) {
        
        final List<Component> collectInto = new ArrayList();
        
        this.findChildren(c, test, Integer.MAX_VALUE, false, collectInto);
        
        return collectInto;
    }

    @Override
    public void find(Component c, Predicate<Component> test, 
            int limit, boolean inclusive, List<Component> collectInto) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        this.findChildren(parent==null?c:parent, test, limit, inclusive, collectInto);
    }
    
    @Override
    public void findChildren(Component c, Predicate<Component> test, 
            int limit, boolean inclusive, List<Component> collectInto) {
    
        Consumer<Component> collect = (component) -> collectInto.add(component);
        
        this.transverseChildren(c, test, limit, inclusive, collect);
    }

    @Override
    public int transverse(Component c, boolean inclusive, Consumer<Component> action) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        return this.transverseChildren(parent==null?c:parent, inclusive, action);
    }
    
    @Override
    public int transverseChildren(Component c, boolean inclusive, Consumer<Component> action) {
    
        return this.transverseChildren(c, (component) -> true, Integer.MAX_VALUE, inclusive, action);
    }

    @Override
    public int transverse(Component c, Predicate<Component> test, 
            int limit, boolean inclusive, Consumer<Component> action) {
        
        final Container parent = this.getTopmostParent(c, null);
        
        return this.transverseChildren(parent==null?c:parent, test, limit, inclusive, action);
    }
    
    @Override
    public int transverseChildren(Component c, Predicate<Component> test, 
            int limit, boolean inclusive, Consumer<Component> action) {
    
        logger.finer(() -> "Candidate: " + c);
        
        int transverseCount = 0;
        
        Objects.requireNonNull(c);
        
        Objects.requireNonNull(test);
        
        Objects.requireNonNull(action);
        
        if(inclusive) {
            
            if(test.test(c)) {
             
                if(transverseCount < limit) {
                  
                    action.accept(c);
                    ++transverseCount;
                }
            }
        }
        
        if(c instanceof Container) {
            transverseCount += this.transverseChildren((Container)c, test, limit - transverseCount, action);
        }
        
        logger.log(Level.FINER, "Transverse count: {0}", transverseCount);
        
        return transverseCount;
    }
    
    @Override
    public int transverse(Container c, Predicate<Component> test, 
            int limit, Consumer<Component> action) {
        
        final Container parent = this.getTopmostParent(c, c);
        
        return this.transverseChildren(parent, test, limit, action);
    }
    
    @Override
    public int transverseChildren(Container parent, Predicate<Component> test, 
            int limit, Consumer<Component> action) {
         
        int transverseCount = 0;
        
        final int count = parent.getComponentCount(); 

        for(int i=0; i<count; i++) {

            if(transverseCount >= limit) {
                break;
            }

            Component child = parent.getComponent(i);

            transverseCount += this.transverseChildren(child, test, limit - transverseCount, true, action);
        }
        
        return transverseCount;
    }
}
