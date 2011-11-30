/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierFace2i<FaceData, FaceVertexData>
{
    final ArrayList<BezierVertex2i> vertices = new ArrayList<BezierVertex2i>();
    final ArrayList<BezierEdge2i> edges = new ArrayList<BezierEdge2i>();
    private FaceData data;
    HashMap<BezierVertex2i, FaceVertexData> faceVertexData 
            = new HashMap<BezierVertex2i, FaceVertexData>();

    /**
     * @return the data
     */
    public FaceData getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(FaceData data)
    {
        this.data = data;
    }
}
