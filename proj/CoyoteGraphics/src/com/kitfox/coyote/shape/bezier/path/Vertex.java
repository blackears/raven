/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.path;

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class Vertex
{
    Coord coord;
    
    ArrayList<Segment> segOut = new ArrayList<Segment>();
    ArrayList<Segment> segIn = new ArrayList<Segment>();

    public Vertex(Coord coord)
    {
        this.coord = coord;
    }

    public boolean isEmpty()
    {
        return segOut.isEmpty() && segIn.isEmpty();
    }
}
