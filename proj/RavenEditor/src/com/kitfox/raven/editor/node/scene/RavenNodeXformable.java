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
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.node.tools.common.ServiceTransformable;
import com.kitfox.raven.util.tree.EventWrapper;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
abstract public class RavenNodeXformable extends RavenNodeRenderable
        implements ServiceTransformable
{
    public static final String PROP_TRANSX = "transX";
    public final PropertyWrapperFloat<RavenNodeXformable> transX =
            new PropertyWrapperFloat(this, PROP_TRANSX);

    public static final String PROP_TRANSY = "transY";
    public final PropertyWrapperFloat<RavenNodeXformable> transY =
            new PropertyWrapperFloat(this, PROP_TRANSY);

//    public static final String PROP_TRANSZ = "transZ";
//    public final PropertyWrapperFloat<RavenNodeSpatial> transZ =
//            new PropertyWrapperFloat(this, PROP_TRANSZ);

    public static final String PROP_ROTATION = "rotation";
    public final PropertyWrapperFloat<RavenNodeXformable> rotation =
            new PropertyWrapperFloat(this, PROP_ROTATION);

    public static final String PROP_SKEWANGLE = "skewAngle";
    public final PropertyWrapperFloat<RavenNodeXformable> skewAngle =
            new PropertyWrapperFloat(this, PROP_SKEWANGLE, 90);

    public static final String PROP_SCALEX = "scaleX";
    public final PropertyWrapperFloat<RavenNodeXformable> scaleX =
            new PropertyWrapperFloat(this, PROP_SCALEX, 1);

    public static final String PROP_SCALEY = "scaleY";
    public final PropertyWrapperFloat<RavenNodeXformable> scaleY =
            new PropertyWrapperFloat(this, PROP_SCALEY, 1);

    public static final String PROP_PIVOTX = "pivotX";
    public final PropertyWrapperFloat<RavenNodeXformable> pivotX =
            new PropertyWrapperFloat(this, PROP_PIVOTX);

    public static final String PROP_PIVOTY = "pivotY";
    public final PropertyWrapperFloat<RavenNodeXformable> pivotY =
            new PropertyWrapperFloat(this, PROP_PIVOTY);



    public static final String EVENT_PICK = "onPick";
    public final EventWrapper<RavenNodeXformable, EventObject> onPick =
            new EventWrapper(this, EVENT_PICK, EventObject.class);

    public static final String EVENT_KEY_TYPED = "onKeyTyped";
    public final EventWrapper<RavenNodeXformable, EventObject> onKeyTyped =
            new EventWrapper(this, EVENT_KEY_TYPED, EventObject.class);

    Rectangle2D boundsLocal;
    PropertyWrapperAdapter clearCache;

    public RavenNodeXformable(int uid)
    {
        super(uid);

        clearCache = new PropertyWrapperAdapter() {
            @Override
            public void propertyWrapperDataChanged(PropertyChangeEvent evt) {
                clearCache();
            }
        };
    }

    protected void clearCache()
    {
        boundsLocal = null;
    }

    @Override
    public CyMatrix4d getLocalToParentTransform(CyMatrix4d result)
    {
        return getLocalToParentTransform(FrameKey.DIRECT, result);
    }

    @Override
    public CyMatrix4d getLocalToParentTransform(FrameKey frame, CyMatrix4d result)
    {
        if (result == null)
        {
            result = CyMatrix4d.createIdentity();
        }

        float tx = transX.getValue(frame);
        float ty = transY.getValue(frame);
        float rot = rotation.getValue(frame);
        float skew = skewAngle.getValue(frame);
        float sx = scaleX.getValue(frame);
        float sy = scaleY.getValue(frame);
        float px = pivotX.getValue(frame);
        float py = pivotY.getValue(frame);

        if (tx != 0 || ty != 0
                || rot != 0 || skew != 90
                || sx != 1 || sy != 1
                || px != 0 || py != 0)
        {
            double sinx = Math.sin(Math.toRadians(rot));
            double cosx = Math.cos(Math.toRadians(rot));
            double siny = Math.sin(Math.toRadians(rot + skew));
            double cosy = Math.cos(Math.toRadians(rot + skew));

//            result.setTransform(cosx * curScaleX, sinx * curScaleX,
//                    cosy * curScaleY, siny * curScaleY,
//                    curTransX, curTransY);

            double rs00 = cosx * sx;
            double rs10 = sinx * sx;
            double rs01 = cosy * sy;
            double rs11 = siny * sy;
            result.set(
                    rs00, rs10, 0, 0,
                    rs01, rs11, 0, 0,
                    0, 0, 1, 0,
                    tx + px - px * rs00 - py * rs01,
                    ty + py - px * rs10 - py * rs11, 0, 1);

            return result;
        }

        result.setIdentity();
        return result;
//        return new AffineTransform();
    }

//    public CyMatrix4d getBoundingBoxToWorldTransform(CyMatrix4d xform)
//    {
//        if (xform == null)
//        {
//            xform = CyMatrix4d.createIdentity();
//        }
//        getLocalToWorldTransform(xform);
//
//        CyRectangle2d bounds = getBoundsLocal();
//        xform.translate(bounds.getX(), bounds.getY(), 0);
//        xform.scale(bounds.getWidth(), bounds.getHeight(), 1);
//
//        return xform;
//    }

    @Override
    public CyMatrix4d getLocalToWorldTransform(CyMatrix4d result)
    {
        return getLocalToWorldTransform(FrameKey.DIRECT, result);
    }

    @Override
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


    @Deprecated
    @Override
    public AffineTransform getLocalToParentTransform(AffineTransform result)
    {
        if (result == null)
        {
            result = new AffineTransform();
        }

        float tx = transX.getValue();
        float ty = transY.getValue();
        float rot = rotation.getValue();
        float skew = skewAngle.getValue();
        float sx = scaleX.getValue();
        float sy = scaleY.getValue();
        float px = pivotX.getValue();
        float py = pivotY.getValue();

        if (tx != 0 || ty != 0
                || rot != 0 || skew != 90
                || sx != 1 || sy != 1
                || px != 0 || py != 0)
        {
            double sinx = Math.sin(Math.toRadians(rot));
            double cosx = Math.cos(Math.toRadians(rot));
            double siny = Math.sin(Math.toRadians(rot + skew));
            double cosy = Math.cos(Math.toRadians(rot + skew));

//            result.setTransform(cosx * curScaleX, sinx * curScaleX,
//                    cosy * curScaleY, siny * curScaleY,
//                    curTransX, curTransY);

            double rs00 = cosx * sx;
            double rs10 = sinx * sx;
            double rs01 = cosy * sy;
            double rs11 = siny * sy;
            result.setTransform(
                    rs00, rs10,
                    rs01, rs11,
                    tx + px - px * rs00 - py * rs01,
                    ty + py - px * rs10 - py * rs11);

            return result;
        }

        result.setToIdentity();
        return result;
    }

    @Override
    public void setLocalToParentTransform(CyMatrix4d newLocal, CyVector2d pivot, boolean history)
    {
        double m00 = newLocal.m00;
        double m10 = newLocal.m10;
        double m01 = newLocal.m01;
        double m11 = newLocal.m11;
        double m03 = newLocal.m03;
        double m13 = newLocal.m13;
        double px = pivot.getX();
        double py = pivot.getY();

        double sx = Math.sqrt(m00 * m00 + m10 * m10);
        double sy = Math.sqrt(m01 * m01 + m11 * m11);
        double rot = Math.toDegrees(Math.atan2(m10, m00));
        double rotSkew = Math.toDegrees(Math.atan2(m11, m01)) - rot;

        double tx = m03 - px + px * m00 + py * m01;
        double ty = m13 - py + px * m10 + py * m11;

        NodeSymbol doc = getSymbol();
        if (history && doc != null)
        {
            doc.getHistory().beginTransaction("Set transform");
        }

        transX.setValue((float)tx, history);
        transY.setValue((float)ty, history);
        rotation.setValue((float)rot, history);
        skewAngle.setValue((float)rotSkew, history);
        scaleX.setValue((float)sx, history);
        scaleY.setValue((float)sy, history);
        pivotX.setValue((float)px, history);
        pivotY.setValue((float)py, history);

        if (history && doc != null)
        {
            doc.getHistory().commitTransaction();
        }
    }

    public Point2D.Float getPivot()
    {
        return new Point2D.Float(pivotX.getValue(), pivotY.getValue());
    }

    public void centerPivot(boolean history)
    {
        NodeSymbol doc = getSymbol();
        if (history && doc != null)
        {
            doc.getHistory().beginTransaction("Center pivot");
        }

        CyRectangle2d bounds = getBoundsLocal(FrameKey.DIRECT);
        pivotX.setValue((float)bounds.getCenterX(), history);
        pivotY.setValue((float)bounds.getCenterY(), history);

        if (history && doc != null)
        {
            doc.getHistory().commitTransaction();
        }
    }

    @Override
    public NodeObject getNodeObject()
    {
        return this;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getTransX()
    {
        return transX;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getTransY()
    {
        return transY;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getRotation()
    {
        return rotation;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getSkewAngle()
    {
        return skewAngle;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getScaleX()
    {
        return scaleX;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getScaleY()
    {
        return scaleY;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getPivotX()
    {
        return pivotX;
    }

    @Override
    public PropertyWrapperFloat<RavenNodeXformable> getPivotY()
    {
        return pivotY;
    }
}
