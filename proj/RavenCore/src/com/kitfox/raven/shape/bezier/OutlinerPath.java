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
import com.kitfox.game.control.color.StrokeStyle.Cap;
import com.kitfox.game.control.color.StrokeStyle.Join;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class OutlinerPath
{
    final double flatnessSquared;

//    ArrayList<Edge> edges = new ArrayList<Edge>();
//    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
//    boolean closed;
    Vertex vtxHead;
    Vertex vtxTail;
    int cursorX;
    int cursorY;
    float cursorW;

    public OutlinerPath(double flatnessSquared)
    {
        this.flatnessSquared = flatnessSquared;
    }


    private void addCurve(BezierCurve curve, int x, int y, float weight, boolean visible)
    {
        Vertex v0 = vtxTail;
        if (v0 == null)
        {
            v0 = new Vertex(cursorW, VertexSmooth.CUSP, cursorX, cursorY);
            vtxHead = v0;
        }
        else
        {
            v0.clearCache();
        }

//if (curve.getStartKnotX() == curve.getEndX()
//                && curve.getStartKnotY() == curve.getEndY())
//{
//    int j = 9;
//}

        Vertex v1 = new Vertex(weight, VertexSmooth.CUSP, x, y);
        vtxTail = v1;

        Edge edge = new Edge(curve, v0, v1, visible);
        v0.next = edge;
        v1.prev = edge;
//        edges.add(edge);

        cursorX = x;
        cursorY = y;
        cursorW = weight;
    }

    public boolean isClosed()
    {
        return vtxHead != null && vtxHead.prev != null;
    }

    public void setClosed(boolean closed)
    {
        if (isClosed() == closed)
        {
            return;
        }

        if (closed)
        {
            Edge edge = new Edge(new BezierCurveLine(vtxTail.x, vtxTail.y, vtxHead.x, vtxHead.y),
                    vtxTail, vtxHead, true);
            vtxHead.prev = edge;
            vtxTail.next = edge;
        }
        else
        {
            vtxHead.prev = vtxTail.next = null;
        }
    }

    public void moveTo(int x, int y, float weight)
    {
        if (vtxHead == null)
        {
            cursorX = x;
            cursorY = y;
            cursorW = weight;
            return;
        }

        BezierCurveLine curve = new BezierCurveLine(cursorX, cursorY, x, y);
        addCurve(curve, x, y, weight, false);
    }

    public void lineTo(int x, int y, float weight)
    {
        BezierCurveLine curve = new BezierCurveLine(cursorX, cursorY, x, y);
        addCurve(curve, x, y, weight, true);
    }

    public void quadTo(int k0x, int k0y, int x, int y, float weight)
    {
        BezierCurveQuadratic curve =
                new BezierCurveQuadratic(cursorX, cursorY, k0x, k0y, x, y);
        addCurve(curve, x, y, weight, true);
    }

    public void cubicTo(int k0x, int k0y, int k1x, int k1y, int x, int y, float weight)
    {
        BezierCurveCubic curve =
                new BezierCurveCubic(cursorX, cursorY, k0x, k0y, k1x, k1y, x, y);
        addCurve(curve, x, y, weight, true);
    }

    public void removeLast()
    {
        if (vtxHead == null)
        {
            return;
        }

        if (vtxTail == vtxHead)
        {
            cursorX = vtxHead.x;
            cursorY = vtxHead.y;
            cursorW = vtxHead.weight;
            vtxTail = vtxHead = null;
            return;
        }

        Vertex vtxPrev = vtxTail.prev.start;
        vtxPrev.next = vtxTail.next;
        vtxTail = vtxPrev;
        cursorX = vtxTail.x;
        cursorY = vtxTail.y;
        cursorW = vtxTail.weight;
        vtxTail.clearCache();
    }

//    private void buildContourOpen()
//    {
//        if (vtxHead == null)
//        {
//            return;
//        }
//
//        Vertex curVtx = vtxHead;
//
//        ArrayList<FlatSegmentList> offsetsLeft = new ArrayList<FlatSegmentList>();
//        ArrayList<FlatSegmentList> offsetsRight = new ArrayList<FlatSegmentList>();
//        do
//        {
////            Edge edgePrev = curVtx.prev;
//            Edge edgeNext = curVtx.next;
//
//            if (edgeNext == null || !edgeNext.visible)
//            {
//                offsetsLeft.add(null);
//                offsetsRight.add(null);
//            }
//            else
//            {
//                offsetsLeft.add(edgeNext.getOffsetLeft());
//                offsetsRight.add(edgeNext.getOffsetRight());
//            }
////            boolean capLeft = edgePrev == null || !edgePrev.visible;
////            boolean capRight = edgeNext == null || !edgeNext.visible;
//
//            curVtx = edgeNext.end;
//        } while (curVtx != null && curVtx != vtxHead);
//
//
//
//        boolean penUp = true;
//
//    }

    public Path2D getOutlinePath(Cap cap, Join join, double miterLimit)
    {
        ArrayList<BezierCurve> curves = new ArrayList<BezierCurve>();
        getOutline(cap, join, miterLimit, curves);

        removeTinySpans(curves);
//        removeLoops(curves);

        if (curves.isEmpty())
        {
            return null;
        }

        Path2D.Double path = new Path2D.Double();
        BezierCurve initCurve = curves.get(0);
        path.moveTo(initCurve.getStartX(), initCurve.getStartY());

//StringWriter sw = new StringWriter();
//PrintWriter pw = new PrintWriter(sw);
        for (BezierCurve curve: curves)
        {
            curve.appendToPath(path);
//pw.println("Path curve: " + curve);
        }
        path.closePath();
//pw.println();
//pw.close();
//System.err.println(sw.toString());
        
        return path;
    }

    public void removeLoops(ArrayList<BezierCurve> spans)
    {
        
    }

    public void removeTinySpans(ArrayList<BezierCurve> spans)
    {
        for (int idx = 0; idx < spans.size(); ++idx)
        {
            int idxNext = idx == spans.size() - 1 ? 0 : idx + 1;
            int idxPrev = idx == 0 ? spans.size() - 1 : idx - 1;

            BezierCurve cur = spans.get(idx);
            BezierCurve next = spans.get(idxNext);
            BezierCurve prev = spans.get(idxPrev);

            if (cur.getHullMaxX() - cur.getHullMinX() <= 100
                    && cur.getHullMaxY() - cur.getHullMinY() <= 100)
            {
                //Delete span
                spans.remove(idx);
                --idx;

                next.setStartX(prev.getEndX());
                next.setStartY(prev.getEndY());
            }
        }
    }

    public void getOutline(Cap cap, Join join, double miterLimit, ArrayList<BezierCurve> result)
    {
        if (vtxHead == null)
        {
            return;
        }

        boolean closed = isClosed();
        if (closed)
        {
//            path.moveTo(vtxHead.prev.getOffsetCurveLeft().getEndX(),
//                    vtxHead.prev.getOffsetCurveLeft().getEndY());

//            getOutlineLeft(path, join, miterLimit, closed);
            Vertex vtx = vtxHead;
            do
            {
                appendJoinLeft(vtx, join, miterLimit, result);

                BezierCurve curveNext = vtx.next.getOffsetCurveLeft();
                if (curveNext.getDegree() > 0)
                {
                    result.add(curveNext.copy());
                }

                //Next iteration
                vtx = vtx.next.end;
            } while (vtx.next != null && vtx != vtxHead);
//            path.closePath();

            //Hole
//            path.moveTo(vtxHead.prev.getOffsetCurveRight().getEndX(),
//                    vtxHead.prev.getOffsetCurveRight().getEndY());

//            getOutlineRight(path, join, miterLimit, closed);
            vtx = vtxTail;
            do
            {
                appendJoinRight(vtx, join, miterLimit, result);

                BezierCurve curvePrev = vtx.prev.getOffsetCurveRight();
                result.add(curvePrev);

                //Next iteration
                vtx = vtx.prev.start;
            } while (vtx.prev != null && vtx != vtxTail);
//            path.closePath();
        }
        else
        {
//            path.moveTo(vtxHead.next.getOffsetCurveLeft().getStartX(),
//                    vtxHead.next.getOffsetCurveLeft().getStartY());

//            ArrayList<BezierCurve> leftSegs = new ArrayList<BezierCurve>();
//            for (Vertex vtx = vtxHead; vtx != vtxTail; vtx = vtx.next.end)
//            {
//                BezierCurve curveNext = vtx.next.getOffsetCurveLeft();
//                if (curveNext.getDegree() > 0)
//                {
//                    leftSegs.add(curveNext);
//                }
//            }
//
//            for (Vertex vtx = vtxTail; vtx != vtxHead; vtx = vtx.prev.start)
//            {
//                BezierCurve curvePrev = vtx.prev.getOffsetCurveRight();
//                if (curvePrev.getDegree() > 0)
//                {
//                    result.add(curvePrev);
//                }
//            }
//
//            for (int i = 0; i < leftSegs.size(); ++i)
//            {
//                BezierCurve curve = leftSegs.get(i);
//                appendJoinLeft(vtx, join, miterLimit, result);
//                result.add(curve);
//
//            }


            ArrayList<BezierCurve> curvesLeft = new ArrayList<BezierCurve>();
            for (Edge edge = vtxHead.next;
                edge != vtxTail.next;
                edge = edge.end.next)
            {
                BezierCurve curve = edge.getOffsetCurveLeft();
                if (curve.getDegree() == 0)
                {
                    continue;
                }

                curvesLeft.add(curve.copy());
//verify(curvesLeft);

                //Find next curve for join
                Edge edgeNext = edge.end.next;
                if (edgeNext == vtxTail.next)
                {
                    break;
                }

                BezierCurve curveNext = edgeNext.getOffsetCurveLeft();
                if (curveNext.getDegree() != 0)
                {
                    appendJoinLeft(edge.end, join, miterLimit, curvesLeft);
//if (!verify(curvesLeft))
//{
//appendJoinLeft(edge.end, join, miterLimit, curvesLeft);
//}
                }
                else
                {
                    //Search for next non-empty curve
                    do
                    {
                        edge = edgeNext;
                        edgeNext = edgeNext.end.next;
                        curveNext = edgeNext.getOffsetCurveLeft();
                    } while (edgeNext != edge.end.next
                            && curveNext.getDegree() != 0);

                    if (edgeNext != edge.end.next)
                    {
                        curvesLeft.add(new BezierCurveLine(
                                curve.getEndX(), curve.getEndY(),
                                curveNext.getStartX(), curveNext.getStartY()));
//verify(curvesLeft);
                    }
                }
            }

            ArrayList<BezierCurve> curvesRight = new ArrayList<BezierCurve>();
            for (Edge edge = vtxTail.prev;
                edge != vtxHead.prev;
                edge = edge.start.prev)
            {
                BezierCurve curve = edge.getOffsetCurveRight();
                if (curve.getDegree() == 0)
                {
                    continue;
                }

                curvesRight.add(curve.copy());
//verify(curvesRight);

                //Find next curve for join
                Edge edgeNext = edge.start.prev;
                if (edgeNext == vtxHead.prev)
                {
                    break;
                }

                BezierCurve curveNext = edgeNext.getOffsetCurveRight();
                if (curveNext.getDegree() != 0)
                {
                    appendJoinRight(edge.start, join, miterLimit, curvesRight);
//if (!verify(curvesRight))
//{
//appendJoinRight(edge.start, join, miterLimit, curvesRight);
//}
                }
                else
                {
                    //Search for next non-empty curve
                    do
                    {
                        edge = edgeNext;
                        edgeNext = edgeNext.start.prev;
                        curveNext = edgeNext.getOffsetCurveRight();
                    } while (edgeNext != edge.start.prev
                            && curveNext.getDegree() != 0);

                    if (edgeNext != edge.start.prev)
                    {
                        curvesRight.add(new BezierCurveLine(
                                curve.getEndX(), curve.getEndY(),
                                curveNext.getStartX(), curveNext.getStartY()));
//verify(curvesRight);
                    }
                }
            }

            BezierCurve lastLeft = curvesLeft.get(curvesLeft.size() - 1);
            BezierCurve firstLeft = curvesLeft.get(0);
            BezierCurve lastRight = curvesRight.get(curvesRight.size() - 1);
            BezierCurve firstRight = curvesRight.get(0);

            result.addAll(curvesLeft);

            BezierMath.cap(lastLeft.getEndX(),
                    lastLeft.getEndY(),
                    firstRight.getStartX(),
                    firstRight.getStartY(),
                    cap, result);
//verify(result);

            result.addAll(curvesRight);

            BezierMath.cap(lastRight.getEndX(),
                    lastRight.getEndY(),
                    firstLeft.getStartX(),
                    firstLeft.getStartY(),
                    cap, result);
//verify(result);

//
//            ArrayList<Edge> edges = new ArrayList<Edge>();
//            for (Vertex vtx = vtxHead; vtx != vtxTail; vtx = vtx.next.end)
//            {
//                edges.add(vtx.next);
//            }



//            for (Vertex vtx = vtxHead; vtx != vtxTail; vtx = vtx.next.end)
//            {
//                if (vtx != vtxHead && vtx != vtxTail)
//                {
//                    appendJoinLeft(vtx, join, miterLimit, result);
//verify(result);
////System.err.println("Join Left: " + result.get(result.size() - 1));
//                }
//
//                BezierCurve curveNext = vtx.next.getOffsetCurveLeft();
//                if (curveNext.getDegree() > 0)
//                {
//                    result.add(curveNext);
//                }
//verify(result);
////System.err.println("w0: " + vtx.next.start.weight);
////System.err.println("w1: " + vtx.next.end.weight);
////System.err.println("Seg Left: " + curveNext);
//            }
//
//            BezierMath.cap(vtxTail.prev.getOffsetCurveLeft().getEndX(),
//                    vtxTail.prev.getOffsetCurveLeft().getEndY(),
//
//                    vtxTail.prev.getOffsetCurveRight().getStartX(),
//                    vtxTail.prev.getOffsetCurveRight().getStartY(),
//                    cap, result);
////System.err.println("Cap End: " + result.get(result.size() - 1));
//verify(result);
//
//            for (Vertex vtx = vtxTail; vtx != vtxHead; vtx = vtx.prev.start)
//            {
//                if (vtx != vtxHead && vtx != vtxTail)
//                {
//                    appendJoinRight(vtx, join, miterLimit, result);
////System.err.println("Join Right: " + result.get(result.size() - 1));
//if (!verify(result))
//{
//    appendJoinRight(vtx, join, miterLimit, result);
//}
//                }
//
//                BezierCurve curvePrev = vtx.prev.getOffsetCurveRight();
//                result.add(curvePrev);
//verify(result);
////System.err.println("Seg Right: " + curvePrev);
//            }
//
//            BezierMath.cap(vtxHead.next.getOffsetCurveRight().getEndX(),
//                    vtxHead.next.getOffsetCurveRight().getEndY(),
//
//                    vtxHead.next.getOffsetCurveLeft().getStartX(),
//                    vtxHead.next.getOffsetCurveLeft().getStartY(),
//                    cap, result);
//verify(result);
//System.err.println("Cap Start: " + result.get(result.size() - 1));
        }

    }

    private void appendJoinLeft(Vertex vtx, Join join, double miterLimit, ArrayList<BezierCurve> result)
    {
        Edge edgeNext = vtx.next;
        Edge edgePrev = vtx.prev;

//if (edgeNext == null || edgePrev == null)
//{
//    int j = 9;
//}
        BezierCurve curveNext = edgeNext.getOffsetCurveLeft();
        BezierCurve curvePrev = edgePrev.getOffsetCurveLeft();

        if (curvePrev.getEndX() == curveNext.getStartX()
                && curvePrev.getEndY() == curveNext.getStartY())
        {
            //We match the end point exactly.  No need to add a join
            return;
        }
        else if (vtx.isWindingCW())
        {
            //Point join
            result.add(new BezierCurveLine(curvePrev.getEndX(), curvePrev.getEndY(),
                    curveNext.getStartX(), curveNext.getStartY()));
        }
        else
        {
            BezierMath.join(new CyVector2d(curvePrev.getEndX(), curvePrev.getEndY()),
                    new CyVector2d(curvePrev.getEndTanX(), curvePrev.getEndTanY()),
                    new CyVector2d(curveNext.getStartX(), curveNext.getStartY()),
                    new CyVector2d(curveNext.getStartTanX(), curveNext.getStartTanY()),
                    new CyVector2d(vtx.x, vtx.y),
                    join, miterLimit, result
                    );
        }
    }

//    private void appendOutlineLeft(Join join, double miterLimit, ArrayList<BezierCurve> result)
//    {
//        Vertex vtx = vtxHead;
//        do
//        {
//            Edge edgeNext = vtx.next;
////            Edge edgePrev = vtx.prev;
//
//            BezierCurve curveNext = edgeNext.getOffsetCurveLeft();
////            BezierCurve curvePrev = edgePrev.getOffsetCurveLeft();
//
//            appendJoinLeft(path, vtx, join, miterLimit);
//
//            curveNext.appendToPath(path);
//
//            //Next iteration
//            vtx = edgeNext.end;
//        } while (vtx.next != null && vtx != vtxHead);
//    }

    private void appendJoinRight(Vertex vtx, Join join, double miterLimit, ArrayList<BezierCurve> result)
    {
        Edge edgeNext = vtx.next;
        Edge edgePrev = vtx.prev;

        BezierCurve curveNext = edgeNext.getOffsetCurveRight();
        BezierCurve curvePrev = edgePrev.getOffsetCurveRight();

        if (curvePrev.getStartX() == curveNext.getEndX()
                && curvePrev.getStartY() == curveNext.getEndY())
        {
            //We match the end point exactly.  No need to add a join
        }
        else if (vtx.isWindingCCW())
        {
            //Point join
            result.add(new BezierCurveLine(curveNext.getEndX(), curveNext.getEndY(),
                    curvePrev.getStartX(), curvePrev.getStartY()));
        }
        else
        {
            CyVector2d p0 = new CyVector2d(curveNext.getEndX(), curveNext.getEndY());
            CyVector2d t0 = new CyVector2d(curveNext.getEndTanX(), curveNext.getEndTanY());
            CyVector2d p1 = new CyVector2d(curvePrev.getStartX(), curvePrev.getStartY());
            CyVector2d t1 = new CyVector2d(curvePrev.getStartTanX(), curvePrev.getStartTanY());

            BezierMath.join(p0,
                    t0,
                    p1,
                    t1,
                    new CyVector2d(vtx.x, vtx.y),
                    join, miterLimit, result
                    );
        }
    }

    public boolean isEmpty()
    {
        return vtxHead == null;
    }

    private boolean verify(ArrayList<BezierCurve> result)
    {
        if (result.isEmpty())
        {
            return true;
        }

        BezierCurve last = result.get(result.size() - 1);
        if ((last.getStartKnotX() == 0 && last.getStartKnotY() == 0)
                ||
                (last.getEndKnotX() == 0 && last.getEndKnotY() == 0))
        {
            int j = 9;
            return false;
        }
        return true;
    }


//    private void appendOutlineRight(Path2D.Double path, Join join, double miterLimit)
//    {
//        Vertex vtx = vtxTail;
//        do
//        {
////            Edge edgeNext = vtx.next;
//            Edge edgePrev = vtx.prev;
//
////            BezierCurve curveNext = edgeNext.getOffsetCurveRight();
//            BezierCurve curvePrev = edgePrev.getOffsetCurveRight();
//
//            appendJoinRight(path, vtx, join, miterLimit);
//
//            curvePrev.appendToPath(path);
//
//            //Next iteration
//            vtx = edgePrev.end;
//        } while (vtx.prev != null && vtx != vtxTail);
//    }

    //-------------------------
//    public static enum VertexSmooth
//    {
//        CUSP, SMOOTH, TENSE
//    }

    class Edge
    {
        BezierCurve curve;
        Vertex start;
        Vertex end;
        boolean visible;
//        BezierCurve offsetLeft;
//        BezierCurve offsetRight;
        
        //Cache useful data
        FlatSegmentList segments;

        FlatSegmentList offsetLeft;
        FlatSegmentList offsetRight;
        BezierCurve offsetCurveLeft;
        BezierCurve offsetCurveRight;
//        BezierCurve clippedLeft;
//        BezierCurve clippedRight;

//        BezierCurve[] curveLeft;
//        BezierCurve[] curveRight;

        public Edge(BezierCurve curve, Vertex start, Vertex end, boolean visible)
        {
            this.curve = curve;
            this.start = start;
            this.end = end;
            this.visible = visible;

//if (start.weight == end.weight)
//{
//    int j = 9;
//}

        }

        public FlatSegmentList getSegments()
        {
            if (segments == null)
            {
                segments = curve.getFlatSegments(flatnessSquared);
            }
            return segments;
        }

        private void buildOffsets()
        {
            if (offsetLeft == null)
            {
//System.err.println("Weight left start/end: " + start.weight + " " + end.weight);
                offsetLeft = curve.createOffset(start.weight, end.weight, flatnessSquared);
                offsetCurveLeft = offsetLeft.fitCurve();
            }
            if (offsetRight == null)
            {
//System.err.println("Weight right start/end: " + start.weight + " " + end.weight);
                offsetRight = curve.reverse().createOffset(end.weight, start.weight, flatnessSquared);
                offsetCurveRight = offsetRight.fitCurve();
            }

if ((offsetCurveLeft.getStartX() == 0 && offsetCurveLeft.getStartY() == 0)
    || (offsetCurveLeft.getEndX() == 0 && offsetCurveLeft.getEndY() == 0)
   )
{
    //DEBUG
    offsetLeft = null;
    buildOffsets();
}
        }

        public FlatSegmentList getOffsetLeft()
        {
            buildOffsets();
            return offsetLeft;
        }

        public FlatSegmentList getOffsetRight()
        {
            buildOffsets();
            return offsetRight;
        }

        public BezierCurve getOffsetCurveLeft()
        {
            buildOffsets();
            return offsetCurveLeft;
        }

        public BezierCurve getOffsetCurveRight()
        {
            buildOffsets();
            return offsetCurveRight;
        }

//        public BezierCurve getClippedOffsetLeft()
//        {
//            if (clippedLeft == null)
//            {
//                double t0 = 0, t1= 1;
//                if (start.isWindingCCW())
//                {
//                    double[] isect = getOffsetLeft().findFirstIntersection(start.prev.getOffsetLeft());
//                    t0 = isect[0];
//                }
//                if (end.isWindingCCW())
//                {
//                    double[] isect = end.next.getOffsetLeft().findFirstIntersection(getOffsetLeft());
//                    t1 = isect[1];
//                }
//
//                if (t0 != 0 && t1 != 1)
//                {
//                    BezierCurve[] segs = clippedLeft.split(new double[]{t0, t1}, null);
//                    clippedLeft = segs[1];
//                }
//                else if (t0 != 0)
//                {
//                    BezierCurve[] segs = clippedLeft.split(t0, null);
//                    clippedLeft = segs[1];
//                }
//                else if (t1 != 0)
//                {
//                    BezierCurve[] segs = clippedLeft.split(t1, null);
//                    clippedLeft = segs[0];
//                }
//                else
//                {
//                    clippedLeft = getOffsetCurveLeft();
//                }
//
//                //Align
//                if (t0 != 0 && start.prev.offsetCurveLeft != null)
//                {
//                    clippedLeft.setStartX(start.prev.offsetCurveLeft.getEndX());
//                    clippedLeft.setStartY(start.prev.offsetCurveLeft.getEndY());
//                }
//                if (t1 != 1 && end.next.offsetCurveLeft != null)
//                {
//                    clippedLeft.setEndX(end.next.offsetCurveLeft.getStartX());
//                    clippedLeft.setEndY(end.next.offsetCurveLeft.getStartY());
//                }
//            }
//            return clippedLeft;
//        }

//        public BezierCurve getClippedOffsetRight()
//        {
//            if (clippedRight == null)
//            {
//                double t0 = 0, t1= 1;
//                if (start.isWindingCW())
//                {
//                    double[] isect = start.prev.getOffsetRight().findFirstIntersection(getOffsetRight());
//                    t1 = isect[1];
//                }
//                if (end.isWindingCW())
//                {
//                    double[] isect = getOffsetRight().findFirstIntersection(end.next.getOffsetRight());
//                    t0 = isect[0];
//                }
//
//                if (t0 != 0 && t1 != 1)
//                {
//                    BezierCurve[] segs = clippedRight.split(new double[]{t0, t1}, null);
//                    clippedRight = segs[1];
//                }
//                else if (t0 != 0)
//                {
//                    BezierCurve[] segs = clippedRight.split(t0, null);
//                    clippedRight = segs[1];
//                }
//                else if (t1 != 0)
//                {
//                    BezierCurve[] segs = clippedRight.split(t1, null);
//                    clippedRight = segs[0];
//                }
//                else
//                {
//                    clippedRight = getOffsetCurveRight();
//                }
//
//                //Align
//                if (t0 != 0 && start.prev.offsetCurveRight != null)
//                {
//                    clippedRight.setStartX(end.next.offsetCurveRight.getEndX());
//                    clippedRight.setStartY(end.next.offsetCurveRight.getEndY());
//                }
//                if (t1 != 1 && end.next.offsetCurveRight != null)
//                {
//                    clippedRight.setEndX(start.prev.offsetCurveRight.getStartX());
//                    clippedRight.setEndY(start.prev.offsetCurveRight.getStartY());
//                }
//            }
//            return clippedRight;
//        }
    }

    class Vertex
    {
        float weight;
        VertexSmooth smooth;
        int x;
        int y;
        Edge next;
        Edge prev;

        //Cached copies of outlines
        BezierCurve[] left;
        BezierCurve[] right;
        BezierCurve[] cap;

        public Vertex(float weight, VertexSmooth smooth, int x, int y)
        {
            this.weight = weight;
            this.smooth = smooth;
            this.x = x;
            this.y = y;
        }

        public boolean isWindingCCW()
        {
            if (prev == null || !prev.visible || next == null || !next.visible)
            {
                return false;
            }
            int ax = prev.getSegments().getTanOutX();
            int ay = prev.getSegments().getTanOutY();
            int bx = next.getSegments().getTanInX();
            int by = next.getSegments().getTanInY();
            int cross = ay * bx - ax * by;
            return cross > 0;
        }

        public boolean isWindingCW()
        {
            if (prev == null || !prev.visible || next == null || !next.visible)
            {
                return false;
            }
            int ax = prev.getSegments().getTanOutX();
            int ay = prev.getSegments().getTanOutY();
            int bx = next.getSegments().getTanInX();
            int by = next.getSegments().getTanInY();
            int cross = ay * bx - ax * by;
            return cross < 0;
        }

        private void clearCache()
        {
            left = right = cap = null;
//            if (next != null)
//            {
//                next.clippedLeft = null;
//                next.clippedRight = null;
//            }
//            if (prev != null)
//            {
//                prev.clippedLeft = null;
//                prev.clippedRight = null;
//            }
        }
    }
}
