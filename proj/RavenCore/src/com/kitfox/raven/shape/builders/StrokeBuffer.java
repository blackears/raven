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

package com.kitfox.raven.shape.builders;

import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.shape.builders.BitmapOutliner.Sampler;
import com.kitfox.raven.util.Grid;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author kitfox
 */
public class StrokeBuffer
        implements Sampler
{
    final int gridCellWidth;
    final int gridCellHeight;
    Grid<BufferedImage> grid = new Grid<BufferedImage>();
    final  GraphicsConfiguration gc;

    public StrokeBuffer(int gridCellWidth, int gridCellHeight, GraphicsConfiguration gc)
    {
        this.gridCellWidth = gridCellWidth;
        this.gridCellHeight = gridCellHeight;
        this.gc = gc;
    }

    public void addCircle(double cx, double cy, double radius)
    {
        if (radius <= 0)
        {
            return;
        }

        double pMinX = cx - radius;
        double pMinY = cy - radius;
        double pMaxX = cx + radius;
        double pMaxY = cy + radius;

        int minCellX = (int)Math.floor(pMinX / gridCellWidth);
        int minCellY = (int)Math.floor(pMinY / gridCellHeight);
        int maxCellX = (int)Math.ceil(pMaxX / gridCellWidth) - 1;
        int maxCellY = (int)Math.ceil(pMaxY / gridCellHeight) - 1;

        Ellipse2D.Double ellipse = new Ellipse2D.Double(
                cx - radius, cy - radius, radius * 2, radius * 2);

        grid.includeRegion(minCellX, minCellY,
                maxCellX - minCellX + 1,
                maxCellY - minCellY + 1,
                null);

        for (int j = minCellY; j <= maxCellY; ++j)
        {
            for (int i = minCellX; i <= maxCellX; ++i)
            {
                BufferedImage img = grid.getValue(i, j);
                Graphics2D g;
                if (img == null)
                {
                    img = gc.createCompatibleImage(gridCellWidth, gridCellHeight, Transparency.BITMASK);
                    grid.setValue(i, j, img);

                    g = img.createGraphics();
//                    g.setColor(Color.white);
//                    g.fillRect(0, 0, gridCellWidth, gridCellHeight);
                }
                else
                {
                    g = img.createGraphics();
                }

                g.translate(-i * gridCellWidth, -j * gridCellHeight);
                g.setColor(Color.blue);
                g.fill(ellipse);

                g.dispose();

            }
        }


    }


    @Override
    public boolean isOpaque(double px, double py)
    {
        int cellX = (int)Math.floor(px / gridCellWidth);
        int cellY = (int)Math.floor(py / gridCellHeight);

        BufferedImage img = grid.getValue(cellX, cellY);
        if (img == null)
        {
            return false;
        }

        int x = (int)(px - cellX * gridCellWidth);
        int y = (int)(py - cellY * gridCellHeight);
        WritableRaster raster = img.getRaster();

        int alpha = raster.getSample(x, y, 3);

        return alpha != 0;
    }

    public BitmapOutliner buildOutliner()
    {
        double tileAreaX = grid.getOffsetX() * gridCellWidth;
        double tileAreaY = grid.getOffsetY() * gridCellHeight;
        double tileAreaWidth = grid.getWidth() * gridCellWidth;
        double tileAreaHeight = grid.getHeight() * gridCellHeight;

        CyRectangle2d region = new CyRectangle2d(
                tileAreaX, tileAreaY,
                tileAreaWidth, tileAreaHeight);

        return new BitmapOutliner(this,
                    region,
                    (int)tileAreaWidth, (int)tileAreaHeight);
    }

    public void render(Graphics2D g)
    {
        int gridOffX = grid.getOffsetX();
        int gridOffY = grid.getOffsetY();

        for (int j = 0; j < grid.getHeight(); ++j)
        {
            for (int i = 0; i < grid.getWidth(); ++i)
            {
                BufferedImage img = grid.getValue(i + gridOffX, j + gridOffY);
                if (img == null)
                {
                    continue;
                }

                g.drawImage(img,
                        (i + gridOffX) * gridCellWidth,
                        (j + gridOffY) * gridCellHeight,
                        null);
            }
        }
    }

    public boolean isEmpty()
    {
        return grid.isEmpty();
    }

}
