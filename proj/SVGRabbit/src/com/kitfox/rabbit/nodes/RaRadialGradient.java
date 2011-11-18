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

package com.kitfox.rabbit.nodes;

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaRadialGradient extends RaGradient
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaRadialGradient>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("radialGradient");
        }

        @Override
        public RaRadialGradient create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaRadialGradient haNode = new RaRadialGradient();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setCx(parseFloat(attr.get("cx"), 0));
            haNode.setCy(parseFloat(attr.get("cy"), 0));
            haNode.setR(parseFloat(attr.get("r"), 0));
            haNode.setFx(parseFloat(attr.get("fx"), 0));
            haNode.setFy(parseFloat(attr.get("fy"), 0));
            haNode.setGradientUnits(parseGradientUnits(attr.get("gradientUnits")));
            haNode.setGradientTransform(parseTransform(attr.get("gradientTransform"), null));
            haNode.setSpreadMethod(parseSpreadMethod(attr.get("spreadMethod")));
            haNode.setHref(parseElementRef(attr.get("href"), builder));

            haNode.addChildren(nodes);

            return haNode;

        }
    }
    private float cx;
    private float cy;
    private float r;
    private float fx;
    private float fy;


    @Override
    public RadialGradientPaint getPaint(RabbitUniverse universe, Rectangle2D bounds)
    {
        MultipleGradientPaint.CycleMethod method;
        switch (getSpreadMethod())
        {
            default:
            case PAD:
                method = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                break;
            case REPEAT:
                method = MultipleGradientPaint.CycleMethod.REPEAT;
                break;
            case REFLECT:
                method = MultipleGradientPaint.CycleMethod.REFLECT;
                break;
        }

        if (getGradientUnits() == GradientUnits.USER_SPACE_ON_USE)
        {
            return new RadialGradientPaint(
                new Point2D.Float(cx, cy),
                r,
                new Point2D.Float(fx, fy),
                getStopFractions(universe),
                getStopColors(universe),
                method,
                MultipleGradientPaint.ColorSpaceType.SRGB,
                getGradientTransform());
        }
        else
        {
//            AffineTransform viewXform = new AffineTransform();
//            viewXform.translate(bounds.getX(), bounds.getY());
//            viewXform.scale(bounds.getWidth(), bounds.getHeight());
//
//            viewXform.concatenate(gradientTransform);

            AffineTransform viewXform = new AffineTransform(
                    bounds.getWidth(), 0,
                    0, bounds.getHeight(),
                    bounds.getX(), bounds.getY());

            AffineTransform gradXform = getGradientTransform();
            if (gradXform != null)
            {
                viewXform.concatenate(getGradientTransform());
            }

            return new RadialGradientPaint(
                new Point2D.Float(cx, cy),
                r,
                new Point2D.Float(fx, fy),
                getStopFractions(universe),
                getStopColors(universe),
                method,
                MultipleGradientPaint.ColorSpaceType.SRGB,
                viewXform);
        }
    }

    /**
     * @return the cx
     */
    public float getCx() {
        return cx;
    }

    /**
     * @param cx the cx to set
     */
    public void setCx(float cx) {
        this.cx = cx;
    }

    /**
     * @return the cy
     */
    public float getCy() {
        return cy;
    }

    /**
     * @param cy the cy to set
     */
    public void setCy(float cy) {
        this.cy = cy;
    }

    /**
     * @return the r
     */
    public float getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(float r) {
        this.r = r;
    }

    /**
     * @return the fx
     */
    public float getFx() {
        return fx;
    }

    /**
     * @param fx the fx to set
     */
    public void setFx(float fx) {
        this.fx = fx;
    }

    /**
     * @return the fy
     */
    public float getFy() {
        return fy;
    }

    /**
     * @param fy the fy to set
     */
    public void setFy(float fy) {
        this.fy = fy;
    }
    
}
