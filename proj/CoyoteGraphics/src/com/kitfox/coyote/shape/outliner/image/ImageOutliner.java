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

package com.kitfox.coyote.shape.outliner.image;

import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.outliner.LevelSampler;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ImageOutliner
{
    HashMap<Coord, ImageVertex> vertMap = new HashMap<Coord, ImageVertex>();
    ArrayList<Coord> corners = new ArrayList<Coord>();
    ArrayList<ImageEdge> edges = new ArrayList<ImageEdge>();
    CyRectangle2i bounds;
    
    LevelSampler sampler;
    CyRectangle2i region;
    int emptyLevel;
    double smoothing;
    
    
    public ImageOutliner(LevelSampler sampler, CyRectangle2i region,
            int emptyLevel, double smoothing)
    {
        this.sampler = sampler;
        this.region = region;
        this.emptyLevel = emptyLevel;
        this.smoothing = smoothing;
        
        buildSegments();
        findCorners();
        extractEdges();
    }

    public CyRectangle2i getBounds()
    {
        return new CyRectangle2i(bounds);
    }
    
    public ArrayList<ImageEdge> getEdges()
    {
        return new ArrayList<ImageEdge>(edges);
    }
    
    private void extractEdges()
    {
        while (!corners.isEmpty())
        {
            edges.add(extractCornerEdge());
        }
        
        //Anything left is an independent closed loop
        while (!vertMap.isEmpty())
        {
            edges.add(extractLoopEdge());
        }
    }

    private ImageEdge extractLoopEdge()
    {
        ArrayList<Coord> points = new ArrayList<Coord>();
        
        ImageVertex v0 = vertMap.values().iterator().next();
        Coord c0 = v0.coord;
        points.add(c0);

        int levelLeft = emptyLevel;
        int levelRight = emptyLevel;
        
        while (true)
        {
            ImageSegment seg = v0.segments.remove(0);
            if (v0.size() == 0)
            {
                vertMap.remove(c0);
            }
            
            Coord c1;
            if (c0.equals(seg.start))
            {
                c1 = seg.end;
                levelLeft = seg.levelLeft;
                levelRight = seg.levelRight;
            }
            else
            {
                c1 = seg.start;
                levelLeft = seg.levelRight;
                levelRight = seg.levelLeft;
            }
            ImageVertex v1 = vertMap.get(c1);
            v1.segments.remove(seg);
            if (v1.size() == 0)
            {
                vertMap.remove(c1);
                break;
            }
            
            points.add(c1);
            
            c0 = c1;
            v0 = v1;
        }
        
        return new ImageEdge(points, levelLeft, levelRight, true);
    }

    private ImageEdge extractCornerEdge()
    {
        ArrayList<Coord> points = new ArrayList<Coord>();
        
        Coord c0 = corners.get(0);
        ImageVertex v0 = vertMap.get(c0);
        points.add(c0);

        int levelLeft = emptyLevel;
        int levelRight = emptyLevel;
        
        while (true)
        {
            ImageSegment seg = v0.segments.remove(0);
            if (v0.size() == 0)
            {
                vertMap.remove(c0);
            }
            
            Coord c1;
            if (c0.equals(seg.start))
            {
                c1 = seg.end;
                levelLeft = seg.levelLeft;
                levelRight = seg.levelRight;
            }
            else
            {
                c1 = seg.start;
                levelLeft = seg.levelRight;
                levelRight = seg.levelLeft;
            }
            ImageVertex v1 = vertMap.get(c1);
            v1.segments.remove(seg);
            
            points.add(c1);

            if (v1.corner)
            {
                if (v1.size() == 0)
                {
                    vertMap.remove(c1);
                }
                break;
            }
            
            c0 = c1;
            v0 = v1;
        }
        
        c0 = points.get(0);
        Coord c1 = points.get(points.size() - 1);
        
        //Remove corners if empty
        if (!vertMap.containsKey(c0))
        {
            corners.remove(c0);
        }
        if (!vertMap.containsKey(c1))
        {
            corners.remove(c1);
        }
        
        return new ImageEdge(points, levelLeft, levelRight, false);
    }
    
    private ImageVertex getOrCreateVertex(Coord coord)
    {
        ImageVertex v = vertMap.get(coord);
        if (v == null)
        {
            v = new ImageVertex(coord);
            vertMap.put(coord, v);
        }
        return v;
    }
    
    private void findCorners()
    {
        for (ImageVertex v: vertMap.values())
        {
            Coord c = v.coord;
            if (bounds == null)
            {
                bounds = new CyRectangle2i(c.x, c.y);
            }
            else
            {
                bounds.union(c.x, c.y);
            }
            
            if (v.size() != 2)
            {
                v.corner = true;
                corners.add(c);
            }
        }
    }
    
    private int sample(int x, int y)
    {
        if (region.contains(x, y))
        {
            return sampler.getLevel(x, y);
        }
        return emptyLevel;
    }
    
    private void buildSegments()
    {
        for (int j = 0; j <= region.getHeight(); ++j)
        {
            int y = region.getY() + j;
            for (int i = 0; i <= region.getWidth(); ++i)
            {
                int x = region.getX() + i;
                int level = sample(x, y);

                int n = sample(x, y - 1);
                int w = sample(x - 1, y);
                
                if (n != level)
                {
                    ImageSegment seg = new ImageSegment(
                            new Coord(x, y),
                            new Coord(x + 1, y), n, level);
                    
                    ImageVertex v0 = getOrCreateVertex(seg.start);
                    ImageVertex v1 = getOrCreateVertex(seg.end);
                    v0.segments.add(seg);
                    v1.segments.add(seg);
                }
                if (w != level)
                {
                    ImageSegment seg = new ImageSegment(
                            new Coord(x, y),
                            new Coord(x, y + 1),
                            w, level);
                    
                    ImageVertex v0 = getOrCreateVertex(seg.start);
                    ImageVertex v1 = getOrCreateVertex(seg.end);
                    v0.segments.add(seg);
                    v1.segments.add(seg);
                }
            }
        }
    }
    
}
