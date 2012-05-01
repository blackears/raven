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

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.outliner.CardinalDirection;

/**
 *
 * @author kitfox
 */
public class OutlinerSegment
{
    private final Coord coord;
    private final CardinalDirection dir;

    public OutlinerSegment(Coord coord, CardinalDirection dir)
    {
        this.coord = coord;
        this.dir = dir;
    }

    public OutlinerSegment followingSeg(int index)
    {
        int x = coord.x;
        int y = coord.y;
        
        switch (dir)
        {
            case NORTH:
                y -= 1;
                break;
            case EAST:
                x += 1;
                break;
            case SOUTH:
                y += 1;
                break;
            case WEST:
                x -= 1;
                break;
        }

        CardinalDirection dirNext;
        switch (index)
        {
            case 0:
                dirNext = dir.nextCW();
                break;
            case 1:
                dirNext = dir;
                break;
            case 2:
                dirNext = dir.nextCCW();
                break;
            default:
                throw new IllegalStateException();
        }
        
        return new OutlinerSegment(new Coord(x, y), dirNext);
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
        final OutlinerSegment other = (OutlinerSegment)obj;
        if (this.coord != other.coord && (this.coord == null || !this.coord.equals(other.coord)))
        {
            return false;
        }
        if (this.dir != other.dir)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.coord != null ? this.coord.hashCode() : 0);
        hash = 59 * hash + (this.dir != null ? this.dir.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "[" + coord + " " + dir + "]";
    }

    /**
     * @return the coord
     */
    public Coord getCoord()
    {
        return coord;
    }

    /**
     * @return the dir
     */
    public CardinalDirection getDir()
    {
        return dir;
    }
    
}
