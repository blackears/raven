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

package com.kitfox.coyote.shape.bezier.path;

import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyPathIterator;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.BezierPathCutter2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.bezier.path.cut.GraphDataManager;
import com.kitfox.coyote.shape.bezier.path.cut.Segment;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierPath2i<VertexData, EdgeData>
{
    protected ArrayList<BezierLoop2i<VertexData, EdgeData>> loops = 
            new ArrayList<BezierLoop2i<VertexData, EdgeData>>();
    protected HashMap<Integer, 
            BezierLoop2i<VertexData, EdgeData>> loopMap
            = new HashMap<Integer, BezierLoop2i<VertexData, EdgeData>>();
    protected HashMap<Integer, 
            BezierPathVertex2i<VertexData>> vertMap
            = new HashMap<Integer, BezierPathVertex2i<VertexData>>();
    protected HashMap<Integer, 
            BezierPathEdge2i<EdgeData>> edgeMap
            = new HashMap<Integer, BezierPathEdge2i<EdgeData>>();

    final GraphDataManager dataMgr;
    int nextLoopId;
    int nextVertId;
    int nextEdgeId;
    
    public BezierPath2i(GraphDataManager dataMgr)
    {
        this.dataMgr = dataMgr;
    }

    public BezierPath2i(BezierPath2i<VertexData, EdgeData> path)
    {
        this(path.dataMgr);
        this.nextLoopId = path.nextLoopId;
        this.nextVertId = path.nextVertId;
        this.nextEdgeId = path.nextEdgeId;
        
        for (BezierPathVertex2i<VertexData> v: path.vertMap.values())
        {
            copyVertexSameId(v);
        }

        for (BezierPathEdge2i<EdgeData> e: path.edgeMap.values())
        {
            copyEdgeSameId(e);
        }
        
        for (BezierLoop2i<VertexData, EdgeData> l: path.loops)
        {
            copyLoopSameId(l);
        }
    }
    
    private BezierPathVertex2i<VertexData> copyVertexSameId(BezierPathVertex2i<VertexData> v0)
    {
        BezierPathVertex2i v1 = new BezierPathVertex2i(
                v0.getId(), v0.getCoord(),
                (VertexData)dataMgr.copyVertexData(v0.getData()));
        vertMap.put(v1.getId(), v1);
        return v1;
    }
    
    BezierPathVertex2i<VertexData> createVertex(int x, int y)
    {
        return createVertex(new Coord(x, y));
    }
    
    BezierPathVertex2i<VertexData> createVertex(Coord c)
    {
        BezierPathVertex2i v = new BezierPathVertex2i(
                nextVertId++, c, dataMgr.createDefaultVertexData(c));
        vertMap.put(v.getId(), v);
        return v;
    }

    protected void createVertex(int id, int x, int y, VertexData dataMap)
    {
        if (vertMap.containsKey(id))
        {
            throw new IllegalArgumentException("id already assigned");
        }
        BezierPathVertex2i v = new BezierPathVertex2i(
                id, new Coord(x, y), dataMap);
        vertMap.put(v.getId(), v);
        nextVertId = Math.max(nextVertId, id + 1);
    }

    public BezierPathVertex2i<VertexData> getVertex(int id)
    {
        return vertMap.get(id);
    }

    public ArrayList<BezierPathVertex2i<VertexData>> getVertices()
    {
        return new ArrayList<BezierPathVertex2i<VertexData>>(vertMap.values());
    }

    void removeVertex(BezierPathVertex2i<VertexData> v)
    {
        vertMap.remove(v.getId());
    }
    
    private BezierPathEdge2i<EdgeData> copyEdgeSameId(
            BezierPathEdge2i<EdgeData> e0)
    {
        BezierPathEdge2i<EdgeData> e1 = 
                new BezierPathEdge2i<EdgeData>(
                e0.getId(), 
                getVertex(e0.getStart().getId()),
                getVertex(e0.getEnd().getId()),
                (EdgeData)dataMgr.copyEdgeData(e0.getData()),
                e0.getSmooth0(),
                e0.getSmooth1(),
                e0.getK0x(), e0.getK0y(),
                e0.getK1x(), e0.getK1y());
        edgeMap.put(e1.getId(), e1);
        return e1;
    }

    protected void createEdge(int id, 
            BezierPathVertex2i start, BezierPathVertex2i end,
            EdgeData data,
            BezierVertexSmooth smooth0,
            BezierVertexSmooth smooth1,
            int k0x, int k0y, int k1x, int k1y)
    {
        if (edgeMap.containsKey(id))
        {
            throw new IllegalArgumentException("id already assigned");
        }
        BezierPathEdge2i e = new BezierPathEdge2i(
                id, 
                start, end,
                data,
                smooth0, smooth1, 
                k0x, k0y, k1x, k1y);
        edgeMap.put(e.getId(), e);
        nextEdgeId = Math.max(nextEdgeId, id + 1);
    }
    
    BezierPathEdge2i<EdgeData> createEdge(
            BezierPathVertex2i start, BezierPathVertex2i end)
    {
        BezierCubic2i curve = new BezierLine2i(
                start.getX(), start.getY(),
                end.getX(), end.getY()).asCubic();
        
        EdgeData data = (EdgeData)dataMgr.createDefaultEdgeData(curve);
        return createEdge(start, end, 
                data, 
                BezierVertexSmooth.CORNER, BezierVertexSmooth.CORNER, 
                curve.getAx1(), curve.getAy1(), 
                curve.getAx2(), curve.getAy2());
    }
    
    BezierPathEdge2i<EdgeData> createEdge(
            BezierPathVertex2i start, BezierPathVertex2i end,
            EdgeData data,
            BezierVertexSmooth smooth0,
            BezierVertexSmooth smooth1,
            int k0x, int k0y, int k1x, int k1y)
    {
        BezierPathEdge2i e = new BezierPathEdge2i(
                nextEdgeId++, start, end,
                data,
                smooth0, smooth1, 
                k0x, k0y, k1x, k1y);
        edgeMap.put(e.getId(), e);
        return e;
    }

    public ArrayList<BezierPathEdge2i<EdgeData>> getEdges()
    {
        return new ArrayList<BezierPathEdge2i<EdgeData>>(edgeMap.values());
    }

    void removeEdge(BezierPathEdge2i<EdgeData> e)
    {
        vertMap.remove(e.getId());
    }
    
    private void copyLoopSameId(BezierLoop2i l0)
    {
        BezierLoop2i l1 = new BezierLoop2i(
                l0.getId(), 
                this,
                getVertex(l0.getId()),
                getVertex(l0.getId()),
                l0.getClosure());
        loops.add(l1);
        loopMap.put(l1.getId(), l1);
    }

    protected void createLoop(int id, 
            BezierPathVertex2i head, BezierPathVertex2i tail,
            BezierLoopClosure closure)
    {
        if (loopMap.containsKey(id))
        {
            throw new IllegalArgumentException("id already assigned");
        }
        BezierLoop2i l = new BezierLoop2i(
                id, 
                this, 
                head, tail, closure);
        loopMap.put(l.getId(), l);
        loops.add(l);
        nextLoopId = Math.max(nextLoopId, id + 1);
    }
    
    public BezierLoop2i createLoop(int x, int y)
    {
        return createLoop(new Coord(x, y));
    }
    
    public BezierLoop2i createLoop(Coord c)
    {
        BezierPathVertex2i v = createVertex(c);
        
        BezierLoop2i loop = new BezierLoop2i(nextLoopId++, this, v);
        loops.add(loop);
        loopMap.put(loop.getId(), loop);
        return loop;
    }

    public ArrayList<BezierLoop2i<VertexData, EdgeData>> getLoops()
    {
        return new ArrayList<BezierLoop2i<VertexData, EdgeData>>(loops);
    }

    public BezierLoop2i getLoop(int id)
    {
        return loopMap.get(id);
    }
    
    public boolean removeLoop(BezierLoop2i loop)
    {
        if (!loops.remove(loop))
        {
            return false;
        }
        loopMap.remove(loop.getId());
        return true;
    }
    
    public boolean removeLoop(int id)
    {
        BezierLoop2i loop = loopMap.remove(id);
        
        if (loop == null)
        {
            return false;
        }
        loops.remove(loop);
        return true;
    }
    
    public CyPath2d asPath()
    {
        CyPath2d path = new CyPath2d();
        
        for (BezierLoop2i<VertexData, EdgeData> l: loops)
        {
            l.appendToPath(path);
        }
        
        return path;
    }
    
    public void append(CyShape shape)
    {
        double[] coords = new double[6];
        double sx = 0;
        double sy = 0;
        double mx = 0;
        double my = 0;
        
        BezierLoop2i loop = null;
        for (CyPathIterator it = shape.getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                {
                    sx = mx = coords[0];
                    sy = my = coords[1];
                    loop = null;
                    break;
                }
                case LINETO:
                {
                    if (loop == null)
                    {
                        loop = createLoop((int)mx, (int)my);
                    }
                    
                    loop.lineTo((int)coords[0], (int)coords[1]);
                    mx = coords[0];
                    my = coords[1];
                    break;
                }
                case QUADTO:
                {
                    if (loop == null)
                    {
                        loop = createLoop((int)mx, (int)my);
                    }
                    
                    loop.quadTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3]);
                    mx = coords[2];
                    my = coords[3];
                    break;
                }
                case CUBICTO:
                {
                    if (loop == null)
                    {
                        loop = createLoop((int)mx, (int)my);
                    }
                    
                    loop.cubicTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3],
                            (int)coords[4], (int)coords[5]);
                    mx = coords[4];
                    my = coords[5];
                    break;
                }
                case CLOSE:
                {
                    if (loop != null)
                    {
                        if (mx != sx || my != sy)
                        {
                            loop.setClosure(BezierLoopClosure.CLOSED_FREE);
                        }
                        else
                        {
                            loop.setClosure(BezierLoopClosure.CLOSED_CLAMPED);
                        }
                        loop = null;
                    }
                    
                    mx = coords[0];
                    my = coords[1];
                    break;
                }
            }
        }
    }
    
    public BezierPathCutter2i createCutGraph(double flatnessSquared)
    {
        BezierPathCutter2i cut = new BezierPathCutter2i(flatnessSquared);
        
        for (BezierLoop2i l: loops)
        {
            l.appendToCutGraph(cut);
        }
        
        return cut;
    }
    
    public BezierPath2i combine(BezierPath2i path, BooleanOp op, double flatnessSquared)
    {
        BezierPathCutter2i c0 = createCutGraph(flatnessSquared);
        BezierPathCutter2i c1 = path.createCutGraph(flatnessSquared);
        
        c0.cutAgainstGraph(c1);

        ArrayList<Segment> segs = new ArrayList<Segment>();
        
        switch (op)
        {
            case UNION:
                c0.getSegments(c1, true, true, false, segs);
                c1.getSegments(c0, true, false, false, segs);
                break;
            case INTERSECTION:
                c0.getSegments(c1, false, true, true, segs);
                c1.getSegments(c0, false, false, true, segs);
                break;
            case A_SUB_B:
                c0.getSegments(c1, true, false, false, segs);
                c1.getSegments(c0, false, false, true, segs);
                break;
            case B_SUB_A:
                c0.getSegments(c1, false, false, true, segs);
                c1.getSegments(c0, true, false, false, segs);
                break;
        }
        
        return BezierPathCutter2i.segmentsToEdges(segs, dataMgr);
    }
    
    public CyRectangle2i getBounds()
    {
        CyRectangle2i bounds = null;
        
        for (BezierLoop2i l: loops)
        {
            if (bounds == null)
            {
                bounds = l.getBounds();
            }
            else
            {
                bounds.union(l.getBounds());
            }
        }
        
        return bounds;
    }

}
