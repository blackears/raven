/*
 * Copyright 2011 Mark McKay
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kitfox.coyote.shape.bezier.cutgraph;

import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kitfox
 */
public class CurveCutter2i
{
    public static BezierCurve2i[][] cutCurves(BezierCurve2i c0, BezierCurve2i c1, 
            double flatnessSquared)
    {
        ArrayList<CutSegment> segs0 = 
                CutSegment.createSegments(c0, flatnessSquared);
        ArrayList<CutSegment> segs1 = 
                CutSegment.createSegments(c1, flatnessSquared);
        
        ArrayList<CutPoint> cuts0 = new ArrayList<CutPoint>();
        ArrayList<CutPoint> cuts1 = new ArrayList<CutPoint>();
        
        //Find all points of intersection
        for (int j = 0; j < segs1.size(); ++j)
        {
            CutSegment s1 = segs1.get(j);
            for (int i = 0; i < segs0.size(); ++i)
            {
                CutSegment s0 = segs0.get(i);
                if (s0.isBoundingBoxOverlap(s1))
                {
                    s0.cutAgainst(s1, cuts0, cuts1);
                }
            }
        }
        
        prepareCutPoints(cuts0);
        prepareCutPoints(cuts1);
        
        return new BezierCurve2i[][]{
            cutCurve(c0, cuts0), cutCurve(c1, cuts1)};
    }

    private static BezierCurve2i[] cutCurve(
            BezierCurve2i c, ArrayList<CutPoint> cuts)
    {
        if (cuts.isEmpty())
        {
            return new BezierCurve2i[]{c};
        }
        
        double[] t = new double[cuts.size()];
        for (int i = 0; i < t.length; ++i)
        {
            CutPoint p = cuts.get(i);
            t[i] = p.t;
        }
        
        BezierCurve2i[] parts = c.split(t);
        
        //Make sure new segments verts match cut points
        for (int i = 0; i < parts.length; ++i)
        {
            Coord c0 = i == 0 ? null : cuts.get(i - 1).c;
            Coord c1 = i == parts.length - 1 ? null : cuts.get(i).c;
            BezierCurve2i p = parts[i];
            
            if (c0 == null)
            {
                if (p.getEndX() != c1.x || p.getEndY() != c1.y)
                {
                    parts[i] = p.setEnd(c1.x, c1.y);
                }
            }
            else if (c1 == null)
            {
                if (p.getStartX() != c0.x || p.getStartY() != c0.y)
                {
                    parts[i] = p.setStart(c0.x, c0.y);
                }
            }
            else
            {
                if (p.getEndX() != c1.x || p.getEndY() != c1.y
                        || p.getStartX() != c0.x || p.getStartY() != c0.y)
                {
                    parts[i] = p.setEndPoints(c0.x, c0.y, c1.x, c1.y);
                }
            }
        }
        
        return parts;
    }
    
    private static void prepareCutPoints(ArrayList<CutPoint> cuts)
    {
        Collections.sort(cuts);
        
        //Remove duplicates, end points
        for (int i = cuts.size() - 1; i >= 0; --i)
        {
            CutPoint p1 = cuts.get(i);
            CutPoint p0 = i == 0 ? null : cuts.get(i - 1);
            if (p1.t <= 0 || p1.t >= 1 || 
                    (p0 != null && p0.c.equals(p1.c)))
            {
                cuts.remove(i);
            }
        }
    }
}
