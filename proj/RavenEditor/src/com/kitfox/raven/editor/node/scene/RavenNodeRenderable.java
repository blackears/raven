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
import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.filter.RavenFilter;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.tree.ChildWrapper;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperBoolean;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class RavenNodeRenderable extends RavenNode
{
    public static final String PROP_VISIBLE = "visible";
    public final PropertyWrapperBoolean<RavenNodeRenderable> visible =
            new PropertyWrapperBoolean(this, PROP_VISIBLE, true);

    public static final String PROP_OPACITY = "opacity";
    public final PropertyWrapperFloat<RavenNodeRenderable> opacity =
            new PropertyWrapperFloat(this, PROP_OPACITY, 1);

    public static final String PROP_FILTER = "filter";
    public final PropertyWrapper<RavenNodeRenderable, RavenFilter> filter =
            new PropertyWrapper(this, PROP_FILTER, RavenFilter.class);

    public static final double EPSILON = .0001;


    public RavenNodeRenderable(int uid)
    {
        super(uid);
    }

    public CyMatrix4d getLocalToParentTransform(CyMatrix4d result)
    {
        return getLocalToParentTransform(result);
    }

    public CyMatrix4d getLocalToParentTransform(FrameKey frame, CyMatrix4d result)
    {
        if (result == null)
        {
            return CyMatrix4d.createIdentity();
        }
        result.setIdentity();
        return result;
    }

    public CyMatrix4d getParentToWorldTransform(CyMatrix4d result)
    {
        return getParentToWorldTransform(FrameKey.DIRECT, result);
    }
    
    public CyMatrix4d getParentToWorldTransform(FrameKey frame, CyMatrix4d result)
    {
        if (result == null)
        {
            result = CyMatrix4d.createIdentity();
        }

        ChildWrapper myParent = getParent();
        if (myParent == null)
        {
            //TODO: this is caused by an attempt to render a node that has
            // just been deleted.  We should really be rendering this
            // synchronized to user IO.
            return result;
        //    myParent = getParent();
        //    assert false;
        }

        NodeObject node = myParent.getNode();
        if (node instanceof RavenNodeRenderable)
        {
            RavenNodeRenderable parentNode = 
                    (RavenNodeRenderable)node;
            return parentNode.getLocalToWorldTransform(frame, result);
        }

        result.setIdentity();
        return result;
    }

    public CyMatrix4d getLocalToWorldTransform(CyMatrix4d result)
    {
        return getLocalToWorldTransform(null, result);
    }
    
    public CyMatrix4d getLocalToWorldTransform(FrameKey frame, CyMatrix4d result)
    {
        if (result == null)
        {
            result = CyMatrix4d.createIdentity();
        }

        CyMatrix4d p2w = getParentToWorldTransform(frame, result);
        CyMatrix4d l2p = getLocalToParentTransform(frame, null);
        p2w.mul(l2p);
        return result;
    }

    public void render(RenderContext ctx)
    {
        CyDrawStack rend = ctx.getDrawStack();
        FrameKey frame = ctx.getFrame();

        if (visible.getValue(frame) != Boolean.TRUE)
        {
            return;
        }

        float curOpacity = Math.max(Math.min(
                opacity.getValue(frame), 1), 0);
        if (curOpacity == 0)
        {
            return;
        }

        RavenFilter curFilter = filter.getValue();

        CyMatrix4d xform = getLocalToParentTransform(
                frame, null);
        if (Math.abs(xform.m00) < EPSILON
                || Math.abs(xform.m11) < EPSILON)
        {
            return;
        }

//System.err.println("Spatial xform: " + xform);

        rend.pushFrame(null);
        rend.mulOpacity(curOpacity);

        rend.mulModelXform(new CyMatrix4d(xform));

        renderContent(ctx);

        rend.popFrame();
    }

    abstract protected void renderContent(RenderContext ctx);


    public RavenNodeRenderable pickObject(CyRectangle2d rectangle,
            CyMatrix4d parentToPick,
            Intersection intersection)
    {
        CyShape shape = getShapePickLocal();
        if (shape == null)
        {
            return null;
        }
        CyMatrix4d l2p = new CyMatrix4d();
        getLocalToParentTransform(l2p);
        l2p.mul(parentToPick, l2p);
        CyPath2d pickShape = shape.createTransformedPath(l2p);

        switch (intersection)
        {
            case INSIDE:
                return pickShape.contains(rectangle) ? this : null;
            case INTERSECTS:
                return pickShape.intersects(rectangle) ? this : null;
            case CONTAINS:
            default:
                return rectangle.contains(pickShape.getBounds()) ? this : null;
        }
    }

    public void pickObjects(CyRectangle2d rectangle,
            CyMatrix4d parentToPick,
            Intersection intersection, ArrayList<NodeObject> pickList)
    {
        if (pickObject(rectangle, parentToPick, intersection) != null)
        {
            pickList.add(this);
        }
    }

    public CyRectangle2d getBoundsLocal()
    {
        return getShapePickLocal().getBounds();
    }

    public CyRectangle2d getBoundsLocal(FrameKey frame)
    {
        return getShapePickLocal().getBounds();
    }

    public CyRectangle2d getBoundsWorld()
    {
        CyShape path = getShapePickLocal();
        return path.createTransformedBounds(
                getLocalToWorldTransform((CyMatrix4d)null));
    }

    abstract public CyShape getShapePickLocal();

    public CyPath2d getShapeWorld()
    {
        CyShape path = getShapePickLocal();
        return path.createTransformedPath(
                getLocalToWorldTransform((CyMatrix4d)null));
    }

    public boolean isVisible(FrameKey frame)
    {
        return visible.getValue(frame);
    }

    public float getOpacity(FrameKey frame)
    {
        return opacity.getValue(frame);
    }

    //------------------

    //-------------------
    @Deprecated
    public AffineTransform getLocalToParentTransform(AffineTransform result)
    {
        if (result == null)
        {
            return new AffineTransform();
        }
        result.setToIdentity();
        return result;
    }

    @Deprecated
    public AffineTransform getParentToWorldTransform(AffineTransform result)
    {
        if (result == null)
        {
            result = new AffineTransform();
        }

        ChildWrapper myParent = getParent();
        if (myParent == null)
        {
            //TODO: this is caused by an attempt to render a node that has
            // just been deleted.  We should really be rendering this
            // synchronized to user IO.
            return result;
        //    myParent = getParent();
        //    assert false;
        }

        NodeObject node = myParent.getNode();
        if (node instanceof RavenNodeXformable)
        {
            RavenNodeXformable parentNode = (RavenNodeXformable)node;
            return parentNode.getLocalToWorldTransform(result);
        }

        result.setToIdentity();
        return result;
    }

    @Deprecated
    public AffineTransform getLocalToWorldTransform(AffineTransform result)
    {
        if (result == null)
        {
            result = new AffineTransform();
        }

        AffineTransform p2w = getParentToWorldTransform(result);
        AffineTransform l2p = getLocalToParentTransform((AffineTransform)null);
        p2w.concatenate(l2p);
        return result;
    }

    @Deprecated
    public AffineTransform getLocalToDeviceTransform(AffineTransform result)
    {
        if (result == null)
        {
            result = new AffineTransform();
        }

        RavenNodeRoot doc = (RavenNodeRoot)getDocument();
        if (doc == null)
        {
            return getLocalToWorldTransform(result);
        }

        AffineTransform w2d = doc.getWorldToDeviceTransform(result);
        AffineTransform l2w = getLocalToWorldTransform((AffineTransform)null);

        w2d.concatenate(l2w);
        return result;
    }

//    @Deprecated
//    public AffineTransform getBoundingBoxToWorldTransform(AffineTransform xform)
//    {
//        if (xform == null)
//        {
//            xform = new AffineTransform();
//        }
//        getLocalToWorldTransform(xform);
//
//        Rectangle2D bounds = getBoundsLocal();
//        xform.translate(bounds.getX(), bounds.getY());
//        xform.scale(bounds.getWidth(), bounds.getHeight());
//
//        return xform;
//    }

    @Deprecated
    public void render(RavenRenderer renderer)
    {
        if (visible.getValue() != Boolean.TRUE)
        {
            return;
        }

        float curOpacity = Math.max(Math.min(opacity.getValue(), 1), 0);
        if (curOpacity == 0)
        {
            return;
        }

        RavenFilter curFilter = filter.getValue();

        AffineTransform xform = getLocalToParentTransform((AffineTransform)null);
        if (Math.abs(xform.getScaleX()) < EPSILON
                || Math.abs(xform.getScaleY()) < EPSILON)
        {
            return;
        }

//System.err.println("Spatial xform: " + xform);

        renderer.pushFrame(curFilter);
        renderer.mulOpacity(curOpacity);

        renderer.mulTransform(xform);

        renderContent(renderer);

        renderer.popFrame();
    }

    @Deprecated
    abstract protected void renderContent(RavenRenderer renderer);

}
