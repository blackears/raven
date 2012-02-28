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

import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.math.bezier.FitCurve;
import static com.kitfox.raven.shape.bezier.BezierMath.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
@Deprecated
public class FlatSegmentList
{
    private FlatSegment head;
    private FlatSegment tail;
    private int size;
    private final int degree;

    public FlatSegmentList(int degree)
    {
        this.degree = degree;
    }
    
    public FlatIterator iterator()
    {
        return new FlatIterator(head);
    }

    public void addSegment(int x, int y, double t)
    {
        FlatSegment seg = new FlatSegment(x, y, t);
        if (head == null)
        {
            head = tail = seg;
            size = 1;
            return;
        }

        tail.next = seg;
        seg.prev = tail;
        tail = seg;
        ++size;
    }

    public GMatrix getPointMatrix()
    {
        GMatrix P = new GMatrix(size, 2);
        int idx = 0;
        for (FlatSegment seg = head; seg != null; seg = seg.next)
        {
            P.setElement(idx, 0, seg.x);
            P.setElement(idx, 1, seg.y);
            ++idx;
        }
        return P;
    }

    public double[] getTimes()
    {
        double[] pTime = new double[size];
        int idx = 0;
        for (FlatSegment seg = head; seg != null; seg = seg.next)
        {
            pTime[idx] = seg.t;
            ++idx;
        }
        return pTime;
    }

    public BezierCurve fitCurve()
    {
        GMatrix P = getPointMatrix();
        double[] Ptimes = getTimes();
        int lastIdx = P.getNumRow() - 1;

        int effectiveDegree = Math.min(degree, P.getNumRow() - 1);

        if (effectiveDegree == 0)
        {
            return new BezierCurvePoint(
                    P.getElement(0, 0), P.getElement(0, 1)
                    );
        }

        if (effectiveDegree == 1)
        {
            return new BezierCurveLine(
                    P.getElement(0, 0), P.getElement(0, 1),
                    P.getElement(lastIdx, 0), P.getElement(lastIdx, 1)
                    );
        }

        GMatrix Q = FitCurve.fitBezierKnots(effectiveDegree, Ptimes, P);
        switch (effectiveDegree)
        {
            case 3:
                return new BezierCurveCubic(
                        P.getElement(0, 0), P.getElement(0, 1),
                        Q.getElement(0, 0), Q.getElement(0, 1),
                        Q.getElement(1, 0), Q.getElement(1, 1),
                        P.getElement(lastIdx, 0), P.getElement(lastIdx, 1)
                        );
            case 2:
                return new BezierCurveQuadratic(
                        P.getElement(0, 0), P.getElement(0, 1),
                        Q.getElement(0, 0), Q.getElement(0, 1),
                        P.getElement(lastIdx, 0), P.getElement(lastIdx, 1)
                        );
        }

        throw new RuntimeException();
    }

    /**
     * Find the closest point on curve to the reference point.
     *
     * @param px
     * @param py
     * @return
     */
    public PointRecord findClosestPointOnCurve(int px, int py)
    {
        if (size <= 2 && head.x == tail.x && head.y == tail.y)
        {
            //This is some weird curve with a flat hull and with
            // its start and end points coincident
            return new PointRecord(BezierMath.distance(head.x, head.y, px, py),
                    0, head.x, head.y);
        }

        double minDist = Float.MAX_VALUE;
        double bestT = 0;
        double bestX = 0;
        double bestY = 0;
//        double[] bestPt = null;

        for (FlatSegment seg = head; seg.next != null; seg = seg.next)
        {
            double[] time = BezierMath.intersectLines(
                    seg.x, seg.y, seg.next.x - seg.x, seg.next.y - seg.y,
                    px, py, seg.next.y - seg.y, -seg.next.x + seg.x, null
                    );

//if (time == null)
//{
//time = BezierMath.intersectLines(
//    seg.x, seg.y, seg.next.x - seg.x, seg.next.y - seg.y,
//    px, py, seg.next.y - seg.y, -seg.next.x + seg.x, null
//    );
//assert false;
//}
            double t = Math.min(1, Math.max(0, time[0]));
            double x = seg.x + t * (seg.next.x - seg.x);
            double y = seg.y + t * (seg.next.y - seg.y);
//            double[] pt = calcPoint(t, null);

            double dist = BezierMath.distance(x, y, px, py);
            if (dist < minDist)
            {
                minDist = dist;
                bestT = seg.t + (seg.next.t - seg.t) * t;
                bestX = x;
                bestY = y;
            }
        }

        return new PointRecord(minDist, bestT, bestX, bestY);
    }

    public void concatenate(FlatSegmentList list)
    {
        if (degree != list.degree)
        {
            throw new IllegalArgumentException();
        }

        if (!tail.equals(list.head))
        {
            throw new IllegalArgumentException();
        }

        tail.next = list.head.next;
        list.head.next.prev = tail;
        tail = list.tail;
        size += list.size - 1;
    }

    /**
     * @return the head
     */
    public FlatSegment getHead()
    {
        return head;
    }

    /**
     * @return the tail
     */
    public FlatSegment getTail()
    {
        return tail;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }

    /**
     * @return the degree
     */
    public int getDegree()
    {
        return degree;
    }

    public int getTanInX()
    {
        return head.next.x - head.x;
    }

    public int getTanInY()
    {
        return head.next.y - head.y;
    }

    public int getTanOutX()
    {
        return tail.x - tail.prev.x;
    }

    public int getTanOutY()
    {
        return tail.y - tail.prev.y;
    }

    public ArrayList<double[]> findIntersections(FlatSegmentList other)
    {
        ArrayList<double[]> isect = new ArrayList<double[]>();

        for (FlatSegment segLocal = head; segLocal.next != null; segLocal = segLocal.next)
        {
            double p0x = segLocal.prev.x;
            double p0y = segLocal.prev.y;
            double r0x = segLocal.x - p0x;
            double r0y = segLocal.y - p0y;

            for (FlatSegment segOther = other.head; segOther.next != null; segOther = segOther.next)
            {
                double p1x = segOther.x;
                double p1y = segOther.y;
                double r1x = segOther.next.x - p1x;
                double r1y = segOther.next.y - p1y;

                double time[] = BezierMath.intersectLines(
                        p0x, p0y, r0x, r0y, p1x, p1y, r1x, r1y, null);
                double t0 = time[0];
                double t1 = time[1];
                if (t0 >= 0 && t0 <= 1 && t1 >= 0 && t1 <= 1)
                {
                    isect.add(time);
                }
            }
        }

        return isect;
    }

    public double[] findFirstIntersection(FlatSegmentList other)
    {
        for (FlatSegment segLocal = head; segLocal.next != null; segLocal = segLocal.next)
        {
            double p0x = segLocal.prev.x;
            double p0y = segLocal.prev.y;
            double r0x = segLocal.x - p0x;
            double r0y = segLocal.y - p0y;

            for (FlatSegment segOther = other.head; segOther.next != null; segOther = segOther.next)
            {
                double p1x = segOther.x;
                double p1y = segOther.y;
                double r1x = segOther.next.x - p1x;
                double r1y = segOther.next.y - p1y;

                double time[] = BezierMath.intersectLines(
                        p0x, p0y, r0x, r0y, p1x, p1y, r1x, r1y, null);
                double t0 = time[0];
                double t1 = time[1];
                if (t0 >= 0 && t0 <= 1 && t1 >= 0 && t1 <= 1)
                {
                    return time;
                }
            }
        }

        return null;
    }

    public FlatSegmentList[] split(double t)
    {
        FlatSegmentList listLeft = new FlatSegmentList(degree);
        FlatSegmentList listRight = new FlatSegmentList(degree);

        FlatSegmentList list = listLeft;

        for (FlatSegment seg = head; seg.next != null; seg = seg.next)
        {
            list.addSegment(seg.x, seg.y, 0);

            if (t >= seg.t && t < seg.next.t)
            {
                int ax = (int)lerp(seg.x, seg.next.x, (t - seg.t) / (seg.next.t - seg.t));
                int ay = (int)lerp(seg.y, seg.next.y, (t - seg.t) / (seg.next.t - seg.t));
                list.addSegment(ax, ay, 1);

                list = listRight;
                list.addSegment(ax, ay, 0);
            }
        }

        return new FlatSegmentList[]{listLeft, listRight};
    }

    public void distributeTimesByDistance()
    {
        double totalDist = 0;
        double[] dist = new double[size - 1];
        int idx = 0;
        for (FlatSegment seg = head; seg.next != null; seg = seg.next)
        {
            dist[idx] = distance(seg.x, seg.y, seg.next.x, seg.next.y);
            totalDist += dist[idx];
            ++idx;
        }

        idx = 0;
        for (FlatSegment seg = head; seg != null; seg = seg.next)
        {
            if (idx == 0)
            {
                seg.t = 0;
            }
            else if (idx == size - 1)
            {
                seg.t = 1;
            }
            else
            {
                seg.t = dist[idx - 1] / totalDist;
            }
            ++idx;
        }

    }

    /**
     * Find the first t value where the point cx, cy is within radius
     * of this edge.
     *
     * @param cx
     * @param cy
     * @param radius
     * @param xform Transform applied to edge points before pick test
     * is performed.
     * @return T value of first hit, or null if there is no hit.
     */
    public Float getHit(double cx, double cy, double radius,
            AffineTransform xform)
    {
        if (head == null)
        {
            return null;
        }

        Point2D.Double curPt = new Point2D.Double();
        Point2D.Double lastPt = new Point2D.Double(head.x, head.y);
        xform.transform(lastPt, lastPt);
        double lastT = 0;
        for (FlatSegment seg = head.next; seg != null; seg = seg.next)
        {
            curPt.setLocation(seg.x, seg.y);
            xform.transform(curPt, curPt);

            double dx = curPt.x - lastPt.x;
            double dy = curPt.y - lastPt.y;
            double lenI = 1 / Math.sqrt(dx * dx + dy * dy);
            double rx = dy * lenI;
            double ry = -dx * lenI;
            double[] time = BezierMath.intersectLines(lastPt.x, lastPt.y,
                    dx, dy,
                    cx, cy, rx, ry, null);

            double distance = Math.abs(time[1]);
            if (time != null && distance < radius
                    && time[0] >= 0 && time[0] <= 1)
            {
                return (float)BezierMath.lerp(lastT, seg.t, time[0]);
            }

            //Get ready for next iteration
            Point2D.Double tmp = lastPt;
            lastPt = curPt;
            curPt = tmp;

            lastT = seg.t;
        }

        return null;
    }

    public FlatSegmentList reverse()
    {
        FlatSegmentList newList = new FlatSegmentList(degree);
        
        for (FlatSegment seg = tail; seg != null; seg = seg.prev)
        {
            newList.addSegment(seg.x, seg.y, 1 - seg.t);
        }
        return newList;
    }

    /**
     * Compares this segment list with passed list.  Determines if it should
     * appear before or after it in a list of segment lists sorted by
     * angle of exit tangent.
     *
     * @param other
     * @return
     */
    public int compareToWinding(FlatSegmentList other)
    {
        if (head.x != other.head.x || head.y != other.head.y)
        {
            throw new IllegalStateException("For angle check, both curves must start at the same point");
        }

        double angle0 = Math.atan2(getTanInY(), getTanInX());
        double angle1 = Math.atan2(other.getTanInY(), other.getTanInX());

        if (angle0 != angle1)
        {
            //Curves have different exit tangents
            return Double.compare(angle0, angle1);
        }

        //Initial line segments have equal angle.  Find first
        // segment to deviate from this angle
        int bend0x = 0, bend0y = 0;
        double bend0angle = Double.NaN;
        if (head.next.next != null)
        {
            bend0x = head.next.x;
            bend0y = head.next.y;
            int dx = head.next.next.x - head.next.x;
            int dy = head.next.next.y - head.next.y;
            bend0angle = Math.atan2(dy, dx);
        }

        int bend1x = 0, bend1y = 0;
        double bend1angle = Double.NaN;
        if (other.head.next.next != null)
        {
            bend1x = other.head.next.x;
            bend1y = other.head.next.y;
            int dx = other.head.next.next.x - other.head.next.x;
            int dy = other.head.next.next.y - other.head.next.y;
            bend1angle = Math.atan2(dy, dx);
        }

        if (!Double.isNaN(bend0angle) && !Double.isNaN(bend1angle))
        {
            //Choose the one that bends first
            long dist0 = BezierMath.distanceSquared(bend0x, bend0y, head.x, head.y);
            long dist1 = BezierMath.distanceSquared(bend1x, bend1y, head.x, head.y);

            if (dist0 == dist1)
            {
                return Double.compare(bend0angle, bend1angle);
            }
            else if (dist0 < dist1)
            {
                return Double.compare(bend0angle, angle0);
            }
            else
            {
                return Double.compare(bend1angle, angle0);
            }
        }
        else if (!Double.isNaN(bend0angle))
        {
            return Double.compare(bend0angle, angle0);
        }
        else if (!Double.isNaN(bend1angle))
        {
            return Double.compare(bend1angle, angle0);
        }

        //Two straight lines
        return 0;
    }

    void pointInsideCheck(BezierInsideOutsideCheck check)
    {
        for (FlatSegment seg = head; seg.next != null; seg = seg.next)
        {
            int x0 = seg.x;
            int y0 = seg.y;
            int x1 = seg.next.x;
            int y1 = seg.next.y;

            check.testNextLineSeg(x0, y0, x1, y1);
        }
        
    }

    //------------------------------

    public class FlatIterator implements Iterator<FlatSegment>
    {
        FlatSegment ptr;

        private FlatIterator(FlatSegment ptr)
        {
            this.ptr = ptr;
        }

        @Override
        public boolean hasNext()
        {
            return ptr != null;
        }

        @Override
        public FlatSegment next()
        {
            FlatSegment value = ptr;
            ptr = ptr.next;
            return value;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
