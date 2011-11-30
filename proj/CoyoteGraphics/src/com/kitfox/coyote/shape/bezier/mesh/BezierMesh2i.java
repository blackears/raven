/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierMesh2i<VertexData, EdgeData, FaceData, FaceVertexData>
{
    //There is always an outside face 
    final BezierFace2i<FaceData, FaceVertexData> faceOutside
            = new BezierFace2i();
    
    ArrayList<VertexData> vertices = new ArrayList<VertexData>();
}
