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
import com.kitfox.raven.editor.node.tools.common.ServiceText;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.text.Justify;
import com.kitfox.raven.util.text.TextFormatter2.LineSetToken;
import com.kitfox.raven.util.text.TextFormatterFont;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
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
public class RavenNodeText extends RavenNodeShape
        implements ServiceText
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

    private CyShape shape;

    protected RavenNodeText(int uid)
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
        if (shape == null)
        {
            Path2D.Double path = new Path2D.Double();

            Font cFont = font.getValue();
            Justify cJustify = justify.getValue();
            String cText = text.getValue();

            if (cFont == null || cText == null)
            {
                return null;
            }

            FontRenderContext frc = new FontRenderContext(
                    new AffineTransform(), false, true);

            TextFormatterFont formatter = new TextFormatterFont(cFont, frc);
            LineSetToken textLines = formatter.layout(cText, 0, cJustify);

            textLines.append(path);

            shape = CyPath2d.create(path);
        }

        return shape;
    }

//    @Override
//    public Shape getShapePickLocal()
//    {
//        if (shape == null)
//        {
//            Path2D.Double path = new Path2D.Double();
//
//            Font cFont = font.getValue();
//            Justify cJustify = justify.getValue();
//            String cText = text.getValue();
//
//            if (cFont == null || cText == null)
//            {
//                return null;
//            }
//
//            FontRenderContext frc = new FontRenderContext(
//                    new AffineTransform(), false, true);
//
//            TextFormatterFont formatter = new TextFormatterFont(cFont, frc);
//            LineSetToken textLines = formatter.layout(cText, 0, cJustify);
//
//            textLines.append(path);
//
//            shape = path;
//        }
//
//        return shape;
//    }



    /**
     * Get the value of justify
     *
     * @return the value of justify
     */
    public Justify getJustify()
    {
        return justify.getValue();
    }

    /**
     * Set the value of justify
     *
     * @param justify new value of justify
     */
    public void setJustify(Justify justify)
    {
        this.justify.setValue(justify);
    }

    /**
     * Get the value of text
     *
     * @return the value of text
     */
    public String getText()
    {
        return text.getValue();
    }

    /**
     * Set the value of text
     *
     * @param text new value of text
     */
    public void setText(String text)
    {
        this.text.setValue(text);
    }

    /**
     * Get the value of font
     *
     * @return the value of font
     */
    public Font getFont()
    {
        return font.getValue();
    }

    /**
     * Set the value of font
     *
     * @param font new value of font
     */
    public void setFont(Font font)
    {
        this.font.setValue(font);
    }

    @Override
    public Justify getTextJustify()
    {
        return justify.getValue();
    }

    @Override
    public void setTextJustify(Justify justify)
    {
        this.justify.setValue(justify);
    }

    @Override
    public Font getTextFont()
    {
        return font.getValue();
    }

    @Override
    public void setTextFont(Font font)
    {
        this.font.setValue(font);
    }

    
    //-----------------------------------------------
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeText>
    {
        public Provider()
        {
            super(RavenNodeText.class, "Text", "/icons/node/text.png");
        }

        @Override
        public RavenNodeText createNode(int uid)
        {
            return new RavenNodeText(uid);
        }
    }
}
