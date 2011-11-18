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

import static com.kitfox.raven.shape.bezier.BezierMath.*;
import com.kitfox.raven.shape.bezier.FlatSegmentList.FlatIterator;
import java.awt.Rectangle;
import java.awt.geom.Path2D.Double;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class BezierCurve
{
    FlatSegment flatSegments;

    protected static final double SEG_FLATTEN_TOLERANCE_SQ = 400 * 400;

    abstract public double getHullLength();
//
//    protected double square(double v)
//    {
//        return v * v;
//    }
//
//    protected double distance(double p0x, double p0y, double p1x, double p1y)
//    {
//        return Math.sqrt(square(p1x - p0x) + square(p1y - p0y));
//    }
//
//    protected double lerp(double v0, double v1, double a)
//    {
//        return v0 * (1 - a) + v1 * a;
//    }

    abstract public double[] calcPoint(double t, double[] point);


    /**
     * Split curve into two parts.
     *
     * @param t The paremetric point where the curve will be split
     *
     * @param segs Array that will be filled with calculated segments.  If
     * null, an array will be allocated.
     * @return Array with component segments.
     */
    abstract public BezierCurve[] split(double t, BezierCurve[] segs);

    /**
     * Split curve into multiple parts.
     *
     * @param t A sorted list of t points at which the curve will
     * be divided.
     *
     * @param segs Array that will be filled with calculated segments.  If
     * null, an array will be allocated.
     * @return Array with component segments.
     */
    public BezierCurve[] split(double[] t, BezierCurve[] segs)
    {
        if (segs == null)
        {
            segs = new BezierCurve[t.length + 1];
        }

        BezierCurve[] segsSplit = new BezierCurve[2];
        BezierCurve curve = this;
        double offset = 0;
        for (int i = 0; i < t.length; ++i)
        {
            curve.split((t[i] - offset) / (1 - offset), segsSplit);
            segs[i] = segsSplit[0];
            curve = segsSplit[1];
            offset = t[i];
        }

        segs[segs.length - 1] = curve;

        return segs;
    }

//    public BezierCurve[] flatten(BezierCurve[] segs)
//    {
//        ArrayList<BezierCurveLine> lines = new ArrayList<BezierCurveLine>();
//
//        if (
//    }

    abstract public int getNumKnots();
    abstract public int getKnotX(int index);
    abstract public int getKnotY(int index);

    abstract public int getStartX();
    abstract public int getStartY();
    abstract public int getEndX();
    abstract public int getEndY();
    abstract public void setStartX(int px);
    abstract public void setStartY(int py);
    abstract public void setEndX(int px);
    abstract public void setEndY(int py);

    abstract public int getStartKnotX();
    abstract public int getStartKnotY();
    abstract public int getEndKnotX();
    abstract public int getEndKnotY();
    abstract public void setStartKnotX(int x);
    abstract public void setStartKnotY(int y);
    abstract public void setEndKnotX(int x);
    abstract public void setEndKnotY(int y);

    abstract public int getHullMinX();
    abstract public int getHullMinY();
    abstract public int getHullMaxX();
    abstract public int getHullMaxY();

    abstract public double calcPointX(double t);
    abstract public double calcPointY(double t);

    abstract public BezierCurve getDerivative();

    public int getDegree()
    {
        return getNumKnots() + 1;
    }

    public int getOrder()
    {
        return getNumKnots() + 2;
    }

    public boolean boundingBoxOverlap(BezierCurve curve)
    {
        return curve.getHullMinX() < getHullMaxX()
                && curve.getHullMaxX() > getHullMinX()
                && curve.getHullMinY() < getHullMaxY()
                && curve.getHullMaxY() > getHullMinY();
    }

    abstract public double getFlatnessSquared();

//    public double getFlatnessSquared()
//    {
//        int vx = getEndX() - getStartX();
//        int vy = getEndY() - getStartY();
//        if (vx == 0 && vy == 0)
//        {
//            return 0;
//        }
//
//        int dotVV = vx * vx + vy * vy;
//        if (dotVV == 0)
//        {
//            return 0;
//        }
////        System.err.write(vy);
//
//        double res = 0;
//
//        for (int i = 0; i < getNumKnots(); ++i)
//        {
//            int kx = getKnotX(i) - getStartX();
//            int ky = getKnotY(i) - getStartY();
//
//            //Project knot vector onto base
//            int dotVK = vx * kx + vy * ky;
//            if (dotVV == 0)
//            {
//                continue;
//            }
//            double scalar = (double)dotVK / dotVV;
//
//            double wx = scalar * vx;
//            double wy = scalar * vy;
//
//            res = Math.max(res, square(wx - kx) + square(wy - ky));
//        }
//        return res;
//    }

//    public FlatSegment getFlatSegments()
//    {
//        if (flatSegments == null)
//        {
//            flatSegments = getFlatSegments(SEG_FLATTEN_TOLERANCE_SQ);
//        }
//        return flatSegments;
//    }

    public FlatSegmentList getFlatSegments(double flatnessSquared)
    {
        return getFlatSegments(flatnessSquared, 0, 1);
//        FlatSegment seg = getFlatSegments(flatnessSquared, 0, 1);
//        FlatSegment end = new FlatSegment(getEndX(), getEndY(), 1);
//        seg.getLast().next = end;
//        return seg;
    }


    protected FlatSegmentList getFlatSegments(double flatnessSquared, double tOffset, double tSpan)
    {
//System.err.println("Depth: " + (Math.log(tSpan) / Math.log(2)));
        if (getFlatnessSquared() <= flatnessSquared)
        {
//            return new FlatSegment(getStartX(), getStartY(), tOffset);
            FlatSegmentList list = new FlatSegmentList(getDegree());
            list.addSegment(getStartX(), getStartY(), tOffset);
            list.addSegment(getEndX(), getEndY(), tOffset + tSpan);
            return list;
        }

//++flatDepth;
//if (tSpan < .001)
//{
//    int j = 9;
//}

        BezierCurve[] segs = split(.5f, null);

        double halfSpan = tSpan / 2;
        FlatSegmentList seg0 = segs[0].getFlatSegments(flatnessSquared, tOffset, halfSpan);
        FlatSegmentList seg1 = segs[1].getFlatSegments(flatnessSquared, tOffset + halfSpan, halfSpan);

//        seg0.getLast().next = seg1;
        seg0.concatenate(seg1);

        return seg0;
    }

    /**
     * Split this segment into non self intersecting segments.  Note that
     * while the segments are guaranteed not to intesect themselves,
     * they still may intersect each other.
     *
     * @return
     */
    public ArrayList<SplitRecord> splitAtSelfIntersections(
            ArrayList<SplitRecord> result, double flatnessSquared)
    {
        SplitRecord rec = new SplitRecord(0, 1, this, flatnessSquared);
        return rec.splitAtSelfIntersections(result);
    }

    /**
     * Split this curve with passed curve at all points where the two curves
     * intersect.  Neither this curve nor the passed one may self intersect.
     *
     * @param curve
     * @param result
     * @return Array of broken apart curves
     */
    public SplitCurvesRecord splitCurves(BezierCurve curve, double flatnessSquared)
    {
        SplitCurvesRecord rec = new SplitCurvesRecord();
        rec.otherSplits.add(new SplitRecord(0, 1, curve, flatnessSquared));
        SplitRecord localRec = new SplitRecord(0, 1, this, flatnessSquared);
        localRec.splitCurves(rec);
        return rec;
    }

    /**
     * A quick way to find a vector tangent to the curve using De Casteljau's
     * algorithm.  Magnitude of vector not necessarily equal to the magnitude
     * of the derivative.
     * 
     * @param t
     * @param tan
     * @return
     */
    abstract public double[] getTangent(double t, double[] tan);

    abstract public BezierCurve reverse();

    public abstract BezierCurve copy();

    public FlatSegmentList createOffset(double weightStart, double weightEnd, double flatnessSquared)
    {
        FlatSegmentList segs = getFlatSegments(flatnessSquared);
//        int size = segs.size();
//        GMatrix P = new GMatrix(size, 2);
//        double[] Ptimes = new double[size];

        BezierCurve der = getDerivative();

//        int idx = 0;
        ArrayList<Ray> rays = new ArrayList<Ray>();
        for (FlatIterator it = segs.iterator(); it.hasNext();)
        {
            FlatSegment seg = it.next();
            double dx = der.calcPointX(seg.t);
            double dy = der.calcPointY(seg.t);

            //Find unit normal, rotated 90 deg CCW from curve direction
            double magI = 1 / Math.sqrt(dx * dx + dy * dy);
            double nx = -dy * magI;
            double ny = dx * magI;

            rays.add(new Ray(seg.x, seg.y,
                    (int)(seg.x + nx * lerp(weightStart, weightEnd, seg.t)),
                    (int)(seg.y + ny * lerp(weightStart, weightEnd, seg.t)),
                    seg.t));
//            Ptimes[idx] = seg.t;
//            P.setElement(idx, 0, seg.x + nx * lerp(weightStart, weightEnd, seg.t));
//            P.setElement(idx, 1, seg.y + ny * lerp(weightStart, weightEnd, seg.t));
        }

        //Eliminate spans of rays that overlap
        FlatSegmentList newSegs = new FlatSegmentList(getDegree());
//        ArrayList<Ray> raysNonIsect = new ArrayList<Ray>();
        for (int i = 0; i < rays.size(); ++i)
        {
//            Ray rayHead = rays.get(i);
//            Ray rayTail = rayHead;
            //Count ahead number of intersecting rays
            int j = i + 1;
            for (; j < rays.size(); ++j)
            {
                Ray rayPrev = rays.get(j - 1);
                Ray rayCur = rays.get(j);
                if (!rayCur.intersects(rayPrev))
                {
                    break;
                }
            }

            if (j == i + 1)
            {
                //No intersecting rays at i
                Ray ray = rays.get(i);
                newSegs.addSegment(ray.ex, ray.ey, ray.t);
            }
            else
            {
                Ray rayStart = rays.get(i);
                Ray rayEnd = rays.get(j - 1);

                newSegs.addSegment((rayStart.ex + rayEnd.ex) / 2,
                        (rayStart.ey + rayEnd.ey) / 2,
                        (rayStart.t + rayEnd.t) / 2);
            }
        }

        return newSegs;
    }

    abstract public void appendToPath(Double path);

    public int getStartTanX()
    {
        return getStartKnotX() - getStartX();
    }

    public int getStartTanY()
    {
        return getStartKnotY() - getStartY();
    }

    public int getEndTanX()
    {
        return getEndX() - getEndKnotX();
    }

    public int getEndTanY()
    {
        return getEndY() - getEndKnotY();
    }

    public double getBaseLength()
    {
        return distance(getStartX(), getStartY(), getEndX(), getEndY());
    }

    abstract public String toMatlab();

    public Rectangle getBounds()
    {
        Rectangle rect = new Rectangle(getStartX(), getStartY(), 0, 0);
        rect.add(getStartKnotX(), getStartKnotY());
        rect.add(getEndKnotX(), getEndKnotY());
        rect.add(getEndX(), getEndY());
        return rect;
    }


//    public BezierCurve createOffset(GMatrix P)
//    {
//        int degree = getDegree();
//        if (degree == 1)
//        {
//            return new BezierCurveLine(
//                    P.getElement(0, 0), P.getElement(0, 1),
//                    P.getElement(degree, 0), P.getElement(degree, 1)
//                    );
//        }
//
//        GMatrix Q = FitCurve.fitBezierKnots(degree, Ptimes, P);
//        switch (degree)
//        {
//            case 3:
//                return new BezierCurveCubic(
//                        P.getElement(0, 0), P.getElement(0, 1),
//                        Q.getElement(0, 0), Q.getElement(0, 1),
//                        Q.getElement(1, 0), Q.getElement(1, 1),
//                        P.getElement(degree, 0), P.getElement(degree, 1)
//                        );
//            case 2:
//                return new BezierCurveQuadratic(
//                        P.getElement(0, 0), P.getElement(0, 1),
//                        Q.getElement(0, 0), Q.getElement(0, 1),
//                        P.getElement(degree, 0), P.getElement(degree, 1)
//                        );
//        }
//
//        throw new RuntimeException();
//    }

    //------------------------------------------


    class Ray
    {
        final int sx;
        final int sy;
        final int ex;
        final int ey;
        final double t;

        public Ray(int sx, int sy, int ex, int ey, double t)
        {
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
            this.t = t;
        }

        private boolean intersects(Ray ray)
        {
            double[] time = BezierMath.intersectLines(sx, sy, ex - sx, ey - sy,
                    ray.sx, ray.sy, ray.ex - ray.sx, ray.ey - ray.sy, null);

            if (time == null)
            {
                //det == 0.  Ie, parallel lines
                return false;
            }

            return time[0] >= 0 && time[1] < 1 && time[1] >= 0 && time[1] < 1;
        }
    }

    abstract public String toSVGPath();
}
