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

package com.kitfox.coyote.math.d3;

import com.kitfox.coyote.math.CyVector3d;


/**
 *
 * @author kitfox
 */
public class Octants
{
    /**
     * Flag represents a membership in the positive X halfspace.  This is the 
     * half space of all values greater than or equal to zero.
     */
    public static final int OCTANT_X = 0x1;
    public static final int OCTANT_Y = 0x2;
    public static final int OCTANT_Z = 0x4;
    
    /** Creates a new instance of Octants */
    public Octants()
    {
    }
    
    public static int getOctant(CyVector3d p)
    {
        return getOctant(p.x, p.y, p.z);
    }
    
    /**
     * Classifies a point into an octant relative to the origin.  
     */
    public static int getOctant(float x, float y, float z)
    {
        return (x < 0 ? 0 : OCTANT_X) + (y < 0 ? 0 : OCTANT_Y) + (z < 0 ? 0 : OCTANT_Z);
    }
    
    public static int getOctant(double x, double y, double z)
    {
        return (x < 0 ? 0 : OCTANT_X) + (y < 0 ? 0 : OCTANT_Y) + (z < 0 ? 0 : OCTANT_Z);
    }
    
    public static int getOctant(boolean positiveX, boolean positiveY, boolean positiveZ)
    {
        return (positiveX ? OCTANT_X : 0) + (positiveY ? OCTANT_Y : 0) + (positiveZ ? OCTANT_Z : 0);
    }
    
    public static int getOctant(CyVector3d center, CyVector3d point)
    {
        return (point.x < center.x ? 0 : OCTANT_X) | (point.y < center.y ? 0 : OCTANT_Y) | (point.z < center.z ? 0 : OCTANT_Z);
    }
    
    public static int getOppositeOctant(int octant)
    {
        return octant ^ 0x7;
    }
}
