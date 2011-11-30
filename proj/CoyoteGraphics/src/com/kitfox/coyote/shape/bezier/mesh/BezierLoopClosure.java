/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

/**
 *
 * @author kitfox
 */
public enum BezierLoopClosure
{
    /**
     * Ends points of this path are independent
     */
    OPEN, 
    /**
     *Loop is closed by connecting the tail to the head with a straight line.
     * The individual end vertices are free to move where they like
     * */
    CLOSED_FREE, 
    /**
     * Loop is closed by considering the tail vertex to be coincident with 
     * the head vertex.  In this case, the final vertex position, smoothing
     * and exit edge are ignored and considered to be identical to that of
     * the head vertex.  The head vertex's enter edge is ignored and the
     * tail's enter edge is used instead.
     */
    CLOSED_CLAMPED
}
