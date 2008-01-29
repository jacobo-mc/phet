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
package proguard.classfile.util;

import proguard.classfile.*;
import proguard.classfile.constant.ClassConstant;
import proguard.classfile.constant.visitor.ConstantVisitor;
import proguard.classfile.visitor.ClassVisitor;

/**
 * This ClassVisitor initializes the superclass hierarchy of all classes that
 * it visits.
 * <p>
 * Visited library classes get direct references to their superclasses and
 * interfaces, replacing the superclass names and interface names. The direct
 * references are equivalent to the names, but they are more efficient to work
 * with.
 * <p>
 * This visitor optionally prints warnings if some items can't be found.
 *
 * @author Eric Lafortune
 */
public class ClassSuperHierarchyInitializer
extends      SimplifiedVisitor
implements   ClassVisitor,
             ConstantVisitor
{
    private final ClassPool      programClassPool;
    private final ClassPool      libraryClassPool;
    private final WarningPrinter warningPrinter;


    /**
     * Creates a new ClassSuperHierarchyInitializer that initializes the super
     * hierarchy of all visited class files, optionally printing warnings if
     * some classes can't be found.
     */
    public ClassSuperHierarchyInitializer(ClassPool      programClassPool,
                                          ClassPool      libraryClassPool,
                                          WarningPrinter warningPrinter)
    {
        this.programClassPool = programClassPool;
        this.libraryClassPool = libraryClassPool;
        this.warningPrinter   = warningPrinter;
    }


    // Implementations for ClassVisitor.

    public void visitProgramClass(ProgramClass programClass)
    {
        // Link to the super class.
        if (programClass.u2superClass != 0)
        {
            programClass.constantPoolEntryAccept(programClass.u2superClass,
                                                 this);
        }

        // Link to the interfaces.
        for (int index = 0; index < programClass.u2interfacesCount; index++)
        {
            programClass.constantPoolEntryAccept(programClass.u2interfaces[index],
                                                 this);
        }
    }


    public void visitLibraryClass(LibraryClass libraryClass)
    {
        String className = libraryClass.getName();

        // Link to the super class.
        String superClassName = libraryClass.superClassName;
        if (superClassName != null)
        {
            // Keep a reference to the superclass.
            libraryClass.superClass = findClass(className, superClassName);
        }

        // Link to the interfaces.
        if (libraryClass.interfaceNames != null)
        {
            String[] interfaceNames   = libraryClass.interfaceNames;
            Clazz[]  interfaceClasses = new Clazz[interfaceNames.length];

            for (int index = 0; index < interfaceNames.length; index++)
            {
                // Keep a reference to the interface class.
                interfaceClasses[index] =
                    findClass(className, interfaceNames[index]);
            }

            libraryClass.interfaceClasses = interfaceClasses;
        }

        // Discard the name Strings. From now on, we'll use the object
        // references.
        libraryClass.superClassName = null;
        libraryClass.interfaceNames = null;
    }


    // Implementations for ConstantVisitor.

    public void visitClassConstant(Clazz clazz, ClassConstant classConstant)
    {
        classConstant.referencedClass =
            findClass(clazz.getName(), classConstant.getName(clazz));
    }


    // Small utility methods.

    /**
     * Returns the class with the given name, either for the program class pool
     * or from the library class pool, or <code>null</code> if it can't be found.
     */
    private Clazz findClass(String subClassName, String name)
    {
        // First look for the class in the program class pool.
        Clazz clazz = programClassPool.getClass(name);

        // Otherwise look for the class in the library class pool.
        if (clazz == null)
        {
            clazz = libraryClassPool.getClass(name);
        }

        if (clazz == null &&
            warningPrinter != null)
        {
            // We didn't find the superclass or interface. Print a warning.
            warningPrinter.print("Warning: " +
                                 ClassUtil.externalClassName(subClassName) +
                                 ": can't find superclass or interface " +
                                 ClassUtil.externalClassName(name));
        }

        return clazz;
    }
}
