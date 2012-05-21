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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutSegHalf;
import com.kitfox.coyote.shape.bezier.path.BezierPath2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.raven.editor.node.tools.common.shape.ServiceShapeManip;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierPath;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkPath;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleFace;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles.HandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles.HandleFace;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kitfox
 */
public class RavenNodePath extends RavenNodeShape
        implements ServiceBezierPath, ServiceShapeManip
{
    public static final String PROP_PATH = "path";
    public final PropertyWrapper<RavenNodePath, NetworkPath> path =
            new PropertyWrapper(
            this, PROP_PATH, NetworkPath.class, 
            new NetworkPath());

    private static final int flatnessSquared = 10000;

    public RavenNodePath(int uid)
    {
        super(uid);

        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
    }

    protected CyPath2d getPathGrid(FrameKey key)
    {
        CyPath2d shapePath = path.getUserCacheValue(CyPath2d.class, key);
        if (shapePath == null)
        {
            BezierPath2i cPath = path.getValue(key);
            if (cPath == null)
            {
                return null;
            }
            shapePath = cPath.asPath();
            path.setUserCacheValue(CyPath2d.class, key, shapePath);
        }
        return shapePath;
    }
    
    @Override
    public CyPath2d createShapeLocal(FrameKey key)
    {
        CyPath2d shapePath = getPathGrid(key);
        if (shapePath == null)
        {
            return null;
        }
        
        return shapePath.createTransformedPath(meshToLocal);
    }

    @Override
    public NetworkPath getNetworkPath()
    {
        return new NetworkPath(path.getValue());
    }

    @Override
    public void setNetworkPath(NetworkPath path, boolean history)
    {
        this.path.setValue(path, history);
    }

    @Override
    public CyMatrix4d getGraphToWorldXform()
    {
        CyMatrix4d g2w = getLocalToWorldTransform((CyMatrix4d)null);
        g2w.scale(.01, .01, 1);
        return g2w;
    }

    private NetworkPathHandles getPathHandles()
    {
        NetworkPathHandles handles = path.getUserCacheValue(NetworkPathHandles.class);
        if (handles == null)
        {
            handles = new NetworkPathHandles(path.getValue());
            path.setUserCacheValue(NetworkPathHandles.class, handles);
        }
        return handles;
    }

    @Override
    public ArrayList<? extends NetworkHandleVertex> pickVertices(CyRectangle2d region, CyMatrix4d l2d, Intersection isect)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.pickVertices(region, l2d, isect);
    }

    @Override
    public ArrayList<? extends NetworkHandleEdge> pickEdges(CyRectangle2d region, CyMatrix4d l2d, Intersection isect)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.pickEdges(region, l2d, isect);
    }

    @Override
    public ArrayList<? extends NetworkHandleFace> pickFaces(CyRectangle2d region, CyMatrix4d l2d, Intersection isect)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.pickFaces(region, l2d, isect);
    }

    @Override
    public ArrayList<? extends NetworkHandleEdge> getConnectedEdges(NetworkHandleEdge edge)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.getConnectedEdges(edge);
    }

    @Override
    public void setEdgePaintAndStroke(RavenPaint paint, RavenStroke stroke, Collection<? extends NetworkHandleEdge> edges, boolean history)
    {
        NetworkPath oldPath = path.getValue();
        NetworkPath newPath = new NetworkPath(oldPath);
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierPathEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypePaint.class, paint);
            data.putEdge(NetworkDataTypeStroke.class, stroke);
        }

        path.setValue(newPath, history);
    }

    @Override
    public void setEdgePaint(RavenPaint paint, Collection<? extends NetworkHandleEdge> edges, boolean history)
    {
        NetworkPath oldPath = path.getValue();
        NetworkPath newPath = new NetworkPath(oldPath);
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierPathEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypePaint.class, paint);
        }

        path.setValue(newPath, history);
    }

    @Override
    public void setEdgeStroke(RavenStroke stroke, Collection<? extends NetworkHandleEdge> edges, boolean history)
    {
        //Create a new path and set coresponding edge data
        NetworkPath oldPath = path.getValue();
        NetworkPath newPath = new NetworkPath(oldPath);
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
        for (NetworkHandleEdge edge: edges)
        {
            HandleEdge handle = newHandles.getEdgeHandle(edge.getIndex());
            BezierPathEdge2i<NetworkDataEdge> bezEdge = newHandles.getEdge(handle);
            
            NetworkDataEdge data = bezEdge.getData();
            data.putEdge(NetworkDataTypeStroke.class, stroke);
        }

        path.setValue(newPath, history);
    }

    @Override
    public void setFacePaint(RavenPaint paint, Collection<? extends NetworkHandleFace> faces, boolean history)
    {
        //Create a new path and set coresponding edge data
        NetworkPath oldPath = path.getValue();
        NetworkPath newPath = new NetworkPath(oldPath);
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
//        for (NetworkHandleFace face: faces)
//        {
//            HandleFace handle = newHandles.getFaceHandle(face.getIndex());
//            
//            CutLoop loop = newHandles.getFace(handle);
//            for (CutSegHalf half: loop.getSegs())
//            {
//                BezierPathEdge2i<NetworkDataEdge> bezEdge = half.getEdge();
//                if (bezEdge == null)
//                {
//                    //Is auto-inserted island connecting segment
//                    continue;
//                }
//                NetworkDataEdge data = bezEdge.getData();
//
//                if (half.isRight())
//                {
//                    data.putRight(NetworkDataTypePaint.class, paint);
//                }
//                else
//                {
//                    data.putLeft(NetworkDataTypePaint.class, paint);
//                }
//            }
//        }

        path.setValue(newPath, history);
    }

    @Override
    public ArrayList<? extends NetworkHandleEdge> getEdges()
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.getEdgeList();
    }

    @Override
    public ArrayList<? extends NetworkHandleVertex> getVertices()
    {
        NetworkPathHandles handles = getPathHandles();
        return handles.getVertList();
    }

    @Override
    public ArrayList<? extends NetworkHandleEdge> getEdgesByIds(ArrayList<Integer> edgeIds)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles == null ? null : handles.getEdgesByIds(edgeIds);
    }

    @Override
    public ArrayList<? extends NetworkHandleFace> getFacesByIds(ArrayList<Integer> faceIds)
    {
        NetworkPathHandles handles = getPathHandles();
        return handles == null ? null : handles.getFacesByIds(faceIds);
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
