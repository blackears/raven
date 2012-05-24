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

package com.kitfox.coyote.shape.outliner.bitmap;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierSchneider2d;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.outliner.CardinalDirection;
import com.kitfox.coyote.shape.outliner.LevelSampler;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author kitfox
 */
public class BitmapOutliner
{
    LinkedHashMap<OutlinerSegment, OutlinerSegmentInfo> segments =
            new LinkedHashMap<OutlinerSegment, OutlinerSegmentInfo>();
    
    LevelSampler sampler;
    CyRectangle2i region;
    int emptyLevel;
    double smoothing;

    CyPath2d loops;
    
    public BitmapOutliner(LevelSampler sampler, CyRectangle2i region,
            int emptyLevel, double smoothing)
    {
        this.sampler = sampler;
        this.region = region;
        this.emptyLevel = emptyLevel;
        this.smoothing = smoothing;
        
        buildPartitions();
        loops = buildLoops();
    }

    public CyPath2d getPath()
    {
        return new CyPath2d(loops);
    }
    
    private CyPath2d buildLoops()
    {
        CyPath2d totalPath = new CyPath2d();
        
        while (!segments.isEmpty())
        {
            ArrayList<Coord> coords = extractLoop();

            PiecewiseBezierSchneider2d builder =
                    new PiecewiseBezierSchneider2d(true, smoothing);
            for (Coord c: coords)
            {
                builder.addPoint(new CyVector2d(c.x, c.y));
            }
            CyPath2d path = builder.getPath();
            totalPath.append(path);
        }
        
        return totalPath;
    }
    
    private ArrayList<Coord> extractLoop()
    {
        OutlinerSegment first = segments.keySet().iterator().next();
        segments.remove(first);
        
        ArrayList<Coord> coords = new ArrayList<Coord>();
        coords.add(first.getCoord());
        
        OutlinerSegment cur = first;
        SEARCH:
        while (true)
        {
            OutlinerSegment next = null;
            for (int i = 0; i < 3; ++i)
            {
                OutlinerSegment follow = cur.followingSeg(i);
                if (follow.equals(first))
                {
                    break SEARCH;
                }
                if (segments.containsKey(follow))
                {
                    segments.remove(follow);
                    next = follow;
                    break;
                }
            }
            coords.add(next.getCoord());
            cur = next;
        }
        
        return coords;
    }
    
    private void buildPartitions()
    {
        for (int j = 0; j < region.getHeight(); ++j)
        {
            int y = region.getY() + j;
            for (int i = 0; i < region.getWidth(); ++i)
            {
                int x = region.getX() + i;
                int level = sampler.getLevel(x, y);
                
                if (level == emptyLevel)
                {
                    continue;
                }
                
                int n = j == 0 
                        ? emptyLevel
                        : sampler.getLevel(x, y - 1);
                int s = j == region.getHeight() - 1 
                        ? emptyLevel
                        : sampler.getLevel(x, y + 1);
                int w = i == 0 
                        ? emptyLevel
                        : sampler.getLevel(x - 1, y);
                int e = i == region.getWidth() - 1
                        ? emptyLevel
                        : sampler.getLevel(x + 1, y);


                if (n != level)
                {
                    segments.put(
                            new OutlinerSegment(new Coord(x + 1, y),
                            CardinalDirection.WEST),
                            new OutlinerSegmentInfo(level, n));
                }
                if (s != level)
                {
                    segments.put(
                            new OutlinerSegment(new Coord(x, y + 1), 
                            CardinalDirection.EAST),
                            new OutlinerSegmentInfo(level, s));
                }
                if (w != level)
                {
                    segments.put(
                            new OutlinerSegment(new Coord(x, y),
                            CardinalDirection.SOUTH),
                            new OutlinerSegmentInfo(level, w));
                }
                if (e != level)
                {
                    segments.put(
                            new OutlinerSegment(new Coord(x + 1, y + 1),
                            CardinalDirection.NORTH),
                            new OutlinerSegmentInfo(level, e));
                }
            }
        }
    }
    
}
