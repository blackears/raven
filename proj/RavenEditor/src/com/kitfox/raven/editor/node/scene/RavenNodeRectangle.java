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
import com.kitfox.coyote.shape.CyEllipse2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyRectangleRound2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;

/**
 *
 * @author kitfox
 */
public class RavenNodeRectangle extends RavenNodeInscribedShape
{
    public static final String PROP_RX = "rx";
    public final PropertyWrapperFloat<RavenNodeRectangle> rx =
            new PropertyWrapperFloat(this, PROP_RX);

    public static final String PROP_RY = "ry";
    public final PropertyWrapperFloat<RavenNodeInscribedShape> ry =
            new PropertyWrapperFloat(this, PROP_RY);

    protected RavenNodeRectangle(int uid)
    {
        super(uid);

//        ShapeChanged listener = new ShapeChanged();
//        rx.addPropertyWrapperListener(listener);
//        ry.addPropertyWrapperListener(listener);
    }


//    @Override
//    protected Shape createShape()
//    {
//        float crx = rx.getValue();
//        float cry = ry.getValue();
//        float cWidth = width.getValue();
//        float cHeight = height.getValue();
//
//        if (crx == 0 && cry == 0)
//        {
//            return new Rectangle2D.Float(
//                    x.getValue() - cWidth / 2,
//                    y.getValue() - cHeight / 2,
//                    cWidth, cHeight);
//        }
//        return new RoundRectangle2D.Float(
//                x.getValue() - cWidth / 2,
//                y.getValue() - cHeight / 2,
//                cWidth, cHeight, crx, cry);
//    }

    @Override
    public CyShape createShapeLocal(FrameKey time)
    {
        NodeSymbol doc = getSymbol();
        float crx = rx.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cry = ry.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cx = x.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cy = y.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cWidth = width.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cHeight = height.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);

        if (crx == 0 && cry == 0)
        {
            return new CyRectangle2d(
                    cx, cy, cWidth, cHeight);
        }
        return new CyRectangleRound2d(
                cx, cy, cWidth, cHeight, crx, cry);
    }

//    protected float rx;
//    public static final String PROP_RX = "rx";
//    protected float ry;
//    public static final String PROP_RY = "ry";
//
//    /**
//     * Get the value of ry
//     *
//     * @return the value of ry
//     */
//    public float getRy() {
//        return ry;
//    }
//
//    /**
//     * Set the value of ry
//     *
//     * @param ry new value of ry
//     */
//    public void setRy(float ry) {
//        float oldRy = this.ry;
//        this.ry = ry;
//        propertyChangeSupport.firePropertyChange(PROP_RY, oldRy, ry);
//    }
//
//    /**
//     * Get the value of rx
//     *
//     * @return the value of rx
//     */
//    public float getRx() {
//        return rx;
//    }
//
//    /**
//     * Set the value of rx
//     *
//     * @param rx new value of rx
//     */
//    public void setRx(float rx) {
//        float oldRx = this.rx;
//        this.rx = rx;
//        propertyChangeSupport.firePropertyChange(PROP_RX, oldRx, rx);
//    }


//    @Override
//    public void getPropertySheet() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeRectangle>
    {
        public Provider()
        {
            super(RavenNodeRectangle.class, "Rectangle", "/icons/node/rectangle.png");
        }

        @Override
        public RavenNodeRectangle createNode(int uid)
        {
            return new RavenNodeRectangle(uid);
        }
    }
}
