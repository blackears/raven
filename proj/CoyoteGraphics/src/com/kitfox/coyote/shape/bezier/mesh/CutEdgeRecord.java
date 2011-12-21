/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

/**
 *
 * @author kitfox
 */
public class CutEdgeRecord
{
    CutEdge[] edges0;
    CutEdge[] edges1;

    public CutEdgeRecord(CutEdge[] edges0, CutEdge[] edges1)
    {
        this.edges0 = edges0;
        this.edges1 = edges1;
    }
}
