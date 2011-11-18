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

package com.kitfox.rabbit.render.awt;

import com.kitfox.rabbit.nodes.RaGradient;
import com.kitfox.rabbit.render.RabbitFrame;
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.rabbit.render.Surface2D;
import com.kitfox.rabbit.style.StyleColor;
import com.kitfox.rabbit.style.StyleGradient;
import com.kitfox.rabbit.style.StylePaint;
import com.kitfox.rabbit.style.Visibility;
import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.types.ImageRef;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author kitfox
 */
public class Surface2DAwt extends Surface2D
{
    private final Graphics2D g;
    private final Rectangle2D bounds;

    public Surface2DAwt(Graphics2D g, Rectangle2D bounds)
    {
        this.g = g;
        this.bounds = bounds;
    }

    /**
     * @return the g
     */
    public Graphics2D getGraphics()
    {
        return g;
    }

    /**
     * @return the width
     */
    @Override
    public Rectangle2D getBounds()
    {
        return bounds.getBounds2D();
    }

    @Override
    public void render(RabbitRenderer renderer, Shape shape)
    {
        RabbitUniverse universe = renderer.getUniverse();
        RabbitFrame frame = renderer.getCurFrame();
        if (frame.getVisibility() != Visibility.VISIBLE)
        {
            return;
        }

        g.setTransform(frame.getXform());

        StylePaint fillPaint = frame.getFillPaint();
        if (fillPaint != null)
        {
            float opacity = frame.getFillOpacity() * frame.getOpacity();
            if (opacity > 0)
            {
                if (opacity >= 1)
                {
                    g.setComposite(AlphaComposite.SrcOver);
                }
                else
                {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                }

                if (fillPaint instanceof StyleColor)
                {
                    g.setColor(((StyleColor)fillPaint).getColor());
                    g.fill(shape);
                }
                else if (fillPaint instanceof StyleGradient)
                {
                    Paint paint = buildPaint(universe, 
                            frame.getSource().getBounds(renderer),
                            (StyleGradient)fillPaint);
                    g.setPaint(paint);
                    g.fill(shape);
                }
            }
        }

        StylePaint strokePaint = frame.getStrokePaint();
        if (strokePaint != null)
        {
            float opacity = frame.getStrokeOpacity() * frame.getOpacity();
            if (opacity > 0)
            {
                if (opacity >= 1)
                {
                    g.setComposite(AlphaComposite.SrcOver);
                }
                else
                {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                }

                if (strokePaint instanceof StyleColor)
                {
                    g.setColor(((StyleColor)strokePaint).getColor());
                    g.setStroke(frame.getStroke());
                    g.draw(shape);
                }
                else if (fillPaint instanceof StyleGradient)
                {
                    Paint paint = buildPaint(universe, 
                            frame.getSource().getBounds(renderer),
                            (StyleGradient)fillPaint);
                    g.setPaint(paint);
                    g.draw(shape);
                }
            }
        }

    }

    protected MultipleGradientPaint buildPaint(RabbitUniverse universe, Rectangle2D bounds, StyleGradient styleGrad)
    {
        ElementRef ref = styleGrad.getGradient();
        RaGradient grad = (RaGradient)universe.lookupElement(ref);
        return grad.getPaint(universe, bounds);
    }

    @Override
    public void render(RabbitRenderer renderer, ImageRef image, float x, float y, float width, float height)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Surface2DAwtImage createBlankSurface(Rectangle2D region)
    {
        return Surface2DAwtImage.create(g.getDeviceConfiguration(), region);
    }

    @Override
    public void dispose()
    {
        g.dispose();
    }

    @Override
    public void drawSurface(RabbitRenderer renderer, Surface2D surface)
    {
        RabbitFrame frame = renderer.getCurFrame();
        if (frame.getVisibility() != Visibility.VISIBLE)
        {
            return;
        }

        g.setTransform(frame.getXform());
        
        if (surface instanceof Surface2DAwtImage)
        {
            Surface2DAwtImage surf = (Surface2DAwtImage)surface;
            Rectangle2D surfBounds = surf.getBounds();

            AffineTransform xform = new AffineTransform();
            xform.translate(surfBounds.getX(), surfBounds.getY());
            g.drawImage(surf.getImage(), xform, null);
        }
    }

    /**
     * @return the g
     */
    public Graphics2D getG() {
        return g;
    }


}
