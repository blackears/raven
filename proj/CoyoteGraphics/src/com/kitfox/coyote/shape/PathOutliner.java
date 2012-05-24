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

package com.kitfox.coyote.shape;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.bezier.BezierCubic2d;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.BezierLine2d;
import com.kitfox.coyote.shape.bezier.BezierQuad2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a connected set of input bezier curves and calculates
 * the outline of best fit at a given offset width.
 *
 * Drawing some ideas from:
 * http://www.home.unix-ag.org/simon/sketch/pathstroke.py
 *
 * @author kitfox
 */
public class PathOutliner extends PathConsumer
{
    private final PathConsumer out;

    private final double radius;
    private final CyStrokeCap cap;
    private final CyStrokeJoin join;
    private final double miterLimit;

    final double flatnessSquared;
    
    //Incoming point and tangent
    double mx;
    double my;
    double sx;
    double sy;

    ArrayList<BezierCurve2d> pathCore = new ArrayList<BezierCurve2d>();

    public PathOutliner(PathConsumer out, double radius, 
            CyStrokeCap cap, CyStrokeJoin join, double miterLimit,
            double flatnessSquared)
    {
        this.out = out;
        this.radius = radius;
        this.cap = cap;
        this.join = join;
        this.miterLimit = miterLimit;
        this.flatnessSquared = flatnessSquared;
    }

    @Override
    public void beginPath()
    {
        out.beginPath();
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
        if (!pathCore.isEmpty())
        {
            finishContour(false);
        }

        sx = mx = x0;
        sy = my = y0;
    }

    @Override
    public void lineTo(double x0, double y0)
    {
        pathCore.add(new BezierLine2d(mx, my, x0, y0));

        mx = x0;
        my = y0;
    }

    @Override
    public void quadTo(double x0, double y0, double x1, double y1)
    {
        pathCore.add(new BezierQuad2d(mx, my, x0, y0, x1, y1));

        mx = x1;
        my = y1;
    }

    @Override
    public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        pathCore.add(new BezierCubic2d(mx, my, x0, y0, x1, y1, x2, y2));

        mx = x2;
        my = y2;
    }

    @Override
    public void closeSubpath()
    {
        if (mx != sx || my != sy)
        {
            lineTo(sx, sy);
        }

        finishContour(true);
    }

    @Override
    public void endPath()
    {
        finishContour(false);
    }

    private void finishContour(boolean close)
    {
        if (pathCore.isEmpty())
        {
            return;
        }

        ArrayList<BezierCurve2d> pathLeft = new ArrayList<BezierCurve2d>();
        ArrayList<BezierCurve2d> pathRight = new ArrayList<BezierCurve2d>();

        for (int i = 0; i < pathCore.size(); ++i)
        {
            BezierCurve2d curve = pathCore.get(i);
            addOffsetWidth(curve, true, 0, pathLeft);
        }

        for (int i = pathCore.size() - 1; i >= 0; --i)
        {
            BezierCurve2d curve = pathCore.get(i).reverse();
            addOffsetWidth(curve, true, 0, pathRight);
        }

        //Prepare paths for export
        BezierCurve2d cl0 = pathLeft.get(0);
        BezierCurve2d cl1 = pathLeft.get(pathLeft.size() - 1);

        BezierCurve2d cr0 = pathRight.get(0);
        BezierCurve2d cr1 = pathRight.get(pathRight.size() - 1);

        if (close)
        {
            //Complete by joining end to start
            addJoin(new CyVector2d(cl1.getEndX(), cl1.getEndY()),
                    new CyVector2d(cl0.getStartX(), cl0.getStartY()),
                    pathLeft);
            exportLoop(pathLeft);

            //Complete by joining end to start
            addJoin(new CyVector2d(cr1.getEndX(), cr1.getEndY()),
                    new CyVector2d(cr0.getStartX(), cr0.getStartY()),
                    pathRight);
            exportLoop(pathRight);
        }
        else
        {
            //Combine into one big path with caps
            addCap(new CyVector2d(cl1.getEndX(), cl1.getEndY()),
                    new CyVector2d(cr0.getStartX(), cr0.getStartY()),
                    pathLeft);
            pathLeft.addAll(pathRight);
            addCap(new CyVector2d(cr1.getEndX(), cr1.getEndY()),
                    new CyVector2d(cl0.getStartX(), cl0.getStartY()),
                    pathLeft);
            
            exportLoop(pathLeft);
        }
        
        pathCore.clear();
    }

    CyVector2d pBase = new CyVector2d();
    CyVector2d tBase = new CyVector2d();
    CyVector2d pOff = new CyVector2d();
    private static final double EPSILON = .01;

//    private boolean isParallelEnough(double t, BezierCurve2d base, BezierCurve2d off)
//    {
//        BezierCurve2d dBase = base.getDerivative();
//        base.evaluate(t, pBase);
//        dBase.evaluate(t, tBase);
//        off.evaluate(t, pOff);
//
//        if (tBase.lengthSquared() == 0)
//        {
//            return true;
//        }
//        tBase.normalize();
//        tBase.rotCCW90();
//        tBase.scale(radius);
//        pBase.add(tBase);
//
//        return pBase.distanceSquared(pOff) < flatnessSquared;
//    }

    private void addOffsetWidth(BezierCurve2d base, boolean join, int depth, ArrayList<BezierCurve2d> list)
    {
        BezierCurve2d off = base.offset(radius);

        boolean splitCurve = false;
        if (!(base instanceof BezierLine2d) 
                && depth < 20)
        {
            BezierCubic2d c = base.asCubic();
            
            double c0x = c.getAx1() - c.getAx0();
            double c0y = c.getAy1() - c.getAy0();
            double c1x = c.getAx2() - c.getAx1();
            double c1y = c.getAy2() - c.getAy1();
            double c2x = c.getAx3() - c.getAx2();
            double c2y = c.getAy3() - c.getAy2();
            
            double dot0 = c0x * c1x + c0y * c1y;
            double dot1 = c1x * c2x + c1y * c2y;
            
            if (dot0 < 0 || dot1 < 0)
            {
                //Split if hull has any angles less than 90  degrees
                splitCurve = true;
            }
            
//            final int numSamp = 5;
//            double ds = 1.0 / (numSamp + 1);
//            for (int i = 0; i < numSamp; ++i)
//            {
//                if (!isParallelEnough((i + 1) * ds, base, off))
//                {
//                    splitCurve = true;
//                    break;
//                }
//            }
        }
        
        if (!splitCurve)
        {
            if (join && !list.isEmpty())
            {
                BezierCurve2d prev = list.get(list.size() - 1);
                addJoin(
                        new CyVector2d(prev.getEndX(), prev.getEndY()),
                        new CyVector2d(off.getStartX(), off.getStartY()),
                        list);
            }

            list.add(off);
            return;
        }

        BezierCurve2d[] curves = base.split(.5);
        addOffsetWidth(curves[0], join, depth + 1, list);
        addOffsetWidth(curves[1], false, depth + 1, list);
    }

    private void addJoin(CyVector2d p0, CyVector2d p1, List<BezierCurve2d> list)
    {
        if (p0.distanceSquared(p1) < flatnessSquared)
        {
            //If close enough, ignore
            return;
        }

        switch (join)
        {
            case BEVEL:
                list.add(new BezierLine2d(p0.x, p0.y, p1.x, p1.y));
                return;
            case MITER:
            {
                if (miterLimit <= 0)
                {
                    list.add(new BezierLine2d(p0.x, p0.y, p1.x, p1.y));
                    return;
                }

                //Vector spanning half way between points
                CyVector2d d = new CyVector2d(p1);
                d.sub(p0);
                d.scale(.5);

                //Shortest distance from spanning seg to origin
                double origDist = Math.sqrt(radius * radius - d.lengthSquared());

                //Distance from origin to apex of unlimited miter
                double miterOrigDist = radius * radius / origDist;
                //Distance from middle of span to max miter point
                double miterDist = miterOrigDist - origDist;

                //Vector pointing away from origin
                CyVector2d norm = new CyVector2d(d);
                norm.rotCCW90();
                norm.normalize();

                CyVector2d miter = new CyVector2d(norm);
                miter.scale(miterDist);
                miter.add(d);
                miter.add(p0);

                if (miterLimit >= miterDist)
                {
                    //Not clamping miter
                    list.add(new BezierLine2d(p0.x, p0.y, miter.x, miter.y));
                    list.add(new BezierLine2d(miter.x, miter.y, p1.x, p1.y));
                    return;
                }

                double ratio = miterLimit / miterDist;

                //Find miter corner points
                CyVector2d miterOff0 = new CyVector2d(miter);
                miterOff0.sub(p0);
                miterOff0.scale(ratio);
                miterOff0.add(p0);

                CyVector2d miterOff1 = new CyVector2d(miter);
                miterOff1.sub(p1);
                miterOff1.scale(ratio);
                miterOff1.add(p1);

                list.add(new BezierLine2d(p0.x, p0.y, miterOff0.x, miterOff0.y));
                list.add(new BezierLine2d(miterOff0.x, miterOff0.y, miterOff1.x, miterOff1.y));
                list.add(new BezierLine2d(miterOff1.x, miterOff1.y, p1.x, p1.y));
                return;
            }
            case ROUND:
            {
                //Vector spanning half way between points
                CyVector2d d = new CyVector2d(p1);
                d.sub(p0);
                d.scale(.5);

                //Shortest distance from spanning seg to origin
                double origDist = Math.sqrt(radius * radius - d.lengthSquared());

                //Vector pointing away from origin
                CyVector2d norm = new CyVector2d(d);
                norm.rotCCW90();
                norm.normalize();

                CyVector2d origin = new CyVector2d(norm);
                origin.scale(-origDist);
                origin.add(d);
                origin.add(p0);

                //Point in middle of circle segment joining points
                CyVector2d pm = new CyVector2d(norm);
                pm.scale(radius);
                pm.add(origin);

                //Find in and out tangents
                CyVector2d t0 = new CyVector2d(p0);
                t0.sub(origin);
                t0.rotCCW90();

                CyVector2d t1 = new CyVector2d(p1);
                t1.sub(origin);
                t1.rotCCW90();

                //Check to see if we are nearly parallel
                double det = t0.x * t1.y - t0.y * t1.x;
                if (det * det < EPSILON)
                {
                    //Tangents are nearly parallel.  Draw a semi circle
                    CyVector2d tan = new CyVector2d(d);
                    tan.rotCCW90();
                    
                    final double ratio = 4 / 3.0;
                    list.add(new BezierCubic2d(p0.x, p0.y,
                            p0.x + tan.x * ratio, p0.y + tan.y * ratio,
                            p1.x + tan.x * ratio, p1.y + tan.y * ratio,
                            p1.x, p1.y));
                    return;
                }

                //Tangents form a basis.  Solve for curve of best fit
                BezierCubic2d curve = BezierCubic2d.create(p0, pm, p1, t0, t1);
                list.add(curve);
                return;
            }
        }
    }

    private void addCap(CyVector2d p0, CyVector2d p1, List<BezierCurve2d> list)
    {
        if (p0.equals(p1))
        {
            //If close enough, ignore
            return;
        }

        switch (cap)
        {
            case BUTT:
                list.add(new BezierLine2d(p0.x, p0.y, p1.x, p1.y));
                return;
            case SQUARE:
            {
                //Vector tangent to line and half the width
                CyVector2d d = new CyVector2d(p1);
                d.sub(p0);
                d.scale(.5);
                d.rotCCW90();

                list.add(new BezierLine2d(p0.x, p0.y,
                        p0.x + d.x, p0.y + d.y));
                list.add(new BezierLine2d(
                        p0.x + d.x, p0.y + d.y,
                        p1.x + d.x, p1.y + d.y));
                list.add(new BezierLine2d(
                        p1.x + d.x, p1.y + d.y,
                        p1.x, p1.y));
                return;
            }
            case ROUND:
            {
                //Vector spanning half way between points
                CyVector2d d = new CyVector2d(p1);
                d.sub(p0);
                d.scale(.5);

                CyVector2d tan = new CyVector2d(d);
                tan.rotCCW90();

                final double ratio = 4 / 3.0;
                list.add(new BezierCubic2d(p0.x, p0.y,
                        p0.x + tan.x * ratio, p0.y + tan.y * ratio,
                        p1.x + tan.x * ratio, p1.y + tan.y * ratio,
                        p1.x, p1.y));
                return;
            }
        }
    }

    private void exportLoop(ArrayList<BezierCurve2d> path)
    {
        BezierCurve2d first = path.get(0);

        double px = first.getStartX();
        double py = first.getStartY();
        out.beginSubpath(px, py);

        for (BezierCurve2d curve: path)
        {
            curve.append(out);
        }

        out.closeSubpath();
    }
}
