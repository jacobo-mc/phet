/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-30 11:40:16 -0500 (Fri, 30 Mar 2007) $
 * $Revision: 7273 $
 *
 * Copyright (C) 2007 Miguel, Bob, Jmol Development
 *
 * Contact: hansonr@stolaf.edu
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jvxl.simplewriter;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.jvxl.calc.MarchingCubes;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.readers.Parameters;

//import org.jmol.util.Logger;

public class SimpleMarchingCubes extends MarchingCubes {

  /*
   * An adaptation of Marching Cubes to include data slicing and the option
   * for progressive reading of the data. Associated SurfaceReader and VoxelData
   * structures are required to store the sequential values in the case of a plane
   * and to deliver the sequential vertex numbers in any case.
   * 
   * Author: Bob Hanson, hansonr@stolaf.edu
   * 
   * The "Simple" version does not create triangle data, 
   * just the JVXL edgeData string
   *  
   */

  private boolean doCalcArea;
  private boolean doSaveSurfacePoints;
  private float calculatedArea = Float.NaN;
  private float calculatedVolume = Float.NaN;
  private List<Point3f> surfacePoints;  
  private VoxelDataCreator vdc;


  public SimpleMarchingCubes(VoxelDataCreator vdc, VolumeData volumeData,
      Parameters params, JvxlData jvxlData, 
      List<Point3f> surfacePointsReturn, float[] areaVolumeReturn) {

    // when just creating a JVXL file all you really need are:
    //
    // volumeData.voxelData[x][y][z]
    // cutoff
    //
    // also includes the option to return a Vector of surfacePoints
    // and/or calculate the area of the surface.
    //

    /* these next two bitsets encode vertices excluded because they are NaN
     * (which will exclude the entire cell)
     * and triangles because, perhaps, they are out of range.
     * 
     */
    this.vdc = vdc;
    mode = (vdc == null ? MODE_CUBE : MODE_GETXYZ);
    setParameters(volumeData, params);
    doCalcArea = (areaVolumeReturn != null);
    surfacePoints = surfacePointsReturn;
    if (surfacePoints == null && doCalcArea)
      surfacePoints = new ArrayList<Point3f>();
    doSaveSurfacePoints = (surfacePoints != null);
    jvxlData.jvxlEdgeData = getEdgeData();
    jvxlData.nPointsX = volumeData.voxelCounts[0];
    jvxlData.nPointsY = volumeData.voxelCounts[1];
    jvxlData.nPointsZ = volumeData.voxelCounts[2];
    jvxlData.setSurfaceInfoFromBitSet(bsVoxels, null);
    if (doCalcArea) {
      areaVolumeReturn[0] = calculatedArea;
      areaVolumeReturn[1] = calculatedVolume;
    }    
  }

  protected float getValue(@SuppressWarnings("unused") int i,
                           int x, int y, int z,
                           int pt, float[] tempValues) {
    if (bsValues.get(pt)) {
      return tempValues[pt % yzCount];
    }
    bsValues.set(pt);
    float value = vdc.getValue(x, y, z);
    tempValues[pt % yzCount] = value;
    if (isInside(value, cutoff, isCutoffAbsolute)) {
      bsVoxels.set(pt);
    }
    return value;
  }

  protected int newVertex(Point3f pointA, Vector3f edgeVector, float f) {
    // you could do something with this point if you wanted to
    // here we save it for the surface area/volume calculation

    if (doSaveSurfacePoints) {
      Point3f pt = new Point3f();
      pt.scaleAdd(f, edgeVector, pointA);
      surfacePoints.add(pt);
    }
    return edgeCount++;
  }
  
  @Override
  protected void processTriangles(int insideMask) {
    if (doCalcArea)
      super.processTriangles(insideMask);
  }

  private Vector3f vTemp = new Vector3f();
  private Vector3f vAC = new Vector3f();
  private Vector3f vAB = new Vector3f();

  @Override
  protected void addTriangle(int ia, int ib, int ic, int edgeType) {
    
    // If you were doing something with the triangle vertices
    // you would do it here.    
    // In this example we are just computing the area and volume
   
    Point3f pta = surfacePoints.get(edgePointIndexes[ia]);
    Point3f ptb = surfacePoints.get(edgePointIndexes[ib]);
    Point3f ptc = surfacePoints.get(edgePointIndexes[ic]);
    
    vAB.sub(ptb, pta);
    vAC.sub(ptc, pta);
    vTemp.cross(vAB, vAC);
    float area = vTemp.length() / 2;
    calculatedArea += area;
    
    vAB.set(ptb);
    vAC.set(ptc);
    vTemp.cross(vAB, vAC);
    vAC.set(pta);
    calculatedVolume += vAC.dot(vTemp) / 6;
  }

}
