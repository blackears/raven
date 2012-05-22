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

package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathVertex2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierPath;
import com.kitfox.raven.shape.network.NetworkPath;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PathDragSetVertex extends PathDragSet
{
    ArrayList<NetworkHandleVertex> pickVertex;

    public PathDragSetVertex(ServiceBezierPath servPath,
            NetworkPathHandles handles,
            CyMatrix4d g2d,
            ArrayList<NetworkHandleVertex> pickVertex)
    {
        super(servPath, handles, g2d);
        this.pickVertex = pickVertex;
    }

    @Override
    public void dragBy(int dx, int dy, boolean history)
    {
        //Translation in graph space
        CyVector2d delta = new CyVector2d(dx, dy);
        xformVectorDev2Graph(delta);
        
        //Alter path
        NetworkPath oldPath = handles.getPath();
        NetworkPath newPath = new NetworkPath(oldPath);
        
        for (NetworkHandleVertex v: pickVertex)
        {
            Coord c = v.getCoord();
            
            BezierPathVertex2i vn = newPath.getVertex(v.getIndex());
            Coord cn = new Coord((int)(c.x + delta.x),
                    (int)(c.y + delta.y));
            vn.setCoord(cn);
            
            BezierPathEdge2i e0 = vn.getEdgeIn();
            e0.setK1x((int)(e0.getK1x() + delta.x));
            e0.setK1y((int)(e0.getK1y() + delta.y));
            
            BezierPathEdge2i e1 = vn.getEdgeOut();
            e1.setK0x((int)(e1.getK0x() + delta.x));
            e1.setK0y((int)(e1.getK0y() + delta.y));
        }
        
        if (history)
        {
            //Restore old path for undo history
            servPath.setNetworkPath(oldPath, false);
            servPath.setNetworkPath(newPath, true);
        }
        else
        {
            servPath.setNetworkPath(newPath, false);
        }
    }

    
    
}
