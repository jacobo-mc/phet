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

import proguard.classfile.*;
import proguard.classfile.attribute.annotation.visitor.*;

/**
 * This ElementValue represents an annotation element value.
 *
 * @author Eric Lafortune
 */
public class AnnotationElementValue extends ElementValue
{
    public Annotation annotationValue;


    /**
     * Creates an uninitialized AnnotationElementValue.
     */
    public AnnotationElementValue()
    {
    }


    /**
     * Applies the given visitor to the annotation.
     */
    public void annotationAccept(Clazz clazz, AnnotationVisitor annotationVisitor)
    {
        annotationVisitor.visitAnnotation(clazz, annotationValue);
    }


    // Implementations for ElementValue.

    public int getTag()
    {
        return ClassConstants.ELEMENT_VALUE_ANNOTATION;
    }

    public void accept(Clazz clazz, Annotation annotation, ElementValueVisitor elementValueVisitor)
    {
        elementValueVisitor.visitAnnotationElementValue(clazz, annotation, this);
    }
}
