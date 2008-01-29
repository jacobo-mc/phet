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
package proguard.optimize.peephole;

import proguard.classfile.*;
import proguard.classfile.attribute.*;
import proguard.classfile.attribute.visitor.AttributeVisitor;
import proguard.classfile.editor.VariableEditor;
import proguard.classfile.util.*;
import proguard.classfile.visitor.MemberVisitor;
import proguard.optimize.*;
import proguard.optimize.info.*;

/**
 * This MemberVisitor removes unused local variables from the code of the methods
 * that it visits.
 *
 * @see ParameterUsageMarker
 * @see MethodStaticizer
 * @see MethodDescriptorShrinker
 * @author Eric Lafortune
 */
public class VariableShrinker
extends      SimplifiedVisitor
implements   AttributeVisitor
{
    private static final boolean DEBUG = false;


    private final MemberVisitor extraVariableMemberVisitor;

    private final VariableUsageMarker variableUsageMarker = new VariableUsageMarker();
    private final VariableEditor      variableEditor      = new VariableEditor();


    /**
     * Creates a new VariableShrinker.
     */
    public VariableShrinker()
    {
        this(null);
    }


    /**
     * Creates a new VariableShrinker with an extra visitor.
     * @param extraVariableMemberVisitor an optional extra visitor for all
     *                                   removed variables.
     */
    public VariableShrinker(MemberVisitor extraVariableMemberVisitor)
    {
        this.extraVariableMemberVisitor = extraVariableMemberVisitor;
    }


    // Implementations for AttributeVisitor.

    public void visitAnyAttribute(Clazz clazz, Attribute attribute) {}


    public void visitCodeAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
        if ((method.getAccessFlags() & ClassConstants.INTERNAL_ACC_ABSTRACT) == 0)
        {
            // Figure out the local variables that are used by the code.
            variableUsageMarker.visitCodeAttribute(clazz, method, codeAttribute);

            // Get the total size of the local variable frame.
            int variablesSize = variableUsageMarker.getVariablesSize();

            // The descriptor may have been been shrunk already, so get the
            // original parameter size.
            int parameterSize = ClassUtil.internalMethodParameterSize(method.getDescriptor(clazz));

            if (DEBUG)
            {
                System.out.println("VariableShrinker: "+clazz.getName()+"."+method.getName(clazz)+method.getDescriptor(clazz));
                System.out.println("  parameter size = " + parameterSize);
                System.out.println("  variables size = " + variablesSize);
            }

            // Make sure the size of the local variable frame is set correctly.
            codeAttribute.u2maxLocals = variablesSize;

            // Delete unused local variables from the local variable frame.
            variableEditor.reset(variablesSize);

            for (int variableIndex = parameterSize+1; variableIndex < variablesSize; variableIndex++)
            {
                // Is the variable not required?
                if (!variableUsageMarker.isVariableUsed(variableIndex))
                {
                    if (DEBUG)
                    {
                        System.out.println("  Deleting local variable #"+variableIndex);
                    }

                    // Delete the unused variable.
                    variableEditor.deleteVariable(variableIndex);

                    // Visit the method, if required.
                    if (extraVariableMemberVisitor != null)
                    {
                        method.accept(clazz, extraVariableMemberVisitor);
                    }
                }
            }

            // Shift all remaining parameters and variables in the byte code.
            variableEditor.visitCodeAttribute(clazz, method, codeAttribute);
        }
    }
}
