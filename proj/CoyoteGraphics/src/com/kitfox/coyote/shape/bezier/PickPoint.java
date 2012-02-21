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

package com.kitfox.coyote.shape.bezier;

/**
 *
 * @author kitfox
 */
public class PickPoint
{
    private final double x;
    private final double y;
    private final double t;
    private final double distSquared;

    public PickPoint(double x, double y, double t, double distSquared)
    {
        this.x = x;
        this.y = y;
        this.t = t;
        this.distSquared = distSquared;
    }

    /**
        * @return the x
        */
    public double getX()
    {
        return x;
    }

    /**
        * @return the y
        */
    public double getY()
    {
        return y;
    }

    /**
        * @return the t
        */
    public double getT()
    {
        return t;
    }

    /**
        * @return the distSquared
        */
    public double getDistSquared()
    {
        return distSquared;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + t + ", " + distSquared + ")";
    }
    
    
}
