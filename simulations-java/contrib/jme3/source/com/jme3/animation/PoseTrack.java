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
import com.jme3.export.InputCapsule;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * A single track of pose animation associated with a certain mesh.
 */
public final class PoseTrack extends Track {
    
    private PoseFrame[] frames;
    private float[]     times;

    public static class PoseFrame implements Savable {

        Pose[] poses;
        float[] weights;

        public PoseFrame(Pose[] poses, float[] weights){
            this.poses = poses;
            this.weights = weights;
        }

        public void write(JmeExporter e) throws IOException {
            OutputCapsule out = e.getCapsule(this);
            out.write(poses, "poses", null);
            out.write(weights, "weights", null);
        }

        public void read(JmeImporter i) throws IOException {
            InputCapsule in = i.getCapsule(this);
            poses = (Pose[]) in.readSavableArray("poses", null);
            weights = in.readFloatArray("weights", null);
        }
    }

    public PoseTrack(int targetMeshIndex, float[] times, PoseFrame[] frames){
        super(targetMeshIndex);
        this.times = times;
        this.frames = frames;
    }

    private void applyFrame(Mesh target, int frameIndex, float weight){
        PoseFrame frame = frames[frameIndex];
        VertexBuffer pb = target.getBuffer(Type.Position);
        for (int i = 0; i < frame.poses.length; i++){
            Pose pose = frame.poses[i];
            float poseWeight = frame.weights[i] * weight;

            pose.apply(poseWeight, (FloatBuffer) pb.getData());
        }

        // force to re-upload data to gpu
        pb.updateData(pb.getData());
    }

    public void setTime(float time, Mesh[] targets, float weight) {
        Mesh target = targets[targetMeshIndex];
        if (time < times[0]){
            applyFrame(target, 0, weight);
        }else if (time > times[times.length-1]){
            applyFrame(target, times.length-1, weight);
        } else{
            int startFrame = 0;
            for (int i = 0; i < times.length; i++){
                if (times[i] < time)
                    startFrame = i;
            }

            int endFrame = startFrame + 1;
            float blend = (time - times[startFrame]) / (times[endFrame] - times[startFrame]);
            applyFrame(target, startFrame, blend * weight);
            applyFrame(target, endFrame,   (1f-blend) * weight);
        }
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        OutputCapsule out = e.getCapsule(this);
        out.write(frames, "frames", null);
        out.write(times, "times", null);
    }

    @Override
    public void read(JmeImporter i) throws IOException {
        InputCapsule in = i.getCapsule(this);
        frames = (PoseFrame[]) in.readSavableArray("frames", null);
        times = in.readFloatArray("times", null);
    }
}
