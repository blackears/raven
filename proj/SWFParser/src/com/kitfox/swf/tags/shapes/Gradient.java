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

package com.kitfox.swf.tags.shapes;

import com.kitfox.swf.dataType.MATRIX;
import com.kitfox.swf.dataType.SWFDataReader;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class Gradient
{
    Spread spreadMode;
    ColorSpace colorSpace;
    GradientRecord[] stops;

    public Gradient(SWFDataReader in, int shapeType) throws IOException
    {
        switch ((int)in.getUB(2))
        {
            case 0:
                spreadMode = Spread.PAD;
                break;
            case 1:
                spreadMode = Spread.REFLECT;
                break;
            case 2:
                spreadMode = Spread.REPEAT;
                break;
        }

        switch ((int)in.getUB(2))
        {
            case 0:
                colorSpace = ColorSpace.SRGB;
                break;
            case 1:
                colorSpace = ColorSpace.LINEAR;
                break;
        }

        int numGrads = (int)in.getUB(4);
        stops = new GradientRecord[numGrads];
        for (int i = 0; i < numGrads; ++i)
        {
            stops[i] = new GradientRecord(in, shapeType);
        }
    }

    public float[] getFractions()
    {
        float[] frac = new float[stops.length];
        for (int i = 0; i < stops.length; ++i)
        {
            frac[i] = stops[i].ratio / 255f;
        }
        return frac;
    }

    public Color[] getColors()
    {
        Color[] colors = new Color[stops.length];
        for (int i = 0; i < stops.length; ++i)
        {
            colors[i] = stops[i].color;
        }
        return colors;
    }

    public CycleMethod getCycleMethodAwt()
    {
        switch (spreadMode)
        {
            default:
            case PAD:
                return CycleMethod.NO_CYCLE;
            case REFLECT:
                return CycleMethod.REFLECT;
            case REPEAT:
                return CycleMethod.REPEAT;
        }
    }

    public ColorSpaceType getColorSpace()
    {
        switch (colorSpace)
        {
            default:
            case SRGB:
                return ColorSpaceType.SRGB;
            case LINEAR:
                return ColorSpaceType.LINEAR_RGB;
        }
    }

    public LinearGradientPaint createLinearPaint(MATRIX gradMtx)
    {
        //All gradients are defined in a standard space called the gradient square. The gradient square is
        // centered at (0,0), and extends from (-16384,-16384) to (16384,16384).

        return new LinearGradientPaint(
                new Point2D.Float(-16384, 0), new Point2D.Float(16384, 0),
                getFractions(), getColors(), getCycleMethodAwt(),
                getColorSpace(), gradMtx.asAffineTransform());
    }

    public RadialGradientPaint createRadialPaint(MATRIX gradMtx)
    {
        return createRadialPaint(gradMtx, new Point2D.Float());
    }

    protected RadialGradientPaint createRadialPaint(MATRIX gradMtx, Point2D focus)
    {
        //All gradients are defined in a standard space called the gradient square. The gradient square is
        // centered at (0,0), and extends from (-16384,-16384) to (16384,16384).

        return new RadialGradientPaint(
                new Point2D.Float(0, 0), 16384, focus,
                getFractions(), getColors(), getCycleMethodAwt(),
                getColorSpace(), gradMtx.asAffineTransform());
    }

    //-----------------------------
    
    static enum Spread { PAD, REFLECT, REPEAT }
    static enum ColorSpace { SRGB, LINEAR }
}
