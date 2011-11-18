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

package com.kitfox.coyote.text.awt;

import com.kitfox.coyote.text.GlyphVisitor;
import com.kitfox.coyote.text.TextFormatter;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TextFormatterFont extends TextFormatter<Shape>
{
    final Font font;
    final FontRenderContext frc;
    int height;
    int ascent;

    public TextFormatterFont(Font font, FontRenderContext frc)
    {
        this.font = font;
        this.frc = frc;

        TextFontMetrics fm = new TextFontMetrics(font);
        this.height = fm.getHeight();
        this.ascent = fm.getAscent();
    }

    @Override
    public int getLineHeight()
    {
        return height;
    }

    @Override
    public int getLineAscent()
    {
        return ascent;
    }

    @Override
    protected GlyphInfo<Shape> createGlyph(char ch)
    {
        GlyphVector gv = font.createGlyphVector(frc, "" + ch);
        Shape shape = gv.getGlyphOutline(0);
        GlyphMetrics gm = gv.getGlyphMetrics(0);

        return new GlyphInfo(ch, shape, gm.getAdvance());
    }

    public static Path2D.Double createTextOutline(ArrayList<LineToken<Shape>> textLines)
    {
        final Path2D.Double textShape = new Path2D.Double();
        final AffineTransform xform = new AffineTransform();

        GlyphVisitor<Shape> visitor = new GlyphVisitor<Shape>() {

            @Override
            public void visit(float x, int y, Shape shape)
            {
                xform.setToTranslation(x, y);
                textShape.append(shape.getPathIterator(xform), true);
            }
        };

        for (LineToken<Shape> line: textLines)
        {
            line.visit(visitor);
        }

        return textShape;
    }

}
