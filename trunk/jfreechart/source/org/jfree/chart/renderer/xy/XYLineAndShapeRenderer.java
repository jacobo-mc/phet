/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------------
 * XYLineAndShapeRenderer.java
 * ---------------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 27-Jan-2004 : Version 1 (DG);
 * 10-Feb-2004 : Minor change to drawItem() method to make cut-and-paste 
 *               overriding easier (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 25-Aug-2004 : Added support for chart entities (required for tooltips) (DG);
 * 24-Sep-2004 : Added flag to allow whole series to be drawn as a path 
 *               (necessary when using a dashed stroke with many data 
 *               items) (DG);
 * 04-Oct-2004 : Renamed BooleanUtils --> BooleanUtilities (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities to translate shapes (DG);
 * 27-Jan-2005 : The getLegendItem() method now omits hidden series (DG);
 * 28-Jan-2005 : Added new constructor (DG);
 * 09-Mar-2005 : Added fillPaint settings (DG);
 * 20-Apr-2005 : Use generators for legend tooltips and URLs (DG);
 * 22-Jul-2005 : Renamed defaultLinesVisible --> baseLinesVisible, 
 *               defaultShapesVisible --> baseShapesVisible and
 *               defaultShapesFilled --> baseShapesFilled (DG);
 * 29-Jul-2005 : Added code to draw item labels (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that can be used with the {@link XYPlot} class.
 */
public class XYLineAndShapeRenderer extends AbstractXYItemRenderer 
                                    implements XYItemRenderer, 
                                               Cloneable,
                                               PublicCloneable,
                                               Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -7435246895986425885L;
    
    /** A flag that controls whether or not lines are visible for ALL series. */
    private Boolean linesVisible;

    /** 
     * A table of flags that control (per series) whether or not lines are 
     * visible. 
     */
    private BooleanList seriesLinesVisible;

    /** The default value returned by the getLinesVisible() method. */
    private boolean baseLinesVisible;

    /** The shape that is used to represent a line in the legend. */
    private transient Shape legendLine;
    
    /** 
     * A flag that controls whether or not shapes are visible for ALL series. 
     */
    private Boolean shapesVisible;

    /** 
     * A table of flags that control (per series) whether or not shapes are 
     * visible. 
     */
    private BooleanList seriesShapesVisible;

    /** The default value returned by the getShapeVisible() method. */
    private boolean baseShapesVisible;

    /** A flag that controls whether or not shapes are filled for ALL series. */
    private Boolean shapesFilled;

    /** 
     * A table of flags that control (per series) whether or not shapes are 
     * filled. 
     */
    private BooleanList seriesShapesFilled;

    /** The default value returned by the getShapeFilled() method. */
    private boolean baseShapesFilled;
    
    /** A flag that controls whether outlines are drawn for shapes. */
    private boolean drawOutlines;
    
    /** 
     * A flag that controls whether the fill paint is used for filling 
     * shapes. 
     */
    private boolean useFillPaint;
    
    /** 
     * A flag that controls whether the outline paint is used for drawing shape 
     * outlines. 
     */
    private boolean useOutlinePaint;
    
    /** 
     * A flag that controls whether or not each series is drawn as a single 
     * path. 
     */
    private boolean drawSeriesLineAsPath;

    /**
     * Creates a new renderer with both lines and shapes visible.
     */
    public XYLineAndShapeRenderer() {
        this(true, true);
    }
    
    /**
     * Creates a new renderer.
     * 
     * @param lines  lines visible?
     * @param shapes  shapes visible?
     */
    public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
        this.linesVisible = null;
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        
        this.shapesFilled = null;
        this.useFillPaint = false;     // use item paint for fills by default
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;

        this.drawOutlines = true;     
        this.useOutlinePaint = false;  // use item paint for outlines by 
                                       // default, not outline paint
        
        this.drawSeriesLineAsPath = false;
    }
    
    /**
     * Returns a flag that controls whether or not each series is drawn as a 
     * single path.
     * 
     * @return A boolean.
     * 
     * @see #setDrawSeriesLineAsPath(boolean)
     */
    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }
    
    /**
     * Sets the flag that controls whether or not each series is drawn as a 
     * single path.
     * 
     * @param flag  the flag.
     * 
     * @see #getDrawSeriesLineAsPath()
     */
    public void setDrawSeriesLineAsPath(boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    /**
     * Returns the number of passes through the data that the renderer requires 
     * in order to draw the chart.  Most charts will require a single pass, but 
     * some require two passes.
     * 
     * @return The pass count.
     */
    public int getPassCount() {
        return 2;
    }
    
    // LINES VISIBLE

    /**
     * Returns the flag used to control whether or not the shape for an item is 
     * visible.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseLinesVisible;   
        }
    }

    /**
     * Returns a flag that controls whether or not lines are drawn for ALL 
     * series.  If this flag is <code>null</code>, then the "per series" 
     * settings will apply.
     * 
     * @return A flag (possibly <code>null</code>).
     */
    public Boolean getLinesVisible() {
        return this.linesVisible;   
    }
    
    /**
     * Sets a flag that controls whether or not lines are drawn between the 
     * items in ALL series, and sends a {@link RendererChangeEvent} to all 
     * registered listeners.  You need to set this to <code>null</code> if you 
     * want the "per series" settings to apply.
     *
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setLinesVisible(Boolean visible) {
        this.linesVisible = visible;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets a flag that controls whether or not lines are drawn between the 
     * items in ALL series, and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     *
     * @param visible  the flag.
     */
    public void setLinesVisible(boolean visible) {
        setLinesVisible(BooleanUtilities.valueOf(visible));
    }

    /**
     * Returns the flag used to control whether or not the lines for a series 
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    /**
     * Sets the 'lines visible' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag (<code>null</code> permitted).
     */
    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the 'lines visible' flag for a series.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     */
    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    /**
     * Returns the base 'lines visible' attribute.
     *
     * @return The base flag.
     */
    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }

    /**
     * Sets the base 'lines visible' flag.
     *
     * @param flag  the flag.
     */
    public void setBaseLinesVisible(boolean flag) {
        this.baseLinesVisible = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the shape used to represent a line in the legend.
     * 
     * @return The legend line (never <code>null</code>).
     */
    public Shape getLegendLine() {
        return this.legendLine;   
    }
    
    /**
     * Sets the shape used as a line in each legend item and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param line  the line (<code>null</code> not permitted).
     */
    public void setLegendLine(Shape line) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");   
        }
        this.legendLine = line;
        notifyListeners(new RendererChangeEvent(this));
    }

    // SHAPES VISIBLE

    /**
     * Returns the flag used to control whether or not the shape for an item is
     * visible.
     * <p>
     * The default implementation passes control to the 
     * <code>getSeriesShapesVisible</code> method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();   
        }
        else {
            return this.baseShapesVisible;
        }
    }

    /**
     * Returns the flag that controls whether the shapes are visible for the 
     * items in ALL series.
     * 
     * @return The flag (possibly <code>null</code>).
     */
    public Boolean getShapesVisible() {
        return this.shapesVisible;    
    }
    
    /**
     * Sets the 'shapes visible' for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag (<code>null</code> permitted).
     */
    public void setShapesVisible(Boolean visible) {
        this.shapesVisible = visible;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the 'shapes visible' for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param visible  the flag.
     */
    public void setShapesVisible(boolean visible) {
        setShapesVisible(BooleanUtilities.valueOf(visible));
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    /**
     * Sets the 'shapes visible' flag for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     */
    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    /**
     * Sets the 'shapes visible' flag for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     */
    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base 'shape visible' attribute.
     *
     * @return The base flag.
     */
    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    /**
     * Sets the base 'shapes visible' flag.
     *
     * @param flag  the flag.
     */
    public void setBaseShapesVisible(boolean flag) {
        this.baseShapesVisible = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    // SHAPES FILLED

    /**
     * Returns the flag used to control whether or not the shape for an item 
     * is filled.
     * <p>
     * The default implementation passes control to the 
     * <code>getSeriesShapesFilled</code> method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        Boolean flag = this.shapesFilled;
        if (flag == null) {
            flag = getSeriesShapesFilled(series);
        }
        if (flag != null) {
            return flag.booleanValue();   
        }
        else {
            return this.baseShapesFilled;   
        }
    }

    /**
     * Sets the 'shapes filled' for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param filled  the flag.
     */
    public void setShapesFilled(boolean filled) {
        setShapesFilled(BooleanUtilities.valueOf(filled));
    }

    /**
     * Sets the 'shapes filled' for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param filled  the flag (<code>null</code> permitted).
     */
    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the flag used to control whether or not the shapes for a series
     * are filled.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }

    /**
     * Sets the 'shapes filled' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     */
    public void setSeriesShapesFilled(int series, boolean flag) {
        setSeriesShapesFilled(series, BooleanUtilities.valueOf(flag));
    }

    /**
     * Sets the 'shapes filled' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     */
    public void setSeriesShapesFilled(int series, Boolean flag) {
        this.seriesShapesFilled.setBoolean(series, flag);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base 'shape filled' attribute.
     *
     * @return The base flag.
     */
    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    /**
     * Sets the base 'shapes filled' flag.
     *
     * @param flag  the flag.
     */
    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns <code>true</code> if outlines should be drawn for shapes, and 
     * <code>false</code> otherwise.
     * 
     * @return A boolean.
     */
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }
    
    /**
     * Sets the flag that controls whether outlines are drawn for 
     * shapes, and sends a {@link RendererChangeEvent} to all registered 
     * listeners. 
     * <P>
     * In some cases, shapes look better if they do NOT have an outline, but 
     * this flag allows you to set your own preference.
     * 
     * @param flag  the flag.
     */
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns <code>true</code> if the renderer should use the fill paint 
     * setting to fill shapes, and <code>false</code> if it should just
     * use the regular paint.
     * 
     * @return A boolean.
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }
    
    /**
     * Sets the flag that controls whether the fill paint is used to fill 
     * shapes, and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     * 
     * @param flag  the flag.
     */
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns <code>true</code> if the renderer should use the outline paint 
     * setting to draw shape outlines, and <code>false</code> if it should just
     * use the regular paint.
     * 
     * @return A boolean.
     */
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }
    
    /**
     * Sets the flag that controls whether the outline paint is used to draw 
     * shape outlines, and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     * 
     * @param flag  the flag.
     */
    public void setUseOutlinePaint(boolean flag) {
        this.useOutlinePaint = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Records the state for the renderer.  This is used to preserve state 
     * information between calls to the drawItem() method for a single chart 
     * drawing.
     */
    public static class State extends XYItemRendererState {
        
        /** The path for the current series. */
        public GeneralPath seriesPath;
        
        /** 
         * A flag that indicates if the last (x, y) point was 'good' 
         * (non-null). 
         */
        private boolean lastPointGood;
        
        /**
         * Creates a new state instance.
         * 
         * @param info  the plot rendering info.
         */
        public State(PlotRenderingInfo info) {
            super(info);
        }
        
        /**
         * Returns a flag that indicates if the last point drawn (in the 
         * current series) was 'good' (non-null).
         * 
         * @return A boolean.
         */
        public boolean isLastPointGood() {
            return this.lastPointGood;
        }
        
        /**
         * Sets a flag that indicates if the last point drawn (in the current 
         * series) was 'good' (non-null).
         * 
         * @param good  the flag.
         */
        public void setLastPointGood(boolean good) {
            this.lastPointGood = good;
        }
    }
    
    /**
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to 
     * maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return The renderer state.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        State state = new State(info);
        state.seriesPath = new GeneralPath();
        return state;

    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;   
        }

        // first pass draws the background (lines, for instance)
        if (isLinePass(pass)) {
            if (item == 0) {
                if (this.drawSeriesLineAsPath) {
                    State s = (State) state;
                    s.seriesPath.reset();
                    s.lastPointGood = false;     
                }
            }

            if (getItemLineVisible(series, item)) {
                if (this.drawSeriesLineAsPath) {
                    drawPrimaryLineAsPath(
                        state, g2, plot, dataset, pass, series, item, 
                        domainAxis, rangeAxis, dataArea
                    );
                }
                else {
                    drawPrimaryLine(
                        state, g2, plot, dataset, pass, series, item, 
                        domainAxis, rangeAxis, dataArea
                    );
                }
            }
        }
        // second pass adds shapes where the items are ..
        else if (isItemPass(pass)) {

            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }

            drawSecondaryPass(
                g2, plot, dataset, pass, series, item, domainAxis, dataArea,
                rangeAxis, crosshairState, entities
            );
        }
    }

    /**
     * Returns <code>true</code> if the specified pass is the one for drawing 
     * lines.
     * 
     * @param pass  the pass.
     * 
     * @return A boolean.
     */
    protected boolean isLinePass(int pass) {
        return pass == 0;
    }

    /**
     * Returns <code>true</code> if the specified pass is the one for drawing 
     * items.
     * 
     * @param pass  the pass.
     * 
     * @return A boolean.
     */
    protected boolean isItemPass(int pass) {
        return pass == 1;
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     */
    protected void drawPrimaryLine(XYItemRendererState state,
                                   Graphics2D g2,
                                   XYPlot plot,
                                   XYDataset dataset,
                                   int pass,
                                   int series,
                                   int item,
                                   ValueAxis domainAxis,
                                   ValueAxis rangeAxis,
                                   Rectangle2D dataArea) {
        if (item == 0) {
            return;
        }

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }

        double x0 = dataset.getXValue(series, item - 1);
        double y0 = dataset.getYValue(series, item - 1);
        if (Double.isNaN(y0) || Double.isNaN(x0)) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);

        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        // only draw if we have good values
        if (Double.isNaN(transX0) || Double.isNaN(transY0)
            || Double.isNaN(transX1) || Double.isNaN(transY1)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY1, transX1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX1, transY1);
        }

        if (state.workingLine.intersects(dataArea)) {
            drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }

    /**
     * Draws the first pass shape.
     * 
     * @param g2  the graphics device.
     * @param pass  the pass.
     * @param series  the series index.
     * @param item  the item index.
     * @param shape  the shape.
     */
    protected void drawFirstPassShape(Graphics2D g2,
                                      int pass,
                                      int series,
                                      int item,
                                      Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(getItemPaint(series, item));
        g2.draw(shape);
    }


    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items. Instead of drawing separate lines,
     * a GeneralPath is constructed and drawn at the end of
     * the series painting.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plot  the plot (can be used to obtain standard color information 
     *              etc).
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataArea  the area within which the data is being drawn.
     */
    protected void drawPrimaryLineAsPath(XYItemRendererState state,
                                         Graphics2D g2, XYPlot plot,
                                         XYDataset dataset,
                                         int pass,
                                         int series,
                                         int item,
                                         ValueAxis domainAxis,
                                         ValueAxis rangeAxis,
                                         Rectangle2D dataArea) {


        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        State s = (State) state;
        // update path to reflect latest point
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x = (float) transX1;
            float y = (float) transY1;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x = (float) transY1;
                y = (float) transX1;
            }
            if (s.isLastPointGood()) {
                s.seriesPath.lineTo(x, y);
            }
            else {
                s.seriesPath.moveTo(x, y);
            }
            s.setLastPointGood(true);
        }
        else {
            s.setLastPointGood(false);
        }
        // if this is the last item, draw the path ...
        if (item == dataset.getItemCount(series) - 1) {
            // draw path
            drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }

    /**
     * Draws the item shapes and adds chart entities (second pass). This method 
     * draws the shapes which mark the item positions. If <code>entities</code> 
     * is not <code>null</code> it will be populated with entity information.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  the crosshair state.
     * @param entities the entity collection.
     */
    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot, 
                                     XYDataset dataset,
                                     int pass, int series, int item,
                                     ValueAxis domainAxis, 
                                     Rectangle2D dataArea,
                                     ValueAxis rangeAxis, 
                                     CrosshairState crosshairState,
                                     EntityCollection entities) {

        Shape entityArea = null;

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        if (getItemShapeVisible(series, item)) {
            Shape shape = getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(
                    shape, transY1, transX1
                );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(
                    shape, transX1, transY1
                );
            }
            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (getItemShapeFilled(series, item)) {
                    if (this.useFillPaint) {
                        g2.setPaint(getItemFillPaint(series, item));
                    }
                    else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item));
                    }
                    else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
            }
        }

        // draw the item label if there is one...
        if (isItemLabelVisible(series, item)) {
            double xx = transX1;
            double yy = transY1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                xx = transY1;
                yy = transX1;
            }          
            drawItemLabel(g2, orientation, dataset, series, item, xx, yy, 
                    (y1 < 0.0));
        }

        updateCrosshairValues(
            crosshairState, x1, y1, transX1, transY1, plot.getOrientation()
        );

        // add an entity for the item...
        if (entities != null) {
            addEntity(
                entities, entityArea, dataset, series, item, transX1, transY1
            );
        }
    }


    /**
     * Returns a legend item for the specified series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }

        LegendItem result = null;
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null) {
            if (getItemVisible(series, 0)) {
                String label = getLegendItemLabelGenerator().generateLabel(
                    dataset, series
                );
                String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(
                        dataset, series
                    );
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(
                        dataset, series
                    );
                }
                boolean shapeIsVisible = getItemShapeVisible(series, 0);
                Shape shape = getSeriesShape(series);
                boolean shapeIsFilled = getItemShapeFilled(series, 0);
                Paint fillPaint = (this.useFillPaint 
                    ? getSeriesFillPaint(series) : getSeriesPaint(series));
                boolean shapeOutlineVisible = this.drawOutlines;  
                Paint outlinePaint = (this.useOutlinePaint 
                    ? getSeriesOutlinePaint(series) 
                    : getSeriesPaint(series));
                Stroke outlineStroke = getSeriesOutlineStroke(series);
                boolean lineVisible = getItemLineVisible(series, 0);
                Stroke lineStroke = getSeriesStroke(series);
                Paint linePaint = getSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, 
                        urlText, shapeIsVisible, shape, shapeIsFilled, 
                        fillPaint, shapeOutlineVisible, outlinePaint, 
                        outlineStroke, lineVisible, this.legendLine, 
                        lineStroke, linePaint);
            }
        }

        return result;

    }
    
    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if the clone cannot be created.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYLineAndShapeRenderer that = (XYLineAndShapeRenderer) obj;
        if (!ObjectUtilities.equal(this.linesVisible, that.linesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.seriesLinesVisible, that.seriesLinesVisible)
        ) {
            return false;
        }
        if (this.baseLinesVisible != that.baseLinesVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.shapesVisible, that.shapesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.seriesShapesVisible, that.seriesShapesVisible)
        ) {
            return false;
        }
        if (this.baseShapesVisible != that.baseShapesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesFilled, that.shapesFilled)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.seriesShapesFilled, that.seriesShapesFilled)
        ) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }

        return true;

    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtilities.readShape(stream);
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendLine, stream);
    }
  
}
