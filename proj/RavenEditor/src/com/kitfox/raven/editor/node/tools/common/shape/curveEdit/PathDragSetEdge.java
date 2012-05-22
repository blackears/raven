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
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierPath;
import com.kitfox.raven.shape.network.NetworkPath;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PathDragSetEdge extends PathDragSet
{
    ArrayList<NetworkHandleEdge> pickEdge;
    double weight0;
    double weight1;

    public PathDragSetEdge(ServiceBezierPath servPath,
            NetworkPathHandles handles,
            CyMatrix4d g2d,
            int pickX, int pickY,
            NetworkHandleEdge refEdge,
            ArrayList<NetworkHandleEdge> pickEdge)
    {
        super(servPath, handles, g2d);
        this.pickEdge = pickEdge;

        //Calc how weight should be distribulted to knots
        CyVector2d refPt = new CyVector2d(pickX, pickY);
        xformPointDev2Graph(refPt);
        
        BezierCurve2i refCurve = refEdge.getCurveGraph();
        
        double dist0 = refPt.distance(refCurve.getStartX(), refCurve.getStartY());
        double dist1 = refPt.distance(refCurve.getEndX(), refCurve.getEndY());
        double sumI = 1 / (dist0 + dist1);
        
        weight0 = dist1 * sumI;
        weight1 = dist0 * sumI;
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
        
        for (NetworkHandleEdge edge: pickEdge)
        {
            BezierPathEdge2i e = newPath.getEdge(edge.getIndex());
            
            e.setK0x((int)(e.getK0x() + delta.x * weight0));
            e.setK0y((int)(e.getK0y() + delta.y * weight0));
            e.setK1x((int)(e.getK1x() + delta.x * weight1));
            e.setK1y((int)(e.getK1y() + delta.y * weight1));
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
