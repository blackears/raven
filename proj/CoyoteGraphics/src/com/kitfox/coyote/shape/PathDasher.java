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

import com.kitfox.coyote.math.Math2DUtil;

/**
 *
 * @author kitfox
 */
public class PathDasher extends PathConsumer
{
    private final PathConsumer out;
    double sx, sy;
    double mx, my;

    private final double[] dashLen;
    private final double[] dashLenSum;
    private final double dashOffset;
    private final double dashTotalSpan;

//    private double initDashOffset;
    private double curDashOffset;
    private boolean hadPenUp;

    private PenState penState;


//    final double[] dashStops;
//    final initU;

    public PathDasher(PathConsumer out, double[] dash, double dashOffset)
    {
        this.out = out;
        this.dashOffset = dashOffset;
        this.dashLen = dash;

        int numDashParts = dash.length & ~1;
        if (numDashParts == 0)
        {
            throw new UnsupportedOperationException("Dash length must be at least 2");
        }

        this.dashLenSum = new double[numDashParts];
        double dashPatLen = 0;
        for (int i = 0; i < numDashParts; ++i)
        {
            dashPatLen += dash[i];
            this.dashLenSum[i] = dashPatLen;
        }
        this.dashTotalSpan = dashPatLen;


        //Determine how much of the pattern has allready been
        // passed over by the time we start drawing the first segment
//        double offsetFrac = dashOffset / dashPatLen;
//        double initOff = 1 - (offsetFrac - Math.floor(offsetFrac));
//        if (initOff >= 1)
//        {
//            initOff = 0;
//        }
//        initDashOffset = dashPatLen * initOff;
    }


    @Override
    public void beginPath()
    {
        out.beginPath();
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
//System.err.println("Begin path");
//        curDashOffset = initDashOffset;
        curDashOffset = dashOffset;

        hadPenUp = false;
        penState = PenState.INIT;

        sx = mx = x0;
        sy = my = y0;

    }

    @Override
    public void lineTo(double x0, double y0)
    {
//        double length = Math2DUtil.dist(mx, my, x0, y0);
//
//        int dashIdx = getDashSegIndex(curDashOffset);
//        dashLine(length, dashIdx, mx, my, x0, y0);
        dashLine(mx, my, x0, y0);

        mx = x0;
        my = y0;
    }

    @Override
    public void quadTo(double x0, double y0, double x1, double y1)
    {
        dashQuad(mx, my, x0, y0, x1, y1);

        mx = x1;
        my = y1;
    }

    @Override
    public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        dashCubic(mx, my, x0, y0, x1, y1, x2, y2);

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

        if (!hadPenUp)
        {
            out.closeSubpath();
        }
    }

    @Override
    public void endPath()
    {
        out.endPath();
    }


    private int getDashSegIndex(double offset)
    {
        for (int i = 0; i < dashLenSum.length; ++i)
        {
            if (offset < dashLenSum[i])
            {
                return i;
            }
        }

        throw new RuntimeException();
    }

    private double distanceSquared(double ax0, double ay0, double ax1, double ay1)
    {
        double dx = ax1 - ax0;
        double dy = ay1 - ay0;
        return dx * dx + dy * dy;
    }

    private double distance(double ax0, double ay0, double ax1, double ay1)
    {
        return Math.sqrt(distanceSquared(ax0, ay0, ax1, ay1));
    }

    private void dashLine(
            double ax0, double ay0, double ax1, double ay1)
    {
        double len = distance(ax0, ay0, ax1, ay1);

        //Convert from uv mesh space to st texture space
        double u0 = curDashOffset / dashTotalSpan;
        double uInt0 = Math.floor(u0);
        double uFrac0 = u0 - uInt0;
        double s0 = (uFrac0) * dashTotalSpan;
        if (s0 >= dashLenSum[dashLenSum.length - 1])
        {
            //If at or slightly past end, rewind to start of array
            s0 = 0;
        }

        int segIdx = getDashSegIndex(s0);
        double dashRemain = dashLenSum[segIdx] - s0;

        while (dashRemain < len)
        {
            //We can complete current dash
            double frac = dashRemain / len;

            double bx0 = ax0 + frac * (ax1 - ax0);
            double by0 = ay0 + frac * (ay1 - ay0);

            emitLine((segIdx & 0x1) == 0, ax0, ay0, bx0, by0);

            curDashOffset += dashRemain;
            len -= dashRemain;
            segIdx = segIdx == dashLenSum.length - 1
                    ? 0 : segIdx + 1;
            dashRemain = dashLen[segIdx];
            ax0 = bx0;
            ay0 = by0;
        }

        //We can only go as far as we have space left in the mesh
        if (len > 0)
        {
            emitLine((segIdx & 0x1) == 0, ax0, ay0, ax1, ay1);
            curDashOffset += len;
        }
    }

    private void dashQuad(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2)
    {
        double len = distance(ax0, ay0, ax1, ay1)
                + distance(ax1, ay1, ax2, ay2);

        //Convert from uv mesh space to st texture space
        double u0 = curDashOffset / dashTotalSpan;
        double uInt0 = Math.floor(u0);
        double uFrac0 = u0 - uInt0;
        double s0 = (uFrac0) * dashTotalSpan;
        if (s0 >= dashLenSum[dashLenSum.length - 1])
        {
            //If at or slightly past end, rewind to start of array
            s0 = 0;
        }

        int segIdx = getDashSegIndex(s0);
        double dashRemain = dashLenSum[segIdx] - s0;

        while (dashRemain < len)
        {
            //We can complete current dash
            double frac = dashRemain / len;

            double bx0 = ax0 + frac * (ax1 - ax0);
            double by0 = ay0 + frac * (ay1 - ay0);
            double bx1 = ax1 + frac * (ax2 - ax1);
            double by1 = ay1 + frac * (ay2 - ay1);
            double cx0 = bx0 + frac * (bx1 - bx0);
            double cy0 = by0 + frac * (by1 - by0);

            emitQuad((segIdx & 1) == 0, ax0, ay0, bx0, by0, cx0, cy0);

            curDashOffset += dashRemain;
            len -= dashRemain;
            segIdx = segIdx == dashLenSum.length - 1
                    ? 0 : segIdx + 1;
            dashRemain = dashLen[segIdx];
            ax0 = cx0;
            ay0 = cy0;
            ax1 = bx1;
            ay1 = by1;
        }

        //We can only go as far as we have space left in the mesh
        if (len > 0)
        {
            emitQuad((segIdx & 1) == 0, ax0, ay0, ax1, ay1, ax2, ay2);
            curDashOffset += len;
        }
    }

    private void dashCubic(
            double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3)
    {
        double len = distance(ax0, ay0, ax1, ay1)
                + distance(ax1, ay1, ax2, ay2)
                + distance(ax2, ay2, ax3, ay3);

        //Convert from uv mesh space to st texture space
        double u0 = curDashOffset / dashTotalSpan;
        double uInt0 = Math.floor(u0);
        double uFrac0 = u0 - uInt0;
        double s0 = (uFrac0) * dashTotalSpan;
        if (s0 >= dashLenSum[dashLenSum.length - 1])
        {
            //If at or slightly past end, rewind to start of array
            s0 = 0;
        }

        int segIdx = getDashSegIndex(s0);
        double dashRemain = dashLenSum[segIdx] - s0;

        while (dashRemain < len)
        {
            //We can complete current dash
            double frac = dashRemain / len;

            double bx0 = ax0 + frac * (ax1 - ax0);
            double by0 = ay0 + frac * (ay1 - ay0);
            double bx1 = ax1 + frac * (ax2 - ax1);
            double by1 = ay1 + frac * (ay2 - ay1);
            double bx2 = ax2 + frac * (ax3 - ax2);
            double by2 = ay2 + frac * (ay3 - ay2);
            double cx0 = bx0 + frac * (bx1 - bx0);
            double cy0 = by0 + frac * (by1 - by0);
            double cx1 = bx1 + frac * (bx2 - bx1);
            double cy1 = by1 + frac * (by2 - by1);
            double dx0 = cx0 + frac * (cx1 - cx0);
            double dy0 = cy0 + frac * (cy1 - cy0);

            emitCubic((segIdx & 1) == 0, ax0, ay0, bx0, by0, cx0, cy0, dx0, dy0);

            curDashOffset += dashRemain;
            len -= dashRemain;
            segIdx = segIdx == dashLenSum.length - 1
                    ? 0 : segIdx + 1;
            dashRemain = dashLen[segIdx];
            ax0 = dx0;
            ay0 = dy0;
            ax1 = cx1;
            ay1 = cy1;
            ax2 = bx2;
            ay2 = by2;
        }

        //We can only go as far as we have space left in the mesh
        if (len > 0)
        {
            emitCubic((segIdx & 1) == 0, ax0, ay0, ax1, ay1, ax2, ay2, ax3, ay3);
            curDashOffset += len;
        }
    }

    private void emitLine(boolean penDown, double ax0, double ay0, double ax1, double ay1)
    {
        if (penDown)
        {
            if (penState != PenState.DOWN)
            {
                out.beginSubpath(ax0, ay0);
            }
            out.lineTo(ax1, ay1);
            penState = PenState.DOWN;
        }
        else
        {
            hadPenUp = true;
            penState = PenState.UP;
        }
    }

    private void emitQuad(boolean penDown, double ax0, double ay0, double ax1, double ay1, double ax2, double ay2)
    {
        if (penDown)
        {
            if (penState != PenState.DOWN)
            {
                out.beginSubpath(ax0, ay0);
            }
            out.quadTo(ax1, ay1, ax2, ay2);
            penState = PenState.DOWN;
        }
        else
        {
            hadPenUp = true;
            penState = PenState.UP;
        }
    }

    private void emitCubic(boolean penDown, double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3)
    {
        if (penDown)
        {
            if (penState != PenState.DOWN)
            {
                out.beginSubpath(ax0, ay0);
            }
            out.cubicTo(ax1, ay1, ax2, ay2, ax3, ay3);
            penState = PenState.DOWN;
        }
        else
        {
            hadPenUp = true;
            penState = PenState.UP;
        }
    }

    //------------------------
    private enum PenState
    {
        INIT, DOWN, UP
    }
}
