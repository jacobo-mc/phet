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
package proguard.classfile.visitor;

import proguard.classfile.*;


/**
 * This class visits ProgramMember objects referring to methods, identified by
 * a name and descriptor pair.
 *
 * @author Eric Lafortune
 */
public class NamedMethodVisitor implements ClassVisitor
{
    private final String        name;
    private final String        descriptor;
    private final MemberVisitor memberVisitor;


    public NamedMethodVisitor(String        name,
                              String        descriptor,
                              MemberVisitor memberVisitor)
    {
        this.name          = name;
        this.descriptor    = descriptor;
        this.memberVisitor = memberVisitor;
    }


    public void visitProgramClass(ProgramClass programClass)
    {
        programClass.methodAccept(name, descriptor, memberVisitor);
    }


    public void visitLibraryClass(LibraryClass libraryClass)
    {
        libraryClass.methodAccept(name, descriptor, memberVisitor);
    }
}
