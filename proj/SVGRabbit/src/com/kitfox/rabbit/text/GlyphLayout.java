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

package com.kitfox.rabbit.text;

import com.kitfox.rabbit.font.Glyph;
import com.kitfox.rabbit.render.RabbitFrame;
import com.kitfox.rabbit.style.Style;
import com.kitfox.rabbit.style.StyleKey;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class GlyphLayout
{
    ArrayList<Glyph> glyphs;

    private final float scale;

    final Style style;
//    float opacity;
//    StylePaint fillPaint;
//    float fillOpacity;
//    StylePaint strokePaint;
//    float strokeOpacity;
//    BasicStroke stroke;

    public GlyphLayout(ArrayList<Glyph> glyph, float scale, RabbitFrame frame)
    {
        Style style = new Style();

        style.put(StyleKey.OPACITY, frame.getOpacity());
        style.put(StyleKey.FILL, frame.getFillPaint());
        style.put(StyleKey.FILL_OPACITY, frame.getFillOpacity());
        style.put(StyleKey.FILL_RULE, frame.getFillRule());
        style.put(StyleKey.STROKE, frame.getStrokePaint());
        style.put(StyleKey.STROKE_OPACITY, frame.getStrokeOpacity());
        style.put(StyleKey.STROKE_WIDTH, frame.getStrokeWidth());
        style.put(StyleKey.STROKE_DASHARRAY, frame.getStrokeDashArray());
        style.put(StyleKey.STROKE_DASHOFFSET, frame.getStrokeDashOffset());
        style.put(StyleKey.STROKE_LINECAP, frame.getStrokeLineCap());
        style.put(StyleKey.STROKE_LINEJOIN, frame.getStrokeLineJoin());
        style.put(StyleKey.STROKE_MITERLIMIT, frame.getStrokeMiterLimit());

        this.glyphs = glyph;
        this.scale = scale;
        this.style = style;
//        this.opacity = opacity;
//        this.fillPaint = fillPaint;
//        this.fillOpacity = fillOpacity;
//        this.strokePaint = strokePaint;
//        this.strokeOpacity = strokeOpacity;
//        this.stroke = stroke;
    }

    public int getNumGlyphs()
    {
        return glyphs.size();
    }

    public Glyph getGlyph(int index)
    {
        return glyphs.get(index);
    }

    public Style getStyle()
    {
        return style;
    }

    public float getHorizAdvX()
    {
        float width = 0;
        for (int i = 0; i < glyphs.size(); ++i)
        {
            width += glyphs.get(i).getHorixAdvX();
        }
        return width * scale;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }




}
