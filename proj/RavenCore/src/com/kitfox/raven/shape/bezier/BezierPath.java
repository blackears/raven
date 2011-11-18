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

package com.kitfox.raven.shape.bezier;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.raven.shape.bezier.BezierNetworkManipulator.EdgeProxy;
import com.kitfox.raven.shape.bezier.BezierNetworkManipulator.VertexProxy;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierPath extends BezierNetwork
{
    private final BezierFace faceInside = new BezierFace();

    ArrayList<Subpath> subpaths = new ArrayList<Subpath>();

    ArrayList<BezierVertex> vertices = new ArrayList<BezierVertex>();

    int nextVertexUid = 0;
    int nextEdgeUid = 0;

    public BezierPath(double flatnessSquared)
    {
        super(flatnessSquared);

        faceInside.setUid(1);
    }

    public BezierPath(PathCurve curve,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> vertexData,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> edgeData,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> faceData,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> faceVertexData,
            double flatnessSquared)
    {
        this(flatnessSquared);


        Path2D.Double path = curve.asPath2D();
        append(path, null);

        ArrayList<BezierVertex> vertLists = new ArrayList<BezierVertex>();
        for (Subpath sub: subpaths)
        {
            vertLists.addAll(sub.vertices);
        }
        vertLists.addAll(vertices);

        int vertIdx = 0;
        int edgeIdx = 0;
        for (BezierVertex vtx: vertLists)
        {
            for (Class<? extends PlaneDataProvider> key: vertexData.keySet())
            {
                ArrayList list = vertexData.get(key);
                vtx.setData(key, list.get(vertIdx++));
            }

            for (BezierEdge edge: vtx.edgeOut)
            {
                for (Class<? extends PlaneDataProvider> key: edgeData.keySet())
                {
                    edge.setData(key, edgeData.get(key).get(edgeIdx++));
                }
            }
        }

        int count = 0;
        for (BezierFace face: new BezierFace[]{faceOutside, faceInside})
        {
            for (Class<? extends PlaneDataProvider> key: faceData.keySet())
            {
                face.setData(key, faceData.get(key).get(count++));
            }
        }
    }

    private BezierEdge appendCurve(BezierVertex vtx, BezierCurve curve)
    {
        BezierVertex vtxLast = getVtxLast();

        BezierEdge edge = new BezierEdge(vtxLast, vtx, curve, flatnessSquared);
        vtxLast.edgeOut.add(edge);
        vtx.edgeIn.add(edge);
        vtxLast = vtx;
        vtx.setData(VertexSmooth.PlaneData.class, VertexSmooth.CUSP);
        edge.setData(EdgeVisible.class, true);
        vtx.setUid(nextVertexUid++);
        edge.setUid(nextEdgeUid++);
        vertices.add(vtx);

        edge.faceLeft = getFaceOutside();
        getFaceOutside().addEdgeLeft(edge);

        //Inside if closed shape
        edge.faceRight = getFaceOutside();
        getFaceOutside().addEdgeRight(edge);

        return edge;
    }

    public void moveTo(int x, int y)
    {
        BezierVertex vtxLast = getVtxLast();

        if (vtxLast == null)
        {
            vtxLast = new BezierVertex(x, y);
            vtxLast.setData(VertexSmooth.PlaneData.class, VertexSmooth.CUSP);
            vtxLast.setUid(nextVertexUid++);
            vertices.add(vtxLast);
            return;
        }

        BezierVertex vtx = new BezierVertex(x, y);
        BezierCurveLine curve = new BezierCurveLine(vtxLast.getPoint().getX(), vtxLast.getPoint().getY(), vtx.getPoint().getX(), vtx.getPoint().getY());

        BezierEdge edge = appendCurve(vtx, curve);
        edge.setData(EdgeVisible.class, false);
    }

    public void lineTo(int x, int y)
    {
        BezierVertex vtxLast = getVtxLast();

        if (vtxLast == null)
        {
            moveTo(0, 0);
        }

        BezierVertex vtx = new BezierVertex(x, y);
        BezierCurveLine curve = new BezierCurveLine(vtxLast.getPoint().getX(), vtxLast.getPoint().getY(), vtx.getPoint().getX(), vtx.getPoint().getY());

        appendCurve(vtx, curve);
    }

    public void quadTo(int k0x, int k0y, int x, int y)
    {
        BezierVertex vtxLast = getVtxLast();

        if (vtxLast == null)
        {
            moveTo(0, 0);
        }

        BezierVertex vtx = new BezierVertex(x, y);
        BezierCurveQuadratic curve = new BezierCurveQuadratic(
                vtxLast.getPoint().getX(), vtxLast.getPoint().getY(),
                k0x, k0y, vtx.getPoint().getX(), vtx.getPoint().getY());

        appendCurve(vtx, curve);
    }

    public void cubicTo(int k0x, int k0y, int k1x, int k1y, int x, int y)
    {
        BezierVertex vtxLast = getVtxLast();

        if (vtxLast == null)
        {
            moveTo(0, 0);
        }

        BezierVertex vtx = new BezierVertex(x, y);
        BezierCurveCubic curve = new BezierCurveCubic(
                vtxLast.getPoint().getX(), vtxLast.getPoint().getY(),
                k0x, k0y,
                k1x, k1y, vtx.getPoint().getX(), vtx.getPoint().getY());

        appendCurve(vtx, curve);
    }

    public void closePath()
    {
        if (vertices.isEmpty())
        {
            return;
        }

        for (BezierEdge edge = nextEdge(getVtxFirst()); edge != null; edge = nextEdge(edge.end))
        {
            edge.faceLeft = faceInside;

            getFaceOutside().removeEdgeLeft(edge);
            faceInside.addEdgeLeft(edge);
        }


        //Close loop in vertex list
        BezierVertex vtxFirst = vertices.get(0);
        BezierVertex vtxLast = vertices.get(vertices.size() - 1);
        if (vtxFirst.point.equals(vtxLast.point))
        {
            BezierEdge edge = vtxLast.edgeIn.get(0);
            vtxFirst.edgeIn.add(edge);
            edge.end = vtxFirst;
            vertices.remove(vertices.size() - 1);
        }
        else
        {
            BezierCurve curve = new BezierCurveLine(
                    vtxLast.point.getX(), vtxLast.point.getY(),
                    vtxFirst.point.getX(), vtxFirst.point.getY()
                    );
            BezierEdge edge = new BezierEdge(vtxLast, vtxFirst, curve, flatnessSquared);
            vtxLast.edgeOut.add(edge);
            vtxFirst.edgeIn.add(edge);
        }

        //Push current working path onto closed paths stack
        subpaths.add(new Subpath(vertices));
        vertices = new ArrayList<BezierVertex>();
    }

    public void removeLast()
    {
        if (vertices.isEmpty())
        {
            return;
        }

        BezierVertex vtxLast = getVtxLast();
        BezierEdge edgeLast = vtxLast.edgeIn.get(0);
        edgeLast.getStart().edgeOut.remove(edgeLast);

        vertices.remove(vertices.size() - 1);
    }

    /**
     * @return the faceInside
     */
    public BezierFace getFaceInside()
    {
        return faceInside;
    }

    public BezierEdge nextEdge(BezierVertex vtx)
    {
        return vtx.edgeOut.isEmpty() ? null : vtx.edgeOut.get(0);
    }

    public BezierEdge prevEdge(BezierVertex vtx)
    {
        return vtx.edgeIn.isEmpty() ? null : vtx.edgeIn.get(0);
    }

    public BezierVertex nextVertex(BezierVertex vtx)
    {
        return vtx.edgeOut.isEmpty() ? null : vtx.edgeOut.get(0).getEnd();
    }

    public BezierVertex prevVertex(BezierVertex vtx)
    {
        return vtx.edgeIn.isEmpty() ? null : vtx.edgeIn.get(0).getStart();
    }

    public Path2D.Double asPath()
    {
        Path2D.Double path = new Path2D.Double();

        //First add all subpaths
        for (Subpath sub: subpaths)
        {
            BezierVertex vtxFirst = sub.vertices.get(0);

            path.moveTo(vtxFirst.getPoint().getX(), vtxFirst.getPoint().getY());

            BezierEdge edge = nextEdge(vtxFirst);
            do
            {
                edge.getCurve().appendToPath(path);

                edge = nextEdge(edge.getEnd());
            } while (edge != nextEdge(vtxFirst));

//            for (BezierEdge edge = nextEdge(vtxFirst); edge != null; edge = nextEdge(edge.getEnd()))
//            {
//                edge.getCurve().appendToPath(path);
//            }
            path.closePath();
        }

        //Add remaining open path
        if (vertices.isEmpty())
        {
            return path;
        }

        BezierVertex vtxFirst = vertices.get(0);

        path.moveTo(vtxFirst.getPoint().getX(), vtxFirst.getPoint().getY());

        for (BezierEdge edge = nextEdge(vtxFirst); edge != null; edge = nextEdge(edge.getEnd()))
        {
            edge.getCurve().appendToPath(path);
        }

        return path;
    }

    public Path2D.Double asPathInPixels()
    {
        Path2D.Double path = asPath();
        return (Path2D.Double)toPixels.createTransformedShape(path);
    }


    /**
     * @return the vtxFirst
     */
    public BezierVertex getVtxFirst()
    {
        return vertices.isEmpty() ? null : vertices.get(0);
    }

    /**
     * @return the vtxLast
     */
    public BezierVertex getVtxLast()
    {
        return vertices.isEmpty() ? null : vertices.get(vertices.size() - 1);
    }

    public void append(Path2D.Double path, AffineTransform xform)
    {
        double[] coords = new double[6];
        for (PathIterator it = path.getPathIterator(xform); !it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_CLOSE:
                    closePath();
                    break;
                case PathIterator.SEG_MOVETO:
                    moveTo((int)coords[0], (int)coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    lineTo((int)coords[0], (int)coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    quadTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3]
                            );
                    break;
                case PathIterator.SEG_CUBICTO:
                    cubicTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3],
                            (int)coords[4], (int)coords[5]
                            );
                    break;
            }
        }
    }

    @Override
    public BezierVertex splitEdge(BezierEdge edge, double t)
    {
        BezierVertex startVtx = edge.getStart();
        int idx = vertices.indexOf(startVtx);
        ArrayList<BezierVertex> curVertList = vertices;

        //Check to see if edge might be in subpath
        if (idx == -1)
        {
            for (Subpath sub: subpaths)
            {
                int curIdx = sub.vertices.indexOf(startVtx);
                if (curIdx != -1)
                {
                    idx = curIdx;
                    curVertList = sub.vertices;
                    break;
                }
            }
        }

        assert idx != -1;

//        int idx = curVertList.indexOf(startVtx);
        BezierCurve curve = edge.getCurve();
        BezierCurve[] segs = curve.split(t, null);

        BezierVertex mid = new BezierVertex(segs[0].getEndX(), segs[0].getEndY());
        mid.setUid(nextVertexUid++);
        segs[1].setStartX(segs[0].getEndX());
        segs[1].setStartY(segs[0].getEndY());
        curVertList.add(idx + 1, mid);

        edge.getStart().edgeOut.remove(edge);
        edge.getEnd().edgeIn.remove(edge);
        edge.faceLeft.removeEdgeLeft(edge);
        edge.faceRight.removeEdgeRight(edge);

        BezierEdge before = new BezierEdge(edge.getStart(), mid, segs[0], flatnessSquared);
        BezierEdge after = new BezierEdge(mid, edge.getEnd(), segs[1], flatnessSquared);

        before.setUid(edge.getUid());
        before.setData(edge.getData());
        before.faceLeft = edge.faceLeft;
        before.faceLeft.addEdgeLeft(before);
        before.faceRight = edge.faceRight;
        before.faceRight.addEdgeRight(before);

        after.setUid(nextEdgeUid++);
        after.setData(edge.getData());
        after.faceLeft = edge.faceLeft;
        after.faceLeft.addEdgeLeft(after);
        after.faceRight = edge.faceRight;
        after.faceRight.addEdgeRight(after);

        edge.getStart().edgeOut.add(before);
        mid.edgeIn.add(before);
        mid.edgeOut.add(after);
        edge.getEnd().edgeIn.add(after);

        return mid;
    }

    @Override
    public int getNumVertices()
    {
        int sum = 0;
        for (Subpath sub: subpaths)
        {
            sum += sub.vertices.size();
        }
        return sum + vertices.size();
    }

    @Override
    public ArrayList<BezierVertex> getVertices()
    {
        ArrayList<BezierVertex> vertList = new ArrayList<BezierVertex>();
        for (Subpath sub: subpaths)
        {
            vertList.addAll(sub.vertices);
        }
        vertList.addAll(vertices);
        
        return vertList;
    }

    @Override
    public void removeVertex(BezierVertex vtx)
    {
        ArrayList<BezierVertex> vertList = null;
        int idx = vertices.indexOf(vtx);
        if (idx != -1)
        {
            if (vertices.size() <= 2)
            {
                vertices.clear();
                return;
            }
            else if (idx == 0)
            {
                //Remove first point in open curve
                BezierEdge edge = vtx.edgeOut.get(0);
                edge.end.edgeIn.remove(edge);
                vertices.remove(0);
                return;
            }
            else if (idx == vertices.size() - 1)
            {
                //Remove last point in open curve
                BezierEdge edge = vtx.edgeIn.get(0);
                edge.start.edgeOut.remove(edge);
                vertices.remove(vertices.size() - 1);
                return;
            }
            else
            {
                vertList = vertices;
            }
        }
        else
        {
            for (int i = 0; i < subpaths.size(); ++i)
            {
                Subpath sub = subpaths.get(i);

                vertList = sub.vertices;
                idx = vertList.indexOf(vtx);
                if (idx != -1)
                {
                    if (vertList.size() <= 3)
                    {
                        //We just removed last point in subpath
                        subpaths.remove(sub);
                        return;
                    }
                }
            }
        }


        //Merge at vertex
        vertList.remove(vtx);

        BezierEdge edgeIn = vtx.edgeIn.get(0);
        BezierCurve curveIn = edgeIn.getCurve();
        BezierEdge edgeOut = vtx.edgeOut.get(0);
        BezierCurve curveOut = edgeOut.getCurve();

        BezierCurve curve;
        if (curveIn.getDegree() == 1 && curveOut.getDegree() == 1)
        {
            curve = new BezierCurveLine(
                    curveIn.getStartX(), curveIn.getStartY(),
                    curveOut.getEndX(), curveOut.getEndY()
                    );
        }
        else
        {
            curve = new BezierCurveCubic(
                    curveIn.getStartX(), curveIn.getStartY(),
                    curveIn.getStartKnotX(), curveIn.getStartKnotY(),
                    curveOut.getEndKnotX(), curveOut.getEndKnotY(),
                    curveOut.getEndX(), curveOut.getEndY()
                    );
        }

        //Deconnect old edges
        edgeIn.start.edgeOut.remove(edgeIn);
        edgeOut.end.edgeIn.remove(edgeOut);

        edgeIn.faceLeft.removeEdgeLeft(edgeIn);
        edgeIn.faceRight.removeEdgeRight(edgeIn);
        edgeOut.faceLeft.removeEdgeLeft(edgeOut);
        edgeOut.faceRight.removeEdgeRight(edgeOut);

        //Insert new edge
        BezierEdge edge = new BezierEdge(edgeIn.start, edgeOut.end, curve, flatnessSquared);
        edgeIn.start.edgeOut.add(edge);
        edgeOut.end.edgeIn.add(edge);
        edgeIn.faceLeft.addEdgeLeft(edge);
        edge.faceLeft = edgeIn.faceLeft;
        edgeIn.faceRight.addEdgeRight(edge);
        edge.faceRight = edgeIn.faceRight;

        edge.setData(edgeIn.getData());
    }

    @Override
    protected void applyManip(BezierNetworkManipulator manip)
    {
        for (BezierVertex vtx: getVertices())
        {
            VertexProxy vtxPx = manip.vertices.get(vtx);
            vtx.point = new BezierPoint(vtxPx.newX, vtxPx.newY);

            for (BezierEdge edge: vtx.edgeOut)
            {
                EdgeProxy edgePx = manip.edges.get(edge);
                edge.setCurve(edgePx.newCurve);
            }
        }
    }
//
//    @Override
//    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysVertex()
//    {
//        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();
//
//        for (Subpath sub: subpaths)
//        {
//            for (BezierVertex vtx: sub.vertices)
//            {
//                keySet.addAll(vtx.getDataKeys());
//            }
//        }
//
//        for (BezierVertex vtx: vertices)
//        {
//            keySet.addAll(vtx.getDataKeys());
//        }
//
//        return keySet;
//    }
//
//    @Override
//    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysEdge()
//    {
//        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();
//
//        for (Subpath sub: subpaths)
//        {
//            for (BezierVertex vtx: sub.vertices)
//            {
//                for (BezierEdge edge: vtx.edgeOut)
//                {
//                    keySet.addAll(edge.getDataKeys());
//                }
//            }
//        }
//
//        for (BezierVertex vtx: vertices)
//        {
//            for (BezierEdge edge: vtx.edgeOut)
//            {
//                keySet.addAll(edge.getDataKeys());
//            }
//        }
//
//        return keySet;
//    }
//
//    @Override
//    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysFace()
//    {
//        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();
//
//        keySet.addAll(faceOutside.getDataKeys());
//        keySet.addAll(faceInside.getDataKeys());
//
//        return keySet;
//    }
//
//    @Override
//    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysFaceVertex()
//    {
//        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();
//
//        for (BezierFace face: new BezierFace[]{faceOutside, faceInside})
//        {
//            for (BezierFaceVertex fv: face.faceVertexMap.values())
//            {
//                keySet.addAll(fv.getDataKeys());
//            }
//        }
//
//        return keySet;
//    }
//
//    @Override
    public ArrayList buildDataPlaneVertex(Class<? extends PlaneDataProvider> key)
    {
        //Path will follow vertex order, so just output them as they're
        // encountered
        ArrayList list = new ArrayList();

        for (Subpath sub: subpaths)
        {
            for (BezierVertex vtx: sub.vertices)
            {
                list.add(vtx.getData(key));
            }
        }

        for (BezierVertex vtx: vertices)
        {
            list.add(vtx.getData(key));
        }

        return list;
    }

//    @Override
    public ArrayList buildDataPlaneEdge(Class<? extends PlaneDataProvider> key)
    {
        //Path will follow vertex order, so just output them as they're
        // encountered
        ArrayList list = new ArrayList();

        for (Subpath sub: subpaths)
        {
            for (BezierVertex vtx: sub.vertices)
            {
                for (BezierEdge edge: vtx.edgeOut)
                {
                    list.add(edge.getData(key));
                }
            }
        }

        for (BezierVertex vtx: vertices)
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                list.add(edge.getData(key));
            }
        }

        return list;
    }

//    @Override
    public ArrayList buildDataPlaneFace(Class<? extends PlaneDataProvider> key)
    {
        //Path will follow vertex order, so just output them as they're
        // encountered
        ArrayList list = new ArrayList();

        list.add(faceOutside.getData(key));
        list.add(faceInside.getData(key));

        return list;
    }
//
//    @Override
//    public ArrayList buildDataPlaneFaceVertex(Class<? extends PlaneDataProvider> key)
//    {
//        //Path will follow vertex order, so just output them as they're
//        // encountered
//        ArrayList list = new ArrayList();
//
//        for (BezierFace face: new BezierFace[]{faceOutside, faceInside})
//        {
//            for (Subpath sub: subpaths)
//            {
//                for (BezierVertex vtx: sub.vertices)
//                {
//                    BezierFaceVertex fv = face.faceVertexMap.get(vtx);
//                    list.add(fv.getData(key));
//                }
//            }
//
//            for (BezierVertex vtx: vertices)
//            {
//                BezierFaceVertex fv = face.faceVertexMap.get(vtx);
//                list.add(fv.getData(key));
//            }
//        }
//
//        return list;
//    }

//    @Override
//    public PathCurve buildPathCurve()
//    {
//        PathCurve path = new PathCurve(asPath());
//        return path;
//    }

//    @Override
//    public ArrayList<BezierVertex> getLinkedVertices(BezierVertex pickVtx)
//    {
//        ArrayList<BezierVertex> list = super.getLinkedVertices(pickVtx);
//
//        for (Subpath sub: subpaths)
//        {
//            BezierVertex vtxFirst = sub.vertices.get(0);
//            BezierVertex vtxLast = sub.vertices.get(sub.vertices.size() - 1);
//            if (vtxFirst == pickVtx)
//            {
//                list.add(vtxLast);
//                break;
//            }
//            if (vtxLast == pickVtx)
//            {
//                list.add(vtxFirst);
//                break;
//            }
//        }
//
//        return list;
//    }

    /**
     * Runs through all vertices.  Returns an array indicating whether or not
     * they form an angle of less than maxAngle with their input and output
     * edges.
     *
     * @param maxAngle
     * @return
     */
    public ArrayList<VertexSmooth> buildVertexSmoothing(double maxAngle)
    {
        ArrayList<VertexSmooth> smoothing = new ArrayList<VertexSmooth>();

        double maxAngleRadians = Math.toRadians(maxAngle);
        for (Subpath sub: subpaths)
        {
            for (int i = 0; i < sub.vertices.size(); ++i)
            {
                BezierVertex vtx = sub.vertices.get(i);

                BezierEdge edgeIn = prevEdge((i == 0)
                        ? sub.vertices.get(sub.vertices.size() - 1)
                        : vtx);
                BezierEdge edgeOut = nextEdge((i == sub.vertices.size() - 1)
                        ? sub.vertices.get(0)
                        : vtx);

                CyVector2d vIn = new CyVector2d(edgeIn.getCurve().getEndTanX(),
                        edgeIn.getCurve().getEndTanY());
                CyVector2d vOut = new CyVector2d(edgeOut.getCurve().getStartTanX(),
                        edgeOut.getCurve().getStartTanY());

                vIn.normalize();
                vOut.normalize();
                double angle = Math.acos(vIn.dot(vOut));

                smoothing.add(angle <= maxAngleRadians
                        ? VertexSmooth.SMOOTH : VertexSmooth.CUSP);
            }
        }

        for (int i = 0; i < vertices.size(); ++i)
        {
            if (i == 0 || i == vertices.size() - 1)
            {
                smoothing.add(VertexSmooth.CUSP);
                continue;
            }

            BezierVertex vtx = vertices.get(i);

            BezierEdge edgeIn = prevEdge(vtx);
            BezierEdge edgeOut = nextEdge(vtx);

            CyVector2d vIn = new CyVector2d(edgeIn.getCurve().getEndTanX(),
                    edgeIn.getCurve().getEndTanY());
            CyVector2d vOut = new CyVector2d(edgeOut.getCurve().getStartTanX(),
                    edgeOut.getCurve().getStartTanY());

            vIn.normalize();
            vOut.normalize();
            double angle = Math.acos(vIn.dot(vOut));

            smoothing.add(angle <= maxAngle ? VertexSmooth.SMOOTH : VertexSmooth.CUSP);
        }

        return smoothing;
    }

//    /**
//     * Gets the edge in used for smoothing this vertex point.  Takes
//     * the looping of closed paths into account so that the returned edge
//     * may not actually include the input vertex.
//     *
//     * @param vtx
//     * @return
//     */
//    @Override
//    public BezierEdge getSmoothingEdgeIn(BezierVertex vtx)
//    {
//        if (vtx.edgeIn.size() == 1)
//        {
//            return vtx.edgeIn.get(0);
//        }
//
//        //No immediate input.  Check to see if this is the initial vertex in
//        // a closed loop
//        for (Subpath sub: subpaths)
//        {
//            if (sub.vertices.get(0) == vtx)
//            {
//                BezierVertex other = sub.vertices.get(sub.vertices.size() - 1);
//                return other.getEdgeIn(0);
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * Gets the edge out used for smoothing this vertex point.  Takes
//     * the looping of closed paths into account so that the returned edge
//     * may not actually include the input vertex.
//     *
//     * @param vtx
//     * @return
//     */
//    @Override
//    public BezierEdge getSmoothingEdgeOut(BezierVertex vtx)
//    {
//        if (vtx.edgeOut.size() == 1)
//        {
//            return vtx.edgeOut.get(0);
//        }
//
//        //No immediate input.  Check to see if this is the initial vertex in
//        // a closed loop
//        for (Subpath sub: subpaths)
//        {
//            if (sub.vertices.get(sub.vertices.size() - 1) == vtx)
//            {
//                BezierVertex other = sub.vertices.get(0);
//                return other.getEdgeOut(0);
//            }
//        }
//
//        return null;
//    }

//    @Override
//    public void applyVertexSmoothing(VertexSmooth smooth, BezierVertex vtx)
//    {
//        BezierEdge prev = getSmoothingEdgeIn(vtx);
//        BezierEdge next = getSmoothingEdgeOut(vtx);
//
//        if (prev == null || next == null)
//        {
//            return;
//        }
//
//        BezierCurve prevCurve = prev.getCurve();
//        BezierCurve nextCurve = next.getCurve();
//
//        if (smooth != VertexSmooth.CUSP)
//        {
//            //Promote curve degree
//            if (prevCurve.getDegree() <= 2)
//            {
//                prevCurve = new BezierCurveCubic(prevCurve.getStartX(), prevCurve.getStartY(),
//                        prevCurve.getStartKnotX(), prevCurve.getStartKnotY(),
//                        prevCurve.getEndKnotX(), prevCurve.getEndKnotY(),
//                        prevCurve.getEndX(), prevCurve.getEndY());
//                prev.setCurve(prevCurve);
//            }
//            if (nextCurve.getDegree() <= 2)
//            {
//                nextCurve = new BezierCurveCubic(nextCurve.getStartX(), nextCurve.getStartY(),
//                        nextCurve.getStartKnotX(), nextCurve.getStartKnotY(),
//                        nextCurve.getEndKnotX(), nextCurve.getEndKnotY(),
//                        nextCurve.getEndX(), nextCurve.getEndY());
//                next.setCurve(nextCurve);
//            }
//        }
//
//        applyVertexSmoothing(smooth, prevCurve, nextCurve);
//    }

//    @Override
//    public ArrayList<BezierEdge> getEdgesSorted()
//    {
//        ArrayList<BezierEdge> edges = new ArrayList<BezierEdge>();
//
//        for (Subpath sub: subpaths)
//        {
//            for (BezierVertex vtx: sub.vertices)
//            {
//                for (BezierEdge edge: vtx.edgeOut)
//                {
//                    edges.add(edge);
//                }
//            }
//        }
//        for (BezierVertex vtx: vertices)
//        {
//            for (BezierEdge edge: vtx.edgeOut)
//            {
//                edges.add(edge);
//            }
//        }
//
//        return edges;
//    }

//    @Override
//    public ArrayList<BezierFace> getAllInteriorFaces()
//    {
//        ArrayList<BezierFace> faces = new ArrayList<BezierFace>();
//        faces.add(faceInside);
//        return faces;
//    }

    //------------------------------

    public class Subpath
    {
        ArrayList<BezierVertex> vertices;

        public Subpath(ArrayList<BezierVertex> vertices)
        {
            this.vertices = vertices;
        }
    }
}
