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

package com.bc.appbase.ui.table;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Jun 30, 2017 12:13:59 AM
 */
public class TablePrintableCustom implements Printable {

    /** The table to print. */
    private final JTable table;

    /** For quick reference to the table's header. */
    private final JTableHeader header;

    /** For quick reference to the table's column model. */
    private final TableColumnModel colModel;

    /** To save multiple calculations of total column width. */
    private final int totalColWidth;

    /** The printing mode of this printable. */
    private final JTable.PrintMode printMode;

    /** Provides the header text for the table. */
    private final MessageFormat headerFormat;

    /** Provides the footer text for the table. */
    private final MessageFormat footerFormat;

    /** The most recent page index asked to print. */
    private int last = -1;

    /** The next row to print. */
    private int row = 0;

    /** The next column to print. */
    private int col = 0;

    /** Used to store an area of the table to be printed. */
    private final Rectangle clip = new Rectangle(0, 0, 0, 0);

    /** Used to store an area of the table's header to be printed. */
    private final Rectangle hclip = new Rectangle(0, 0, 0, 0);

    /** Saves the creation of multiple rectangles. */
    private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);

    private final TablePrintProperties printProperties;

    /**
     * Create a new <code>TablePrintable</code> for the given
     * <code>JTable</code>. Header and footer text can be specified using the
     * two <code>MessageFormat</code> parameters. When called upon to provide
     * a String, each format is given the current page number.
     *
     * @param  table           the table to print
     * @param  printMode       the printing mode for this printable
     * @param  headerFormat    a <code>MessageFormat</code> specifying the text to
     *                         be used in printing a header, or null for none
     * @param  footerFormat    a <code>MessageFormat</code> specifying the text to
     *                         be used in printing a footer, or null for none
     * @param  printProperties the print properties (e.g font, vertical-space etc)
     * @throws IllegalArgumentException if passed an invalid print mode
     */
    public TablePrintableCustom(JTable table,
                          JTable.PrintMode printMode,
                          MessageFormat headerFormat,
                          MessageFormat footerFormat,
                          TablePrintProperties printProperties) {

        this.table = Objects.requireNonNull(table);

        header = table.getTableHeader();
        colModel = table.getColumnModel();
        totalColWidth = colModel.getTotalColumnWidth();

        if (header != null) {
            // the header clip height can be set once since it's unchanging
            hclip.height = header.getHeight();
        }

        this.printMode = printMode;

        this.headerFormat = headerFormat;
        this.footerFormat = footerFormat;
        
        this.printProperties = Objects.requireNonNull(printProperties);
    }

    /**
     * Prints the specified page of the table into the given {@link Graphics}
     * context, in the specified format.
     *
     * @param   graphics    the context into which the page is drawn
     * @param   pageFormat  the size and orientation of the page being drawn
     * @param   pageIndex   the zero based index of the page to be drawn
     * @return  PAGE_EXISTS if the page is rendered successfully, or
     *          NO_SUCH_PAGE if a non-existent page index is specified
     * @throws  PrinterException if an error causes printing to be aborted
     */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                                                       throws PrinterException {

        // for easy access to these values
        final int imgWidth = (int)pageFormat.getImageableWidth();
        final int imgHeight = (int)pageFormat.getImageableHeight();
//System.out.println("Page image, width: "+imgWidth+", height: "+imgHeight+" "+this.getClass().getName());
        if (imgWidth <= 0) {
            throw new PrinterException("Width of printable area is too small.");
        }

        final TablePrintProperties.HeaderFooterProperties headerProps = this.printProperties.getHeaderProperties();
        final TablePrintProperties.HeaderFooterProperties footerProps = this.printProperties.getFooterProperties();

        // to pass the page number when formatting the header and footer text
        final Object[] pageNumber = new Object[]{pageIndex + 1};

        // fetch the formatted header text, if any
        final String [] headerText = this.getHeaderText(headerFormat, pageNumber, null);
//System.out.println("Header text: "+(headerText==null?null:Arrays.toString(headerText))+" "+this.getClass().getName());

        // to store the bounds of the header text
        final Rectangle2D [] headerRect;

        // the amount of vertical space needed for the header text
        final int headerTextTotalHeight;

        // the amount of vertical space available for printing the table
        int availableSpace = imgHeight;
        
        // if there's header text, find out how much space is needed for it
        // and subtract that from the available space
        if (headerText == null) {
            headerRect = null;
            headerTextTotalHeight = 0;
        }else{   
            graphics.setFont(headerProps.getFont());
            headerRect = new Rectangle2D[headerText.length];
            int cummulativeHeight = 0;
            double cummulativeY = 0;
            for(int i=0; i<headerRect.length; i++) {
                final Rectangle2D rec = graphics.getFontMetrics().getStringBounds(headerText[i], graphics);
                cummulativeHeight += (int)Math.ceil(rec.getHeight());
                cummulativeY += rec.getY();
                headerRect[i] = new Rectangle2D.Double(rec.getX(), cummulativeY, rec.getWidth(), rec.getHeight());
            }
            headerTextTotalHeight = cummulativeHeight;
            availableSpace -= (headerTextTotalHeight + headerProps.getVerticalSpaceToTable());
        }
//System.out.println("Header text total space: "+headerTextTotalHeight+" "+this.getClass().getName());

        // fetch the formatted footer text, if any
        final String [] footerText = this.getFooterText(footerFormat, pageNumber, null);
//System.out.println("Footer text: "+(footerText==null?null:Arrays.toString(footerText))+" "+this.getClass().getName());

        // to store the bounds of the footer text
        final Rectangle2D [] footerRect;
        
        // the amount of vertical space needed for the footer text
        final int footerTextTotalHeight;
        
        // if there's footer text, find out how much space is needed for it
        // and subtract that from the available space
        if (footerText == null) {
            footerRect = null;
            footerTextTotalHeight = 0;
        }else{    
            graphics.setFont(footerProps.getFont());
            footerRect = new Rectangle2D[footerText.length];
            int cummulativeHeight = 0;
            double cummulativeY = 0;
            for(int i=0; i<footerRect.length; i++) {
                final Rectangle2D rec = graphics.getFontMetrics().getStringBounds(footerText[i], graphics);
                footerRect[i] = new Rectangle2D.Double(rec.getX(), cummulativeY, rec.getWidth(), rec.getHeight());
                cummulativeHeight += (int)Math.ceil(rec.getHeight());
                cummulativeY += rec.getY();
            }
            footerTextTotalHeight = cummulativeHeight;
            availableSpace -= (footerTextTotalHeight + footerProps.getVerticalSpaceToTable());
        }
//System.out.println("Footer text total space: "+footerTextTotalHeight+" "+this.getClass().getName());

        if (availableSpace <= 0) {
            throw new PrinterException("Height of printable area is too small.");
        }

        // depending on the print mode, we may need a scale factor to
        // fit the table's entire width on the page
        final double scaleFactor;
        if (printMode == JTable.PrintMode.FIT_WIDTH &&
                totalColWidth > imgWidth) {

            // if not, we would have thrown an acception previously
            assert imgWidth > 0;

            // it must be, according to the if-condition, since imgWidth > 0
            assert totalColWidth > 1;

            scaleFactor = (double)imgWidth / (double)totalColWidth;
        }else{
            scaleFactor = 1.0D;
        }

        // dictated by the previous two assertions
        assert scaleFactor > 0;

        // This is in a loop for two reasons:
        // First, it allows us to catch up in case we're called starting
        // with a non-zero pageIndex. Second, we know that we can be called
        // for the same page multiple times. The condition of this while
        // loop acts as a check, ensuring that we don't attempt to do the
        // calculations again when we are called subsequent times for the
        // same page.
        while (last < pageIndex) {
            // if we are finished all columns in all rows
            if (row >= table.getRowCount() && col == 0) {
                return NO_SUCH_PAGE;
            }

            // rather than multiplying every row and column by the scale factor
            // in findNextClip, just pass a width and height that have already
            // been divided by it
            final int scaledWidth = (int)(imgWidth / scaleFactor);
            final int scaledHeight = (int)((availableSpace - hclip.height) / scaleFactor);

            // calculate the area of the table to be printed for this page
            findNextClip(scaledWidth, scaledHeight);

            last++;
        }

        // create a copy of the graphics so we don't affect the one given to us
        final Graphics2D g2d = (Graphics2D)graphics.create();

        // translate into the co-ordinate system of the pageFormat
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // to save and store the transform
        AffineTransform oldTrans;

        // if there's footer text, print it at the bottom of the imageable area
        if (footerText != null) {
            
            oldTrans = g2d.getTransform();

            final int y = imgHeight - (footerTextTotalHeight);

            g2d.translate(0, y);
                
            for(int i=0; i<footerText.length; i++) {
                
//System.out.println("@[0:"+y+" FOOTER: "+footerText[i]+" "+this.getClass().getName());                

                printText(g2d, footerText[i], footerRect[i], footerProps.getFont(), imgWidth);
            }
            
            g2d.setTransform(oldTrans);
        }

        // if there's header text, print it at the top of the imageable area
        // and then translate downwards
        if (headerText != null) {
            
            for(int i=0; i<headerText.length; i++) {
//                final Rectangle rec = g2d.getClipBounds();
//System.out.println("@["+rec.getX()+':'+rec.getY()+"] HEADER: "+headerText[i]+" "+this.getClass().getName());                                
                printText(g2d, headerText[i], headerRect[i], headerProps.getFont(), imgWidth);
            }
            
            g2d.translate(0, headerTextTotalHeight + headerProps.getVerticalSpaceToTable());
        }

        // constrain the table output to the available space
        tempRect.x = 0;
        tempRect.y = 0;
        tempRect.width = imgWidth;
        tempRect.height = availableSpace;
        g2d.clip(tempRect);

        // if we have a scale factor, scale the graphics object to fit
        // the entire width
        if (scaleFactor != 1.0D) {
            g2d.scale(scaleFactor, scaleFactor);

        // otherwise, ensure that the current portion of the table is
        // centered horizontally
        } else {
            int diff = (imgWidth - clip.width) / 2;
            g2d.translate(diff, 0);
        }

        // store the old transform and clip for later restoration
        oldTrans = g2d.getTransform();
        Shape oldClip = g2d.getClip();

        // if there's a table header, print the current section and
        // then translate downwards
        if (header != null) {
            hclip.x = clip.x;
            hclip.width = clip.width;

            g2d.translate(-hclip.x, 0);
            g2d.clip(hclip);
            header.print(g2d);

            // restore the original transform and clip
            g2d.setTransform(oldTrans);
            g2d.setClip(oldClip);

            // translate downwards
            g2d.translate(0, hclip.height);
        }

        // print the current section of the table
        g2d.translate(-clip.x, -clip.y);
        g2d.clip(clip);
        
        table.print(g2d);

        // restore the original transform and clip
        g2d.setTransform(oldTrans);
        g2d.setClip(oldClip);

        // draw a box around the table
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

        // dispose the graphics copy
        g2d.dispose();

        return PAGE_EXISTS;
    }
    
    public String [] getHeaderText(MessageFormat headerFormat, Object[] pageNumber, String [] outputIfNone) {
        final String headerText = headerFormat == null ? null : headerFormat.format(pageNumber);
        return headerText == null ? outputIfNone : new String[]{headerText};
    }

    public String [] getFooterText(MessageFormat footerFormat, Object[] pageNumber, String[] outputIfNone) {
        final String footerText = footerFormat == null ? null : footerFormat.format(pageNumber);
        return footerText == null ? outputIfNone : new String[]{footerText};
    }
    
    /**
     * A helper method that encapsulates common code for rendering the
     * header and footer text.
     *
     * @param  g2d       the graphics to draw into
     * @param  text      the text to draw, non null
     * @param  rect      the bounding rectangle for this text,
     *                   as calculated at the given font, non null
     * @param  font      the font to draw the text in, non null
     * @param  imgWidth  the width of the area to draw into
     */
    protected void printText(Graphics2D g2d,
                           String text,
                           Rectangle2D rect,
                           Font font,
                           int imgWidth) {

            int tx;

            // if the text is small enough to fit, center it
            if (rect.getWidth() < imgWidth) {
                tx = (int)((imgWidth - rect.getWidth()) / 2);

            // otherwise, if the table is LTR, ensure the left side of
            // the text shows; the right can be clipped
            } else if (table.getComponentOrientation().isLeftToRight()) {
                tx = 0;

            // otherwise, ensure the right side of the text shows
            } else {
                tx = -(int)(Math.ceil(rect.getWidth()) - imgWidth);
            }

            int ty = (int)Math.ceil(Math.abs(rect.getY()));
            g2d.setColor(Color.BLACK);
            g2d.setFont(font);
            
            this.drawString(g2d, text, tx, ty);
    }
    
    protected void drawString(Graphics2D g2d, String text, int x, int y) {
//System.out.println("@["+x+':'+y+"] Drawing: "+text+" "+this.getClass().getName());                        
        g2d.drawString(text, x, y);
    }

    /**
     * Calculate the area of the table to be printed for
     * the next page. This should only be called if there
     * are rows and columns left to print.
     *
     * To avoid an infinite loop in printing, this will
     * always put at least one cell on each page.
     *
     * @param  pw  the width of the area to print in
     * @param  ph  the height of the area to print in
     */
    private void findNextClip(int pw, int ph) {
        final boolean ltr = table.getComponentOrientation().isLeftToRight();

        // if we're ready to start a new set of rows
        if (col == 0) {
            if (ltr) {
                // adjust clip to the left of the first column
                clip.x = 0;
            } else {
                // adjust clip to the right of the first column
                clip.x = totalColWidth;
            }

            // adjust clip to the top of the next set of rows
            clip.y += clip.height;

            // adjust clip width and height to be zero
            clip.width = 0;
            clip.height = 0;

            // fit as many rows as possible, and at least one
            int rowCount = table.getRowCount();
            int rowHeight = table.getRowHeight(row);
            do {
                clip.height += rowHeight;

                if (++row >= rowCount) {
                    break;
                }

                rowHeight = table.getRowHeight(row);
            } while (clip.height + rowHeight <= ph);
        }

        // we can short-circuit for JTable.PrintMode.FIT_WIDTH since
        // we'll always fit all columns on the page
        if (printMode == JTable.PrintMode.FIT_WIDTH) {
            clip.x = 0;
            clip.width = totalColWidth;
            return;
        }

        if (ltr) {
            // adjust clip to the left of the next set of columns
            clip.x += clip.width;
        }

        // adjust clip width to be zero
        clip.width = 0;

        // fit as many columns as possible, and at least one
        int colCount = table.getColumnCount();
        int colWidth = colModel.getColumn(col).getWidth();
        do {
            clip.width += colWidth;
            if (!ltr) {
                clip.x -= colWidth;
            }

            if (++col >= colCount) {
                // reset col to 0 to indicate we're finished all columns
                col = 0;

                break;
            }

            colWidth = colModel.getColumn(col).getWidth();
        } while (clip.width + colWidth <= pw);

    }

}
