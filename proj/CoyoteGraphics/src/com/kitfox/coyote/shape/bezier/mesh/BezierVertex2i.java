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
public class BezierVertex2i<VertexData>
{
    private int x;
    private int y;
    private BezierVertexSmooth smooth;
    //User defined data stored per vertex
    private VertexData data;
    
    final ArrayList<BezierEdge2i> edgesIn = new ArrayList<BezierEdge2i>();
    final ArrayList<BezierEdge2i> edgesOut = new ArrayList<BezierEdge2i>();

    public BezierVertex2i(int x, int y, BezierVertexSmooth smooth, VertexData data)
    {
        this.x = x;
        this.y = y;
        this.smooth = smooth;
        this.data = data;
    }

    public BezierVertex2i(int x, int y)
    {
        this(x, y, BezierVertexSmooth.CUSP, null);
    }

    public BezierVertex2i()
    {
        this(0, 0);
    }

    public BezierEdge2i getEdgeIn(int index)
    {
        return edgesIn.get(index);
    }
    
    public BezierEdge2i getEdgeOut(int index)
    {
        return edgesOut.get(index);
    }
    
    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * @return the smooth
     */
    public BezierVertexSmooth getSmooth()
    {
        return smooth;
    }

    /**
     * @param smooth the smooth to set
     */
    public void setSmooth(BezierVertexSmooth smooth)
    {
        this.smooth = smooth;
    }

    /**
     * @return the data
     */
    public VertexData getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(VertexData data)
    {
        this.data = data;
    }

}
