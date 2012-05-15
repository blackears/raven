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
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class MeshDragSetEdge extends MeshDragSet
{
//    NetworkHandleEdge refEdge;
//    CyVector2d refPt;
    ArrayList<NetworkHandleEdge> pickEdge;
    double weight0;
    double weight1;

    public MeshDragSetEdge(ServiceBezierMesh servMesh,
            NetworkMeshHandles handles,
            CyMatrix4d g2d,
            int pickX, int pickY,
            NetworkHandleEdge refEdge,
            ArrayList<NetworkHandleEdge> pickEdge)
    {
        super(servMesh, handles, g2d);
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
        
        //Find new positions for edges
        UpdateSet updateSet = new UpdateSet();
        for (NetworkHandleEdge edge: pickEdge)
        {
            EdgeUpdate update = updateSet.getEdgeUpdate(edge);
            
            BezierCubic2i curve = edge.getCurveGraph().asCubic();

            update.k0 = new Coord(
                    (int)(curve.getAx1() + delta.x * weight0), 
                    (int)(curve.getAy1() + delta.y * weight0));
            update.k1 = new Coord(
                    (int)(curve.getAx2() + delta.x * weight1), 
                    (int)(curve.getAy2() + delta.y * weight1));
        }
        
        //Alter mesh
        NetworkMesh oldMesh = handles.getMesh();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        for (EdgeUpdate update: updateSet.edgeMap.values())
        {
            NetworkHandleEdge e0 = update.edge;
            NetworkHandleEdge e1 = newHandles.getEdgeHandle(e0.getIndex());
            e1.remove();
        }
        
        for (EdgeUpdate update: updateSet.edgeMap.values())
        {
            BezierCurve2i curve = update.createCurve();
            NetworkDataEdge data = update.data;
            newMesh.addEdge(curve, data);
        }
        
        cleanupFaces(newMesh);
        
        if (history)
        {
            //Restore old mesh for undo history
            servMesh.setNetworkMesh(oldMesh, false);
            servMesh.setNetworkMesh(newMesh, true);
        }
        else
        {
            servMesh.setNetworkMesh(newMesh, false);
        }
    }
    
    //--------------------
    static class UpdateSet
    {
        HashMap<NetworkHandleEdge, EdgeUpdate> edgeMap = 
                new HashMap<NetworkHandleEdge, EdgeUpdate>();

        public EdgeUpdate getEdgeUpdate(NetworkHandleEdge e)
        {
            EdgeUpdate update = edgeMap.get(e);
            if (update == null)
            {
                update = new EdgeUpdate(e);
                edgeMap.put(e, update);
            }
            return update;
        }
        
    }
    
    static class EdgeUpdate
    {
        NetworkHandleEdge edge;
        private BezierCubic2i curve;
        NetworkDataEdge data;
        
        //Set if updating respective knots
        Coord k0;
        Coord k1;

        public EdgeUpdate(NetworkHandleEdge edge)
        {
            this.edge = edge;
            curve = edge.getCurveGraph().asCubic();
            this.data = new NetworkDataEdge(edge.getData());
        }

        private BezierCubic2i createCurve()
        {
            int k0x = k0 == null ? curve.getAx1() : k0.x;
            int k0y = k0 == null ? curve.getAy1() : k0.y;
            int k1x = k1 == null ? curve.getAx2() : k1.x;
            int k1y = k1 == null ? curve.getAy2() : k1.y;
            
            return new BezierCubic2i(curve.getAx0(), curve.getAy0(),
                    k0x, k0y,
                    k1x, k1y,
                    curve.getAx3(), curve.getAy3());
        }
    }

    
}
