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

import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.rabbit.style.StyleKey;
import com.kitfox.rabbit.types.ElementRef;
import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 *
 * @author kitfox
 */
abstract public class RaGradient extends RaElement
{
    private GradientUnits gradientUnits = GradientUnits.OBJECT_BOUNDING_BOX;
    private AffineTransform gradientTransform = new AffineTransform();
    private SpreadMethod spreadMethod;
    private ElementRef href;

    float[] stopFractions;
    Color[] stopColors;

    abstract public MultipleGradientPaint getPaint(RabbitUniverse universe, Rectangle2D bounds);

    protected void buildStops()
    {
        stopColors = new Color[getNumChildren()];
        stopFractions = new float[getNumChildren()];

        for (int i = 0; i < getNumChildren(); ++i)
        {
            RaElement ele = getChild(i);
            RaStop stop = (RaStop)ele;
            stopFractions[i] = stop.getOffset();

            Color stopColor = (Color)stop.getStyle().get(StyleKey.STOP_COLOR);
            float stopOpacity = (float)(Float)stop.getStyle().get(StyleKey.STOP_OPACITY);
            if (stopOpacity < 1)
            {
                int alpha = (int)(stopOpacity * 255 + .5);
                int col = (stopColor.getRGB() & 0xffffff) | (alpha << 24);
                stopColor = new Color(col);
            }
            stopColors[i] = stopColor;
        }
    }

    public float[] getStopFractions(RabbitUniverse universe)
    {
        if (href != null)
        {
            RaGradient grad = (RaGradient)universe.lookupElement(href);
            return grad.getStopFractions(universe);
        }

        if (stopFractions == null)
        {
            buildStops();
        }
        return stopFractions;
    }

    public Color[] getStopColors(RabbitUniverse universe)
    {
        if (href != null)
        {
            RaGradient grad = (RaGradient)universe.lookupElement(href);
            return grad.getStopColors(universe);
        }

        if (stopColors == null)
        {
            buildStops();
        }
        return stopColors;
    }

    public void clearStops()
    {
        stopColors = null;
        stopFractions = null;
    }

    @Override
    public void addChild(RaElement child)
    {
        super.addChild(child);
        clearStops();
    }

    @Override
    public void addChildren(Collection<RaElement> list)
    {
        super.addChildren(list);
        clearStops();
    }


    /**
     * @return the spreadMethod
     */
    public SpreadMethod getSpreadMethod() {
        return spreadMethod;
    }

    /**
     * @param spreadMethod the spreadMethod to set
     */
    public void setSpreadMethod(SpreadMethod spreadMethod) {
        this.spreadMethod = spreadMethod;
    }

    /**
     * @return the href
     */
    public ElementRef getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(ElementRef href) {
        this.href = href;
    }

    /**
     * @return the gradientUnits
     */
    public GradientUnits getGradientUnits() {
        return gradientUnits;
    }

    /**
     * @param gradientUnits the gradientUnits to set
     */
    public void setGradientUnits(GradientUnits gradientUnits) {
        this.gradientUnits = gradientUnits;
    }

    /**
     * @return the gradientTransform
     */
    public AffineTransform getGradientTransform() {
        return gradientTransform;
    }

    /**
     * @param gradientTransform the gradientTransform to set
     */
    public void setGradientTransform(AffineTransform gradientTransform) {
        this.gradientTransform = gradientTransform;
    }
}
