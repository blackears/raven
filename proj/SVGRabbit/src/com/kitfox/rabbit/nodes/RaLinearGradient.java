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
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaLinearGradient extends RaGradient
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaLinearGradient>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("linearGradient");
        }

        @Override
        public RaLinearGradient create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaLinearGradient haNode = new RaLinearGradient();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX1(parseFloat(attr.get("x1"), 0));
            haNode.setY1(parseFloat(attr.get("y1"), 0));
            haNode.setX2(parseFloat(attr.get("x2"), 0));
            haNode.setY2(parseFloat(attr.get("y2"), 0));
            haNode.setGradientUnits(parseGradientUnits(attr.get("gradientUnits")));
            haNode.setGradientTransform(parseTransform(attr.get("gradientTransform"), null));
            haNode.setSpreadMethod(parseSpreadMethod(attr.get("spreadMethod")));
            haNode.setHref(parseElementRef(attr.get("href"), builder));

            haNode.addChildren(nodes);

            return haNode;
        }
    }
    
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    @Override
    public LinearGradientPaint getPaint(RabbitUniverse universe, Rectangle2D bounds)
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
            return new LinearGradientPaint(
                new Point2D.Float(x1, y1),
                new Point2D.Float(x2, y2),
                getStopFractions(universe),
                getStopColors(universe),
                method,
                MultipleGradientPaint.ColorSpaceType.SRGB,
                getGradientTransform());
        }
        else
        {
//            AffineTransform viewXform = new AffineTransform();
//            viewXform.translate(bounds.x, bounds.y);
//
//            //This is a hack to get around shapes that have a width or height of 0.  Should be close enough to the true answer.
//            float width = bounds.width;
//            float height = bounds.height;
//            if (width == 0) width = 1;
//            if (height == 0) height = 1;
//            viewXform.scale(width, height);

            AffineTransform viewXform = new AffineTransform(
                    bounds.getWidth(), 0,
                    0, bounds.getHeight(),
                    bounds.getX(), bounds.getY());

            AffineTransform gradXform = getGradientTransform();
            if (gradXform != null)
            {
                viewXform.concatenate(getGradientTransform());
            }

            return new LinearGradientPaint(
                new Point2D.Float(x1, y1),
                new Point2D.Float(x2, y2),
                getStopFractions(universe),
                getStopColors(universe),
                method,
                MultipleGradientPaint.ColorSpaceType.SRGB,
                viewXform);
        }
    }

    /**
     * @return the x1
     */
    public float getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(float x1) {
        this.x1 = x1;
    }

    /**
     * @return the y1
     */
    public float getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(float y1) {
        this.y1 = y1;
    }

    /**
     * @return the x2
     */
    public float getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(float x2) {
        this.x2 = x2;
    }

    /**
     * @return the y2
     */
    public float getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(float y2) {
        this.y2 = y2;
    }
}
