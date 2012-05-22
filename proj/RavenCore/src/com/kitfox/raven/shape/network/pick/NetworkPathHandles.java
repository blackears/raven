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
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.BezierLoop2i;
import com.kitfox.coyote.shape.bezier.path.BezierLoopClosure;
import com.kitfox.coyote.shape.bezier.path.BezierPath2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathVertex2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkPath;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.util.Intersection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class NetworkPathHandles extends NetworkHandles
{
    private final NetworkPath path;
    private HashMap<Integer, HandleVertex> vertList = new HashMap<Integer, HandleVertex>();
    private HashMap<Integer, HandleEdge> edgeList = new HashMap<Integer, HandleEdge>();
    private HashMap<Integer, HandleFace> faceList = new HashMap<Integer, HandleFace>();
    private HashMap<Integer, HandleKnot> knotList = new HashMap<Integer, HandleKnot>();

//    HandleFace face;
            
    HashMap<BezierPathEdge2i, HandleEdge> edgeMap
            = new HashMap<BezierPathEdge2i, HandleEdge>();
    HashMap<BezierPathVertex2i, HandleVertex> vertMap
            = new HashMap<BezierPathVertex2i, HandleVertex>();
    
    public NetworkPathHandles(NetworkPath path)
    {
        this.path = path;
        
        for (BezierPathVertex2i v: path.getVertices())
        {
            HandleVertex handleVert = new HandleVertex(path, v);
            vertList.put(v.getId(), handleVert);
            vertMap.put(v, handleVert);

        }

        for (BezierPathEdge2i e: path.getEdges())
        {
            HandleEdge handleEdge = new HandleEdge(path, e);
            edgeList.put(e.getId(), handleEdge);
            edgeMap.put(e, handleEdge);

            if (!e.isLine())
            {
                HandleKnot k0 = new HandleKnot(e, false);
                HandleKnot k1 = new HandleKnot(e, true);
                knotList.put(k0.getIndex(), k0);
                knotList.put(k1.getIndex(), k1);
            }
        }
        
        {
            CyPath2d pathShape = path.asPath();
            HandleFace face = new HandleFace(pathShape);
            faceList.put(face.getIndex(), face);
        }
        
//        for (BezierLoop2i loop: path.getLoops())
//        {
//        }
        
    }

    /**
     * @return the path
     */
    public NetworkPath getPath()
    {
        return path;
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
     * Return edge handles, excluding those that are the closing segment of an
     * open curve.
     * 
     * @return 
     */
    public ArrayList<HandleEdge> getDrawnEdges()
    {
        ArrayList<HandleEdge> list = getEdgeList();
        for (BezierLoop2i loop: path.getLoops())
        {
            if (loop.getClosure() == BezierLoopClosure.OPEN)
            {
                HandleEdge e = getEdgeHandle(loop.getHead().getEdgeIn());
                list.remove(e);
            }
        }
        return list;
    }

    public BezierPathEdge2i<NetworkDataEdge> getEdge(NetworkHandleEdge edge)
    {
        if (edge instanceof HandleEdge)
        {
            return ((HandleEdge)edge).e;
        }
        return null;
    }

    public HandleEdge getEdgeHandle(BezierPathEdge2i e)
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
            CyPath2d path = face.getPathLocal();
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
        ArrayList<NetworkHandleEdge> retList = new ArrayList<NetworkHandleEdge>();
        
        BezierPathEdge2i<NetworkDataEdge> initEdge = getEdge(edge);
        BezierPathEdge2i<NetworkDataEdge> curEdge = initEdge;

        retList.add(edge);
        do
        {
            BezierPathVertex2i v0 = curEdge.getEnd();
            curEdge = v0.getEdgeOut();
            retList.add(getEdgeHandle(curEdge));
        } while (curEdge != initEdge);
                
        return retList;
    }
    
    //--------------------------
    public class HandleVertex implements NetworkHandleVertex
    {
        NetworkPath path;
        BezierPathVertex2i v;

        public HandleVertex(NetworkPath path, BezierPathVertex2i v)
        {
            this.path = path;
            this.v = v;
        }

        @Override
        public int getIndex()
        {
            return v.getId();
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
            BezierPathEdge2i edge = v.getEdgeIn();
            if (edge != null)
            {
                list.add(edgeMap.get(edge));
            }
            return list;
        }

        @Override
        public ArrayList<HandleEdge> getOutputEdges()
        {
            ArrayList<HandleEdge> list = new ArrayList<HandleEdge>();
            BezierPathEdge2i edge = v.getEdgeOut();
            if (edge != null)
            {
                list.add(edgeMap.get(edge));
            }
            return list;
        }

        public int getNumEdges()
        {
            return v.getNumEdges();
        }
        
        public void delete()
        {
            path.deleteVertex(v);
        }
    }
    
    
    public class HandleEdge implements NetworkHandleEdge
    {
        BezierPath2i path;
        BezierPathEdge2i<NetworkDataEdge> e;
        private CyPath2d pathShape;

        public HandleEdge(BezierPath2i path, BezierPathEdge2i<NetworkDataEdge> e)
        {
            this.path = path;
            this.e = e;
            
            BezierCurve2i c = e.asCurve();
            BezierCurve2d curveLocal = c.transfrom(coordToLocal);
            pathShape = curveLocal.asPath();
        }

        public BezierPathEdge2i<NetworkDataEdge> getEdge()
        {
            return e;
        }

        public void setEdgeLayout(RavenPaintLayout layout)
        {
            NetworkDataEdge data = e.getData();
            data.putEdge(NetworkDataTypePaintLayout.class, layout);
        }
        
        @Override
        public int getIndex()
        {
            return e.getId();
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
            return pathShape;
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
            path.deleteEdge(e);
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
    }
    
    public class HandleKnot implements NetworkHandleKnot
    {
        int index;
        BezierPathEdge2i edge;
        boolean head;

        public HandleKnot(BezierPathEdge2i e, boolean head)
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
            return head
                    ? new Coord(edge.getK1x(), edge.getK1y())
                    : new Coord(edge.getK0x(), edge.getK0y());
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
            BezierPathVertex2i v = head ? edge.getEnd() : edge.getStart();
            
            Coord ck0 = getCoord();
            Coord cv = v.getCoord();
            double len0 = Math2DUtil.dist(
                    ck0.x, ck0.y, cv.x, cv.y);
            
            BezierPathEdge2i e = v.getOtherEdge(edge);
            
            HandleKnot k1 = knotList.get(e.getId() * 2
                    + (head ? 0 : 1));
            if (k1 == null)
            {
                return null;
            }
            BezierVertexSmooth sm = k1.getSmoothing();
            if (sm != BezierVertexSmooth.SMOOTH 
                    && sm != BezierVertexSmooth.AUTO_SMOOTH)
            {
                return null;
            }

            Coord ck1 = k1.getCoord();
            double len1 = Math2DUtil.dist(ck1.x, ck1.y, cv.x, cv.y);
            double cosAngle = Math2DUtil.dot(ck0.x - cv.x, ck0.y - cv.y,
                    ck1.x - cv.x, ck1.y - cv.y) / (len0 * len1);

            if (cosAngle < smoothCutoff)
            {
                return k1;
            }
            
            return null;
        }
    }

    public class HandleFace implements NetworkHandleFace
    {
        //Face path in pixel space
        private CyPath2d pathGraph;
        private CyPath2d pathLocal;
        
        public HandleFace(CyPath2d pathGraph)
        {
            this.pathGraph = pathGraph;
            this.pathLocal = pathGraph.createTransformedPath(coordToLocal);
        }
        
        @Override
        public int getIndex()
        {
            return 0;
        }

        @Override
        public RavenPaint getPaint()
        {
            return null;
        }

        @Override
        public RavenPaintLayout getPaintLayout()
        {
            return null;
        }

        /**
         * Get the path of this face in local space
         * @return 
         */
        public CyPath2d getPathLocal()
        {
            return pathLocal;
        }

        public CyPath2d getPathGraph()
        {
            return pathGraph;
        }

        public void delete()
        {
        }
    }
    
}
