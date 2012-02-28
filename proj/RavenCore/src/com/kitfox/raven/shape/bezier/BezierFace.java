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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierFace extends BezierNetworkComponent
{
    //Edges connected to this face on the left side
    final ArrayList<BezierEdge> edgesLeft = new ArrayList<BezierEdge>();
    //Edges connected to this face on the right side
    final ArrayList<BezierEdge> edgesRight = new ArrayList<BezierEdge>();

    public BezierFace()
    {
    }

    protected void addEdgeLeft(BezierEdge edge)
    {
        edgesLeft.add(edge);
    }

    protected void removeEdgeLeft(BezierEdge edge)
    {
        edgesLeft.remove(edge);
    }

    protected void addEdgeRight(BezierEdge edge)
    {
        edgesRight.add(edge);

    }

    protected void removeEdgeRight(BezierEdge edge)
    {
        edgesRight.remove(edge);
    }

//    public BezierEdge getFirstEdge()
//    {
//        if (!edgesLeft.isEmpty())
//        {
//            return edgesLeft.get(0);
//        }
//
//        if (!edgesRight.isEmpty())
//        {
//            return edgesRight.get(0);
//        }
//
//        //There should be at least one edge
//        throw new IllegalStateException();
//    }
//
//    public void visitEdgesCCW(EdgeVisitor visitor)
//    {
//        BezierEdge initEdge = getFirstEdge();
//        BezierEdge curEdge = initEdge;
//        do
//        {
//            assert (curEdge.faceLeft == this || curEdge.faceRight == this)
//                    : "Either left or right edge must be part of this face";
//
//            boolean againstWinding = curEdge.faceRight == this;
//            visitor.visit(curEdge, againstWinding);
//
//            int tanX, tanY;
//            BezierVertex vtx;
//            if (againstWinding)
//            {
//                tanX = curEdge.getCurve().getStartKnotX() - curEdge.getCurve().getStartX();
//                tanY = curEdge.getCurve().getStartKnotY() - curEdge.getCurve().getStartY();
//                vtx = curEdge.getStart();
//            }
//            else
//            {
//                tanX = curEdge.getCurve().getEndKnotX() - curEdge.getCurve().getEndX();
//                tanY = curEdge.getCurve().getEndKnotY() - curEdge.getCurve().getEndY();
//                vtx = curEdge.getEnd();
//            }
//
//            curEdge = vtx.nextEdgeCW(tanX, tanY);
//        } while (initEdge != curEdge);
//
//        visitor.finishedVisiting();
//    }
//
//
//    public void visitEdgesCW(EdgeVisitor visitor)
//    {
//        BezierEdge initEdge = getFirstEdge();
//        BezierEdge curEdge = initEdge;
//        do
//        {
//            assert (curEdge.faceLeft == this || curEdge.faceRight == this)
//                    : "Either left or right edge must be part of this face";
//
//            boolean againstWinding = curEdge.faceLeft == this;
//            visitor.visit(curEdge, againstWinding);
//
//            int tanX, tanY;
//            BezierVertex vtx;
//            if (againstWinding)
//            {
//                tanX = curEdge.getCurve().getStartKnotX() - curEdge.getCurve().getStartX();
//                tanY = curEdge.getCurve().getStartKnotY() - curEdge.getCurve().getStartY();
//                vtx = curEdge.getStart();
//            }
//            else
//            {
//                tanX = curEdge.getCurve().getEndKnotX() - curEdge.getCurve().getEndX();
//                tanY = curEdge.getCurve().getEndKnotY() - curEdge.getCurve().getEndY();
//                vtx = curEdge.getEnd();
//            }
//
//            curEdge = vtx.nextEdgeCCW(tanX, tanY);
//        } while (initEdge != curEdge);
//
//        visitor.finishedVisiting();
//    }

    @Override
    public Rectangle getBounds()
    {
        Rectangle rect = null;
        for (BezierEdge edge: edgesLeft)
        {
            if (rect == null)
            {
                rect = edge.getBounds();
            }
            else
            {
                rect.union(edge.getBounds());
            }
        }

        for (BezierEdge edge: edgesRight)
        {
            if (rect == null)
            {
                rect = edge.getBounds();
            }
            else
            {
                rect.union(edge.getBounds());
            }
        }

        return rect;
    }

//    public Path2D.Double getContourCCW()
//    {
//        class Builder implements EdgeVisitor
//        {
//            Path2D.Double path;
//
//            @Override
//            public void visit(BezierEdge edge, boolean againstWinding)
//            {
//                BezierCurve curve = edge.getCurve();
//                if (againstWinding)
//                {
//                    curve = curve.reverse();
//                }
//
//                if (path == null)
//                {
//                    path = new Path2D.Double();
//                    path.moveTo(curve.getStartX(), curve.getStartY());
//                }
//
//                curve.appendToPath(path);
//            }
//
//            @Override
//            public void finishedVisiting()
//            {
//                path.closePath();
//            }
//        }
//
//        Builder builder = new Builder();
//        visitEdgesCCW(builder);
//        return builder.path;
//    }

    public BezierContourSet getContours()
    {
        ArrayList<BezierEdgeAttach> edgeList = new ArrayList<BezierEdgeAttach>();

        for (BezierEdge edge: edgesLeft)
        {
            edgeList.add(new BezierEdgeAttach(edge, false));
        }
        for (BezierEdge edge: edgesRight)
        {
            edgeList.add(new BezierEdgeAttach(edge, true));
        }

System.err.println("+++++ Building contours");

        //ContourSet contourSet = new ContourSet();
        ArrayList<BezierContour> contourList = new ArrayList<BezierContour>();
        while (!edgeList.isEmpty())
        {
for (int i = 0; i < edgeList.size(); ++i)
{
System.err.println("edgeList: " + edgeList.get(i));
}

            //Start new contour.  Start by popping last edge off list.
            ArrayList<BezierEdgeAttach> contour = new ArrayList<BezierEdgeAttach>();
            BezierEdgeAttach initAtt = edgeList.remove(edgeList.size() - 1);
            contour.add(initAtt);

System.err.println("----init: " + initAtt);

            while (contour != null)
            {
                //Build contour CCW
                BezierEdgeAttach attStart = contour.get(0);
                BezierEdgeAttach attEnd = contour.get(contour.size() - 1);
                BezierVertex ctrStart = attStart.rightJoin
                        ? attStart.edge.end : attStart.edge.start;
                BezierVertex ctrEnd = attEnd.rightJoin
                        ? attEnd.edge.start : attEnd.edge.end;

System.err.println("--Find next seg: head(" + ctrStart + ") tail(" + ctrEnd + ")");
                boolean extendedCtr = false;
                for (Iterator<BezierEdgeAttach> it = edgeList.iterator(); it.hasNext();)
                {
                    BezierEdgeAttach curEdgeAtt = it.next();
                    BezierVertex curStart = curEdgeAtt.rightJoin
                            ? curEdgeAtt.edge.end : curEdgeAtt.edge.start;
                    BezierVertex curEnd = curEdgeAtt.rightJoin
                            ? curEdgeAtt.edge.start : curEdgeAtt.edge.end;

                    boolean canAddToTail = curStart == ctrEnd;
                    boolean canAddToHead = curEnd == ctrStart;

                    if (canAddToTail && canAddToHead)
                    {
                        //We've completed a loop
                        contour.add(curEdgeAtt);
                        contourList.add(new BezierContour(contour));
                        
                        //Clear contour list to indicate we've completed a loop
                        contour = null;
                        it.remove();
                        extendedCtr = true;
System.err.println("!!Finished ctr: " + curEdgeAtt);
                        break;
                    }
                    else if (canAddToTail)
                    {
                        //We've completed a loop
                        contour.add(curEdgeAtt);
                        it.remove();
                        extendedCtr = true;
System.err.println("Add tail: " + curEdgeAtt);
                        break;
                    }
                    else if (canAddToHead)
                    {
                        //We've completed a loop
                        contour.add(0, curEdgeAtt);
                        it.remove();
                        extendedCtr = true;
System.err.println("Add head: " + curEdgeAtt);
                        break;
                    }
                    else
                    {
//System.err.println("Skipping: " + curEdgeAtt);
                    }
                }

                if (!extendedCtr)
                {
                    throw new IllegalStateException(
                            "Something went wrong - we should have added at least one segment");
                }
            }
        } 

        return new BezierContourSet(contourList);
    }

    ArrayList<BezierEdgeAttach> getEdgeAttachments()
    {
        ArrayList<BezierEdgeAttach> list = new ArrayList<BezierEdgeAttach>();
        for (BezierEdge edge: edgesLeft)
        {
            list.add(new BezierEdgeAttach(edge, false));
        }
        for (BezierEdge edge: edgesRight)
        {
            list.add(new BezierEdgeAttach(edge, true));
        }
        return list;
    }

    public String toSVG(String indent)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Random rand = new Random();
        
        pw.println(indent + "<g>");
        for (BezierEdge edge: edgesLeft)
        {
            pw.println(
                    String.format(indent + "    <path style=\"stroke:#%06x;fill:none\" d=\"%s\"/>",
                    rand.nextInt() & 0xffffff,
                    edge.getCurve().toSVGPath()));
        }
        pw.println(indent + "</g>");

        pw.println(indent + "<g>");
        for (BezierEdge edge: edgesRight)
        {
            pw.println(
                    String.format(indent + "    <path style=\"stroke:#%06x;fill:none\" d=\"%s\"/>",
                    rand.nextInt() & 0xffffff,
                    edge.getCurve().toSVGPath()));
        }
        pw.println(indent + "</g>");
        

        pw.close();
        return sw.toString();
    }

    //-----------------------

}
