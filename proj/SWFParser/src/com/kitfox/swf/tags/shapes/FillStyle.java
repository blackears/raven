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
import java.awt.Paint;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class FillStyle
{
    private Type fillStyleType = null;
    private Color color = null;
    private MATRIX gradMtx = null;
    private Gradient grad = null;
    private int bitmapId = 0;
    private MATRIX bitmapMtx = null;

    public FillStyle(SWFDataReader in, int shapeType) throws IOException
    {
        int type = in.getUI8();
        switch (type)
        {
            case 0x0:
                fillStyleType = Type.SOLID;
                color = shapeType <= 2 ? in.getRGB().asColor() : in.getRGBA().asColor();
                break;
            case 0x10:
                fillStyleType = Type.GRAD_LINEAR;
                gradMtx = in.getMATRIX();
                grad = new Gradient(in, shapeType);
                break;
            case 0x12:
                fillStyleType = Type.GRAD_RADIAL;
                gradMtx = in.getMATRIX();
                grad = new Gradient(in, shapeType);
                break;
            case 0x13:
                fillStyleType = Type.GRAD_RADIAL_FOCAL;
                gradMtx = in.getMATRIX();
                grad = new GradientFocal(in, shapeType);
                break;
            case 0x40:
                fillStyleType = Type.BITMAP_REPEAT;
                bitmapId = in.getUI16();
                bitmapMtx = in.getMATRIX();
                break;
            case 0x41:
                fillStyleType = Type.BITMAP_CLIPPED;
                bitmapId = in.getUI16();
                bitmapMtx = in.getMATRIX();
                break;
            case 0x42:
                fillStyleType = Type.BITMAP_REPEAT_NONSMOOTH;
                bitmapId = in.getUI16();
                bitmapMtx = in.getMATRIX();
                break;
            case 0x43:
                fillStyleType = Type.BITMAP_CLIPPED_NONSMOOTH;
                bitmapId = in.getUI16();
                bitmapMtx = in.getMATRIX();
                break;
        }
    }

    public Paint createPaint()
    {
        switch (fillStyleType)
        {
            case SOLID:
                return color;
            case GRAD_LINEAR:
                return grad.createLinearPaint(gradMtx);
            case GRAD_RADIAL:
            case GRAD_RADIAL_FOCAL:
                return grad.createRadialPaint(gradMtx);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    /**
     * @return the fillStyleType
     */
    public Type getFillStyleType() {
        return fillStyleType;
    }

    /**
     * @param fillStyleType the fillStyleType to set
     */
    public void setFillStyleType(Type fillStyleType) {
        this.fillStyleType = fillStyleType;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the gradMtx
     */
    public MATRIX getGradMtx() {
        return gradMtx;
    }

    /**
     * @param gradMtx the gradMtx to set
     */
    public void setGradMtx(MATRIX gradMtx) {
        this.gradMtx = gradMtx;
    }

    /**
     * @return the grad
     */
    public Gradient getGrad() {
        return grad;
    }

    /**
     * @param grad the grad to set
     */
    public void setGrad(Gradient grad) {
        this.grad = grad;
    }

    /**
     * @return the bitmapId
     */
    public int getBitmapId() {
        return bitmapId;
    }

    /**
     * @param bitmapId the bitmapId to set
     */
    public void setBitmapId(int bitmapId) {
        this.bitmapId = bitmapId;
    }

    /**
     * @return the bitmapMtx
     */
    public MATRIX getBitmapMtx() {
        return bitmapMtx;
    }

    /**
     * @param bitmapMtx the bitmapMtx to set
     */
    public void setBitmapMtx(MATRIX bitmapMtx) {
        this.bitmapMtx = bitmapMtx;
    }

    //--------------------------------
    public static enum Type {
        SOLID,
        GRAD_LINEAR,
        GRAD_RADIAL,
        GRAD_RADIAL_FOCAL,
        BITMAP_REPEAT,
        BITMAP_CLIPPED,
        BITMAP_REPEAT_NONSMOOTH,
        BITMAP_CLIPPED_NONSMOOTH
    };
}
