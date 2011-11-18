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

package com.kitfox.coyote.shape.bezier;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
public class BezierQuad extends BezierCurve
{
    final double ax0;
    final double ay0;
    final double ax1;
    final double ay1;
    final double ax2;
    final double ay2;

    public BezierQuad(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
        this.ax1 = ax1;
        this.ay1 = ay1;
        this.ax2 = ax2;
        this.ay2 = ay2;
    }

    @Override
    public BezierQuad reverse()
    {
        return new BezierQuad(ax2, ay2, ax1, ay1, ax0, ay0);
    }

    @Override
    public double getTanInX()
    {
        return (ax1 != ax0 || ay1 != ay0) ? ax1 - ax0 : ax2 - ax0;
    }

    @Override
    public double getTanInY()
    {
        return (ax1 != ax0 || ay1 != ay0) ? ay1 - ay0 : ay2 - ay0;
    }

    @Override
    public double getTanOutX()
    {
        return (ax2 != ax1 || ay2 != ay1) ? ax2 - ax1 : ax2 - ax0;
    }

    @Override
    public double getTanOutY()
    {
        return (ax2 != ax1 || ay2 != ay1) ? ay2 - ay1 : ay2 - ay0;
    }

    @Override
    public double getStartX()
    {
        return ax0;
    }

    @Override
    public double getStartY()
    {
        return ay0;
    }

    @Override
    public double getEndX()
    {
        return ax2;
    }

    @Override
    public double getEndY()
    {
        return ay2;
    }

    @Override
    public BezierQuad[] split(double t)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);

        return new BezierQuad[]{
            new BezierQuad(ax0, ay0, bx0, by0, cx0, cy0),
            new BezierQuad(cx0, cy0, bx1, by1, ax2, ay2)
        };
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);

        if (pos != null)
        {
            pos.set(cx0, cy0);
        }

        if (tan != null)
        {
            tan.set(bx1 - bx0, by1 - by0);
        }
    }

    @Override
    public double getCurvatureSquared()
    {
        return Math2DUtil.distPointLineSquared(ax1, ay1,
                ax0, ay0, ax2 - ax0, ay2 - ay0);
    }

    @Override
    public BezierQuad offset(double width)
    {
        //Find points and tangents offset line will need to match
        //Initial points of offset curve displaced perpendicular
        // to curve
        CyVector2d p0 = new CyVector2d();
        CyVector2d t0 = new CyVector2d();
        evaluate(0, p0, t0);
        t0.normalize();
        t0.scale(width);

        CyVector2d p2 = new CyVector2d();
        CyVector2d t2 = new CyVector2d();
        evaluate(1, p2, t2);
        t2.normalize();
        t2.scale(width);

        CyVector2d pm = new CyVector2d();
        CyVector2d tm = new CyVector2d();
        evaluate(.5, pm, tm);
        tm.normalize();
        tm.scale(width);

        CyVector2d q0 = new CyVector2d(p0.x - t0.y, p0.y + t0.x);
        CyVector2d q2 = new CyVector2d(p2.x - t2.y, p2.y + t2.x);
        CyVector2d qm = new CyVector2d(pm.x - tm.y, pm.y + tm.x);

        //Calculate bezier B(t) where
        //B(0) = q0
        //B(1) = q2
        //B(.5) = qm
        //
        // Eqn of bezier:
        //B(t) = (1 - t)^2 p0 + 2 (1 - t) t p1 + t^2 p2
        //
        //So
        // B(.5) = 1/4 (q0 + 2 * q1 + q2)
        // 4qm = q0 + 2 q1 + q2
        // q1 = 1/2 (4qm - q0 - q2)

        return new BezierQuad(
                q0.x, q0.y,
                (4 * qm.x - q0.x - q2.x) / 2, (4 * qm.y - q0.y - q2.y) / 2,
                q2.x, q2.y);
    }

    @Override
    public void append(PathConsumer out)
    {
        out.quadTo(ax1, ay1, ax2, ay2);
    }


}
