/*
 * Copyright (c) 2008-2009, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolo;

import java.awt.Cursor;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Interface that a component needs to implement if it wants to act as a Piccolo
 * canvas.
 * 
 * @version 1.0
 * @author Lance Good
 */
public interface PComponent {

    /**
     * Called to notify PComponent that given bounds need repainting.
     * 
     * @param bounds bounds needing repaint
     */
    void repaint(PBounds bounds);

    /**
     * Sends a repaint notification the repaint manager if PComponent is not
     * already painting immediately.
     */
    void paintImmediately();

    /**
     * Pushes the given cursor onto the cursor stack and sets the current cursor
     * to the one provided.
     * 
     * @param cursor The cursor to set as the current one and push
     */
    void pushCursor(Cursor cursor);

    /**
     * Pops the topmost cursor from the stack and sets it as the current one.
     */
    void popCursor();

    /**
     * Sets whether the component is currently being interacted with.
     * 
     * @param interacting whether the component is currently being interacted
     *            with
     */
    void setInteracting(boolean interacting);
}
