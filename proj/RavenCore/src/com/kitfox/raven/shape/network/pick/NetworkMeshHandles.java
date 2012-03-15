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

package com.kitfox.raven.shape.network.pick;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.*;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.util.Intersection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Calculates some convenient indexes for a {@link NetworkMesh}.  
 * Also calculates and indexes faces.  Used by {@link NetworkHandleSelection}
 * to indicate parts of mesh.
 * Meant to make using a {@link NetworkMesh} easier.
 *
 * @author kitfox
 */
public class NetworkMeshHandles
{
    private final NetworkMesh mesh;
    private HashMap<Integer, HandleVertex> vertList = new HashMap<Integer, HandleVertex>();
    private HashMap<Integer, HandleEdge> edgeList = new HashMap<Integer, HandleEdge>();
    private HashMap<Integer, HandleFace> faceList = new HashMap<Integer, HandleFace>();
    private HashMap<Integer, HandleKnot> knotList = new HashMap<Integer, HandleKnot>();

    HashMap<BezierMeshEdge2i, HandleEdge> edgeMap = new HashMap<BezierMeshEdge2i, HandleEdge>();
    HashMap<BezierMeshVertex2i, HandleVertex> vertMap = new HashMap<BezierMeshVertex2i, HandleVertex>();
    
    private CutLoop boundingLoop;
    
    static final double smoothAngle = 5;
    static final double smoothCutoff = Math.cos(Math.PI - Math.toRadians(smoothAngle));
        
    private static CyMatrix4d coordToLocal;
    static
    {
        CyMatrix4d m = CyMatrix4d.createIdentity();
        m.scale(.01, .01, 1);
        coordToLocal = m;
    }
    
    public NetworkMeshHandles(NetworkMesh mesh)
    {
        this.mesh = mesh;
        
        //int edgeId = 0;
        ArrayList<BezierMeshVertex2i> meshVerts = mesh.getVertices();
        for (int i = 0; i < meshVerts.size(); ++i)
        {
            BezierMeshVertex2i v = meshVerts.get(i);
            int idx = v.getId();
            
            HandleVertex handleVert = new HandleVertex(idx, v);
            vertList.put(idx, handleVert);
            vertMap.put(v, handleVert);
            
            ArrayList<BezierMeshEdge2i> edgeOut = v.getEdgesOut();
            for (BezierMeshEdge2i e: edgeOut)
            {
                HandleEdge handle = new HandleEdge(e);
                edgeList.put(handle.getIndex(), handle);
                edgeMap.put(e, handle);
                
                if (!e.isLine())
                {
                    HandleKnot k0 = new HandleKnot(e, false);
                    HandleKnot k1 = new HandleKnot(e, true);
                    knotList.put(k0.getIndex(), k0);
                    knotList.put(k1.getIndex(), k1);
                }
            }
        }
        
        buildFaces();
    }
    
    private void buildFaces()
    {
        faceList.clear();
        boundingLoop = null;
        
//        int faceId = 0;
        ArrayList<CutLoop> loops = mesh.createFaces();
        for (CutLoop loop: loops)
        {
            if (loop.isCcw())
            {
                HandleFace face = new HandleFace(loop);
                faceList.put(face.getIndex(), face);
            }
            else
            {
                if (boundingLoop != null)
                {
                    throw new RuntimeException("Mesh has more than one bounding loop");
                }
                boundingLoop = loop;
            }
        }
    }

    public void cleanupFaces()
    {
        for (HandleFace f: faceList.values())
        {
            f.cleanup();
        }
    }
    
    /**
     * @return the mesh
     */
    public NetworkMesh getMesh()
    {
        return mesh;
    }

    /**
     * @return the vertList
     */
    public ArrayList<HandleVertex> getVertList()
    {
        return new ArrayList<HandleVertex>(vertList.values());
    }

    /**
     * @return the edgeList
     */
    public ArrayList<HandleEdge> getEdgeList()
    {
        return new ArrayList<HandleEdge>(edgeList.values());
    }

    /**
     * @return the faceList
     */
    public ArrayList<HandleFace> getFaceList()
    {
        return new ArrayList<HandleFace>(faceList.values());
    }

    /**
     * @return the faceList
     */
    public ArrayList<HandleKnot> getKnotList()
    {
        return new ArrayList<HandleKnot>(knotList.values());
    }

    /**
     * @return the boundingLoop
     */
    public CutLoop getBoundingLoop()
    {
        return boundingLoop;
    }

    public BezierMeshEdge2i<NetworkDataEdge> getEdge(NetworkHandleEdge edge)
    {
        if (edge instanceof HandleEdge)
        {
            return ((HandleEdge)edge).e;
        }
        return null;
    }

    public CutLoop getFace(NetworkHandleFace face)
    {
        if (face instanceof HandleFace)
        {
            return ((HandleFace)face).loop;
        }
        return null;
    }

    public HandleEdge getEdgeHandle(BezierMeshEdge2i e)
    {
        return edgeMap.get(e);
    }

    public HandleVertex getVertexHandle(int idx)
    {
        return vertList.get(idx);
    }

    public HandleEdge getEdgeHandle(int idx)
    {
        return edgeList.get(idx);
    }

    public HandleFace getFaceHandle(int idx)
    {
        return faceList.get(idx);
    }

    public HandleKnot getKnotHandle(int idx)
    {
        return knotList.get(idx);
    }

    public ArrayList<HandleVertex> getVerticesByIds(Collection<Integer> vertIds)
    {
        ArrayList<HandleVertex> list = new ArrayList<HandleVertex>();
        for (Integer id: vertIds)
        {
            list.add(getVertexHandle(id));
        }
        return list;
    }

    public ArrayList<HandleEdge> getEdgesByIds(Collection<Integer> edgeIds)
    {
        ArrayList<HandleEdge> list = new ArrayList<HandleEdge>();
        for (Integer id: edgeIds)
        {
            list.add(getEdgeHandle(id));
        }
        return list;
    }

    public ArrayList<HandleFace> getFacesByIds(Collection<Integer> faceIds)
    {
        ArrayList<HandleFace> list = new ArrayList<HandleFace>();
        for (Integer id: faceIds)
        {
            list.add(getFaceHandle(id));
        }
        return list;
    }
    
    public ArrayList<NetworkHandleVertex> pickVertices(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        ArrayList<NetworkHandleVertex> retList = new ArrayList<NetworkHandleVertex>();
        
        CyVector2d pt = new CyVector2d();
        for (HandleVertex v: vertList.values())
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
    
    public ArrayList<NetworkHandleKnot> pickKnots(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        ArrayList<NetworkHandleKnot> retList = new ArrayList<NetworkHandleKnot>();
        
        CyVector2d pt = new CyVector2d();
        for (HandleKnot k: knotList.values())
        {
            Coord c = k.getCoord();
            pt.set(c.x / 100f, c.y / 100f);
            l2d.transformPoint(pt);
            
            if (region.contains(pt))
            {
                retList.add(k);
            }
        }
        
        return retList;
    }

    public ArrayList<NetworkHandleEdge> pickEdges(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        ArrayList<NetworkHandleEdge> retList = new ArrayList<NetworkHandleEdge>();
        
        for (HandleEdge e: edgeList.values())
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

    public ArrayList<NetworkHandleFace> pickFaces(CyRectangle2d region, 
            CyMatrix4d l2d, Intersection isect)
    {
        ArrayList<NetworkHandleFace> retList = new ArrayList<NetworkHandleFace>();
        
        for (HandleFace face: faceList.values())
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

    public ArrayList<NetworkHandleEdge> getConnectedEdges(NetworkHandleEdge edge)
    {
        BezierMeshEdge2i<NetworkDataEdge> bezEdge = getEdge(edge);
        
        ArrayList<NetworkHandleEdge> retList = new ArrayList<NetworkHandleEdge>();
        
        ArrayList<BezierMeshEdge2i> edges = bezEdge.getConnectedEdges();
        for (BezierMeshEdge2i e: edges)
        {
            retList.add(getEdgeHandle(e));
        }
        
        return retList;
    }

    
    //--------------------------
    public class HandleVertex implements NetworkHandleVertex
    {
        int index;
        BezierMeshVertex2i v;

        public HandleVertex(int index, BezierMeshVertex2i v)
        {
            this.index = index;
            this.v = v;
        }

        @Override
        public int getIndex()
        {
            return index;
        }

        @Override
        public Coord getCoord()
        {
            return v.getCoord();
        }

        @Override
        public ArrayList<HandleEdge> getInputEdges()
        {
            ArrayList<HandleEdge> list = new ArrayList<HandleEdge>();
            ArrayList<BezierMeshEdge2i> edges = v.getEdgesIn();
            for (BezierMeshEdge2i e: edges)
            {
                list.add(edgeMap.get(e));
            }
            return list;
        }

        @Override
        public ArrayList<HandleEdge> getOutputEdges()
        {
            ArrayList<HandleEdge> list = new ArrayList<HandleEdge>();
            ArrayList<BezierMeshEdge2i> edges = v.getEdgesOut();
            for (BezierMeshEdge2i e: edges)
            {
                list.add(edgeMap.get(e));
            }
            return list;
        }

        public int getNumEdges()
        {
            return v.getNumEdges();
        }
        
        public void delete()
        {
            if (v.getNumEdges() == 2)
            {
                //Connect existing edges
                BezierMeshEdge2i e0 = v.getEdge(0);
                BezierMeshEdge2i e1 = v.getEdge(1);
                
                BezierMeshVertex2i v0, v1;
                BezierVertexSmooth s0, s1;
                Coord k0, k1;
                if (e0.getStart() == v)
                {
                    v0 = e0.getEnd();
                    s0 = e0.getSmooth1();
                    k0 = e0.getK1();
                }
                else
                {
                    v0 = e0.getStart();
                    s0 = e0.getSmooth0();
                    k0 = e0.getK0();
                }

                if (e1.getStart() == v)
                {
                    v1 = e1.getEnd();
                    s1 = e1.getSmooth1();
                    k1 = e1.getK1();
                }
                else
                {
                    v1 = e1.getStart();
                    s1 = e1.getSmooth0();
                    k1 = e1.getK0();
                }
                
                mesh.removeEdge(e0);
                mesh.removeEdge(e1);
                mesh.removeEmptyVertex(v);
                
                edgeList.remove(e0.getId());
                edgeList.remove(e1.getId());
                vertList.remove(v.getId());
                
                Coord c0 = v0.getCoord();
                Coord c1 = v1.getCoord();
                
                //Add merged edge
                NetworkDataEdge data = new NetworkDataEdge((NetworkDataEdge)e0.getData());
                BezierCubic2i curve = new BezierCubic2i(
                        c0.x, c0.y, k0.x, k0.y, k1.x, k1.y, c1.x, c1.y);
                ArrayList<BezierMeshEdge2i> newEdgeList = mesh.addEdge(curve, data);
                BezierMeshEdge2i newEdge = newEdgeList.get(0);
                newEdge.setSmooth0(s0);
                newEdge.setSmooth1(s1);
                
                edgeList.put(newEdge.getId(), new HandleEdge(newEdge));
            }
            else
            {
                ArrayList<BezierMeshEdge2i> edgeIn = v.getEdgesIn();
                ArrayList<BezierMeshEdge2i> edgeOut = v.getEdgesOut();
                
                for (BezierMeshEdge2i e: edgeIn)
                {
                    mesh.removeEdge(e);
                }
                
                for (BezierMeshEdge2i e: edgeOut)
                {
                    mesh.removeEdge(e);
                }
                
                mesh.removeEmptyVertices();
                buildFaces();
                cleanupFaces();
            }
        }
    }

    public class HandleEdge implements NetworkHandleEdge
    {
        int index;
        BezierMeshEdge2i<NetworkDataEdge> e;
        private CyPath2d path;

        public HandleEdge(BezierMeshEdge2i<NetworkDataEdge> e)
        {
            this.index = e.getId();
            this.e = e;
            BezierCurve2i c = e.asCurve();
            BezierCurve2d curveLocal = c.transfrom(coordToLocal);
            path = curveLocal.asPath();
        }

        public BezierMeshEdge2i<NetworkDataEdge> getEdge()
        {
            return e;
        }
        
        @Override
        public int getIndex()
        {
            return index;
        }
        
        @Override
        public RavenStroke getStroke()
        {
            NetworkDataEdge data = e.getData();
            return data.getEdge(NetworkDataTypeStroke.class);
        }

        @Override
        public RavenPaint getPaint()
        {
            NetworkDataEdge data = e.getData();
            return data.getEdge(NetworkDataTypePaint.class);
        }

        @Override
        public RavenPaintLayout getPaintLayout()
        {
            NetworkDataEdge data = e.getData();
            return data.getEdge(NetworkDataTypePaintLayout.class);
        }

        @Override
        public BezierCurve2d getCurveLocal()
        {
            BezierCurve2i c = e.asCurve();
            return c.asDouble();
//            return c.transfrom(coordToLocal);
        }

        /**
         * @return the path
         */
        public CyPath2d getPath()
        {
            return path;
        }

        @Override
        public NetworkHandleVertex getStartVertex()
        {
            return vertMap.get(e.getStart());
        }

        @Override
        public NetworkHandleVertex getEndVertex()
        {
            return vertMap.get(e.getEnd());
        }

        @Override
        public boolean isLine()
        {
            return e.isLine();
        }

        @Override
        public void remove()
        {
            mesh.removeEdge(e);
        }

        @Override
        public BezierCurve2i getCurveGraph()
        {
            return e.asCurve();
        }

        @Override
        public NetworkDataEdge getData()
        {
            return e.getData();
        }

        @Override
        public BezierVertexSmooth getSmooth0()
        {
            return e.getSmooth0();
        }

        @Override
        public BezierVertexSmooth getSmooth1()
        {
            return e.getSmooth1();
        }

        @Override
        public void setSmooth0(BezierVertexSmooth smooth)
        {
            e.setSmooth0(smooth);
        }

        @Override
        public void setSmooth1(BezierVertexSmooth smooth)
        {
            e.setSmooth1(smooth);
        }

        public void delete()
        {
            mesh.removeEdge(e);

            mesh.removeEmptyVertices();
            buildFaces();
            cleanupFaces();
        }
    }
    
    public class HandleKnot implements NetworkHandleKnot
    {
        int index;
        BezierMeshEdge2i edge;
        boolean head;

        public HandleKnot(BezierMeshEdge2i e, boolean head)
        {
            this.index = e.getId() * 2 + (head ? 1 : 0);
            this.edge = e;
            this.head = head;
        }

        @Override
        public int getIndex()
        {
            return index;
        }

        @Override
        public Coord getCoord()
        {
            return head ? edge.getK1() : edge.getK0();
        }

        @Override
        public HandleVertex getVertex()
        {
            return vertMap.get(head ? edge.getEnd() : edge.getStart());
        }

        @Override
        public HandleEdge getEdge()
        {
            return edgeMap.get(edge);
        }

        @Override
        public boolean isHead()
        {
            return head;
        }

        @Override
        public void setSmoothing(BezierVertexSmooth smooth)
        {
            if (head)
            {
                edge.setSmooth1(smooth);
            }
            else
            {
                edge.setSmooth0(smooth);
            }
        }

        @Override
        public BezierVertexSmooth getSmoothing()
        {
            return head ? edge.getSmooth1() : edge.getSmooth0();
        }

        /**
         * Find a smooth knot on the opposite side of this vertex such that
         * the angle between these two knots is greater than (PI - smoothAngle)
         * 
         * @return 
         */
        @Override
        public NetworkHandleKnot getSmoothingPeer()
        {
            BezierMeshVertex2i v = head ? edge.getEnd() : edge.getStart();
            
            Coord ck0 = getCoord();
            Coord cv = v.getCoord();
            double len0 = Math2DUtil.dist(ck0.x, ck0.y, cv.x, cv.y);
            
            for (BezierMeshEdge2i e: 
                    (ArrayList<BezierMeshEdge2i>)v.getEdgesOut())
            {
                HandleKnot k1 = knotList.get(e.getId() * 2);
                if (k1 == null)
                {
                    continue;
                }
                BezierVertexSmooth sm = k1.getSmoothing();
                if (sm != BezierVertexSmooth.SMOOTH 
                        && sm != BezierVertexSmooth.AUTO_SMOOTH)
                {
                    continue;
                }
                
                Coord ck1 = k1.getCoord();
                double len1 = Math2DUtil.dist(ck1.x, ck1.y, cv.x, cv.y);
                double cosAngle = Math2DUtil.dot(ck0.x - cv.x, ck0.y - cv.y,
                        ck1.x - cv.x, ck1.y - cv.y) / (len0 * len1);
                
                if (cosAngle < smoothCutoff)
                {
                    return k1;
                }
            }
            
            for (BezierMeshEdge2i e: 
                    (ArrayList<BezierMeshEdge2i>)v.getEdgesIn())
            {
                HandleKnot k1 = knotList.get(e.getId() * 2 + 1);
                if (k1 == null)
                {
                    continue;
                }
                BezierVertexSmooth sm = k1.getSmoothing();
                if (sm != BezierVertexSmooth.SMOOTH 
                        && sm != BezierVertexSmooth.AUTO_SMOOTH)
                {
                    continue;
                }
                
                Coord ck1 = k1.getCoord();
                double len1 = Math2DUtil.dist(ck1.x, ck1.y, cv.x, cv.y);
                double cosAngle = Math2DUtil.dot(ck0.x - cv.x, ck0.y - cv.y,
                        ck1.x - cv.x, ck1.y - cv.y) / (len0 * len1);
                
                if (cosAngle < smoothCutoff)
                {
                    return k1;
                }
            }
            
            return null;
        }
        
        
    }

    public class HandleFace implements NetworkHandleFace
    {
        final int index;
        CutLoop loop;
        
        RavenPaint paint;
        RavenPaintLayout layout;
        //Face path in pixel space
        CyPath2d path;

        public HandleFace(CutLoop loop)
        {
//            this.index = index;
            this.loop = loop;
            
            this.path = new CyPath2d();
            loop.appendPath(path);
            path = path.createTransformedPath(coordToLocal);
            
            //Create a unique hash for the face by combining index of
            // minimum edge with it's half side
            int minIndex = Integer.MAX_VALUE;
            for (CutSegHalf half: loop.getSegs())
            {
                BezierMeshEdge2i<NetworkDataEdge> e
                        = (BezierMeshEdge2i<NetworkDataEdge>)half.getEdge();
                minIndex = Math.min(minIndex, e.getId() * 2 
                        + (half.isRight() ? 1 : 0));
            }
            this.index = minIndex;
            
            //Determine paint for this face
            for (CutSegHalf half: loop.getSegs())
            {
                BezierMeshEdge2i<NetworkDataEdge> e
                        = (BezierMeshEdge2i<NetworkDataEdge>)half.getEdge();
                NetworkDataEdge data = e.getData();
                
                if (data == null)
                {
                    continue;
                }
                
                if (half.isRight())
                {
                    paint = data.getRight(NetworkDataTypePaint.class);
                    layout = data.getRight(NetworkDataTypePaintLayout.class);
                }
                else
                {
                    paint = data.getLeft(NetworkDataTypePaint.class);
                    layout = data.getLeft(NetworkDataTypePaintLayout.class);
                }
            }
        }

        @Override
        public int getIndex()
        {
            return index;
        }

        @Override
        public RavenPaint getPaint()
        {
            return paint;
        }

        @Override
        public RavenPaintLayout getPaintLayout()
        {
            return layout;
        }

        public CyPath2d getPath()
        {
            return path;
        }

        public void cleanup()
        {
            RavenPaint curPaint = null;
            RavenPaintLayout curLayout = null;
            
            //Find a consistent paint color
            if (loop.isCcw())
            {
                //Only ccw loops should have paints
                for (CutSegHalf seg: loop.getSegs())
                {
                    BezierMeshEdge2i e = seg.getEdge();
                    NetworkDataEdge data = (NetworkDataEdge)e.getData();

                    if (data != null && curPaint == null)
                    {
                        if (seg.isRight())
                        {
                            curPaint = data.getRight(NetworkDataTypePaint.class);
                            curLayout = data.getRight(NetworkDataTypePaintLayout.class);
                        }
                        else
                        {
                            curPaint = data.getLeft(NetworkDataTypePaint.class);
                            curLayout = data.getLeft(NetworkDataTypePaintLayout.class);
                        }

                        if (curPaint != null)
                        {
                            break;
                        }
                    }
                }
            }
            
            this.paint = curPaint;
            this.layout = curLayout;
            
            //Set all edges to common face paint
            for (CutSegHalf seg: loop.getSegs())
            {
                BezierMeshEdge2i e = seg.getEdge();
                NetworkDataEdge data = (NetworkDataEdge)e.getData();
                
                if (data == null)
                {
                    data = new NetworkDataEdge();
                    e.setData(data);
                }
                
                if (seg.isRight())
                {
                    data.putRight(NetworkDataTypePaint.class, curPaint);
                    data.putRight(NetworkDataTypePaintLayout.class, curLayout);
                }
                else
                {
                    data.putLeft(NetworkDataTypePaint.class, curPaint);
                    data.putLeft(NetworkDataTypePaintLayout.class, curLayout);
                }
            }
        }

        public void delete()
        {
            //Set all edges to null face paint
            for (CutSegHalf seg: loop.getSegs())
            {
                BezierMeshEdge2i e = seg.getEdge();
                NetworkDataEdge data = (NetworkDataEdge)e.getData();
                
                if (data == null)
                {
                    data = new NetworkDataEdge();
                    e.setData(data);
                }
                
                if (seg.isRight())
                {
                    data.putRight(NetworkDataTypePaint.class, null);
                    data.putRight(NetworkDataTypePaintLayout.class, null);
                }
                else
                {
                    data.putLeft(NetworkDataTypePaint.class, null);
                    data.putLeft(NetworkDataTypePaintLayout.class, null);
                }
            }
        }
        
    }
    
}
