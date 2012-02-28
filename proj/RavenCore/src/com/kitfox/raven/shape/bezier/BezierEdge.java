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

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierEdge extends BezierNetworkComponent
{
    private final double flatnessSquared;
    protected BezierVertex start;
    protected BezierVertex end;

    private BezierCurve curve;
    BezierFace faceLeft;
    BezierFace faceRight;
    FlatSegmentList segList;

//    public static final String KEY_VISIBLE = "visible";

    public BezierEdge(BezierVertex start, BezierVertex end, BezierCurve curve, double flatnessSquared)
    {
        this.start = start;
        this.end = end;
        this.curve = curve;
        this.flatnessSquared = flatnessSquared;
    }

    public BezierFace getFaceLeft()
    {
        return faceLeft;
    }

    public BezierFace getFaceRight()
    {
        return faceRight;
    }

    public FlatSegmentList getSegList()
    {
        if (segList == null)
        {
            segList = curve.getFlatSegments(flatnessSquared);
        }
        return segList;
    }

    public boolean exitsFrom(BezierVertex vtx)
    {
        return start == vtx;
    }

    public boolean entersInto(BezierVertex vtx)
    {
        return end == vtx;
    }

    public void visitEdgesCCW(EdgeVisitor visitor)
    {
        BezierEdge curEdge = this;
        boolean againstWinding = false;
        do
        {
            visitor.visit(curEdge, againstWinding);
            BezierVertex vtx;
            if (againstWinding)
            {
                vtx = curEdge.start;
            }
            else
            {
                vtx = curEdge.end;
            }

            curEdge = vtx.nextEdgeCW(curEdge);
            againstWinding = curEdge.entersInto(vtx);
        } while (this != curEdge || againstWinding);

        visitor.finishedVisiting();

//        BezierEdge curEdge = this;
//        boolean againstWinding = false;
//        do
//        {
//            visitor.visit(curEdge, againstWinding);
//
//            int tanX, tanY;
//            BezierVertex vtx;
//            if (againstWinding)
//            {
//                tanX = curEdge.curve.getStartKnotX() - curEdge.curve.getStartX();
//                tanY = curEdge.curve.getStartKnotY() - curEdge.curve.getStartY();
//                vtx = curEdge.start;
//            }
//            else
//            {
//                tanX = curEdge.curve.getEndKnotX() - curEdge.curve.getEndX();
//                tanY = curEdge.curve.getEndKnotY() - curEdge.curve.getEndY();
//                vtx = curEdge.end;
//            }
//
//            curEdge = vtx.nextEdgeCW(tanX, tanY);
//            againstWinding = curEdge.entersInto(vtx);
//        } while (this != curEdge);
//
//        visitor.finishedVisiting();
    }

    public void visitEdgesCW(EdgeVisitor visitor)
    {
        BezierEdge curEdge = this;
        boolean againstWinding = false;
        do
        {
            visitor.visit(curEdge, againstWinding);
            BezierVertex vtx;
            if (againstWinding)
            {
                vtx = curEdge.start;
            }
            else
            {
                vtx = curEdge.end;
            }

            curEdge = vtx.nextEdgeCCW(curEdge);
            againstWinding = curEdge.entersInto(vtx);
        } while (this != curEdge || againstWinding);

        visitor.finishedVisiting();


//            int tanX, tanY;
//            BezierVertex vtx;
//            if (againstWinding)
//            {
//                tanX = curEdge.curve.getStartKnotX() - curEdge.curve.getStartX();
//                tanY = curEdge.curve.getStartKnotY() - curEdge.curve.getStartY();
//                vtx = curEdge.start;
//            }
//            else
//            {
//                tanX = curEdge.curve.getEndKnotX() - curEdge.curve.getEndX();
//                tanY = curEdge.curve.getEndKnotY() - curEdge.curve.getEndY();
//                vtx = curEdge.end;
//            }
//
//            curEdge = vtx.nextEdgeCCW(tanX, tanY);
//            againstWinding = curEdge.entersInto(vtx);
    }

    /**
     * @return the curve
     */
    public BezierCurve getCurve()
    {
        return curve;
    }

    /**
     * @param curve the curve to set
     */
    public void setCurve(BezierCurve curve)
    {
        this.curve = curve;
        segList = null;
    }

    /**
     * @return the flatnessSquared
     */
    public double getFlatnessSquared()
    {
        return flatnessSquared;
    }

    /**
     * @return the start
     */
    public BezierVertex getStart()
    {
        return start;
    }

    /**
     * @return the end
     */
    public BezierVertex getEnd()
    {
        return end;
    }

    @Override
    public Rectangle getBounds()
    {
        return curve.getBounds();
    }

    @Override
    public String toString()
    {
        return curve.toString();
    }

    /**
     * True if this edge is part of a peninsula.  A peninsula is a chain of
     * line segments snaking off of a face that is not part of a face boundary.
     *
     * @return
     */
    public boolean isPeninsula()
    {
        class DoubleBack implements EdgeVisitor
        {
            boolean peninsula;

            @Override
            public void visit(BezierEdge edge, boolean againstWinding)
            {
                if (edge == BezierEdge.this && againstWinding)
                {
                    peninsula = true;
                }
            }

            @Override
            public void finishedVisiting()
            {
            }
        }

        DoubleBack db = new DoubleBack();
        visitEdgesCCW(db);
        return db.peninsula;
    }

    public ArrayList<BezierVertex> getVerticesCW()
    {
        VertexCollector collect = new VertexCollector();
        visitEdgesCW(collect);
        return collect.list;
    }

    public ArrayList<BezierVertex> getVerticesCCW()
    {
        VertexCollector collect = new VertexCollector();
        visitEdgesCCW(collect);
        return collect.list;
    }

    void pointInsideCheck(BezierInsideOutsideCheck check)
    {
        getSegList().pointInsideCheck(check);
    }

    //----------------------------
    class VertexCollector implements EdgeVisitor
    {
        ArrayList<BezierVertex> list = new ArrayList<BezierVertex>();

        @Override
        public void visit(BezierEdge edge, boolean againstWinding)
        {
            if (againstWinding)
            {
                list.add(edge.end);
            }
            else
            {
                list.add(edge.start);
            }
        }

        @Override
        public void finishedVisiting()
        {
        }
    }

    public interface EdgeVisitor
    {
        public void visit(BezierEdge edge, boolean againstWinding);

        public void finishedVisiting();
    }

}
