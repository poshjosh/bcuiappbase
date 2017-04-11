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

import com.bc.appcore.actions.Action;
import com.bc.jpa.search.SearchResults;
import com.bc.appbase.ui.DialogManager;
import com.bc.appbase.ui.SearchResultsFrame;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.event.WindowEvent;
import javax.print.attribute.standard.MediaSizeName;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:10 AM
 */
public class Print implements Action<App, Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws com.bc.appcore.actions.TaskExecutionException {
        
        final DialogManager.PageSelection pageSelection = 
                app.get(DialogManager.class).promptSelectPages("Which pages do you want to print?");
        
        final JTable table = (JTable)params.get(JTable.class.getName());
        
        final Boolean success;
        
        if(pageSelection == null) {
            success = Boolean.FALSE;
        }else{
            switch(pageSelection) {
                case CurrentPage:
                    success = this.print(app, table); break;
                case AllPages:
                    final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table);
                    this.print(app, searchResults, 0, searchResults.getPageCount());
                    success = Boolean.TRUE; break;
                case FirstPage:
                    this.print(app, app.getUIContext().getLinkedSearchResults(table), 0, 1);
                    success = Boolean.TRUE; break;
                default: 
                    app.getUIContext().showErrorMessage(null, "Only printing of 'Current Page' or 'All Pages' or 'First Page' is supported for now");
                    success = Boolean.FALSE;
            }
        }
        
        return success;
    }
    
    public Boolean print(App app, JTable table) {
        
        try{

//            final PrinterResolution madeTheOutputFontVerySmall = new PrinterResolution(144, 144, PrinterResolution.DPI);

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(OrientationRequested.LANDSCAPE);
            aset.add(MediaSizeName.ISO_A4);
//            aset.add(madeTheOutputFontVerySmall);
    
            MessageFormat header = null; //new MessageFormat("RESTRICTED"); 
            MessageFormat footer = new MessageFormat("{0}"); 
            
            if(table.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, aset, true)) {
            
                app.getUIContext().showSuccessMessage("Print Successful");
                
                return Boolean.TRUE;
            }
        }catch(PrinterException e) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error printing table", e);
            
            app.getUIContext().showErrorMessage(e, "Print Failed");
        }
        
        return Boolean.FALSE;
    }
    
    public void print(App app, SearchResults searchResults, int pageNum, int numberOfPages) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.doPrint(app, searchResults, pageNum, numberOfPages);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    doPrint(app, searchResults, pageNum, numberOfPages);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    
    private Boolean doPrint(App app, SearchResults searchResults, int pageNum, int numberOfPages) {
        
        final SearchResultsFrame allResultsFrame = app.getUIContext().createSearchResultsFrame(
                app.getSearchContext(null), searchResults, null, pageNum, numberOfPages, searchResults.getSize()+" results for printing", true);
        try{
            app.getUIContext().positionFullScreen(allResultsFrame);
            allResultsFrame.pack();
            allResultsFrame.setVisible(true);
            final JTable allResultsTable = allResultsFrame.getSearchResultsPanel().getSearchResultsTable();
            Boolean success = this.print(app, allResultsTable);
            return success;
        }finally{
            allResultsFrame.dispatchEvent(new WindowEvent(allResultsFrame, WindowEvent.WINDOW_CLOSING));
            allResultsFrame.setVisible(false);
            allResultsFrame.dispose();
        }
    }
}
