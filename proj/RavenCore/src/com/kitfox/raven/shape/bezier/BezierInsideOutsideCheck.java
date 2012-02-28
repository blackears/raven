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

package com.kitfox.raven.shape.bezier;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierInsideOutsideCheck
{
    Cursor enterFrom = Cursor.NONE;
    int enterY;

    Cursor initExit = Cursor.NONE;
    int initExitY;
    
    int numCrossings;

    int x;
    int y;

    public BezierInsideOutsideCheck(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    void testNextLineSeg(int x0, int y0, int x1, int y1)
    {
        if (x0 == x1)
        {
            //Vertical segments cause no change
            return;
        }
        else if (x0 < x && x1 < x)
        {
            //Both segments to left - no change
            return;
        }
        else if (x0 > x && x1 > x)
        {
            //Both segments to right - no change
            return;
        }
        else if (x1 == x && x0 > x)
        {
            //Enter from right
            enterFrom = Cursor.RIGHT;
            enterY = y1;
        }
        else if (x1 == x && x0 < x)
        {
            //Enter from left
            enterFrom = Cursor.LEFT;
            enterY = y1;
        }
        else if (x1 > x && x0 == x)
        {
            //Exit to right
            if (enterFrom == Cursor.LEFT && enterY < y && y0 < y)
            {
                ++numCrossings;
            }
            else if (enterFrom == Cursor.NONE)
            {
                //We're starting at the middle
                initExit = Cursor.RIGHT;
                initExitY = y0;
            }
        }
        else if (x1 < x && x0 == x)
        {
            //Exit to left
            if (enterFrom == Cursor.RIGHT && enterY < y && y0 < y)
            {
                ++numCrossings;
            }
            else if (enterFrom == Cursor.NONE)
            {
                //We're starting at the middle
                initExit = Cursor.LEFT;
                initExitY = y0;
            }
        }
        else
        {
            //Segment spans left-right gap
            double slope = (y1 - y0) / (double)(x1 - x0);
            double midY = slope * (x - x0) + y0;
            if (midY < y)
            {
                ++numCrossings;
            }
        }
    }

    void finish()
    {
        //If we started at a midpoint, count looping segment
        if (initExit == Cursor.LEFT && enterFrom == Cursor.RIGHT
                && enterY < y && initExitY < y)
        {
            ++numCrossings;
        }
        else if (initExit == Cursor.RIGHT && enterFrom == Cursor.LEFT
                && enterY < y && initExitY < y)
        {
            ++numCrossings;
        }
    }

    boolean isInside()
    {
        //Inside if there are an odd number of crossings
        return (numCrossings & 0x1) == 1;
    }

    enum Cursor { NONE, LEFT, RIGHT };
}
