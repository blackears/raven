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
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class MeshDragSetVertex extends MeshDragSet
{
    ArrayList<NetworkHandleVertex> pickVertex;

    public MeshDragSetVertex(ServiceBezierMesh servMesh,
            NetworkMeshHandles handles,
            CyMatrix4d g2d,
            ArrayList<NetworkHandleVertex> pickVertex)
    {
        super(servMesh, handles, g2d);
        this.pickVertex = pickVertex;
    }

    @Override
    public void dragBy(int dx, int dy, boolean history)
    {
        //Translation in graph space
        CyVector2d delta = new CyVector2d(dx, dy);
        xformVectorDev2Graph(delta);
        
        HashMap<Coord, Coord> moveVertexMap = new HashMap<Coord, Coord>();
        
        //Pick new positions for selected knots
        UpdateSet updateSet = new UpdateSet();
        for (NetworkHandleVertex v: pickVertex)
        {
            Coord c = v.getCoord();
            moveVertexMap.put(c, 
                    new Coord((int)(c.x + delta.x), (int)(c.y + delta.y)));
            
            for (NetworkHandleEdge e: v.getInputEdges())
            {
                EdgeUpdate update = updateSet.getEdgeUpdate(e);
                update.v1 = delta;
            }
            
            for (NetworkHandleEdge e: v.getOutputEdges())
            {
                EdgeUpdate update = updateSet.getEdgeUpdate(e);
                update.v0 = delta;
            }
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
        
        newMesh.moveEmptyVertices(moveVertexMap);
        
        for (EdgeUpdate update: updateSet.edgeMap.values())
        {
            BezierCurve2i curve = update.createCurve();
            NetworkDataEdge data = update.data;
            newMesh.addEdge(curve, data);
        }
        
//        newMesh.removeEmptyVertices();
        cleanupFaces(newMesh);
        
        servMesh.setNetworkMesh(newMesh, history);
    }

    
    //--------------------
    class UpdateSet
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
    
    class EdgeUpdate
    {
        NetworkHandleEdge edge;
        private BezierCurve2i curve;
        NetworkDataEdge data;
        
        //Set if updating respective knots
        CyVector2d v0;
        CyVector2d v1;

        public EdgeUpdate(NetworkHandleEdge edge)
        {
            this.edge = edge;
            curve = edge.getCurveGraph();
            this.data = new NetworkDataEdge(edge.getData());
        }

        private BezierCurve2i createCurve()
        {
            BezierCubic2i cubic = curve.asCubic();
            
            int dx0 = v0 == null ? 0 : (int)v0.x;
            int dy0 = v0 == null ? 0 : (int)v0.y;
            int dx1 = v1 == null ? 0 : (int)v1.x;
            int dy1 = v1 == null ? 0 : (int)v1.y;
            
            int ax0 = cubic.getAx0() + dx0;
            int ay0 = cubic.getAy0() + dy0;
            int ax1 = cubic.getAx1() + dx0;
            int ay1 = cubic.getAy1() + dy0;
            
            int ax2 = cubic.getAx2() + dx1;
            int ay2 = cubic.getAy2() + dy1;
            int ax3 = cubic.getAx3() + dx1;
            int ay3 = cubic.getAy3() + dy1;
            
            switch (curve.getOrder())
            {
                case 2:
                    return new BezierLine2i(ax0, ay0, ax3, ay3);
                default:
                    return new BezierCubic2i(
                            ax0, ay0, ax1, ay1, ax2, ay2, ax3, ay3);
            }
        }
    }
    
}
