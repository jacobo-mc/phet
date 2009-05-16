/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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
package jmetest.text;

import java.awt.Font;
import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.util.GameTaskQueueManager;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * This test displays 3D text flatly on the screen.
 * 
 * @author Matthew D. Hicks
 */
public class Test3DFlatText {
	public static void main(String[] args) throws Exception {
		StandardGame game = new StandardGame("Test 3D Flat Text");
		game.start();
		
		GameTaskQueueManager.getManager().update(new Callable<Void>() {

			public Void call() throws Exception {				
				final DebugGameState debug = new DebugGameState();
				GameStateManager.getInstance().attachChild(debug);
				debug.setActive(true);

				Font3D font = new Font3D(new Font("Arial", Font.PLAIN, 24), 0.001f, true, true, true);
				Text3D text = font.createText("Testing 1, 2, 3", 50.0f, 0);
				text.setLocalScale(new Vector3f(5.0f, 5.0f, 0.01f));
				debug.getRootNode().attachChild(text);
				return null;
			}
		});
	}
}
