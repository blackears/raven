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

package com.kitfox.rabbit.render;

import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.style.FillRule;
import com.kitfox.rabbit.font.FontStyle;
import com.kitfox.rabbit.font.FontVariant;
import com.kitfox.rabbit.font.FontWeight;
import com.kitfox.rabbit.style.StrokeLineCap;
import com.kitfox.rabbit.style.StrokeLineJoin;
import com.kitfox.rabbit.style.Style;
import com.kitfox.rabbit.style.StyleKey;
import com.kitfox.rabbit.style.StylePaint;
import com.kitfox.rabbit.text.TextAlign;
import com.kitfox.rabbit.style.Visibility;
import com.kitfox.rabbit.text.Direction;
import com.kitfox.rabbit.text.TextAnchor;
import com.kitfox.rabbit.text.WritingMode;
import com.kitfox.rabbit.types.RaLength;
import com.kitfox.rabbit.types.ImageRef;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author kitfox
 */
abstract public class RabbitRenderer
{
    private final RabbitUniverse universe;
    private RabbitFrame curFrame;

    public RabbitRenderer(RabbitUniverse universe, Surface2D surf)
    {
        this.universe = universe;
        curFrame = new RabbitFrame(surf);
    }

    abstract public void render(Shape shape);
    abstract public void drawImage(ImageRef image, float x, float y, float width, float height);
    abstract public void drawSurface(Surface2D surface);


    /**
     * @return the curFrame
     */
    public RabbitFrame getCurFrame()
    {
        return curFrame;
    }

    public void pushFrame(RaElement source)
    {
        curFrame = new RabbitFrame(curFrame, source);
    }

    public void popFrame()
    {
        curFrame = curFrame.getLast();
    }

    public AffineTransform getTransform()
    {
        return new AffineTransform(curFrame.getXform());
    }

    public void setTransformToIdentity()
    {
        curFrame.getXform().setToIdentity();
    }

    public void mulTransform(AffineTransform xform)
    {
        curFrame.getXform().concatenate(xform);
    }

    public Visibility getVisiblity()
    {
        return curFrame.getVisibility();
    }

    public float getStrokeOpacity()
    {
        return curFrame.getStrokeOpacity();
    }

    public void applyStyles(Style style)
    {
        for (StyleKey key: style.getKeys())
        {
            switch (key)
            {
                case VISIBILITY:
                    curFrame.setVisibility((Visibility) style.get(key));
                    break;
                case OPACITY:
                    curFrame.setOpacity(curFrame.getOpacity() * (Float) style.get(key));
                    break;

                case FILL:
                    curFrame.setFillPaint((StylePaint) style.get(key));
                    break;
                case FILL_OPACITY:
                    curFrame.setFillOpacity((float) (Float)style.get(key));
                    break;
                case FILL_RULE:
                    curFrame.setFillRule((FillRule) style.get(key));
                    break;

                case FONT_FAMILY:
                    curFrame.setFontFamily((String[]) style.get(key));
                    break;
                case FONT_SIZE:
                    curFrame.setFontSize((RaLength)style.get(key));
                    break;
                case FONT_STRETCH:
                    break;
                case FONT_STYLE:
                    curFrame.setFontStyle((FontStyle) style.get(key));
                    break;
                case FONT_VARIANT:
                    curFrame.setFontVariant((FontVariant) style.get(key));
                    break;
                case FONT_WEIGHT:
                    curFrame.setFontWeight((FontWeight) style.get(key));
                    break;
                case TEXT_ALIGN:
                    curFrame.setTextAlign((TextAlign) style.get(key));
                    break;
                case TEXT_ANCHOR:
                    curFrame.setTextAnchor((TextAnchor) style.get(key));
                    break;
                case DIRECTION:
                    curFrame.setDirection((Direction) style.get(key));
                    break;
                case WRITING_MODE:
                    curFrame.setWritingMode((WritingMode) style.get(key));
                    break;

                case STROKE:
                    curFrame.setStrokePaint((StylePaint) style.get(key));
                    break;
                case STROKE_DASHARRAY:
                    curFrame.setStrokeDashArray((float[]) style.get(key));
                    break;
                case STROKE_DASHOFFSET:
                    curFrame.setStrokeDashOffset((float) (Float)style.get(key));
                    break;
                case STROKE_LINECAP:
                    curFrame.setStrokeLineCap((StrokeLineCap) style.get(key));
                    break;
                case STROKE_LINEJOIN:
                    curFrame.setStrokeLineJoin((StrokeLineJoin) style.get(key));
                    break;
                case STROKE_MITERLIMIT:
                    curFrame.setStrokeMiterLimit((float) (Float)style.get(key));
                    break;
                case STROKE_OPACITY:
                    curFrame.setStrokeOpacity((float) (Float)style.get(key));
                    break;
                case STROKE_WIDTH:
                    curFrame.setStrokeWidth((RaLength)style.get(key));
                    break;

                case STOP_COLOR:
                    curFrame.setStopColor((Color) style.get(key));
                    break;
                case STOP_OPACITY:
                    curFrame.setStopOpacity((float) (Float)style.get(key));
                    break;

            }
        }
    }

    /**
     * @return the universe
     */
    public RabbitUniverse getUniverse()
    {
        return universe;
    }

    public void translate(float x, float y)
    {
        curFrame.getXform().translate(x, y);
    }

    public void scale(float x, float y)
    {
        curFrame.getXform().scale(x, y);
    }

}
