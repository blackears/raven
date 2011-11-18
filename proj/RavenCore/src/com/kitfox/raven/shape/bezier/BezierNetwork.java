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
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.Selection.Type;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Provides a framework for displaying and manipulating piecewise
 * bezier networks.
 *
 * A bezier network consists of a set of bezier segments connected
 * at vertices.  Each bezier component can be augmented with additional
 * data managed by a PlaneDataProvider.
 *
 * @author kitfox
 */
abstract public class BezierNetwork
{
    final double flatnessSquared;
    
    public static final int POINT_HANDLE_RADIUS = 4;
    final Rectangle pointShapeCusp = new Rectangle(
            -POINT_HANDLE_RADIUS, -POINT_HANDLE_RADIUS,
            POINT_HANDLE_RADIUS * 2, POINT_HANDLE_RADIUS * 2);
    final Ellipse2D.Double pointShapeTense = new Ellipse2D.Double(
            -POINT_HANDLE_RADIUS, -POINT_HANDLE_RADIUS,
            POINT_HANDLE_RADIUS * 2, POINT_HANDLE_RADIUS * 2);
    final Shape pointShapeSmooth;
    {
        AffineTransform xform = new AffineTransform();
        xform.setToRotation(Math.toRadians(45));
        pointShapeSmooth = xform.createTransformedShape(pointShapeCusp);
    }

    public static final AffineTransform toPixels = new AffineTransform(
            1 / 100.0, 0, 0, 1 / 100.0, 0, 0);


    protected final BezierFace faceOutside = new BezierFace();

    public BezierNetwork(double flatnessSquared)
    {
        this.flatnessSquared = flatnessSquared;

        faceOutside.setUid(0);
    }

    public Shape getPointShape(VertexSmooth smooth)
    {
        switch (smooth)
        {
            default:
            case CUSP:
                return pointShapeCusp;
            case SMOOTH:
                return pointShapeSmooth;
            case TENSE:
                return pointShapeTense;
        }
    }

    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysVertex()
    {
        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();

        ArrayList<BezierVertex> list = getVertices();
        for (BezierVertex vtx: list)
        {
            keySet.addAll(vtx.getDataKeys());
        }

        return keySet;
    }

    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysEdge()
    {
        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();

        ArrayList<BezierEdge> list = getEdges();
        for (BezierEdge edge: list)
        {
            keySet.addAll(edge.getDataKeys());
        }

        return keySet;
    }

    public HashSet<Class<? extends PlaneDataProvider>> getDataKeysFace()
    {
        HashSet<Class<? extends PlaneDataProvider>> keySet = new HashSet<Class<? extends PlaneDataProvider>>();

        HashSet<BezierFace> list = getFaces();
        for (BezierFace face: list)
        {
            keySet.addAll(face.getDataKeys());
        }

        return keySet;
    }

//    abstract public ArrayList<BezierEdge> getEdgesSorted();
//
//    public  ArrayList<BezierVertex> getVerticesSorted()
//    {
//        ArrayList<BezierVertex> list = new ArrayList<BezierVertex>();
//
//        ArrayList<BezierEdge> edges = getEdgesSorted();
//        for (BezierEdge edge: edges)
//        {
//            if (!list.contains(edge.start))
//            {
//                list.add(edge.start);
//            }
//            if (!list.contains(edge.end))
//            {
//                list.add(edge.end);
//            }
//        }
//        return list;
//    }
//
//    public ArrayList<BezierFace> getFacesSorted()
//    {
//        ArrayList<BezierFace> list = new ArrayList<BezierFace>();
//        list.add(faceOutside);
//
//        ArrayList<BezierEdge> edges = getEdgesSorted();
//        for (BezierEdge edge: edges)
//        {
//            if (!list.contains(edge.faceLeft))
//            {
//                list.add(edge.faceLeft);
//            }
//            if (!list.contains(edge.faceRight))
//            {
//                list.add(edge.faceRight);
//            }
//        }
//        return list;
//    }
//
//    public ArrayList<BezierFaceVertex> getFacesVerticesSorted()
//    {
//        ArrayList<BezierFaceVertex> list = new ArrayList<BezierFaceVertex>();
//
//        ArrayList<BezierFace> faces = getFacesSorted();
//        ArrayList<BezierVertex> vertices = getVerticesSorted();
//
//        for (BezierFace face: faces)
//        {
//            for (BezierVertex vtx: vertices)
//            {
//                BezierFaceVertex fv = face.faceVertexMap.get(vtx);
//                if (fv == null)
//                {
//                    continue;
//                }
//                list.add(fv);
//            }
//        }
//
//        return list;
//    }
//
//    public <T extends PlaneDataProvider> ArrayList<T> buildDataPlaneVertex(Class<T> key)
//    {
//        ArrayList list = new ArrayList();
//        ArrayList<BezierVertex> vertices = getVerticesSorted();
//
//        for (BezierVertex vtx: vertices)
//        {
//            list.add(vtx.getData(key));
//        }
//
//        return list;
//    }
//
//    public <T extends PlaneDataProvider> ArrayList<T> buildDataPlaneEdge(Class<T> key)
//    {
//        ArrayList list = new ArrayList();
//        ArrayList<BezierEdge> edges = getEdgesSorted();
//
//        for (BezierEdge edge: edges)
//        {
//            list.add(edge.getData(key));
//        }
//
//        return list;
//    }
//
//    public <T extends PlaneDataProvider> ArrayList<T> buildDataPlaneFace(Class<T> key)
//    {
//        ArrayList list = new ArrayList();
//        ArrayList<BezierFace> faces = getFacesSorted();
//
//        for (BezierFace face: faces)
//        {
//            list.add(face.getData(key));
//        }
//
//        return list;
//    }
//
//    public <T extends PlaneDataProvider> ArrayList<T> buildDataPlaneFaceVertex(Class<T> key)
//    {
//        ArrayList list = new ArrayList();
//        ArrayList<BezierFaceVertex> fvs = getFacesVerticesSorted();
//
//        for (BezierFaceVertex fv: fvs)
//        {
//            list.add(fv.getData(key));
//        }
//
//        return list;
//    }

    /**
     * Exports bezier data as a curve.  Both meshes an paths will return
     * data in the form of a PathCurve, but these two systems will
     * interpret the data differently.
     *
     * There is a relationship between the data returned in the path curve
     * and the lists returned by the buildDataPlane* methods.  An ordering
     * is imposed as follows:
     *
     * Vertices are indexed as they are encountered
     * Edges are indexed as they are encountered.
     * Faces are indexed as they are encountered as edges are
     * traveresed in order, and looking first to the left and then right of
     * the edge.
     * FaceVertices are indexed as encountered as faces are traversed and
     * starting with the lowest indexed vertex associated with the face,
     * and then as vertices they are encountered in indexed order.
     *
     * @return
     */
//    abstract public PathCurve buildPathCurve();

    protected MeshPointRecord getClosestPoint(int px, int py)
    {
        PointRecord bestRecord = null;
        BezierEdge bestEdge = null;

        for (BezierVertex vtx: getVertices())
        {
            //Each edge should have exactly one edgeIn and one edgeOut
            // entry across all vertices
            for (BezierEdge edge: vtx.edgeOut)
            {
                PointRecord rec = edge.getSegList().findClosestPointOnCurve(px, py);
                if (bestRecord == null || rec.dist < bestRecord.dist)
                {
                    bestRecord = rec;
                    bestEdge = edge;
                }
            }
        }

        if (bestRecord == null)
        {
            return null;
        }

        return new MeshPointRecord(bestEdge, bestRecord);
    }

    public BezierFace getFace(int px, int py)
    {
        MeshPointRecord rec = getClosestPoint(px, py);
        if (rec == null)
        {
            //First segment added to mesh
            return faceOutside;
        }

        //Vector from boundary to interior point
        double dx = px - rec.record.px;
        double dy = py - rec.record.py;

        if (rec.record.t == 0)
        {
            return rec.edge.getStart().getFaceCCW(dx, dy);
        }

        if (rec.record.t == 1)
        {
            return rec.edge.getEnd().getFaceCCW(dx, dy);
        }

        double[] tan = rec.edge.getCurve().getTangent(rec.record.t, null);

        //Use y component of cross product in XZ plane to
        // determine if we are to left or right of tangent
        double crossY = dy * tan[0] - dx * tan[1];
        return crossY > 0 ? rec.edge.faceRight : rec.edge.faceLeft;
    }

    public ArrayList<BezierEdge> getEdges()
    {
        ArrayList<BezierEdge> edges = new ArrayList<BezierEdge>();

        for (BezierVertex vtx: getVertices())
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                edges.add(edge);
            }
        }

        return edges;
    }

    public BezierVertex splitEdgeAtPoint(BezierEdge edge, int px, int py)
    {
        PointRecord rec = edge.getSegList().findClosestPointOnCurve(px, py);
        return splitEdge(edge, rec.t);
    }

    abstract public BezierVertex splitEdge(BezierEdge edge, double t);

    public HashSet<BezierFace> getFaces()
    {
        HashSet<BezierFace> faces = new HashSet<BezierFace>();
        for (BezierVertex v: getVertices())
        {
            for (BezierEdge e: v.edgeOut)
            {
                faces.add(e.faceLeft);
                faces.add(e.faceRight);
            }
        }
        return faces;
    }

    public void convertAllCubicToQuadratic()
    {
        for (BezierVertex vtx: getVertices())
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                if (edge.getCurve() instanceof BezierCurveCubic)
                {
                    BezierCurveQuadratic quad = ((BezierCurveCubic)edge.getCurve()).midpointAprox();
                    edge.setCurve(quad);
                }
            }
        }

    }

    /**
     * @return the faceOutside
     */
    public BezierFace getFaceOutside()
    {
        return faceOutside;
    }

    abstract public int getNumVertices();
    abstract public ArrayList<BezierVertex> getVertices();

    public void renderManipulator(Graphics2D g, Subselection subselection,
            AffineTransform localToDevice, Color color)
    {
        g.setColor(color);
        g.setStroke(new BasicStroke(1));


        AffineTransform cacheXform = g.getTransform();

        AffineTransform pathToDevice;
        if (localToDevice == null)
        {
            pathToDevice = toPixels;
        }
        else
        {
            pathToDevice = new AffineTransform(localToDevice);
            pathToDevice.concatenate(toPixels);
        }

        for (BezierVertex vtx: getVertices())
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                BezierCurve curve = edge.getCurve();
                Path2D.Double path = new Path2D.Double();
                path.moveTo(curve.getStartX(), curve.getStartY());
                curve.appendToPath(path);

                Shape pathDevice = pathToDevice.createTransformedShape(path);
                g.draw(pathDevice);

                //Draw input tangent
                if (subselection != null &&
                        (subselection.contains(edge.getStart()) || subselection.contains(edge.getEnd())))
                {
                    if (edge.getCurve().getDegree() > 1)
                    {
                        //Input
                        Point2D.Double pt0 = new Point2D.Double(
                                curve.getStartX(), curve.getStartY());
                        pathToDevice.transform(pt0, pt0);

                        Point2D.Double kt0 = new Point2D.Double(
                                curve.getStartKnotX(), curve.getStartKnotY());
                        pathToDevice.transform(kt0, kt0);
                        renderKnotManipulator(g, pt0.x, pt0.y, kt0.x, kt0.y, color);


                        //Output
                        Point2D.Double pt1 = new Point2D.Double(
                                curve.getEndX(), curve.getEndY());
                        pathToDevice.transform(pt1, pt1);

                        Point2D.Double kt1 = new Point2D.Double(
                                curve.getEndKnotX(), curve.getEndKnotY());
                        pathToDevice.transform(kt1, kt1);
                        renderKnotManipulator(g, pt1.x, pt1.y, kt1.x, kt1.y, color);
                    }
                }
            }

        }


        for (BezierVertex vtx: getVertices())
        {
            Point2D.Double pt = new Point2D.Double(vtx.getPoint().getX(), vtx.getPoint().getY());
            pathToDevice.transform(pt, pt);

            boolean selected = false;

            //Draw outside manip handles for endpoints
            if (subselection != null && subselection.contains(vtx))
            {
                selected = true;
                if (vtx.edgeIn.isEmpty() && vtx.edgeOut.size() == 1)
                {
                    BezierEdge edge = vtx.edgeOut.get(0);
                    if (edge != null && edge.getCurve().getDegree() > 1)
                    {
                        BezierCurve curve = edge.getCurve();
                        Point2D.Double kt = new Point2D.Double(
                                curve.getStartKnotX(), curve.getStartKnotY());
                        pathToDevice.transform(kt, kt);

                        renderKnotManipulator(g, pt.x, pt.y,
                                pt.x * 2 - kt.x, pt.y * 2 - kt.y, color);
                    }
                }
                else if (vtx.edgeOut.isEmpty() && vtx.edgeIn.size() == 1)
                {
                    BezierEdge edge = vtx.edgeIn.get(0);
                    if (edge != null && edge.getCurve().getDegree() > 1)
                    {
                        BezierCurve curve = edge.getCurve();
                        Point2D.Double kt = new Point2D.Double(
                                curve.getEndKnotX(), curve.getEndKnotY());
                        pathToDevice.transform(kt, kt);

                        renderKnotManipulator(g, pt.x, pt.y,
                                pt.x * 2 - kt.x, pt.y * 2 - kt.y, color);
                    }
                }
            }

            //Draw point manipulator
            g.translate(pt.x, pt.y);

            Shape pointShape = getPointShape(vtx.getData(VertexSmooth.PlaneData.class));

            g.setColor(selected ? Color.green : Color.white);
            g.fill(pointShape);
            g.setColor(color);
            g.draw(pointShape);
            g.setTransform(cacheXform);

        }
    }

    public void renderKnotManipulator(Graphics2D g, double px, double py, double kx, double ky, Color color)
    {
        renderKnotManipulator(g, (int)px, (int)py, (int)kx, (int)ky, color);
    }

    public void renderKnotManipulator(Graphics2D g, int px, int py, int kx, int ky, Color color)
    {
        AffineTransform cacheXform = g.getTransform();

        g.setColor(color);
        g.drawLine(px, py, kx, ky);

        g.translate(kx, ky);

        Shape pointShape = getPointShape(VertexSmooth.CUSP);

        g.setColor(Color.black);
        g.fill(pointShape);

        g.setColor(color);
        g.draw(pointShape);

        g.setTransform(cacheXform);
    }

    private boolean isHit(Rectangle rectangle, Rectangle2D ptArea, Intersection intersection)
    {
        switch (intersection)
        {
            case CONTAINS:
                return rectangle.contains(ptArea);
            default:
            case INTERSECTS:
                return rectangle.intersects(ptArea);
            case INSIDE:
                return ptArea.contains(rectangle);
        }
    }

    public ArrayList<ManipComponent> selectPointManipulators(Rectangle rectangle,
            Subselection subsel,
            AffineTransform localToDevice,
            Intersection intersection)
    {
        ArrayList<ManipComponent> result = new ArrayList<ManipComponent>();
        
        AffineTransform pathToDevice;
        if (localToDevice == null)
        {
            pathToDevice = toPixels;
        }
        else
        {
            pathToDevice = new AffineTransform(localToDevice);
            pathToDevice.concatenate(toPixels);
        }

        Point2D.Double pt = new Point2D.Double();
        Rectangle2D.Double ptArea = new Rectangle2D.Double();
        ptArea.width = POINT_HANDLE_RADIUS * 2;
        ptArea.height = POINT_HANDLE_RADIUS * 2;
        for (BezierVertex vtx: getVertices())
        {
            pt.setLocation(vtx.getPoint().getX(), vtx.getPoint().getY());
            pathToDevice.transform(pt, pt);

            ptArea.x = pt.x - POINT_HANDLE_RADIUS;
            ptArea.y = pt.y - POINT_HANDLE_RADIUS;
            if (isHit(rectangle, ptArea, intersection))
            {
                result.add(new ManipVertex(vtx));
            }

            if (subsel != null)
            {
                Point2D.Double kt = new Point2D.Double();

                for (BezierEdge edge: vtx.edgeOut)
                {
                    BezierCurve curve = edge.getCurve();
                    if (curve.getDegree() <= 1
                            || (!subsel.contains(edge.start)
                                && !subsel.contains(edge.end)))
                    {
                        continue;
                    }

                    kt.setLocation(curve.getStartKnotX(), curve.getStartKnotY());
                    pathToDevice.transform(kt, kt);

                    ptArea.x = kt.x - POINT_HANDLE_RADIUS;
                    ptArea.y = kt.y - POINT_HANDLE_RADIUS;

                    if (isHit(rectangle, ptArea, intersection))
                    {
//                        result.add(new ManipKnot(edge, KnotType.START));
                        result.add(new ManipKnot(vtx, true));
                    }
                }

                for (BezierEdge edge: vtx.edgeIn)
                {
                    BezierCurve curve = edge.getCurve();
                    if (curve.getDegree() <= 1
                            || (!subsel.contains(edge.start)
                                && !subsel.contains(edge.end)))
                    {
                        continue;
                    }

                    kt.setLocation(curve.getEndKnotX(), curve.getEndKnotY());
                    pathToDevice.transform(kt, kt);

                    ptArea.x = kt.x - POINT_HANDLE_RADIUS;
                    ptArea.y = kt.y - POINT_HANDLE_RADIUS;

                    if (isHit(rectangle, ptArea, intersection))
                    {
//                        result.add(new ManipKnot(edge, KnotType.END));
                        result.add(new ManipKnot(vtx, false));
                    }
                }
            }
        }

        return result;
    }

    abstract protected void applyManip(BezierNetworkManipulator manip);

//    abstract public BezierEdge getSmoothingEdgeOut(BezierVertex vtx);
//    abstract public BezierEdge getSmoothingEdgeIn(BezierVertex vtx);

    public void applyVertexSmoothing(VertexSmooth smooth, BezierVertex vtx)
    {
        if (vtx.edgeIn.size() != 1 || vtx.edgeOut.size() != 1)
        {
            //Only smooth verts with one in and one out
            return;
        }

        BezierEdge prev = vtx.edgeIn.get(0);
        BezierEdge next = vtx.edgeOut.get(0);

        if (prev == null || next == null)
        {
            return;
        }

        BezierCurve prevCurve = prev.getCurve();
        BezierCurve nextCurve = next.getCurve();

        if (smooth != VertexSmooth.CUSP)
        {
            //Promote curve degree
            if (prevCurve.getDegree() <= 2)
            {
                prevCurve = new BezierCurveCubic(prevCurve.getStartX(), prevCurve.getStartY(),
                        prevCurve.getStartKnotX(), prevCurve.getStartKnotY(),
                        prevCurve.getEndKnotX(), prevCurve.getEndKnotY(),
                        prevCurve.getEndX(), prevCurve.getEndY());
                prev.setCurve(prevCurve);
            }
            if (nextCurve.getDegree() <= 2)
            {
                nextCurve = new BezierCurveCubic(nextCurve.getStartX(), nextCurve.getStartY(),
                        nextCurve.getStartKnotX(), nextCurve.getStartKnotY(),
                        nextCurve.getEndKnotX(), nextCurve.getEndKnotY(),
                        nextCurve.getEndX(), nextCurve.getEndY());
                next.setCurve(nextCurve);
            }
        }

        applyVertexSmoothing(smooth, prevCurve, nextCurve);
    }

    public static void applyVertexSmoothing(VertexSmooth smooth, BezierCurve prevCurve, BezierCurve nextCurve)
    {
        switch (smooth)
        {
            case SMOOTH:
            {
                CyVector2d vIn = new CyVector2d(prevCurve.getEndTanX(), prevCurve.getEndTanY());
                CyVector2d vOut = new CyVector2d(nextCurve.getStartTanX(), nextCurve.getStartTanY());
                CyVector2d dir = new CyVector2d(vIn.x + vOut.x, vIn.y + vOut.y);
                dir.normalize();

                double magIn = vIn.length();
                double magOut = vOut.length();

                vOut.setScaled(dir, magOut);
                vIn.setScaled(dir, magIn);

                prevCurve.setEndKnotX((int)(prevCurve.getEndX() - vIn.x));
                prevCurve.setEndKnotY((int)(prevCurve.getEndY() - vIn.y));
                nextCurve.setStartKnotX((int)(nextCurve.getStartX() + vOut.x));
                nextCurve.setStartKnotY((int)(nextCurve.getStartY() + vOut.y));
                break;
            }
            case TENSE:
            {
                //In tense vertex, difference in in and out knots will form a vector
                // parallel to the span of the prev and next verts and 1/3
                // of its length.  Will also be divided by central vertex into
                // a ratio equal to the ratio between the lengths of the input
                // and output curve spans
                CyVector2d vIn = new CyVector2d(prevCurve.getEndX() - prevCurve.getStartX(),
                        prevCurve.getEndY() - prevCurve.getStartY());
                CyVector2d vOut = new CyVector2d(nextCurve.getEndX() - nextCurve.getStartX(),
                        nextCurve.getEndY() - nextCurve.getStartY());
                CyVector2d span = new CyVector2d(vIn.x + vOut.x, vIn.y + vOut.y);

                double magIn = vIn.length();
                double magOut = vOut.length();
                double ratio = 1 / (3 * (magIn + magOut));
                vIn.setScaled(span, magIn * ratio);
                vOut.setScaled(span, magOut * ratio);

                prevCurve.setEndKnotX((int)(prevCurve.getEndX() - vIn.x));
                prevCurve.setEndKnotY((int)(prevCurve.getEndY() - vIn.y));
                nextCurve.setStartKnotX((int)(nextCurve.getStartX() + vOut.x));
                nextCurve.setStartKnotY((int)(nextCurve.getStartY() + vOut.y));

                break;
            }
            case CUSP:
                //Do nothing
                break;
        }
    }

    /**
     * Some vertices may be positionally linked.  This will return a list of all
     * other vertices that are forced to match this one's potition.
     *
     * @param pickVtx
     * @return
     */
//    public ArrayList<BezierVertex> getLinkedVertices(BezierVertex pickVtx)
//    {
//        return new ArrayList<BezierVertex>();
//    }

//    abstract public ArrayList<BezierEdge> getAllEdges();

    public ArrayList<BezierVertex> getVerticesByUid(ArrayList<Integer> vertUids)
    {
        ArrayList<BezierVertex> list = getVertices();
        for (Iterator<BezierVertex> it = list.iterator(); it.hasNext();)
        {
            BezierVertex vtx = it.next();
            if (!vertUids.contains(vtx.getUid()))
            {
                it.remove();
            }
        }
        return list;
    }

    public ArrayList<BezierEdge> getEdgesByUid(ArrayList<Integer> edgeUids)
    {
        ArrayList<BezierEdge> list = getEdges();
        for (Iterator<BezierEdge> it = list.iterator(); it.hasNext();)
        {
            BezierEdge edge = it.next();
            if (!edgeUids.contains(edge.getUid()))
            {
                it.remove();
            }
        }
        return list;
    }

    public ArrayList<BezierFace> getFacesByUid(ArrayList<Integer> faceUids)
    {
        HashSet<BezierFace> list = getFaces();

        for (Iterator<BezierFace> it = list.iterator(); it.hasNext();)
        {
            BezierFace face = it.next();
            if (!faceUids.contains(face.getUid()))
            {
                it.remove();
            }
        }
        return new ArrayList<BezierFace>(list);
    }

    abstract public void removeVertex(BezierVertex vtx);

    //---------------------------------------------------

    public static interface NetworkUpdateCallback
    {
        /**
         * The edge being replaced
         * @param edge
         */
//        public void startReplaceExistingEdge(BezierEdge edge);
        /**
         * One or more segments that are replacing the replaced edge.  Called
         * after startReplaceExistingEdge()
         * @param tOffset
         * @param tSpan
         * @param newEdge
         */
//        public void replaceExistingEdge(double tOffset, double tSpan, BezierEdge edge);
        /**
         * Finished updating the edge started with startReplaceExistingEdge()
         */
//        public void endReplaceExistingEdge();

        /**
         * New edge segment added as part of the
         * @param tOffset
         * @param tSpan
         * @param edge
         */
        public void addedEdge(double tOffset, double tSpan, BezierEdge edge);
    }

    public class MeshPointRecord
    {
        BezierEdge edge;
        PointRecord record;

        public MeshPointRecord(BezierEdge edge, PointRecord record)
        {
            this.edge = edge;
            this.record = record;
        }
    }

    public class ManipComponent
    {

    }

    public class ManipVertex extends ManipComponent
    {
        private final BezierVertex vertex;

        public ManipVertex(BezierVertex vertex)
        {
            this.vertex = vertex;
        }

        /**
         * @return the vertex
         */
        public BezierVertex getVertex()
        {
            return vertex;
        }

    }

    public class ManipKnot extends ManipComponent
    {
        private final BezierVertex vtx;
        private final boolean knotOut;

        public ManipKnot(BezierVertex vtx, boolean knotOut)
        {
            this.vtx = vtx;
            this.knotOut = knotOut;
        }

        /**
         * @return the vtx
         */
        public BezierVertex getVtx()
        {
            return vtx;
        }

        /**
         * @return the knotOut
         */
        public boolean isKnotOut()
        {
            return knotOut;
        }
    }

    public static class Subselection
    {
        Selection<Integer> vertices = new Selection<Integer>();
        Selection<Integer> edges = new Selection<Integer>();
        Selection<Integer> faces = new Selection<Integer>();

        public Subselection()
        {
        }

        public Subselection(Collection<Integer> vertices,
                Collection<Integer> edges,
                Collection<Integer> faces)
        {
            if (vertices != null)
            {
                this.vertices.select(Type.REPLACE, vertices);
            }
            if (edges != null)
            {
                this.edges.select(Type.REPLACE, edges);
            }
            if (faces != null)
            {
                this.faces.select(Type.REPLACE, faces);
            }
        }

        public Subselection(BezierVertex... vertices)
        {
            this(createHashSet(vertices), null, null);
        }

        public Subselection(BezierEdge... edges)
        {
            this(null, createHashSet(edges), null);
        }

        public Subselection(BezierFace... faces)
        {
            this(null, null, createHashSet(faces));
        }

        private static HashSet<Integer> createHashSet(BezierNetworkComponent[] comps)
        {
            HashSet<Integer> set = new HashSet<Integer>();
            for (BezierNetworkComponent comp: comps)
            {
                set.add(comp.getUid());
            }
            return set;
        }

        public Subselection(Subselection sub)
        {
            this(sub.vertices.getSelection(),
                    sub.edges.getSelection(),
                    sub.faces.getSelection());
        }

        public boolean contains(BezierVertex vertex)
        {
            return vertex != null && vertices.isSelected(vertex.getUid());
        }

        public boolean contains(BezierEdge edge)
        {
            return edge != null && edges.isSelected(edge.getUid());
        }

        public boolean contains(BezierFace face)
        {
            return face != null && faces.isSelected(face.getUid());
        }

        public void addVertices(Type type, Collection<BezierVertex> vertices)
        {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (BezierVertex vtx: vertices)
            {
                list.add(vtx.getUid());
            }

            this.vertices.select(type, list);
        }

        public void addVertex(Type type, BezierVertex vtx)
        {
            this.vertices.select(type, vtx.getUid());
        }

        public boolean isEmptyVertices()
        {
            return vertices.isEmpty();
        }

        public boolean isEmptyEdges()
        {
            return edges.isEmpty();
        }

        public boolean isEmptyFaces()
        {
            return faces.isEmpty();
        }

        public ArrayList<Integer> getVertexUids()
        {
            return vertices.getSelection();
        }

        public ArrayList<Integer> getEdgeUids()
        {
            return edges.getSelection();
        }

        public ArrayList<Integer> getFaceUids()
        {
            return faces.getSelection();
        }
    }
}
