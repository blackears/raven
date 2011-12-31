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

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.tools.common.ServiceBezierNetwork;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.bezier.BezierPath;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.undo.History;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RavenNodePath extends RavenNodeShape
        implements ServiceBezierNetwork
{
    public static final String PROP_PATH = "path";
    public final PropertyWrapper<RavenNodePath, PathCurve> path =
            new PropertyWrapper(
            this, PROP_PATH, PathCurve.class);

    public static final String CHILD_VERTEX_PLANES = "vertexPlanes";
    public final ChildWrapperList<RavenNodePath, RavenNodeDataPlane> vertexPlanes =
            new ChildWrapperList<RavenNodePath, RavenNodeDataPlane>(
            this, CHILD_VERTEX_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_EDGE_PLANES = "edgePlanes";
    public final ChildWrapperList<RavenNodePath, RavenNodeDataPlane> edgePlanes =
            new ChildWrapperList<RavenNodePath, RavenNodeDataPlane>(
            this, CHILD_EDGE_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_FACE_PLANES = "facePlanes";
    public final ChildWrapperList<RavenNodePath, RavenNodeDataPlane> facePlanes =
            new ChildWrapperList<RavenNodePath, RavenNodeDataPlane>(
            this, CHILD_FACE_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_FACE_VERTEX_PLANES = "faceVertexPlanes";
    public final ChildWrapperList<RavenNodePath, RavenNodeDataPlane> faceVertexPlanes =
            new ChildWrapperList<RavenNodePath, RavenNodeDataPlane>(
            this, CHILD_FACE_VERTEX_PLANES, RavenNodeDataPlane.class);

    private static final int flatnessSquared = 10000;
//    Path2D.Double pathCache;
    BezierPath bezierPath;

    static final AffineTransform toPixels = new AffineTransform(1.0 / 100, 0, 0, 1.0 / 100, 0, 0);

    public RavenNodePath(int uid)
    {
        super(uid);

        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
//        pathCache = null;
        bezierPath = null;
    }

    @Override
    public CyShape createShapeLocal(FrameKey time)
    {
        NodeDocument doc = getDocument();
        PathCurve curve = path.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);

//        if (pathCache == null)
//        {
//            PathCurve curve = path.getValue();
            if (curve == null)
            {
                return null;
            }

            Path2D pathCache = curve.asPath2D();
            pathCache = (Path2D.Double)toPixels.createTransformedShape(pathCache);
//        }
//        return pathCache;
        return CyPath2d.create(pathCache);
    }

//    @Override
//    public Shape getShapePickLocal()
//    {
//        if (pathCache == null)
//        {
//            PathCurve curve = path.getValue();
//            if (curve == null)
//            {
//                return null;
//            }
//
//            pathCache = curve.asPath2D();
//            pathCache = (Path2D.Double)toPixels.createTransformedShape(pathCache);
//        }
//        return pathCache;
//    }

    @Override
    public BezierNetwork getBezierNetwork()
    {
        if (bezierPath == null)
        {
            //Build from path curve
            PathCurve curve = path.getValue();
            if (curve == null)
            {
                return null;
            }

//            bezierPath = new BezierPath(flatnessSquared);
//            bezierPath.append(curve.asPath2D(), null);

            bezierPath = new BezierPath(curve,
                    getPlaneData(vertexPlanes),
                    getPlaneData(edgePlanes),
                    getPlaneData(facePlanes),
                    getPlaneData(faceVertexPlanes),
                    flatnessSquared);
        }

        return bezierPath;
    }

//    private ArrayList<RavenNodeDataPlane> getPlanes(ChildWrapper<RavenNodePath,
//            RavenNodeDataPlane> planes)
//    {
//        for (int i = 0; i < planes.size(); ++i)
//        {
//            RavenNodeDataPlane dataPlane = (RavenNodeDataPlane)planes.get(i);
//            dataPlane.getPlaneDataType();
//
//        }
//    }

    @Override
    public void updateFromBezierNetwork(BezierNetwork network, boolean history)
    {
        if (!(network instanceof BezierPath))
        {
            return;
        }

        BezierPath bezPath = (BezierPath)network;
        PathCurve curve = new PathCurve(bezPath.asPath());

        History hist = getDocument().getHistory();

        if (history)
        {
            hist.beginTransaction("Update path");
        }

        path.setValue(curve, history);

        vertexPlanes.removeAll();
        for (Class<? extends PlaneDataProvider> key : network.getDataKeysVertex())
        {
            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
                    RavenNodeDataPlane.class, getDocument());
            vertexPlanes.add(plane);
            plane.dataValues.addPropertyWrapperListener(clearCache);

            ArrayList list = bezPath.buildDataPlaneVertex(key);
            plane.setPlaneData(key, list, history);
        }

        edgePlanes.removeAll();
        for (Class<? extends PlaneDataProvider> key : network.getDataKeysEdge())
        {
            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
                    RavenNodeDataPlane.class, getDocument());
            edgePlanes.add(plane);
            plane.dataValues.addPropertyWrapperListener(clearCache);

            ArrayList list = bezPath.buildDataPlaneEdge(key);
            plane.setPlaneData(key, list, history);
        }

        facePlanes.removeAll();
        for (Class<? extends PlaneDataProvider> key : network.getDataKeysFace())
        {
            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
                    RavenNodeDataPlane.class, getDocument());
            facePlanes.add(plane);
            plane.dataValues.addPropertyWrapperListener(clearCache);

            ArrayList list = bezPath.buildDataPlaneFace(key);
            plane.setPlaneData(key, list, history);
        }

//        faceVertexPlanes.removeAll();
//        for (Class<? extends PlaneDataProvider> key : network.getDataKeysFaceVertex())
//        {
//            RavenNodeDataPlane plane = NodeObjectProviderIndex.inst().createNode(
//                    RavenNodeDataPlane.class, getDocument());
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

    private HashMap<Class<? extends PlaneDataProvider>, ArrayList>
            getPlaneData(
            ChildWrapperList<RavenNodePath, RavenNodeDataPlane> planeChildren)
    {
        HashMap<Class<? extends PlaneDataProvider>, ArrayList> map
                = new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();

        for (int i = 0; i < planeChildren.size(); ++i)
        {
            RavenNodeDataPlane plane = planeChildren.get(i);
            map.put(plane.getPlaneDataType(), plane.getPlaneData());
        }

        return map;
    }



    //-----------------------------------------------

    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodePath>
    {
        public Provider()
        {
            super(RavenNodePath.class, "Path", "/icons/node/path.png");
        }

        @Override
        public RavenNodePath createNode(int uid)
        {
            return new RavenNodePath(uid);
        }
    }

}
