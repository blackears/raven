/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.Segment.CutPoint;
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

    private CutEdge(BezierCurve2i curve, Segment[] segs)
    {
        this.curve = curve;
        this.segs = segs;
    }

    public CutEdge(BezierCurve2i curve, double flatnessSquared)
    {
        this.curve = curve;
        segs = flatten(curve, flatnessSquared);
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
        
        ArrayList<CutPointEdge> cuts0 = new ArrayList<CutPointEdge>();
        ArrayList<CutPointEdge> cuts1 = new ArrayList<CutPointEdge>();
        
        //Add cut points
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

        if (cuts0.isEmpty() && cuts1.isEmpty())
        {
            return null;
        }

        //Add existing segment points
        for (int i = 0; i < segs.length; ++i)
        {
            Segment s0 = segs[i];
            
            if (i == 0)
            {
                cuts0.add(new CutPointEdge(s0.t0, s0.c0, false));
            }
            cuts0.add(new CutPointEdge(s0.t1, s0.c1, false));
        }            
        
        for (int i = 0; i < e1.segs.length; ++i)
        {
            Segment s1 = e1.segs[i];
            
            if (i == 0)
            {
                cuts0.add(new CutPointEdge(s1.t0, s1.c0, false));
            }
            cuts0.add(new CutPointEdge(s1.t1, s1.c1, false));
        }            
        
        //Sort, remove duplicates
        formatCuts(cuts0, getStart(), getEnd());
        formatCuts(cuts1, e1.getStart(), e1.getEnd());
        
        CutEdge[] edges0 = buildEdges(cuts0, curve);
        CutEdge[] edges1 = buildEdges(cuts1, e1.curve);
        
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
    
    private CutEdge[] buildEdges(ArrayList<CutPointEdge> cuts,
            BezierCurve2i curve)
    {
        ArrayList<ArrayList<CutPointEdge>> sections = 
                new ArrayList<ArrayList<CutPointEdge>>();
     
        {
            ArrayList<CutPointEdge> section = new ArrayList<CutPointEdge>();
            sections.add(section);
            for (int i = 0; i < cuts.size(); ++i)
            {
                CutPointEdge p = cuts.get(i);
                if (p.cutPoint && !(i == 0 || i == cuts.size() - 1))
                {
                    section.add(p);

                    section = new ArrayList<CutPointEdge>();
                    sections.add(section);
                }

                section.add(p);
            }
        }

        double[] t = new double[sections.size() - 1];
        for (int i = 0; i < sections.size() - 1; ++i)
        {
            ArrayList<CutPointEdge> section = sections.get(i + 1);
            CutPointEdge p = section.get(0);
            t[i] = p.t;
        }

        BezierCurve2i[] curves = curve.split(t);
        CutEdge[] edges = new CutEdge[sections.size()];
        
        for (int i = 0; i < edges.length; ++i)
        {
            ArrayList<CutPointEdge> section = sections.get(i);
            Coord cMin = section.get(0).coord;
            Coord cMax = section.get(section.size() - 1).coord;
            double tMin = section.get(0).t;
            double tMax = section.get(section.size() - 1).t;
            
            double tStart = 0;
            Segment[] segs = new Segment[section.size() - 1];
            for (int j = 0; j < segs.length - 1; ++j)
            {
                CutPointEdge p0 = section.get(i);
                CutPointEdge p1 = section.get(i + 1);
                
                double tEnd = j == segs.length - 1 
                        ? 1 : (p1.t - tMin) / (tMax - tMin);
                segs[j] = new Segment(tStart, tEnd, 
                        p0.coord, p1.coord);
                tStart = tEnd;
            }
            
            edges[i] = new CutEdge(curves[i].setEndPoints(
                    cMin.x, cMin.y, cMax.x, cMax.y),
                    segs);
        }

        return edges;
    }
    
    
    private void formatCuts(ArrayList<CutPointEdge> cuts, Coord c0, Coord c1)
    {
        Collections.sort(cuts);

        for (int i = 0; i < cuts.size() - 1; ++i)
        {
            CutPointEdge p0 = cuts.get(i);
            CutPointEdge p1 = cuts.get(i + 1);
            
            if (p0.coord.equals(p1.coord))
            {
                if (p0.cutPoint)
                {
                    cuts.remove(i + 1);
                }
                else
                {
                    cuts.remove(i);
                }
                --i;
            }
        }
    }
    
    private void appendCuts(ArrayList<CutPointEdge> list, Segment s, CutPoint[] cuts)
    {
        if (cuts == null)
        {
            return;
        }
        
        for (int i = 0; i < cuts.length; ++i)
        {
            CutPoint item = cuts[i];
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
            list.add(new CutPointEdge(t, item.coord, true));
        }
    }
    
    private BezierCurve2i[] cutCurve(BezierCurve2i curve, 
            CutPoint[] cuts)
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
    
    //---------------------------------
    
    class CutPointEdge implements Comparable<CutPointEdge>
    {
        final double t;
        final Coord coord;
        boolean cutPoint;

        public CutPointEdge(double t, Coord coord, boolean cutPoint)
        {
            this.t = t;
            this.coord = coord;
            this.cutPoint = cutPoint;
        }

        @Override
        public int compareTo(CutPointEdge oth)
        {
            return Double.compare(t, oth.t);
        }
    }

}
