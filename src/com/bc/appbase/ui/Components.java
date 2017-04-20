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

package com.bc.appbase.ui;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 15, 2017 1:05:14 PM
 */
public class Components {

    public int getComponentCount(Component c) {
        int output;
        if(c instanceof Container) {
            output = ((Container)c).getComponentCount();
        }else{
            output = -1;
        }
        return output;
    }
    
    public Set<String> getChildNames(Component component) {
        final Set<String> childNames;
        if(component instanceof Container) {
            final Container container = (Container)component;
            final int count = container.getComponentCount();
            childNames = new HashSet(count);
            for(int i=0; i<count; i++) {
                final String childName = container.getComponent(i).getName();
                if(childName != null) {
                    childNames.add(childName);
                }
            }
        }else{
            childNames = Collections.EMPTY_SET;
        }
        return childNames;
    }

    public Component findFirstChild(Component c, Predicate<Component> test, Component outputIfNone) {
    
        return this.findFirstChild(c, test, false, outputIfNone);
    }
    
    public Component findFirstChild(Component c, Predicate<Component> test, boolean inclusive, Component outputIfNone) {
    
        final List<Component> collectInto = new ArrayList(1);
        
        this.findChildren(c, test, 1, inclusive, collectInto);
        
        return collectInto.isEmpty() ? outputIfNone : collectInto.get(0);
    }
    
    public List<Component> findChildren(Component c, Predicate<Component> test) {
        
        final List<Component> collectInto = new ArrayList();
        
        this.findChildren(c, test, Integer.MAX_VALUE, false, collectInto);
        
        return collectInto;
    }
    
    public void findChildren(Component c, Predicate<Component> test, int limit, boolean inclusive, List<Component> collectInto) {
        
        Objects.requireNonNull(c);
        
        Objects.requireNonNull(test);
        
        Objects.requireNonNull(collectInto);
        
        if(inclusive && test.test(c)) {
            if(collectInto.size() < limit) {
                collectInto.add(c);
            }
        }else{
            if(c instanceof Container) {
                
                final Container parent = (Container)c;
                
                final int count = parent.getComponentCount();
                
                for(int i=0; i<count; i++) {
                    
                    if(collectInto.size() >= limit) {
                        break;
                    }
                    
                    Component child = parent.getComponent(i);
                    
                    this.findChildren(child, test, limit, true, collectInto);
                }
            }
        }
    }
}
