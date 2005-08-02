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
 * -------------
 * DateTick.java
 * -------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 07-Nov-2003 : Version 1 (DG);
 * 13-May-2004 : Added equals() method (DG);
 *
 */

package org.jfree.chart.axis;

import java.util.Date;

import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

/**
 * A tick used by the {@link DateAxis} class.
 */
public class DateTick extends ValueTick {

    /** The date. */
    private Date date;
    
    /**
     * Creates a new date tick.
     * 
     * @param date  the date.
     * @param label  the label.
     * @param textAnchor  the part of the label that is aligned to the anchor 
     *                    point.
     * @param rotationAnchor  defines the rotation point relative to the text.
     * @param angle  the rotation angle (in radians).
     */
    public DateTick(Date date, String label,
                    TextAnchor textAnchor, TextAnchor rotationAnchor, 
                    double angle) {
                        
        super(date.getTime(), label, textAnchor, rotationAnchor, angle);
        this.date = date;
            
    }
    
    /**
     * Returns the date.
     * 
     * @return The date.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Tests this tick for equality with an arbitrary object.
     * 
     * @param obj  the object to test (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof DateTick && super.equals(obj)) {
            DateTick dt = (DateTick) obj;
            if (!ObjectUtilities.equal(this.date, dt.date)) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
    /**
     * Returns a hash code for this object.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        return this.date.hashCode();
    }
    
}
