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

package com.jme3.scene.plugins.ogre.matext;

import java.util.HashMap;

/**
 * <code>MaterialExtensionSet</code> is simply a container for several
 * {@link MaterialExtension}s so that it can be set globally for all
 * {@link OgreMaterialKey}s used.
 */
public class MaterialExtensionSet {
    private HashMap<String, MaterialExtension> extensions
            = new HashMap<String, MaterialExtension>();

    /**
     * Adds a new material extension to the set of extensions.
     * @param extension The {@link MaterialExtension} to add.
     */
    public void addMaterialExtension(MaterialExtension extension){
        extensions.put(extension.getBaseMaterialName(), extension);
    }

    /**
     * Returns the {@link MaterialExtension} for a given Ogre3D base
     * material name.
     *
     * @param baseMatName The ogre3D base material name.
     * @return {@link MaterialExtension} that is set, or null if not set.
     */
    public MaterialExtension getMaterialExtension(String baseMatName){
        return extensions.get(baseMatName);
    }
}
