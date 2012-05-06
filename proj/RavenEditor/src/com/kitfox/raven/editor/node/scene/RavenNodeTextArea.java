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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.text.Justify;
import com.kitfox.raven.util.text.TextFormatter2.LineSetToken;
import com.kitfox.raven.util.text.TextFormatterFont;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import com.kitfox.raven.util.tree.PropertyWrapperInteger;
import com.kitfox.raven.util.tree.PropertyWrapperString;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
public class RavenNodeTextArea extends RavenNodeShape
{
    public static final String PROP_FONT = "font";
    public final PropertyWrapper<RavenNodeText, Font> font =
            new PropertyWrapper(
            this, PROP_FONT, Font.class,
            new Font(Font.SERIF, Font.PLAIN, 12));

    public static final String PROP_TEXT = "text";
    public final PropertyWrapperString<RavenNodeText> text =
            new PropertyWrapperString(this, PROP_TEXT);

    public static final String PROP_JUSTIFY = "justify";
    public final PropertyWrapper<RavenNodeText, Justify> justify =
            new PropertyWrapper(
            this, PROP_JUSTIFY, Justify.class, Justify.LEFT);

//    public static final String PROP_X = "x";
//    public final PropertyWrapperFloat<RavenNodeTextArea> x =
//            new PropertyWrapperFloat(this, PROP_X);
//
//    public static final String PROP_Y = "y";
//    public final PropertyWrapperFloat<RavenNodeTextArea> y =
//            new PropertyWrapperFloat(this, PROP_Y);

    public static final String PROP_WIDTH = "width";
    public final PropertyWrapperFloat<RavenNodeTextArea> width =
            new PropertyWrapperFloat(this, PROP_WIDTH);

    public static final String PROP_HEIGHT = "height";
    public final PropertyWrapperFloat<RavenNodeTextArea> height =
            new PropertyWrapperFloat(this, PROP_HEIGHT);

    public static final String PROP_PAGE = "page";
    public final PropertyWrapperInteger<RavenNodeTextArea> page =
            new PropertyWrapperInteger(this, PROP_PAGE);

    private CyShape shape;

    protected RavenNodeTextArea(int uid)
    {
        super(uid);

        PropertyWrapperAdapter adapt = new PropertyWrapperAdapter()
        {
            @Override
            public void propertyWrapperDataChanged(PropertyChangeEvent evt) {
                clearCache();
            }
        };

        font.addPropertyWrapperListener(adapt);
        text.addPropertyWrapperListener(adapt);
        justify.addPropertyWrapperListener(adapt);
        width.addPropertyWrapperListener(adapt);
        height.addPropertyWrapperListener(adapt);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
        shape = null;
    }

    @Override
    public CyShape createShapeLocal(FrameKey time)
    {
        NodeSymbol doc = getSymbol();
        if (shape == null)
        {
            Path2D.Double path = new Path2D.Double();

            Font cFont = font.getValue();
            Justify cJustify = justify.getValue();
            String cText = text.getValue();
            float cWidth = width.getValue();

            if (cFont == null || cText == null)
            {
                return null;
            }

            FontRenderContext frc = new FontRenderContext(
                    new AffineTransform(), false, true);

            TextFormatterFont formatter = new TextFormatterFont(cFont, frc);
            LineSetToken textLines = formatter.layout(cText, cWidth, cJustify);

            textLines.append(path);
//            for (LineToken line: textLines)
//            {
//                line.append(path);
//            }
//            ArrayList<WordPos> wordList = formatter.layout(cText, cWidth, cJustify);
//
//            TextFontMetrics fm = new TextFontMetrics(cFont);
//            for (WordPos word: wordList)
//            {
//                GlyphVector glyphs = cFont.createGlyphVector(frc, word.getText());
//                Shape wordShape = glyphs.getOutline(word.getCursorX(),
//                        word.getLine() * fm.getHeight());
//                path.append(wordShape, false);
//            }

            shape = CyPath2d.create(path);
        }

        return shape;
    }


//    /**
//     * Get the value of page
//     *
//     * @return the value of page
//     */
//    public int getPage()
//    {
//        return page.getValue();
//    }
//
//    /**
//     * Set the value of page
//     *
//     * @param page new value of page
//     */
//    public void setPage(int page)
//    {
//        this.page.setValue(page);
//    }
//
//    /**
//     * Get the value of height
//     *
//     * @return the value of height
//     */
//    public float getHeight()
//    {
//        return height.getValue();
//    }
//
//    /**
//     * Set the value of height
//     *
//     * @param height new value of height
//     */
//    public void setHeight(float height)
//    {
//        this.height.setValue(height);
//    }
//
//    /**
//     * Get the value of width
//     *
//     * @return the value of width
//     */
//    public float getWidth()
//    {
//        return width.getValue();
//    }
//
//    /**
//     * Set the value of width
//     *
//     * @param width new value of width
//     */
//    public void setWidth(float width)
//    {
//        this.width.setValue(width);
//    }
//
//    /**
//     * Get the value of y
//     *
//     * @return the value of y
//     */
//    public float getY()
//    {
//        return y.getValue();
//    }
//
//    /**
//     * Set the value of y
//     *
//     * @param y new value of y
//     */
//    public void setY(float y)
//    {
//        this.y.setValue(y);
//    }
//
//    /**
//     * Get the value of x
//     *
//     * @return the value of x
//     */
//    public float getX()
//    {
//        return this.x.getValue();
//    }
//
//    /**
//     * Set the value of x
//     *
//     * @param x new value of x
//     */
//    public void setX(float x)
//    {
//        this.x.setValue(x);
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeTextArea>
    {
        public Provider()
        {
            super(RavenNodeTextArea.class, "Text Area", "/icons/node/textArea.png");
        }

        @Override
        public RavenNodeTextArea createNode(int uid)
        {
            return new RavenNodeTextArea(uid);
        }
    }
}
