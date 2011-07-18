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
package org.jmol.jvxl.calc;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.jvxl.api.VertexDataServer;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.readers.Parameters;
import org.jmol.util.TriangleData;

public class MarchingCubes extends TriangleData {

  /*
   * An adaptation of Marching Cubes that includes data slicing. 
   * Associated SurfaceReader and VoxelData structures are required 
   * to store the sequential values in the case of a plane
   * and to deliver the sequential vertex numbers in any case.
   * 
   * Author: Bob Hanson, hansonr@stolaf.edu
   *  
   * inputs: surfaceReader: interface to other methods
   *         volumeData, containing all information relating to origin, axes, etc.
   *         
   *         MODE_CUBE volumeData.voxelData possibly with 3D voxel data
   *         MODE_BITSET bsVoxels -- optional alternative, JVXL-type BitSet indicating which vertices are inside and which outside
   *            in order for(x 0 to nX){for(y 0 to nY){for(z 0 to nZ){}}}
   *         MODE_GETXYZ If BOTH voxelData and bsVoxels are null, 
   *            then we assume we can get the voxelData on the fly
   *            using surfaceReader.getValue(x, y, z)  
   * 
   * surfaceReader.getSurfacePointIndex gets the actual Point3f vertex points and
   * returns the fraction information. It is exported here, because the JVXL reader may use this
   * as an opportunity to deliver the fractional information, thus providing the
   * actual position of the vertex point on the isosurface. 
   *      
   * outputs: Point3f positions of isosurface intersections with grid via SurfaceReader.addVertex()
   *          Point4f triangle data via surfaceReader.addTriangleCheck()
   *          bsVoxels -- returned same (JVXL file data) or filled (otherwise)
   *          edgeData -- encoded fraction data as a string
   *          
   *          bsExcludedVertices -- an option to exclude vertices based on having NaN values
   *          bsExcludedTriangles -- an option to exclude triangles based on position in space
   *  
   */

  protected VertexDataServer surfaceReader;
  protected VolumeData volumeData;
  protected int contourType;
  protected boolean isContoured;
  protected float cutoff;
  protected boolean isCutoffAbsolute;
  protected boolean isSquared;
  protected boolean isXLowToHigh;

  protected int cubeCountX, cubeCountY, cubeCountZ;
  protected int nY, nZ;
  protected int yzCount;
  
  protected boolean colorDensity;
  protected float fractionOutside;
  protected boolean integrateSquared = true;
  protected BitSet bsVoxels;
  protected BitSet bsExcludedVertices;
  protected BitSet bsExcludedTriangles;
  protected BitSet bsExcludedPlanes;

  protected StringBuffer edgeData = new StringBuffer();
  
  private boolean excludePartialCubes = true; // original way
  
  public BitSet getBsVoxels() {
    return bsVoxels;
  }
  
  public MarchingCubes() {
    // as triangleServer  
  }
  
  public MarchingCubes(VertexDataServer surfaceReader, VolumeData volumeData,
      Parameters params, BitSet bsVoxels) {

    // If just creating a JVXL file, see org.openscience.jmol.jvxl.simplewriter.SimpleMarchingCubes.java
    //
  
    // setting this false could upset reading 
    // older Jmol version files -- will need a flag IN the file for this if we do it
    excludePartialCubes = true; 
    
    this.surfaceReader = surfaceReader;
    this.bsVoxels = bsVoxels;
    BitSet[] bsExcluded = params.bsExcluded;
    bsExcludedVertices =  (bsExcluded[0] == null ? bsExcluded[0] = new BitSet() : bsExcluded[0]);
    bsExcludedPlanes =    (bsExcluded[2] == null ? bsExcluded[2] = new BitSet() : bsExcluded[2]);
    bsExcludedTriangles = (bsExcluded[3] == null ? bsExcluded[3] = new BitSet() : bsExcluded[3]);
    mode = (volumeData.voxelData != null ? MODE_CUBE 
        : bsVoxels != null ? MODE_BITSET : MODE_GETXYZ);
    setParameters(volumeData, params);
  }

  protected void setParameters(VolumeData volumeData, Parameters params) {
    this.volumeData = volumeData;
    colorDensity = params.colorDensity;
    isContoured = params.thePlane == null && params.isContoured && !colorDensity;
    cutoff = params.cutoff;
    isCutoffAbsolute = params.isCutoffAbsolute;
    contourType = params.contourType;
    isSquared = params.isSquared;
    isXLowToHigh = params.isXLowToHigh;

    cubeCountX = volumeData.voxelCounts[0] - 1;
    cubeCountY = (nY = volumeData.voxelCounts[1]) - 1;
    cubeCountZ = (nZ = volumeData.voxelCounts[2]) - 1;
    yzCount = volumeData.getYzCount();
    if (bsVoxels == null)
      bsVoxels = new BitSet();
    edgeVertexPointers = (isXLowToHigh ? edgeVertexPointersLowToHigh : edgeVertexPointersHighToLow);
    edgeVertexPlanes =  (isXLowToHigh ? edgeVertexPlanesLowToHigh : edgeVertexPlanesHighToLow);
    isoPointIndexPlanes = new int[2][yzCount][3];
    yzPlanes = (mode == MODE_GETXYZ ? new float[2][yzCount] : null);
    setLinearOffsets();
    calcVoxelVertexVectors();
  }

  protected int mode;
  protected final static int MODE_CUBE = 1;
  protected final static int MODE_BITSET = 2;
  protected final static int MODE_GETXYZ = 3;

  protected final float[] vertexValues = new float[8];

  protected int edgeCount;

  protected final Vector3f[] voxelVertexVectors = new Vector3f[8];
  protected final Vector3f[] edgeVectors = new Vector3f[12];
  {
    for (int i = 12; --i >= 0;)
      edgeVectors[i] = new Vector3f();
  }

  protected void calcVoxelVertexVectors() {
    for (int i = 8; --i >= 0;)
      volumeData.transform(cubeVertexVectors[i],
          voxelVertexVectors[i] = new Vector3f());
    for (int i = 12; --i >= 0;)
      edgeVectors[i].sub(voxelVertexVectors[edgeVertexes[i + i + 1]],
          voxelVertexVectors[edgeVertexes[i + i]]);
  }

  /* see also org.openscience.jmol.jvxl.simplewriter.SimpleMarchingCubes.java
   * for a streamlined version of this method that does simple writing
   * of JVXL files from cube data. 
   * 
   */

  protected static int[] yzPlanePts = new int[] { 
      0, 1, 1, 0, 
      0, 1, 1, 0 
  };
  protected final int[] edgePointIndexes = new int[12];
  protected int[][][] isoPointIndexPlanes;
  protected float[][] yzPlanes;

  protected int[][] resetIndexPlane(int[][] plane) {
    for (int i = 0; i < yzCount; i++)
      for (int j = 0; j < 3; j++)
        plane[i][j] = Integer.MIN_VALUE;
    return plane;
  }

  public String getEdgeData() {


    // Logger.startTimer();

    /*
     * The (new, Jmol 11.7.26) Marching Cubes code creates the
     * isoPointIndexes[2][nY * nZ][3] array that holds two slices of edge data.
     * Each edge is assigned a specific vertex, such that each vertex may have
     * up to 3 associated edges.
     * 
     * Feb 10, 2009 -- Bob Hanson
     */

    int insideCount = 0, outsideCount = 0, surfaceCount = 0;
    edgeCount = 0;

    int x0, x1, xStep, ptStep, pt, ptX;
    if (isXLowToHigh) {
      x0 = 0;
      x1 = cubeCountX;
      xStep = 1;
      ptStep = yzCount;
      pt = ptX = (yzCount - 1) - nZ - 1;
      // we are starting at the top corner, in the next to last
      // cell on the next to last row of the first plane
    } else {
      x0 = cubeCountX - 1;
      x1 = -1;
      xStep = -1;
      ptStep = -yzCount;
      pt = ptX = (cubeCountX * yzCount - 1) - nZ - 1;
      // we are starting at the top corner, in the next to last
      // cell on the next to last row of the next to last plane(!)
    }
    if (cubeCountX < 0 || cubeCountY < 0 || cubeCountZ < 0)
      return "";
    int cellIndex0 = cubeCountY * cubeCountZ - 1;
    int cellIndex = cellIndex0;
    resetIndexPlane(isoPointIndexPlanes[1]);
    if (mode == MODE_GETXYZ)
      surfaceReader.getPlane(x0);

    float v = 0;
    int pti = 0;
    boolean allInside = (colorDensity && (cutoff == 0 || bsVoxels.cardinality() == 0));

    for (int x = x0; x != x1; x += xStep, ptX += ptStep, pt = ptX, cellIndex = cellIndex0) {

      // we swap planes of grid data when
      // obtaining the grid data point by point

      if (mode == MODE_GETXYZ) {
        // for a progressive reader, we read the next two planes
        // for x = 0, 2, 4, 6...
        if (x + xStep != x1)
          surfaceReader.getPlane(x + xStep);
        float[] plane = yzPlanes[0];
        yzPlanes[0] = yzPlanes[1];
        yzPlanes[1] = plane;
      }

      // we swap the edge vertex index planes

      int[][] indexPlane = isoPointIndexPlanes[0];
      isoPointIndexPlanes[0] = isoPointIndexPlanes[1];
      isoPointIndexPlanes[1] = resetIndexPlane(indexPlane);

      // now scan the plane of cubicals
      
      if (bsExcludedPlanes.get(x) && bsExcludedPlanes.get(x + xStep))
        continue;
      int xCount = 0;
      for (int y = cubeCountY; --y >= 0; pt--) {        
        for (int z = cubeCountZ; --z >= 0; pt--, cellIndex--) {

          // create the bitset mask indicating which vertices are inside.
          // 0xFF here means "all inside"; 0x00 means "all outside"

          int insideMask = 0;
          for (int i = 8; --i >= 0;) {

            // cubeVertexOffsets just gets us the specific grid point relative
            // to our base x,y,z cube position

            boolean isInside;
            Point3i offset = cubeVertexOffsets[i];
            pti = pt + linearOffsets[i];
            switch (mode) {
            case MODE_GETXYZ:
              v = vertexValues[i] = getValue(x + offset.x, y + offset.y, z
                  + offset.z, pti, yzPlanes[yzPlanePts[i]]);
              isInside = bsVoxels.get(pti);
              break;
            case MODE_BITSET:
              isInside = (allInside || bsVoxels.get(pti));
              v = vertexValues[i] = (bsExcludedVertices.get(pti) ? Float.NaN
                  : isInside ? 1 : 0);
              break;
            default:
            case MODE_CUBE:
              v = vertexValues[i] = volumeData.voxelData[x + offset.x][y
                  + offset.y][z + offset.z];
              if (isSquared)
                vertexValues[i] *= vertexValues[i];
              isInside = (allInside ? true : isInside(vertexValues[i], cutoff, isCutoffAbsolute));
              if (isInside)
                bsVoxels.set(pti);
            }
            if (isInside) {
              insideMask |= Pwr2[i];
            } else {
              fractionOutside += (integrateSquared ? vertexValues[i]
                  * vertexValues[i] : vertexValues[i]);
            }

            //if (Float.isNaN(v))
              //bsExcludedVertices.set(pti);
          }
          if (!Float.isNaN(v)) {
            xCount++;
          }
          if (colorDensity && cutoff == 0) {
            // 0 cutoff read as "show grid points only"
            addVertex(x, y, z, pti, v);
              continue;            
          }
          if (insideMask == 0) {
            ++outsideCount;
            continue;
          }
          if (colorDensity && (insideMask & 1) == 1) {
            // xyz corner is inside, so add this point
            addVertex(x, y, z, pti, v);
          }
          if (insideMask == 0xFF) {
            ++insideCount;
            continue;
          }
          ++surfaceCount;

          // This cube is straddling the cutoff. We must check all edges
          // Note that we do not process it if it has an NaN values
          if (processOneCubical(insideMask, x, y, z, pt) 
              && !isContoured && !colorDensity) {
            processTriangles(insideMask);
          }
        }
      }
      if (xCount == 0) {
        bsExcludedPlanes.set(x);
      }
    }

    return edgeData.toString();
  }

  protected void processTriangles(int insideMask) {

    // the inside mask serves to define the triangles necessary
    // if just creating JVXL files, this step is unnecessary

    byte[] triangles = triangleTable2[insideMask];
    for (int i = triangles.length; (i -= 4) >= 0;)
      addTriangle(triangles[i], triangles[i + 1], triangles[i + 2],
          triangles[i + 3]);   
  }

  protected void addVertex(int x, int y, int z, int pti, float value) {
    volumeData.voxelPtToXYZ(x, y, z, pt0);
    if (surfaceReader.addVertexCopy(pt0, value, -4) < 0)
      bsExcludedVertices.set(pti);
  }

  protected int nTriangles;
  protected void addTriangle(int ia, int ib, int ic, int edgeType) {
    if (!bsExcludedTriangles.get(nTriangles) &&
        surfaceReader.addTriangleCheck(edgePointIndexes[ia], 
        edgePointIndexes[ib], edgePointIndexes[ic], 
        edgeType, 0, isCutoffAbsolute, 0) < 0) {
      bsExcludedTriangles.set(nTriangles);
    }
    nTriangles++;
  }

  protected BitSet bsValues = new BitSet();

  protected float getValue(int x, int y, int z, int pt, float[] tempValues) {
    int ptyz = pt % yzCount;
    //if (bsValues.get(pt))
      //return tempValues[ptyz];
    bsValues.set(pt);
    float value = surfaceReader.getValue(x, y, z, ptyz);
    if (isSquared)
      value *= value;
    tempValues[ptyz] = value;
    if (isInside(value, cutoff, isCutoffAbsolute))
      bsVoxels.set(pt);
    return value;
  }

  public static boolean isInside(float voxelValue, float max, boolean isAbsolute) {
    return ((max > 0 && (isAbsolute ? Math.abs(voxelValue) : voxelValue) >= max) || (max <= 0 && voxelValue <= max));
  }

  protected final Point3f pt0 = new Point3f();
  protected final Point3f pointA = new Point3f();

  protected final static int[] edgeVertexPointersLowToHigh = new int[] {
      1, 1, 2, 0, 
      5, 5, 6, 4,
      0, 1, 2, 3
  };
  
  protected final static int[] edgeVertexPointersHighToLow = new int[] {
      0, 1, 3, 0, 
      4, 5, 7, 4,
      0, 1, 2, 3
  };

  protected int[] edgeVertexPointers;

  protected final static int[] edgeVertexPlanesLowToHigh = new int[] {
      1, 1, 1, 0, 
      1, 1, 1, 0, 
      0, 1, 1, 0
  };  // from high to low, only edges 3, 7, 8, and 11 are from plane 0

  protected final static int[] edgeVertexPlanesHighToLow = new int[] {
      1, 0, 1, 1,
      1, 0, 1, 1,
      1, 0, 0, 1
  }; //from high to low, only edges 1, 5, 9, and 10 are from plane 0

  protected int[] edgeVertexPlanes;
  
  protected boolean processOneCubical(int insideMask, int x, int y, int z, int pt) {

    /*
     * The key to the algorithm is that we have a catalog that
     * maps the inside-vertex mask to an edge mask, and then
     * each edge is associated with a specific vertex. 
     * 
     * Each cube vertex may be associated with from 0 to 3 edges,
     * depending upon where it lies in the overall cube of data.
     * 
     * When scanning X from low to high, the "leading vertex" is
     * vertex 1 and edgeVertexPlanes[1]. Edges 0, 1, and 9 are 
     * associated with vertex 1, and others are associated similarly.
     * 
     * When scanning X from high to low, the "leading vertex" is
     * vertex 0 and edgeVertexPlanes[1]. Edges 0, 3, and 8 are 
     * associated with vertex 0, and others are associated similarly.
     * 
     * edgePointIndexes[iEdge] tracks the vertex index for this
     * specific cubical so that triangles can be created properly.
     *  
     * 
     *                      Y 
     *                      4 --------4--------- 5  
     *                     /|                   /|
     *                    / |                  / |
     *                   /  |                 /  |
     *                  7   8                5   |
     *                 /    |               /    9
     *                /     |              /     |
     *               7 --------6--------- 6      |
     *               |      |             |      |
     *               |      0 ---------0--|----- 1    X
     *               |     /              |     /
     *              11    /               10   /
     *               |   3                |   1
     *               |  /                 |  /
     *               | /                  | /
     *               3 ---------2-------- 2
     *              Z 
     *              /                    /              
     *  edgeVertexPlanes[0]            [1] (scanning x low to high)
     *  edgeVertexPlanes[1]            [0] (scanning x high to low)
     *           
     */
    


    int edgeMask = insideMaskTable[insideMask];
    boolean isNaN = false;
    for (int iEdge = 12; --iEdge >= 0;) {

      // bit set to one means it's a relevant edge

      int xEdge = Pwr2[iEdge];
      if ((edgeMask & xEdge) == 0)
        continue;

      // if we have a point already, we don't need to check this edge.
      // for triangles, this will be an index into an array;
      // for just creating JVXL files, this can just be 0

      int iPlane = edgeVertexPlanes[iEdge];
      int iPt = (pt + linearOffsets[edgeVertexPointers[iEdge]]) % yzCount;
      int iType = edgeTypeTable[iEdge];
      int index = edgePointIndexes[iEdge] = isoPointIndexPlanes[iPlane][iPt][iType];
      if (index != Integer.MIN_VALUE) {
        if (index == -1)
          isNaN = excludePartialCubes; // -- problem with older Jmol files? 
          // this says, "If any point on the cube is NaN, then 
          //don't process the cube. 
        continue; // propagated from neighbor
      }
      // here's an edge that has to be checked.

      // get the vertex numbers 0 - 7

      int vertexA = edgeVertexes[iEdge << 1];
      int vertexB = edgeVertexes[(iEdge << 1) + 1];

      // pick up the actual value at each vertex
      // this array of 8 values is updated as we go.

      float valueA = vertexValues[vertexA];
      float valueB = vertexValues[vertexB];

      // we allow for NaN values -- missing triangles


      // the exact point position -- not important for just
      // creating the JVXL file. In that case, all you 
      // need are the two values valueA and valueB and the cutoff.
      // from those you can define the fractional offset

      // here is where we get the value and assign the point for that edge
      // it is where the JVXL surface data line is appended

      calcVertexPoint(x, y, z, vertexA, pointA);

      edgeCount++;

      int i = edgePointIndexes[iEdge] = isoPointIndexPlanes[iPlane][iPt][iType] = surfaceReader
          .getSurfacePointIndexAndFraction(cutoff, isCutoffAbsolute, x, y, z,
              cubeVertexOffsets[vertexA], vertexA, vertexB, valueA, valueB,
              pointA, edgeVectors[iEdge], iType == contourType, fReturn);

      addEdgeData(i < 0 ? Float.NaN : fReturn[0]);

      // If the fraction returns NaN, this is because one of the end points
      // is NaN; if the point index returns -1, then the point has been 
      // excluded by the meshDataServer (Isosurface) for some other reason,
      // for example because it is outside the limits of the box.
      
      if (Float.isNaN(fReturn[0]) || i < 0)
        isNaN = excludePartialCubes;
    }
    return !isNaN;
  }

  protected void addEdgeData(float f) {
    char ch = JvxlCoder.jvxlFractionAsCharacter(f);
    edgeData.append(ch);
  }

  protected float[] fReturn = new float[1];
  
  public void calcVertexPoint(int x, int y, int z, int vertex, Point3f pt) {
    volumeData.voxelPtToXYZ(x, y, z, pt0);
    pt.add(pt0, voxelVertexVectors[vertex]);
  }

  protected final static Vector3f[] cubeVertexVectors = { 
    new Vector3f(0, 0, 0),
    new Vector3f(1, 0, 0), 
    new Vector3f(1, 0, 1), 
    new Vector3f(0, 0, 1),
    new Vector3f(0, 1, 0), 
    new Vector3f(1, 1, 0), 
    new Vector3f(1, 1, 1),
    new Vector3f(0, 1, 1) };


  /*                     Y 
   *                      4 --------4--------- 5                     +z --------4--------- +yz+z                  
   *                     /|                   /|                     /|                   /|
   *                    / |                  / |                    / |                  / |
   *                   /  |                 /  |                   /  |                 /  |
   *                  7   8                5   |                  7   8                5   |
   *                 /    |               /    9                 /    |               /    9
   *                /     |              /     |                /     |              /     |
   *               7 --------6--------- 6      |            +z+1 --------6--------- +yz+z+1|
   *               |      |             |      |               |      |             |      |
   *               |      0 ---------0--|----- 1    X          |      0 ---------0--|----- +yz    X(outer)    
   *               |     /              |     /                |     /              |     /
   *              11    /               10   /                11    /               10   /
   *               |   3                |   1                  |   3                |   1
   *               |  /                 |  /                   |  /                 |  /
   *               | /                  | /                    | /                  | /
   *               3 ---------2-------- 2                     +1 ---------2-------- +yz+1
   *              Z                                           Z (inner)
   * 
   *                                                              streaming data offsets
   * type 0: x-edges: 0 2 4 6
   * type 1: y-edges: 8 9 10 11
   * type 2: z-edges: 1 3 5 7
   * 
   * Data stream offsets for vertices, relative to point 0, based on reading 
   * loops {for x {for y {for z}}} 0-->n-1
   * y and z are numbers of grid points in those directions:
   * 
   *            0    1      2      3      4      5      6        7
   *            0   +yz   +yz+1   +1     +z    +yz+z  +yz+z+1  +z+1     
   * 
   * These are just looked up in a table. After the first set of cubes, 
   * we are only adding points 1, 2, 5 or 6. This means that initially
   * we need two data slices, but after that only one (slice 1):
   * 
   *            base
   *           offset 0    1      2      3      4      5      6     7
   *  slice[0]        0                 +1     +z                 +z+1     
   *  slice[1]  +yz        0     +1                   +z    +z+1      
   * 
   *  slice:          0    1      1      0      0      1      1     0
   *  
   *  We can request reading of two slices (2*nY*nZ data points) first, then
   *  from then on, just nY*nZ points. "Reading" is really just being handed a 
   *  pointer into an array. Perhaps that array is already filled completely;
   *  perhaps it is being read incrementally. 
   *  
   *  As it is now, the JVXL data are read into a BitSet 
   *  so we can continue to do that with NON progressive files.
   *  
   *   
   */

  protected final static int edgeTypeTable[] = { 
    0, 2, 0, 2, 
    0, 2, 0, 2, 
    1, 1, 1, 1 };
  // 0=along X, 1=along Y, 2=along Z

  protected final int[] linearOffsets = new int[8];

  /* 
   * set the linear offsets for generating a unique cell ID,
   * for pointing into the inside/outside BitSet,
   * and for finding the associated vertex for an edge.
   * 
   */
  
  protected void setLinearOffsets() {
    linearOffsets[0] = 0;
    linearOffsets[1] = yzCount;
    linearOffsets[2] = yzCount + 1;
    linearOffsets[3] = 1;
    linearOffsets[4] = nZ;
    linearOffsets[5] = yzCount + nZ;
    linearOffsets[6] = yzCount + nZ + 1;
    linearOffsets[7] = nZ + 1;
  }

  public int getLinearOffset(int x, int y, int z, int offset) {
    return x * yzCount + y * nZ + z + linearOffsets[offset];
  }

  protected final static short insideMaskTable[] = { 0x0000, 0x0109, 0x0203,
      0x030A, 0x0406, 0x050F, 0x0605, 0x070C, 0x080C, 0x0905, 0x0A0F, 0x0B06,
      0x0C0A, 0x0D03, 0x0E09, 0x0F00, 0x0190, 0x0099, 0x0393, 0x029A, 0x0596,
      0x049F, 0x0795, 0x069C, 0x099C, 0x0895, 0x0B9F, 0x0A96, 0x0D9A, 0x0C93,
      0x0F99, 0x0E90, 0x0230, 0x0339, 0x0033, 0x013A, 0x0636, 0x073F, 0x0435,
      0x053C, 0x0A3C, 0x0B35, 0x083F, 0x0936, 0x0E3A, 0x0F33, 0x0C39, 0x0D30,
      0x03A0, 0x02A9, 0x01A3, 0x00AA, 0x07A6, 0x06AF, 0x05A5, 0x04AC, 0x0BAC,
      0x0AA5, 0x09AF, 0x08A6, 0x0FAA, 0x0EA3, 0x0DA9, 0x0CA0, 0x0460, 0x0569,
      0x0663, 0x076A, 0x0066, 0x016F, 0x0265, 0x036C, 0x0C6C, 0x0D65, 0x0E6F,
      0x0F66, 0x086A, 0x0963, 0x0A69, 0x0B60, 0x05F0, 0x04F9, 0x07F3, 0x06FA,
      0x01F6, 0x00FF, 0x03F5, 0x02FC, 0x0DFC, 0x0CF5, 0x0FFF, 0x0EF6, 0x09FA,
      0x08F3, 0x0BF9, 0x0AF0, 0x0650, 0x0759, 0x0453, 0x055A, 0x0256, 0x035F,
      0x0055, 0x015C, 0x0E5C, 0x0F55, 0x0C5F, 0x0D56, 0x0A5A, 0x0B53, 0x0859,
      0x0950, 0x07C0, 0x06C9, 0x05C3, 0x04CA, 0x03C6, 0x02CF, 0x01C5, 0x00CC,
      0x0FCC, 0x0EC5, 0x0DCF, 0x0CC6, 0x0BCA, 0x0AC3, 0x09C9, 0x08C0, 0x08C0,
      0x09C9, 0x0AC3, 0x0BCA, 0x0CC6, 0x0DCF, 0x0EC5, 0x0FCC, 0x00CC, 0x01C5,
      0x02CF, 0x03C6, 0x04CA, 0x05C3, 0x06C9, 0x07C0, 0x0950, 0x0859, 0x0B53,
      0x0A5A, 0x0D56, 0x0C5F, 0x0F55, 0x0E5C, 0x015C, 0x0055, 0x035F, 0x0256,
      0x055A, 0x0453, 0x0759, 0x0650, 0x0AF0, 0x0BF9, 0x08F3, 0x09FA, 0x0EF6,
      0x0FFF, 0x0CF5, 0x0DFC, 0x02FC, 0x03F5, 0x00FF, 0x01F6, 0x06FA, 0x07F3,
      0x04F9, 0x05F0, 0x0B60, 0x0A69, 0x0963, 0x086A, 0x0F66, 0x0E6F, 0x0D65,
      0x0C6C, 0x036C, 0x0265, 0x016F, 0x0066, 0x076A, 0x0663, 0x0569, 0x0460,
      0x0CA0, 0x0DA9, 0x0EA3, 0x0FAA, 0x08A6, 0x09AF, 0x0AA5, 0x0BAC, 0x04AC,
      0x05A5, 0x06AF, 0x07A6, 0x00AA, 0x01A3, 0x02A9, 0x03A0, 0x0D30, 0x0C39,
      0x0F33, 0x0E3A, 0x0936, 0x083F, 0x0B35, 0x0A3C, 0x053C, 0x0435, 0x073F,
      0x0636, 0x013A, 0x0033, 0x0339, 0x0230, 0x0E90, 0x0F99, 0x0C93, 0x0D9A,
      0x0A96, 0x0B9F, 0x0895, 0x099C, 0x069C, 0x0795, 0x049F, 0x0596, 0x029A,
      0x0393, 0x0099, 0x0190, 0x0F00, 0x0E09, 0x0D03, 0x0C0A, 0x0B06, 0x0A0F,
      0x0905, 0x080C, 0x070C, 0x0605, 0x050F, 0x0406, 0x030A, 0x0203, 0x0109,
      0x0000 };


}
