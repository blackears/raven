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

import java.util.ArrayList;

/**
 * Represents a clipped section of a larger curve.  tOffset and tSpan represent
 * original t position within original curve.
 *
 * @author kitfox
 */
@Deprecated
public class SplitRecord
{
    final double tOffset;
    final double tSpan;
    final BezierCurve curve;
    final FlatSegmentList segList;
    final double flatnessSquared;

    public SplitRecord(BezierCurve curve, double flatnessSquared)
    {
        this(0, 1, curve, flatnessSquared);
    }

    public SplitRecord(double tOffset, double tSpan, BezierCurve curve, double flatnessSquared)
    {
        this.tOffset = tOffset;
        this.tSpan = tSpan;
        this.curve = curve;
        this.flatnessSquared = flatnessSquared;
        segList = curve.getFlatSegments(flatnessSquared, tOffset, tSpan);
    }

    public SplitRecord[] split(double t, SplitRecord[] result)
    {
        if (result == null)
        {
            result = new SplitRecord[2];
        }

        BezierCurve[] curves = curve.split(t, null);
        result[0] = new SplitRecord(tOffset, tSpan * t, curves[0], flatnessSquared);
        result[1] = new SplitRecord(tOffset + tSpan * t, (1 - t) * tSpan, curves[0], flatnessSquared);
        return result;
    }

    public SplitRecord[] split(double[] t, SplitRecord[] segs)
    {
        if (segs == null)
        {
            segs = new SplitRecord[t.length + 1];
        }

        BezierCurve[] curves = curve.split(t, null);
        for (int i = 0; i < curves.length; ++i)
        {
            double t0 = i == 0 ? 0 : t[i - 1];
            double t1 = i == curves.length - 1 ? 1 : t[i];
            segs[i] = new SplitRecord(tOffset + tSpan * t0, tSpan * (t1 - t0), curves[i], flatnessSquared);
        }
        return segs;
    }

    public ArrayList<SplitRecord> splitAtSelfIntersections(ArrayList<SplitRecord> result)
    {
        if (result == null)
        {
            result = new ArrayList<SplitRecord>();
        }

//        FlatSegment segs = curve.getFlatSegments();

        for (FlatSegment h0 = segList.getHead(); h0.next != null; h0 = h0.next)
        {
            int p0x = h0.x;
            int p0y = h0.y;
            int r0x = h0.next.x - h0.x;
            int r0y = h0.next.y - h0.y;

            //Skip ahead 2
            FlatSegment h1 = h0.next;
            if (h1 != null)
            {
                h1 = h1.next;
            }

            for (; h1 != null && h1.next != null; h1 = h1.next)
            {
                int p1x = h1.x;
                int p1y = h1.y;
                int r1x = h1.next.x - h1.x;
                int r1y = h1.next.y - h1.y;

                double[] time = BezierMath.intersectLines(
                        p0x, p0y, r0x, r0y, p1x, p1y, r1x, r1y, null);
                if (time == null)
                {
                    continue;
                }
                double t0 = time[0];
                double t1 = time[1];
                if (t0 >= 0 && t0 <= 1 && t1 >= 0 && t1 <= 1)
                {
                    //Split curve into 3 parts
                    BezierCurve[] curves = curve.split(time, null);
                    BezierCurve c0 = curves[0];
                    BezierCurve c1 = curves[1];
                    BezierCurve c2 = curves[2];

                    //Make sure cut points line up
                    c1.setStartX(c0.getEndX());
                    c1.setStartY(c0.getEndY());
                    c1.setEndX(c0.getEndX());
                    c1.setEndY(c0.getEndY());
                    c2.setStartX(c0.getEndX());
                    c2.setStartY(c0.getEndY());

                    result.add(new SplitRecord(tOffset, tSpan * time[0], c0, flatnessSquared));
                    new SplitRecord(tOffset + tSpan * time[0], tSpan * (time[1] - time[0]), curve, flatnessSquared)
                        .splitAtSelfIntersections(result);
                    new SplitRecord(tOffset + tSpan * time[1], tSpan * (1 - time[1]), curve, flatnessSquared)
                        .splitAtSelfIntersections(result);

                    return result;
                }
            }
        }

        //No self intersections
        result.add(this);
        return result;
    }

    public SplitCurvesRecord splitCurves(BezierCurve curve)
    {
        SplitCurvesRecord rec = new SplitCurvesRecord();
        rec.otherSplits.add(new SplitRecord(0, 1, curve, flatnessSquared));
        splitCurves(rec);
        return rec;

    }

    public void splitCurves(SplitCurvesRecord rec)
    {
//        FlatSegmentList segs0 = curve.getFlatSegments();

        //Go through all of local segments
        for (FlatSegment h0 = segList.getHead(); h0.next != null; h0 = h0.next)
        {
            int p0x = h0.x;
            int p0y = h0.y;
            int r0x = h0.next.x - h0.x;
            int r0y = h0.next.y - h0.y;

            for (int i = 0; i < rec.otherSplits.size(); ++i)
            {
                SplitRecord splitRec = rec.otherSplits.get(i);
                FlatSegment segs1 = splitRec.segList.getHead();

                //Go through other segments
                for (FlatSegment h1 = segs1; h1.next != null; h1 = h1.next)
                {
                    int p1x = h1.x;
                    int p1y = h1.y;
                    int r1x = h1.next.x - h1.x;
                    int r1y = h1.next.y - h1.y;

                    double[] time = BezierMath.intersectLines(
                            p0x, p0y, r0x, r0y, p1x, p1y, r1x, r1y, null);
                    if (time == null)
                    {
                        continue;
                    }
                    double t0 = time[0];
                    double t1 = time[1];
                    if (t0 >= 0 && t0 <= 1 && t1 >= 0 && t1 <= 1)
                    {
                        //Found intersection

                        //Split curves
                        SplitRecord[] localCurves = split(t0, null);
                        SplitRecord[] otherCurves = splitRec.split(t1, null);

                        SplitRecord cl0 = localCurves[0];
                        SplitRecord cl1 = localCurves[1];
                        SplitRecord co0 = otherCurves[0];
                        SplitRecord co1 = otherCurves[1];

                        //Align curves
                        int ax = cl0.curve.getEndX();
                        int ay = cl0.curve.getEndY();
                        cl1.curve.setStartX(ax);
                        cl1.curve.setStartY(ay);
                        co0.curve.setEndX(ax);
                        co0.curve.setEndY(ay);
                        co1.curve.setStartX(ax);
                        co1.curve.setStartY(ay);

                        rec.localSplits.add(cl0);
                        rec.otherSplits.set(i, co0);
                        rec.otherSplits.add(i + 1, co1);

                        cl1.splitCurves(rec);
                        return;
                    }
                }
            }
        }

        //No intersection.  Just add curve
        rec.localSplits.add(this);
    }

}
