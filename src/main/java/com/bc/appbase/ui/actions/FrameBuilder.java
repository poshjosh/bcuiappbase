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

import com.bc.appbase.ui.table.TableFormat;
import com.bc.appbase.ui.UIContext;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 8, 2017 10:03:24 PM
 */
public class FrameBuilder {
    
    private final UIContext uiContext;
    
    private JTable table;

    public FrameBuilder(UIContext uiContext) {
        this.uiContext = uiContext;
    }

    public JFrame build(TableModel tableModel, Class entityType, int serialColumnIndex) {
        
        final JFrame frame = this.newFrame();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        table = this.newTable(tableModel);

        frame.getContentPane().add(new JScrollPane(table));

        new TableFormat(uiContext).format(table);
        
        frame.pack();
        
        this.position(frame);
        
        uiContext.updateTableUI(table, entityType, serialColumnIndex);
        
        return frame;
    }

    public JTable getTable() {
        return table;
    }
    
    public JFrame newFrame() {
        return new JFrame();
    }
    
    public JTable newTable(TableModel tableModel) {
        return new JTable(tableModel);
    }
    
    public void position(Window window) {
        uiContext.positionFullScreen(window);
    }
}
