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

import com.kitfox.raven.shape.bezier.BezierEdge.EdgeVisitor;
import com.kitfox.raven.shape.bezier.BezierNetworkManipulator.EdgeProxy;
import java.awt.geom.Path2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierMesh extends BezierNetwork
{
    HashMap<BezierPoint, BezierVertex> vertexMap
            = new HashMap<BezierPoint, BezierVertex>();
    int nextFaceId;

    public BezierMesh(double flatnessSquared)
    {
        super(flatnessSquared);
    }

    public BezierVertex getVertex(int x, int y)
    {
        BezierPoint pt = new BezierPoint(x, y);
        return vertexMap.get(pt);
    }

    protected BezierVertex getOrCreateVertex(int x, int y)
    {
        BezierPoint pt = new BezierPoint(x, y);
        BezierVertex v = vertexMap.get(pt);
        if (v == null)
        {
            v = new BezierVertex(pt);
            vertexMap.put(pt, v);
        }
        return v;
    }

    @Override
    public int getNumVertices()
    {
        return vertexMap.size();
    }

    @Override
    public ArrayList<BezierVertex> getVertices()
    {
        return new ArrayList<BezierVertex>(vertexMap.values());
    }

    /**
     *
     * @param edge
     * @param t
     * @return Vertex that was inserted.  Null if no split was made.
     */
    @Override
    public BezierVertex splitEdge(BezierEdge edge, double t)
    {
        if (t <= 0 || t >= 1)
        {
            return null;
        }

        BezierCurve[] curves = edge.getCurve().split(t, null);

        if (curves[0].getEndX() == edge.getStart().getPoint().getX() &&
                curves[0].getEndY() == edge.getStart().getPoint().getY())
        {
            //Split occurs at start point
            return null;
        }
        if (curves[0].getEndX() == edge.getEnd().getPoint().getX() &&
                curves[0].getEndY() == edge.getEnd().getPoint().getY())
        {
            //Split occurs at end point
            return null;
        }

        //Get point of split
        int px = curves[0].getEndX();
        int py = curves[0].getEndY();

        BezierVertex vtx = getOrCreateVertex(px, py);

        //Remove current edge
        edge.getStart().edgeOut.remove(edge);
        edge.getEnd().edgeIn.remove(edge);

        //Create new edges
        edge.faceLeft.removeEdgeLeft(edge);
        edge.faceRight.removeEdgeRight(edge);

        BezierEdge edge0 = new BezierEdge(edge.getStart(), vtx, curves[0], flatnessSquared);
        edge.getStart().edgeOut.add(edge0);
        vtx.edgeIn.add(edge0);
        edge0.setData(edge.getData());
        edge0.faceLeft = edge.faceLeft;
        edge0.faceRight = edge.faceRight;
        edge.faceLeft.addEdgeLeft(edge0);
        edge.faceRight.addEdgeRight(edge0);

        BezierEdge edge1 = new BezierEdge(vtx, edge.getEnd(), curves[1], flatnessSquared);
        vtx.edgeOut.add(edge1);
        edge.getEnd().edgeIn.add(edge1);
        edge1.setData(edge.getData());
        edge1.faceLeft = edge.faceLeft;
        edge1.faceRight = edge.faceRight;
        edge.faceLeft.addEdgeLeft(edge1);
        edge.faceRight.addEdgeRight(edge1);

        return vtx;
    }

    /**
     * Merge new curve into grid.  This may cut the curve and existing
     * edges in the mesh.  Curve may self intersect
     * 
     * @param curve
     */
    public void addCurve(BezierCurve curve, NetworkUpdateCallback callback)
    {
        ArrayList<SplitRecord> curves = curve.splitAtSelfIntersections(null, flatnessSquared);

System.err.println("Adding curve " + curve + "(split into " + curves.size() + " pieces)");
        for (SplitRecord c: curves)
        {
            addCurveNoSelfIsect(c, callback);
        }
    }

    private void addCurveNoSelfIsect(SplitRecord splitRec,
            NetworkUpdateCallback callback)
    {
        //Find edges we potentially intersect
        ArrayList<BezierEdge> bboxOverlapEdges = new ArrayList<BezierEdge>();

        for (BezierVertex vtx: vertexMap.values())
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                if (!edge.getCurve().boundingBoxOverlap(splitRec.curve))
                {
                    continue;
                }
                bboxOverlapEdges.add(edge);
            }
        }

        //Prepare list of curves to add
        ArrayList<SplitRecord> addCurves = new ArrayList<SplitRecord>();
        addCurves.add(splitRec);


        for (Iterator<BezierEdge> meshEdgeIt = bboxOverlapEdges.iterator();
            meshEdgeIt.hasNext();)
        {
            BezierEdge meshEdge = meshEdgeIt.next();

            ArrayList<SplitRecord> newCurves = new ArrayList<SplitRecord>();

            for (Iterator<SplitRecord> insertCurveIt = addCurves.iterator();
                insertCurveIt.hasNext();)
            {
                SplitRecord insertCurve = insertCurveIt.next();

                SplitCurvesRecord rec = insertCurve.splitCurves(meshEdge.getCurve());
                if (rec.getNumOtherSplits() != 0)
                {
                    //Remove old edge
                    meshEdge.getStart().edgeOut.remove(meshEdge);
                    meshEdge.getEnd().edgeIn.remove(meshEdge);
                    meshEdge.faceLeft.removeEdgeLeft(meshEdge);
                    meshEdge.faceRight.removeEdgeRight(meshEdge);

//                    callback.startReplaceExistingEdge(edge);
//                    meshEdgeIt.remove();

                    //Replace with new edges
                    for (int k = 0; k < rec.getNumOtherSplits(); ++k)
                    {
                        SplitRecord otherRec = rec.getOtherSplit(k);
                        BezierCurve c = otherRec.curve;

                        //Attach new edge segment to verts
                        BezierVertex v0 = getOrCreateVertex(c.getStartX(), c.getStartY());
                        BezierVertex v1 = getOrCreateVertex(c.getEndX(), c.getEndY());
                        BezierEdge newEdge = new BezierEdge(v0, v1, c, flatnessSquared);
                        v0.edgeOut.add(newEdge);
                        v1.edgeIn.add(newEdge);

                        //Attach to faces used by removed edge
                        newEdge.faceLeft = meshEdge.faceLeft;
                        newEdge.faceRight = meshEdge.faceRight;
                        newEdge.faceLeft.addEdgeLeft(meshEdge);
                        newEdge.faceRight.addEdgeRight(meshEdge);
                    }

                    //Replace original edge with split segments
                    insertCurveIt.remove();

                    for (int k = 0; k < rec.getNumLocalSplits(); ++k)
                    {
                        newCurves.add(rec.getLocalSplit(k));
                    }
                }

            }
            
            //If we created any new curves during the split, add them to the
            // list
            addCurves.addAll(newCurves);
        }


        //Graph should be ready.  addCurves holds the input curve, all
        // chopped up and aligned to the vertex positions in the mesh.
        // We can run through and insert each piece into the mesh
        for (int i = 0; i < addCurves.size(); ++i)
        {
            SplitRecord addSplit = addCurves.get(i);
            BezierCurve addCurve = addSplit.curve;
            BezierVertex v0 = getOrCreateVertex(addCurve.getStartX(), addCurve.getStartY());
            BezierVertex v1 = getOrCreateVertex(addCurve.getEndX(), addCurve.getEndY());

            boolean v0Empty = v0.isEmpty();
            boolean v1Empty = v1.isEmpty();
System.err.println("--vtx head empty: " + v0Empty + "\t  vtx end empty: " + v1Empty);

            if (v0Empty && v1Empty)
            {
                //In the middle of a face
                BezierFace face = getFace(v0.getPoint().getX(), v0.getPoint().getY());

                BezierEdge edge = new BezierEdge(v0, v1, addCurve, flatnessSquared);
                v0.edgeOut.add(edge);
                v1.edgeIn.add(edge);
                edge.faceLeft = face;
                edge.faceRight = face;
                face.addEdgeLeft(edge);
                face.addEdgeRight(edge);

                callback.addedEdge(addSplit.tOffset, addSplit.tSpan, edge);
                continue;
            }

            BezierEdge edge = new BezierEdge(v0, v1, addCurve, flatnessSquared);
            v0.edgeOut.add(edge);
            v1.edgeIn.add(edge);

            BezierFace face = null;
            if (!v0Empty)
            {
                BezierEdge nextEdge = v0.nextEdgeCCW(edge);
                if (v0.edgeOut.contains(nextEdge))
                {
                    face = nextEdge.faceRight;
                }
                else
                {
                    face = nextEdge.faceLeft;
                }
            }
            else
            {
                BezierEdge nextEdge = v1.nextEdgeCCW(edge);
                if (v1.edgeOut.contains(nextEdge))
                {
                    face = nextEdge.faceRight;
                }
                else
                {
                    face = nextEdge.faceLeft;
                }
            }

            //Set both sides of the face to match the next edge's face
            edge.faceLeft = face;
            edge.faceRight = face;
            face.addEdgeLeft(edge);
            face.addEdgeRight(edge);

            if (!v0Empty && !v1Empty)
            {
                //We may have partitioned a new face
                if (!edge.isPeninsula())
                {
                    BezierFace newFace = new BezierFace();
                    newFace.setUid(++nextFaceId);
                    newFace.setData(face.getData());

                    BezierContour ctrCW = getContourCW(edge);
                    BezierContour ctrCCW = getContourCCW(edge);
                    ArrayList<BezierEdgeAttach> ctrIslands = face.getEdgeAttachments();
                    ctrIslands.removeAll(ctrCW.edges);
                    ctrIslands.removeAll(ctrCCW.edges);

                    //If 
                    boolean replaceCCW = ctrCCW.isCCW();

//                    Path2D.Double path;
//                    if (replaceCCW)
//                    {
//                        ctrCCW.attachToFace(newFace);
//                        path = ctrCCW.createPath();
//                    }
//                    else
//                    {
//                        ctrCW.attachToFace(newFace);
//                        path = ctrCW.createPath();
//                    }

                    BezierContour ctrReplace = replaceCCW ? ctrCCW : ctrCW;
                    ctrReplace.attachToFace(newFace);

                    //Also update any edges that are entirely within face
                    for (BezierEdgeAttach island: ctrIslands)
                    {
                        BezierVertex vtx = island.edge.start;
//                        if (path.contains(vtx.point.getX(), vtx.point.getY()))
                        if (ctrReplace.contains(vtx.point.getX(), vtx.point.getY()))
                        {
//path.contains(vtx.point.getX(), vtx.point.getY());
                            island.attachToFace(newFace);
                        }
                    }

                }
            }

            callback.addedEdge(addSplit.tOffset, addSplit.tSpan, edge);

        }
    }

    public BezierContour getContourCCW(BezierEdge edge)
    {
        final ArrayList<BezierEdgeAttach> edgeList = new ArrayList<BezierEdgeAttach>();

        EdgeVisitor visitor = new EdgeVisitor()
        {
            @Override
            public void visit(BezierEdge edge, boolean againstWinding)
            {
                if (againstWinding)
                {
                    edgeList.add(new BezierEdgeAttach(edge, true));
                }
                else
                {
                    edgeList.add(new BezierEdgeAttach(edge, false));
                }
            }

            @Override
            public void finishedVisiting()
            {
            }
        };

        edge.visitEdgesCCW(visitor);
        return new BezierContour(edgeList);
    }

    public BezierContour getContourCW(BezierEdge edge)
    {
        final ArrayList<BezierEdgeAttach> edgeList = new ArrayList<BezierEdgeAttach>();

        EdgeVisitor visitor = new EdgeVisitor()
        {
            @Override
            public void visit(BezierEdge edge, boolean againstWinding)
            {
                if (againstWinding)
                {
                    edgeList.add(new BezierEdgeAttach(edge, false));
                }
                else
                {
                    edgeList.add(new BezierEdgeAttach(edge, true));
                }
            }

            @Override
            public void finishedVisiting()
            {
            }
        };

        edge.visitEdgesCW(visitor);
        return new BezierContour(edgeList);
    }

//    protected void setEdgeFacesCCW(BezierEdge edge, final BezierFace face)
//    {
//        EdgeVisitor visitor = new EdgeVisitor()
//        {
//            @Override
//            public void visit(BezierEdge edge, boolean againstWinding)
//            {
//                if (againstWinding)
//                {
//                    edge.faceRight.removeEdgeRight(edge);
//                    edge.faceRight = face;
//                    face.addEdgeRight(edge);
//                }
//                else
//                {
//                    edge.faceLeft.removeEdgeLeft(edge);
//                    edge.faceLeft = face;
//                    face.addEdgeLeft(edge);
//                }
//            }
//
//            @Override
//            public void finishedVisiting()
//            {
//            }
//        };
//
//        edge.visitEdgesCCW(visitor);
//    }
//
//    protected void setEdgeFacesCW(BezierEdge edge, final BezierFace face)
//    {
//        EdgeVisitor visitor = new EdgeVisitor()
//        {
//            @Override
//            public void visit(BezierEdge edge, boolean againstWinding)
//            {
//                if (againstWinding)
//                {
//                    edge.faceLeft.removeEdgeLeft(edge);
//                    edge.faceLeft = face;
//                    face.addEdgeLeft(edge);
//                }
//                else
//                {
//                    edge.faceRight.removeEdgeRight(edge);
//                    edge.faceRight = face;
//                    face.addEdgeRight(edge);
//                }
//            }
//
//            @Override
//            public void finishedVisiting()
//            {
//            }
//        };
//
//        edge.visitEdgesCW(visitor);
//    }

    public void removeEdge(BezierEdge edge)
    {
        //Merge faces if border between faces is broken
        if (edge.faceLeft != edge.faceRight)
        {
            //Make sure not to remove exterior face
            BezierFace keepFace, removeFace;
            if (isLeftExterior(edge))
            {
                keepFace = edge.faceLeft;
                removeFace = edge.faceRight;
            }
            else
            {
                keepFace = edge.faceRight;
                removeFace = edge.faceLeft;
            }

            ArrayList<BezierEdgeAttach> list = removeFace.getEdgeAttachments();
            for (BezierEdgeAttach edgeAtt: list)
            {
                edgeAtt.attachToFace(keepFace);
            }
//            setEdgeFacesCW(edge, edge.faceLeft);
        }

        //Remove edges
        edge.getStart().edgeOut.remove(edge);
        edge.getEnd().edgeIn.remove(edge);

        if (edge.getStart().isEmpty())
        {
            vertexMap.remove(edge.getStart().getPoint());
        }
        if (edge.getEnd().isEmpty())
        {
            vertexMap.remove(edge.getEnd().getPoint());
        }
    }

    public void removeVertex(int px, int py)
    {
        BezierPoint pt = new BezierPoint(px, py);
        BezierVertex vtx = vertexMap.get(pt);
        if (vtx == null)
        {
            return;
        }
        removeVertex(vtx);
    }

    @Override
    public void removeVertex(BezierVertex vtx)
    {
        if (vtx.edgeIn.size() == 1 && vtx.edgeOut.size() == 1)
        {
            //Special case of vertex with one input and one output
            // Merge adjacent edges
            if (vtx.edgeIn.get(0) == vtx.edgeOut.get(0))
            {
                //Single loop with one vertex
                vertexMap.remove(vtx.getPoint());
                return;
            }

            //Line segment.  Replace with new edge
            BezierEdge edge0 = vtx.edgeIn.get(0);
            BezierEdge edge1 = vtx.edgeOut.get(0);

            edge0.getStart().edgeOut.remove(edge0);
            edge1.getEnd().edgeIn.remove(edge0);
            vertexMap.remove(vtx.getPoint());

            BezierCurve curveNew;
            if (edge0.getCurve() instanceof BezierCurveLine
                    && edge1.getCurve() instanceof BezierCurveLine)
            {
                curveNew = new BezierCurveLine(
                        edge0.getCurve().getStartX(), edge0.getCurve().getStartY(),
                        edge1.getCurve().getEndX(), edge1.getCurve().getEndY()
                        );
            }
            else
            {
                curveNew = new BezierCurveCubic(
                        edge0.getCurve().getStartX(), edge0.getCurve().getStartY(),
                        edge0.getCurve().getStartKnotX(), edge0.getCurve().getStartKnotY(),
                        edge1.getCurve().getEndKnotX(), edge1.getCurve().getEndKnotY(),
                        edge1.getCurve().getEndX(), edge1.getCurve().getEndY()
                        );
            }

            //addCurve(curveNew, edge0.stroke);
            BezierEdge newEdge = new BezierEdge(edge0.getStart(), edge1.getEnd(), curveNew, flatnessSquared);
            edge0.getStart().edgeOut.add(newEdge);
            edge1.getEnd().edgeIn.add(newEdge);
            return;
        }

        //Delete all edges in this curve.  Should implicitly delete
        // vertex too.
        ArrayList<BezierEdge> edges = vtx.getAllEdges();
        for (int i = 0; i < edges.size(); ++i)
        {
            removeEdge(edges.get(i));
        }
    }

    @Override
    protected void applyManip(BezierNetworkManipulator manip)
    {
//        HashMap<BezierEdge, BezierFace> leftFaces =
//                new HashMap<BezierEdge, BezierFace>();
//        HashMap<BezierEdge, BezierFace> rightFaces =
//                new HashMap<BezierEdge, BezierFace>();
//        ArrayList<BezierEdge> edgeList = new ArrayList<BezierEdge>();
//
//        for (BezierVertex vtx: vertexMap.values())
//        {
//            for (BezierEdge edge: vtx.edgeOut)
//            {
//                edgeList.add(edge);
//                leftFaces.put(edge, edge.faceLeft);
//                rightFaces.put(edge, edge.faceRight);
//            }
//        }

        vertexMap.clear();

        //Rebuild mesh
        class ApplyManipCallback implements NetworkUpdateCallback
        {
            BezierEdge oldEdge;
            HashMap<BezierEdge, BezierEdge> newToOldEdges = new HashMap<BezierEdge, BezierEdge>();

            @Override
            public void addedEdge(double tOffset, double tSpan, BezierEdge edge)
            {
                edge.setData(oldEdge.getData());
                edge.start.setData(oldEdge.start.getData());
                edge.end.setData(oldEdge.end.getData());

                newToOldEdges.put(edge, oldEdge);
            }
        }

        ApplyManipCallback callback = new ApplyManipCallback();
        
        for (EdgeProxy px: manip.edges.values())
        {
            callback.oldEdge = px.edge;
            addCurve(px.newCurve, callback);
        }

        //Copy face data
        for (BezierEdge edge: callback.newToOldEdges.keySet())
        {
            BezierEdge oldEdge = callback.newToOldEdges.get(edge);
            edge.faceLeft.setData(oldEdge.faceLeft.getData());
            edge.faceRight.setData(oldEdge.faceRight.getData());
        }
    }

    public ArrayList<ArrayList<BezierEdge>> getStripifiedEdges()
    {
        class CtrEdgeBuilder
        {
            ArrayList<BezierEdge> list = new ArrayList<BezierEdge>();
            public BezierEdge getFirstEdge()
            {
                return list.get(0);
            }
            public BezierEdge getLastEdge()
            {
                return list.get(list.size() - 1);
            }
        }

        HashMap<BezierEdge, CtrEdgeBuilder> edgeMap = new HashMap<BezierEdge, CtrEdgeBuilder>();

        for (BezierVertex vtx: vertexMap.values())
        {
            if (vtx.edgeIn.size() != 1 || vtx.edgeOut.size() != 1)
            {
                continue;
            }

            BezierEdge edgePrev = vtx.edgeIn.get(0);
            BezierEdge edgeNext = vtx.edgeOut.get(0);

            //Adjacent edges must have same data content
            if (!edgePrev.getData().equals(edgeNext.getData()))
            {
                continue;
            }

            CtrEdgeBuilder ctrPrev = edgeMap.get(edgePrev);
            CtrEdgeBuilder ctrNext = edgeMap.get(edgeNext);

            if (ctrPrev == null && ctrNext == null)
            {
                ctrPrev = new CtrEdgeBuilder();
                ctrPrev.list.add(edgePrev);
                ctrPrev.list.add(edgeNext);
                edgeMap.put(edgePrev, ctrPrev);
                edgeMap.put(edgeNext, ctrPrev);
            }
            else if (ctrPrev == null)
            {
                ctrNext.list.add(0, edgePrev);
                edgeMap.put(edgePrev, ctrNext);
            }
            else if (ctrNext == null)
            {
                ctrPrev.list.add(edgeNext);
                edgeMap.put(edgeNext, ctrPrev);
            }
            else
            {
                if (ctrPrev != ctrNext)
                {
                    //Merge prev, next list
                    ctrPrev.list.addAll(ctrNext.list);
                    //Replace next entries with prev ones
                    for (BezierEdge nextEdge: ctrNext.list)
                    {
                        edgeMap.put(nextEdge, ctrPrev);
                    }
                }
            }
        }

        ArrayList<ArrayList<BezierEdge>> sortedEdges = new ArrayList<ArrayList<BezierEdge>>();

        //Add one segment edges
        for (BezierVertex vtx: vertexMap.values())
        {
            for (BezierEdge edge: vtx.edgeOut)
            {
                if (!edgeMap.containsKey(edge))
                {
                    ArrayList<BezierEdge> list = new ArrayList<BezierEdge>();
                    list.add(edge);
                    sortedEdges.add(list);
                }
            }
        }

        for (CtrEdgeBuilder builder: edgeMap.values())
        {
            if (!sortedEdges.contains(builder.list))
            {
                sortedEdges.add(builder.list);
            }
        }

        class Compare implements Comparator<ArrayList<BezierEdge>>
        {
            @Override
            public int compare(ArrayList<BezierEdge> o1, ArrayList<BezierEdge> o2)
            {
                return o1.get(0).getUid() - o2.get(0).getUid();
            }
        }
        Collections.sort(sortedEdges, new Compare());

        return sortedEdges;
    }

    private boolean isLeftExterior(BezierEdge edge)
    {
        //If we're walking CW, sign of area should be positive for all
        // interior faces.  If negative, this must be the exterior face
        ArrayList<BezierVertex> verts = edge.getVerticesCW();

        BezierVertex v0 = verts.get(0);
        long area = 0;
        for (int i = 1; i < verts.size() - 1; ++i)
        {
            BezierVertex v1 = verts.get(i);
            BezierVertex v2 = verts.get(i + 1);

            long dx0 = v1.point.getX() - v0.point.getX();
            long dy0 = v1.point.getY() - v0.point.getY();
            long dx1 = v2.point.getX() - v0.point.getX();
            long dy1 = v2.point.getY() - v0.point.getY();

            //Tri area is half of cross product
            long triArea = dy0 * dx1 - dx0 * dy1;
            area += triArea;
        }

        //CW shapes have -ve area, CCW have +ve
        return area < 0;
    }

//    @Override
//    public PathCurve buildPathCurve()
//    {
//        Path2D.Double path = new Path2D.Double();
//        ArrayList<ArrayList<BezierEdge>> strips = getStripifiedEdges();
//
//        for (ArrayList<BezierEdge> strip: strips)
//        {
//            BezierEdge firstEdge = strip.get(0);
//            path.moveTo(firstEdge.start.point.getX(), firstEdge.start.point.getY());
//
//            for (int i = 0; i < strip.size(); ++i)
//            {
//                BezierEdge edge = strip.get(i);
//                edge.getCurve().appendToPath(path);
//            }
//        }
//
//        return new PathCurve(path);
//    }
//
//    @Override
//    public ArrayList<BezierEdge> getEdgesSorted()
//    {
//        ArrayList<ArrayList<BezierEdge>> strips = getStripifiedEdges();
//        ArrayList<BezierEdge> list = new ArrayList<BezierEdge>();
//
//        for (ArrayList<BezierEdge> strip: strips)
//        {
//            for (int i = 0; i < strip.size(); ++i)
//            {
//                BezierEdge edge = strip.get(i);
//                list.add(edge);
//            }
//        }
//
//        return list;
//    }

    public String toSVG()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        for (BezierFace face: getFaces())
        {
            pw.println(String.format("<g name=\"face%d\">", face.getUid()));
            pw.append(face.toSVG("    "));
            pw.println("</g>");
        }

        pw.close();
        return sw.toString();
        
    }
}
