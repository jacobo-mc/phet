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
package proguard.optimize.peephole;

import proguard.classfile.*;
import proguard.classfile.editor.MethodInvocationFixer;
import proguard.classfile.util.*;
import proguard.classfile.visitor.MemberVisitor;
import proguard.optimize.info.NonPrivateMemberMarker;

/**
 * This MemberVisitor makes all class members that it visits private, unless they
 * have been marked by a NonPrivateMemberMarker. The invocations of the
 * privatized method still have to be fixed.
 *
 * @see NonPrivateMemberMarker
 * @see MethodInvocationFixer
 * @author Eric Lafortune
 */
public class MemberPrivatizer
extends      SimplifiedVisitor
implements   MemberVisitor
{
    private final MemberVisitor extraFieldVisitor;
    private final MemberVisitor extraMethodVisitor;


    /**
     * Creates a new MemberPrivatizer.
     */
    public MemberPrivatizer()
    {
        this(null, null);
    }


    /**
     * Creates a new MemberPrivatizer.
     * @param extraFieldVisitor  an optional extra visitor for all privatized
     *                           fields.
     * @param extraMethodVisitor an optional extra visitor for all privatized
     *                           methods.
     */
    public MemberPrivatizer(MemberVisitor extraFieldVisitor,
                            MemberVisitor extraMethodVisitor)
    {
        this.extraFieldVisitor  = extraFieldVisitor;
        this.extraMethodVisitor = extraMethodVisitor;
    }


    // Implementations for MemberVisitor.

    public void visitProgramField(ProgramClass programClass, ProgramField programField)
    {
        // Is the field unmarked?
        if (NonPrivateMemberMarker.canBeMadePrivate(programField))
        {
            // Make the field private.
            programField.u2accessFlags =
                AccessUtil.replaceAccessFlags(programField.u2accessFlags,
                                              ClassConstants.INTERNAL_ACC_PRIVATE);

            // Visit the field, if required.
            if (extraFieldVisitor != null)
            {
                extraFieldVisitor.visitProgramField(programClass, programField);
            }
        }
    }


    public void visitProgramMethod(ProgramClass programClass, ProgramMethod programMethod)
    {
        // Is the method unmarked?
        if (NonPrivateMemberMarker.canBeMadePrivate(programMethod))
        {
            // Make the method private.
            programMethod.u2accessFlags =
                AccessUtil.replaceAccessFlags(programMethod.u2accessFlags,
                                              ClassConstants.INTERNAL_ACC_PRIVATE);

            // Visit the method, if required.
            if (extraMethodVisitor != null)
            {
                extraMethodVisitor.visitProgramMethod(programClass, programMethod);
            }
        }
    }
}
