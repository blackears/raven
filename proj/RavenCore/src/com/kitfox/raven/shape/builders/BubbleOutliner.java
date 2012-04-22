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
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BubbleOutliner
        implements Sampler
{
    final double gridCellWidth;
    final double gridCellHeight;
    Grid<GridSquare> grid = new Grid<GridSquare>();
    ArrayList<Bubble> bubbleList = new ArrayList<Bubble>();

//    Area area = new Area();

    public BubbleOutliner(double gridCellWidth, double gridCellHeight)
    {
        this.gridCellWidth = gridCellWidth;
        this.gridCellHeight = gridCellHeight;
    }

    public void addBubble(double cx, double cy, double radius)
    {
//        Ellipse2D.Double circle = new Ellipse2D.Double(cx - radius, cy - radius,
//                radius * 2, radius * 2);
//        area.add(new Area(circle));

        int cellMinX = (int)Math.floor((cx - radius) / gridCellWidth);
        int cellMaxX = (int)Math.ceil((cx + radius) / gridCellWidth) - 1;
        int cellMinY = (int)Math.floor((cy - radius) / gridCellHeight);
        int cellMaxY = (int)Math.ceil((cy + radius) / gridCellHeight) - 1;

        grid.includeRegion(cellMinX, cellMinY,
                cellMaxX - cellMinX + 1,
                cellMaxY - cellMinY + 1, null);

        Bubble bubble = new Bubble(cx, cy, radius);
        bubbleList.add(bubble);
        for (int j = cellMinY; j <= cellMaxY; ++j)
        {
            for (int i = cellMinX; i <= cellMaxX; ++i)
            {
                GridSquare sq = grid.getValue(i, j);
                if (sq == null)
                {
                    sq = new GridSquare();
                    grid.setValue(i, j, sq);
                }
                sq.bubbles.add(bubble);
            }
        }
    }

    @Override
    public boolean isOpaque(double px, double py)
    {
//        return area.contains(px, py);


//        for (int i = 0; i < bubbleList.size(); ++i)
//        {
//            Bubble bubble = bubbleList.get(i);
//            if (bubble.isHit(px, py))
//            {
//                return true;
//            }
//        }
//        return false;

        int cellX = (int)Math.floor(px / gridCellWidth);
        int cellY = (int)Math.floor(py / gridCellHeight);

        GridSquare sq = grid.getValue(cellX, cellY);
        if (sq == null)
        {
            return false;
        }

        for (Bubble bubble: sq.bubbles)
        {
            if (bubble.isHit(px, py))
            {
                return true;
            }
        }
        return false;
    }

    public BitmapOutliner buildOutliner()
    {
//        Rectangle region = area.getBounds();
//        return new BitmapOutliner(this,
//                    region,
//                    region.width, region.height);

        double tileAreaX = grid.getOffsetX() * gridCellWidth;
        double tileAreaY = grid.getOffsetY() * gridCellHeight;
        double tileAreaWidth = grid.getWidth() * gridCellWidth;
        double tileAreaHeight = grid.getHeight() * gridCellHeight;

        CyRectangle2d region = new CyRectangle2d(tileAreaX, tileAreaY, tileAreaWidth, tileAreaHeight);
        return new BitmapOutliner(this,
                    region,
                    (int)tileAreaWidth, (int)tileAreaHeight);

    }

    public void render(Graphics2D g)
    {
//        g.fill(area);
        for (Bubble bubble: bubbleList)
        {
            bubble.render(g);
        }
    }

    public boolean isEmpty()
    {
//        return area.isEmpty();
        return bubbleList.isEmpty();
    }
    
    //-------------------------------
    class GridSquare
    {
        ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
    }

    static class Bubble
    {
        final double cx;
        final double cy;
        final double radius;
        final double radiusSq;

        public Bubble(double cx, double cy, double radius)
        {
            this.cx = cx;
            this.cy = cy;
            this.radius = radius;
            this.radiusSq = radius * radius;
        }

        public boolean isHit(double px, double py)
        {
            double dx = px - cx;
            double dy = py - cy;
            return dx * dx + dy * dy <= radiusSq;
        }

        public void render(Graphics2D g)
        {
            g.fillOval((int)(cx - radius),
                    (int)(cy - radius),
                    (int)(radius * 2),
                    (int)(radius * 2));
        }
    }
}
