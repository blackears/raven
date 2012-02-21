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

package com.kitfox.coyote.shape.tessellator;

/**
 *
 * @author kitfox
 */
@Deprecated
public class TessSeg
{
    TessPoint pt0;
    TessPoint pt1;

    public TessSeg(TessPoint p0, TessPoint p1)
    {
        this.pt0 = p0;
        this.pt1 = p1;
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
        final TessSeg other = (TessSeg)obj;
        if (this.pt0 != other.pt0 && (this.pt0 == null || !this.pt0.equals(other.pt0)))
        {
            return false;
        }
        if (this.pt1 != other.pt1 && (this.pt1 == null || !this.pt1.equals(other.pt1)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 41 * hash + (this.pt0 != null ? this.pt0.hashCode() : 0);
        hash = 41 * hash + (this.pt1 != null ? this.pt1.hashCode() : 0);
        return hash;
    }

    public double minX()
    {
        return Math.min(pt0.x, pt1.x);
    }
    
    public double minY()
    {
        return Math.min(pt0.y, pt1.y);
    }

    public double maxX()
    {
        return Math.max(pt0.x, pt1.x);
    }
    
    public double maxY()
    {
        return Math.max(pt0.y, pt1.y);
    }
    
    public boolean boundsOverlap(TessSeg s1)
    {
        return maxX() >= s1.minX()
                && minX() <= s1.maxX()
                && maxY() >= s1.minY()
                && minY() <= s1.maxY();
    }

    @Override
    public String toString()
    {
        return "[" + pt0 + " " + pt1 + "]";
    }

    
}
