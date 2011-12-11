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

package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.shape.CyPathIterator;
import com.kitfox.coyote.shape.CyShape;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierPath2i<VertexData, EdgeData>
{
    //Paths only have two faces - an outside and an inside
//    private final BezierFace2i<FaceData, FaceVertexData> faceLeft
//            = new BezierFace2i();
//    private final BezierFace2i<FaceData, FaceVertexData> faceRight 
//            = new BezierFace2i();
    
    ArrayList<BezierLoop2i> loops = new ArrayList<BezierLoop2i>();

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
