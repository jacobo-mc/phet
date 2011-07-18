/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-05-18 15:41:42 -0500 (Fri, 18 May 2007) $
 * $Revision: 7752 $

 *
 * Copyright (C) 2003-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.export;


import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.Measure;
import org.jmol.viewer.Viewer;

/*
 * Contributed by pim schravendijk <pimlists@googlemail.com>
 * Jmol 11.3.30
 * Oct 2008
 * 
 */

public class _PovrayExporter extends __RayTracerExporter {
  
  public _PovrayExporter() {
    commentChar = "// ";
  }

  @Override
  String finalizeOutput() {
    super.finalizeOutput();
    return getAuxiliaryFileData();
  }

  @Override
  protected void outputHeader() {
    super.outputHeader();
    output("// ******************************************************\n");
    output("// Created by Jmol " + Viewer.getJmolVersion() + "\n");
    output("//\n");
    output("// This script was generated on " + getExportDate() + "\n");
    output("// ******************************************************\n");
    try {
    output(viewer.getWrappedState(true));
    } catch (Exception e) {
      // tough luck
    }
    output("\n");
    output("// ******************************************************\n");
    output("// Declare the resolution, camera, and light sources.\n");
    output("// ******************************************************\n");
    output("\n");
    output("// NOTE: if you plan to render at a different resolution,\n");
    output("// be sure to update the following two lines to maintain\n");
    output("// the correct aspect ratio.\n" + "\n");
    output("#declare Width = " + screenWidth + ";\n");
    output("#declare Height = " + screenHeight + ";\n");
    output("#declare minScreenDimension = " + minScreenDimension + ";\n");
    output("#declare showAtoms = true;\n");
    output("#declare showBonds = true;\n");
    output("#declare noShadows = true;\n");
    output("camera{\n");
    output("  orthographic\n");
    output("  location < " + screenWidth / 2f + ", " + screenHeight / 2f
        + ", 0>\n" + "\n");
    output("  // Negative right for a right hand coordinate system.\n");
    output("\n");
    output("  sky < 0, -1, 0 >\n");
    output("  right < -" + screenWidth + ", 0, 0>\n");
    output("  up < 0, " + screenHeight + ", 0 >\n");
    output("  look_at < " + screenWidth / 2f + ", " + screenHeight / 2f
        + ", 1000 >\n");
    output("}\n");
    output("\n");

    output("background { color rgb <" + 
        rgbFractionalFromColix(backgroundColix, ',')
        + "> }\n");
    output("\n");

    // light source

    float distance = Math.max(screenWidth, screenHeight);
    output("light_source { <" + lightSource.x * distance + "," + lightSource.y * distance
        + ", " + (-1 * lightSource.z * distance) + "> " + " rgb <0.6,0.6,0.6> }\n");
    output("\n");
    output("\n");

    output("// ***********************************************\n");
    output("// macros for common shapes\n");
    output("// ***********************************************\n");
    output("\n");

    writeMacros();
  }

  private void writeMacros() {
    output("#default { finish {\n" + "  ambient "
        + Graphics3D.getAmbientPercent() / 100f + "\n" + "  diffuse "
        + Graphics3D.getDiffusePercent() / 100f + "\n" + "  specular "
        + Graphics3D.getSpecularPercent() / 100f + "\n"
        + "  roughness .00001\n  metallic\n  phong 0.9\n  phong_size 120\n}}"
        + "\n\n");

    output("#macro check_shadow()\n"
        + " #if (noShadows)\n"
        + "  no_shadow \n"
        + " #end\n"
        + "#end\n\n");

    output("#declare slabZ = " + slabZ + ";\n"
        + "#declare depthZ = " + depthZ + ";\n"
        + "#declare dzSlab = 10;\n"
        + "#declare dzDepth = dzSlab;\n"
        + "#declare dzStep = 0.001;\n\n");
    
    output("#macro clip()\n"
        + "  clipped_by { box {<0,0,slabZ>,<Width,Height,depthZ>} }\n"
        + "#end\n\n");

    output("#macro circleCap(Z,RADIUS,R,G,B,T)\n"
        + "// cap for lower clip\n"
        + " #local cutDiff = Z - slabZ;\n"
        + " #local cutRadius2 = (RADIUS*RADIUS) - (cutDiff*cutDiff);\n"
        + " #if (cutRadius2 > 0)\n"
        + "  #local cutRadius = sqrt(cutRadius2);\n"
        + "  #if (dzSlab > 0)\n" 
        + "   #declare dzSlab = dzSlab - dzStep;\n"
        + "  #end\n"
        + "  cylinder{<X,Y,slabZ-dzSlab>,"
        + "<X,Y,(slabZ+1)>,cutRadius\n"
        + "   pigment{rgbt<R,G,B,T>}\n"
        + "   translucentFinish(T)\n"
        + "   check_shadow()}\n"
        + " #end\n"
        + "// cap for upper clip\n"
        + " #declare cutDiff = Z - depthZ;\n"
        + " #declare cutRadius2 = (RADIUS*RADIUS) - (cutDiff*cutDiff);\n"
        + " #if (cutRadius2 > 0)\n"
        + "  #local cutRadius = sqrt(cutRadius2);\n"
        + "  #if (dzDepth > 0)\n"
        + "   #declare dzDepth = dzDepth - dzStep;\n"
        + "  #end\n"
        + "  cylinder{<X,Y,depthZ+dzDepth>,"
        + "<X,Y,(depthZ-1)>,cutRadius\n"
        + "   pigment{rgbt<R,G,B,T>}\n"
        + "   translucentFinish(T)\n"
        + "   check_shadow()}\n"
        + " #end\n"
        + "#end\n\n");

    writeMacrosFinish();
    writeMacrosAtom();
    writeMacrosBond();
    //    writeMacrosRing();
  }

  private void writeMacrosFinish() {
    output("#macro translucentFinish(T)\n"
        + " #local shineFactor = T;\n"
        + " #if (T <= 0.25)\n"
        + "  #declare shineFactor = (1.0-4*T);\n"
        + " #end\n"
        + " #if (T > 0.25)\n"
        + "  #declare shineFactor = 0;\n"
        + " #end\n"
        + " finish {\n" + "  ambient "
        + Graphics3D.getAmbientPercent() / 100f + "\n" + "  diffuse "
        + Graphics3D.getDiffusePercent() / 100f + "\n" + "  specular "
        + Graphics3D.getSpecularPercent() / 100f + "\n"
        + "  roughness .00001\n"  
        + "  metallic shineFactor\n"  
        + "  phong 0.9*shineFactor\n"  
        + "  phong_size 120*shineFactor\n}"
        + "#end\n\n");
  }


  private void writeMacrosAtom() {
    output("#macro a(X,Y,Z,RADIUS,R,G,B,T)\n" 
        + " sphere{<X,Y,Z>,RADIUS\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n"
        + (isSlabEnabled? " circleCap(Z,RADIUS,R,G,B,T)\n" : "")
        + "#end\n\n");

    output("#macro q(XX,YY,ZZ,XY,XZ,YZ,X,Y,Z,J,R,G,B,T)\n" 
        + " quadric{<XX,YY,ZZ>,<XY,XZ,YZ>,<X,Y,Z>,J\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n"
//        + (isSlabEnabled? " circleCap(Z,RADIUS,R,G,B,T)\n" : "")
        + "#end\n\n");
/*    output("#macro qf(XX,YY,ZZ,XY,XZ,YZ,X,Y,Z,J,R,G,B,T,X0,Y0,Z0,X1,Y1,Z1)\n" 
        + " difference{\n" 
        + "  quadric{<XX,YY,ZZ>,<XY,XZ,YZ>,<X,Y,Z>,J\n"
        + "   pigment{rgbt<R,G,B,T>}\n"
        + "   translucentFinish(T)\n"
        + "   clip()\n"
        + "   check_shadow()}\n"
        + "  box {<X0,Y0,Z0>,<X1,Y1,Z1>}\n"
        + " }\n"
*///        + (isSlabEnabled? " circleCap(Z,RADIUS,R,G,B,T)\n" : "")
//        + "#end\n\n");

  }

  private void writeMacrosBond() {
    // We always use cones here, in orthographic mode this will give us
    //  cones with two equal radii, in perspective mode Jmol will calculate
    //  the cone radii for us.
    output("#macro b(X1,Y1,Z1,RADIUS1,X2,Y2,Z2,RADIUS2,R,G,B,T)\n"
        + " cone{<X1,Y1,Z1>,RADIUS1,<X2,Y2,Z2>,RADIUS2\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    // and just the cylinder
    output("#macro c(X1,Y1,Z1,RADIUS1,X2,Y2,Z2,RADIUS2,R,G,B,T)\n"
        + " cone{<X1,Y1,Z1>,RADIUS1,<X2,Y2,Z2>,RADIUS2 open\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    // and rounded endcaps

  }

  private boolean haveMacros;
  
  private void writeMacros2() {
    // triangle
    output("#macro r(X1,Y1,Z1,X2,Y2,Z2,X3,Y3,Z3,R,G,B,T)\n"
        + " triangle{<X1,Y1,Z1>,<X2,Y2,Z2>,<X3,Y3,Z3>\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    // text pixel
    output("#macro p(X,Y,Z,R,G,B)\n" 
        + " box{<X,Y,Z>,<X+1,Y+1,Z+1>\n"
        + "  pigment{rgb<R,G,B>}\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    // draw arrow BARB
    output("#macro barb(X1,Y1,Z1,RADIUS1,X2,Y2,Z2,RADIUS2,R,G,B,T,X3,Y3,Z3,W3)\n"
        + " cone{<X1,Y1,Z1>,RADIUS1,<X2,Y2,Z2>,RADIUS2\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  clipped_by{plane{<X3,Y3,Z3>,W3}}\n"
        + "  check_shadow()}\n" + "#end\n\n");
    /*
    // This type of ring does not take into account perspective effects!
    output("#macro o(X,Y,Z,RADIUS,R,G,B,T)\n"
    + " torus{RADIUS,wireRadius pigment{rgbt<R,G,B,T>}\n"
    + " translate<X,Z,-Y> rotate<90,0,0>\n" + "  check_shadow()}\n"
    + "#end\n\n");
    */
    haveMacros = true;
  }
  
  private String triad(Tuple3f pt) {
    if (Float.isNaN(pt.x))
      return "0,0,0";
    return pt.x + "," + pt.y + "," + pt.z;
  }

  private String triad(int[] i) {
    return i[0] + "," + i[1] + "," + i[2];
  }

  private String color4(short colix) {
    return rgbFractionalFromColix(colix, ',') + ","
        + translucencyFractionalFromColix(colix);
  }

  private String getAuxiliaryFileData() {
    String fName = fileName.substring(fileName.lastIndexOf("/") + 1);    
    fName = fName.substring(fName.lastIndexOf("\\") + 1);    
    return "; Created by: Jmol " + Viewer.getJmolVersion()
        + "\n; Creation date: " + getExportDate() 
        + "\n; File created: "  + fileName + " (" + nBytes + " bytes)\n\n" 
        + (commandLineOptions != null ? commandLineOptions :
          "\n; Jmol state: (embedded in input file)" 
        + "\nInput_File_Name=" + fName 
        + "\nOutput_to_File=true"
        + "\nOutput_File_Type=N"
        + "\nOutput_File_Name=" + fName + ".png" 
        + "\nWidth=" + screenWidth 
        + "\nHeight=" + screenHeight
        + "\nAntialias=true"
        + "\nAntialias_Threshold=0.1" 
        + "\nDisplay=true"
        + "\nPause_When_Done=true"
        + "\nWarning_Level=5"
        + "\nVerbose=false" + "\n");

  }

  @Override
  protected void output(Tuple3f pt) {
    output(", <" + triad(pt) + ">");    
  }
  
  @Override
  protected void outputCircle(int x, int y, int z, float radius, short colix,
                              boolean doFill) {
    output((doFill ? "b(" : "c(") + x + "," + y + "," + z + "," + radius + ","
        + x + "," + y + "," + (z + 1) + "," + (radius + (doFill ? 0 : 2)) + ","
        + color4(colix) + ")\n");
  }

  @Override
  protected void outputCone(Point3f screenBase, Point3f screenTip,
                            float radius, short colix, boolean isBarb) {
    if (isBarb) {
      if (!haveMacros)
        writeMacros2();
      Point4f plane = new Point4f();
      tempP1.set(screenBase.x, screenTip.y, 12345.6789f);
      Measure.getPlaneThroughPoints(screenBase, screenTip, tempP1, tempV1,
          tempV2, tempV3, plane);
      output("barb(" + triad(screenBase) + "," + radius + ","
          + triad(screenTip) + ",0" + "," + color4(colix) + "," + plane.x + ","
          + plane.y + "," + plane.z + "," + -plane.w + ")\n");
    } else {
      output("b(" + triad(screenBase) + "," + radius + "," + triad(screenTip)
          + ",0" + "," + color4(colix) + ")\n");
    }
  }

  @Override
  protected void outputCylinder(Point3f screenA, Point3f screenB, float radius,
                              short colix, boolean withCaps) {
    String color = color4(colix);
    output((withCaps ? "b(" : "c(") 
        + triad(screenA) + "," + radius + "," + triad(screenB) + ","
        + radius + "," + color + ")\n");
  }
  
  @Override
  protected void outputCylinderConical(Point3f screenA, Point3f screenB,
                                       float radius1, float radius2, short colix) {
    output("b(" + triad(screenA) + "," + radius1 + "," + triad(screenB) + ","
        + radius2 + "," + color4(colix) + ")\n");
  }

  @Override
  protected void outputEllipsoid(Point3f center, float radius, double[] coef, short colix) {
    // no quadrant cut-out here
    String s = coef[0] + "," + coef[1] + "," + coef[2] + "," + coef[3] + ","
        + coef[4] + "," + coef[5] + "," + coef[6] + "," + coef[7] + ","
        + coef[8] + "," + coef[9] + "," + color4(colix);
    output("q(" + s + ")\n");
  }

  @Override
  protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                                  short[] colixes, int[][] indices, 
                                  short[] polygonColixes,
                                  int nVertices, int nPolygons, int nFaces, BitSet bsPolygons,
                                  int faceVertexMax, short colix,
                                  List<Short> colorList, Map<Short, Integer> htColixes, Point3f offset) {
    if (polygonColixes != null) {
      boolean isAll = (bsPolygons == null);
      int i0 = (isAll ? nPolygons - 1 : bsPolygons.nextSetBit(0));
      for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsPolygons.nextSetBit(i + 1))) {
        //if ((p++) % 10 == 0)
        //  output("\n");
        output("polygon { 4\n"); 
        for (int j = 0; j <= 3; j++)
          outputVertex(vertices[indices[i][j % 3]], offset);
        output("\n");
        output("pigment{rgbt<" + color4(colix = polygonColixes[i]) + ">}\n");
        output("  translucentFinish(" + translucencyFractionalFromColix(colix)
            + ")\n");
        output("  check_shadow()\n");
        output("  clip()\n");
        output("}\n");
      }
      return;
    }

    output("mesh2 {\n");
    output("vertex_vectors { " + nVertices);
    for (int i = 0; i < nVertices; i++)
      outputVertex(vertices[i], offset);
    output("\n}\n");

    boolean haveNormals = (normals != null);
    if (haveNormals) {
      output("normal_vectors { " + nVertices);
      for (int i = 0; i < nVertices; i++) {
        setTempVertex(vertices[i], offset, tempP2);
        output(getScreenNormal(tempP2, normals[i], 1));
        output("\n");
      }
      output("\n}\n");
    }

    if (colixes != null) {
      int nColix = colorList.size();
      output("texture_list { " + nColix);
      // just using the transparency of the first colix there... 
      String finish = ">}" + " translucentFinish("
        + translucencyFractionalFromColix(colixes[0]) + ")}";
      for (int i = 0; i < nColix; i++)
        output("\n, texture{pigment{rgbt<" + color4(colorList.get(i).shortValue()) + finish);
      output("\n}\n");
    }
    output("face_indices { " + nFaces);
    boolean isAll = (bsPolygons == null);
    int i0 = (isAll ? nPolygons - 1 : bsPolygons.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsPolygons.nextSetBit(i + 1))) {
      output(", <" + triad(indices[i]) + ">");
      if (colixes != null) {
        output("," + htColixes.get("" + colixes[indices[i][0]]));
        output("," + htColixes.get("" + colixes[indices[i][1]]));
        output("," + htColixes.get("" + colixes[indices[i][2]]));
      }
      if (faceVertexMax == 4 && indices[i].length == 4) {
        output(", <" + indices[i][0] + "," + indices[i][2] + "," + indices[i][3] + ">");
        if (colixes != null) {
          output("," + htColixes.get("" + colixes[indices[i][0]]));
          output("," + htColixes.get("" + colixes[indices[i][2]]));
          output("," + htColixes.get("" + colixes[indices[i][3]]));
        }
      }
      output("\n");
    }
    output("\n}\n");

    if (colixes == null) {
      output("pigment{rgbt<" + color4(colix) + ">}\n");
      output("  translucentFinish(" + translucencyFractionalFromColix(colix)
          + ")\n");
    }
    output("  check_shadow()\n");
    output("  clip()\n");
    output("}\n");

    /*
     mesh2 {
     vertex_vectors {
     9, 
     <0,0,0>, <0.5,0,0>, <0.5,0.5,0>,
     <1,0,0>, <1,0.5,0>, <1,1,0>   
     <0.5,1,0>, <0,1,0>, <0,0.5,0> 
     }
     normal_vectors {
     9,
     <-1,-1,0>, <0,-1,0>, <0,0,1>,
     <1,-1,0>, <1,0,0>, <1,1,0>,
     <0,1,0>, <-1,1,0>, <-1,0,0>
     }
     texture_list {
     3,
     texture{pigment{rgb <0,0,1>}},
     texture{pigment{rgb 1}},
     texture{pigment{rgb <1,0,0>}}
     }
     face_indices {
     8, 
     <0,1,2>,0,1,2,  <1,3,2>,1,0,2,
     <3,4,2>,0,1,2,  <4,5,2>,1,0,2,
     <5,6,2>,0,1,2,  <6,7,2>,1,0,2,
     <7,8,2>,0,1,2,  <8,0,2>,1,0,2
     }
     }
     */

  }

  @Override
  protected void outputSphere(float x, float y, float z, float radius,
                                  short colix) {
   output("a(" + x + "," + y + "," + z + "," + radius + ","
        + color4(colix) + ")\n");
  }
  
  @Override
  protected void outputTextPixel(int x, int y, int z, int argb) {
    if (!haveMacros)
      writeMacros2();
    //text only
    output("p(" + x + "," + y + "," + z + "," + 
        rgbFractionalFromArgb(argb, ',') + ")\n");
  }
  
  @Override
  protected void outputTriangle(Point3f ptA, Point3f ptB, Point3f ptC, short colix) {
    if (!haveMacros)
      writeMacros2();
    //cartoons, mesh, isosurface
    output("r(" + triad(ptA) + "," + triad(ptB) + "," + triad(ptC) + ","
        + color4(colix) + ")\n");
  }
}
