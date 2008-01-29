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
package proguard.classfile.editor;

import proguard.classfile.*;
import proguard.classfile.attribute.*;
import proguard.classfile.attribute.annotation.*;
import proguard.classfile.attribute.annotation.visitor.*;
import proguard.classfile.attribute.visitor.*;
import proguard.classfile.constant.*;
import proguard.classfile.constant.visitor.ConstantVisitor;
import proguard.classfile.util.*;
import proguard.classfile.visitor.*;

/**
 * This ClassVisitor fixes references of constant pool entries, fields,
 * methods, and attributes to classes whose names have changed. Descriptors
 * of member references are not updated yet.
 *
 * @see MemberReferenceFixer
 * @author Eric Lafortune
 */
public class ClassReferenceFixer
extends      SimplifiedVisitor
implements   ClassVisitor,
             ConstantVisitor,
             MemberVisitor,
             AttributeVisitor,
             InnerClassesInfoVisitor,
             LocalVariableInfoVisitor,
             LocalVariableTypeInfoVisitor,
             AnnotationVisitor,
             ElementValueVisitor
{
    private final boolean ensureUniqueMemberNames;


    private final ConstantPoolEditor constantPoolEditor = new ConstantPoolEditor();


    /**
     * Creates a new ClassReferenceFixer.
     * @param ensureUniqueMemberNames specifies whether class members whose
     *                                descriptor changes should get new, unique
     *                                names, in order to avoid naming conflicts
     *                                with similar methods.
     */
    public ClassReferenceFixer(boolean ensureUniqueMemberNames)
    {
        this.ensureUniqueMemberNames = ensureUniqueMemberNames;
    }


    // Implementations for ClassVisitor.

    public void visitProgramClass(ProgramClass programClass)
    {
        // Fix the constant pool.
        programClass.constantPoolEntriesAccept(this);

        // Fix class members.
        programClass.fieldsAccept(this);
        programClass.methodsAccept(this);

        // Fix the attributes.
        programClass.attributesAccept(this);
    }


    public void visitLibraryClass(LibraryClass libraryClass)
    {
        // Fix class members.
        libraryClass.fieldsAccept(this);
        libraryClass.methodsAccept(this);
    }


    // Implementations for MemberVisitor.

    public void visitProgramField(ProgramClass programClass, ProgramField programField)
    {
        // Has the descriptor changed?
        String descriptor    = programField.getDescriptor(programClass);
        String newDescriptor = newDescriptor(descriptor,
                                             programField.referencedClass);

        if (!descriptor.equals(newDescriptor))
        {
            // Update the descriptor.
            programField.u2descriptorIndex =
                constantPoolEditor.addUtf8Constant(programClass, newDescriptor);

            // Update the name, if requested.
            if (ensureUniqueMemberNames)
            {
                String name    = programField.getName(programClass);
                String newName = newUniqueMemberName(name, descriptor);
                programField.u2nameIndex =
                    constantPoolEditor.addUtf8Constant(programClass, newName);
            }
        }

        // Fix the attributes.
        programField.attributesAccept(programClass, this);
    }


    public void visitProgramMethod(ProgramClass programClass, ProgramMethod programMethod)
    {
        // Has the descriptor changed?
        String descriptor    = programMethod.getDescriptor(programClass);
        String newDescriptor = newDescriptor(descriptor,
                                             programMethod.referencedClasses);

        if (!descriptor.equals(newDescriptor))
        {
            // Update the descriptor.
            programMethod.u2descriptorIndex =
                constantPoolEditor.addUtf8Constant(programClass, newDescriptor);

            // Update the name, if requested.
            if (ensureUniqueMemberNames)
            {
                String name    = programMethod.getName(programClass);
                String newName = newUniqueMemberName(name, descriptor);
                programMethod.u2nameIndex =
                    constantPoolEditor.addUtf8Constant(programClass, newName);
            }
        }

        // Fix the attributes.
        programMethod.attributesAccept(programClass, this);
    }


    public void visitLibraryField(LibraryClass libraryClass, LibraryField libraryField)
    {
        // Has the descriptor changed?
        String descriptor    = libraryField.getDescriptor(libraryClass);
        String newDescriptor = newDescriptor(descriptor,
                                             libraryField.referencedClass);

        // Update the descriptor.
        libraryField.descriptor = newDescriptor;
    }


    public void visitLibraryMethod(LibraryClass libraryClass, LibraryMethod libraryMethod)
    {
        // Has the descriptor changed?
        String descriptor    = libraryMethod.getDescriptor(libraryClass);
        String newDescriptor = newDescriptor(descriptor,
                                             libraryMethod.referencedClasses);

        // Update the descriptor.
        libraryMethod.descriptor = newDescriptor;
    }


    // Implementations for ConstantVisitor.

    public void visitAnyConstant(Clazz clazz, Constant constant) {}


    public void visitStringConstant(Clazz clazz, StringConstant stringConstant)
    {
        // Does the string refer to a class, due to a Class.forName construct?
        Clazz  referencedClass  = stringConstant.referencedClass;
        Member referencedMember = stringConstant.referencedMember;
        if (referencedClass  != null &&
            referencedMember == null)
        {
            // Reconstruct the new class name.
            String externalClassName    = stringConstant.getString(clazz);
            String internalClassName    = ClassUtil.internalClassName(externalClassName);
            String newInternalClassName = newClassName(internalClassName,
                                                       referencedClass);

            // Update the String entry if required.
            if (!newInternalClassName.equals(internalClassName))
            {
                String newExternalClassName = ClassUtil.externalClassName(newInternalClassName);

                // Refer to a new Utf8 entry.
                stringConstant.u2stringIndex =
                    constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                       newExternalClassName);
            }
        }
    }


    public void visitClassConstant(Clazz clazz, ClassConstant classConstant)
    {
        // Do we know the referenced class?
        Clazz referencedClass = classConstant.referencedClass;
        if (referencedClass != null)
        {
            // Has the class name changed?
            String className    = classConstant.getName(clazz);
            String newClassName = newClassName(className, referencedClass);
            if (!className.equals(newClassName))
            {
                // Refer to a new Utf8 entry.
                classConstant.u2nameIndex =
                    constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                       newClassName);
            }
        }
    }


    // Implementations for AttributeVisitor.

    public void visitAnyAttribute(Clazz clazz, Attribute attribute) {}


    public void visitInnerClassesAttribute(Clazz clazz, InnerClassesAttribute innerClassesAttribute)
    {
        // Fix the inner class names.
        innerClassesAttribute.innerClassEntriesAccept(clazz, this);
    }


    public void visitCodeAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
        // Fix the attributes.
        codeAttribute.attributesAccept(clazz, method, this);
    }


    public void visitLocalVariableTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTableAttribute localVariableTableAttribute)
    {
        // Fix the types of the local variables.
        localVariableTableAttribute.localVariablesAccept(clazz, method, codeAttribute, this);
    }


    public void visitLocalVariableTypeTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTypeTableAttribute localVariableTypeTableAttribute)
    {
        // Fix the signatures of the local variables.
        localVariableTypeTableAttribute.localVariablesAccept(clazz, method, codeAttribute, this);
    }


    public void visitSignatureAttribute(Clazz clazz, SignatureAttribute signatureAttribute)
    {
        // Compute the new signature.
        String signature    = clazz.getString(signatureAttribute.u2signatureIndex);
        String newSignature = newDescriptor(signature,
                                            signatureAttribute.referencedClasses);

        if (!signature.equals(newSignature))
        {
            signatureAttribute.u2signatureIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newSignature);
        }
    }


    public void visitAnyAnnotationsAttribute(Clazz clazz, AnnotationsAttribute annotationsAttribute)
    {
        // Fix the annotations.
        annotationsAttribute.annotationsAccept(clazz, this);
    }


    public void visitAnyParameterAnnotationsAttribute(Clazz clazz, Method method, ParameterAnnotationsAttribute parameterAnnotationsAttribute)
    {
        // Fix the annotations.
        parameterAnnotationsAttribute.annotationsAccept(clazz, method, this);
    }


    public void visitAnnotationDefaultAttribute(Clazz clazz, Method method, AnnotationDefaultAttribute annotationDefaultAttribute)
    {
        // Fix the annotation.
        annotationDefaultAttribute.defaultValueAccept(clazz, this);
    }


    // Implementations for InnerClassesInfoVisitor.

    public void visitInnerClassesInfo(Clazz clazz, InnerClassesInfo innerClassesInfo)
    {
        // Fix the inner class name.
        int innerClassIndex = innerClassesInfo.u2innerClassIndex;
        int innerNameIndex  = innerClassesInfo.u2innerNameIndex;
        if (innerClassIndex != 0 &&
            innerNameIndex  != 0)
        {
            String newInnerName = clazz.getClassName(innerClassIndex);
            int index = newInnerName.lastIndexOf(ClassConstants.INNER_CLASS_SEPARATOR);
            if (index >= 0)
            {
                innerClassesInfo.u2innerNameIndex =
                    constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                       newInnerName.substring(index + 1));
            }
        }
    }


    // Implementations for LocalVariableInfoVisitor.

    public void visitLocalVariableInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableInfo localVariableInfo)
    {
        // Has the descriptor changed?
        String descriptor    = clazz.getString(localVariableInfo.u2descriptorIndex);
        String newDescriptor = newDescriptor(descriptor,
                                             localVariableInfo.referencedClass);

        if (!descriptor.equals(newDescriptor))
        {
            // Refer to a new Utf8 entry.
            localVariableInfo.u2descriptorIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newDescriptor);
        }
    }


    // Implementations for LocalVariableTypeInfoVisitor.

    public void visitLocalVariableTypeInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTypeInfo localVariableTypeInfo)
    {
        // Has the signature changed?
        String signature    = clazz.getString(localVariableTypeInfo.u2signatureIndex);
        String newSignature = newDescriptor(signature,
                                            localVariableTypeInfo.referencedClasses);

        if (!signature.equals(newSignature))
        {
            localVariableTypeInfo.u2signatureIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newSignature);
        }
    }


    // Implementations for AnnotationVisitor.

    public void visitAnnotation(Clazz clazz, Annotation annotation)
    {
        // Compute the new type name.
        String typeName    = clazz.getString(annotation.u2typeIndex);
        String newTypeName = newDescriptor(typeName,
                                           annotation.referencedClasses);

        if (!typeName.equals(newTypeName))
        {
            // Refer to a new Utf8 entry.
            annotation.u2typeIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newTypeName);
        }

        // Fix the element values.
        annotation.elementValuesAccept(clazz, this);
    }


    // Implementations for ElementValueVisitor.

    public void visitConstantElementValue(Clazz clazz, Annotation annotation, ConstantElementValue constantElementValue)
    {
    }


    public void visitEnumConstantElementValue(Clazz clazz, Annotation annotation, EnumConstantElementValue enumConstantElementValue)
    {
        // Compute the new type name.
        String typeName    = clazz.getString(enumConstantElementValue.u2typeNameIndex);
        String newTypeName = newDescriptor(typeName,
                                           enumConstantElementValue.referencedClasses);

        if (!typeName.equals(newTypeName))
        {
            // Refer to a new Utf8 entry.
            enumConstantElementValue.u2typeNameIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newTypeName);
        }
    }


    public void visitClassElementValue(Clazz clazz, Annotation annotation, ClassElementValue classElementValue)
    {
        // Compute the new class name.
        String className    = clazz.getString(classElementValue.u2classInfoIndex);
        String newClassName = newDescriptor(className,
                                            classElementValue.referencedClasses);

        if (!className.equals(newClassName))
        {
            // Refer to a new Utf8 entry.
            classElementValue.u2classInfoIndex =
                constantPoolEditor.addUtf8Constant((ProgramClass)clazz,
                                                   newClassName);
        }
    }


    public void visitAnnotationElementValue(Clazz clazz, Annotation annotation, AnnotationElementValue annotationElementValue)
    {
        // Fix the annotation.
        annotationElementValue.annotationAccept(clazz, this);
    }


    public void visitArrayElementValue(Clazz clazz, Annotation annotation, ArrayElementValue arrayElementValue)
    {
        // Fix the element values.
        arrayElementValue.elementValuesAccept(clazz, annotation, this);
    }


    // Small utility methods.

    private static String newDescriptor(String descriptor,
                                        Clazz  referencedClass)
    {
        // If there is no referenced class, the descriptor won't change.
        if (referencedClass == null)
        {
            return descriptor;
        }

        // Unravel and reconstruct the class element of the descriptor.
        DescriptorClassEnumeration descriptorClassEnumeration =
            new DescriptorClassEnumeration(descriptor);

        StringBuffer newDescriptorBuffer = new StringBuffer(descriptor.length());
        newDescriptorBuffer.append(descriptorClassEnumeration.nextFluff());

        // Only if the descriptor contains a class name (e.g. with an array of
        // primitive types), the descriptor can change.
        if (descriptorClassEnumeration.hasMoreClassNames())
        {
            String className = descriptorClassEnumeration.nextClassName();
            String fluff     = descriptorClassEnumeration.nextFluff();

            String newClassName = newClassName(className,
                                               referencedClass);

            newDescriptorBuffer.append(newClassName);
            newDescriptorBuffer.append(fluff);
        }

        return newDescriptorBuffer.toString();
    }


    private static String newDescriptor(String  descriptor,
                                        Clazz[] referencedClasses)
    {
        // If there are no referenced classes, the descriptor won't change.
        if (referencedClasses == null ||
            referencedClasses.length == 0)
        {
            return descriptor;
        }

        // Unravel and reconstruct the class elements of the descriptor.
        DescriptorClassEnumeration descriptorClassEnumeration =
            new DescriptorClassEnumeration(descriptor);

        StringBuffer newDescriptorBuffer = new StringBuffer(descriptor.length());
        newDescriptorBuffer.append(descriptorClassEnumeration.nextFluff());

        int index = 0;
        while (descriptorClassEnumeration.hasMoreClassNames())
        {
            String className = descriptorClassEnumeration.nextClassName();
            String fluff     = descriptorClassEnumeration.nextFluff();

            String newClassName = newClassName(className,
                                               referencedClasses[index++]);

            newDescriptorBuffer.append(newClassName);
            newDescriptorBuffer.append(fluff);
        }

        return newDescriptorBuffer.toString();
    }


    /**
     * Returns a unique class member name, based on the given name and descriptor.
     */
    private String newUniqueMemberName(String name, String descriptor)
    {
        // TODO: Avoid duplicate constructors.
        return name.equals(ClassConstants.INTERNAL_METHOD_NAME_INIT) ?
            ClassConstants.INTERNAL_METHOD_NAME_INIT :
            name + ClassConstants.SPECIAL_MEMBER_SEPARATOR + Long.toHexString(Math.abs((descriptor).hashCode()));
    }


    /**
     * Returns the new class name based on the given class name and the new
     * name of the given referenced class. Class names of array types
     * are handled properly.
     */
    private static String newClassName(String className,
                                       Clazz  referencedClass)
    {
        // If there is no referenced class, the class name won't change.
        if (referencedClass == null)
        {
            return className;
        }

        // Reconstruct the class name.
        String newClassName = referencedClass.getName();

        // Is it an array type?
        if (className.charAt(0) == ClassConstants.INTERNAL_TYPE_ARRAY)
        {
            // Add the array prefixes and suffix "[L...;".
            newClassName =
                 className.substring(0, className.indexOf(ClassConstants.INTERNAL_TYPE_CLASS_START)+1) +
                 newClassName +
                 ClassConstants.INTERNAL_TYPE_CLASS_END;
        }

        return newClassName;
    }
}
