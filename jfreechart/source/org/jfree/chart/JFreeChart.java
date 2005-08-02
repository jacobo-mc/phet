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
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ---------------
 * JFreeChart.java
 * ---------------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Christian W. Zuckschwerdt;
 *                   Klaus Rheinwald;
 *                   Nicolas Brodu;
 *
 * $Id$
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend 
 *               placement;
 * 21-Jun-2001 : Removed JFreeChart parameter from Plot constructors (DG);
 * 22-Jun-2001 : Multiple titles added (original code by David Berry, with 
 *               reworkings by DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Moved data source classes into new package 
 *               com.jrefinery.data.* (DG);
 * 18-Oct-2001 : New factory method for creating VerticalXYBarChart (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods to the Plot class (DG);
 *               Moved static chart creation methods to new ChartFactory 
 *               class (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *               Fixed bug where chart isn't registered with the dataset (DG);
 * 07-Nov-2001 : Fixed bug where null title in constructor causes 
 *               exception (DG);
 *               Tidied up event notification code (DG);
 * 17-Nov-2001 : Added getLegendItemCount() method (DG);
 * 21-Nov-2001 : Set clipping in draw method to ensure that nothing gets drawn 
 *               outside the chart area (DG);
 * 11-Dec-2001 : Added the createBufferedImage() method, taken from the 
 *               JFreeChartServletDemo class (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added handleClick() method (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 05-Feb-2002 : Removed redundant tooltips code (DG);
 * 19-Feb-2002 : Added accessor methods for the backgroundImage and 
 *               backgroundImageAlpha attributes (DG);
 * 21-Feb-2002 : Added static fields for INFO, COPYRIGHT, LICENCE, CONTRIBUTORS
 *               and LIBRARIES.  These can be used to display information about
 *               JFreeChart (DG);
 * 06-Mar-2002 : Moved constants to JFreeChartConstants interface (DG);
 * 18-Apr-2002 : PieDataset is no longer sorted (oldman);
 * 23-Apr-2002 : Moved dataset to the Plot class (DG);
 * 13-Jun-2002 : Added an extra draw() method (DG);
 * 25-Jun-2002 : Implemented the Drawable interface and removed redundant 
 *               imports (DG);
 * 26-Jun-2002 : Added another createBufferedImage() method (DG);
 * 18-Sep-2002 : Fixed issues reported by Checkstyle (DG);
 * 23-Sep-2002 : Added new contributor (DG);
 * 28-Oct-2002 : Created main title and subtitle list to replace existing title
 *               list (DG);
 * 08-Jan-2003 : Added contributor (DG);
 * 17-Jan-2003 : Added new constructor (DG);
 * 22-Jan-2003 : Added ChartColor class by Cameron Riley, and background image 
 *               alignment code by Christian W. Zuckschwerdt (DG);
 * 11-Feb-2003 : Added flag to allow suppression of chart change events, based 
 *               on a suggestion by Klaus Rheinwald (DG);
 * 04-Mar-2003 : Added small fix for suppressed chart change events (see bug id
 *               690865) (DG);
 * 10-Mar-2003 : Added Benoit Xhenseval to contributors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 15-Jul-2003 : Added an optional border for the chart (DG);
 * 11-Sep-2003 : Took care of listeners while cloning (NB);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 22-Sep-2003 : Added nullpointer checks.
 * 25-Sep-2003 : Added nullpointer checks too (NB).
 * 03-Dec-2003 : Legends are now registered by this class instead of using the 
 *               old constructor way (TM);
 * 03-Dec-2003 : Added anchorPoint to draw() method (DG);
 * 08-Jan-2004 : Reworked title code, introducing line wrapping (DG);
 * 09-Feb-2004 : Created additional createBufferedImage() method (DG);
 * 05-Apr-2004 : Added new createBufferedImage() method (DG);
 * 27-May-2004 : Moved constants from JFreeChartConstants.java back to this 
 *               class (DG);
 * 25-Nov-2004 : Updates for changes to Title class (DG);
 * 06-Jan-2005 : Change lookup for default background color (DG);
 * 31-Jan-2005 : Added Don Elliott to contributors (DG);
 * 02-Feb-2005 : Added clearSubtitles() method (DG);
 * 03-Feb-2005 : Added Mofeed Shahin to contributors (DG);
 * 08-Feb-2005 : Updated for RectangleConstraint changes (DG);
 * 28-Mar-2005 : Renamed Legend --> OldLegend (DG);
 * 12-Apr-2005 : Added methods to access legend(s) in subtitle list (DG);
 * 13-Apr-2005 : Added removeLegend() and removeSubtitle() methods (DG);
 * 20-Apr-2005 : Modified to collect chart entities from titles and 
 *               subtitles (DG);
 * 26-Apr-2005 : Removed LOGGER (DG);
 *
 */

package org.jfree.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import org.jfree.JCommon;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.LegendChangeEvent;
import org.jfree.chart.event.LegendChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.Drawable;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.about.Contributor;
import org.jfree.ui.about.Licences;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.util.ObjectUtilities;

/**
 * A chart class implemented using the Java 2D APIs.  The current version
 * supports bar charts, line charts, pie charts and xy plots (including time
 * series data).
 * <P>
 * JFreeChart coordinates several objects to achieve its aim of being able to
 * draw a chart on a Java 2D graphics device: a list of {@link Title} objects, 
 * a {@link OldLegend}, a {@link Plot} and a 
 * {@link org.jfree.data.general.Dataset} (the plot in turn manages a 
 * horizontal axis and a vertical axis).
 * <P>
 * You should use a {@link ChartPanel} to display a chart in a GUI.
 * <P>
 * The {@link ChartFactory} class contains static methods for creating 
 * 'ready-made' charts.
 *
 * @see ChartPanel
 * @see ChartFactory
 * @see Title
 * @see OldLegend
 * @see Plot
 *
 */
public class JFreeChart implements Drawable,
                                   TitleChangeListener,
                                   LegendChangeListener,
                                   PlotChangeListener,
                                   Serializable,
                                   Cloneable {

    /** For serialization. */    
    private static final long serialVersionUID = -3470703747817429120L;
    
    /** Information about the project. */
    public static final ProjectInfo INFO = new JFreeChartInfo();

    /** The default font for titles. */
    public static final Font DEFAULT_TITLE_FONT 
        = new Font("SansSerif", Font.BOLD, 18);

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT 
        = UIManager.getColor("Panel.background");

    /** The default background image. */
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    /** The default background image alignment. */
    public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = Align.FIT;

    /** The default background image alpha. */
    public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

    /** Rendering hints that will be used for chart drawing. */
    private transient RenderingHints renderingHints;

    /** A flag that controls whether or not the chart border is drawn. */
    private boolean borderVisible;

    /** The stroke used to draw the chart border (if visible). */
    private transient Stroke borderStroke;

    /** The paint used to draw the chart border (if visible). */
    private transient Paint borderPaint;

    /** The chart title (optional). */
    private TextTitle title;

    /** The chart subtitles (zero, one or many). */
    private List subtitles;

    /** The chart legend. */
    private OldLegend oldLegend;

    /** Draws the visual representation of the data. */
    private Plot plot;

    /** Paint used to draw the background of the chart. */
    private transient Paint backgroundPaint;

    /** An optional background image for the chart. */
    private transient Image backgroundImage;  // todo: not serialized yet

    /** The alignment for the background image. */
    private int backgroundImageAlignment = Align.FIT;

    /** The alpha transparency for the background image. */
    private float backgroundImageAlpha = 0.5f;

    /** Storage for registered change listeners. */
    private transient EventListenerList changeListeners;

    /** Storage for registered progress listeners. */
    private transient EventListenerList progressListeners;

    /** 
     * A flag that can be used to enable/disable notification of chart change 
     * events. 
     */
    private boolean notify;
    
    /**
     * Creates a new chart based on the supplied plot.  The chart will have
     * a legend added automatically, but no title (although you can easily add
     * one later).  
     * <br><br>
     * Note that the  {@link ChartFactory} class contains a range 
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param plot  the plot (<code>null</code> not permitted).
     */
    public JFreeChart(Plot plot) {

        this(
            null, // title
            null, // font
            plot,
            true // create legend
        );

    }

    /**
     * Creates a new chart with the given title and plot.  A default font 
     * (@link DEFAULT_TITLE_FONT) is used for the title, and the chart will 
     * have a legend added automatically.  
     * <br><br>
     * Note that the  {@link ChartFactory} class contains a range 
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param plot  the plot (<code>null</code> not permitted).
     */
    public JFreeChart(String title, Plot plot) {
        this(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    /**
     * Creates a new chart with the given title and plot.  The 
     * <code>createLegend</code> argument specifies whether or not a legend
     * should be added to the chart.  
     * <br><br>
     * Note that the  {@link ChartFactory} class contains a range 
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param titleFont  the font for displaying the chart title 
     *                   (<code>null</code> permitted).
     * @param plot  controller of the visual representation of the data 
     *              (<code>null</code> not permitted).
     * @param createLegend  a flag indicating whether or not a legend should   
     *                      be created for the chart.
     */
    public JFreeChart(String title, Font titleFont, Plot plot, 
                      boolean createLegend) {

        if (plot == null) {
            throw new NullPointerException("Null 'plot' argument.");
        }

        // create storage for listeners...
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.notify = true;  // default is to notify listeners when the 
                             // chart changes

        this.renderingHints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        this.borderVisible = false;
        this.borderStroke = new BasicStroke(1.0f);
        this.borderPaint = Color.black;

        this.plot = plot;
        plot.addChangeListener(this);

        this.subtitles = new ArrayList();

        // create a legend, if requested...
        if (createLegend) {
            LegendTitle legend = new LegendTitle(this.plot);
            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
            legend.setBorder(new BlockBorder());
            legend.setBackgroundPaint(Color.white);
            legend.setPosition(RectangleEdge.BOTTOM);
            this.subtitles.add(legend);
        }

        // add the chart title, if one has been specified...
        if (title != null) {
            if (titleFont == null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            this.title = new TextTitle(title, titleFont);
            this.title.addChangeListener(this);
        }

        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;

        this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;

    }

    /**
     * Returns the collection of rendering hints for the chart.
     *
     * @return The rendering hints for the chart (never <code>null</code>).
     */
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    /**
     * Sets the rendering hints for the chart.  These will be added (using the 
     * Graphics2D.addRenderingHints() method) near the start of the 
     * JFreeChart.draw() method.
     *
     * @param renderingHints  the rendering hints (<code>null</code> not 
     *                        permitted).
     */
    public void setRenderingHints(RenderingHints renderingHints) {
        if (renderingHints == null) {
            throw new NullPointerException("RenderingHints given are null");
        }
        this.renderingHints = renderingHints;
        fireChartChanged();
    }

    /**
     * Returns a flag that controls whether or not a border is drawn around the
     * outside of the chart.
     *
     * @return A boolean.
     */
    public boolean isBorderVisible() {
        return this.borderVisible;
    }

    /**
     * Sets a flag that controls whether or not a border is drawn around the 
     * outside of the chart.
     *
     * @param visible  the flag.
     */
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        fireChartChanged();
    }

    /**
     * Returns the stroke used to draw the chart border (if visible).
     *
     * @return The border stroke.
     */
    public Stroke getBorderStroke() {
        return this.borderStroke;
    }

    /**
     * Sets the stroke used to draw the chart border (if visible).
     *
     * @param stroke  the stroke.
     */
    public void setBorderStroke(Stroke stroke) {
        this.borderStroke = stroke;
        fireChartChanged();
    }

    /**
     * Returns the paint used to draw the chart border (if visible).
     *
     * @return The border paint.
     */
    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    /**
     * Sets the paint used to draw the chart border (if visible).
     *
     * @param paint  the paint.
     */
    public void setBorderPaint(Paint paint) {
        this.borderPaint = paint;
        fireChartChanged();
    }

    /**
     * Returns the main chart title.  Very often a chart will have just one
     * title, so we make this case simple by providing accessor methods for
     * the main title.  However, multiple titles are supported - see the
     * {@link #addSubtitle(Title)} method.
     *
     * @return The chart title (possibly <code>null</code>).
     */
    public TextTitle getTitle() {
        return this.title;
    }

    /**
     * Sets the main title for the chart and sends a {@link ChartChangeEvent} 
     * to all registered listeners.  If you do not want a title for the 
     * chart, set it to <code>null</code>.  If you want more than one title on
     * a chart, use the {@link #addSubtitle(Title)} method.
     *
     * @param title  the title (<code>null</code> permitted).
     */
    public void setTitle(TextTitle title) {
        this.title = title;
        fireChartChanged();
    }

    /**
     * Sets the chart title and sends a {@link ChartChangeEvent} to all 
     * registered listeners.  This is a convenience method that ends up calling 
     * the {@link #setTitle(TextTitle)} method.  If there is an existing title,
     * its text is updated, otherwise a new title using the default font is 
     * added to the chart.  If <code>text</code> is <code>null</code> the chart
     * title is set to <code>null</code>.
     *
     * @param text  the title text (<code>null</code> permitted).
     */
    public void setTitle(String text) {
        if (text != null) {
            if (this.title == null) {
                setTitle(new TextTitle(text, JFreeChart.DEFAULT_TITLE_FONT));
            }
            else {
                this.title.setText(text);
            }
        }
        else {
            setTitle((TextTitle) null);
        }
    }

    /**
     * Returns the legend for the chart, if there is one.  Note that a chart
     * can have more than one legend - this method returns the first.
     * 
     * @return The legend (possibly <code>null</code>).
     */
    public LegendTitle getLegend() {
        return getLegend(0);
    }
    
    /**
     * Returns the nth legend for a chart, or <code>null</code>.
     * 
     * @param index  the legend index (zero-based).
     * 
     * @return The legend (possibly <code>null</code>).
     */
    public LegendTitle getLegend(int index) {
        int seen = 0;
        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title subtitle = (Title) iterator.next();
            if (subtitle instanceof LegendTitle) {
                if (seen == index) {
                    return (LegendTitle) subtitle;
                }
                else {
                    seen++;   
                }
            }
        }
        return null;        
    }
    
    /**
     * Removes the first legend in the chart and sends a 
     * {@link ChartChangeEvent} to all registered listeners.
     */
    public void removeLegend() {
        removeSubtitle(getLegend());
    }
    
    /**
     * Returns the list of subtitles for the chart.
     *
     * @return The subtitle list (possibly empty, but never <code>null</code>).
     */
    public List getSubtitles() {
        return this.subtitles;
    }

    /**
     * Sets the title list for the chart (completely replaces any existing 
     * titles).
     *
     * @param subtitles  the new list of subtitles (<code>null</code> not 
     *                   permitted).
     */
    public void setSubtitles(List subtitles) {
        if (subtitles == null) {
            throw new NullPointerException("Null 'subtitles' argument.");
        }
        this.subtitles = subtitles;
        fireChartChanged();
    }

    /**
     * Returns the number of titles for the chart.
     *
     * @return The number of titles for the chart.
     */
    public int getSubtitleCount() {
        return this.subtitles.size();
    }

    /**
     * Returns a chart subtitle.
     *
     * @param index  the index of the chart subtitle (zero based).
     *
     * @return A chart subtitle.
     */
    public Title getSubtitle(int index) {
        if ((index < 0) || (index == getSubtitleCount())) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return (Title) this.subtitles.get(index);
    }

    /**
     * Adds a chart subtitle, and notifies registered listeners that the chart 
     * has been modified.
     *
     * @param subtitle  the subtitle (<code>null</code> not permitted).
     */
    public void addSubtitle(Title subtitle) {
        if (subtitle == null) {
            throw new IllegalArgumentException("Null 'subtitle' argument.");
        }
        this.subtitles.add(subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }
    
    /**
     * Clears all subtitles from the chart and sends a {@link ChartChangeEvent}
     * to all registered listeners.
     */
    public void clearSubtitles() {
        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title t = (Title) iterator.next();
            t.removeChangeListener(this);
        }
        this.subtitles.clear();
        fireChartChanged();
    }

    /**
     * Removes the specified subtitle and sends a {@link ChartChangeEvent} to
     * all registered listeners.
     * 
     * @param title  the title.
     */
    public void removeSubtitle(Title title) {
        this.subtitles.remove(title);
        fireChartChanged();
    }
    
    /**
     * Returns the chart legend.
     *
     * @return The chart legend (possibly <code>null</code>).
     */
    public OldLegend getOldLegend() {
        return this.oldLegend;
    }

    /**
     * Sets the chart legend.  Registered listeners are notified that the chart
     * has been modified. The legends chart reference is updated.
     *
     * @param legend  the new chart legend (<code>null</code> permitted).
     */
    public void setOldLegend(OldLegend legend) {

        // if there is an existing legend, remove the chart from the list of
        // change listeners...
        OldLegend existing = this.oldLegend;
        if (existing != null) {
            existing.removeChangeListener(this);
            existing.registerChart(null);
        }

        // set the new legend, and register the chart as a change listener...
        this.oldLegend = legend;
        if (legend != null) {
            legend.registerChart(this);
            legend.addChangeListener(this);
        }

        // notify chart change listeners...
        fireChartChanged();

    }

    /**
     * Returns the plot for the chart.  The plot is a class responsible for
     * coordinating the visual representation of the data, including the axes
     * (if any).
     *
     * @return The plot.
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns the plot cast as a {@link CategoryPlot}.
     * <p>
     * NOTE: if the plot is not an instance of {@link CategoryPlot}, then a
     * <code>ClassCastException</code> is thrown.
     *
     * @return The plot.
     */
    public CategoryPlot getCategoryPlot() {
        return (CategoryPlot) this.plot;
    }

    /**
     * Returns the plot cast as an {@link XYPlot}.
     * <p>
     * NOTE: if the plot is not an instance of {@link XYPlot}, then a
     * <code>ClassCastException</code> is thrown.
     *
     * @return The plot.
     */
    public XYPlot getXYPlot() {
        return (XYPlot) this.plot;
    }

    /**
     * Returns a flag that indicates whether or not anti-aliasing is used when
     * the chart is drawn.
     *
     * @return The flag.
     */
    public boolean getAntiAlias() {
        Object o = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
        if (o == null) {
            return false;
        }
        return (o.equals(RenderingHints.VALUE_ANTIALIAS_ON));
    }

    /**
     * Sets a flag that indicates whether or not anti-aliasing is used when the
     * chart is drawn.
     * <P>
     * Anti-aliasing usually improves the appearance of charts, but is slower.
     *
     * @param flag  the new value of the flag.
     */
    public void setAntiAlias(boolean flag) {

        Object o = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
        if (o == null) {
            o = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
        }
        if (!flag && RenderingHints.VALUE_ANTIALIAS_OFF.equals(o) 
            || flag && RenderingHints.VALUE_ANTIALIAS_ON.equals(o)) {
            // no change, do nothing
            return;
        }
        if (flag) {
            this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else {
            this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        fireChartChanged();

    }

    /**
     * Returns the paint used for the chart background.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the paint used to fill the chart background and sends a 
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setBackgroundPaint(Paint paint) {

        if (this.backgroundPaint != null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }
        else {
            if (paint != null) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the background image for the chart, or <code>null</code> if 
     * there is no image.
     *
     * @return The image (possibly <code>null</code>).
     */
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    /**
     * Sets the background image for the chart and sends a 
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param image  the image (<code>null</code> permitted).
     */
    public void setBackgroundImage(Image image) {

        if (this.backgroundImage != null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }
        else {
            if (image != null) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the background image alignment. Alignment constants are defined 
     * in the <code>org.jfree.ui.Align</code> class in the JCommon class 
     * library.
     *
     * @return The alignment.
     */
    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    /**
     * Sets the background alignment.  Alignment options are defined by the 
     * {@link org.jfree.ui.Align} class.
     *
     * @param alignment  the alignment.
     */
    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChartChanged();
        }
    }

    /**
     * Returns the alpha-transparency for the chart's background image.
     *
     * @return The alpha-transparency.
     */
    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    /**
     * Sets the alpha-transparency for the chart's background image.
     * Registered listeners are notified that the chart has been changed.
     *
     * @param alpha  the alpha value.
     */
    public void setBackgroundImageAlpha(float alpha) {

        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }

    }

    /**
     * Returns a flag that controls whether or not change events are sent to 
     * registered listeners.
     *
     * @return A boolean.
     */
    public boolean isNotify() {
        return this.notify;
    }

    /**
     * Sets a flag that controls whether or not listeners receive 
     * {@link ChartChangeEvent} notifications.
     *
     * @param notify  a boolean.
     */
    public void setNotify(boolean notify) {
        this.notify = notify;
        // if the flag is being set to true, there may be queued up changes...
        if (notify) {
            notifyListeners(new ChartChangeEvent(this));
        }
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the chart should be drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null, null);
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).  This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the chart should be drawn.
     * @param info  records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
        draw(g2, area, null, info);
    }
    
    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param chartArea  the area within which the chart should be drawn.
     * @param anchor  the anchor point (in Java2D space) for the chart 
     *                (<code>null</code> permitted).
     * @param info  records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, 
                     Rectangle2D chartArea, Point2D anchor, 
                     ChartRenderingInfo info) {

        notifyListeners(
            new ChartProgressEvent(
                this, this, ChartProgressEvent.DRAWING_STARTED, 0
            )
        );

        // record the chart area, if info is requested...
        if (info != null) {
            info.clear();
            info.setChartArea(chartArea);
        }

        // ensure no drawing occurs outside chart area...
        Shape savedClip = g2.getClip();
        g2.clip(chartArea);

        g2.addRenderingHints(this.renderingHints);

        // draw the chart background...
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(chartArea);
        }

        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(
                AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 
                    this.backgroundImageAlpha
                )
            );
            Rectangle2D dest = new Rectangle2D.Double(
                0.0, 0.0, this.backgroundImage.getWidth(null), 
                this.backgroundImage.getHeight(null)
            );
            Align.align(dest, chartArea, this.backgroundImageAlignment);
            g2.drawImage(
                this.backgroundImage, (int) dest.getX(), (int) dest.getY(),
                (int) dest.getWidth(), (int) dest.getHeight(), null
            );
            g2.setComposite(originalComposite);
        }

        if (isBorderVisible()) {
            Paint paint = getBorderPaint();
            Stroke stroke = getBorderStroke();
            if (paint != null && stroke != null) {
                Rectangle2D borderArea = new Rectangle2D.Double(
                    chartArea.getX(), chartArea.getY(),
                    chartArea.getWidth() - 1.0, chartArea.getHeight() - 1.0
                );
                g2.setPaint(paint);
                g2.setStroke(stroke);
                g2.draw(borderArea);
            }
        }

        // draw the title and subtitles...
        Rectangle2D nonTitleArea = new Rectangle2D.Double();
        nonTitleArea.setRect(chartArea);

        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();   
        }
        if (this.title != null) {
            EntityCollection e = drawTitle(
                this.title, g2, nonTitleArea, (entities != null)
            );
            if (e != null) {
                entities.addAll(e);   
            }
        }

        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title currentTitle = (Title) iterator.next();
            EntityCollection e = drawTitle(
                currentTitle, g2, nonTitleArea, (entities != null)
            );
            if (e != null) {
                entities.addAll(e);   
            }
        }

        // draw the legend - the draw method will return the remaining area
        // after the legend steals a chunk of the non-title area for itself
        Rectangle2D plotArea = nonTitleArea;
        if (this.oldLegend != null) {
            plotArea.setRect(this.oldLegend.draw(g2, nonTitleArea, info));
        }

        // draw the plot (axes and data visualisation)
        PlotRenderingInfo plotInfo = null;
        if (info != null) {
            plotInfo = info.getPlotInfo();
        }
        this.plot.draw(g2, plotArea, anchor, null, plotInfo);

        g2.setClip(savedClip);

        notifyListeners(
            new ChartProgressEvent(
                this, this, ChartProgressEvent.DRAWING_FINISHED, 100
            )
        );
    }

    /**
     * Creates a rectangle that is aligned to the frame.
     * 
     * @param dimensions
     * @param frame
     * @param hAlign
     * @param vAlign
     * 
     * @return A rectangle.
     */
    private Rectangle2D createAlignedRectangle2D(Size2D dimensions, 
            Rectangle2D frame, HorizontalAlignment hAlign, 
            VerticalAlignment vAlign) {
        double x = Double.NaN;
        double y = Double.NaN;
        if (hAlign == HorizontalAlignment.LEFT) {
            x = frame.getX();   
        }
        else if (hAlign == HorizontalAlignment.CENTER) {
            x = frame.getCenterX() - (dimensions.width / 2.0);   
        }
        else if (hAlign == HorizontalAlignment.RIGHT) {
            x = frame.getMaxX() - dimensions.width;   
        }
        if (vAlign == VerticalAlignment.TOP) {
            y = frame.getY();   
        }
        else if (vAlign == VerticalAlignment.CENTER) {
            y = frame.getCenterY() - (dimensions.height / 2.0);   
        }
        else if (vAlign == VerticalAlignment.BOTTOM) {
            y = frame.getMaxY() - dimensions.height;   
        }
        
        return new Rectangle2D.Double(
            x, y, dimensions.width, dimensions.height
        );
    }
    
    /**
     * Draws a title.  The title should be drawn at the top, bottom, left or 
     * right of the specified area, and the area should be updated to reflect 
     * the amount of space used by the title.
     *
     * @param t  the title (<code>null</code> not permitted).
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param area  the chart area, excluding any existing titles 
     *              (<code>null</code> not permitted).
     * @param entities  a flag that controls whether or not an entity 
     *                  collection is returned for the title.
     * 
     * @return An entity collection for the title (possibly <code>null</code>).
     */
    protected EntityCollection drawTitle(Title t, Graphics2D g2, 
                                         Rectangle2D area, boolean entities) {

        if (t == null) {
            throw new IllegalArgumentException("Null 't' argument.");   
        }
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");   
        }
        Rectangle2D titleArea = new Rectangle2D.Double();
        RectangleEdge position = t.getPosition();
        double ww = area.getWidth();
        double hh = area.getHeight();
        RectangleConstraint constraint = new RectangleConstraint(
            ww, new Range(0.0, ww), LengthConstraintType.RANGE,
            hh, new Range(0.0, hh), LengthConstraintType.RANGE
        );
        Object retValue = null;
        BlockParams p = new BlockParams();
        p.setGenerateEntities(entities);
        if (position == RectangleEdge.TOP) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(
                size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP
            );
            retValue = t.draw(g2, titleArea, p);
            area.setRect(
                area.getX(), 
                Math.min(area.getY() + size.height, area.getMaxY()),
                area.getWidth(), Math.max(area.getHeight() - size.height, 0)
            );
        }
        else if (position == RectangleEdge.BOTTOM) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(
                size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM
            );
            retValue = t.draw(g2, titleArea, p);
            area.setRect(
                area.getX(), area.getY(), 
                area.getWidth(), area.getHeight() - size.height
            );
        }
        else if (position == RectangleEdge.RIGHT) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(
                size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment()
            );
            retValue = t.draw(g2, titleArea, p);
            area.setRect(
                area.getX(), area.getY(), 
                area.getWidth() - size.width, area.getHeight()
            );
        }

        else if (position == RectangleEdge.LEFT) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(
                size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment()
            );
            retValue = t.draw(g2, titleArea, p);
            area.setRect(
                area.getX() + size.width, area.getY(),
                area.getWidth() - size.width, area.getHeight()
            );
        }
        else {
            throw new RuntimeException("Unrecognised title position.");
        }
        EntityCollection result = null;
        if (retValue instanceof EntityBlockResult) {
            EntityBlockResult ebr = (EntityBlockResult) retValue;
            result = ebr.getEntityCollection();
        }
        return result;   
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height) {
        return createBufferedImage(width, height, null);
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     * @param info  carries back chart state information (<code>null</code> 
     *              permitted).
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height, 
                                             ChartRenderingInfo info) {
        return createBufferedImage(
            width, height, BufferedImage.TYPE_INT_RGB, info
        );
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     * @param imageType  the image type.
     * @param info  carries back chart state information (<code>null</code> 
     *              permitted).
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height, 
                                             int imageType, 
                                             ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics2D g2 = image.createGraphics();
        draw(g2, new Rectangle2D.Double(0, 0, width, height), null, info);
        g2.dispose();
        return image;
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param imageWidth  the image width.
     * @param imageHeight  the image height.
     * @param drawWidth  the width for drawing the chart (will be scaled to 
     *                   fit image).
     * @param drawHeight  the height for drawing the chart (will be scaled to 
     *                    fit image).
     * @param info  optional object for collection chart dimension and entity 
     *              information.
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int imageWidth, 
                                             int imageHeight,
                                             double drawWidth, 
                                             double drawHeight, 
                                             ChartRenderingInfo info) {

        BufferedImage image = new BufferedImage(
            imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2 = image.createGraphics();
        double scaleX = imageWidth / drawWidth;
        double scaleY = imageHeight / drawHeight;
        AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
        g2.transform(st);
        draw(
            g2, new Rectangle2D.Double(0, 0, drawWidth, drawHeight), null, info
        );
        g2.dispose();
        return image;

    }

    /**
     * Handles a 'click' on the chart.
     * <P>
     * JFreeChart is not a UI component, so some other object (e.g. ChartPanel)
     * needs to capture the click event and pass it onto the JFreeChart object.
     * If you are not using JFreeChart in a client application, then this
     * method is not required (and hopefully it doesn't get in the way).
     *
     * @param x  x-coordinate of the click (in Java2D space).
     * @param y  y-coordinate of the click (in Java2D space).
     * @param info  contains chart dimension and entity information.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // pass the click on to the plot...
        // rely on the plot to post a plot change event and redraw the chart...
        this.plot.handleClick(x, y, info.getPlotInfo());

    }

    /**
     * Registers an object for notification of changes to the chart.
     *
     * @param listener  the listener (<code>null</code> not permitted).
     */
    public void addChangeListener(ChartChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.add(ChartChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the listener (<code>null</code> not permitted)
     */
    public void removeChangeListener(ChartChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.remove(ChartChangeListener.class, listener);
    }

    /**
     * Sends a default {@link ChartChangeEvent} to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    public void fireChartChanged() {
        ChartChangeEvent event = new ChartChangeEvent(this);
        notifyListeners(event);
    }

    /**
     * Sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the 
     *               notification.
     */
    protected void notifyListeners(ChartChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.changeListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChartChangeListener.class) {
                    ((ChartChangeListener) listeners[i + 1]).chartChanged(
                        event
                    );
                }
            }
        }
    }

    /**
     * Registers an object for notification of progress events relating to the 
     * chart.
     *
     * @param listener  the object being registered.
     */
    public void addProgressListener(ChartProgressListener listener) {
        this.progressListeners.add(ChartProgressListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the object being deregistered.
     */
    public void removeProgressListener(ChartProgressListener listener) {
        this.progressListeners.remove(ChartProgressListener.class, listener);
    }

    /**
     * Sends a {@link ChartProgressEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the 
     *               notification.
     */
    protected void notifyListeners(ChartProgressEvent event) {

        Object[] listeners = this.progressListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChartProgressListener.class) {
                ((ChartProgressListener) listeners[i + 1]).chartProgress(event);
            }
        }

    }

    /**
     * Receives notification that a chart title has changed, and passes this
     * on to registered listeners.
     *
     * @param event  information about the chart title change.
     */
    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the chart legend has changed, and passes this
     * on to registered listeners.
     *
     * @param event  information about the chart legend change.
     */
    public void legendChanged(LegendChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the plot has changed, and passes this on to
     * registered listeners.
     *
     * @param event  information about the plot change.
     */
    public void plotChanged(PlotChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Tests this chart for equality with another object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof JFreeChart)) {
            return false;
        }

        JFreeChart that = (JFreeChart) obj;
        if (!ObjectUtilities.equal(this.title, that.title)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.subtitles, that.subtitles)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.oldLegend, that.oldLegend)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.plot, that.plot)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.backgroundPaint, that.backgroundPaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.backgroundImage, that.backgroundImage
        )) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }

        return true;

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
        SerialUtilities.writeStroke(this.borderStroke, stream);
        SerialUtilities.writePaint(this.borderPaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
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
        this.borderStroke = SerialUtilities.readStroke(stream);
        this.borderPaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.renderingHints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );

        // register as a listener with sub-components...
        if (this.title != null) {
            this.title.addChangeListener(this);
        }

        for (int i = 0; i < getSubtitleCount(); i++) {
            getSubtitle(i).addChangeListener(this);
        }

        if (this.oldLegend != null) {
            this.oldLegend.addChangeListener(this);
        }

        //if (this.plot != null) {
        // the plot can never be null.
        this.plot.addChangeListener(this);
        //}

    }

    /**
     * Prints information about JFreeChart to standard output.
     *
     * @param args  no arguments are honored.
     */
    public static void main(String[] args) {
        System.out.println(JFreeChart.INFO.toString());
    }

    /**
     * Clones the object, and takes care of listeners.
     * Note: caller shall register its own listeners on cloned graph.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if the chart is not cloneable.
     */
    public Object clone() throws CloneNotSupportedException {
        JFreeChart chart = (JFreeChart) super.clone();

        chart.renderingHints = (RenderingHints) this.renderingHints.clone();
        // private boolean borderVisible;
        // private transient Stroke borderStroke;
        // private transient Paint borderPaint;

        if (this.title != null) {
            chart.title = (TextTitle) this.title.clone();
            chart.title.addChangeListener(chart);
        }

        chart.subtitles = new ArrayList();
        for (int i = 0; i < getSubtitleCount(); i++) {
            Title subtitle = (Title) getSubtitle(i).clone();
            chart.subtitles.add(subtitle);
            subtitle.addChangeListener(chart);
        }

        if (this.oldLegend != null) {
            chart.oldLegend = (OldLegend) this.oldLegend.clone();
            chart.oldLegend.registerChart(chart);
            chart.oldLegend.addChangeListener(chart);
        }

        if (this.plot != null) {
            chart.plot = (Plot) this.plot.clone();
            chart.plot.addChangeListener(chart);
        }

        //private boolean antialias;
        //private transient Paint backgroundPaint;
        //private transient Image backgroundImage;  // todo: not serialized yet
        //private int backgroundImageAlignment = Align.FIT;
        //private float backgroundImageAlpha = 0.5f;

        chart.progressListeners = new EventListenerList();
        chart.changeListeners = new EventListenerList();
        //private boolean notify;

        return chart;
    }

}

/**
 * Information about the JFreeChart project.  One instance of this class is 
 * assigned to <code>JFreeChart.INFO<code>.
 */
class JFreeChartInfo extends ProjectInfo {

    /** 
     * Default constructor. 
     */
    public JFreeChartInfo() {

        // get a locale-specific resource bundle...
        String baseResourceClass 
            = "org.jfree.chart.resources.JFreeChartResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseResourceClass);

        setName(resources.getString("project.name"));
        setVersion(resources.getString("project.version"));
        setInfo(resources.getString("project.info"));
        setCopyright(resources.getString("project.copyright"));
        setLogo(null);  // load only when required
        setLicenceName("LGPL");
        setLicenceText(Licences.getInstance().getLGPL());

        setContributors(Arrays.asList(
            new Contributor[]{
                new Contributor("Eric Alexander", "-"),
                new Contributor(
                    "Richard Atkinson", "richard_c_atkinson@ntlworld.com"
                ),
                new Contributor("David Basten", "-"),
                new Contributor("David Berry", "-"),
                new Contributor("Anthony Boulestreau", "-"),
                new Contributor("Jeremy Bowman", "-"),
                new Contributor("Nicolas Brodu", "-"),
                new Contributor("David Browning", "-"),
                new Contributor("S???ren Caspersen", "-"),
                new Contributor("Chuanhao Chiu", "-"),
                new Contributor("Brian Cole", "-"),
                new Contributor("Pascal Collet", "-"),
                new Contributor("Martin Cordova", "-"),
                new Contributor("Paolo Cova", "-"),
                new Contributor("Mike Duffy", "-"),
                new Contributor("Don Elliott", "-"),
                new Contributor("Jonathan Gabbai", "-"),
                new Contributor(
                    "David Gilbert", "david.gilbert@object-refinery.com"
                ),
                new Contributor("Serge V. Grachov", "-"),
                new Contributor("Daniel Gredler", "-"),
                new Contributor("Hans-Jurgen Greiner", "-"),
                new Contributor("Joao Guilherme Del Valle", "-"),
                new Contributor("Aiman Han", "-"),
                new Contributor("Cameron Hayne", "-"),
                new Contributor("Jon Iles", "-"),
                new Contributor("Wolfgang Irler", "-"),
                new Contributor("Xun Kang", "-"),
                new Contributor("Bill Kelemen", "-"),
                new Contributor("Norbert Kiesel", "-"),
                new Contributor("Gideon Krause", "-"),
                new Contributor("Arnaud Lelievre", "-"),
                new Contributor("Wolfgang Lenhard", "-"),
                new Contributor("David Li", "-"),
                new Contributor("Tin Luu", "-"),
                new Contributor("Craig MacFarlane", "-"),
                new Contributor("Achilleus Mantzios", "-"),
                new Contributor("Thomas Meier", "-"),
                new Contributor("Jim Moore", "-"),
                new Contributor("Jonathan Nash", "-"),
                new Contributor("Barak Naveh", "-"),
                new Contributor("David M. O'Donnell", "-"),
                new Contributor("Krzysztof Paz", "-"),
                new Contributor("Tomer Peretz", "-"),
                new Contributor("Andrzej Porebski", "-"),
                new Contributor("Xavier Poinsard", "-"),
                new Contributor("Viktor Rajewski", "-"),
                new Contributor("Eduardo Ramalho", "-"),
                new Contributor("Michael Rauch", "-"),
                new Contributor("Cameron Riley", "-"),
                new Contributor("Dan Rivett", "d.rivett@ukonline.co.uk"),
                new Contributor("Scott Sams", "-"),
                new Contributor("Michel Santos", "-"),
                new Contributor("Thierry Saura", "-"),
                new Contributor("Andreas Schneider", "-"),
                new Contributor("Jean-Luc SCHWAB", "-"),
                new Contributor("Bryan Scott", "-"),
                new Contributor("Tobias Selb", "-"),
                new Contributor("Mofeed Shahin", "-"),
                new Contributor("Greg Steckman", "-"),
                new Contributor("Roger Studner", "-"),
                new Contributor("Irv Thomae", "-"),
                new Contributor("Eric Thomas", "-"),
                new Contributor("Rich Unger", "-"),
                new Contributor("Daniel van Enckevort", "-"),
                new Contributor("Laurence Vanhelsuwe", "-"),
                new Contributor("Sylvain Vieujot", "-"),
                new Contributor("Mark Watson", "www.markwatson.com"),
                new Contributor("Alex Weber", "-"),
                new Contributor("Matthew Wright", "-"),
                new Contributor("Benoit Xhenseval", "-"),
                new Contributor(
                    "Christian W. Zuckschwerdt", 
                    "Christian.Zuckschwerdt@Informatik.Uni-Oldenburg.de"
                ),
                new Contributor("Hari", "-"),
                new Contributor("Sam (oldman)", "-"),
            }
        ));

        addLibrary(JCommon.INFO);

    }

    /**
     * Returns the JFreeChart logo (a picture of a gorilla).
     *
     * @return The JFreeChart logo.
     */
    public Image getLogo() {

        Image logo = super.getLogo();
        if (logo == null) {
            URL imageURL = ClassLoader.getSystemResource(
                "org/jfree/chart/gorilla.jpg"
            );
            if (imageURL != null) {
                ImageIcon temp = new ImageIcon(imageURL);  
                    // use ImageIcon because it waits for the image to load...
                logo = temp.getImage();
                setLogo(logo);
            }
        }
        return logo;

    }

}
