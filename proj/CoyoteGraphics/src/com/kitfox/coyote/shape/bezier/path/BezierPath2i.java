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

package com.kitfox.coyote.shape.bezier.path;

import com.kitfox.coyote.shape.CyPathIterator;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.bezier.mesh.BezierLoopClosure;
import com.kitfox.coyote.shape.bezier.path.cut.BezierPathCutter2i;
import com.kitfox.coyote.shape.bezier.path.cut.Segment;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierPath2i
{
    ArrayList<BezierLoop2i> loops = new ArrayList<BezierLoop2i>();

    public void addLoop(BezierLoop2i loop)
    {
        loops.add(loop);
    }
    
    public BezierPathCutter2i createCutGraph(double flatnessSquared)
    {
        BezierPathCutter2i cut = new BezierPathCutter2i(flatnessSquared);
        
        for (BezierLoop2i l: loops)
        {
            l.appendToCutGraph(cut);
        }
        
        return cut;
    }
    
    public BezierPath2i combine(BezierPath2i path, BooleanOp op, double flatnessSquared)
    {
        BezierPathCutter2i c0 = createCutGraph(flatnessSquared);
        BezierPathCutter2i c1 = path.createCutGraph(flatnessSquared);
        
        c0.cutAgainstGraph(c1);

        ArrayList<Segment> segs = new ArrayList<Segment>();
        
        switch (op)
        {
            case UNION:
                c0.getSegments(c1, true, true, false, segs);
                c1.getSegments(c0, true, false, false, segs);
                break;
            case INTERSECTION:
                c0.getSegments(c1, false, true, true, segs);
                c1.getSegments(c0, false, false, true, segs);
                break;
            case A_SUB_B:
                c0.getSegments(c1, true, false, false, segs);
                c1.getSegments(c0, false, false, true, segs);
                break;
            case B_SUB_A:
                c0.getSegments(c1, false, false, true, segs);
                c1.getSegments(c0, true, false, false, segs);
                break;
        }
        
        return BezierPathCutter2i.segmentsToEdges(segs);
    }
    
    public static BezierPath2i create(CyShape shape)
    {
        double[] coords = new double[6];
        
        BezierPath2i path = new BezierPath2i();
        BezierLoop2i curLoop = null;
        double mx = 0, my = 0;
        
        for (CyPathIterator it = shape.getIterator();
                it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                {
                    if (curLoop != null)
                    {
                        //Finish last loop
                        path.loops.add(curLoop);
                        curLoop = null;
                    }
                    curLoop = new BezierLoop2i((int)coords[0], (int)coords[1]);

                    mx = coords[0];
                    my = coords[1];
                    break;
                }
                case LINETO:
                {
                    if (curLoop == null)
                    {
                        //Finish last loop
                        path.loops.add(curLoop);
                        curLoop = new BezierLoop2i((int)mx, (int)my);
                    }
                    curLoop.appendLine((int)coords[0], (int)coords[1]);
                    
                    //Track cursor position
                    mx = coords[0];
                    my = coords[1];
                    break;
                }
                case QUADTO:
                {
                    if (curLoop == null)
                    {
                        //Finish last loop
                        path.loops.add(curLoop);
                        curLoop = new BezierLoop2i((int)mx, (int)my);
                    }
                    curLoop.appendQuad((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3]);
                    
                    //Track cursor position
                    mx = coords[2];
                    my = coords[3];
                    break;
                }
                case CUBICTO:
                {
                    if (curLoop == null)
                    {
                        //Finish last loop
                        path.loops.add(curLoop);
                        curLoop = new BezierLoop2i((int)mx, (int)my);
                    }
                    curLoop.appendCubic((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3],
                            (int)coords[4], (int)coords[5]);
                    
                    //Track cursor position
                    mx = coords[4];
                    my = coords[5];
                    break;
                }
                case CLOSE:
                {
                    if (curLoop == null)
                    {
                        continue;
                    }
                    curLoop.setClosure(curLoop.endPointsOverlap()
                            ? BezierLoopClosure.CLOSED_CLAMPED
                            : BezierLoopClosure.CLOSED_FREE);
                    
                    path.loops.add(curLoop);
                    curLoop = null;
                    
                    break;
                }
            }
        }
        
        if (curLoop != null)
        {
            //Finish last loop
            path.loops.add(curLoop);
            curLoop = null;
        }
        
        return path;
    }
    
}
