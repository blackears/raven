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

package com.kitfox.coyote.text.bitmap.awt;

import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.CyGLWrapper.DataType;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.CyGLWrapper.TexTarget;
import com.kitfox.coyote.renderer.jogl.TexSourceAWTBufferedImage;
import com.kitfox.coyote.text.bitmap.BitmapFont;
import com.kitfox.coyote.text.bitmap.GlyphDef;
import com.kitfox.coyote.text.bitmap.GlyphSymbolTable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BitmapFontBuilder
{
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
    private int margin = 2;
//    private boolean antiAliased = false;
//    private Paint fillPaint = Color.WHITE;
    private Color outlineColor = Color.BLACK;
//    private Paint fillPaint = Color.WHITE;
    private Color color = Color.WHITE;
    private Color background;

    public BitmapFontBuilder()
    {
    }

    public Data build(Collection<Character> characters)
    {
        char[] arr = new char[characters.size()];
        int idx = 0;
        for (Character ch : characters)
        {
            arr[idx++] = ch;
        }
        return build(arr);
    }

    public Data build(char[] characters)
    {
        if (characters == null || characters.length == 0)
        {
            characters = new char[]{' '};
        }

        AffineTransform xform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(xform, false, false);

        HashMap<Character, GlyphPlacement> charMap = new HashMap<Character, GlyphPlacement>();

        //Find glyphs
        Rectangle allBounds = null;
        int xpos = 0;
        for (char ch : characters)
        {
            if (charMap.containsKey(ch))
            {
                continue;
            }

            GlyphVector vec = getFont().createGlyphVector(frc, new char[]
                    {
                        ch
                    });

            Rectangle pixBounds = vec.getPixelBounds(frc, 0, 0);
            allBounds = allBounds == null ? pixBounds : pixBounds.union(allBounds);

            Rectangle2D logicBounds = vec.getLogicalBounds();
            float advance = (float) logicBounds.getWidth();

            int xoffset = xpos - pixBounds.x + getMargin();
            xpos += pixBounds.width + getMargin() * 2;

            GlyphPlacement place = new GlyphPlacement(ch, vec, xoffset, pixBounds, advance);
            charMap.put(ch, place);
        }

        //Draw background
        final int plateWidth = xpos;
        final int plateHeight = allBounds.height + getMargin() * 2;
//        BufferedImage compositePlate = new BufferedImage(plateWidth, plateHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g = compositePlate.createGraphics();
//        if (background != null)
//        {
//            g.setColor(background);
//            g.fillRect(0, 0, compositePlate.getWidth(), compositePlate.getHeight());
//        }
//        g.dispose();

//        g.setStroke(getStroke());

        //Place glyphs
        BufferedImage glyphPlate = new BufferedImage(plateWidth, plateHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = glyphPlate.createGraphics();
        g.setPaint(color);

        HashMap<Character, GlyphDef> glyphs = new HashMap<Character, GlyphDef>();

        for (GlyphPlacement place : charMap.values())
        {
            int xorigin = place.x;
            int yorigin = -allBounds.y + getMargin();

            glyphs.put(place.ch, new GlyphDef(
                    place.pixBounds.x + xorigin - margin,
                    place.pixBounds.y + yorigin - margin,
                    place.pixBounds.width + margin * 2,
                    place.pixBounds.height + margin * 2,
                    xorigin,
                    yorigin,
                    (int) place.advance));


            //Draw to image
            g.drawGlyphVector(place.vec, xorigin, yorigin);
        }

        g.dispose();


        //Caclculate shadow
        BufferedImage glyphPlateShadow = new BufferedImage(plateWidth, plateHeight, BufferedImage.TYPE_INT_ARGB);
        if (outlineColor != null)
        {
            int rgb = outlineColor.getRGB();

//            Graphics2D gg = glyphPlateShadow.createGraphics();
            WritableRaster raster = glyphPlate.getRaster();

            for (int j = 0; j < plateHeight; ++j)
            {
                for (int i = 0; i < plateWidth; ++i)
                {
//                    if (raster.getSample(i, j, 3) == 0
//                            && nextToPixel(i, j, raster))
                    if (nextToPixel(i, j, raster))
                    {
                        glyphPlateShadow.setRGB(i, j, rgb);
                    }
                }
            }

//            g.drawImage(glyphPlateShadow, 0, 0, null);
        }
//
//        if (background != null)
//        {
//            g.setColor(background);
//            g.fillRect(0, 0, plateWidth, plateHeight);
//        }


//try {
//    ImageIO.write(glyphPlate, "png", new File("glyphs.png"));
//} catch (IOException ex) {
//    Logger.getLogger(BitmapFont.class.getName()).log(Level.SEVERE, null, ex);
//}

        LineMetrics lineMet = font.getLineMetrics(characters, 0, characters.length, frc);
        return new Data(
                new GlyphSymbolTable(glyphs, (int)lineMet.getHeight(), (int)lineMet.getAscent()),
                glyphPlate, glyphPlateShadow,
                lineMet.getHeight(), lineMet.getAscent());
    }

    private static boolean nextToPixel(int i, int j, Raster raster)
    {
        if (i > 0 && raster.getSample(i - 1, j, 3) != 0)
        {
            return true;
        }
        if (i < raster.getWidth() - 1 && raster.getSample(i + 1, j, 3) != 0)
        {
            return true;
        }
        if (j > 0 && raster.getSample(i, j - 1, 3) != 0)
        {
            return true;
        }
        if (j < raster.getHeight() - 1 && raster.getSample(i, j + 1, 3) != 0)
        {
            return true;
        }
        return false;
    }

    /**
     * @return the background
     */
    public Color getBackground()
    {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Color background)
    {
        this.background = background;
    }

    /**
     * @return the font
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * @return the margin
     */
    public int getMargin()
    {
        return margin;
    }

    /**
     * @param margin the margin to set
     */
    public void setMargin(int margin)
    {
        this.margin = margin;
    }

//    /**
//     * @return the stroke
//     */
//    public Stroke getStroke() {
//        return stroke;
//    }
//
//    /**
//     * @param stroke the stroke to set
//     */
//    public void setStroke(Stroke stroke) {
//        this.stroke = stroke;
//    }
//
//    /**
//     * @return the outline
//     */
//    public boolean isOutline() {
//        return outline;
//    }
//
//    /**
//     * @param outline the outline to set
//     */
//    public void setOutline(boolean outline) {
//        this.outline = outline;
//    }
//
//    /**
//     * @return the dropShadowPaint
//     */
//    public Paint getDropShadowPaint() {
//        return dropShadowPaint;
//    }
//
//    /**
//     * @param dropShadowPaint the dropShadowPaint to set
//     */
//    public void setDropShadowPaint(Paint dropShadowPaint) {
//        this.dropShadowPaint = dropShadowPaint;
//    }
//
//    /**
//     * @return the dropShadowOffset
//     */
//    public Point getDropShadowOffset() {
//        return new Point(dropShadowOffset);
//    }
//
//    /**
//     * @param dropShadowOffset the dropShadowOffset to set
//     */
//    public void setDropShadowOffset(Point dropShadowOffset) {
//        this.dropShadowOffset.setLocation(dropShadowOffset);
//    }
//
//    /**
//     * @return the dropShadow
//     */
//    public boolean isDropShadow() {
//        return dropShadow;
//    }
//
//    /**
//     * @param dropShadow the dropShadow to set
//     */
//    public void setDropShadow(boolean dropShadow) {
//        this.dropShadow = dropShadow;
//    }
//
//    /**
//     * @return the dropShadowJitterOffset
//     */
//    public Point getDropShadowJitterOffset() {
//        return new Point(dropShadowJitterOffset);
//    }
//
//    /**
//     * @param dropShadowJitterOffset the dropShadowJitterOffset to set
//     */
//    public void setDropShadowJitterOffset(Point dropShadowJitterOffset) {
//        this.dropShadowJitterOffset.setLocation(dropShadowJitterOffset);
//    }
//
//    /**
//     * @return the dropShadowJitter
//     */
//    public boolean isDropShadowJitter() {
//        return dropShadowJitter;
//    }
//
//    /**
//     * @param dropShadowJitter the dropShadowJitter to set
//     */
//    public void setDropShadowJitter(boolean dropShadowJitter) {
//        this.dropShadowJitter = dropShadowJitter;
//    }
//
//    /**
//     * @return the dropShadowAlpha
//     */
//    public float getDropShadowAlpha() {
//        return dropShadowAlpha;
//    }
//
//    /**
//     * @param dropShadowAlpha the dropShadowAlpha to set
//     */
//    public void setDropShadowAlpha(float dropShadowAlpha) {
//        this.dropShadowAlpha = dropShadowAlpha;
//    }
    /**
     * @return the outlineColor
     */
    public Color getOutlineColor()
    {
        return outlineColor;
    }

    /**
     * @param outlineColor the outlineColor to set
     */
    public void setOutlineColor(Color outlineColor)
    {
        this.outlineColor = outlineColor;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    //---------------------------------
    public class Data
    {
        private final GlyphSymbolTable symbols;
        private final BufferedImage glyphPlate;
        private final BufferedImage glyphShadowPlate;
        private final float lineHeight;
        private final float lineAscent;

        public Data(GlyphSymbolTable symbols,
                BufferedImage glyphPlate, BufferedImage glyphShadowPlate,
                float lineHeight, float lineAscent)
        {
            this.symbols = symbols;
            this.glyphPlate = glyphPlate;
            this.glyphShadowPlate = glyphShadowPlate;
            this.lineHeight = lineHeight;
            this.lineAscent = lineAscent;
        }

        public BitmapFont buildFont()
        {
            CyTextureImage imgGlyph = new CyTextureImage(
                    TexTarget.GL_TEXTURE_2D,
                    InternalFormatTex.GL_RGBA,
                    DataType.GL_UNSIGNED_BYTE,
                    glyphPlate.getWidth(), glyphPlate.getHeight(),
                    CyTransparency.TRANSLUCENT,
                    new TexSourceAWTBufferedImage(glyphPlate));

            CyTextureImage imgGlyphShadow = new CyTextureImage(
                    TexTarget.GL_TEXTURE_2D,
                    InternalFormatTex.GL_RGBA,
                    DataType.GL_UNSIGNED_BYTE,
                    glyphShadowPlate.getWidth(), glyphShadowPlate.getHeight(),
                    CyTransparency.TRANSLUCENT,
                    new TexSourceAWTBufferedImage(glyphShadowPlate));

            return new BitmapFont(symbols, imgGlyph, imgGlyphShadow, (int)lineHeight, (int)lineAscent);
        }

        /**
         * @return the symbols
         */
        public GlyphSymbolTable getSymbols()
        {
            return symbols;
        }

        /**
         * @return the glyphPlate
         */
        public BufferedImage getGlyphPlate()
        {
            return glyphPlate;
        }

        /**
         * @return the glyphShadowPlate
         */
        public BufferedImage getGlyphShadowPlate()
        {
            return glyphShadowPlate;
        }

        /**
         * @return the lineHeight
         */
        public float getLineHeight()
        {
            return lineHeight;
        }

        /**
         * @return the lineAscent
         */
        public float getLineAscent()
        {
            return lineAscent;
        }
    }

    class GlyphPlacement
    {

        final char ch;
        GlyphVector vec;
        int x;
        Rectangle pixBounds;
        float advance;

        GlyphPlacement(char ch, GlyphVector vec, int x, Rectangle pixBounds, float advance)
        {
            this.ch = ch;
            this.vec = vec;
            this.x = x;
            this.pixBounds = pixBounds;
            this.advance = advance;
        }
    }
}
