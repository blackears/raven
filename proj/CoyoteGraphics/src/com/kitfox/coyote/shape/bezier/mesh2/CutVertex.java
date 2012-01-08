/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh2;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class CutVertex
{
    Coord coord;
    ArrayList<CutSegment> segIn = new ArrayList<CutSegment>();
    ArrayList<CutSegment> segOut = new ArrayList<CutSegment>();

    public CutVertex(Coord coord)
    {
        this.coord = coord;
    }

}
