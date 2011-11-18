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
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class RavenNodeGroup extends RavenNodeXformable
{
    public static final String CHILD_CHILDREN = "children";
    public final ChildWrapperList<RavenNodeGroup, RavenNodeXformable> children =
            new ChildWrapperList(this, CHILD_CHILDREN, RavenNodeXformable.class);

    protected RavenNodeGroup(int uid)
    {
        super(uid);
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            child.render(renderer);
        }
    }

//    @Override
//    public Shape getPickShapeLocal()
//    {
//        Path2D.Float combined = new Path2D.Float();
//        for (int i = 0; i < children.size(); ++i)
//        {
//            RavenNodeXformable child = children.get(i);
//
//            Shape childClip = child.getPickShapeLocal();
//            AffineTransform l2p = child.getLocalToParentTransform((AffineTransform)null);
//            if (!l2p.isIdentity())
//            {
//                childClip = l2p.createTransformedShape(childClip);
//            }
//            combined.append(childClip, false);
//        }
//        return combined;
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
//        CyDrawStack renderer = ctx.getDrawStack();

        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            child.render(ctx);
        }
    }

    @Override
    public CyShape getShapePickLocal()
    {
        CyPath2d combined = new CyPath2d();
        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);

            CyShape childClip = child.getShapePickLocal();
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
    public RavenNodeRenderable pickObject(CyRectangle2d rectangle, CyMatrix4d parentToPick, Intersection intersection)
    {
        CyMatrix4d l2p = new CyMatrix4d();
        getLocalToParentTransform(l2p);
        l2p.mul(parentToPick, l2p);

        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            RavenNodeRenderable res = child.pickObject(rectangle, l2p, intersection);
            if (res != null)
            {
                return res;
            }
        }

        return null;
    }

    @Override
    public void pickObjects(CyRectangle2d rectangle, CyMatrix4d parentToPick, Intersection intersection, ArrayList<NodeObject> pickList)
    {
        CyMatrix4d l2p = new CyMatrix4d();
        getLocalToParentTransform(l2p);
        l2p.mul(parentToPick, l2p);

        for (int i = 0; i < children.size(); ++i)
        {
            RavenNodeXformable child = children.get(i);
            child.pickObjects(rectangle, l2p, intersection, pickList);
        }
    }

//    private final ChildList<RavenNodeSpatial> children = new ChildList<RavenNodeSpatial>(RavenNodeSpatial.class);
//
//
////    @Override
////    public void getPropertySheet() {
////        throw new UnsupportedOperationException("Not supported yet.");
////    }
//
//    /**
//     * @return the children
//     */
//    public ChildList<RavenNodeSpatial> getChildren()
//    {
//        return children;
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeGroup>
    {
        public Provider()
        {
            super(RavenNodeGroup.class, "Group", "/icons/node/group.png");
        }

        @Override
        public RavenNodeGroup createNode(int uid)
        {
            return new RavenNodeGroup(uid);
        }
    }
}
