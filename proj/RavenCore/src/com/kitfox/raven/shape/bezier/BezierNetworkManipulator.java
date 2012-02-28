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
import com.kitfox.raven.shape.bezier.BezierNetwork.Subselection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierNetworkManipulator
{
    static final AffineTransform toPixels = new AffineTransform(
            1 / 100.0, 0, 0, 1 / 100.0, 0, 0);
    
//    public static final int POINT_HANDLE_RADIUS = 3;
//    final Rectangle pointShape = new Rectangle(
//            -POINT_HANDLE_RADIUS, -POINT_HANDLE_RADIUS,
//            POINT_HANDLE_RADIUS * 2, POINT_HANDLE_RADIUS * 2);

    final BezierNetwork network;
    HashMap<BezierVertex, VertexProxy> vertices = new HashMap<BezierVertex, VertexProxy>();
    HashMap<BezierEdge, EdgeProxy> edges = new HashMap<BezierEdge, EdgeProxy>();

    public BezierNetworkManipulator(BezierNetwork network)
    {
        this.network = network;

        for (BezierVertex vtx: network.getVertices())
        {
            vertices.put(vtx, new VertexProxy(vtx));
            for (BezierEdge edge: vtx.edgeOut)
            {
                edges.put(edge, new EdgeProxy(edge));
            }
        }
    }

    public void offsetVertexFromInitBy(BezierVertex vtx, int x, int y)
    {
        setVertexPosition(vtx, vtx.getPoint().getX() + x, vtx.getPoint().getY() + y);
    }

//    private BezierEdge getUniqueNextEdge(BezierVertex vtx)
//    {
//        if (vtx.edgeOut.size() == 1)
//        {
//            return vtx.edgeOut.get(0);
//        }
//
//        ArrayList<BezierVertex> linkedList = network.getLinkedVertices(vtx);
//        if (linkedList.size() == 1)
//        {
//            vtx = linkedList.get(0);
//            if (vtx.edgeOut.size() == 1)
//            {
//                return vtx.edgeOut.get(0);
//            }
//        }
//        return null;
//    }
//
//    private BezierEdge getUniquePrevEdge(BezierVertex vtx)
//    {
//        if (vtx.edgeIn.size() == 1)
//        {
//            return vtx.edgeIn.get(0);
//        }
//
//        ArrayList<BezierVertex> linkedList = network.getLinkedVertices(vtx);
//        if (linkedList.size() == 1)
//        {
//            vtx = linkedList.get(0);
//            if (vtx.edgeIn.size() == 1)
//            {
//                return vtx.edgeIn.get(0);
//            }
//        }
//        return null;
//    }

    public void setVertexPosition(BezierVertex vtx, int x, int y)
    {
        VertexProxy vPx = vertices.get(vtx);
        vPx.newX = x;
        vPx.newY = y;

        for (BezierEdge edge: vtx.edgeOut)
        {
            EdgeProxy ePx = edges.get(edge);
            ePx.newCurve.setStartX(x);
            ePx.newCurve.setStartY(y);
        }

        for (BezierEdge edge: vtx.edgeIn)
        {
            EdgeProxy ePx = edges.get(edge);
            ePx.newCurve.setEndX(x);
            ePx.newCurve.setEndY(y);
        }

//        if (vtx.getData(VertexSmooth.PlaneData.class) == VertexSmooth.TENSE)
//        {
//            BezierEdge edgeIn = getUniquePrevEdge(vtx);
//            BezierEdge edgeOut = getUniqueNextEdge(vtx);
//            EdgeProxy ePxIn = edges.get(edgeIn);
//            EdgeProxy ePxOut = edges.get(edgeOut);
//
//            if (edgeIn != null && edgeOut != null)
//            {
//                BezierCurve curveIn = ePxIn.newCurve;
//                BezierCurve curveOut = ePxOut.newCurve;
//
//                CyVector2d span = new CyVector2d(curveOut.getEndX() - curveIn.getStartX(),
//                        curveOut.getEndY() - curveIn.getStartY());
//                CyVector2d vIn = new CyVector2d(curveIn.getEndX() - curveIn.getStartX(),
//                        curveIn.getEndY() - curveIn.getStartY());
//                CyVector2d vOut = new CyVector2d(curveOut.getEndX() - curveOut.getStartX(),
//                        curveOut.getEndY() - curveOut.getStartY());
//
//                double vInLen = vIn.length();
//                double vOutLen = vOut.length();
//                double scalar = 1 / (3 * (vInLen + vOutLen));
//                vIn.scale(vInLen * scalar, span);
//                vOut.scale(vOutLen * scalar, span);
//
//                curveIn.setEndKnotX((int)(curveIn.getEndX() - vIn.x));
//                curveIn.setEndKnotY((int)(curveIn.getEndY() - vIn.y));
//                curveOut.setStartKnotX((int)(curveOut.getStartX() + vOut.x));
//                curveOut.setStartKnotY((int)(curveOut.getStartY() + vOut.y));
//            }
//        }
    }

    public void offsetStartKnotBy(BezierEdge edge, int x, int y)
    {
        BezierCurve initCurve = edge.getCurve();
        EdgeProxy ePx = edges.get(edge);

        ePx.newCurve.setStartKnotX(initCurve.getStartKnotX() + x);
        ePx.newCurve.setStartKnotY(initCurve.getStartKnotY() + y);
    }

    public void setKnotStartPosition(BezierEdge edge, int x, int y)
    {
        EdgeProxy ePx = edges.get(edge);

        ePx.newCurve.setStartKnotX(x);
        ePx.newCurve.setStartKnotY(y);
    }

    public void offsetEndKnotBy(BezierEdge edge, int x, int y)
    {
        BezierCurve initCurve = edge.getCurve();
        EdgeProxy ePx = edges.get(edge);

        ePx.newCurve.setEndKnotX(initCurve.getEndKnotX() + x);
        ePx.newCurve.setEndKnotY(initCurve.getEndKnotY() + y);
    }

    public void setKnotEndPosition(BezierEdge edge, int x, int y)
    {
        EdgeProxy ePx = edges.get(edge);

        ePx.newCurve.setEndKnotX(x);
        ePx.newCurve.setEndKnotY(y);
    }


//    public void offsetKnotFromInitBy(BezierEdge edge, KnotType knotType, int x, int y)
//    {
//        BezierCurve initCurve = edge.getCurve();
//        int kx;
//        int ky;
//
//        switch (knotType)
//        {
//            case BEFORE_START:
//                kx = initCurve.getStartX() * 2 - initCurve.getStartKnotX();
//                ky = initCurve.getStartY() * 2 - initCurve.getStartKnotY();
//                break;
//            case START:
//                kx = initCurve.getStartKnotX();
//                ky = initCurve.getStartKnotY();
//                break;
//            case END:
//                kx = initCurve.getEndKnotX();
//                ky = initCurve.getEndKnotY();
//                break;
//            case AFTER_END:
//                kx = initCurve.getEndX() * 2 - initCurve.getEndKnotX();
//                ky = initCurve.getEndY() * 2 - initCurve.getEndKnotY();
//                break;
//            default:
//                throw new UnsupportedOperationException();
//        }
//
//        setKnotPosition(edge, knotType, kx + x, ky + y);
//    }
//
//    public void setKnotPosition(BezierEdge edge, KnotType type, int x, int y)
//    {
//        EdgeProxy ePx = edges.get(edge);
//
////        VertexSmooth endSmooth = edge.end.getData(VertexSmooth.PlaneData.class);
////        VertexSmooth startSmooth = edge.start.getData(VertexSmooth.PlaneData.class);
//
//        switch (type)
//        {
//            case BEFORE_START:
//                ePx.newCurve.setStartKnotX(ePx.newCurve.getStartX() * 2 - x);
//                ePx.newCurve.setStartKnotY(ePx.newCurve.getStartY() * 2 - y);
//                break;
//            case START:
//                ePx.newCurve.setStartKnotX(x);
//                ePx.newCurve.setStartKnotY(y);
//
////                if (startSmooth == VertexSmooth.SMOOTH)
////                {
////                    BezierVertex vtx = edge.start;
////                    BezierEdge prevEdge = getUniquePrevEdge(vtx);
////
////                    if (prevEdge != null)
////                    {
////                        CyVector2d dir = new CyVector2d(x - vtx.point.getX(), y - vtx.point.getY());
////                        dir.normalize();
////
////                        EdgeProxy prevEPx = edges.get(prevEdge);
////                        CyVector2d vIn = new CyVector2d(vtx.point.getX() - prevEPx.newCurve.getEndKnotX(),
////                                vtx.point.getY() - prevEPx.newCurve.getEndKnotY());
////                        dir.scale(vIn.length());
////
////                        prevEPx.newCurve.setEndKnotX((int)(prevEPx.newCurve.getEndX() - dir.x));
////                        prevEPx.newCurve.setEndKnotY((int)(prevEPx.newCurve.getEndY() - dir.y));
////                    }
////                }
//                break;
//            case END:
//                ePx.newCurve.setEndKnotX(x);
//                ePx.newCurve.setEndKnotY(y);
//
////                if (endSmooth == VertexSmooth.SMOOTH)
////                {
////                    BezierVertex vtx = edge.end;
////                    BezierEdge nextEdge = getUniqueNextEdge(vtx);
////
////                    if (nextEdge != null)
////                    {
////                        CyVector2d dir = new CyVector2d(x - vtx.point.getX(), y - vtx.point.getY());
////                        dir.normalize();
////
////                        EdgeProxy nextEPx = edges.get(nextEdge);
////                        CyVector2d vOut = new CyVector2d(vtx.point.getX() - nextEPx.newCurve.getStartKnotX(),
////                                vtx.point.getY() - nextEPx.newCurve.getStartKnotY());
////                        dir.scale(vOut.length());
////
////                        nextEPx.newCurve.setStartKnotX((int)(nextEPx.newCurve.getStartX() - dir.x));
////                        nextEPx.newCurve.setStartKnotY((int)(nextEPx.newCurve.getStartY() - dir.y));
////                    }
////                }
//                break;
//            case AFTER_END:
//                ePx.newCurve.setEndKnotX(ePx.newCurve.getEndX() * 2 - x);
//                ePx.newCurve.setEndKnotY(ePx.newCurve.getEndY() * 2 - y);
//                break;
//        }
//    }

    public void smooth(VertexSmooth vertexSmooth, BezierEdge edgeIn, BezierEdge edgeOut)
    {
        EdgeProxy ePxIn = edges.get(edgeIn);
        EdgeProxy ePxOut = edges.get(edgeOut);

        BezierNetwork.applyVertexSmoothing(vertexSmooth, ePxIn.newCurve, ePxOut.newCurve);
    }

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

        for (EdgeProxy edgePx: edges.values())
        {
            BezierCurve curve = edgePx.newCurve;
            Path2D.Double path = new Path2D.Double();
            path.moveTo(curve.getStartX(), curve.getStartY());
            curve.appendToPath(path);

            Shape pathDevice = pathToDevice.createTransformedShape(path);
            g.draw(pathDevice);

            //Draw input tangent
            if (subselection != null &&
                    (subselection.contains(edgePx.edge.getStart()) || subselection.contains(edgePx.edge.getEnd())))
            {
                if (curve.getDegree() > 1)
                {
                    //Input
                    Point2D.Double pt0 = new Point2D.Double(
                            curve.getStartX(), curve.getStartY());
                    pathToDevice.transform(pt0, pt0);

                    Point2D.Double kt0 = new Point2D.Double(
                            curve.getStartKnotX(), curve.getStartKnotY());
                    pathToDevice.transform(kt0, kt0);
                    network.renderKnotManipulator(g, pt0.x, pt0.y, kt0.x, kt0.y, color);


                    //Output
                    Point2D.Double pt1 = new Point2D.Double(
                            curve.getEndX(), curve.getEndY());
                    pathToDevice.transform(pt1, pt1);

                    Point2D.Double kt1 = new Point2D.Double(
                            curve.getEndKnotX(), curve.getEndKnotY());
                    pathToDevice.transform(kt1, kt1);
                    network.renderKnotManipulator(g, pt1.x, pt1.y, kt1.x, kt1.y, color);
                }
            }
        }



        for (VertexProxy vtxPx: vertices.values())
        {
            Point2D.Double pt = new Point2D.Double(vtxPx.newX, vtxPx.newY);
            pathToDevice.transform(pt, pt);

            boolean selected = false;

            //Draw outside manip handles for endpoints
            if (subselection != null && subselection.contains(vtxPx.vtx))
            {
                selected = true;
                if (vtxPx.vtx.edgeIn.isEmpty() && vtxPx.vtx.edgeOut.size() == 1)
                {
                    EdgeProxy edge = edges.get(vtxPx.vtx.edgeOut.get(0));
                    if (edge != null && edge.newCurve.getDegree() > 1)
                    {
                        BezierCurve curve = edge.newCurve;
                        Point2D.Double kt = new Point2D.Double(
                                curve.getStartKnotX(), curve.getStartKnotY());
                        pathToDevice.transform(kt, kt);

                        network.renderKnotManipulator(g, pt.x, pt.y,
                                pt.x * 2 - kt.x, pt.y * 2 - kt.y, color);
                    }
                }
                else if (vtxPx.vtx.edgeOut.isEmpty() && vtxPx.vtx.edgeIn.size() == 1)
                {
                    EdgeProxy edge = edges.get(vtxPx.vtx.edgeIn.get(0));
                    if (edge != null && edge.newCurve.getDegree() > 1)
                    {
                        BezierCurve curve = edge.newCurve;
                        Point2D.Double kt = new Point2D.Double(
                                curve.getEndKnotX(), curve.getEndKnotY());
                        pathToDevice.transform(kt, kt);

                        network.renderKnotManipulator(g, pt.x, pt.y,
                                pt.x * 2 - kt.x, pt.y * 2 - kt.y, color);
                    }
                }
            }

            //Draw point manipulator
            g.translate(pt.x, pt.y);

            Shape pointShape = network.getPointShape(
                    vtxPx.vtx.getData(VertexSmooth.PlaneData.class));

            g.setColor(selected ? Color.green : Color.white);
            g.fill(pointShape);
            g.setColor(color);
            g.draw(pointShape);
            g.setTransform(cacheXform);

        }
    }

    public void apply()
    {
        network.applyManip(this);
    }

    public void alignSmoothKnotIn(BezierEdge edgeIn, BezierEdge edgeOut)
    {
        EdgeProxy prevEPx = edges.get(edgeIn);
        EdgeProxy nextEPx = edges.get(edgeOut);

        CyVector2d dir = new CyVector2d(nextEPx.newCurve.getStartTanX(),
                nextEPx.newCurve.getStartTanY());
        dir.normalize();

        CyVector2d vOut = new CyVector2d(
                prevEPx.newCurve.getEndX() - prevEPx.newCurve.getEndKnotX(),
                prevEPx.newCurve.getEndY() - prevEPx.newCurve.getEndKnotY());
        dir.scale(vOut.length());

        prevEPx.newCurve.setEndKnotX((int)(prevEPx.newCurve.getEndX() - dir.x));
        prevEPx.newCurve.setEndKnotY((int)(prevEPx.newCurve.getEndY() - dir.y));
    }

    public void alignSmoothKnotOut(BezierEdge edgeIn, BezierEdge edgeOut)
    {
        EdgeProxy prevEPx = edges.get(edgeIn);
        EdgeProxy nextEPx = edges.get(edgeOut);

        CyVector2d dir = new CyVector2d(prevEPx.newCurve.getEndTanX(),
                prevEPx.newCurve.getEndTanY());
        dir.normalize();

        CyVector2d vOut = new CyVector2d(
                nextEPx.newCurve.getStartX() - nextEPx.newCurve.getStartKnotX(),
                nextEPx.newCurve.getStartY() - nextEPx.newCurve.getStartKnotY());
        dir.scale(vOut.length());

        nextEPx.newCurve.setStartKnotX((int)(nextEPx.newCurve.getStartX() + dir.x));
        nextEPx.newCurve.setStartKnotY((int)(nextEPx.newCurve.getStartY() + dir.y));
    }


    //-------------------------
    
    class EdgeProxy
    {
        BezierEdge edge;

        BezierCurve newCurve;

        public EdgeProxy(BezierEdge edge)
        {
            this.edge = edge;
            this.newCurve = edge.getCurve().copy();
        }


    }

    class VertexProxy
    {
        BezierVertex vtx;

        int newX;
        int newY;

        public VertexProxy(BezierVertex source)
        {
            this.vtx = source;
            this.newX = source.getPoint().getX();
            this.newY = source.getPoint().getY();
        }

    }
}
