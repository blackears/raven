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

package com.kitfox.raven.editor.node.renderer;

import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.paint.RavenPaint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RavenRenderer
{
    final GraphicsConfiguration gc;
    Rectangle tileArea;
    private RavenRenderFrame curFrame;
    private final long startTime;
    private final long curTime;
    private final int curPass;

    public RavenRenderer(GraphicsConfiguration gc, 
            Rectangle tileArea,
            long startTime, long curTime, int curPass)
    {
        this(gc, tileArea, null, startTime, curTime, curPass);
    }

    public RavenRenderer(GraphicsConfiguration gc, 
            Rectangle tileArea, BufferedImage tileBuffer,
            long startTime, long curTime, int curPass)
    {
        this.gc = gc;
        this.tileArea = tileArea;
        this.startTime = startTime;
        this.curTime = curTime;
        this.curPass = curPass;

        if (tileBuffer != null
                && (tileBuffer.getWidth() != tileArea.width
                || tileBuffer.getHeight() != tileArea.height))
        {
            throw new IllegalArgumentException("tileArea and tileBuffer must have the same dimension");
        }
        curFrame = new RavenRenderFrame(this, tileArea, tileBuffer, null);
    }

    /**
     * Push the current state.  A new, blank rendering tile will be
     * allocated for all subsequent drawing operations.
     *
     * @param filter If not null, the tile will be processed by this
     * filter when it is popped.
     */
    public void pushFrame(RavenFilter filter)
    {
        curFrame = new RavenRenderFrame(curFrame, filter);
    }

    /**
     * Composites the tile built from the current frame over its parent.
     * Pops the frame stack so that drawing operations revert to the
     * parent tile.
     */
    public void popFrame()
    {
        RavenRenderFrame oldFrame = curFrame;
        //Deallocate frame and apply filter
        oldFrame.popping();
        curFrame = oldFrame.getParent();

//        curFrame.compositeTile(oldFrame);
    }

    public float getOpacity()
    {
        return curFrame.getOpacity();
    }

    public void setOpacity(float opacity)
    {
        curFrame.setOpacity(opacity);
    }

    public void mulOpacity(float opacity)
    {
        curFrame.mulOpacity(opacity);
    }

    public AffineTransform getTransform()
    {
        return new AffineTransform(curFrame.getXform());
    }

    public void setTransformToIdentity()
    {
        curFrame.setXformToIdentity();
    }

    public void mulTransform(AffineTransform xform)
    {
        curFrame.mulXform(xform);
    }

    public void setTransform(AffineTransform xform)
    {
        curFrame.setXform(xform);
    }

    public void setWorldToViewTransform(AffineTransform xform)
    {
        curFrame.setWorldToViewXform(xform);
    }

    /**
     * @return the fillPaint
     */
    public RavenPaint getPaint()
    {
        return curFrame.getPaint();
    }

    /**
     * @param paint the fillPaint to set
     */
    public void setPaint(RavenPaint paint)
    {
        curFrame.setPaint(paint);
    }

    /**
     * @return the fillPaint
     */
    public PaintLayout getPaintLayout()
    {
        return curFrame.getPaintLayout();
    }

    /**
     * @param layout the fillPaint to set
     */
    public void setPaintLayout(PaintLayout layout)
    {
        curFrame.setPaintLayout(layout);
    }

    /**
     * @return the stroke
     */
    public Stroke getStroke()
    {
        return curFrame.getStroke();
    }

    /**
     * @param stroke the stroke to set
     */
    public void setStroke(Stroke stroke)
    {
        curFrame.setStroke(stroke);
    }

    public void setAntialiased(boolean value)
    {
        curFrame.setAntialiased(value);
    }

    public void draw(Shape shape)
    {
        curFrame.draw(shape);
    }

    public void fill(Shape shape)
    {
        curFrame.fill(shape);
    }

    public void drawImage(BufferedImage img)
    {
        curFrame.drawImage(img);
    }

    public void drawImage(BufferedImage img,
            int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2)
    {
        curFrame.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    public void clear(Color color)
    {
        curFrame.clear(color);
    }

    public void dispose()
    {
        curFrame.popping();
    }
    static HashMap<Dimension, TileCache<RavenRenderFrame>> cacheMap =
            new HashMap<Dimension, TileCache<RavenRenderFrame>>();

    public BufferedImage allocTile(RavenRenderFrame owner, Rectangle area)
    {
        Dimension dim = area.getSize();
        TileCache<RavenRenderFrame> cache = cacheMap.get(dim);
        if (cache == null)
        {
            cache = new TileCache<RavenRenderFrame>(gc, area.width, area.height, true);
            cacheMap.put(dim, cache);
        }

        return cache.allocTile(owner);

//        return gc.createCompatibleImage(
//                area.width, area.height, Transparency.TRANSLUCENT);

    }

    /**
     *
     * @param localBounds
     * @return True if none of the area in localBounds will be
     * maps to the region being rendered by the tile.
     */
    public boolean isBoundsClipped(Rectangle2D localBounds)
    {
        return curFrame.isBoundsClipped(localBounds);
    }

    /**
     * @return the startTime
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @return the curTime
     */
    public long getCurTime()
    {
        return curTime;
    }

    /**
     * @return the curPass
     */
    public int getCurPass()
    {
        return curPass;
    }
}
