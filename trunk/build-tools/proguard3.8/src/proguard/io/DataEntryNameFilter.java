/* $Id: DataEntryNameFilter.java,v 1.2 2004/08/15 12:39:30 eric Exp $
 *
 * ProGuard -- shrinking, optimization, and obfuscation of Java class files.
 *
 * Copyright (c) 2002 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.io;

import proguard.util.StringMatcher;

/**
 * This DataEntryFilter filters data entries based on whether their names match
 * a given StringMatcher.
 *
 * @author Eric Lafortune
 */
public class DataEntryNameFilter
implements   DataEntryFilter
{
    private StringMatcher stringMatcher;


    /**
     * Creates a new DataEntryNameFilter.
     * @param stringMatcher the string matcher that will be applied to the names
     *                      of the filtered data entries.
     */
    public DataEntryNameFilter(StringMatcher stringMatcher)
    {
        this.stringMatcher = stringMatcher;
    }


    // Implementations for DataEntryFilter.

    public boolean accepts(DataEntry dataEntry)
    {
        return dataEntry != null && stringMatcher.matches(dataEntry.getName());
    }
}
