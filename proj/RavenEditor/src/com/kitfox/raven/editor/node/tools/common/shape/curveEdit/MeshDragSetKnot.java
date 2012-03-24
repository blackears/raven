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
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleKnot;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class MeshDragSetKnot extends MeshDragSet
{
    ArrayList<NetworkHandleKnot> pickKnot;

    public MeshDragSetKnot(ServiceBezierMesh servMesh,
            NetworkMeshHandles handles,
            CyMatrix4d g2d,
            ArrayList<NetworkHandleKnot> pickKnot)
    {
        super(servMesh, handles, g2d);
        this.pickKnot = pickKnot;
    }

    @Override
    public void dragBy(int dx, int dy, boolean history)
    {
        //Translation in graph space
        CyVector2d delta = new CyVector2d(dx, dy);
        xformVectorDev2Graph(delta);
        
        //Pick new positions for selected knots
        UpdateSet updateSet = new UpdateSet();
        for (NetworkHandleKnot k: pickKnot)
        {
            Coord c = k.getCoord();
            NetworkHandleEdge e = k.getEdge();
            EdgeUpdate update = updateSet.getEdgeUpdate(e);
            
            Coord cNew = new Coord((int)(c.x + delta.x), 
                        (int)(c.y + delta.y));
            if (k.isHead())
            {
                update.k1 = cNew;
            }
            else
            {
                update.k0 = cNew;
            }
        }
        
        //Apply smoothing
        for (NetworkHandleKnot k0: pickKnot)
        {
            BezierVertexSmooth smooth = k0.getSmoothing();
            if (smooth == BezierVertexSmooth.SMOOTH
                    || smooth == BezierVertexSmooth.AUTO_SMOOTH)
            {
                NetworkHandleKnot k1 = k0.getSmoothingPeer();
                if (k1 == null)
                {
                    continue;
                }

                EdgeUpdate update0 = updateSet.getEdgeUpdate(k0.getEdge());
                Coord ck0;
                if (k0.isHead())
                {
                    ck0 = update0.k1;
                }
                else
                {
                    ck0 = update0.k0;
                }
                
                NetworkHandleEdge e = k1.getEdge();
                EdgeUpdate update1 = updateSet.getEdgeUpdate(e);
                
                Coord cv = k0.getVertex().getCoord();
                Coord ck1 = k1.getCoord();
                double len = Math2DUtil.dist(cv.x, cv.y, ck1.x, ck1.y);
                CyVector2d tan = new CyVector2d(cv.x - ck0.x, cv.y - ck0.y);
                tan.normalize();
                tan.scale(len);
                Coord cNew = new Coord((int)(tan.x + cv.x), (int)(tan.y + cv.y));
                
                if (k1.isHead())
                {
                    if (update1.k1 == null)
                    {
                        update1.k1 = cNew;
                    }
                }
                else
                {
                    if (update1.k0 == null)
                    {
                        update1.k0 = cNew;
                    }
                }
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
