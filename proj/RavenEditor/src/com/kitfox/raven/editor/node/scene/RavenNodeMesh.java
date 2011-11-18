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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.ServiceBezierNetwork;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintNone;
import com.kitfox.raven.editor.paint.RavenPaintProxy;
import com.kitfox.raven.editor.paintLayout.PaintLayoutProxy;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.editor.stroke.RavenStrokeNone;
import com.kitfox.raven.editor.stroke.RavenStrokeProxy;
import com.kitfox.raven.shape.bezier.BezierContourSet;
import com.kitfox.raven.shape.bezier.BezierEdge;
import com.kitfox.raven.shape.bezier.BezierFace;
import com.kitfox.raven.shape.bezier.BezierMesh;
import com.kitfox.raven.shape.bezier.BezierMeshSerializer;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.undo.History;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RavenNodeMesh extends RavenNodeXformable
        implements ServiceBezierNetwork
{
    public static final String PROP_PATH = "path";
    public final PropertyWrapper<RavenNodeMesh, PathCurve> path =
            new PropertyWrapper(
            this, PROP_PATH, PathCurve.class);

    public static final String CHILD_VERTEX_PLANES = "vertexPlanes";
    public final ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> vertexPlanes =
            new ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane>(
            this, CHILD_VERTEX_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_EDGE_PLANES = "edgePlanes";
    public final ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> edgePlanes =
            new ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane>(
            this, CHILD_EDGE_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_FACE_PLANES = "facePlanes";
    public final ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> facePlanes =
            new ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane>(
            this, CHILD_FACE_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_FACE_VERTEX_PLANES = "faceVertexPlanes";
    public final ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> faceVertexPlanes =
            new ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane>(
            this, CHILD_FACE_VERTEX_PLANES, RavenNodeDataPlane.class);

    //ShapeInfo meshContours;
    BezierMesh mesh;
    ArrayList<RenderableComponent> edgeContours;
    ArrayList<RenderableComponent> faceContours;
    Path2D.Double pickShape;

    static final AffineTransform toPixels = new AffineTransform(1.0 / 100, 0, 0, 1.0 / 100, 0, 0);

    static final int flatnessSquared = 10000;

    protected RavenNodeMesh(int uid)
    {
        super(uid);

        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
        mesh = null;
        edgeContours = null;
        faceContours = null;
        pickShape = null;
    }

//    private ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>>
//            buildPlaneData(ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> planeGroup)
//    {
//        ArrayList<HashMap<Class <? extends PlaneDataProvider>, Object>> planeData
//                = new ArrayList<HashMap<Class<? extends PlaneDataProvider>, Object>>();
//
//        for (int i = 0; i < planeGroup.size(); ++i)
//        {
//            RavenNodeDataPlane plane = planeGroup.get(i);
//            Class<? extends PlaneDataProvider> key = plane.getPlaneDataType();
//            ArrayList values = plane.getPlaneData();
//
//            for (int j = 0; j < values.size(); ++j)
//            {
//                if (planeData.size() <= j)
//                {
//                    planeData.add(new HashMap<Class<? extends PlaneDataProvider>, Object>());
//                }
//                HashMap<Class<? extends PlaneDataProvider>, Object> map
//                        = planeData.get(j);
//                map.put(key, values.get(j));
//            }
//        }
//
//        return planeData;
//    }

    private HashMap<Class <? extends PlaneDataProvider>, ArrayList>
            buildPlaneData(ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> planeGroup)
    {
        HashMap<Class <? extends PlaneDataProvider>, ArrayList> planeData
                = new HashMap<Class <? extends PlaneDataProvider>, ArrayList>();

        for (int i = 0; i < planeGroup.size(); ++i)
        {
            RavenNodeDataPlane plane = planeGroup.get(i);
            Class<? extends PlaneDataProvider> key = plane.getPlaneDataType();
            ArrayList values = plane.getPlaneData();
            planeData.put(key, values);
        }
        return planeData;
    }

    public BezierMesh getBezierMesh()
    {
        if (mesh == null)
        {
            HashMap<Class <? extends PlaneDataProvider>, ArrayList> vertexData
                    = buildPlaneData(vertexPlanes);
            HashMap<Class <? extends PlaneDataProvider>, ArrayList> edgeData
                    = buildPlaneData(edgePlanes);
            HashMap<Class <? extends PlaneDataProvider>, ArrayList> faceData
                    = buildPlaneData(facePlanes);

            BezierMeshSerializer serial = new BezierMeshSerializer(
                    path.getValue(), vertexData, edgeData, faceData);

            mesh = serial.asMesh();
//            double[] coords = new double[6];
//            mesh = new BezierMesh(flatnessSquared);
//
//            //Build mesh skeleton from path commands
//            PathCurve pathCurve = path.getValue();
//            double px = 0, py = 0;
//            for (PathIterator it = pathCurve.asPath2D().getPathIterator(null);
//                !it.isDone(); it.next())
//            {
//                switch (it.currentSegment(coords))
//                {
//                    case PathIterator.SEG_MOVETO:
//                        px = (int)coords[0];
//                        py = (int)coords[1];
//                        break;
//                    case PathIterator.SEG_LINETO:
//                    {
//                        double ex = coords[0];
//                        double ey = coords[1];
//                        BezierCurveLine curve = new BezierCurveLine(px, py, ex, ey);
//                        mesh.addCurve(curve, null);
//                        px = ex;
//                        py = ey;
//                        break;
//                    }
//                    case PathIterator.SEG_QUADTO:
//                    {
//                        double k0x = coords[0];
//                        double k0y = coords[1];
//                        double ex = coords[2];
//                        double ey = coords[3];
//                        BezierCurveQuadratic curve = new BezierCurveQuadratic(
//                                px, py, k0x, k0y, ex, ey);
//                        mesh.addCurve(curve, null);
//                        px = ex;
//                        py = ey;
//                        break;
//                    }
//                    case PathIterator.SEG_CUBICTO:
//                    {
//                        double k0x = coords[0];
//                        double k0y = coords[1];
//                        double k1x = coords[2];
//                        double k1y = coords[3];
//                        double ex = coords[4];
//                        double ey = coords[5];
//                        BezierCurveCubic curve = new BezierCurveCubic(
//                                px, py, k0x, k0y, k1x, k1y, ex, ey);
//                        mesh.addCurve(curve, null);
//                        px = ex;
//                        py = ey;
//                        break;
//                    }
//                    case PathIterator.SEG_CLOSE:
//                        break;
//                }
//            }
//
//            setPlaneData(mesh.getVerticesSorted(), vertexPlanes);
//            setPlaneData(mesh.getEdgesSorted(), edgePlanes);
//            setPlaneData(mesh.getFacesSorted(), facePlanes);
//            setPlaneData(mesh.getFacesVerticesSorted(), faceVertexPlanes);
        }
        return mesh;
    }


//    private void setPlaneData(ArrayList<? extends BezierNetworkComponent> list,
//            ChildWrapper<RavenNodeMesh, RavenNodeDataPlane> planes)
//    {
//        //Attach plane data
//        for (int i = 0; i < planes.size(); ++i)
//        {
//            RavenNodeDataPlane plane = planes.get(i);
//            String dataTypeStrn = plane.dataType.getValue();
//            String dataValueStrn = plane.dataValues.getValue();
//
//            PlaneDataProvider prov =
//                    PlaneDataProviderIndex.inst().getProvider(dataTypeStrn);
//
//            CacheList cacheList;
//            try
//            {
//                cacheList = (CacheList)CacheParser.parse(dataValueStrn);
//            } catch (ParseException ex)
//            {
//                Logger.getLogger(RavenNodeMesh.class.getName()).log(Level.SEVERE, null, ex);
//                continue;
//            }
//
//
//            for (int j = 0; j < list.size(); ++j)
//            {
//                Object value = prov.parse(cacheList.get(j));
//                list.get(j).setData(prov.getClass(), value);
//            }
//        }
//    }

    private ArrayList<RenderableComponent> getEdgeContours()
    {
        NodeDocument doc = getDocument();

        if (edgeContours == null)
        {
            edgeContours = new ArrayList<RenderableComponent>();

            BezierMesh curMesh = getBezierMesh();
            for (ArrayList<BezierEdge> strip: curMesh.getStripifiedEdges())
            {
                Path2D.Double strokePath = new Path2D.Double();
                BezierEdge firstEdge = strip.get(0);
                strokePath.moveTo(firstEdge.getStart().getPoint().getX(),
                        firstEdge.getStart().getPoint().getY());

                for (int i = 0; i < strip.size(); ++i)
                {
                    BezierEdge edge = strip.get(i);

                    edge.getCurve().appendToPath(strokePath);
                }

                RenderableComponent comp = new RenderableComponent(strokePath,
                        firstEdge.getData(RavenPaintProxy.PlaneData.class).getPaint(doc),
                        firstEdge.getData(PaintLayoutProxy.PlaneData.class).getLayout(),
                        firstEdge.getData(RavenStrokeProxy.PlaneData.class).getStroke(doc));

                edgeContours.add(comp);
            }
        }
        return edgeContours;
    }

    private ArrayList<RenderableComponent> getFaceContours()
    {
        NodeDocument doc = getDocument();

        if (faceContours == null)
        {
            faceContours = new ArrayList<RenderableComponent>();
            BezierMesh curMesh = getBezierMesh();
//curMesh.toSVG();

//            ArrayList<BezierFace> faces = new ArrayList<BezierFace>(curMesh.getFaces());
            for (BezierFace face: curMesh.getFaces())
            {
//                if (face == curMesh.getFaceOutside())
//                {
//                    continue;
//                }
                
                //Path2D.Double ctr = face.getContourCCW();
                BezierContourSet ctrSet = face.getContours();
                Path2D.Double ctr = ctrSet.createPath();

                RenderableComponent comp = new RenderableComponent(ctr,
                        face.getData(RavenPaintProxy.PlaneData.class).getPaint(doc),
                        face.getData(PaintLayoutProxy.PlaneData.class).getLayout());

                faceContours.add(comp);
            }
        }
        return faceContours;
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        for (RenderableComponent comp: getFaceContours())
        {
            RavenPaint paint = comp.paint;
            PaintLayout layout = comp.layout;
            if (paint == null || paint == RavenPaintNone.PAINT)
            {
                continue;
            }

            Path2D.Double path = (Path2D.Double)toPixels.createTransformedShape(comp.path);

            renderer.setPaint(paint);
            renderer.setPaintLayout(layout);
            renderer.fill(path);
        }

        for (RenderableComponent comp: getEdgeContours())
        {
            RavenStroke stroke = comp.stroke;
            RavenPaint paint = comp.paint;
            PaintLayout layout = comp.layout;
            if (paint == null || paint == RavenPaintNone.PAINT
                    || stroke == null || stroke == RavenStrokeNone.STROKE)
            {
                continue;
            }

            Path2D.Double path = (Path2D.Double)toPixels.createTransformedShape(comp.path);

            renderer.setPaint(paint);
            renderer.setPaintLayout(layout);
            renderer.setStroke(stroke.getStroke());
            renderer.draw(path);
        }

    }

//    @Override
    public Shape getPickShapeLocal()
    {
        if (pickShape == null)
        {
            pickShape = new Path2D.Double();
            for (RenderableComponent comp: getFaceContours())
            {
                pickShape.append(comp.path, false);
            }
            for (RenderableComponent comp: getEdgeContours())
            {
                pickShape.append(comp.path, false);
            }
        }
        return pickShape;
    }

    @Override
    public BezierNetwork getBezierNetwork()
    {
        return getBezierMesh();
    }

    @Override
    public void updateFromBezierNetwork(BezierNetwork network, boolean history)
    {
        NodeDocument doc = getDocument();
        updateFromBezierNetwork(doc, network, history);
    }

    private void setPlaneData(NodeDocument doc,
            ChildWrapperList<RavenNodeMesh, RavenNodeDataPlane> planeGroup,
            HashMap<Class<? extends PlaneDataProvider>, ArrayList> data,
            boolean history)
    {

        //Store data
        planeGroup.removeAll();
        for (Class<? extends PlaneDataProvider> key: data.keySet())
        {
            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
                RavenNodeDataPlane.class, doc);
            planeGroup.add(plane);
            plane.dataValues.addPropertyWrapperListener(clearCache);

            ArrayList dataList = data.get(key);
            plane.setPlaneData(key, dataList, history);
        }
    }

    public void updateFromBezierNetwork(NodeDocument doc, BezierNetwork network, boolean history)
    {
        History hist = doc == null ? null : doc.getHistory();
        if (history)
        {
            hist.beginTransaction("Update path");
        }

        BezierMeshSerializer serial = BezierMeshSerializer.createSerialRecord((BezierMesh)network);
        path.setValue(serial.getPath(), history);

        setPlaneData(doc, vertexPlanes, serial.getVertexDataAsMap(), history);
        setPlaneData(doc, edgePlanes, serial.getEdgeDataAsMap(), history);
        setPlaneData(doc, facePlanes, serial.getFaceDataAsMap(), history);

//DEBUG
//BezierMesh unpack = serial.asMesh();
//int j = 9;


//        PathCurve pathCurve = network.buildPathCurve();
//        path.setValue(pathCurve, history);
//
//        vertexPlanes.removeAll();
//        for (Class<? extends PlaneDataProvider> key : network.getDataKeysVertex())
//        {
//            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
//                    RavenNodeDataPlane.class, doc);
//            vertexPlanes.add(plane);
//            plane.dataValues.addPropertyWrapperListener(clearCache);
//
//            ArrayList list = network.buildDataPlaneVertex(key);
//            plane.setPlaneData(key, list, history);
//        }
//
//        edgePlanes.removeAll();
//        for (Class<? extends PlaneDataProvider> key : network.getDataKeysEdge())
//        {
//            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
//                    RavenNodeDataPlane.class, doc);
//            edgePlanes.add(plane);
//            plane.dataValues.addPropertyWrapperListener(clearCache);
//
//            ArrayList list = network.buildDataPlaneEdge(key);
//            plane.setPlaneData(key, list, history);
//        }
//
//        facePlanes.removeAll();
//        for (Class<? extends PlaneDataProvider> key : network.getDataKeysFace())
//        {
//            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
//                    RavenNodeDataPlane.class, doc);
//            facePlanes.add(plane);
//            plane.dataValues.addPropertyWrapperListener(clearCache);
//
//            ArrayList list = network.buildDataPlaneFace(key);
//            plane.setPlaneData(key, list, history);
//        }
//
//        faceVertexPlanes.removeAll();
//        for (Class<? extends PlaneDataProvider> key : network.getDataKeysFaceVertex())
//        {
//            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
//                    RavenNodeDataPlane.class, doc);
//            faceVertexPlanes.add(plane);
//            plane.dataValues.addPropertyWrapperListener(clearCache);
//
//            ArrayList list = network.buildDataPlaneFaceVertex(key);
//            plane.setPlaneData(key, list, history);
//        }

        if (history)
        {
            hist.commitTransaction();
        }
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CyShape getShapePickLocal()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    //-----------------------------------------------
    class RenderableComponent
    {
        Path2D.Double path;
        RavenPaint paint;
        PaintLayout layout;
        RavenStroke stroke;

        public RenderableComponent(Double path,
                RavenPaint paint, PaintLayout layout, RavenStroke stroke)
        {
            this.path = path;
            this.paint = paint;
            this.layout = layout;
            this.stroke = stroke;
        }

        public RenderableComponent(Double path, 
                RavenPaint paint, PaintLayout layout)
        {
            this(path, paint, layout, null);
        }
    }
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeMesh>
    {
        public Provider()
        {
            super(RavenNodeMesh.class, "Mesh", "/icons/node/mesh.png");
        }

        @Override
        public RavenNodeMesh createNode(int uid)
        {
            return new RavenNodeMesh(uid);
        }
    }
}
