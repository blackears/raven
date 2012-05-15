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
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class RavenNodeSceneGraph extends RavenNodeRenderable
{
    public static final String CHILD_SCENEGRAPH = "sceneGraph";
    public final ChildWrapperList<RavenNodeSceneGraph, RavenNodeXformable> children =
            new ChildWrapperList(
            this, CHILD_SCENEGRAPH, RavenNodeXformable.class);

    public RavenNodeSceneGraph(int uid)
    {
        super(uid);
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        for (int i = 0; i < children.size(); ++i)
        {
            children.get(i).render(ctx);
        }
    }

    @Override
    public CyShape getShapePickLocal(FrameKey key)
    {
        CyPath2d combined = new CyPath2d();
        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);

            CyShape childClip = child.getShapePickLocal(key);
            if (childClip == null)
            {
                continue;
            }
            
            CyMatrix4d l2p = child.getLocalToParentTransform((CyMatrix4d)null);
            if (!l2p.isIdentity())
            {
                childClip = childClip.createTransformedPath(l2p);
            }
            combined.append(childClip);
        }
        return combined;
    }

    @Override
    public RavenNodeRenderable pickObject(CyRectangle2d rectangle, 
            FrameKey key,
            CyMatrix4d worldToPick, 
            Intersection intersection)
    {
        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            RavenNodeRenderable node = child.pickObject(rectangle, key, worldToPick, intersection);
            if (node != null)
            {
                return node;
            }
        }
        return null;
    }

    @Override
    public void pickObjects(CyRectangle2d rectangle, 
            FrameKey key, 
            CyMatrix4d worldToPick, 
            Intersection intersection, 
            ArrayList<NodeObject> pickList)
    {
        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            child.pickObjects(rectangle, key, worldToPick, intersection, pickList);
        }
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList<RavenNodeXformable> getChildren()
    {
        return children.getChildren();
    }

    public void add(RavenNodeXformable child)
    {
        children.add(child);
    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeSceneGraph>
    {
        public Provider()
        {
            super(RavenNodeSceneGraph.class, "Scene Graph", "/icons/node/scenegraph.png");
        }

        @Override
        public RavenNodeSceneGraph createNode(int uid)
        {
            return new RavenNodeSceneGraph(uid);
        }
    }
    
}
