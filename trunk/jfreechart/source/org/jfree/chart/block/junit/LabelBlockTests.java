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
 * --------------------
 * LabelBlockTests.java
 * --------------------
 * (C) Copyright 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 01-Sep-2005 : Version 1 (DG);
 *
 */

package org.jfree.chart.block.junit;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.LabelBlock;

/**
 * Some tests for the {@link LabelBlock} class.
 */
public class LabelBlockTests extends TestCase {
    
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(LabelBlockTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public LabelBlockTests(String name) {
        super(name);
    }
    
    /**
     * Confirm that the equals() method can distinguish all the required fields.
     */
    public void testEquals() {
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog", 
                Font.PLAIN, 12), Color.red);
        LabelBlock b2 = new LabelBlock("ABC", new Font("Dialog", 
                Font.PLAIN, 12), Color.red);
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b2));
        
        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.PLAIN, 12), 
                Color.red);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.PLAIN, 12), 
                Color.red);
        assertTrue(b1.equals(b2));

        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12), 
                Color.red);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12), 
                Color.red);
        assertTrue(b1.equals(b2));    

        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12), 
                Color.blue);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12), 
                Color.blue);
        assertTrue(b1.equals(b2));
        
        b1.setToolTipText("Tooltip");
        assertFalse(b1.equals(b2));
        b2.setToolTipText("Tooltip");
        assertTrue(b1.equals(b2));
        
        b1.setURLText("URL");
        assertFalse(b1.equals(b2));
        b2.setURLText("URL");
        assertTrue(b1.equals(b2));
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog", 
                Font.PLAIN, 12), Color.red);
        LabelBlock b2 = null;
        
        try {
            b2 = (LabelBlock) b1.clone();
        }
        catch (CloneNotSupportedException e) {
            fail(e.toString());
        }
        assertTrue(b1 != b2);
        assertTrue(b1.getClass() == b2.getClass());
        assertTrue(b1.equals(b2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog", 
                Font.PLAIN, 12), Color.red);
        LabelBlock b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            b2 = (LabelBlock) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(b1, b2);
    }
   
}
