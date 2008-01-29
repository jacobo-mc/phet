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

import proguard.classfile.constant.*;
import proguard.classfile.instruction.*;
import proguard.classfile.util.InstructionSequenceMatcher;

/**
 * This class contains a set of instruction sequences and their suggested
 * replacements.
 *
 * @see InstructionSequencesReplacer
 * @author Eric Lafortune
 */
public class InstructionSequenceConstants
{
    public static final int X = InstructionSequenceMatcher.X;
    public static final int Y = InstructionSequenceMatcher.Y;
    public static final int Z = InstructionSequenceMatcher.Z;

    public static final int A = InstructionSequenceMatcher.A;
    public static final int B = InstructionSequenceMatcher.B;
    public static final int C = InstructionSequenceMatcher.C;
    public static final int D = InstructionSequenceMatcher.D;


    private static final int I_32768              =  0;
    private static final int I_65536              =  1;
    private static final int I_16777216           =  2;

//  private static final int I_0x000000ff
    private static final int I_0x0000ff00         =  3;
    private static final int I_0x00ff0000         =  4;
    private static final int I_0xff000000         =  5;
    private static final int I_0x0000ffff         =  6;
    private static final int I_0xffff0000         =  7;

    private static final int L_M1                 =  8;
    private static final int L_2                  =  9;
    private static final int L_4                  = 10;
    private static final int L_8                  = 11;
    private static final int L_16                 = 12;
    private static final int L_32                 = 13;
    private static final int L_64                 = 14;
    private static final int L_128                = 15;
    private static final int L_256                = 16;
    private static final int L_512                = 17;
    private static final int L_1024               = 18;
    private static final int L_2048               = 19;
    private static final int L_4096               = 20;
    private static final int L_8192               = 21;
    private static final int L_16384              = 22;
    private static final int L_32768              = 23;
    private static final int L_65536              = 24;
    private static final int L_16777216           = 25;
    private static final int L_4294967296         = 26;

    private static final int L_0x00000000ffffffff = 27;
    private static final int L_0xffffffff00000000 = 28;

    private static final int F_M1                 = 29;

    private static final int D_M1                 = 30;

    private static final int FIELD_I              = 31;
    private static final int FIELD_L              = 32;
    private static final int FIELD_F              = 33;
    private static final int FIELD_D              = 34;

    private static final int NAME_AND_TYPE_I      = 35;
    private static final int NAME_AND_TYPE_L      = 36;
    private static final int NAME_AND_TYPE_F      = 37;
    private static final int NAME_AND_TYPE_D      = 38;

    private static final int TYPE_I               = 39;
    private static final int TYPE_L               = 40;
    private static final int TYPE_F               = 41;
    private static final int TYPE_D               = 42;


    public static final Constant[] PATTERN_CONSTANTS = new Constant[]
    {
        new IntegerConstant(32768),
        new IntegerConstant(65536),
        new IntegerConstant(16777216),

        new IntegerConstant(0x0000ff00),
        new IntegerConstant(0x00ff0000),
        new IntegerConstant(0xff000000),
        new IntegerConstant(0x0000ffff),
        new IntegerConstant(0xffff0000),

        new LongConstant(-1L),
        new LongConstant(2L),
        new LongConstant(4L),
        new LongConstant(8L),
        new LongConstant(16L),
        new LongConstant(32L),
        new LongConstant(64L),
        new LongConstant(128L),
        new LongConstant(256L),
        new LongConstant(512L),
        new LongConstant(1024L),
        new LongConstant(2048L),
        new LongConstant(4096L),
        new LongConstant(8192L),
        new LongConstant(16384L),
        new LongConstant(32768L),
        new LongConstant(65536L),
        new LongConstant(16777216L),
        new LongConstant(4294967296L),

        new LongConstant(0x00000000ffffffffL),
        new LongConstant(0xffffffff00000000L),

        new FloatConstant(-1f),

        new DoubleConstant(-1d),

        new FieldrefConstant(X, NAME_AND_TYPE_I, null, null),
        new FieldrefConstant(X, NAME_AND_TYPE_L, null, null),
        new FieldrefConstant(X, NAME_AND_TYPE_F, null, null),
        new FieldrefConstant(X, NAME_AND_TYPE_D, null, null),

        new NameAndTypeConstant(Y, TYPE_I),
        new NameAndTypeConstant(Y, TYPE_L),
        new NameAndTypeConstant(Y, TYPE_F),
        new NameAndTypeConstant(Y, TYPE_D),

        new Utf8Constant("I"),
        new Utf8Constant("J"),
        new Utf8Constant("F"),
        new Utf8Constant("D"),
    };


    public static final Instruction[][][] INSTRUCTION_SEQUENCES =
        new Instruction[][][]
    {
        {   // nop = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_NOP),
            },{
                // Nothing.
            },
        },
        {   // i = i = nothing
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                // Nothing.
            },
        },
        {   // l = l = nothing
            {
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
            },{
                // Nothing.
            },
        },
        {   // f = f = nothing
            {
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
            },{
                // Nothing.
            },
        },
        {   // d = d = nothing
            {
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
            },{
                // Nothing.
            },
        },
        {   // a = a = nothing
            {
                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
            },{
                // Nothing.
            },
        },
        {   // istore/istore = pop/istore
            {
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },
        },
        {   // lstore/lstore = pop2/lstore
            {
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
            },
        },
        {   // fstore/fstore = pop/fstore
            {
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
            },
        },
        {   // dstore/dstore = pop2/dstore
            {
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
            },
        },
        {   // astore/astore = pop/astore
            {
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
            },
        },
        {   // istore/iload = dup/istore
            {
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },
        },
        {   // lstore/lload = dup2/lstore
            {
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP2),
                new VariableInstruction(InstructionConstants.OP_LSTORE, X),
            },
        },
        {   // fstore/fload = dup/fstore
            {
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP),
                new VariableInstruction(InstructionConstants.OP_FSTORE, X),
            },
        },
        {   // dstore/dload = dup2/dstore
            {
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP2),
                new VariableInstruction(InstructionConstants.OP_DSTORE, X),
            },
        },
        {   // astore/aload = dup/astore
            {
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP),
                new VariableInstruction(InstructionConstants.OP_ASTORE, X),
            },
        },
        {   // c + i = i + c
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },
        },
        {   // b + i = i + b
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },
        },
        {   // s + i = i + s
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },
        },
        {   // c + i = i + c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },
        },
        {   // c * i = i * c
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },
        },
        {   // b * i = i * b
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },
        },
        {   // s * i = i * s
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },
        },
        {   // c * i = i * c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },
        },
        {   // c + l = l + c
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },{
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },
        },
        {   // c + l = l + c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },{
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },
        },
        {   // c * l = l * c
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_LLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },
        },
        {   // c + f = f + c
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },{
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_FCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },
        },
        {   // c + f = f + c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },{
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },
        },
        {   // c * f = f * c
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_FMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_FCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_FMUL),
            },
        },
        {   // c * f = f * c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_FLOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC, A),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },
        },
        {   // c + d = d + c
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },{
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },
        },
        {   // c + d = d + c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },{
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },
        },
        {   // c * d = d * c
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_0, A),
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DCONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },
        },
        {   // c * d = d * c
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },{
                new VariableInstruction(InstructionConstants.OP_DLOAD, X),
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, A),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },
        },
        {   // i = i + c = i += c
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, A),
            },
        },
        {   // i = i + b = i += b
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, A),
            },
        },
        {   // i = i + s = i += s
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new SimpleInstruction(InstructionConstants.OP_IADD),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, A),
            },
        },
        {   // i = i - -1 = i++
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, 1),
            },
        },
        {   // i = i - 1 = i--
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, -1),
            },
        },
        {   // i = i - 2 = i -= 2
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, -2),
            },
        },
        {   // i = i - 3 = i -= 3
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, -3),
            },
        },
        {   // i = i - 4 = i -= 4
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_4),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, -4),
            },
        },
        {   // i = i - 5 = i -= 5
            {
                new VariableInstruction(InstructionConstants.OP_ILOAD, X),
                new SimpleInstruction(InstructionConstants.OP_ICONST_5),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
                new VariableInstruction(InstructionConstants.OP_ISTORE, X),
            },{
                new VariableInstruction(InstructionConstants.OP_IINC, X, -5),
            },
        },
        {   // ... + 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                // Nothing.
            },
        },
        {   // ... + 0L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },{
                // Nothing.
            },
        },
        {   // ... + 0f = ...
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_0),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },{
                // Nothing.
            },
        },
        {   // ... + 0d = ...
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_0),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },{
                // Nothing.
            },
        },
        {   // ... - 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_ISUB),
            },{
                // Nothing.
            },
        },
        {   // ... - 0L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LSUB),
            },{
                // Nothing.
            },
        },
        {   // ... - 0f = ...
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_0),
                new SimpleInstruction(InstructionConstants.OP_FSUB),
            },{
                // Nothing.
            },
        },
        {   // ... - 0d = ...
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_0),
                new SimpleInstruction(InstructionConstants.OP_DSUB),
            },{
                // Nothing.
            },
        },
        {   // ... * -1 = -...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_INEG),
            },
        },
        {   // ... * 0 = 0
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
            },
        },
        {   // ... * 1 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                // Nothing.
            },
        },
        {   // ... * 2 = ... << 1
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 4 = ... << 2
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_4),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 8 = ... << 3
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 16 = ... << 4
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 4),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 32 = ... << 5
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 5),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 64 = ... << 6
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 64),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 6),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 128 = ... << 7
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 128),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 7),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 256 = ... << 8
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 256),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 512 = ... << 9
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 512),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 9),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 1024 = ... << 10
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 1024),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 10),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 2048 = ... << 11
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 2048),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 11),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 4096 = ... << 12
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 4096),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 12),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 8192 = ... << 13
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 8192),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 13),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 16384 = ... << 14
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 16384),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 14),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 32768 = ... << 15
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_32768),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 15),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 65536 = ... << 16
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_65536),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * 16777216 = ... << 24
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_16777216),
                new SimpleInstruction(InstructionConstants.OP_IMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },
        },
        {   // ... * -1L = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_M1),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_LNEG),
            },
        },
        {   // ... * 0L = 0L
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
            },
        },
        {   // ... * 1L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_1),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                // Nothing.
            },
        },
        {   // ... * 2L = ... << 1
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_2),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 4L = ... << 2
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 8L = ... << 3
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_8),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 16L = ... << 4
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 4),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 32L = ... << 5
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_32),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 5),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 64L = ... << 6
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_64),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 6),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 128L = ... << 7
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_128),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 7),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 256L = ... << 8
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_256),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 512L = ... << 9
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_512),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 9),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 1024L = ... << 10
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_1024),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 10),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 2048L = ... << 11
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_2048),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 11),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 4096L = ... << 12
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4096),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 12),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 8192L = ... << 13
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_8192),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 13),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 16384L = ... << 14
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16384),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 14),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 32768L = ... << 15
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_32768),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 15),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 65536LL = ... << 16
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_65536),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 16777216L = ... << 24
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16777216),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * 4294967296L = ... << 32
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4294967296),
                new SimpleInstruction(InstructionConstants.OP_LMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },
        },
        {   // ... * -1f = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, F_M1),
                new SimpleInstruction(InstructionConstants.OP_FMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_FNEG),
            },
        },
//        {   // ... * 0f = 0f (or NaN)
//            {
//                new SimpleInstruction(InstructionConstants.OP_FCONST_0),
//                new SimpleInstruction(InstructionConstants.OP_FMUL),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_POP),
//                new SimpleInstruction(InstructionConstants.OP_FCONST_0),
//            },
//        },
        {   // ... * 1f = ...
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_1),
                new SimpleInstruction(InstructionConstants.OP_FMUL),
            },{
                // Nothing.
            },
        },
        {   // ... * -1d = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, D_M1),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },{
                new SimpleInstruction(InstructionConstants.OP_DNEG),
            },
        },
//        {   // ... * 0d = 0d (or NaN)
//            {
//                new SimpleInstruction(InstructionConstants.OP_DCONST_0),
//                new SimpleInstruction(InstructionConstants.OP_DMUL),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_POP2),
//                new SimpleInstruction(InstructionConstants.OP_DCONST_0),
//            },
//        },
        {   // ... * 1d = ...
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_1),
                new SimpleInstruction(InstructionConstants.OP_DMUL),
            },{
                // Nothing.
            },
        },
        {   // ... / -1 = -...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
                new SimpleInstruction(InstructionConstants.OP_IDIV),
            },{
                new SimpleInstruction(InstructionConstants.OP_INEG),
            },
        },
        {   // ... / 1 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_IDIV),
            },{
                // Nothing.
            },
        },
//        {   // ... / 2 = ... >> 1
//            {
//                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 4 = ... >> 2
//            {
//                new SimpleInstruction(InstructionConstants.OP_ICONST_4),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 8 = ... >> 3
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 16 = ... >> 4
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 4),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 32 = ... >> 5
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 5),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 64 = ... >> 6
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 64),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 6),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 128 = ... >> 7
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 128),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 7),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 256 = ... >> 8
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 256),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 512 = ... >> 9
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 512),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 9),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 1024 = ... >> 10
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 1024),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 10),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 2048 = ... >> 11
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 2048),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 11),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 4096 = ... >> 12
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 4096),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 12),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 8192 = ... >> 13
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 8192),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 13),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 16384 = ... >> 14
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 16384),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 14),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 32768 = ... >> 15
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC, I_32768),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 15),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 65536 = ... >> 16
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC, I_65536),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
//        {   // ... / 16777216 = ... >> 24
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC, I_16777216),
//                new SimpleInstruction(InstructionConstants.OP_IDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
//                new SimpleInstruction(InstructionConstants.OP_ISHR),
//            },
//        },
        {   // ... / -1L = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_M1),
                new SimpleInstruction(InstructionConstants.OP_LDIV),
            },{
                new SimpleInstruction(InstructionConstants.OP_LNEG),
            },
        },
        {   // ... / 1L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_1),
                new SimpleInstruction(InstructionConstants.OP_LDIV),
            },{
                // Nothing.
            },
        },
//        {   // ... / 2L = ... >> 1
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_2),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 4L = ... >> 2
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 8L = ... >> 3
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_8),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 16L = ... >> 4
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 4),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 32L = ... >> 5
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_32),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 5),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 64L = ... >> 6
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_64),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 6),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 128L = ... >> 7
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_128),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 7),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 256L = ... >> 8
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_256),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 512L = ... >> 9
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_512),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 9),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 1024L = ... >> 10
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_1024),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 10),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 2048L = ... >> 11
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_2048),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 11),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 4096L = ... >> 12
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4096),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 12),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 8192L = ... >> 13
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_8192),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 13),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 16384L = ... >> 14
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16384),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 14),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 32768L = ... >> 15
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_32768),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 15),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 65536LL = ... >> 16
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_65536),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 16777216L = ... >> 24
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_16777216),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
//        {   // ... / 4294967296L = ... >> 32
//            {
//                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_4294967296),
//                new SimpleInstruction(InstructionConstants.OP_LDIV),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
//                new SimpleInstruction(InstructionConstants.OP_LSHR),
//            },
//        },
        {   // ... / -1f = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, F_M1),
                new SimpleInstruction(InstructionConstants.OP_FDIV),
            },{
                new SimpleInstruction(InstructionConstants.OP_FNEG),
            },
        },
        {   // ... / 1f = ...
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_1),
                new SimpleInstruction(InstructionConstants.OP_FDIV),
            },{
                // Nothing.
            },
        },
        {   // ... / -1d = -...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, D_M1),
                new SimpleInstruction(InstructionConstants.OP_DDIV),
            },{
                new SimpleInstruction(InstructionConstants.OP_DNEG),
            },
        },
        {   // ... / 1d = ...
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_1),
                new SimpleInstruction(InstructionConstants.OP_DDIV),
            },{
                // Nothing.
            },
        },
        {   // ... % 1 = 0
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
                new SimpleInstruction(InstructionConstants.OP_IREM),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
            },
        },
//        {   // ... % 2 = ... & 0x1
//            {
//                new SimpleInstruction(InstructionConstants.OP_ICONST_2),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_1),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 4 = ... & 0x3
//            {
//                new SimpleInstruction(InstructionConstants.OP_ICONST_4),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_ICONST_3),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 8 = ... & 0x07
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 0x07),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 16 = ... & 0x0f
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 0x0f),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 32 = ... & 0x1f
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 0x1f),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 64 = ... & 0x3f
//            {
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 64),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 0x3f),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 128 = ... & 0x7f
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 128),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 0x7f),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 256 = ... & 0x00ff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 256),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x00ff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 512 = ... & 0x01ff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 512),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x01ff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 1024 = ... & 0x03ff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 1024),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x03ff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 2048 = ... & 0x07ff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 2048),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x07ff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 4096 = ... & 0x0fff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 4096),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x0fff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 8192 = ... & 0x1fff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 8192),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x1fff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
//        {   // ... % 16384 = ... & 0x3fff
//            {
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 16384),
//                new SimpleInstruction(InstructionConstants.OP_IREM),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0x3fff),
//                new SimpleInstruction(InstructionConstants.OP_IAND),
//            },
//        },
        {   // ... % 1L = 0L
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_1),
                new SimpleInstruction(InstructionConstants.OP_LREM),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
            },
        },
        {   // ... % 1f = 0f
            {
                new SimpleInstruction(InstructionConstants.OP_FCONST_1),
                new SimpleInstruction(InstructionConstants.OP_FREM),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new SimpleInstruction(InstructionConstants.OP_FCONST_0),
            },
        },
        {   // ... % 1d = 0d
            {
                new SimpleInstruction(InstructionConstants.OP_DCONST_1),
                new SimpleInstruction(InstructionConstants.OP_DREM),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new SimpleInstruction(InstructionConstants.OP_DCONST_0),
            },
        },
        {   // -(-...) = ...
            {
                new SimpleInstruction(InstructionConstants.OP_INEG),
                new SimpleInstruction(InstructionConstants.OP_INEG),
            },{
                // Nothing.
            },
        },
        {   // -(-...) = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LNEG),
                new SimpleInstruction(InstructionConstants.OP_LNEG),
            },{
                // Nothing.
            },
        },
        {   // -(-...) = ...
            {
                new SimpleInstruction(InstructionConstants.OP_FNEG),
                new SimpleInstruction(InstructionConstants.OP_FNEG),
            },{
                // Nothing.
            },
        },
        {   // -(-...) = ...
            {
                new SimpleInstruction(InstructionConstants.OP_DNEG),
                new SimpleInstruction(InstructionConstants.OP_DNEG),
            },{
                // Nothing.
            },
        },
        {   // +(-...) = -...
            {
                new SimpleInstruction(InstructionConstants.OP_INEG),
                new SimpleInstruction(InstructionConstants.OP_IADD),
            },{
                new SimpleInstruction(InstructionConstants.OP_ISUB),
            },
        },
        {   // +(-...) = -...
            {
                new SimpleInstruction(InstructionConstants.OP_LNEG),
                new SimpleInstruction(InstructionConstants.OP_LADD),
            },{
                new SimpleInstruction(InstructionConstants.OP_LSUB),
            },
        },
        {   // +(-...) = -...
            {
                new SimpleInstruction(InstructionConstants.OP_FNEG),
                new SimpleInstruction(InstructionConstants.OP_FADD),
            },{
                new SimpleInstruction(InstructionConstants.OP_FSUB),
            },
        },
        {   // +(-...) = -...
            {
                new SimpleInstruction(InstructionConstants.OP_DNEG),
                new SimpleInstruction(InstructionConstants.OP_DADD),
            },{
                new SimpleInstruction(InstructionConstants.OP_DSUB),
            },
        },
        {   // ... << 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
            },{
                // Nothing.
            },
        },
        {   // ... << 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
            },{
                // Nothing.
            },
        },
        {   // ... >> 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                // Nothing.
            },
        },
        {   // ... >> 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_LSHR),
            },{
                // Nothing.
            },
        },
        {   // ... >>> 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },{
                // Nothing.
            },
        },
        {   // ... >>> 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_LUSHR),
            },{
                // Nothing.
            },
        },
        {   // ... & -1 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },{
                // Nothing.
            },
        },
        {   // ... & 0 = 0
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
            },
        },
        {   // ... & -1L = ...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_M1),
                new SimpleInstruction(InstructionConstants.OP_LAND),
            },{
                // Nothing.
            },
        },
        {   // ... & 0L = 0L
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LAND),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
            },
        },
        {   // ... | -1 = -1
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
                new SimpleInstruction(InstructionConstants.OP_IOR),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new SimpleInstruction(InstructionConstants.OP_ICONST_M1),
            },
        },
        {   // ... | 0 = ...
            {
               new SimpleInstruction(InstructionConstants.OP_ICONST_0),
               new SimpleInstruction(InstructionConstants.OP_IOR),
           },{
                // Nothing.
            },
        },
        {   // ... | -1L = -1L
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_M1),
                new SimpleInstruction(InstructionConstants.OP_LAND),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_M1),
            },
        },
        {   // ... | 0L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LOR),
            },{
                // Nothing.
            },
        },
        {   // ... ^ 0 = ...
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new SimpleInstruction(InstructionConstants.OP_IXOR),
            },{
                // Nothing.
            },
        },
        {   // ... ^ 0L = ...
            {
                new SimpleInstruction(InstructionConstants.OP_LCONST_0),
                new SimpleInstruction(InstructionConstants.OP_LXOR),
            },{
                // Nothing.
            },
        },
        {   // (... & 0x0000ff00) >> 8 = (... >> 8) & 0xff
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x0000ff00),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },
        },
        {   // (... & 0x0000ff00) >>> 8 = (... >>> 8) & 0xff
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x0000ff00),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 8),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },
        },
        {   // (... & 0x00ff0000) >> 16 = (... >> 16) & 0xff
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x00ff0000),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },
        },
        {   // (... & 0x00ff0000) >>> 16 = (... >>> 16) & 0xff
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x00ff0000),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },
        },
        {   // (... & 0xff000000) >> 24 = ... >> 24
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0xff000000),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // (... & 0xffff0000) >> 16 = ... >> 16
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0xffff0000),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // (... & 0xffff0000) >>> 16 = ... >>> 16
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0xffff0000),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },
        },
        {   // (... >> 24) & 0xff = ... >>> 24
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },
        },
        {   // (... >>> 24) & 0xff = ... >>> 24
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },
        },
        {   // (byte)(... & 0x000000ff) = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, 0xff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // (char)(... & 0x0000ffff) = (char)...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x0000ffff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },
        },
        {   // (short)(... & 0x0000ffff) = (short)...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC, I_0x0000ffff),
                new SimpleInstruction(InstructionConstants.OP_IAND),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },
        },
        {   // (byte)(... >> 24) = ... >> 24
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // (byte)(... >>> 24) = ... >> 24
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // (char)(... >> 16) = ... >>> 16
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },
        },
        {   // (char)(... >>> 16) = ... >>> 16
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },
        },
        {   // (short)(... >> 16) = ... >> 16
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // (short)(... >>> 16) = ... >> 16
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },
        },
        {   // ... << 24 >> 24 = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 24),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // ... << 16 >>> 16 = (char)...
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_IUSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },
        },
        {   // ... << 16 >> 16 = (short)...
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHL),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 16),
                new SimpleInstruction(InstructionConstants.OP_ISHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },
        },
        {   // ... << 32 >> 32 = (long)(int)...
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LSHL),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_L2I),
                new SimpleInstruction(InstructionConstants.OP_I2L),
            },
        },
        {   // (int)(... & 0x00000000ffffffffL) = (int)...
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_0x00000000ffffffff),
                new SimpleInstruction(InstructionConstants.OP_LAND),
                new SimpleInstruction(InstructionConstants.OP_L2I),
            },{
                new SimpleInstruction(InstructionConstants.OP_L2I),
            },
        },
        {   // (... & 0xffffffff00000000L) >> 32 = ... >> 32
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_0xffffffff00000000),
                new SimpleInstruction(InstructionConstants.OP_LAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LSHR),
            },
        },
        {   // (... & 0xffffffff00000000L) >>> 32 = ... >>> 32
            {
                new ConstantInstruction(InstructionConstants.OP_LDC2_W, L_0xffffffff00000000),
                new SimpleInstruction(InstructionConstants.OP_LAND),
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LUSHR),
            },{
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, 32),
                new SimpleInstruction(InstructionConstants.OP_LUSHR),
            },
        },
        {   // ... += 0 = nothing
            {
                new VariableInstruction(InstructionConstants.OP_IINC, X, 0),
            },{
                // Nothing.
            },
        },
        {   // getfield/putfield = nothing
            {
                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
                new ConstantInstruction(InstructionConstants.OP_GETFIELD, Y),
                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
            },{
                // Nothing.
            },
        },
//        {   // putfield_L/putfield_L = pop2_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_L),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_L),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_POP2),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_L),
//            },
//        },
//        {   // putfield_D/putfield_D = pop2_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_D),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_D),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_POP2),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_D),
//            },
//        },
//        {   // putfield/putfield = pop_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_POP),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
//            },
//        },
//        {   // putfield_L/getfield_L = dup2_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_L),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                new ConstantInstruction(InstructionConstants.OP_GETFIELD, FIELD_L),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_DUP2_X1),
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_L),
//            },
//        },
//        {   // putfield_D/getfield_D = dup2_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_D),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                new ConstantInstruction(InstructionConstants.OP_GETFIELD, FIELD_D),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_DUP2_X1),
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, FIELD_D),
//            },
//        },
//        {   // putfield/getfield = dup_x1/putfield
//            {
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                new ConstantInstruction(InstructionConstants.OP_GETFIELD, Y),
//            },{
//                new VariableInstruction(InstructionConstants.OP_ALOAD, X),
//                // ...
//                new SimpleInstruction(InstructionConstants.OP_DUP_X1),
//                new ConstantInstruction(InstructionConstants.OP_PUTFIELD, Y),
//            },
//        },
        {   // getstatic/putstatic = nothing
            {
                new ConstantInstruction(InstructionConstants.OP_GETSTATIC, X),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
            },{
                // Nothing.
            },
        },
        {   // putstatic_L/putstatic_L = pop2/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_L),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_L),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_L),
            },
        },
        {   // putstatic_D/putstatic_D = pop2/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_D),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_D),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_D),
            },
        },
        {   // putstatic/putstatic = pop/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
            },
        },
        {   // putstatic_L/getstatic_L = dup2/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_L),
                new ConstantInstruction(InstructionConstants.OP_GETSTATIC, FIELD_L),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP2),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_L),
            },
        },
        {   // putstatic_D/getstatic_D = dup2/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_D),
                new ConstantInstruction(InstructionConstants.OP_GETSTATIC, FIELD_D),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP2),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, FIELD_D),
            },
        },
        {   // putstatic/getstatic = dup/putstatic
            {
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
                new ConstantInstruction(InstructionConstants.OP_GETSTATIC, X),
            },{
                new SimpleInstruction(InstructionConstants.OP_DUP),
                new ConstantInstruction(InstructionConstants.OP_PUTSTATIC, X),
            },
        },
        {   // (byte)(byte)... = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2B),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // (byte)(char)... = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2C),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // (byte)(short)... = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2S),
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // (char)(char)... = (char)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2C),
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },
        },
        {   // (char)(short)... = (char)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2S),
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2C),
            },
        },
        {   // (short)(byte)... = (byte)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2B),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2B),
            },
        },
        {   // (short)(char)... = (short)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2C),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },
        },
        {   // (short)(short)... = (short)...
            {
                new SimpleInstruction(InstructionConstants.OP_I2S),
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },{
                new SimpleInstruction(InstructionConstants.OP_I2S),
            },
        },
        {   // (int)(long)... = ...
            {
                new SimpleInstruction(InstructionConstants.OP_I2L),
                new SimpleInstruction(InstructionConstants.OP_L2I),
            },{
                // Nothing.
            },
        },
        // Not handled correctly in all cases by VMs prior to Java 6...
//        {   // (byte)bytes[...] = bytes[...]
//            {
//                new SimpleInstruction(InstructionConstants.OP_BALOAD),
//                new SimpleInstruction(InstructionConstants.OP_I2B),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BALOAD),
//            },
//        },
//        {   // (short)bytes[...] = bytes[...]
//            {
//                 new SimpleInstruction(InstructionConstants.OP_BALOAD),
//                 new SimpleInstruction(InstructionConstants.OP_I2S),
//             },{
//                new SimpleInstruction(InstructionConstants.OP_BALOAD),
//            },
//        },
//        {   // (char)chars[...] = chars[...]
//            {
//                new SimpleInstruction(InstructionConstants.OP_CALOAD),
//                new SimpleInstruction(InstructionConstants.OP_I2C),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_CALOAD),
//            },
//        },
//        {   // (short)shorts[...] = shorts[...]
//            {
//                new SimpleInstruction(InstructionConstants.OP_SALOAD),
//                new SimpleInstruction(InstructionConstants.OP_I2S),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SALOAD),
//            },
//        },
//        {   // bytes[...] = (byte)... = bytes[...] = ...
//            {
//                new SimpleInstruction(InstructionConstants.OP_I2B),
//                new SimpleInstruction(InstructionConstants.OP_BASTORE),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_BASTORE),
//            },
//        },
//        {   // chars[...] = (char)... = chars[...] = ...
//            {
//                new SimpleInstruction(InstructionConstants.OP_I2C),
//                new SimpleInstruction(InstructionConstants.OP_CASTORE),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_CASTORE),
//            },
//        },
//        {   // shorts[...] = (short)... = shorts[...] = ...
//            {
//                new SimpleInstruction(InstructionConstants.OP_I2S),
//                new SimpleInstruction(InstructionConstants.OP_SASTORE),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SASTORE),
//            },
//        },
        {   // goto +3 = nothing
            {
                new BranchInstruction(InstructionConstants.OP_GOTO, 3),
            },{
                // Nothing.
            },
        },
        {   // ifeq +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFEQ, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ifne +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFNE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // iflt +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFLT, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ifge +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFGE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ifgt +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFGT, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ifle +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFLE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ificmpeq +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPEQ, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ificmpne +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPNE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ificmplt +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPLT, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ificmpge +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPGE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ificmpgt +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPGT, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ificmple +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPLE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ifacmpeq +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFACMPEQ, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ifacmpne +3 = pop2
            {
                new BranchInstruction(InstructionConstants.OP_IFACMPNE, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP2),
            },
        },
        {   // ifnull +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFNULL, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // ifnonnull +3 = pop
            {
                new BranchInstruction(InstructionConstants.OP_IFNONNULL, 3),
            },{
                new SimpleInstruction(InstructionConstants.OP_POP),
            },
        },
        {   // if (... == 0) = ifeq
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPEQ, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },
        },
        {   // if (... != 0) = ifne
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPNE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },
        },
        {   // if (... < 0) = iflt
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPLT, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFLT, X),
            },
        },
        {   // if (... >= 0) = ifge
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPGE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFGE, X),
            },
        },
        {   // if (... > 0) = ifgt
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPGT, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFGT, X),
            },
        },
        {   // if (... <= 0) = ifle
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFICMPLE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFLE, X),
            },
        },
        {   // if (... == null) = ifnull
            {
                new SimpleInstruction(InstructionConstants.OP_ACONST_NULL),
                new BranchInstruction(InstructionConstants.OP_IFACMPEQ, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNULL, X),
            },
        },
        {   // if (... != null) = ifnonnull
            {
                new SimpleInstruction(InstructionConstants.OP_ACONST_NULL),
                new BranchInstruction(InstructionConstants.OP_IFACMPNE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNONNULL, X),
            },
        },
        {   // iconst_0/ifeq = goto
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // iconst/ifeq = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },{
                // Nothing.
            },
        },
        {   // bipush/ifeq = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },{
                // Nothing.
            },
        },
        {   // sipush/ifeq = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },{
                // Nothing.
            },
        },
        {   // iconst_0/ifne = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },{
                // Nothing.
            },
        },
        {   // iconst/ifne = goto
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0, A),
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // bipush/ifne = goto
            {
                new SimpleInstruction(InstructionConstants.OP_BIPUSH, A),
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // sipush/ifne = goto
            {
                new SimpleInstruction(InstructionConstants.OP_SIPUSH, A),
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // iconst_0/iflt = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFLT, X),
            },{
                // Nothing.
            },
        },
        {   // iconst_0/ifge = goto
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFGE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // iconst_0/ifgt = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFGT, X),
            },{
                // Nothing.
            },
        },
        {   // iconst_0/ifle = goto
            {
                new SimpleInstruction(InstructionConstants.OP_ICONST_0),
                new BranchInstruction(InstructionConstants.OP_IFLE, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // aconst_null/ifnull = goto
            {
                new SimpleInstruction(InstructionConstants.OP_ACONST_NULL),
                new BranchInstruction(InstructionConstants.OP_IFNULL, X),
            },{
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },
        },
        {   // aconst_null/ifnonnul = nothing
            {
                new SimpleInstruction(InstructionConstants.OP_ACONST_NULL),
                new BranchInstruction(InstructionConstants.OP_IFNONNULL, X),
            },{
                // Nothing.
            },
        },
        {   // ifeq/goto = ifne
            {
                new BranchInstruction(InstructionConstants.OP_IFEQ, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNE, X),
            },
        },
        {   // ifne/goto = ifeq
            {
                new BranchInstruction(InstructionConstants.OP_IFNE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFEQ, X),
            },
        },
        {   // iflt/goto = ifge
            {
                new BranchInstruction(InstructionConstants.OP_IFLT, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFGE, X),
            },
        },
        {   // ifge/goto = iflt
            {
                new BranchInstruction(InstructionConstants.OP_IFGE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFLT, X),
            },
        },
        {   // ifgt/goto = ifle
            {
                new BranchInstruction(InstructionConstants.OP_IFGT, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFLE, X),
            },
        },
        {   // ifle/goto = ifgt
            {
                new BranchInstruction(InstructionConstants.OP_IFLE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFGT, X),
            },
        },
        {   // ificmpeq/goto = ificmpne
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPEQ, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPNE, X),
            },
        },
        {   // ificmpne/goto = ificmpeq
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPNE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPEQ, X),
            },
        },
        {   // ificmplt/goto = ificmpge
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPLT, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPGE, X),
            },
        },
        {   // ificmpge/goto = ificmplt
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPGE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPLT, X),
            },
        },
        {   // ificmpgt/goto = ificmple
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPGT, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPLE, X),
            },
        },
        {   // ificmple/goto = ificmpgt
            {
                new BranchInstruction(InstructionConstants.OP_IFICMPLE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFICMPGT, X),
            },
        },
        {   // ifacmpeq/goto = ifacmpne
            {
                new BranchInstruction(InstructionConstants.OP_IFACMPEQ, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFACMPNE, X),
            },
        },
        {   // ifacmpne/goto = ifacmpeq
            {
                new BranchInstruction(InstructionConstants.OP_IFACMPNE, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFACMPEQ, X),
            },
        },
        {   // ifnull/goto = ifnonnull
            {
                new BranchInstruction(InstructionConstants.OP_IFNULL, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNONNULL, X),
            },
        },
        {   // ifnonnull/goto = ifnull
            {
                new BranchInstruction(InstructionConstants.OP_IFNONNULL, 6),
                new BranchInstruction(InstructionConstants.OP_GOTO, X),
            },{
                new BranchInstruction(InstructionConstants.OP_IFNULL, X),
            },
        },
//        {   // switch (...) { default: ... } = pop/goto ...
//            {
//                new TableSwitchInstruction(InstructionConstants.OP_TABLESWITCH, A, X, Y, 0, new int[0]),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_POP),
//                new BranchInstruction(InstructionConstants.OP_GOTO, A),
//            },
//        },
//        {   // switch (...) { default: ... } = pop/goto ...
//            {
//                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, 0, new int[0], new int[0]),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_POP),
//                new BranchInstruction(InstructionConstants.OP_GOTO, A),
//            },
//        },
        {   // switch (...) { case/case/default: ... } = switch (...) { case/default: ... }
            {
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, new int[] { X, Y }, new int[] { A, B }),
            },{
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, new int[] { Y }, new int[] { B }),
            },
        },
        {   // switch (...) { case/case/default: ... } = switch (...) { case/default: ... }
            {
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, B, new int[] { X, Y }, new int[] { A, B }),
            },{
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, B, new int[] { X }, new int[] { A }),
            },
        },
        {   // switch (...) { case/case/case/default: ... } = switch (...) { case/case/default: ... }
            {
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, new int[] { X, Y, Z }, new int[] { A, B, C }),
            },{
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, new int[] { Y, Z }, new int[] { B, C }),
            },
        },
        {   // switch (...) { case/case/case/default: ... } = switch (...) { case/case/default: ... }
            {
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, B, new int[] { X, Y, Z }, new int[] { A, B, C }),
            },{
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, B, new int[] { X, Z }, new int[] { A, C }),
            },
        },
        {   // switch (...) { case/case/case/default: ... } = switch (...) { case/case/default: ... }
            {
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, C, new int[] { X, Y, Z }, new int[] { A, B, C }),
            },{
                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, C, new int[] { X, Y }, new int[] { A, B }),
            },
        },
//        {   // switch (...) { case ...: ...  default:  ... }
//            // = if (... == ...) ... else ...
//            {
//                new TableSwitchInstruction(InstructionConstants.OP_TABLESWITCH, A, X, Y, 1, new int[] { B }),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, X),
//                new BranchInstruction(InstructionConstants.OP_IFICMPNE, A),
//                new BranchInstruction(InstructionConstants.OP_GOTO, B),
//            },
//        },
//        {   // switch (...) { case ...: ...  default:  ... }
//            // = if (... == ...) ... else ...
//            {
//                new LookUpSwitchInstruction(InstructionConstants.OP_LOOKUPSWITCH, A, 1, new int[] { X }, new int[] { B }),
//            },{
//                new SimpleInstruction(InstructionConstants.OP_SIPUSH, X),
//                new BranchInstruction(InstructionConstants.OP_IFICMPNE, A),
//                new BranchInstruction(InstructionConstants.OP_GOTO, B),
//            },
//        }
    };
}