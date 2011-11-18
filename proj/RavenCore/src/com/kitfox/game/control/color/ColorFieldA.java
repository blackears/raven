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

package com.kitfox.game.control.color;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
public class ColorFieldA extends ColorModelField1D
{
    private float hue;
    private float bright;
    private float sat;
    private float[] hsv = new float[3];
    private float[] rgb = new float[3];

    public ColorFieldA(ColorChooserModel model)
    {
        super(model);
    }

    @Override
    public ColorStyle toColor(float x, float y)
    {
        float alpha = isHorizontal() ? x : y;

        ColorStyle.HSVtoRGB(hue, sat, bright, rgb);
        return new ColorStyle(rgb[0], rgb[1], rgb[2], alpha);
    }

    @Override
    public ColorStyle toDisplayColor(float x, float y)
    {
        float alpha = isHorizontal() ? x : y;

        ColorStyle.HSVtoRGB(hue, sat, bright, rgb);
        return new ColorStyle(rgb[0], rgb[1], rgb[2], alpha);
    }

    @Override
    public Point2D.Float toCoords(ColorStyle color)
    {
        if (color == null)
        {
            return new Point2D.Float();
        }

        return new Point2D.Float(color.a, color.a);
    }

    /**
     * @return the bright
     */
    public float getHue()
    {
        return hue;
    }

    /**
     * @param hue the hue to set
     */
    public void setHue(float hue)
    {
        this.hue = hue;
        fireModelChanged();
    }

    /**
     * @return the bright
     */
    public float getBright()
    {
        return bright;
    }

    /**
     * @param bright the hue to set
     */
    public void setBright(float bright)
    {
        this.bright = bright;
        fireModelChanged();
    }

    /**
     * @return the sat
     */
    public float getSat()
    {
        return sat;
    }

    /**
     * @param sat the sat to set
     */
    public void setSat(float sat)
    {
        this.sat = sat;
        fireModelChanged();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getSource() == model && ColorChooserModel.PROP_COLOR.equals(evt.getPropertyName()))
        {
            ColorStyle color = model.getColor();
            if (color == null)
            {
                color = new ColorStyle();
            }
            ColorStyle.RGBtoHSV(color.r, color.g, color.b, hsv);
            setHue(hsv[0]);
            setSat(hsv[1]);
            setBright(hsv[2]);
        }
    }

}
