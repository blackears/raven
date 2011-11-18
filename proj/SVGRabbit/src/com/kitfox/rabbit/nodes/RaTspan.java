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

import com.kitfox.rabbit.font.FontShape;
import com.kitfox.rabbit.font.Glyph;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.render.RabbitFrame;
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.rabbit.text.GlyphLayout;
import com.kitfox.rabbit.text.TextChunkBuilder;
import com.kitfox.rabbit.types.RaLength;
import com.kitfox.raven.util.service.ServiceInst;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaTspan extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaTspan>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("tspan");
        }

        @Override
        public RaTspan create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaTspan haNode = new RaTspan();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloatArr(attr.get("x"), null));
            haNode.setY(parseFloatArr(attr.get("y"), null));
            haNode.setDx(parseFloatArr(attr.get("dx"), null));
            haNode.setDy(parseFloatArr(attr.get("dy"), null));
            haNode.setRotate(parseFloatArr(attr.get("rotate"), null));

            haNode.addChildren(nodes);


            return haNode;

        }
    }

    private float[] x;
    private float[] y;
    private float[] dx;
    private float[] dy;
    private float[] rotate;


    void layoutText(RabbitRenderer renderer, TextChunkBuilder builder)
    {
        renderer.pushFrame(this);
        renderer.applyStyles(getStyle());

        RabbitFrame frame = renderer.getCurFrame();
        RabbitUniverse uni = renderer.getUniverse();
        FontShape font = uni.getFont(frame.getFontFamily(), frame.getFontStyle(), frame.getFontVariant(), frame.getFontWeight());

        if (x != null && x.length != 0
                && y != null && y.length != 0)
        {
            builder.startChunk(x[0], y[0], frame.getDirection(), frame.getWritingMode(), frame.getTextAlign(), frame.getTextAnchor());
        }

        float ascent = font.getFontFace().getAscent();
        float descent = font.getFontFace().getDescent();
        float fontHeight = ascent - descent;
        RaLength size = frame.getFontSize();
        float glyphScale = size.getValue() / fontHeight;

        for (int i = 0; i < getNumChildren(); ++i)
        {
            RaElement ele = getChild(i);
            if (ele instanceof RaString)
            {
                ArrayList<Glyph> glyphs = font.toGlyphs(((RaString)ele).getText());
                builder.addGlyphs(new GlyphLayout(glyphs,
                        glyphScale, frame));
            }
            else if (ele instanceof RaTspan)
            {
                ((RaTspan)ele).layoutText(renderer, builder);
            }
        }

        renderer.popFrame();
    }

    /**
     * @return the x
     */
    public float[] getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float[] x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float[] getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float[] y) {
        this.y = y;
    }

    /**
     * @return the dx
     */
    public float[] getDx() {
        return dx;
    }

    /**
     * @param dx the dx to set
     */
    public void setDx(float[] dx) {
        this.dx = dx;
    }

    /**
     * @return the dy
     */
    public float[] getDy() {
        return dy;
    }

    /**
     * @param dy the dy to set
     */
    public void setDy(float[] dy) {
        this.dy = dy;
    }

    /**
     * @return the rotate
     */
    public float[] getRotate() {
        return rotate;
    }

    /**
     * @param rotate the rotate to set
     */
    public void setRotate(float[] rotate) {
        this.rotate = rotate;
    }
}
