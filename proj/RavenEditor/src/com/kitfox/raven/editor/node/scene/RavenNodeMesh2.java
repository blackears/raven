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
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.*;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutSegHalf;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
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
public class RavenNodeMesh2 extends RavenNodeXformable
        implements ServiceBezierMesh, ServiceShapeManip
{
    public static final String PROP_MESH = "mesh";
    public final PropertyWrapper<RavenNodeMesh2, NetworkMesh> mesh =
            new PropertyWrapper(
            this, PROP_MESH, NetworkMesh.class, new NetworkMesh());

    final double TESS_FLAT_SQ = 2500;
    
    protected RavenNodeMesh2(int uid)
    {
        super(uid);
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
        stack.pushFrame(null);
        stack.scale(.01, .01, 1);
        
//        CyMatrix4d mvp = stack.getModelViewProjXform();
//        mvp.scale(.01, .01, 1);
        
        for (FaceLayout lay: meshLayout.paths)
        {
            if (lay.paint != null)
            {
                lay.paint.fillShape(stack, lay.paintLayout, lay.vertBuf);
            }
            
//            CyMaterialColorDrawRecord rec =
//                    CyMaterialColorDrawRecordFactory.inst().allocRecord();
//
//            rec.setColor(lay.color);
//            rec.setMesh(lay.vertBuf);
//            rec.setOpacity(1);
//            rec.setMvpMatrix(mvp);
//            
//            stack.addDrawRecord(rec);
        }
        
        for (EdgeLayout lay: meshLayout.edgeLayouts)
        {
            if (lay.paint != null && lay.vertBuf != null)
            {
                lay.paint.fillShape(stack, lay.paintLayout, lay.vertBuf);
            }
            
            
//            CyMaterialColorDrawRecord rec =
//                    CyMaterialColorDrawRecordFactory.inst().allocRecord();
//
//            rec.setColor(lay.color);
//            rec.setMesh(lay.vertBuf);
//            rec.setOpacity(1);
//            rec.setMvpMatrix(mvp);
//            
//            stack.addDrawRecord(rec);            
        }
        
        stack.popFrame();
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
        ArrayList<NetworkHandleVertex> retList = new ArrayList<NetworkHandleVertex>();
        
        CyVector2d pt = new CyVector2d();
        for (HandleVertex v: handles.getVertList())
        {
            Coord c = v.getCoord();
            pt.set(c.x / 100f, c.y / 100f);
            l2d.transformPoint(pt);
            
            if (region.contains(pt))
            {
                retList.add(v);
            }
        }
        
        return retList;
    }

    @Override
    public ArrayList<NetworkHandleEdge> pickEdges(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        NetworkMeshHandles handles = getMeshHandles();
        ArrayList<NetworkHandleEdge> retList = new ArrayList<NetworkHandleEdge>();
        
        for (HandleEdge e: handles.getEdgeList())
        {
            CyPath2d path = e.getPath();
            CyPath2d devPath = path.createTransformedPath(l2d);

            CyRectangle2d bounds = devPath.getBounds();
            
            switch (isect)
            {
                case CONTAINS:
                    if (bounds.contains(region))
                    {
                        retList.add(e);
                    }
                    break;
                case INTERSECTS:
                    if (devPath.intersects(region))
                    {
                        retList.add(e);
                    }
                    break;
                case INSIDE:
                    if (devPath.contains(region))
                    {
                        retList.add(e);
                    }
                    break;
            }
        }
        
        return retList;
    }

    @Override
    public ArrayList<NetworkHandleFace> pickFaces(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        NetworkMeshHandles handles = getMeshHandles();
        ArrayList<NetworkHandleFace> retList = new ArrayList<NetworkHandleFace>();
        
        for (HandleFace face: handles.getFaceList())
        {
            CyPath2d path = face.getPath();
            CyPath2d devPath = path.createTransformedPath(l2d);

            CyRectangle2d bounds = devPath.getBounds();
            
            switch (isect)
            {
                case CONTAINS:
                    if (bounds.contains(region))
                    {
                        retList.add(face);
                    }
                    break;
                case INTERSECTS:
                    if (devPath.intersects(region))
                    {
                        retList.add(face);
                    }
                    break;
                case INSIDE:
                    if (devPath.contains(region))
                    {
                        retList.add(face);
                    }
                    break;
            }
        }
        
        return retList;
    }

    @Override
    public ArrayList<NetworkHandleEdge> getConnectedEdges(NetworkHandleEdge edge)
    {
        NetworkMeshHandles handles = getMeshHandles();
        BezierMeshEdge2i<NetworkDataEdge> bezEdge = handles.getEdge(edge);
        
        ArrayList<NetworkHandleEdge> retList = new ArrayList<NetworkHandleEdge>();
        
        ArrayList<BezierMeshEdge2i> edges = bezEdge.getConnectedEdges();
        for (BezierMeshEdge2i e: edges)
        {
            retList.add(handles.getEdgeHandle(e));
        }
        
        return retList;
    }

    @Override
    public void setEdgePaintAndStroke(RavenPaint paint, RavenStroke stroke, 
            Collection<NetworkHandleEdge> edges, boolean history)
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
    public void setEdgePaint(RavenPaint paint, Collection<NetworkHandleEdge> edges,
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
    public void setEdgeStroke(RavenStroke stroke, Collection<NetworkHandleEdge> edges,
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
    public void setFacePaint(RavenPaint paint, Collection<NetworkHandleFace> faces,
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
    public static class Provider extends NodeObjectProvider<RavenNodeMesh2>
    {
        public Provider()
        {
            super(RavenNodeMesh2.class, "Mesh2", "/icons/node/mesh.png");
        }

        @Override
        public RavenNodeMesh2 createNode(int uid)
        {
            return new RavenNodeMesh2(uid);
        }
    }
    
}
