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

import com.kitfox.raven.shape.bezier.BezierNetwork.NetworkUpdateCallback;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierMeshSerializer
{
    private PathCurve path;

    //Ordered lists of components
//        ArrayList<BezierVertex> vertexList = new ArrayList<BezierVertex>();
//        ArrayList<BezierEdge> edgeList = new ArrayList<BezierEdge>();
//        ArrayList<BezierFace> faceList = new ArrayList<BezierFace>();
//        ArrayList<BezierFaceVertex> faceVertexList = new ArrayList<BezierFaceVertex>();
    private ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> vertexData;
    private ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> edgeData;
    private ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>> faceData;

    private BezierMeshSerializer()
    {
        this(null,
            new ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>(),
            new ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>(),
            new ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>());
    }

    public BezierMeshSerializer(PathCurve path,
            ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> vertexData,
            ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> edgeData,
            ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> faceData)
    {
        this.path = path;
        this.vertexData = vertexData;
        this.edgeData = edgeData;
        this.faceData = faceData;
    }

    public BezierMeshSerializer(PathCurve path,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> vertexData,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> edgeData,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> faceData)
    {
        this(path, toArrayForm(vertexData), toArrayForm(edgeData), toArrayForm(faceData));
    }

    private static ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>>
            toArrayForm(HashMap<Class<? extends PlaneDataProvider>, ArrayList> dataMap)
    {
        ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> arrayData
                = new ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>();

        for (Class<? extends PlaneDataProvider> key: dataMap.keySet())
        {
            ArrayList values = dataMap.get(key);

            for (int j = 0; j < values.size(); ++j)
            {
                if (arrayData.size() <= j)
                {
                    arrayData.add(new HashMap<Class<? extends PlaneDataProvider>, Object>());
                }
                HashMap<Class<? extends PlaneDataProvider>, Object> map
                        = arrayData.get(j);
                map.put(key, values.get(j));
            }
        }

        return arrayData;
    }


    public BezierMesh asMesh()
    {
        final ArrayList<BezierEdge> edgesOrdered = new ArrayList<BezierEdge>();
        class EdgeBuilder implements NetworkUpdateCallback
        {
            @Override
            public void addedEdge(double tOffset, double tSpan, BezierEdge edge)
            {
                assert tOffset == 0 && tSpan == 1;

                edgesOrdered.add(edge);
            }
        }
        EdgeBuilder builder = new EdgeBuilder();

        BezierMesh mesh = new BezierMesh(10000);

        double[] coords = new double[6];
        double px = 0, py = 0;

        for (PathIterator it = path.asPath2D().getPathIterator(null);
            !it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                    px = coords[0];
                    py = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                {
                    BezierCurveLine curve = new BezierCurveLine(
                            px, py,
                            coords[0], coords[1]);
                    mesh.addCurve(curve, builder);
                    px = coords[0];
                    py = coords[1];
                    break;
                }
                case PathIterator.SEG_QUADTO:
                {
                    BezierCurveQuadratic curve = new BezierCurveQuadratic(
                            px, py,
                            coords[0], coords[1],
                            coords[2], coords[3]);
                    mesh.addCurve(curve, builder);
                    px = coords[2];
                    py = coords[3];
                    break;
                }
                case PathIterator.SEG_CUBICTO:
                {
                    BezierCurveCubic curve = new BezierCurveCubic(
                            px, py,
                            coords[0], coords[1],
                            coords[2], coords[3],
                            coords[4], coords[5]);
                    mesh.addCurve(curve, builder);
                    px = coords[4];
                    py = coords[5];
                    break;
                }
            }
        }

        ArrayList<BezierVertex> verticesOrdered = new ArrayList<BezierVertex>();
        ArrayList<BezierFace> facesOrdered = new ArrayList<BezierFace>();

        facesOrdered.add(mesh.getFaceOutside());

        //Place ordering on derrived components
        for (BezierEdge edge: edgesOrdered)
        {
            if (!verticesOrdered.contains(edge.start))
            {
                verticesOrdered.add(edge.start);
            }
            if (!verticesOrdered.contains(edge.end))
            {
                verticesOrdered.add(edge.end);
            }
            if (!facesOrdered.contains(edge.faceLeft))
            {
                facesOrdered.add(edge.faceLeft);
            }
            if (!facesOrdered.contains(edge.faceRight))
            {
                facesOrdered.add(edge.faceRight);
            }
        }

        if (!vertexData.isEmpty())
        {
            assert verticesOrdered.size() == vertexData.size();
            for (int i = 0; i < verticesOrdered.size(); ++i)
            {
                BezierVertex vtx = verticesOrdered.get(i);
                vtx.setData(vertexData.get(i));
            }
        }

        if (!edgeData.isEmpty())
        {
            assert edgesOrdered.size() == edgeData.size();
            for (int i = 0; i < edgesOrdered.size(); ++i)
            {
                BezierEdge edge = edgesOrdered.get(i);
                edge.setData(edgeData.get(i));
            }
        }

        if (!faceData.isEmpty())
        {
            assert facesOrdered.size() == faceData.size();
            for (int i = 0; i < facesOrdered.size(); ++i)
            {
                BezierFace face = facesOrdered.get(i);
                face.setData(faceData.get(i));
            }
        }

        return mesh;
    }

    public static BezierMeshSerializer createSerialRecord(BezierMesh mesh)
    {
        //Export all data in one shot
        ArrayList<EdgeStrip> strips = createEdgeStrips(mesh);

        ArrayList<BezierVertex> vertexOrder = new ArrayList<BezierVertex>();
        ArrayList<BezierEdge> edgeOrder = new ArrayList<BezierEdge>();
        ArrayList<BezierFace> faceOrder = new ArrayList<BezierFace>();
        faceOrder.add(mesh.getFaceOutside());

        Path2D.Double path = new Path2D.Double();
        for (EdgeStrip head: strips)
        {
            BezierVertex startVtx = head.getVertexStart();
            path.moveTo(startVtx.point.getX(), startVtx.point.getY());

            for (EdgeStrip edge = head; edge != null; edge = edge.next)
            {
                BezierVertex vtxStart = edge.getVertexStart();
                BezierVertex vtxEnd = edge.getVertexEnd();
                BezierFace faceLeft = edge.getFaceLeft();
                BezierFace faceRight = edge.getFaceRight();
                BezierCurve curve = edge.getCurve();

                curve.appendToPath(path);
                edgeOrder.add(edge.edge);

                if (!vertexOrder.contains(vtxStart))
                {
                    vertexOrder.add(vtxStart);
                }
                if (!vertexOrder.contains(vtxEnd))
                {
                    vertexOrder.add(vtxEnd);
                }
                if (!faceOrder.contains(faceLeft))
                {
                    faceOrder.add(faceLeft);
                }
                if (!faceOrder.contains(faceRight))
                {
                    faceOrder.add(faceRight);
                }
            }
        }

        BezierMeshSerializer rec = new BezierMeshSerializer();
        rec.path = new PathCurve(path);

        for (BezierVertex vtx: vertexOrder)
        {
            rec.vertexData.add(vtx.getData());
        }
        for (BezierEdge edge: edgeOrder)
        {
            rec.edgeData.add(edge.getData());
        }
        for (BezierFace face: faceOrder)
        {
            rec.faceData.add(face.getData());
        }
        return rec;
    }

    private static ArrayList<EdgeStrip> createEdgeStrips(BezierMesh mesh)
    {
        ArrayList<BezierEdge> edges = mesh.getEdges();
        EdgeStrip head = null;
        EdgeStrip tail = null;

        ArrayList<EdgeStrip> strips = new ArrayList<EdgeStrip>();

        while (!edges.isEmpty())
        {
            if (head == null)
            {
                head = tail = new EdgeStrip(edges.remove(edges.size() - 1), false);
            }

            boolean extendedStrip = false;

            //Add to strip start
            {
                boolean extendedHead = false;
                BezierVertex vtx = head.getVertexStart();
                for (BezierEdge edge: vtx.edgeIn)
                {
                    if (edges.contains(edge))
                    {
                        edges.remove(edge);
                        EdgeStrip entry = new EdgeStrip(edge, false);
                        entry.next = head;
                        head.prev = entry;
                        head = entry;
                        extendedStrip = true;
                        extendedHead = true;
                        break;
                    }
                }

                if (!extendedHead)
                {
                    //Check for segs with reverse winding
                    for (BezierEdge edge: vtx.edgeOut)
                    {
                        if (edges.contains(edge))
                        {
                            edges.remove(edge);
                            EdgeStrip entry = new EdgeStrip(edge, true);
                            entry.next = head;
                            head.prev = entry;
                            head = entry;
                            extendedStrip = true;
                            break;
                        }
                    }
                }
            }

            //Add to strip end
            {
                boolean extendedTail = false;
                BezierVertex vtx = tail.getVertexEnd();
                for (BezierEdge edge: vtx.edgeOut)
                {
                    if (edges.contains(edge))
                    {
                        edges.remove(edge);
                        EdgeStrip entry = new EdgeStrip(edge, false);
                        entry.prev = tail;
                        tail.next = entry;
                        tail = entry;
                        extendedStrip = true;
                        extendedTail = true;
                        break;
                    }
                }

                if (!extendedTail)
                {
                    //Check for segs with reverse winding
                    for (BezierEdge edge: vtx.edgeIn)
                    {
                        if (edges.contains(edge))
                        {
                            edges.remove(edge);
                            EdgeStrip entry = new EdgeStrip(edge, true);
                            entry.prev = tail;
                            tail.next = entry;
                            tail = entry;
                            extendedStrip = true;
                            break;
                        }
                    }
                }
            }

            if (!extendedStrip)
            {
                //Signal to start new strip
                strips.add(head);
                head = tail = null;
            }
        }

        if (head != null)
        {
            strips.add(head);
        }

        return strips;
    }

    /**
     * @return the path
     */
    public PathCurve getPath()
    {
        return path;
    }

    /**
     * @return the vertexData
     */
    public ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>
            getVertexData()
    {
        return vertexData;
    }

    public HashMap<Class<? extends PlaneDataProvider>, ArrayList>
            getVertexDataAsMap()
    {
        return toMapForm(vertexData);
    }

    /**
     * @return the edgeData
     */
    public ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>
            getEdgeData()
    {
        return edgeData;
    }

    public HashMap<Class<? extends PlaneDataProvider>, ArrayList>
            getEdgeDataAsMap()
    {
        return toMapForm(edgeData);
    }

    /**
     * @return the faceData
     */
    public ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>
            getFaceData()
    {
        return faceData;
    }

    public HashMap<Class<? extends PlaneDataProvider>, ArrayList>
            getFaceDataAsMap()
    {
        return toMapForm(faceData);
    }

    private HashMap<Class<? extends PlaneDataProvider>, ArrayList> toMapForm(
            ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>> data)
    {
        //Sort data by component index, then data type
        HashMap<Class<? extends PlaneDataProvider>, ArrayList> newDataArrays
                 = new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();

        for (int compIdx = 0; compIdx < data.size(); ++compIdx)
        {
            HashMap<Class<? extends PlaneDataProvider>, Object> map
                    = data.get(compIdx);
            for (Class<? extends PlaneDataProvider> key: map.keySet())
            {
                ArrayList dataList = newDataArrays.get(key);
                if (dataList == null)
                {
                    dataList = new ArrayList();
                    newDataArrays.put(key, dataList);
                }
                dataList.add(map.get(key));
            }
        }

        return newDataArrays;
    }

    static class EdgeStrip
    {
        BezierEdge edge;
        boolean reversed;
        EdgeStrip prev;
        EdgeStrip next;

        public EdgeStrip(BezierEdge edge, boolean reversed)
        {
            this.edge = edge;
            this.reversed = reversed;
        }

        private BezierVertex getVertexStart()
        {
            return reversed ? edge.end : edge.start;
        }

        private BezierVertex getVertexEnd()
        {
            return reversed ? edge.start : edge.end;
        }

        private BezierFace getFaceLeft()
        {
            return reversed ? edge.faceRight : edge.faceLeft;
        }

        private BezierFace getFaceRight()
        {
            return reversed ? edge.faceLeft : edge.faceRight;
        }

        private BezierCurve getCurve()
        {
            return reversed ? edge.getCurve().reverse() : edge.getCurve();
        }

    }

}
