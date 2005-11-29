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
 * ---------------
 * MonthTests.java
 * ---------------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 14-Feb-2002 : Order of parameters in Month(int, int) constructor 
 *               changed (DG);
 * 26-Jun-2002 : Removed unnecessary import (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 21-Oct-2003 : Added hashCode test (DG);
 * 11-Jan-2005 : Added non-clonability test (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.date.MonthConstants;

/**
 * Tests for the {@link Month} class.
 */
public class MonthTests extends TestCase {

    /** A month. */
    private Month jan1900;

    /** A month. */
    private Month feb1900;

    /** A month. */
    private Month nov9999;

    /** A month. */
    private Month dec9999;

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(MonthTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MonthTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        this.jan1900 = new Month(MonthConstants.JANUARY, 1900);
        this.feb1900 = new Month(MonthConstants.FEBRUARY, 1900);
        this.nov9999 = new Month(MonthConstants.NOVEMBER, 9999);
        this.dec9999 = new Month(MonthConstants.DECEMBER, 9999);
    }

    /**
     * Check that a Month instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Month month = new Month();
        assertTrue(month.equals(month));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Month m1 = new Month(MonthConstants.MAY, 2002);
        Month m2 = new Month(MonthConstants.MAY, 2002);
        assertTrue(m1.equals(m2));
    }

    /**
     * In GMT, the end of Feb 2000 is java.util.Date(951,868,799,999L).  Use 
     * this to check the Month constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Month m1 = new Month(new Date(951868799999L), zone);
        Month m2 = new Month(new Date(951868800000L), zone);

        assertEquals(MonthConstants.FEBRUARY, m1.getMonth());
        assertEquals(951868799999L, m1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, m2.getMonth());
        assertEquals(951868800000L, m2.getFirstMillisecond(zone));

    }

    /**
     * In Auckland, the end of Feb 2000 is java.util.Date(951,821,999,999L).
     * Use this to check the Month constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Pacific/Auckland");
        Month m1 = new Month(new Date(951821999999L), zone);
        Month m2 = new Month(new Date(951822000000L), zone);

        assertEquals(MonthConstants.FEBRUARY, m1.getMonth());
        assertEquals(951821999999L, m1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, m2.getMonth());
        assertEquals(951822000000L, m2.getFirstMillisecond(zone));

    }

    /**
     * Set up a month equal to Jan 1900.  Request the previous month, it should
     * be null.
     */
    public void testJan1900Previous() {
        Month previous = (Month) this.jan1900.previous();
        assertNull(previous);
    }

    /**
     * Set up a month equal to Jan 1900.  Request the next month, it should be 
     * Feb 1900.
     */
    public void testJan1900Next() {
        Month next = (Month) this.jan1900.next();
        assertEquals(this.feb1900, next);
    }

    /**
     * Set up a month equal to Dec 9999.  Request the previous month, it should
     * be Nov 9999.
     */
    public void testDec9999Previous() {
        Month previous = (Month) this.dec9999.previous();
        assertEquals(this.nov9999, previous);
    }

    /**
     * Set up a month equal to Dec 9999.  Request the next month, it should be
     * null.
     */
    public void testDec9999Next() {
        Month next = (Month) this.dec9999.next();
        assertNull(next);
    }

    /**
     * Tests the string parsing code...
     */
    public void testParseMonth() {

        Month month = null;

        // test 1...
        try {
            month = Month.parseMonth("1990-01");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(1, month.getMonth());
        assertEquals(1990, month.getYear().getYear());

        // test 2...
        try {
            month = Month.parseMonth("02-1991");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(2, month.getMonth());
        assertEquals(1991, month.getYear().getYear());

        // test 3...
        try {
            month = Month.parseMonth("March 1993");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(3, month.getMonth());
        assertEquals(1993, month.getYear().getYear());

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Month m1 = new Month(12, 1999);
        Month m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (Month) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        Month m1 = new Month(2, 2003);
        Month m2 = new Month(2, 2003);
        assertTrue(m1.equals(m2));
        int h1 = m1.hashCode();
        int h2 = m2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * The {@link Month} class is immutable, so should not be {@link Cloneable}.
     */
    public void testNotCloneable() {
        Month m = new Month(2, 2003);
        assertFalse(m instanceof Cloneable);
    }

}
