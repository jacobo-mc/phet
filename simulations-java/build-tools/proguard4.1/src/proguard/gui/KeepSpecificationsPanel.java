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
package proguard.gui;

import proguard.*;

import javax.swing.*;

/**
 * This <code>ListPanel</code> allows the user to add, edit, move, and remove
 * KeepSpecification entries in a list.
 *
 * @author Eric Lafortune
 */
final class KeepSpecificationsPanel extends ClassSpecificationsPanel
{
    private final boolean markClasses;
    private final boolean markConditionally;
    private final boolean allowShrinking;
    private final boolean allowOptimization;
    private final boolean allowObfuscation;


    public KeepSpecificationsPanel(JFrame  owner,
                                   boolean markClasses,
                                   boolean markConditionally,
                                   boolean allowShrinking,
                                   boolean allowOptimization,
                                   boolean allowObfuscation)
    {
        super(owner, true);

        this.markClasses       = markClasses;
        this.markConditionally = markConditionally;
        this.allowShrinking    = allowShrinking;
        this.allowOptimization = allowOptimization;
        this.allowObfuscation  = allowObfuscation;
    }


    // Factory methods for ClassSpecificationsPanel.

    protected ClassSpecification createClassSpecification()
    {
        return new KeepSpecification(markClasses,
                                     markConditionally,
                                     allowShrinking,
                                     allowOptimization,
                                     allowObfuscation);
    }


    protected void setClassSpecification(ClassSpecification classSpecification)
    {
        classSpecificationDialog.setKeepSpecification((KeepSpecification)classSpecification);
    }


    protected ClassSpecification getClassSpecification()
    {
        return classSpecificationDialog.getKeepSpecification();
    }
}
