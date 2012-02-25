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

import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.editor.stroke.RavenStrokeBasic;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenNodeStroke extends RavenNode
        implements RavenStroke
{

//    public static final String PROP_WIDTH = "width";
//    public final PropertyWrapperFloat<RavenNodeStroke> width =
//            new PropertyWrapperFloat(this, PROP_WIDTH, 1);
//
//    public static final String PROP_JOIN = "join";
//    public final PropertyWrapper<RavenNodeStroke, Join> join =
//            new PropertyWrapper(
//            this, PROP_JOIN, Join.class, Join.ROUND);
//
//    public static final String PROP_CAP = "cap";
//    public final PropertyWrapper<RavenNodeStroke, Cap> cap =
//            new PropertyWrapper(
//            this, PROP_CAP, Cap.class, Cap.ROUND);
//
//    public static final String PROP_MITERLIMIT = "miterLimit";
//    public final PropertyWrapperFloat<RavenNodeStroke> miterLimit =
//            new PropertyWrapperFloat(this, PROP_MITERLIMIT, 4);
//
//    public static final String PROP_DASHES = "dashes";
//    public final PropertyWrapper<RavenNodeStroke, float[]> dashes =
//            new PropertyWrapper(
//            this, PROP_DASHES, float[].class);
//
//    public static final String PROP_DASHPHASE = "dashPhase";
//    public final PropertyWrapperFloat<RavenNodeStroke> dashPhase =
//            new PropertyWrapperFloat(this, PROP_DASHPHASE, 0);
//
//    RavenStrokeBasic ravenStroke;

    public static final String PROP_STROKE = "stroke";
    public final PropertyWrapper<RavenNodeStroke, RavenStrokeBasic> stroke =
            new PropertyWrapper(this, PROP_STROKE, RavenStrokeBasic.class,
            new RavenStrokeBasic());


    protected RavenNodeStroke(int uid)
    {
        super(uid);

//        PropertyWrapperAdapter adapt = new PropertyWrapperAdapter()
//        {
//            @Override
//            public void propertyWrapperDataChanged(PropertyChangeEvent evt) {
//                clearCache();
//            }
//        };
//
//        width.addPropertyWrapperListener(adapt);
//        join.addPropertyWrapperListener(adapt);
//        cap.addPropertyWrapperListener(adapt);
//        miterLimit.addPropertyWrapperListener(adapt);
//        dashes.addPropertyWrapperListener(adapt);
//        dashPhase.addPropertyWrapperListener(adapt);
    }


//    private void clearCache()
//    {
//        ravenStroke = null;
//    }

    @Override
    public Stroke getStroke()
    {
//        RavenStrokeBasic curStroke = getRavenStroke();
        RavenStrokeBasic curStroke = stroke.getValue();
        return curStroke.getStroke();
    }

    public RavenStrokeBasic getRavenStroke()
    {
//        if (ravenStroke == null)
//        {
//            ravenStroke = new RavenStrokeBasic(
//                    width.getValue(),
//                    cap.getValue(),
//                    join.getValue(),
//                    miterLimit.getValue(),
//                    dashes.getValue(),
//                    dashPhase.getValue());
//        }
        return stroke.getValue();
    }

    @Override
    public void drawPreview(Graphics2D g, Rectangle bounds)
    {
        RavenStrokeBasic curStroke = stroke.getValue();
        curStroke.drawPreview(g, bounds);
    }

//    protected float width;
//    public static final String PROP_WIDTH = "width";
//    protected float miterLimit;
//    public static final String PROP_MITERLIMIT = "miterLimit";
//    protected Join join;
//    public static final String PROP_JOIN = "join";
//    protected Cap cap;
//    public static final String PROP_CAP = "cap";
//    protected float[] dashes;
//    public static final String PROP_DASHES = "dashes";
//    protected RavenPaint paint;
//    public static final String PROP_PAINT = "paint";
//
//    public RavenNodeStroke()
//    {
//        this(1, 4, Join.ROUND, Cap.ROUND, null);
//    }
//
//
//    public RavenNodeStroke(float width, float miterLimit, Join join, Cap cap, float[] dashes)
//    {
//        this.width = width;
//        this.miterLimit = miterLimit;
//        this.join = join;
//        this.cap = cap;
//        this.dashes = dashes;
//    }
//
//    /**
//     * Get the value of paint
//     *
//     * @return the value of paint
//     */
//    public RavenPaint getPaint() {
//        return paint;
//    }
//
//    /**
//     * Set the value of paint
//     *
//     * @param paint new value of paint
//     */
//    public void setPaint(RavenPaint paint) {
//        RavenPaint oldPaint = this.paint;
//        this.paint = paint;
//        propertyChangeSupport.firePropertyChange(PROP_PAINT, oldPaint, paint);
//    }
//
//
//    /**
//     * Get the value of dashes
//     *
//     * @return the value of dashes
//     */
//    public float[] getDashes() {
//        return dashes;
//    }
//
//    /**
//     * Set the value of dashes
//     *
//     * @param dashes new value of dashes
//     */
//    public void setDashes(float[] dashes) {
//        float[] oldDashes = this.dashes;
//        this.dashes = dashes;
//        propertyChangeSupport.firePropertyChange(PROP_DASHES, oldDashes, dashes);
//    }
//
//
//    /**
//     * Get the value of cap
//     *
//     * @return the value of cap
//     */
//    public Cap getCap() {
//        return cap;
//    }
//
//    /**
//     * Set the value of cap
//     *
//     * @param cap new value of cap
//     */
//    public void setCap(Cap cap) {
//        Cap oldCap = this.cap;
//        this.cap = cap;
//        propertyChangeSupport.firePropertyChange(PROP_CAP, oldCap, cap);
//    }
//
//    /**
//     * Get the value of join
//     *
//     * @return the value of join
//     */
//    public Join getJoin() {
//        return join;
//    }
//
//    /**
//     * Set the value of join
//     *
//     * @param join new value of join
//     */
//    public void setJoin(Join join) {
//        Join oldJoin = this.join;
//        this.join = join;
//        propertyChangeSupport.firePropertyChange(PROP_JOIN, oldJoin, join);
//    }
//
//    /**
//     * Get the value of miterLimit
//     *
//     * @return the value of miterLimit
//     */
//    public float getMiterLimit() {
//        return miterLimit;
//    }
//
//    /**
//     * Set the value of miterLimit
//     *
//     * @param miterLimit new value of miterLimit
//     */
//    public void setMiterLimit(float miterLimit) {
//        float oldMiterLimit = this.miterLimit;
//        this.miterLimit = miterLimit;
//        propertyChangeSupport.firePropertyChange(PROP_MITERLIMIT, oldMiterLimit, miterLimit);
//    }
//
//    /**
//     * Get the value of width
//     *
//     * @return the value of width
//     */
//    public float getWidth() {
//        return width;
//    }
//
//    /**
//     * Set the value of width
//     *
//     * @param width new value of width
//     */
//    public void setWidth(float width) {
//        float oldWidth = this.width;
//        this.width = width;
//        propertyChangeSupport.firePropertyChange(PROP_WIDTH, oldWidth, width);
//    }

    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeStroke>
    {
        public Provider()
        {
            super(RavenNodeStroke.class, "Stroke", "/icons/node/stroke.png");
        }

        @Override
        public RavenNodeStroke createNode(int uid)
        {
            return new RavenNodeStroke(uid);
        }
    }
}
