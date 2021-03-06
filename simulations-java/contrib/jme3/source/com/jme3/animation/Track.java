/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.animation;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.scene.Mesh;
import java.io.IOException;

/**
 * A single track of mesh animation (either morph or pose based).
 * Currently morph animations are not supported (only pose).
 */
public abstract class Track implements Savable {

    protected int targetMeshIndex;

    /**
     * build a track for a an index
     * @param targetMeshIndex 
     */
    public Track(int targetMeshIndex) {
        this.targetMeshIndex = targetMeshIndex;
    }

    /**
     * return the mesh index
     * @return 
     */
    public int getTargetMeshIndex() {
        return targetMeshIndex;
    }

    /**
     * sets time for this track
     * @param time
     * @param targets
     * @param weight 
     */
    public abstract void setTime(float time, Mesh[] targets, float weight);

    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this).write(targetMeshIndex, "meshIndex", 0);
    }

    public void read(JmeImporter im) throws IOException {
        targetMeshIndex = im.getCapsule(this).readInt("meshIndex", 0);
    }
}
