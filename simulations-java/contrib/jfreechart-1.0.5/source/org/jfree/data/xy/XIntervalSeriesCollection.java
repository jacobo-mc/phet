/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * ------------------------------
 * XIntervalSeriesCollection.java
 * ------------------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XIntervalSeriesCollection.java,v 1.1.2.3 2006/11/27 17:27:52 mungady Exp $
 *
 * Changes
 * -------
 * 20-Oct-2006 : Version 1 (DG);
 * 27-Nov-2006 : Added clone() override (DG);
 *
 */

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.ObjectUtilities;

/**
 * A collection of {@link XIntervalSeries} objects.
 *
 * @since 1.0.3
 *
 * @see XIntervalSeries
 */
public class XIntervalSeriesCollection extends AbstractIntervalXYDataset
                                implements IntervalXYDataset, Serializable {

    /** Storage for the data series. */
    private List data;
    
    /** 
     * Creates a new instance of <code>XIntervalSeriesCollection</code>. 
     */
    public XIntervalSeriesCollection() {
        this.data = new java.util.ArrayList();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} 
     * to all registered listeners.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void addSeries(XIntervalSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series from the collection.
     *
     * @param series  the series index (zero-based).
     *
     * @return The series.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     range <code>0</code> to <code>getSeriesCount() - 1</code>.
     */
    public XIntervalSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (XIntervalSeries) this.data.get(series);
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     *
     * @return The key for a series.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     specified range.
     */
    public Comparable getSeriesKey(int series) {
        // defer argument checking
        return getSeries(series).getKey();
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The item count.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the
     *     range <code>0</code> to <code>getSeriesCount() - 1</code>.
     */
    public int getItemCount(int series) {
        // defer argument checking
        return getSeries(series).getItemCount();
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    public Number getX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return di.getX();
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The y-value.
     */
    public Number getY(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getYValue());
    }

    /**
     * Returns the start x-value for an item within a series.  
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    public Number getStartX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getXLowValue());
    }

    /**
     * Returns the end x-value for an item within a series.  
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    public Number getEndX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getXHighValue());
    }

    /**
     * Returns the start y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start y-value.
     */
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the end y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end y-value.
     */
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean. 
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XIntervalSeriesCollection)) {
            return false;
        }
        XIntervalSeriesCollection that = (XIntervalSeriesCollection) obj;
        return ObjectUtilities.equal(this.data, that.data);
    }
    
    /**
     * Returns a clone of this instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem.
     */
    public Object clone() throws CloneNotSupportedException {
        XIntervalSeriesCollection clone 
                = (XIntervalSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
  
}
