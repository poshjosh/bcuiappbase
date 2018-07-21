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
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JTable;
import com.bc.appbase.App;
import com.bc.appbase.ui.table.TableForPrinting;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.util.Locale;
import java.util.Objects;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSize;
import javax.swing.JFrame;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:10 AM
 */
public class Print implements Action<App, Boolean> {

    private static final Logger logger = Logger.getLogger(Print.class.getName());
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Class entityType = this.getEntityTypeOrException(app, params);
        
        final TableModel tableModel = (TableModel)app.getAction(
                ActionCommands.PROMPT_SELECT_ROWS_AS_TABLE_MODEL).execute(app, params);
        
        final String title = "Print Results for " + entityType.getSimpleName();
        
        final Boolean output = execute(app, tableModel, entityType, title);
        
        return output;
    }

    public Boolean execute(App app, TableModel tableModel, Class entityType, String title) 
            throws ParameterException, TaskExecutionException {    

        final FrameBuilder frameBuilder = new FrameBuilder(app.getUIContext()){
            @Override
            public JTable newTable(TableModel tableModel) {
                return new TableForPrinting(null, tableModel);
            }
            @Override
            public JFrame newFrame() {
                return new JFrame(title);
            }
        };
        
        final JFrame frame = frameBuilder.build(tableModel, entityType, -1);
        
        final JTable table = frameBuilder.getTable();
        
//System.out.println("- - - - - - -  AFTER Table size: "+table.getSize()+" "+this.getClass().getName()); 
        return this.print(app, frame, table, title);
    }    
    
    private Boolean print(App app, JFrame frame, JTable table, String title) {
        
        final Boolean success;
        try{
            
            frame.setVisible(true);
            
            success = this.print(table, this.getPrintRequestAttributeSet(title));
            
        }finally{
            
            frame.setVisible(false);
            
            frame.dispose();
        }

        if(success) {

            app.getUIContext().showSuccessMessage("Print Job sent to Printer");

        }else{

            app.getUIContext().showErrorMessage(null, "Error Printing");
        }

        return success;
    }
    
    public JTable getTable(TableModel tableModel, int verticalSpaceFromHeaderOrFooterToTable) {
        
        final JTable table = new TableForPrinting(null, tableModel);
        
        return table;
    }
    
    public PrintRequestAttributeSet getPrintRequestAttributeSet(String jobName) {
        final PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(new JobName(jobName, Locale.getDefault()));
        attr.add(OrientationRequested.LANDSCAPE);
        final MediaSize mediaSize = MediaSize.ISO.A4;
        attr.add(mediaSize.getMediaSizeName()); 
//        final int units = MediaPrintableArea.INCH;
//        final float w = mediaSize.getX(units);
//        final float h = mediaSize.getY(units);
//System.out.println("============================== Width: "+w+", height: "+h+" "+this.getClass().getName());  
//        attr.add(new MediaPrintableArea(0.0f, 0.0f, w, h, units)); 
//        attr.add(new MediaPrintableArea(0.5f, 0.5f, w-0.5f, h-0.5f, units)); 
//        final PrinterResolution madeTheOutputFontVerySmall = new PrinterResolution(144, 144, PrinterResolution.DPI);
//        attr.add(madeTheOutputFontVerySmall);
        return attr;
    }
    
    public Boolean print(JTable table, PrintRequestAttributeSet attr) {
        
        try{

            final String NEW_LINE = System.getProperty("line.separator");
            final MessageFormat headerFormat = new MessageFormat("RESTRICTED"); 
            final MessageFormat footerFormat = new MessageFormat("{0}"+NEW_LINE+"RESTRICTED"); 
            
            if(table.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat, true, attr, true)) {
            
                return Boolean.TRUE;
            }
        }catch(PrinterException e) {
            
            logger.log(Level.WARNING, "Error printing table", e);
            
            return Boolean.FALSE;
        }
        
        return Boolean.FALSE;
    }
    
    public Class getEntityTypeOrException(App app, Map<String, Object> params) {
        Class entityType = (Class)params.get(ParamNames.ENTITY_TYPE);
        if(entityType == null) {
            entityType = app.getSearchContext(entityType).getResultType();
        }
        Objects.requireNonNull(entityType);
        return entityType;
    }
}
/**
 * 
    public Boolean execute_old(App app, Map<String, Object> params) 
            throws TaskExecutionException {
        
        final Class resultType = (Class)params.get(ParamNames.ENTITY_TYPE);
        final JTable table = (JTable)params.get(JTable.class.getName());
        
        final SearchResults searchResults = app.getUIContext().getLinkedSearchResults(table, null);
        
        final DialogManager.PageSelection pageSelection;
        if(searchResults != null && searchResults.getPageCount() > 1) {
            pageSelection = 
                    app.getOrException(DialogManager.class).promptSelectPages(
                            "Which page(s) do you want to print?", 
                            "Select Page(s)", JOptionPane.QUESTION_MESSAGE);
        }else{
            pageSelection = DialogManager.PageSelection.CurrentPage;
        }
        
        final Boolean success;
        
        if(pageSelection == null) {
            success = Boolean.FALSE;
        }else{
            switch(pageSelection) {
                case CurrentPage:
                    success = this.print(app, table); break;
                case AllPages:
                    this.print(app, resultType, searchResults, 0, searchResults.getPageCount());
                    success = Boolean.TRUE; break;
                case FirstPage:
                    this.print(app, resultType, searchResults, 0, 1);
                    success = Boolean.TRUE; break;
                default: 
                    app.getUIContext().showErrorMessage(null, "Only printing of 'Current Page' or 'All Pages' or 'First Page' is supported for now");
                    success = Boolean.FALSE;
            }
        }
        
        return success;
    }
   
    public void print(App app, Class resultType, SearchResults searchResults, int pageNum, int numberOfPages) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.doPrint(app, resultType, searchResults, pageNum, numberOfPages);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    doPrint(app, resultType, searchResults, pageNum, numberOfPages);
                }catch(RuntimeException e) {
                    logger.log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    
    private Boolean doPrint(App app, Class resultType, SearchResults searchResults, int pageNum, int numberOfPages) {
        
        final String KEY = Long.toString(System.currentTimeMillis()) + "_for_printing";
        final String MSG = searchResults.getSize() + " results for printing";
        
        final ResultsFrame allResultsFrame = app.getUIContext().createSearchResultsFrame(
                app.getSearchContext(resultType), searchResults, KEY, pageNum, numberOfPages, MSG, true);
        try{
            app.getUIContext().positionFullScreen(allResultsFrame);
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

 * 
 */