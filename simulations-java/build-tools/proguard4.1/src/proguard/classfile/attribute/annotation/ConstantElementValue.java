/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.classfile.attribute.annotation;

import proguard.classfile.Clazz;
import proguard.classfile.attribute.annotation.visitor.ElementValueVisitor;

/**
 * This ElementValue represents a constant element value.
 *
 * @author Eric Lafortune
 */
public class ConstantElementValue extends ElementValue
{
    public final int u1tag;
    public int u2constantValueIndex;


    /**
     * Creates an uninitialized ConstantElementValue.
     */
    public ConstantElementValue(int u1tag)
    {
        this.u1tag = u1tag;
    }


    // Implementations for ElementValue.

    public int getTag()
    {
        return u1tag;
    }

    public void accept(Clazz clazz, Annotation annotation, ElementValueVisitor elementValueVisitor)
    {
        elementValueVisitor.visitConstantElementValue(clazz, annotation, this);
    }
}
