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
 * --------------------------
 * AbstractSeriesDataset.java
 * --------------------------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 28-Mar-2002 : Implemented SeriesChangeListener interface (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 04-Feb-2003 : Removed redundant methods (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.data.general;

import java.io.Serializable;

/**
 * An abstract implementation of the {@link SeriesDataset} interface, 
 * containing a mechanism for registering change listeners.
 */
public abstract class AbstractSeriesDataset extends AbstractDataset
                                            implements SeriesDataset,
                                                       SeriesChangeListener,
                                                       Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -6074996219705033171L;
    
    /**
     * Creates a new dataset.
     */
    protected AbstractSeriesDataset() {
        super();
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    public abstract int getSeriesCount();

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The series key.
     */
    public abstract Comparable getSeriesKey(int series);
    
    /**
     * Returns the index of the named series, or -1.
     * 
     * @param seriesKey  the series key.
     * 
     * @return The index.
     */
    public int indexOf(Comparable seriesKey) {
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
           if (getSeriesKey(s).equals(seriesKey)) {
               return s;
           }
        }
        return -1;
    }
    
    /**
     * Called when a series belonging to the dataset changes.
     *
     * @param event  information about the change.
     */
    public void seriesChanged(SeriesChangeEvent event) {
        fireDatasetChanged();
    }

}
