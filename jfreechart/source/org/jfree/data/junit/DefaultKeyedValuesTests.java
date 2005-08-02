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
 * ----------------------------
 * DefaultKeyedValuesTests.java
 * ----------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 05-Mar-2003 : Version 1 (DG);
 * 27-Aug-2003 : Moved SortOrder from org.jfree.data --> org.jfree.util (DG);
 *
 */

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.DefaultKeyedValues;
import org.jfree.util.SortOrder;

/**
 * Tests for the {@link DefaultKeyedValues} class.
 */
public class DefaultKeyedValuesTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultKeyedValuesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DefaultKeyedValuesTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup required
    }
    
    /**
     * Some checks for the getValue() methods.
     */
    public void testGetValue() {
        DefaultKeyedValues v1 = new DefaultKeyedValues();
        try {
            /* Number n = */ v1.getValue(-1);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            /* Number n = */ v1.getValue(0);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        DefaultKeyedValues v2 = new DefaultKeyedValues();
        v2.addValue("K1", new Integer(1));
        v2.addValue("K2", new Integer(2));
        v2.addValue("K3", new Integer(3));
        assertEquals(new Integer(3), v2.getValue(2));
    }

    /**
     * Some checks for the getKey() methods.
     */
    public void testGetKey() {
        DefaultKeyedValues v1 = new DefaultKeyedValues();
        try {
            /* Comparable k = */ v1.getKey(-1);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            /* Comparable k = */ v1.getKey(0);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        DefaultKeyedValues v2 = new DefaultKeyedValues();
        v2.addValue("K1", new Integer(1));
        v2.addValue("K2", new Integer(2));
        v2.addValue("K3", new Integer(3));
        assertEquals("K2", v2.getKey(1));
    }

    /**
     * Some checks for the getKey() methods.
     */
    public void testGetIndex() {
        DefaultKeyedValues v1 = new DefaultKeyedValues();
        assertEquals(-1, v1.getIndex("K1"));

        DefaultKeyedValues v2 = new DefaultKeyedValues();
        v2.addValue("K1", new Integer(1));
        v2.addValue("K2", new Integer(2));
        v2.addValue("K3", new Integer(3));
        assertEquals(2, v2.getIndex("K3"));
    }
    
    /**
     * Some checks for the clone() method.
     */
    public void testCloning() {
        DefaultKeyedValues v1 = new DefaultKeyedValues();
        v1.addValue("V1", new Integer(1));
        v1.addValue("V2", null);
        v1.addValue("V3", new Integer(3));
        DefaultKeyedValues v2 = null;
        try {
            v2 = (DefaultKeyedValues) v1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(v1 != v2);
        assertTrue(v1.getClass() == v2.getClass());
        assertTrue(v1.equals(v2));
        
        // confirm that the clone is independent of the original
        v2.setValue("V1", new Integer(44));
        assertFalse(v1.equals(v2));
    }
    
    /**
     * Check that inserting and retrieving values works as expected.
     */
    public void testInsertAndRetrieve() {

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("A", new Double(1.0));
        data.addValue("B", new Double(2.0));
        data.addValue("C", new Double(3.0));
        data.addValue("D", null);

        // check key order
        assertEquals(data.getKey(0), "A");
        assertEquals(data.getKey(1), "B");
        assertEquals(data.getKey(2), "C");
        assertEquals(data.getKey(3), "D");

        // check retrieve value by key
        assertEquals(data.getValue("A"), new Double(1.0));
        assertEquals(data.getValue("B"), new Double(2.0));
        assertEquals(data.getValue("C"), new Double(3.0));
        assertEquals(data.getValue("D"), null);

        // check retrieve value by index
        assertEquals(data.getValue(0), new Double(1.0));
        assertEquals(data.getValue(1), new Double(2.0));
        assertEquals(data.getValue(2), new Double(3.0));
        assertEquals(data.getValue(3), null);

    }

    /**
     * Tests sorting of data by key (ascending).
     */
    public void testSortByKeyAscending() {

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("C", new Double(1.0));
        data.addValue("B", null);
        data.addValue("D", new Double(3.0));
        data.addValue("A", new Double(2.0));

        data.sortByKeys(SortOrder.ASCENDING);

        // check key order
        assertEquals(data.getKey(0), "A");
        assertEquals(data.getKey(1), "B");
        assertEquals(data.getKey(2), "C");
        assertEquals(data.getKey(3), "D");

        // check retrieve value by key
        assertEquals(data.getValue("A"), new Double(2.0));
        assertEquals(data.getValue("B"), null);
        assertEquals(data.getValue("C"), new Double(1.0));
        assertEquals(data.getValue("D"), new Double(3.0));

        // check retrieve value by index
        assertEquals(data.getValue(0), new Double(2.0));
        assertEquals(data.getValue(1), null);
        assertEquals(data.getValue(2), new Double(1.0));
        assertEquals(data.getValue(3), new Double(3.0));

    }

    /**
     * Tests sorting of data by key (descending).
     */
    public void testSortByKeyDescending() {

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("C", new Double(1.0));
        data.addValue("B", null);
        data.addValue("D", new Double(3.0));
        data.addValue("A", new Double(2.0));

        data.sortByKeys(SortOrder.DESCENDING);

        // check key order
        assertEquals(data.getKey(0), "D");
        assertEquals(data.getKey(1), "C");
        assertEquals(data.getKey(2), "B");
        assertEquals(data.getKey(3), "A");

        // check retrieve value by key
        assertEquals(data.getValue("A"), new Double(2.0));
        assertEquals(data.getValue("B"), null);
        assertEquals(data.getValue("C"), new Double(1.0));
        assertEquals(data.getValue("D"), new Double(3.0));

        // check retrieve value by index
        assertEquals(data.getValue(0), new Double(3.0));
        assertEquals(data.getValue(1), new Double(1.0));
        assertEquals(data.getValue(2), null);
        assertEquals(data.getValue(3), new Double(2.0));

    }

    /**
     * Tests sorting of data by value (ascending).
     */
    public void testSortByValueAscending() {

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("C", new Double(1.0));
        data.addValue("B", null);
        data.addValue("D", new Double(3.0));
        data.addValue("A", new Double(2.0));

        data.sortByValues(SortOrder.ASCENDING);

        // check key order
        assertEquals(data.getKey(0), "C");
        assertEquals(data.getKey(1), "A");
        assertEquals(data.getKey(2), "D");
        assertEquals(data.getKey(3), "B");

        // check retrieve value by key
        assertEquals(data.getValue("A"), new Double(2.0));
        assertEquals(data.getValue("B"), null);
        assertEquals(data.getValue("C"), new Double(1.0));
        assertEquals(data.getValue("D"), new Double(3.0));

        // check retrieve value by index
        assertEquals(data.getValue(0), new Double(1.0));
        assertEquals(data.getValue(1), new Double(2.0));
        assertEquals(data.getValue(2), new Double(3.0));
        assertEquals(data.getValue(3), null);

    }

    /**
     * Tests sorting of data by key (descending).
     */
    public void testSortByValueDescending() {

        DefaultKeyedValues data = new DefaultKeyedValues();
        data.addValue("C", new Double(1.0));
        data.addValue("B", null);
        data.addValue("D", new Double(3.0));
        data.addValue("A", new Double(2.0));

        data.sortByValues(SortOrder.DESCENDING);

        // check key order
        assertEquals(data.getKey(0), "D");
        assertEquals(data.getKey(1), "A");
        assertEquals(data.getKey(2), "C");
        assertEquals(data.getKey(3), "B");

        // check retrieve value by key
        assertEquals(data.getValue("A"), new Double(2.0));
        assertEquals(data.getValue("B"), null);
        assertEquals(data.getValue("C"), new Double(1.0));
        assertEquals(data.getValue("D"), new Double(3.0));

        // check retrieve value by index
        assertEquals(data.getValue(0), new Double(3.0));
        assertEquals(data.getValue(1), new Double(2.0));
        assertEquals(data.getValue(2), new Double(1.0));
        assertEquals(data.getValue(3), null);

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultKeyedValues v1 = new DefaultKeyedValues();
        v1.addValue("Key 1", new Double(23));
        v1.addValue("Key 2", null);
        v1.addValue("Key 3", new Double(42));

        DefaultKeyedValues v2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(v1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            v2 = (DefaultKeyedValues) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(v1, v2);

    }

}
