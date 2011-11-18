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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author kitfox
 */
public class BezierVertex extends BezierNetworkComponent
{
    protected BezierPoint point;

    ArrayList<BezierEdge> edgeIn = new ArrayList();
    ArrayList<BezierEdge> edgeOut = new ArrayList();

//    public static final String KEY_SMOOTHING = "smoothing";

    public BezierVertex(BezierPoint point)
    {
        this.point = point;
    }

    public BezierVertex(int x, int y)
    {
        this(new BezierPoint(x, y));
    }

    public void moveTo(int px, int py)
    {
        point = new BezierPoint(px, py);
        for (BezierEdge edge: edgeOut)
        {
            edge.getCurve().setStartX(px);
            edge.getCurve().setStartY(py);
        }
        for (BezierEdge edge: edgeIn)
        {
            edge.getCurve().setEndX(px);
            edge.getCurve().setEndY(py);
        }
    }

    public void addEdgeOut(BezierEdge edge)
    {
        if (edge.getCurve().getStartX() != point.getX() ||
                edge.getCurve().getStartY() != point.getY())
        {
            throw new IllegalStateException("Edge does not start in given vertex");
        }
        edgeOut.add(edge);
        edge.start = this;
    }

    public void addEdgeIn(BezierEdge edge)
    {
        if (edge.getCurve().getEndX() != point.getX() ||
                edge.getCurve().getEndY() != point.getY())
        {
            throw new IllegalStateException("Edge does not end in given vertex");
        }
        edgeIn.add(edge);
        edge.end = this;
    }

    public boolean isEmpty()
    {
        return edgeIn.isEmpty() && edgeOut.isEmpty();
    }

    public ArrayList<BezierEdge> getAllEdges()
    {
        ArrayList<BezierEdge> edges = new ArrayList<BezierEdge>(edgeOut);
        edges.addAll(edgeIn);
        return edges;
    }

    public ArrayList<BezierEdge> getEdgesCW()
    {
        ArrayList<BezierEdge> edges = getAllEdges();
        ExitAngleSorter sorter = new ExitAngleSorter(false);
        Collections.sort(edges, sorter);
        return edges;
    }

    public ArrayList<BezierEdge> getEdgesCCW()
    {
        ArrayList<BezierEdge> edges = getAllEdges();
        ExitAngleSorter sorter = new ExitAngleSorter(true);
        Collections.sort(edges, sorter);
        return edges;
    }

    /**
     * Find the paint of the first vector in the CCW direction from the
     * vector given by (dx, dy).  Null if vertex has no edges.
     *
     * @param rx
     * @param ry
     * @return
     */
    public BezierFace getFaceCCW(double rx, double ry)
    {
        BezierEdge edge = nextEdgeCCW(rx, ry);
        if (edge == null)
        {
//            edge = nextEdgeCCW(rx, ry);
            throw new IllegalArgumentException();
//            return null;
        }

        if (edge.exitsFrom(this))
        {
            return edge.faceRight;
        }
        else
        {
            return edge.faceLeft;
        }
    }

    public BezierFace getFaceCW(double rx, double ry)
    {
        BezierEdge edge = nextEdgeCW(rx, ry);
        if (edge == null)
        {
            throw new IllegalArgumentException();
//            return null;
        }

        if (edge.exitsFrom(this))
        {
            return edge.faceLeft;
        }
        else
        {
            return edge.faceRight;
        }
    }

    /**
     * Find the first vector in the CCW direction from the
     * vector given by (dx, dy).  Null if vertex has no edges.
     *
     * @param rx
     * @param ry
     * @return
     */
    public BezierEdge nextEdgeCCW(double rx, double ry)
    {
        double angleR = Math.atan2(ry, rx);
        double bestAngle = Double.NEGATIVE_INFINITY;
        BezierEdge bestEdge = null;

        for (int i = 0; i < edgeOut.size(); ++i)
        {
            BezierEdge edge = edgeOut.get(i);
            //Since rays do not intersect, can use line aprox of curve
            int dx = edge.getCurve().getEndX() - edge.getCurve().getStartX();
            int dy = edge.getCurve().getEndY() - edge.getCurve().getStartY();

            double angleD = Math.atan2(dy, dx);
            while (angleD >= angleR)
            {
                angleD -= Math.PI * 2;
            }

            if (angleD > bestAngle)
            {
                bestEdge = edge;
                bestAngle = angleD;
            }
        }

        for (int i = 0; i < edgeIn.size(); ++i)
        {
            BezierEdge edge = edgeIn.get(i);
            //Since rays do not intersect, can use line aprox of curve
            int dx = edge.getCurve().getStartX() - edge.getCurve().getEndX();
            int dy = edge.getCurve().getStartY() - edge.getCurve().getEndY();

            double angleD = Math.atan2(dy, dx);
            while (angleD >= angleR)
            {
                angleD -= Math.PI * 2;
            }

            if (angleD > bestAngle)
            {
                bestEdge = edge;
                bestAngle = angleD;
            }
        }

        return bestEdge;
    }

    public BezierEdge nextEdgeCW(double rx, double ry)
    {
        double angleR = Math.atan2(ry, rx);
        double bestAngle = Double.POSITIVE_INFINITY;
        BezierEdge bestEdge = null;

        for (int i = 0; i < edgeOut.size(); ++i)
        {
            BezierEdge edge = edgeOut.get(i);
            //Since rays do not intersect, can use line aprox of curve
            int dx = edge.getCurve().getEndX() - edge.getCurve().getStartX();
            int dy = edge.getCurve().getEndY() - edge.getCurve().getStartY();

            double angleD = Math.atan2(dy, dx);
            while (angleD <= angleR)
            {
                angleD += Math.PI * 2;
            }

            if (angleD < bestAngle)
            {
                bestEdge = edge;
                bestAngle = angleD;
            }
        }

        for (int i = 0; i < edgeIn.size(); ++i)
        {
            BezierEdge edge = edgeIn.get(i);
            //Since rays do not intersect, can use line aprox of curve
            int dx = edge.getCurve().getStartX() - edge.getCurve().getEndX();
            int dy = edge.getCurve().getStartY() - edge.getCurve().getEndY();

            double angleD = Math.atan2(dy, dx);
            while (angleD <= angleR)
            {
                angleD += Math.PI * 2;
            }

            if (angleD < bestAngle)
            {
                bestEdge = edge;
                bestAngle = angleD;
            }
        }

        return bestEdge;
    }

    public int getNumEdgesOut()
    {
        return edgeOut.size();
    }

    public int getNumEdgesIn()
    {
        return edgeIn.size();
    }

    public BezierEdge getEdgeOut(int index)
    {
        return edgeOut.get(index);
    }

    public BezierEdge getEdgeIn(int index)
    {
        return edgeIn.get(index);
    }

    /**
     * @return the point
     */
    public BezierPoint getPoint()
    {
        return point;
    }

    @Override
    public Rectangle getBounds()
    {
        return new Rectangle(point.getX(), point.getY(), 0, 0);
    }

    @Override
    public String toString()
    {
        return point.toString();
    }

    public BezierEdge nextEdgeCCW(BezierEdge edge)
    {
        ArrayList<BezierEdge> edges = getEdgesCCW();
        int idx = edges.indexOf(edge);

        if (idx == edges.size() - 1)
        {
            return edges.get(0);
        }

        return edges.get(idx + 1);
    }

    public BezierEdge nextEdgeCW(BezierEdge edge)
    {
        ArrayList<BezierEdge> edges = getEdgesCW();
        int idx = edges.indexOf(edge);

        if (idx == edges.size() - 1)
        {
            return edges.get(0);
        }

        return edges.get(idx + 1);
    }

    //------------------------------------
    class ExitAngleSorter implements Comparator<BezierEdge>
    {
        final boolean ccw;

        public ExitAngleSorter(boolean ccw)
        {
            this.ccw = ccw;
        }

        @Override
        public int compare(BezierEdge o0, BezierEdge o1)
        {
            int res = compareExitTangentAngle(o0, o1);
            if (ccw)
            {
                res = -res;
            }
            return res;
        }

        private int compareExitTangentAngle(BezierEdge o0, BezierEdge o1)
        {
            FlatSegmentList segs0 = o0.getSegList();
            FlatSegmentList segs1 = o1.getSegList();

            if (edgeIn.contains(o0))
            {
                segs0 = segs0.reverse();
            }
            if (edgeIn.contains(o1))
            {
                segs1 = segs1.reverse();
            }

            return segs0.compareToWinding(segs1);
        }
    }

}
