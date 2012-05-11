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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.*;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutSegHalf;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierMesh;
import com.kitfox.raven.editor.node.tools.common.shape.ServiceShapeManip;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleFace;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleFace;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleVertex;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author kitfox
 */
public class RavenNodeMesh extends RavenNodeXformable
        implements ServiceBezierMesh, ServiceShapeManip
{
    public static final String PROP_MESH = "mesh";
    public final PropertyWrapper<RavenNodeMesh, NetworkMesh> mesh =
            new PropertyWrapper(
            this, PROP_MESH, NetworkMesh.class, new NetworkMesh());

    static final double TESS_FLAT_SQ = 2500;
    
    static final CyMatrix4d meshToLocal;
    static {
        meshToLocal = CyMatrix4d.createIdentity();
        meshToLocal.scale(.01, .01, 1);
    }
    
    protected RavenNodeMesh(int uid)
    {
        super(uid);
    }

    public static CyMatrix4d getMeshToLocal()
    {
        return new CyMatrix4d(meshToLocal);
    }
    
    @Override
    protected void renderContent(RenderContext ctx)
    {
        MeshLayout meshLayout = getFaceSet();
        if (meshLayout == null)
        {
            return;
        }
        
        CyDrawStack stack = ctx.getDrawStack();
//        stack.pushFrame(null);
//        stack.scale(.01, .01, 1);
        
        for (FaceLayout lay: meshLayout.paths)
        {
            if (lay.paint != null)
            {
                lay.paint.fillShape(stack, lay.paintLayout, lay.vertBuf,
                        meshToLocal);
            }
        }
        
        for (EdgeLayout lay: meshLayout.edgeLayouts)
        {
            if (lay.paint != null && lay.vertBuf != null)
            {
                lay.paint.fillShape(stack, lay.paintLayout, lay.vertBuf,
                        meshToLocal);
            }
        }
        
//        stack.popFrame();
    }

    protected MeshLayout getFaceSet()
    {
        MeshLayout faceSet = mesh.getUserCacheValue(MeshLayout.class);
        if (faceSet == null)
        {
            NetworkMesh curMesh = mesh.getValue();
            if (curMesh == null)
            {
                return null;
            }
            faceSet = new MeshLayout(curMesh);
            mesh.setUserCacheValue(MeshLayout.class, faceSet);
        }
        return faceSet;
    }
    
    @Override
    public CyShape getShapePickLocal()
    {
        MeshLayout faceSet = getFaceSet();
        return faceSet.combinedPath;
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NetworkMesh getNetworkMesh()
    {
        return new NetworkMesh(mesh.getValue());
    }
    
    @Override
    public void setNetworkMesh(NetworkMesh mesh, boolean history)
    {
        this.mesh.setValue(mesh, history);
    }

    private NetworkMeshHandles getMeshHandles()
    {
        NetworkMeshHandles handles = mesh.getUserCacheValue(NetworkMeshHandles.class);
        if (handles == null)
        {
            handles = new NetworkMeshHandles(mesh.getValue());
            mesh.setUserCacheValue(NetworkMeshHandles.class, handles);
        }
        return handles;
    }
    
    @Override
    public ArrayList<NetworkHandleVertex> pickVertices(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.pickVertices(region, l2d, isect);
    }

    @Override
    public ArrayList<NetworkHandleEdge> pickEdges(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.pickEdges(region, l2d, isect);
    }
    
    @Override
    public ArrayList<NetworkHandleFace> pickFaces(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.pickFaces(region, l2d, isect);
    }


    @Override
    public ArrayList<NetworkHandleEdge> getConnectedEdges(NetworkHandleEdge edge)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.getConnectedEdges(edge);
    }

    @Override
    public void setEdgePaintAndStroke(RavenPaint paint, RavenStroke stroke, 
            Collection<? extends NetworkHandleEdge> edges, boolean history)
    {
        //Create a new mesh and set coresponding edge data
        NetworkMesh oldMesh = mesh.getValue();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierMeshEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypePaint.class, paint);
            data.putEdge(NetworkDataTypeStroke.class, stroke);
        }

        mesh.setValue(newMesh, history);
    }

    @Override
    public void setEdgePaint(RavenPaint paint, 
            Collection<? extends NetworkHandleEdge> edges,
            boolean history)
    {
        //Create a new mesh and set coresponding edge data
        NetworkMesh oldMesh = mesh.getValue();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierMeshEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypePaint.class, paint);
        }

        mesh.setValue(newMesh, history);
    }

    @Override
    public void setEdgeStroke(RavenStroke stroke, 
            Collection<? extends NetworkHandleEdge> edges,
            boolean history)
    {
        //Create a new mesh and set coresponding edge data
        NetworkMesh oldMesh = mesh.getValue();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierMeshEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypeStroke.class, stroke);
        }

        mesh.setValue(newMesh, history);
    }

    @Override
    public void setFacePaint(RavenPaint paint, 
            Collection<? extends NetworkHandleFace> faces,
            boolean history)
    {
        //Create a new mesh and set coresponding edge data
        NetworkMesh oldMesh = mesh.getValue();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        for (NetworkHandleFace face: faces)
        {
            HandleFace handle = newHandles.getFaceHandle(face.getIndex());
            
            CutLoop loop = newHandles.getFace(handle);
            for (CutSegHalf half: loop.getSegs())
            {
                BezierMeshEdge2i<NetworkDataEdge> bezEdge = half.getEdge();
                if (bezEdge == null)
                {
                    //Is auto-inserted island connecting segment
                    continue;
                }
                NetworkDataEdge data = bezEdge.getData();

                if (half.isRight())
                {
                    data.putRight(NetworkDataTypePaint.class, paint);
                }
                else
                {
                    data.putLeft(NetworkDataTypePaint.class, paint);
                }
            }
        }

        mesh.setValue(newMesh, history);
    }

    @Override
    public CyMatrix4d getGraphToWorldXform()
    {
        CyMatrix4d g2w = getLocalToWorldTransform((CyMatrix4d)null);
        g2w.scale(.01, .01, 1);
        return g2w;
    }

    @Override
    public ArrayList<HandleEdge> getEdges()
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.getEdgeList();
    }

    @Override
    public ArrayList<HandleVertex> getVertices()
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles.getVertList();
    }

    @Override
    public ArrayList<? extends NetworkHandleEdge> getEdgesByIds(ArrayList<Integer> edgeIds)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles == null ? null : handles.getEdgesByIds(edgeIds);
    }

    @Override
    public ArrayList<? extends NetworkHandleFace> getFacesByIds(ArrayList<Integer> faceIds)
    {
        NetworkMeshHandles handles = getMeshHandles();
        return handles == null ? null : handles.getFacesByIds(faceIds);
    }

    
    //----------------------------------------
    protected class MeshLayout //implements PathVisitor
    {
        ArrayList<EdgeLayout> edgeLayouts = new ArrayList<EdgeLayout>();
        ArrayList<FaceLayout> paths = new ArrayList<FaceLayout>();
        CyPath2d combinedPath;

        public MeshLayout(NetworkMesh mesh)
        {
            buildEdges(mesh);
            
            ArrayList<CutLoop> faces = mesh.createFaces();

            for (CutLoop loop: faces)
            {
                if (loop.isCcw())
                {
                    paths.add(new FaceLayout(loop));
                }
                else
                {
                    combinedPath = loop.createPath();
                }
            }
        }
        
        private void buildEdges(NetworkMesh mesh)
        {
            ArrayList<BezierMeshEdge2i> edges = mesh.getEdges();
            HashSet<BezierMeshEdge2i> visited = new HashSet<BezierMeshEdge2i>();
            
            while (!edges.isEmpty())
            {
                ArrayList<BezierMeshEdge2i> edgeChain = new ArrayList<BezierMeshEdge2i>();
                
                BezierMeshEdge2i curEdge = edges.remove(edges.size() - 1);
                edgeChain.add(curEdge);
                visited.add(curEdge);
                
                //Trace back along continiously connected verts
                for (BezierMeshEdge2i leadEdge = curEdge;;)
                {
                    BezierMeshVertex2i v = curEdge.getStart();
                    if (v.getNumEdges() != 2)
                    {
                        break;
                    }
                    BezierMeshEdge2i adjEdge = v.getEdge(0);
                    if (adjEdge == leadEdge)
                    {
                        adjEdge = v.getEdge(1);
                    }
                    
                    if (visited.contains(adjEdge))
                    {
                        break;
                    }
                    
                    Object data0 = leadEdge.getData();
                    Object data1 = adjEdge.getData();
                    if (!((data0 == null && data1 == null) ||
                            (data0 != null && data0.equals(data1))))
                    {
                        //Break if data not equal
                        break;
                    }
                    
                    edgeChain.add(0, adjEdge);
                    visited.add(adjEdge);
                    edges.remove(adjEdge);
                }
                
                //Trace forward along continiously connected verts
                for (BezierMeshEdge2i leadEdge = curEdge;;)
                {
                    BezierMeshVertex2i v = curEdge.getEnd();
                    if (v.getNumEdges() != 2)
                    {
                        break;
                    }
                    BezierMeshEdge2i adjEdge = v.getEdge(0);
                    if (adjEdge == leadEdge)
                    {
                        adjEdge = v.getEdge(1);
                    }
                    
                    if (visited.contains(adjEdge))
                    {
                        break;
                    }
                    
                    Object data0 = leadEdge.getData();
                    Object data1 = adjEdge.getData();
                    if (!((data0 == null && data1 == null) ||
                            (data0 != null && data0.equals(data1))))
                    {
                        //Break if data not equal
                        break;
                    }
                    
                    edgeChain.add(adjEdge);
                    visited.add(adjEdge);
                    edges.remove(adjEdge);
                }
                
                edgeLayouts.add(new EdgeLayout(edgeChain));
            }
        }
    }
    
    class EdgeLayout
    {
        CyPath2d path;
        CyColor4f color;
        CyVertexBuffer vertBuf;
        
        CyPath2d strokedPath;
        RavenStroke stroke;
        RavenPaint paint;
        RavenPaintLayout paintLayout;
        
        EdgeLayout(ArrayList<BezierMeshEdge2i> edgeChain)
        {
            BezierMeshEdge2i edgeStart = edgeChain.get(0);
            
            //NetworkDataEdge data = (NetworkDataEdge)edgeStart.getData();

            path = new CyPath2d();
            Coord c0 = edgeStart.getStart().getCoord();
            path.moveTo(c0.x, c0.y);

            for (int i = 0; i < edgeChain.size(); ++i)
            {
                BezierMeshEdge2i e = edgeChain.get(i);
                if (e.isLine())
                {
                    Coord c1 = e.getEnd().getCoord();
                    path.lineTo(c1.x, c1.y);
                }
                else
                {
                    Coord c1 = e.getEnd().getCoord();
                    Coord k0 = e.getK0();
                    Coord k1 = e.getK1();
                    path.cubicTo(k0.x, k0.y, k1.x, k1.y, c1.x, c1.y);
                }
            }
            
            //Decorate
            NetworkDataEdge data =  (NetworkDataEdge)edgeStart.getData();
            stroke = data.getEdge(NetworkDataTypeStroke.class);
            paint = data.getEdge(NetworkDataTypePaint.class);
            paintLayout = data.getEdge(NetworkDataTypePaintLayout.class);
            
            if (stroke != null)
            {
                CyStroke cyStroke = stroke.getStroke().scale(100);
                CyPath2d pathStroked = cyStroke.outlineShape(path);
                
                ShapeMeshProvider prov = new ShapeMeshProvider(pathStroked, TESS_FLAT_SQ);
                vertBuf = new CyVertexBuffer(prov);
            }

            //Color for debugging - remove later
            color = CyColor4f.randomRGB();
        }
    }
    
    class FaceLayout
    {
        CyPath2d path;
        CyColor4f color;
        CyVertexBuffer vertBuf;
        RavenPaint paint;
        RavenPaintLayout paintLayout;

        private FaceLayout(CutLoop loop)
        {
            this.path = loop.createPath();
            this.color = CyColor4f.randomRGB();
            ShapeMeshProvider prov = new ShapeMeshProvider(path, TESS_FLAT_SQ);
            vertBuf = new CyVertexBuffer(prov);

            //Decorate
            ArrayList<CutSegHalf> segs = loop.getSegs();
            for (CutSegHalf seg: segs)
            {
                BezierMeshEdge2i edge = (BezierMeshEdge2i)seg.getEdge();
                if (edge == null)
                {
                    continue;
                }
                NetworkDataEdge data = (NetworkDataEdge)edge.getData();
                if (data == null)
                {
                    continue;
                }
                
                //We found data.  Use to decorate face
                if (seg.isRight())
                {
                    paint = data.getRight(NetworkDataTypePaint.class);
                    paintLayout = data.getRight(NetworkDataTypePaintLayout.class);
                    break;
                }
                else
                {
                    paint = data.getLeft(NetworkDataTypePaint.class);
                    paintLayout = data.getLeft(NetworkDataTypePaintLayout.class);
                    break;
                }
            }
        }
    }
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeMesh>
    {
        public Provider()
        {
            super(RavenNodeMesh.class, "Mesh", "/icons/node/mesh.png");
        }

        @Override
        public RavenNodeMesh createNode(int uid)
        {
            return new RavenNodeMesh(uid);
        }
    }
    
}
