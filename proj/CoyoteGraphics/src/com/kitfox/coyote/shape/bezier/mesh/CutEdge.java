/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.Segment.CutItem;
import com.kitfox.coyote.shape.bezier.mesh.Segment.CutRecord;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kitfox
 */
public class CutEdge
{
    BezierCurve2i curve;
    Segment[] segs;
    final double flatnessSquared;

    public CutEdge(BezierCurve2i curve, double flatnessSquared)
    {
        this.curve = curve;
        segs = flatten(curve, flatnessSquared);
        this.flatnessSquared = flatnessSquared;
    }
    
    public CutEdgeRecord cutAgainst(CutEdge e1)
    {
        if (!curve.boundingBoxIntersects(e1.curve))
        {
            return null;
        }
        
        if (curve.equals(e1.curve))
        {
            return null;
        }
        
        ArrayList<CutItem> cuts0 = new ArrayList<CutItem>();
        ArrayList<CutItem> cuts1 = new ArrayList<CutItem>();
        
        for (int i = 0; i < segs.length; ++i)
        {
            Segment s0 = segs[i];
            
            for (int j = 0; j < e1.segs.length; ++j)
            {
                Segment s1 = segs[i];
                
                CutRecord rec = s0.findCuts(s1);
                if (rec == null)
                {
                    continue;
                }
           
                appendCuts(cuts0, s0, rec.cuts0);
                appendCuts(cuts1, s1, rec.cuts1);
            }
        }

        formatCuts(cuts0, getStart(), getEnd());
        formatCuts(cuts1, e1.getStart(), e1.getEnd());
        
        BezierCurve2i[] curves0 = cutCurve(curve, cuts0.toArray(new CutItem[cuts0.size()]));
        BezierCurve2i[] curves1 = cutCurve(e1.curve, cuts1.toArray(new CutItem[cuts1.size()]));
        
        CutEdge[] edges0 = curves0.length == 1
                ? new CutEdge[]{this}
                : buildEdges(curves0);
        CutEdge[] edges1 = curves1.length == 1
                ? new CutEdge[]{e1}
                : buildEdges(curves1);
        
        return new CutEdgeRecord(edges0, edges1);
    }

    public Coord getStart()
    {
        return segs[0].c0;
    }
    
    public Coord getEnd()
    {
        return segs[segs.length - 1].c1;
    }
    
    private CutEdge[] buildEdges(BezierCurve2i[] curves)
    {
        CutEdge[] edges = new CutEdge[curves.length];
        for (int i = 0; i < edges.length; ++i)
        {
            edges[i] = new CutEdge(curves[1], flatnessSquared);
        }
        return edges;
    }
    
    private void formatCuts(ArrayList<CutItem> cuts, Coord c0, Coord c1)
    {
        Collections.sort(cuts);
        
        //Remove cuts at start point
        while (!cuts.isEmpty())
        {
            CutItem item = cuts.get(0);
            if (item.coord.equals(c0))
            {
                cuts.remove(0);
            }
        }
        
        //Remove cuts at end point
        while (!cuts.isEmpty())
        {
            CutItem item = cuts.get(cuts.size() - 1);
            if (item.coord.equals(c1))
            {
                cuts.remove(cuts.size() - 1);
            }
        }
        
        //Remove duplicates
        for (int i = 0; i < cuts.size() - 1; ++i)
        {
            CutItem item0 = cuts.get(i);
            CutItem item1 = cuts.get(i + 1);
            
            if (item0.coord.equals(item1.coord))
            {
                cuts.remove(i + 1);
                --i;
            }
        }
    }
    
    private void appendCuts(ArrayList<CutItem> list, Segment s, CutItem[] cuts)
    {
        if (cuts == null)
        {
            return;
        }
        
        for (int i = 0; i < cuts.length; ++i)
        {
            CutItem item = cuts[i];
            double t;
            if (item.t == 0)
            {
                t = s.t0;
            }
            else if (item.t == 1)
            {
                t = s.t1;
            }
            else
            {
                t = Math2DUtil.lerp(s.t0, s.t1, item.t);
            }
            list.add(new CutItem(t, item.coord));
        }
    }
    
    private BezierCurve2i[] cutCurve(BezierCurve2i curve, CutItem[] cuts)
    {
        double[] arr = new double[cuts.length];
        for (int i = 0; i < arr.length; ++i)
        {
            arr[i] = cuts[i].t;
        }
        BezierCurve2i[] curves = curve.split(arr);
        
        //Make sure we align with cut coords
        for (int i = 0; i < curves.length; ++i)
        {
            int x0 = i == 0 ? curve.getStartX() : cuts[i - 1].coord.x;
            int y0 = i == 0 ? curve.getStartY() : cuts[i - 1].coord.y;
            int x1 = i == curves.length - 1
                    ? curve.getEndX() : cuts[i].coord.x;
            int y1 = i == curves.length - 1
                    ? curve.getEndY() : cuts[i].coord.y;
            
            BezierCurve2i c = curves[i];
            if (c.getStartX() != x0 || c.getStartY() != y0
                    || c.getEndX() != x1 || c.getEndY() != y1)
            {
                curves[i] = c.setEndPoints(x0, y0, x1, y1);
            }
        }
        return curves;
    }
    
    public static Segment[] flatten(BezierCurve2i curve, double flatnessSquared)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            Coord c0 = new Coord(curve.getStartX(), curve.getStartY());
            Coord c1 = new Coord(curve.getEndX(), curve.getEndY());
            return new Segment[]{new Segment(0, 1, c0, c1)};
        }
        
        ArrayList<Segment> segs = new ArrayList<Segment>();
        flatten(curve, flatnessSquared, 0, 1, segs);
        
        return segs.toArray(new Segment[segs.size()]);
    }

    private static void flatten(BezierCurve2i curve,
            double flatnessSquared, 
            double t0, double t1,
            ArrayList<Segment> segs)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            Coord c0 = new Coord(curve.getStartX(), curve.getStartY());
            Coord c1 = new Coord(curve.getEndX(), curve.getEndY());
            Segment seg = new Segment(t0, t1, c0, c1);
            segs.add(seg);
            return;
        }
        
        BezierCurve2i[] curves = curve.split(.5);
        double tm = (t0 + t1) / 2;
        flatten(curves[0], flatnessSquared, t0, tm, segs);
        flatten(curves[1], flatnessSquared, tm, t1, segs);
    }
    
    
}
