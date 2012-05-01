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

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierSchneider2d;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class ImageEdge
{
    ArrayList<Coord> points;
    private int levelLeft;
    private int levelRight;
    private boolean continuous;

    CyPath2d path;
    
    public ImageEdge(ArrayList<Coord> points, int levelLeft, int levelRight, boolean continuous)
    {
        this.points = points;
        this.levelLeft = levelLeft;
        this.levelRight = levelRight;
        this.continuous = continuous;
    }

    public CyPath2d getPath()
    {
        if (path == null)
        {
            buildPath();
        }
        return path;
    }
    
    private void buildPath()
    {
        PiecewiseBezierSchneider2d builder = 
                new PiecewiseBezierSchneider2d(continuous);
        
        for (Coord c: points)
        {
            builder.addPoint(c.x, c.y);
        }
        
        path = builder.getPath();
    }

    /**
     * @return the levelLeft
     */
    public int getLevelLeft()
    {
        return levelLeft;
    }

    /**
     * @param levelLeft the levelLeft to set
     */
    public void setLevelLeft(int levelLeft)
    {
        this.levelLeft = levelLeft;
    }

    /**
     * @return the levelRight
     */
    public int getLevelRight()
    {
        return levelRight;
    }

    /**
     * @param levelRight the levelRight to set
     */
    public void setLevelRight(int levelRight)
    {
        this.levelRight = levelRight;
    }

    /**
     * @return the continuous
     */
    public boolean isContinuous()
    {
        return continuous;
    }

    /**
     * @param continuous the continuous to set
     */
    public void setContinuous(boolean continuous)
    {
        this.continuous = continuous;
    }
}
