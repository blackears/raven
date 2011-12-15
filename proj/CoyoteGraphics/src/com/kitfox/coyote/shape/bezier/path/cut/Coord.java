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

package com.kitfox.coyote.shape.bezier.path.cut;


/**
 *
 * @author kitfox
 */
public class Coord implements Comparable<Coord>
{
    public final int x;
    public final int y;

    public Coord(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Coord other = (Coord)obj;
        if (this.x != other.x)
        {
            return false;
        }
        if (this.y != other.y)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.x;
        hash = 23 * hash + this.y;
        return hash;
    }

    @Override
    public int compareTo(Coord oth)
    {
        if (x != oth.x)
        {
            return x - oth.x;
        }
        return y - oth.y;
    }
    
    public static Coord min(Coord c0, Coord c1)
    {
        return c0.compareTo(c1) <= 0 ? c0 : c1;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
    
    
}
