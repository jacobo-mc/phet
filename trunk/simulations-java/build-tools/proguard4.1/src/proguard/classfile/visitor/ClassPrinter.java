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
import proguard.classfile.attribute.*;
import proguard.classfile.attribute.annotation.*;
import proguard.classfile.attribute.annotation.visitor.*;
import proguard.classfile.attribute.preverification.*;
import proguard.classfile.attribute.preverification.visitor.*;
import proguard.classfile.attribute.visitor.*;
import proguard.classfile.constant.*;
import proguard.classfile.constant.visitor.ConstantVisitor;
import proguard.classfile.instruction.*;
import proguard.classfile.instruction.visitor.InstructionVisitor;
import proguard.classfile.util.*;

import java.io.PrintStream;


/**
 * This <code>ClassVisitor</code> prints out the complete internal
 * structure of the classes it visits.
 *
 * @author Eric Lafortune
 */
public class ClassPrinter
extends      SimplifiedVisitor
implements   ClassVisitor,
             ConstantVisitor,
             MemberVisitor,
             AttributeVisitor,
             ExceptionInfoVisitor,
             InnerClassesInfoVisitor,
             StackMapFrameVisitor,
             VerificationTypeVisitor,
             LineNumberInfoVisitor,
             LocalVariableInfoVisitor,
             LocalVariableTypeInfoVisitor,
             AnnotationVisitor,
             ElementValueVisitor,
             InstructionVisitor
{
    private static final String INDENTATION = "  ";

    private final PrintStream ps;
    private int         indentation;


    /**
     * Creates a new ClassPrinter that prints to <code>System.out</code>.
     */
    public ClassPrinter()
    {
        this(System.out);
    }


    /**
     * Creates a new ClassPrinter that prints to the given
     * <code>PrintStream</code>.
     */
    public ClassPrinter(PrintStream printStream)
    {
        ps = printStream;
    }


    // Implementations for ClassVisitor.

    public void visitProgramClass(ProgramClass programClass)
    {
        println("_____________________________________________________________________");
        println(visitorInfo(programClass) + " " +
                "Program class: " + programClass.getName());
        indent();
        println("Superclass:    " + programClass.getSuperName());
        println("Major version: 0x" + Integer.toHexString(ClassUtil.internalMajorClassVersion(programClass.u4version)));
        println("Minor version: 0x" + Integer.toHexString(ClassUtil.internalMinorClassVersion(programClass.u4version)));
        println("Access flags:  0x" + Integer.toHexString(programClass.u2accessFlags));
        println("  = " +
                ClassUtil.externalClassAccessFlags(programClass.u2accessFlags) +
                ((programClass.u2accessFlags & ClassConstants.INTERNAL_ACC_INTERFACE) == 0 ? "class " : "") +
                ClassUtil.externalClassName(programClass.getName()) +
                (programClass.u2superClass == 0 ? "" : " extends " +
                ClassUtil.externalClassName(programClass.getSuperName())));
        outdent();
        println();

        println("Interfaces (count = " + programClass.u2interfacesCount + "):");
        indent();
        for (int index = 0; index < programClass.u2interfacesCount; index++)
        {
            println("+ " + programClass.getClassName(programClass.u2interfaces[index]));
        }
        outdent();
        println();

        println("Constant Pool (count = " + programClass.u2constantPoolCount + "):");
        indent();
        programClass.constantPoolEntriesAccept(this);
        outdent();
        println();

        println("Fields (count = " + programClass.u2fieldsCount + "):");
        indent();
        programClass.fieldsAccept(this);
        outdent();
        println();

        println("Methods (count = " + programClass.u2methodsCount + "):");
        indent();
        programClass.methodsAccept(this);
        outdent();
        println();

        println("Class file attributes (count = " + programClass.u2attributesCount + "):");
        indent();
        programClass.attributesAccept(this);
        outdent();
        println();
    }


    public void visitLibraryClass(LibraryClass libraryClass)
    {
        println("_____________________________________________________________________");
        println(visitorInfo(libraryClass) + " " +
                "Library class: " + libraryClass.getName());
        indent();
        println("Superclass:    " + libraryClass.getSuperName());
        println("Access flags:  0x" + Integer.toHexString(libraryClass.u2accessFlags));
        println("  = " +
                ClassUtil.externalClassAccessFlags(libraryClass.u2accessFlags) +
                ((libraryClass.u2accessFlags & ClassConstants.INTERNAL_ACC_INTERFACE) == 0 ? "class " : "") +
                ClassUtil.externalClassName(libraryClass.getName()) + " extends "  +
                ClassUtil.externalClassName(libraryClass.getSuperName()));
        outdent();
        println();

        println("Interfaces (count = " + libraryClass.interfaceClasses.length + "):");
        for (int index = 0; index < libraryClass.interfaceClasses.length; index++)
        {
            Clazz interfaceClass = libraryClass.interfaceClasses[index];
            if (interfaceClass != null)
            {
                println("  + " + interfaceClass.getName());
            }
        }

        println("Fields (count = " + libraryClass.fields.length + "):");
        libraryClass.fieldsAccept(this);

        println("Methods (count = " + libraryClass.methods.length + "):");
        libraryClass.methodsAccept(this);
    }


    // Implementations for ConstantVisitor.

    public void visitIntegerConstant(Clazz clazz, IntegerConstant integerConstant)
    {
        println(visitorInfo(integerConstant) + " Integer [" +
                integerConstant.getValue() + "]");
    }


    public void visitLongConstant(Clazz clazz, LongConstant longConstant)
    {
        println(visitorInfo(longConstant) + " Long [" +
                longConstant.getValue() + "]");
    }


    public void visitFloatConstant(Clazz clazz, FloatConstant floatConstant)
    {
        println(visitorInfo(floatConstant) + " Float [" +
                floatConstant.getValue() + "]");
    }


    public void visitDoubleConstant(Clazz clazz, DoubleConstant doubleConstant)
    {
        println(visitorInfo(doubleConstant) + " Double [" +
                doubleConstant.getValue() + "]");
    }


    public void visitStringConstant(Clazz clazz, StringConstant stringConstant)
    {
        println(visitorInfo(stringConstant) + " String [" +
                clazz.getString(stringConstant.u2stringIndex) + "]");
    }


    public void visitUtf8Constant(Clazz clazz, Utf8Constant utf8Constant)
    {
        println(visitorInfo(utf8Constant) + " Utf8 [" +
                utf8Constant.getString() + "]");
    }


    public void visitFieldrefConstant(Clazz clazz, FieldrefConstant fieldrefConstant)
    {
        println(visitorInfo(fieldrefConstant) + " Fieldref [" +
                clazz.getClassName(fieldrefConstant.u2classIndex)  + "." +
                clazz.getName(fieldrefConstant.u2nameAndTypeIndex) + " " +
                clazz.getType(fieldrefConstant.u2nameAndTypeIndex) + "]");
    }


    public void visitInterfaceMethodrefConstant(Clazz clazz, InterfaceMethodrefConstant interfaceMethodrefConstant)
    {
        println(visitorInfo(interfaceMethodrefConstant) + " InterfaceMethodref [" +
                clazz.getClassName(interfaceMethodrefConstant.u2classIndex)  + "." +
                clazz.getName(interfaceMethodrefConstant.u2nameAndTypeIndex) + " " +
                clazz.getType(interfaceMethodrefConstant.u2nameAndTypeIndex) + "]");
    }


    public void visitMethodrefConstant(Clazz clazz, MethodrefConstant methodrefConstant)
    {
        println(visitorInfo(methodrefConstant) + " Methodref [" +
                clazz.getClassName(methodrefConstant.u2classIndex)  + "." +
                clazz.getName(methodrefConstant.u2nameAndTypeIndex) + " " +
                clazz.getType(methodrefConstant.u2nameAndTypeIndex) + "]");
    }


    public void visitClassConstant(Clazz clazz, ClassConstant classConstant)
    {
        println(visitorInfo(classConstant) + " Class [" +
                clazz.getString(classConstant.u2nameIndex) + "]");
    }


    public void visitNameAndTypeConstant(Clazz clazz, NameAndTypeConstant nameAndTypeConstant)
    {
        println(visitorInfo(nameAndTypeConstant) + " NameAndType [" +
                clazz.getString(nameAndTypeConstant.u2nameIndex) + " " +
                clazz.getString(nameAndTypeConstant.u2descriptorIndex) + "]");
    }


    // Implementations for MemberVisitor.

    public void visitProgramField(ProgramClass programClass, ProgramField programField)
    {
        println(visitorInfo(programField) + " " +
                "Field:        " +
                programField.getName(programClass) + " " +
                programField.getDescriptor(programClass));

        indent();
        println("Access flags: 0x" + Integer.toHexString(programField.u2accessFlags));
        println("  = " +
                ClassUtil.externalFullFieldDescription(programField.u2accessFlags,
                                                       programField.getName(programClass),
                                                       programField.getDescriptor(programClass)));

        visitMember(programClass, programField);
        outdent();
    }


    public void visitProgramMethod(ProgramClass programClass, ProgramMethod programMethod)
    {
        println(visitorInfo(programMethod) + " " +
                "Method:       " +
                programMethod.getName(programClass) +
                programMethod.getDescriptor(programClass));

        indent();
        println("Access flags: 0x" + Integer.toHexString(programMethod.u2accessFlags));
        println("  = " +
                ClassUtil.externalFullMethodDescription(programClass.getName(),
                                                        programMethod.u2accessFlags,
                                                        programMethod.getName(programClass),
                                                        programMethod.getDescriptor(programClass)));

        visitMember(programClass, programMethod);
        outdent();
    }


    private void visitMember(ProgramClass programClass, ProgramMember programMember)
    {
        if (programMember.u2attributesCount > 0)
        {
            println("Class member attributes (count = " + programMember.u2attributesCount + "):");
            programMember.attributesAccept(programClass, this);
        }
    }


    public void visitLibraryField(LibraryClass libraryClass, LibraryField libraryField)
    {
        println(visitorInfo(libraryField) + " " +
                "Field:        " +
                libraryField.getName(libraryClass) + " " +
                libraryField.getDescriptor(libraryClass));

        indent();
        println("Access flags: 0x" + Integer.toHexString(libraryField.u2accessFlags));
        println("  = " +
                ClassUtil.externalFullFieldDescription(libraryField.u2accessFlags,
                                                       libraryField.getName(libraryClass),
                                                       libraryField.getDescriptor(libraryClass)));
        outdent();
    }


    public void visitLibraryMethod(LibraryClass libraryClass, LibraryMethod libraryMethod)
    {
        println(visitorInfo(libraryMethod) + " " +
                "Method:       " +
                libraryMethod.getName(libraryClass) + " " +
                libraryMethod.getDescriptor(libraryClass));

        indent();
        println("Access flags: 0x" + Integer.toHexString(libraryMethod.u2accessFlags));
        println("  = " +
                ClassUtil.externalFullMethodDescription(libraryClass.getName(),
                                                        libraryMethod.u2accessFlags,
                                                        libraryMethod.getName(libraryClass),
                                                        libraryMethod.getDescriptor(libraryClass)));
        outdent();
    }


    // Implementations for AttributeVisitor.
    // Note that attributes are typically only referenced once, so we don't
    // test if they are marked already.

    public void visitUnknownAttribute(Clazz clazz, UnknownAttribute unknownAttribute)
    {
        println(visitorInfo(unknownAttribute) +
                " Unknown attribute (" + clazz.getString(unknownAttribute.u2attributeNameIndex) + ")");
    }


    public void visitSourceFileAttribute(Clazz clazz, SourceFileAttribute sourceFileAttribute)
    {
        println(visitorInfo(sourceFileAttribute) +
                " Source file attribute:");

        indent();
        clazz.constantPoolEntryAccept(sourceFileAttribute.u2sourceFileIndex, this);
        outdent();
    }


    public void visitSourceDirAttribute(Clazz clazz, SourceDirAttribute sourceDirAttribute)
    {
        println(visitorInfo(sourceDirAttribute) +
                " Source dir attribute:");

        indent();
        clazz.constantPoolEntryAccept(sourceDirAttribute.u2sourceDirIndex, this);
        outdent();
    }


    public void visitInnerClassesAttribute(Clazz clazz, InnerClassesAttribute innerClassesAttribute)
    {
        println(visitorInfo(innerClassesAttribute) +
                " Inner classes attribute (count = " + innerClassesAttribute.u2classesCount + ")");

        indent();
        innerClassesAttribute.innerClassEntriesAccept(clazz, this);
        outdent();
    }


    public void visitEnclosingMethodAttribute(Clazz clazz, EnclosingMethodAttribute enclosingMethodAttribute)
    {
        println(visitorInfo(enclosingMethodAttribute) +
                " Enclosing method attribute [" +
                clazz.getClassName(enclosingMethodAttribute.u2classIndex)  +
                (enclosingMethodAttribute.u2nameAndTypeIndex == 0 ?  "" : "." +
                 clazz.getName(enclosingMethodAttribute.u2nameAndTypeIndex) + " " +
                 clazz.getType(enclosingMethodAttribute.u2nameAndTypeIndex)) + "]");
    }


    public void visitDeprecatedAttribute(Clazz clazz, DeprecatedAttribute deprecatedAttribute)
    {
        println(visitorInfo(deprecatedAttribute) +
                " Deprecated attribute");
    }


    public void visitSyntheticAttribute(Clazz clazz, SyntheticAttribute syntheticAttribute)
    {
        println(visitorInfo(syntheticAttribute) +
                " Synthetic attribute");
    }


    public void visitSignatureAttribute(Clazz clazz, SignatureAttribute signatureAttribute)
    {
        println(visitorInfo(signatureAttribute) +
                " Signature attribute:");

        indent();
        clazz.constantPoolEntryAccept(signatureAttribute.u2signatureIndex, this);
        outdent();
    }


    public void visitConstantValueAttribute(Clazz clazz, Field field, ConstantValueAttribute constantValueAttribute)
    {
        println(visitorInfo(constantValueAttribute) +
                " Constant value attribute:");

        clazz.constantPoolEntryAccept(constantValueAttribute.u2constantValueIndex, this);
    }


    public void visitExceptionsAttribute(Clazz clazz, Method method, ExceptionsAttribute exceptionsAttribute)
    {
        println(visitorInfo(exceptionsAttribute) +
                " Exceptions attribute (count = " + exceptionsAttribute.u2exceptionIndexTableLength + ")");

        indent();
        exceptionsAttribute.exceptionEntriesAccept((ProgramClass)clazz, this);
        outdent();
    }


    public void visitCodeAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
        println(visitorInfo(codeAttribute) +
                " Code attribute instructions (code length = "+ codeAttribute.u4codeLength +
                ", locals = "+ codeAttribute.u2maxLocals +
                ", stack = "+ codeAttribute.u2maxStack + "):");

        indent();

        codeAttribute.instructionsAccept(clazz, method, this);

        println("Code attribute exceptions (count = " +
                codeAttribute.u2exceptionTableLength + "):");

        codeAttribute.exceptionsAccept(clazz, method, this);

        println("Code attribute attributes (attribute count = " +
                codeAttribute.u2attributesCount + "):");

        codeAttribute.attributesAccept(clazz, method, this);

        outdent();
    }


    public void visitStackMapAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, StackMapAttribute stackMapAttribute)
    {
        println(visitorInfo(codeAttribute) +
                " Stack map attribute (count = "+
                stackMapAttribute.u2stackMapFramesCount + "):");

        indent();
        stackMapAttribute.stackMapFramesAccept(clazz, method, codeAttribute, this);
        outdent();
    }


    public void visitStackMapTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, StackMapTableAttribute stackMapTableAttribute)
    {
        println(visitorInfo(codeAttribute) +
                " Stack map table attribute (count = "+
                stackMapTableAttribute.u2stackMapFramesCount + "):");

        indent();
        stackMapTableAttribute.stackMapFramesAccept(clazz, method, codeAttribute, this);
        outdent();
    }


    public void visitLineNumberTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, LineNumberTableAttribute lineNumberTableAttribute)
    {
        println(visitorInfo(lineNumberTableAttribute) +
                " Line number table attribute (count = " +
                lineNumberTableAttribute.u2lineNumberTableLength + ")");

        indent();
        lineNumberTableAttribute.lineNumbersAccept(clazz, method, codeAttribute, this);
        outdent();
    }


    public void visitLocalVariableTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTableAttribute localVariableTableAttribute)
    {
        println(visitorInfo(localVariableTableAttribute) +
                " Local variable table attribute (count = " +
                localVariableTableAttribute.u2localVariableTableLength + ")");

        indent();
        localVariableTableAttribute.localVariablesAccept(clazz, method, codeAttribute, this);
        outdent();
    }


    public void visitLocalVariableTypeTableAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTypeTableAttribute localVariableTypeTableAttribute)
    {
        println(visitorInfo(localVariableTypeTableAttribute) +
                " Local variable type table attribute (count = "+
                localVariableTypeTableAttribute.u2localVariableTypeTableLength + ")");

        indent();
        localVariableTypeTableAttribute.localVariablesAccept(clazz, method, codeAttribute, this);
        outdent();
    }


    public void visitRuntimeVisibleAnnotationsAttribute(Clazz clazz, RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute)
    {
        println(visitorInfo(runtimeVisibleAnnotationsAttribute) +
                " Runtime visible annotations attribute:");

        indent();
        runtimeVisibleAnnotationsAttribute.annotationsAccept(clazz, this);
        outdent();
    }


    public void visitRuntimeInvisibleAnnotationsAttribute(Clazz clazz, RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute)
    {
        println(visitorInfo(runtimeInvisibleAnnotationsAttribute) +
                " Runtime invisible annotations attribute:");

        indent();
        runtimeInvisibleAnnotationsAttribute.annotationsAccept(clazz, this);
        outdent();
    }


    public void visitRuntimeVisibleParameterAnnotationsAttribute(Clazz clazz, Method method, RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute)
    {
        println(visitorInfo(runtimeVisibleParameterAnnotationsAttribute) +
                " Runtime visible parameter annotations attribute:");

        indent();
        runtimeVisibleParameterAnnotationsAttribute.annotationsAccept(clazz, method, this);
        outdent();
    }


    public void visitRuntimeInvisibleParameterAnnotationsAttribute(Clazz clazz, Method method, RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute)
    {
        println(visitorInfo(runtimeInvisibleParameterAnnotationsAttribute) +
                " Runtime invisible parameter annotations attribute:");

        indent();
        runtimeInvisibleParameterAnnotationsAttribute.annotationsAccept(clazz, method, this);
        outdent();
    }


    public void visitAnnotationDefaultAttribute(Clazz clazz, Method method, AnnotationDefaultAttribute annotationDefaultAttribute)
    {
        println(visitorInfo(annotationDefaultAttribute) +
                " Annotation default attribute:");

        indent();
        annotationDefaultAttribute.defaultValueAccept(clazz, this);
        outdent();
    }


    // Implementations for InnerClassesInfoVisitor.

    public void visitInnerClassesInfo(Clazz clazz, InnerClassesInfo innerClassesInfo)
    {
        println(visitorInfo(innerClassesInfo) +
                " InnerClassesInfo:");

        indent();
        if (innerClassesInfo.u2innerClassIndex != 0)
        {
            clazz.constantPoolEntryAccept(innerClassesInfo.u2innerClassIndex, this);
        }

        if (innerClassesInfo.u2outerClassIndex != 0)
        {
            clazz.constantPoolEntryAccept(innerClassesInfo.u2outerClassIndex, this);
        }

        if (innerClassesInfo.u2innerNameIndex != 0)
        {
            clazz.constantPoolEntryAccept(innerClassesInfo.u2innerNameIndex, this);
        }
        outdent();
    }


    // Implementations for InstructionVisitor.

    public void visitAnyInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, Instruction instruction)
    {
        println(instruction.toString(offset));
    }


    public void visitConstantInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, ConstantInstruction constantInstruction)
    {
        println(constantInstruction.toString(offset));

        indent();
        clazz.constantPoolEntryAccept(constantInstruction.constantIndex, this);
        outdent();
    }


    public void visitTableSwitchInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, TableSwitchInstruction tableSwitchInstruction)
    {
        println(tableSwitchInstruction.toString(offset));

        indent();

        int[] jumpOffsets = tableSwitchInstruction.jumpOffsets;

        for (int index = 0; index < jumpOffsets.length; index++)
        {
            int jumpOffset = jumpOffsets[index];
            println(Integer.toString(tableSwitchInstruction.lowCase + index)  + ": offset = " + jumpOffset + ", target = " + (offset + jumpOffset));
        }

        int defaultOffset = tableSwitchInstruction.defaultOffset;
        println("default: offset = " + defaultOffset + ", target = "+ (offset + defaultOffset));

        outdent();
    }


    public void visitLookUpSwitchInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, LookUpSwitchInstruction lookUpSwitchInstruction)
    {
        println(lookUpSwitchInstruction.toString(offset));

        indent();

        int[] cases       = lookUpSwitchInstruction.cases;
        int[] jumpOffsets = lookUpSwitchInstruction.jumpOffsets;

        for (int index = 0; index < jumpOffsets.length; index++)
        {
            int jumpOffset = jumpOffsets[index];
            println(Integer.toString(cases[index])  + ": offset = " + jumpOffset + ", target = " + (offset + jumpOffset));
        }

        int defaultOffset = lookUpSwitchInstruction.defaultOffset;
        println("default: offset = " + defaultOffset + ", target = "+ (offset + defaultOffset));

        outdent();
    }


    // Implementations for ExceptionInfoVisitor.

    public void visitExceptionInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, ExceptionInfo exceptionInfo)
    {
        println(visitorInfo(exceptionInfo) +
                " ExceptionInfo (" +
                exceptionInfo.u2startPC + " -> " +
                exceptionInfo.u2endPC + ": " +
                exceptionInfo.u2handlerPC + "):");

        if (exceptionInfo.u2catchType != 0)
        {
            clazz.constantPoolEntryAccept(exceptionInfo.u2catchType, this);
        }
    }


    // Implementations for StackMapFrameVisitor.

    public void visitSameZeroFrame(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, SameZeroFrame sameZeroFrame)
    {
        println(visitorInfo(sameZeroFrame) +
                " [" + offset  + "]" +
                " Var: ..., Stack: (empty)");
    }


    public void visitSameOneFrame(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, SameOneFrame sameOneFrame)
    {
        print(visitorInfo(sameOneFrame) +
              " [" + offset  + "]" +
              " Var: ..., Stack: ");

        sameOneFrame.stackItemAccept(clazz, method, codeAttribute, offset, this);

        println();
    }


    public void visitLessZeroFrame(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, LessZeroFrame lessZeroFrame)
    {
        println(visitorInfo(lessZeroFrame) +
                " [" + offset  + "]" +
                " Var: -" + lessZeroFrame.choppedVariablesCount +
                ", Stack: (empty)");
    }


    public void visitMoreZeroFrame(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, MoreZeroFrame moreZeroFrame)
    {
        print(visitorInfo(moreZeroFrame) +
              " [" + offset  + "]" +
              " Var: ...");

        moreZeroFrame.additionalVariablesAccept(clazz, method, codeAttribute, offset, this);

        ps.println(", Stack: (empty)");
    }


    public void visitFullFrame(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, FullFrame fullFrame)
    {
        print(visitorInfo(fullFrame) +
              " [" + offset  + "]" +
              " Var: ");

        fullFrame.variablesAccept(clazz, method, codeAttribute, offset, this);

        ps.print(", Stack: ");

        fullFrame.stackAccept(clazz, method, codeAttribute, offset, this);

        println();
    }


    // Implementations for VerificationTypeVisitor.

    public void visitIntegerType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, IntegerType integerType)
    {
        ps.print("[i]");
    }


    public void visitFloatType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, FloatType floatType)
    {
        ps.print("[f]");
    }


    public void visitLongType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, LongType longType)
    {
        ps.print("[l]");
    }


    public void visitDoubleType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, DoubleType doubleType)
    {
        ps.print("[d]");
    }


    public void visitTopType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, TopType topType)
    {
        ps.print("[T]");
    }


    public void visitObjectType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, ObjectType objectType)
    {
        ps.print("[a:" + clazz.getClassName(objectType.u2classIndex) + "]");
    }


    public void visitNullType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, NullType nullType)
    {
        ps.print("[n]");
    }


    public void visitUninitializedType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, UninitializedType uninitializedType)
    {
        ps.print("[u:" + uninitializedType.u2newInstructionOffset + "]");
    }


    public void visitUninitializedThisType(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, UninitializedThisType uninitializedThisType)
    {
        ps.print("[u:this]");
    }


    // Implementations for LineNumberInfoVisitor.

    public void visitLineNumberInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, LineNumberInfo lineNumberInfo)
    {
        println("[" + lineNumberInfo.u2startPC + "] -> line " +
                lineNumberInfo.u2lineNumber);
    }


    // Implementations for LocalVariableInfoVisitor.

    public void visitLocalVariableInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableInfo localVariableInfo)
    {
        println("#" + localVariableInfo.u2index + ": " +
                localVariableInfo.u2startPC + " -> " +
                (localVariableInfo.u2startPC + localVariableInfo.u2length) + " [" +
                clazz.getString(localVariableInfo.u2descriptorIndex) + " " +
                clazz.getString(localVariableInfo.u2nameIndex) + "]");
    }


    // Implementations for LocalVariableTypeInfoVisitor.

    public void visitLocalVariableTypeInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, LocalVariableTypeInfo localVariableTypeInfo)
    {
        println("#" + localVariableTypeInfo.u2index + ": " +
                localVariableTypeInfo.u2startPC + " -> " +
                (localVariableTypeInfo.u2startPC + localVariableTypeInfo.u2length) + " [" +
                clazz.getString(localVariableTypeInfo.u2signatureIndex) + " " +
                clazz.getString(localVariableTypeInfo.u2nameIndex) + "]");
    }


    // Implementations for AnnotationVisitor.

    public void visitAnnotation(Clazz clazz, Annotation annotation)
    {
        println(visitorInfo(annotation) +
                " Annotation [" + clazz.getString(annotation.u2typeIndex) + "]:");

        indent();
        annotation.elementValuesAccept(clazz, this);
        outdent();
    }


    // Implementations for ElementValueVisitor.

    public void visitConstantElementValue(Clazz clazz, Annotation annotation, ConstantElementValue constantElementValue)
    {
        println(visitorInfo(constantElementValue) +
                " Constant element value [" +
                (constantElementValue.u2elementNameIndex == 0 ? "(default)" :
                clazz.getString(constantElementValue.u2elementNameIndex)) + " '" +
                constantElementValue.u1tag + "']");

        indent();
        clazz.constantPoolEntryAccept(constantElementValue.u2constantValueIndex, this);
        outdent();
    }


    public void visitEnumConstantElementValue(Clazz clazz, Annotation annotation, EnumConstantElementValue enumConstantElementValue)
    {
        println(visitorInfo(enumConstantElementValue) +
                " Enum constant element value [" +
                (enumConstantElementValue.u2elementNameIndex == 0 ? "(default)" :
                clazz.getString(enumConstantElementValue.u2elementNameIndex)) + ", " +
                clazz.getString(enumConstantElementValue.u2typeNameIndex)  + ", " +
                clazz.getString(enumConstantElementValue.u2constantNameIndex) + "]");
    }


    public void visitClassElementValue(Clazz clazz, Annotation annotation, ClassElementValue classElementValue)
    {
        println(visitorInfo(classElementValue) +
                " Class element value [" +
                (classElementValue.u2elementNameIndex == 0 ? "(default)" :
                clazz.getString(classElementValue.u2elementNameIndex)) + ", " +
                clazz.getString(classElementValue.u2classInfoIndex) + "]");
    }


    public void visitAnnotationElementValue(Clazz clazz, Annotation annotation, AnnotationElementValue annotationElementValue)
    {
        println(visitorInfo(annotationElementValue) +
                " Annotation element value [" +
                (annotationElementValue.u2elementNameIndex == 0 ? "(default)" :
                clazz.getString(annotationElementValue.u2elementNameIndex)) + "]:");

        indent();
        annotationElementValue.annotationAccept(clazz, this);
        outdent();
    }


    public void visitArrayElementValue(Clazz clazz, Annotation annotation, ArrayElementValue arrayElementValue)
    {
        println(visitorInfo(arrayElementValue) +
                " Array element value [" +
                (arrayElementValue.u2elementNameIndex == 0 ? "(default)" :
                clazz.getString(arrayElementValue.u2elementNameIndex)) + "]:");

        indent();
        arrayElementValue.elementValuesAccept(clazz, annotation, this);
        outdent();
    }


    // Small utility methods.

    private void indent()
    {
        indentation++;
    }

    private void outdent()
    {
        indentation--;
    }

    private void println(String string)
    {
        print(string);
        println();

    }

    private void print(String string)
    {
        for (int index = 0; index < indentation; index++)
        {
            ps.print(INDENTATION);
        }

        ps.print(string);
    }

    private void println()
    {
        ps.println();
    }


    private String visitorInfo(VisitorAccepter visitorAccepter)
    {
        return visitorAccepter.getVisitorInfo() == null ? "-" : "+";
    }
}
