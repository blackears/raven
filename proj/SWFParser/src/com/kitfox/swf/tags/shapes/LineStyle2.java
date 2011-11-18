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

import com.kitfox.swf.dataType.SWFDataReader;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class LineStyle2 extends LineStyle
{
    private Cap capStart;
    private Cap capEnd;
    private Join join;
    private boolean hasFill;
    private boolean noHScale;
    private boolean noVScale;
    private boolean pixelHinting;
    private boolean noClose;
    private float miterLimit;
    private FillStyle fill;

    public LineStyle2(SWFDataReader in, int shapeType) throws IOException
    {
        setWidth(in.getUI16());

        switch ((int)in.getUB(2))
        {
            case 0:
                capStart = Cap.ROUND;
                break;
            case 1:
                capStart = Cap.FLAT;
                break;
            case 2:
                capStart = Cap.SQUARE;
                break;
        }
        switch ((int)in.getUB(2))
        {
            case 0:
                join = Join.ROUND;
                break;
            case 1:
                join = Join.BEVEL;
                break;
            case 2:
                join = Join.MITER;
                break;
        }
        hasFill = in.getUB(1) != 0;
        noHScale = in.getUB(1) != 0;
        noVScale = in.getUB(1) != 0;
        pixelHinting = in.getUB(1) != 0;
        in.getUB(5);
        noClose = in.getUB(1) != 0;
        switch ((int)in.getUB(2))
        {
            case 0:
                capEnd = Cap.ROUND;
                break;
            case 1:
                capEnd = Cap.FLAT;
                break;
            case 2:
                capEnd = Cap.SQUARE;
                break;
        }
        in.flushToByteBoundary();

        if (join == Join.MITER)
        {
            miterLimit = in.getUI16();
        }

        if (!hasFill)
        {
            setColor(shapeType <= 2 ? in.getRGB().asColor() : in.getRGBA().asColor());
        }
        else
        {
            fill = new FillStyle(in, shapeType);
        }
    }

    @Override
    public BasicStroke createStroke()
    {
        int capStyle;
        switch (capStart)
        {
            case FLAT:
                capStyle = BasicStroke.CAP_BUTT;
                break;
            default:
            case ROUND:
                capStyle = BasicStroke.CAP_ROUND;
                break;
            case SQUARE:
                capStyle = BasicStroke.CAP_SQUARE;
                break;
        }

        int joinStyle;
        switch (join)
        {
            case BEVEL:
                joinStyle = BasicStroke.JOIN_BEVEL;
                break;
            case MITER:
                joinStyle = BasicStroke.JOIN_MITER;
                break;
            default:
            case ROUND:
                joinStyle = BasicStroke.JOIN_ROUND;
                break;
        }

        return new BasicStroke(getWidth(),capStyle, joinStyle, miterLimit);
    }

    @Override
    public Paint createPaint()
    {
        return hasFill ? fill.createPaint() : getColor();
    }

    /**
     * @return the capStart
     */
    public Cap getCapStart() {
        return capStart;
    }

    /**
     * @param capStart the capStart to set
     */
    public void setCapStart(Cap capStart) {
        this.capStart = capStart;
    }

    /**
     * @return the capEnd
     */
    public Cap getCapEnd() {
        return capEnd;
    }

    /**
     * @param capEnd the capEnd to set
     */
    public void setCapEnd(Cap capEnd) {
        this.capEnd = capEnd;
    }

    /**
     * @return the join
     */
    public Join getJoin() {
        return join;
    }

    /**
     * @param join the join to set
     */
    public void setJoin(Join join) {
        this.join = join;
    }

    /**
     * @return the hasFill
     */
    public boolean isHasFill() {
        return hasFill;
    }

    /**
     * @param hasFill the hasFill to set
     */
    public void setHasFill(boolean hasFill) {
        this.hasFill = hasFill;
    }

    /**
     * @return the noHScale
     */
    public boolean isNoHScale() {
        return noHScale;
    }

    /**
     * @param noHScale the noHScale to set
     */
    public void setNoHScale(boolean noHScale) {
        this.noHScale = noHScale;
    }

    /**
     * @return the noVScale
     */
    public boolean isNoVScale() {
        return noVScale;
    }

    /**
     * @param noVScale the noVScale to set
     */
    public void setNoVScale(boolean noVScale) {
        this.noVScale = noVScale;
    }

    /**
     * @return the pixelHinting
     */
    public boolean isPixelHinting() {
        return pixelHinting;
    }

    /**
     * @param pixelHinting the pixelHinting to set
     */
    public void setPixelHinting(boolean pixelHinting) {
        this.pixelHinting = pixelHinting;
    }

    /**
     * @return the noClose
     */
    public boolean isNoClose() {
        return noClose;
    }

    /**
     * @param noClose the noClose to set
     */
    public void setNoClose(boolean noClose) {
        this.noClose = noClose;
    }

    /**
     * @return the miterLimit
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * @param miterLimit the miterLimit to set
     */
    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }

    /**
     * @return the fill
     */
    public FillStyle getFill() {
        return fill;
    }

    /**
     * @param fill the fill to set
     */
    public void setFill(FillStyle fill) {
        this.fill = fill;
    }

    //---------------------------
    public static enum Cap { ROUND, SQUARE, FLAT }
    public static enum Join { ROUND, BEVEL, MITER }
}
