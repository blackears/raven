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

import com.kitfox.raven.filter.RavenFilter;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutTexture;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenRenderFrame
{
    private final RavenRenderFrame parent;
    private final RavenRenderer renderer;
    private final BufferedImage tileBuffer;
    private final BufferedImage tileBufferPreFilter;
    private Rectangle tileAreaPostFilter;
    private Rectangle tileArea;
    private final RavenFilter filter;
    Graphics2D g;

    private AffineTransform localToWorldXform;
    private AffineTransform localToTileXform = new AffineTransform();
    private AffineTransform viewToTileXform;
    private AffineTransform worldToViewXform;
    private float opacity;
    private boolean antialiased;

    private RavenPaint paint;
    private PaintLayout paintLayout;
    private Stroke stroke;

//    public RavenRenderFrame(RavenRenderer renderer, Rectangle tileArea)
//    {
//        this(renderer, tileArea, null);
//    }

    /**
     * Called by RavenRenderer the when the first frame is allocated
     * 
     * @param renderer
     * @param tileArea
     * @param tileBuffer
     */
    public RavenRenderFrame(RavenRenderer renderer, Rectangle tileArea, 
            BufferedImage tileBuffer, RavenFilter filter)
    {
        this.parent = null;
        this.renderer = renderer;
        this.tileArea = tileArea;
        this.tileBuffer = tileBuffer;
        this.filter = filter;
        if (filter == null)
        {
            this.tileBufferPreFilter = null;
            this.tileAreaPostFilter = tileArea;
            this.g = tileBuffer.createGraphics();
        }
        else
        {
            this.tileAreaPostFilter = filter.calcSampleRegion(tileArea, null);
            this.tileBufferPreFilter = allocTile(tileAreaPostFilter);
            this.g = tileBufferPreFilter.createGraphics();
        }


        //Defaults
        localToWorldXform = new AffineTransform();
        worldToViewXform = new AffineTransform();
        opacity = 1;
        this.antialiased = true;

        paint = new RavenPaintColor(Color.BLACK);
        paintLayout = new PaintLayoutTexture();
        stroke = new BasicStroke();

        viewToTileXform = new AffineTransform();
        viewToTileXform.setToTranslation(-tileArea.x, -tileArea.y);

        updateAntialiasing();
        updateLocalXform();
    }

    /**
     * Called by RavenRenderer in response to pushing a frame
     *
     * @param frame
     * @param filter
     */
    RavenRenderFrame(RavenRenderFrame frame, RavenFilter filter)
    {
        this.parent = frame;
        this.renderer = frame.renderer;
        this.tileBuffer = frame.tileBuffer;
        this.tileArea = frame.tileArea;
        this.filter = filter;
        if (filter == null)
        {
            this.tileAreaPostFilter = null;
            this.tileBufferPreFilter = null;
//            this.g = tileBuffer.createGraphics();
            this.g = frame.g;
        }
        else
        {
            this.tileAreaPostFilter = filter.calcSampleRegion(tileArea, null);
            this.tileBufferPreFilter = allocTile(tileAreaPostFilter);
            this.g = tileBufferPreFilter.createGraphics();
        }

        localToWorldXform = new AffineTransform(frame.localToWorldXform);
        worldToViewXform = new AffineTransform(frame.worldToViewXform);
        opacity = frame.opacity;
        this.antialiased = frame.antialiased;

        paint = frame.paint;
        paintLayout = frame.paintLayout;
        stroke = frame.stroke;

        viewToTileXform = new AffineTransform();
        viewToTileXform.setToTranslation(-tileArea.x, -tileArea.y);

        updateAntialiasing();
        updateLocalXform();
    }

    private BufferedImage allocTile(Rectangle area)
    {
        return renderer.gc.createCompatibleImage(
                area.width, area.height, Transparency.TRANSLUCENT);
    }

    public void popping()
    {
        if (filter != null || parent == null)
        {
            //Non filtered, non root frames will borrow the Graphics2D from the
            // parent frame
            g.dispose();
        }

        if (filter != null)
        {
            filter.apply(tileBufferPreFilter, tileArea, tileBuffer);
        }
    }

    private void updateLocalXform()
    {
        localToTileXform.setTransform(viewToTileXform);
        localToTileXform.concatenate(worldToViewXform);
        localToTileXform.concatenate(localToWorldXform);
        g.setTransform(localToTileXform);
    }
    
    private void updateAntialiasing()
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                antialiased ? RenderingHints.VALUE_ANTIALIAS_ON
                : RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * @return the xform
     */
    public AffineTransform getXform()
    {
        return localToWorldXform;
    }

    /**
     * @param xform the xform to set
     */
    public void mulXform(AffineTransform xform)
    {
        this.localToWorldXform.concatenate(xform);
        updateLocalXform();
    }

    /**
     * @param xform the xform to set
     */
    public void setXform(AffineTransform xform)
    {
        this.localToWorldXform.setTransform(xform);
        updateLocalXform();
    }

    /**
     * @param xform the xform to set
     */
    public void setXformToIdentity()
    {
        this.localToWorldXform.setToIdentity();
        updateLocalXform();
    }

    public void setWorldToViewXform(AffineTransform xform)
    {
        this.worldToViewXform.setTransform(xform);
        updateLocalXform();
    }

    /**
     * @return the opacity
     */
    public float getOpacity()
    {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(float opacity)
    {
        this.opacity = opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void mulOpacity(float opacity)
    {
        this.opacity *= opacity;
    }

    public void setAntialiased(boolean value)
    {
        this.antialiased = value;
        updateAntialiasing();
    }

    /**
     * @return the fillPaint
     */
    public RavenPaint getPaint()
    {
        return paint;
    }

    /**
     * @param fillPaint the fillPaint to set
     */
    public void setPaint(RavenPaint paint)
    {
        this.paint = paint;
    }

    /**
     * @return the stroke
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * @param stroke the stroke to set
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void draw(Shape shape)
    {
        if (stroke == null || paint == null)
        {
            return;
        }

        g.setStroke(stroke);

        g.setTransform(viewToTileXform);
        g.transform(worldToViewXform);

        Paint worldPaint = paint.getPaint(paintLayout, localToWorldXform);
        g.setPaint(worldPaint);
//        g.transform(localToWorldXform);
//        g.setPaint(paint);
//        g.draw(shape);

        Shape worldShape = localToWorldXform.createTransformedShape(shape);
        g.draw(worldShape);
    }

    public void fill(Shape shape)
    {
        if (paint == null)
        {
            return;
        }


        g.setTransform(viewToTileXform);
        g.transform(worldToViewXform);

        Paint worldPaint = paint.getPaint(paintLayout, localToWorldXform);
        g.setPaint(worldPaint);
//        g.transform(localToWorldXform);
//        g.setPaint(paint);
//        g.fill(shape);
        
        Shape worldShape = localToWorldXform.createTransformedShape(shape);
        g.fill(worldShape);
    }

    public void clear(Color color)
    {
        g.setTransform(new AffineTransform());
        g.setColor(color);
        g.fillRect(0, 0, tileBuffer.getWidth(), tileBuffer.getHeight());
        updateLocalXform();
    }

    public void drawImage(BufferedImage img)
    {
        g.drawImage(img, 0, 0, null);
    }

    public void drawImage(BufferedImage img,
            int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2)
    {
        g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    /**
     * @return the parent
     */
    public RavenRenderFrame getParent() {
        return parent;
    }

    /**
     * @return the tileBufferPostFilter
     */
    public BufferedImage getTileBufferPostFilter() {
        return tileBufferPreFilter;
    }

//    void compositeTile(RavenRenderFrame childFrame)
//    {
//        //Used by popping routine.
//        float curOpacity = Math.max(Math.min(childFrame.opacity, 1), 0);
//        if (curOpacity == 0)
//        {
//            return;
//        }
//
//        g.setTransform(new AffineTransform());
//        if (curOpacity == 1)
//        {
//            g.drawImage(childFrame.tileBufferPreFilter, 0, 0, null);
//        }
//        else
//        {
//            AlphaComposite comp =
//                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
//            g.setComposite(comp);
//            g.drawImage(childFrame.tileBufferPreFilter, 0, 0, null);
//            g.setComposite(AlphaComposite.SrcOver);
//        }
//        updateLocalXform();
//    }

    /**
     * @return the antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
    }

    /**
     * @return the paintLayout
     */
    public PaintLayout getPaintLayout()
    {
        return paintLayout;
    }

    /**
     * @param paintLayout the paintLayout to set
     */
    public void setPaintLayout(PaintLayout paintLayout)
    {
        this.paintLayout = paintLayout;
    }

    public boolean isBoundsClipped(Rectangle2D localBounds)
    {
        if (localBounds == null)
        {
            return true;
        }

        Shape bounds = localToTileXform.createTransformedShape(localBounds);
        return !bounds.intersects(0, 0, tileArea.width, tileArea.height);
    }
}
