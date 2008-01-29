/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
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
package proguard.evaluation.value;

import proguard.classfile.*;
import proguard.classfile.util.ClassUtil;

/**
 * This class provides methods to create and reuse Value objects.
 *
 * @author Eric Lafortune
 */
public class ValueFactory
{
    // Shared copies of Value objects, to avoid creating a lot of objects.
    static final IntegerValue INTEGER_VALUE = new IntegerValue();
    static final LongValue    LONG_VALUE    = new LongValue();
    static final FloatValue   FLOAT_VALUE   = new FloatValue();
    static final DoubleValue  DOUBLE_VALUE  = new DoubleValue();

    static final ReferenceValue REFERENCE_VALUE_NULL                        = new ReferenceValue(null, null, true);
    static final ReferenceValue REFERENCE_VALUE_JAVA_LANG_OBJECT_MAYBE_NULL = new ReferenceValue(ClassConstants.INTERNAL_NAME_JAVA_LANG_OBJECT, null, true);
    static final ReferenceValue REFERENCE_VALUE_JAVA_LANG_OBJECT_NOT_NULL   = new ReferenceValue(ClassConstants.INTERNAL_NAME_JAVA_LANG_OBJECT, null, false);


    /**
     * Creates a new undefined Value of the given type.
     * The type must be a fully specified internal type for primitives, classes,
     * or arrays.
     */
    public Value createValue(String type, Clazz referencedClass, boolean mayBeNull)
    {
        switch (type.charAt(0))
        {
            case ClassConstants.INTERNAL_TYPE_VOID:    return null;
            case ClassConstants.INTERNAL_TYPE_BOOLEAN:
            case ClassConstants.INTERNAL_TYPE_BYTE:
            case ClassConstants.INTERNAL_TYPE_CHAR:
            case ClassConstants.INTERNAL_TYPE_SHORT:
            case ClassConstants.INTERNAL_TYPE_INT:     return INTEGER_VALUE;
            case ClassConstants.INTERNAL_TYPE_LONG:    return LONG_VALUE;
            case ClassConstants.INTERNAL_TYPE_FLOAT:   return FLOAT_VALUE;
            case ClassConstants.INTERNAL_TYPE_DOUBLE:  return DOUBLE_VALUE;
            default:                                   return createReferenceValue(ClassUtil.isInternalArrayType(type) ?
                                                                                       type :
                                                                                       ClassUtil.internalClassNameFromClassType(type),
                                                                                   referencedClass,
                                                                                   mayBeNull);
        }
    }

    /**
     * Creates a new IntegerValue with an undefined value.
     */
    public IntegerValue createIntegerValue()
    {
        return INTEGER_VALUE;
    }

    /**
     * Creates a new IntegerValue with a given specific value.
     */
    public IntegerValue createIntegerValue(int value)
    {
        return createIntegerValue();
    }


    /**
     * Creates a new LongValue with an undefined value.
     */
    public LongValue createLongValue()
    {
        return LONG_VALUE;
    }

    /**
     * Creates a new LongValue with a given specific value.
     */
    public LongValue createLongValue(long value)
    {
        return createLongValue();
    }


    /**
     * Creates a new FloatValue with an undefined value.
     */
    public FloatValue createFloatValue()
    {
        return FLOAT_VALUE;
    }

    /**
     * Creates a new FloatValue with a given specific value.
     */
    public FloatValue createFloatValue(float value)
    {
        return createFloatValue();
    }


    /**
     * Creates a new DoubleValue with an undefined value.
     */
    public DoubleValue createDoubleValue()
    {
        return DOUBLE_VALUE;
    }

    /**
     * Creates a new DoubleValue with a given specific value.
     */
    public DoubleValue createDoubleValue(double value)
    {
        return createDoubleValue();
    }


    /**
     * Creates a new ReferenceValue that represents <code>null</code>.
     */
    public ReferenceValue createReferenceValueNull()
    {
        return REFERENCE_VALUE_NULL;
    }


    /**
     * Creates a new ReferenceValue of the given type. The type must be an
     * internal class name or an array type. If the type is <code>null</code>,
     * the ReferenceValue represents <code>null</code>.
     */
    public ReferenceValue createReferenceValue(String  type,
                                               Clazz   referencedClass,
                                               boolean mayBeNull)
    {
        return type == null                                                ? REFERENCE_VALUE_NULL                                 :
               !type.equals(ClassConstants.INTERNAL_NAME_JAVA_LANG_OBJECT) ? new ReferenceValue(type, referencedClass, mayBeNull) :
               mayBeNull                                                   ? REFERENCE_VALUE_JAVA_LANG_OBJECT_MAYBE_NULL          :
                                                                             REFERENCE_VALUE_JAVA_LANG_OBJECT_NOT_NULL;
    }


    /**
     * Creates a new ReferenceValue for arrays of the given type and length.
     * The type must be a fully specified internal type for primitives, classes,
     * or arrays.
     */
    public ReferenceValue createArrayReferenceValue(String       type,
                                                    Clazz        referencedClass,
                                                    IntegerValue arrayLength)
    {
        return createArrayReferenceValue(type,
                                         referencedClass,
                                         arrayLength,
                                         createValue(type, referencedClass, false));
    }


    /**
     * Creates a new ReferenceValue for arrays of the given type and length,
     * containing the given element. The type must be a fully specified internal
     * type for primitives, classes, or arrays.
     */
    public ReferenceValue createArrayReferenceValue(String       type,
                                                    Clazz        referencedClass,
                                                    IntegerValue arrayLength,
                                                    Value        elementValue)
    {
        return createReferenceValue(ClassConstants.INTERNAL_TYPE_ARRAY + type,
                                    referencedClass,
                                    false);
    }
}
