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

package com.kitfox.coyote.shape.outliner;

/**
 *
 * @author kitfox
 */
public enum CardinalDirection
{
    NORTH, EAST, SOUTH, WEST;
    
    public CardinalDirection nextCW()
    {
        switch (this)
        {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            default:
            case WEST:
                return NORTH;
        }
    }
    
    public CardinalDirection nextCCW()
    {
        switch (this)
        {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            default:
            case WEST:
                return SOUTH;
        }
    }

    public CardinalDirection opposite()
    {
        switch (this)
        {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            default:
            case WEST:
                return EAST;
        }
    }
}
