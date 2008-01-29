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
package proguard.evaluation;

import proguard.classfile.*;
import proguard.classfile.attribute.CodeAttribute;
import proguard.classfile.constant.*;
import proguard.classfile.constant.visitor.ConstantVisitor;
import proguard.classfile.instruction.*;
import proguard.classfile.util.*;
import proguard.classfile.visitor.MemberVisitor;
import proguard.evaluation.value.*;

/**
 * This InvocationUnit sets up the variables for entering a method,
 * and it updates the stack for the invocation of a class member,
 * using simple values.
 *
 * @author Eric Lafortune
 */
public class BasicInvocationUnit
extends      SimplifiedVisitor
implements   InvocationUnit,
             ConstantVisitor,
             MemberVisitor
{
    private final ValueFactory valueFactory = new ValueFactory();

    // Fields acting as parameters between the visitor methods.
    private boolean isStatic;
    private boolean isLoad;
    private Stack   stack;
    private Clazz   returnTypeClass;


    /**
     * Sets up the given variables for entering the given method.
     */
    public void enterMethod(Clazz clazz, Method method, Variables variables)
    {
        String descriptor = method.getDescriptor(clazz);

        // Initialize the parameters.
        boolean isStatic =
            (method.getAccessFlags() & ClassConstants.INTERNAL_ACC_STATIC) != 0;

        // Count the number of parameters, taking into account their categories.
        int parameterSize = ClassUtil.internalMethodParameterSize(descriptor);
        if (!isStatic)
        {
            parameterSize++;
        }

        // Reuse the existing parameters object, ensuring the right size.
        variables.reset(parameterSize);

        // Go over the parameters again.
        InternalTypeEnumeration internalTypeEnumeration =
            new InternalTypeEnumeration(descriptor);

        int parameterIndex = 0;
        int variableIndex  = 0;

        // Put the 'this' reference in variable 0.
        if (!isStatic)
        {
            // Get the reference value.
            Value value = getMethodParameterValue(clazz,
                                                  method,
                                                  parameterIndex++,
                                                  ClassUtil.internalTypeFromClassName(clazz.getName()),
                                                  clazz);

            // Store the value in variable 0.
            variables.store(variableIndex++, value);
        }

        Clazz[] referencedClasses = ((ProgramMethod)method).referencedClasses;
        int referencedClassIndex = 0;

        // Set up the variables corresponding to the parameter types and values.
        while (internalTypeEnumeration.hasMoreTypes())
        {
            String type = internalTypeEnumeration.nextType();

            Clazz referencedClass = referencedClasses != null &&
                                    ClassUtil.isInternalClassType(type) ?
                referencedClasses[referencedClassIndex++] :
                null;

            // Get the parameter value.
            Value value = getMethodParameterValue(clazz,
                                                  method,
                                                  parameterIndex++,
                                                  type,
                                                  referencedClass);

            // Store the value in the corresponding variable.
            variables.store(variableIndex++, value);

            // Increment the variable index again for Category 2 values.
            if (value.isCategory2())
            {
                variableIndex++;
            }
        }
    }


    /**
     * Exits the given method with the given return value.
     */
    public void exitMethod(Clazz clazz, Method method, Value returnValue)
    {
        setMethodReturnValue(clazz, method, returnValue);
    }


    /**
     * Updates the given stack corresponding to the execution of the given
     * field or method reference instruction.
     */
    public void invokeMember(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, ConstantInstruction constantInstruction, Stack stack)
    {
        int constantIndex = constantInstruction.constantIndex;

        switch (constantInstruction.opcode)
        {
            case InstructionConstants.OP_GETSTATIC:
                isStatic = true;
                isLoad   = true;
                break;

            case InstructionConstants.OP_PUTSTATIC:
                isStatic = true;
                isLoad   = false;
                break;

            case InstructionConstants.OP_GETFIELD:
                isStatic = false;
                isLoad   = true;
                break;

            case InstructionConstants.OP_PUTFIELD:
                isStatic = false;
                isLoad   = false;
                break;

            case InstructionConstants.OP_INVOKESTATIC:
                isStatic = true;
                break;

            case InstructionConstants.OP_INVOKEVIRTUAL:
            case InstructionConstants.OP_INVOKESPECIAL:
            case InstructionConstants.OP_INVOKEINTERFACE:
                isStatic = false;
                break;
        }

        // Pop the parameters and push the return value.
        this.stack = stack;
        clazz.constantPoolEntryAccept(constantIndex, this);
        this.stack = null;
    }


    // Implementations for ConstantVisitor.

    public void visitFieldrefConstant(Clazz clazz, FieldrefConstant fieldrefConstant)
    {
        // Pop the field value, if applicable.
        if (!isLoad)
        {
            setFieldValue(clazz, fieldrefConstant, stack.pop());
        }

        // Pop the reference value, if applicable.
        if (!isStatic)
        {
            setFieldClassValue(clazz, fieldrefConstant, stack.apop());
        }

        // Push the field value, if applicable.
        if (isLoad)
        {
            String type = fieldrefConstant.getType(clazz);

            stack.push(getFieldValue(clazz, fieldrefConstant, type));
        }
    }


    public void visitAnyMethodrefConstant(Clazz clazz, RefConstant methodrefConstant)
    {
        String type = methodrefConstant.getType(clazz);

        // Count the number of parameters.
        int parameterCount = ClassUtil.internalMethodParameterCount(type);
        if (!isStatic)
        {
            parameterCount++;
        }

        // Pop the parameters and the class reference, in reverse order.
        for (int parameterIndex = parameterCount-1; parameterIndex >= 0; parameterIndex--)
        {
            setMethodParameterValue(clazz, methodrefConstant, parameterIndex, stack.pop());
        }

        // Pop the reference value, if applicable.
        // Push the return value, if applicable.
        String returnType = ClassUtil.internalMethodReturnType(type);
        if (returnType.charAt(0) != ClassConstants.INTERNAL_TYPE_VOID)
        {
            stack.push(getMethodReturnValue(clazz, methodrefConstant, returnType));
        }
    }


    protected void setFieldClassValue(Clazz          clazz,
                                      RefConstant    refConstant,
                                      ReferenceValue value)
    {
        // We don't care about the new value.
    }


    protected Value getFieldClassValue(Clazz       clazz,
                                       RefConstant refConstant,
                                       String      type)
    {
        // Try to figure out the class of the return type.
        returnTypeClass = null;
        refConstant.referencedMemberAccept(this);

        return valueFactory.createValue(type,
                                        returnTypeClass,
                                        true);
    }


    protected void setFieldValue(Clazz       clazz,
                                 RefConstant refConstant,
                                 Value       value)
    {
        // We don't care about the new field value.
    }


    protected Value getFieldValue(Clazz       clazz,
                                  RefConstant refConstant,
                                  String      type)
    {
        // Try to figure out the class of the return type.
        returnTypeClass = null;
        refConstant.referencedMemberAccept(this);

        return valueFactory.createValue(type,
                                        returnTypeClass,
                                        true);
    }


    protected void setMethodParameterValue(Clazz       clazz,
                                           RefConstant refConstant,
                                           int         parameterIndex,
                                           Value       value)
    {
        // We don't care about the parameter value.
    }


    protected Value getMethodParameterValue(Clazz  clazz,
                                            Method method,
                                            int    parameterIndex,
                                            String type,
                                            Clazz  referencedClass)
    {
        return valueFactory.createValue(type, referencedClass, true);
    }


    protected void setMethodReturnValue(Clazz  clazz,
                                        Method method,
                                        Value  value)
    {
        // We don't care about the return value.
    }


    protected Value getMethodReturnValue(Clazz       clazz,
                                         RefConstant refConstant,
                                         String      type)
    {
        // Try to figure out the class of the return type.
        returnTypeClass = null;
        refConstant.referencedMemberAccept(this);

        return valueFactory.createValue(type,
                                        returnTypeClass,
                                        true);
    }


    // Implementations for MemberVisitor.

    public void visitProgramField(ProgramClass programClass, ProgramField programField)
    {
        returnTypeClass = programField.referencedClass;
    }


    public void visitProgramMethod(ProgramClass programClass, ProgramMethod programMethod)
    {
        Clazz[] referencedClasses = programMethod.referencedClasses;
        if (referencedClasses != null)
        {
            returnTypeClass = referencedClasses[referencedClasses.length - 1];
        }
    }


    public void visitLibraryField(LibraryClass programClass, LibraryField programField)
    {
        returnTypeClass = programField.referencedClass;
    }


    public void visitLibraryMethod(LibraryClass programClass, LibraryMethod programMethod)
    {
        Clazz[] referencedClasses = programMethod.referencedClasses;
        if (referencedClasses != null)
        {
            returnTypeClass = referencedClasses[referencedClasses.length - 1];
        }
    }


//    public void visitLibraryMember(LibraryClass libraryClass, LibraryMember libraryMember)
//    {
//    }
}
