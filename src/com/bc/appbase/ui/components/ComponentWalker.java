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

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 16, 2017 4:36:57 PM
 */
public interface ComponentWalker {

    List<Component> find(Component c, Predicate<Component> test);

    void find(Component c, Predicate<Component> test, int limit, boolean inclusive, List<Component> collectInto);

    List<Component> findChildren(Component c, Predicate<Component> test);

    void findChildren(Component c, Predicate<Component> test, int limit, boolean inclusive, List<Component> collectInto);

    Component findFirst(Component c, Predicate<Component> test, Component outputIfNone);

    Component findFirst(Component c, Predicate<Component> test, boolean inclusive, Component outputIfNone);

    Component findFirstChild(Component c, Predicate<Component> test, Component outputIfNone);

    Component findFirstChild(Component c, Predicate<Component> test, boolean inclusive, Component outputIfNone);

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
    Container getTopLevelAncestor(Component component);

    Container getTopmostParent(Component component, Container outputIfNone);

    int transverse(Component c, boolean inclusive, Consumer<Component> action);

    int transverse(Component c, Predicate<Component> test, int limit, boolean inclusive, Consumer<Component> action);

    int transverse(Container c, Predicate<Component> test, int limit, Consumer<Component> action);

    int transverseChildren(Component c, boolean inclusive, Consumer<Component> action);

    int transverseChildren(Component c, Predicate<Component> test, int limit, boolean inclusive, Consumer<Component> action);

    int transverseChildren(Container parent, Predicate<Component> test, int limit, Consumer<Component> action);
}
