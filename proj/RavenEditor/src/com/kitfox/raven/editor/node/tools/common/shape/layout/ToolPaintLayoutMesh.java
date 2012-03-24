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

package com.kitfox.raven.editor.node.tools.common.shape.layout;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierMesh;
import com.kitfox.raven.editor.node.tools.common.shape.MeshUtil;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.*;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleFace;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
public class ToolPaintLayoutMesh extends ToolPaintLayoutDelegate
{
    NodeObject node;
    final ServiceBezierMesh servMesh;
    NetworkMeshHandles cacheHandles;

//    private MouseEvent mouseStart;
//    private MouseEvent mouseCur;
    
    NetworkMesh cacheMesh;
    NetworkMesh meshDragStart;
    PaintLayoutManipulator cacheManip;
    NetworkMesh initMesh;
    RavenPaintLayout curLayout = new RavenPaintLayout();
    PaintLayoutManipulator.Handle manipHandle;

    public ToolPaintLayoutMesh(ToolPaintLayoutDispatch dispatch, 
            NodeObject node,
            ServiceBezierMesh servMesh)
    {
        super(dispatch);
        this.servMesh = servMesh;
        this.node = node;

        initMesh = new NetworkMesh(servMesh.getNetworkMesh());
        
        loadLayoutFromSelection();
        
    }

    private void checkCache()
    {
        NetworkMesh mesh = servMesh.getNetworkMesh();
        if (mesh != cacheMesh)
        {
            cacheHandles = null;
            
            if (manipHandle == null)
            {
                //Only clear the manipulator if there is no handle
                // being dragged
                cacheManip = null;
            }
        }
        
//        ???
//        if (cacheHandles == null || cacheHandles.getMesh() != mesh)
//        {
//            cacheHandles = null;
//            
//            if (manipHandle == null)
//            {
//                //Only clear the manipulator if there is no handle
//                // being dragged
//                cacheManip = null;
//            }
//        }
        
    }
    
    private PaintLayoutManipulator getManip()
    {
        checkCache();
        if (cacheManip == null)
        {
            loadLayoutFromSelection();
            cacheManip = new PaintLayoutManipulator(curLayout);
        }
        return cacheManip;
    }
    
    private NetworkMeshHandles getMeshHandles()
    {
        checkCache();
        NetworkMesh mesh = getMesh();
        if (cacheHandles == null)
        {
            cacheHandles = mesh == null ? null : new NetworkMeshHandles(mesh);
        }
        return cacheHandles;
    }
    
    private NetworkMesh getMesh()
    {
        return servMesh.getNetworkMesh();
    }
    
    private void loadLayoutFromSelection()
    {
        RavenPaintLayout layout = getLayoutFromSelection();
        if (layout != null)
        {
            curLayout = layout;
            cacheManip = null;
        }
    }
    
    private RavenPaintLayout getLayoutFromSelection()
    {
        NetworkMeshHandles handles = getMeshHandles();
        
        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        if (subSel == null)
        {
            return null;
        }
        
        for (int i = subSel.size() - 1; i >= 0; --i)
        {
            NetworkSubselElement ele = subSel.get(i);
            if (ele.getType() == NetworkSubselType.EDGE)
            {
                HandleEdge edge = handles.getEdgeHandle(ele.getId());
                RavenPaintLayout layout = edge.getPaintLayout();
                if (layout != null)
                {
                    return layout;
                }
            }
            if (ele.getType() == NetworkSubselType.FACE)
            {
                HandleFace face = handles.getFaceHandle(ele.getId());
                RavenPaintLayout layout = face.getPaintLayout();
                if (layout != null)
                {
                    return layout;
                }
            }
        }
        return null;
    }

    
    @Override
    protected void click(MouseEvent evt)
    {
        RavenNodeRoot root = getDocument();
        if (root == null)
        {
            return;
        }

        int mod = evt.getModifiersEx();
        if ((mod & InputEvent.BUTTON3_DOWN_MASK) != 0)
        {
            showPopupMenu(evt);
            return;
        }
        
        //Find components to select
        float pickRad = root == null ? 1 : root.getGraphRadiusPick();
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);
        
        CyMatrix4d l2w = servMesh.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        MeshUtil.adjustSelection(region, getSelectType(evt), Intersection.INTERSECTS,
                getSelection(), node, getMeshHandles(), l2w, l2d);

        loadLayoutFromSelection();
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        PaintLayoutManipulator manip = getManip();
        
        CyMatrix4d l2d = getLocalToDevice(null);
        
        meshDragStart = servMesh.getNetworkMesh();
//        mouseStart = evt;
        manipHandle = manip.getManipulatorHandle(evt, l2d);
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        if (manipHandle != null)
        {
            applyLayout(manipHandle.getLayout(), false);
            manipHandle.dragTo(evt.getX(), evt.getY());
        }
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (manipHandle != null)
        {
            manipHandle.dragTo(evt.getX(), evt.getY());
            applyLayout(manipHandle.getLayout(), true);
            meshDragStart = null;
            manipHandle = null;
            cacheManip = null;
        }
  //      mouseStart = null;
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                cancel();
                return;
        }
        
        super.keyPressed(evt);
    }

    @Override
    public void cancel()
    {
        //applyLayout(handle.getLayout(), false);
        servMesh.setNetworkMesh(initMesh, false);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);

        CyDrawStack stack = ctx.getDrawStack();

        MeshUtil.drawGraph(stack, getMeshHandles(), 
                getSelection(), node, servMesh.getGraphToWorldXform());
        
        PaintLayoutManipulator manip = getManip();
        manip.render(ctx);
    }    
    
    private void applyLayout(RavenPaintLayout layout, boolean history)
    {
        curLayout = layout;
        
        //Push layout onto mesh
        NetworkMeshHandles oldHandles = getMeshHandles();
        NetworkMesh oldMesh = oldHandles.getMesh();
        
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);

        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        for (int i = 0; i < subSel.size(); ++i)
        {
            NetworkSubselElement ele = subSel.get(i);
            if (ele.getType() == NetworkSubselType.EDGE)
            {
                HandleEdge edge = newHandles.getEdgeHandle(ele.getId());
                if (edge != null)
                {
                    edge.setEdgeLayout(layout);
                }
//                NetworkDataEdge data = edge.getData();
//                data.putEdge(NetworkDataTypePaintLayout.class, layout);
            }
            else if (ele.getType() == NetworkSubselType.FACE)
            {
                HandleFace face = newHandles.getFaceHandle(ele.getId());
                if (face != null)
                {
                    face.setFaceLayout(layout);
                }
//                data.putEdge(NetworkDataTypePaintLayout.class, layout);
            }
        }
        
        
        if (history)
        {
            //Restore old mesh for undo history
            servMesh.setNetworkMesh(meshDragStart, false);
            servMesh.setNetworkMesh(newMesh, true);
        }
        else
        {
            servMesh.setNetworkMesh(newMesh, false);
        }
    }
    
    private void showPopupMenu(MouseEvent evt)
    {
    }
}
