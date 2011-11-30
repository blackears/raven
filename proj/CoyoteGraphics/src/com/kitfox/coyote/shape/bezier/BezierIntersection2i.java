/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier;

/**
 * Finds intersections of two Bezier curves.
 * 
 * Note that this implementation heavily relies on resolution.  Curves
 * are sampled down to unit resolution.  If a point is common to 
 * both curves at a given resolution, they are considered to intersect
 * at that point.  Note that this means that if curves come close to
 * touching (ie, they are nearly tangent at one point), many 
 * local intersections may be detected for what may actually be only
 * a single intersection.
 *
 * @author kitfox
 */
public class BezierIntersection2i
{
    public static void findIntersections(Callback callback, 
            BezierCurve2i c0, BezierCurve2i c1)
    {
        findIntersections(callback, c0, 0, 1, c1, 0, 1);
        callback.done();
    }
    
    private static void findIntersections(Callback callback, 
            BezierCurve2i c0, double t0Min, double t0Max,
            BezierCurve2i c1, double t1Min, double t1Max)
    {
        if (!c0.boundingBoxIntersects(c1))
        {
            return;
        }
        
        boolean c0Unit = c0.isUnitBoundingBox();
        boolean c1Unit = c1.isUnitBoundingBox();
        if (c0Unit && c1Unit)
        {
            callback.emitCrossover(
                    t0Max == 1 ? 1 : t0Min, t1Max == 1 ? 1 : t1Min,
                    c1.getMinX(), c1.getMinY());
        }
        else if (c0Unit)
        {
            double t1Mid = (t1Min + t1Max) / 2;
            BezierCurve2i[] curves1 = c1.split(.5);
            if (!isDegenerate(curves1[0], curves1[1]))
            {
                findIntersections(callback, 
                        c0, t0Min, t0Max, curves1[0], t1Min, t1Mid);
            }
            if (!isDegenerate(curves1[1], curves1[0]))
            {
                findIntersections(callback, 
                        c0, t0Min, t0Max, curves1[1], t1Mid, t1Max);
            }
        }
        else if (c1Unit)
        {
            double t0Mid = (t0Min + t0Max) / 2;
            BezierCurve2i[] curves0 = c1.split(.5);
            if (!isDegenerate(curves0[0], curves0[1]))
            {
                findIntersections(callback, 
                        curves0[0], t0Min, t0Mid, c1, t1Min, t1Max);
            }
            if (!isDegenerate(curves0[1], curves0[0]))
            {
                findIntersections(callback, 
                        curves0[1], t0Mid, t0Max, c1, t1Min, t1Max);
            }
        }
        else
        {
            double t0Mid = (t0Min + t0Max) / 2;
            BezierCurve2i[] curves0 = c1.split(.5);
            double t1Mid = (t1Min + t1Max) / 2;
            BezierCurve2i[] curves1 = c1.split(.5);
            
            if (!isDegenerate(curves0[0], curves0[1]) && !isDegenerate(curves1[0], curves1[1]))
            {
                findIntersections(callback, 
                        curves0[0], t0Min, t0Mid, curves1[0], t1Min, t1Mid);
            }
            if (!isDegenerate(curves0[1], curves0[0]) && !isDegenerate(curves1[0], curves1[1]))
            {
                findIntersections(callback, 
                        curves0[1], t0Mid, t0Max, curves1[0], t1Min, t1Mid);
            }
            if (!isDegenerate(curves0[0], curves0[1]) && !isDegenerate(curves1[1], curves1[0]))
            {
                findIntersections(callback, 
                        curves0[0], t0Min, t0Mid, curves1[1], t1Mid, t1Max);
            }
            if (!isDegenerate(curves0[1], curves0[0]) && !isDegenerate(curves1[1], curves1[0]))
            {
                findIntersections(callback, 
                        curves0[1], t0Mid, t0Max, curves1[1], t1Mid, t1Max);
            }
        }
    }
    
    private static boolean isDegenerate(BezierCurve2i c0, BezierCurve2i c1)
    {
        return c0.isUnitBoundingBox() && c1.boundingBoxContains(c0);
    }
    
    /**
     * Cubic curves may self intersect at one point.  Check
     * to see if such a point exists.
     * 
     * @param curve
     * @return 
     */
    public static void findSelfIntersection(Callback callback, 
            BezierCurve2i curve)
    {
        if (!curve.convexHullSelfIsect())
        {
            callback.done();
            return;
        }
        
        double tMin = 0, tMax = 1;
        while (true)
        {
            double t = (tMax + tMin) / 2;
            BezierCurve2i[] curves = curve.split(t);
            if (curves[0].convexHullSelfIsect())
            {
                tMax = t;
            }
            else if (curves[1].convexHullSelfIsect())
            {
                tMin = t;
            }
            else
            {
                callback =
                        new SelfCallback(callback, curves[0].getEndX(), curves[0].getEndY());
                
                findIntersections(callback, 
                        curves[0], 0, t,
                        curves[1], t, 1);
                callback.done();
                return;
            }
        }
    }
    
    //----------------------------
    public static interface Callback
    {
        /**
         * A crossover has been detected between two curves.
         * 
         * @param t0 Time value for intersection for curve 0
         * @param t1 Time value for intersection for curve 1
         * @param x X coord of intersection
         * @param y Y coord of intersection
         */
        public void emitCrossover(double t0, double t1, int x, int y);
        
        /**
         * Indicates that search for intersecting points has finished.
         */
        public void done();
    }

    /**
     * This callback wrapper omits the overlapping point where 
     * the self intersecting curve was split into two pieces.
     */
    private static class SelfCallback implements Callback
    {
        Callback wrapped;
        int sx;
        int sy;

        public SelfCallback(Callback wrapped, int sx, int sy)
        {
            this.wrapped = wrapped;
            this.sx = sx;
            this.sy = sy;
        }

        @Override
        public void emitCrossover(double t0, double t1, int x, int y)
        {
            if (x == sx && y == sy)
            {
                return;
            }
            wrapped.emitCrossover(t0, t1, x, y);
        }

        @Override
        public void done()
        {
            wrapped.done();
        }
    }
}
