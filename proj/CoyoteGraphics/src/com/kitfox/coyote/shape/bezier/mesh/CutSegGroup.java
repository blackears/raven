/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author kitfox
 */
public class CutSegGroup
{
    ArrayList<CutSegment> segs = new ArrayList<CutSegment>();
    HashSet<Coord> coords = new HashSet<Coord>();

    void addSeg(CutSegment seg)
    {
        segs.add(seg);
        coords.add(seg.c0);
        coords.add(seg.c1);
    }
    
    void addAll(CutSegGroup grp)
    {
        segs.addAll(grp.segs);
        coords.addAll(grp.coords);
    }
}
