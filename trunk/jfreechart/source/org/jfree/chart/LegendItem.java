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
 * LegendItem.java
 * ---------------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Luke Quinane;
 *
 * $Id$
 *
 * Changes (from 2-Oct-2002)
 * -------------------------
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 17-Jan-2003 : Dropped outlineStroke attribute (DG);
 * 08-Oct-2003 : Applied patch for displaying series line style, contributed by
 *               Luke Quinane (DG);
 * 21-Jan-2004 : Added the shapeFilled flag (DG);
 * 04-Jun-2004 : Added equals() method, implemented Serializable (DG);
 * 25-Nov-2004 : Changes required by new LegendTitle implementation (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0 
 *               release (DG);
 * 20-Apr-2005 : Added tooltip and URL text (DG);
 * 
 */

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.ShapeUtilities;

/**
 * A storage object for recording the properties of a legend item, without any 
 * consideration for layout issues.  Instances of this class are immutable.
 */
public class LegendItem implements Serializable {

    // TODO:  keeping this class immutable is becoming a lot of overhead, need 
    // to look at the consequences of dropping immutability

    /** For serialization. */
    private static final long serialVersionUID = -797214582948827144L;
    
    /** The label. */
    private String label;
    
    /** 
     * The description (not currently used - could be displayed as a tool tip). 
     */
    private String description;
    
    /** The tool tip text. */
    private String toolTipText;
    
    /** The url text. */
    private String urlText;

    /** A flag that controls whether or not the shape is visible. */
    private boolean shapeVisible;
    
    /** The shape. */
    private transient Shape shape;
    
    /** A flag that controls whether or not the shape is filled. */
    private boolean shapeFilled;

    /** The paint. */
    private transient Paint fillPaint;
    
    /** A flag that controls whether or not the shape outline is visible. */
    private boolean shapeOutlineVisible;
    
    /** The outline paint. */
    private transient Paint outlinePaint;
    
    /** The outline stroke. */
    private transient Stroke outlineStroke;

    /** A flag that controls whether or not the line is visible. */
    private boolean lineVisible;
    
    /** The line. */
    private transient Shape line;
    
    /** The stroke. */
    private transient Stroke lineStroke;
    
    /** The line paint. */
    private transient Paint linePaint;

    /**
     * The shape must be non-null for a LegendItem - if no shape is required,
     * use this.
     */
    private static final Shape UNUSED_SHAPE = new Line2D.Float();
    
    /**
     * The stroke must be non-null for a LegendItem - if no stroke is required,
     * use this.
     */
    private static final Stroke UNUSED_STROKE = new BasicStroke(0.0f);
    
    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     * 
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shape  the shape (<code>null</code> not permitted).
     * @param fillPaint  the paint used to fill the shape (<code>null</code>
     *                   not permitted).
     */
    public LegendItem(String label, String description, 
                      String toolTipText, String urlText, 
                      Shape shape, Paint fillPaint) {  
        this(
            label, 
            description,
            toolTipText,
            urlText, 
            true,           // shape visible
            shape,
            true,           // shape filled
            fillPaint,
            false,          // shape not outlined
            Color.black,
            UNUSED_STROKE,
            false,          // line not visible
            UNUSED_SHAPE,
            UNUSED_STROKE,
            Color.black
        );
    }
    
    /**
     * Creates a legend item with a filled and outlined shape.
     * 
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shape  the shape (<code>null</code> not permitted).
     * @param fillPaint  the paint used to fill the shape (<code>null</code>
     *                   not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not 
     *                       permitted).
     * @param outlinePaint  the outline paint (<code>null</code> not 
     *                      permitted).
     */
    public LegendItem(String label, String description, 
                      String toolTipText, String urlText, 
                      Shape shape, Paint fillPaint, 
                      Stroke outlineStroke, Paint outlinePaint) {
        this(
            label, 
            description, 
            toolTipText,
            urlText,
            true,           // shape visible
            shape,
            true,           // shape filled
            fillPaint,
            true,           // shape outlined
            outlinePaint,
            outlineStroke,
            false,          // line not visible
            UNUSED_SHAPE,
            UNUSED_STROKE,
            Color.black
        );
    }
    
    /**
     * Creates a legend item using a line.
     * 
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (<code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param line  the line (<code>null</code> not permitted).
     * @param lineStroke  the line stroke (<code>null</code> not permitted).
     * @param linePaint  the line paint (<code>null</code> not permitted).
     */
    public LegendItem(String label, String description, 
                      String toolTipText, String urlText, 
                      Shape line, Stroke lineStroke, Paint linePaint) {
        this(
            label, 
            description, 
            toolTipText,
            urlText,
            false,         // shape not visible
            UNUSED_SHAPE,
            false,         // shape not filled
            Color.black,
            false,         // shape not outlined
            Color.black,
            UNUSED_STROKE,
            true,          // line visible
            line,
            lineStroke,
            linePaint
        );
    }
    
    /**
     * Creates a new legend item.
     *
     * @param label  the label (<code>null</code> not permitted).
     * @param description  the description (not currently used, 
     *        <code>null</code> permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text (<code>null</code> permitted).
     * @param shapeVisible  a flag that controls whether or not the shape is 
     *                      displayed.
     * @param shape  the shape (<code>null</code> permitted).
     * @param shapeFilled  a flag that controls whether or not the shape is 
     *                     filled.
     * @param fillPaint  the fill paint (<code>null</code> not permitted).
     * @param shapeOutlineVisible  a flag that controls whether or not the 
     *                             shape is outlined.
     * @param outlinePaint  the outline paint (<code>null</code> not permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> not 
     *                       permitted).
     * @param lineVisible  a flag that controls whether or not the line is 
     *                     visible.
     * @param line  the line.
     * @param lineStroke  the stroke (<code>null</code> not permitted).
     * @param linePaint  the line paint (<code>null</code> not permitted).
     */
    public LegendItem(String label,
                      String description,
                      String toolTipText,
                      String urlText,
                      boolean shapeVisible,
                      Shape shape,
                      boolean shapeFilled,
                      Paint fillPaint, 
                      boolean shapeOutlineVisible,
                      Paint outlinePaint,
                      Stroke outlineStroke,
                      boolean lineVisible,
                      Shape line,
                      Stroke lineStroke,
                      Paint linePaint) {
        
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");   
        }
        if (fillPaint == null) {
            throw new IllegalArgumentException("Null 'fillPaint' argument.");   
        }
        if (lineStroke == null) {
            throw new IllegalArgumentException("Null 'lineStroke' argument.");
        }
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Null 'outlinePaint' argument.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException(
                "Null 'outlineStroke' argument."
            );   
        }
        this.label = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Returns the label.
     *
     * @return The label (never <code>null</code>).
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the description for the legend item.
     * 
     * @return The description.
     */
    public String getDescription() {
        return this.description;   
    }
    
    /**
     * Returns the tool tip text.
     * 
     * @return The tool tip text (possibly <code>null</code>).
     */
    public String getToolTipText() {
        return this.toolTipText;   
    }
    
    /**
     * Returns the URL text.
     * 
     * @return The URL text (possibly <code>null</code>).
     */
    public String getURLText() {
        return this.urlText; 
    }
    
    /**
     * Returns a flag that indicates whether or not the shape is visible.
     * 
     * @return A boolean.
     */
    public boolean isShapeVisible() {
        return this.shapeVisible;
    }
    
    /**
     * Returns the shape used to label the series represented by this legend 
     * item.
     *
     * @return The shape (never <code>null</code>).
     */
    public Shape getShape() {
        return this.shape;
    }
    
    /**
     * Returns a flag that controls whether or not the shape is filled.
     * 
     * @return A boolean.
     */
    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint (never <code>null</code>).
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Returns the flag that controls whether or not the shape outline
     * is visible.
     * 
     * @return A boolean.
     */
    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }
    
    /**
     * Returns the line stroke for the series.
     *
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getLineStroke() {
        return this.lineStroke;
    }
    
    /**
     * Returns the paint used for lines.
     * 
     * @return The paint.
     */
    public Paint getLinePaint() {
        return this.linePaint;
    }
    
    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never <code>null</code>).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never <code>null</code>).
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }
    
    /**
     * Returns a flag that indicates whether or not the line is visible.
     * 
     * @return A boolean.
     */
    public boolean isLineVisible() {
        return this.lineVisible;
    }
    
    /**
     * Returns the line.
     * 
     * @return The line.
     */
    public Shape getLine() {
        return this.line;
    }
    
    /**
     * Tests this item for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof LegendItem)) {
                return false;
        }
        LegendItem that = (LegendItem) obj;
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.description, that.description)) {
            return false;
        }
        if (this.shapeVisible != that.shapeVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        if (this.shapeFilled != that.shapeFilled) {
            return false;
        }
        if (!this.fillPaint.equals(that.fillPaint)) {
            return false;   
        }
        if (this.shapeOutlineVisible != that.shapeOutlineVisible) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;   
        }
        if (!this.outlinePaint.equals(that.outlinePaint)) {
            return false;   
        }
        if (!this.lineVisible == that.lineVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.line, that.line)) {
            return false;
        }
        if (!this.lineStroke.equals(that.lineStroke)) {
            return false;   
        }
        if (!this.linePaint.equals(that.linePaint)) {
            return false;
        }
        return true;
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream (<code>null</code> not permitted).
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeShape(this.line, stream);
        SerialUtilities.writeStroke(this.lineStroke, stream);
        SerialUtilities.writePaint(this.linePaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream (<code>null</code> not permitted).
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shape = SerialUtilities.readShape(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.line = SerialUtilities.readShape(stream);
        this.lineStroke = SerialUtilities.readStroke(stream);
        this.linePaint = SerialUtilities.readPaint(stream);
    }
    
}
