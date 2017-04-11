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
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 3:12:48 PM
 */
public class SearchResultsPanelMouseRightClickListener implements MouseListener {

    private final App app;
    
    private final SearchResultsPanel searchResultsPanel;
    
    private final JPopupMenu rightClickMenu;
    
    /**
     * The relative position of the mouse while dragging.
     */
    private Point mousePositionRelativeToSource;
    
    private int selectedRow;
    
    private int selectedColumn;

    public SearchResultsPanelMouseRightClickListener(App app, SearchResultsPanel searchResultsPanel) {
        this.app = Objects.requireNonNull(app);
        this.searchResultsPanel = Objects.requireNonNull(searchResultsPanel);
        this.rightClickMenu = new JPopupMenu();
        this.rightClickMenu.setName (searchResultsPanel.getClass().getSimpleName()+"_Table_RightClickMenu");
        final JTable table = this.searchResultsPanel.getSearchResultsTable();
        this.rightClickMenu.setInvoker(table);
        final UIContext uiContext = app.getUIContext();
        final Font font = uiContext.getFont(this.rightClickMenu, uiContext.getFont(table, uiContext.getFont()));
        this.rightClickMenu.setFont(font);
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param event The mouse pressed event.
     */
    @Override
    public void mousePressed (MouseEvent event) {
        
        final Object component = event.getSource ();
        
        if (component instanceof JTable) {
            
            this.updateSelection(component);
            
            // translate the point relative to the enclosing container
            Point point = event.getPoint ();
            Point upperleft = ((Component)component).getLocation ();
            point.translate (upperleft.x, upperleft.y);
            
            this.mousePositionRelativeToSource = point;
            
        }else {
            
            this.mousePositionRelativeToSource = null;
        }    
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * @param event The mouse clicked event.
     */
    @Override
    public void mouseClicked (MouseEvent event) {
        
        if(!SwingUtilities.isRightMouseButton(event)) {
            return;
        }    

        final Object component = event.getSource ();
        
        if(component instanceof JTable) {
            
            this.updateSelection(component);
            
//            this.rightClickMenu.setInvoker((JTable)component);
            
            this.rightClickMenu.show(event.getComponent(), event.getX(), event.getY());
        }    
    }
    
    private void updateSelection(Object component) {
        JTable table = (JTable)component;
        this.selectedRow = table.getSelectedRow();
        this.selectedColumn = table.getSelectedColumn();
    }

    public void addMenuItem(String text, String actionCommand) {
        final JMenuItem item = new JMenuItem(text);
        item.setFont(this.rightClickMenu.getFont());
        item.setActionCommand(actionCommand);
        item.addActionListener(
                app.getUIContext().getActionListener(this.searchResultsPanel, actionCommand)
        );
        this.rightClickMenu.add(item);
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    public Point getMousePositionRelativeToSource() {
        return mousePositionRelativeToSource;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }
}
