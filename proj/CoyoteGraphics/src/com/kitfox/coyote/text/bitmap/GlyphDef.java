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

package com.kitfox.coyote.text.bitmap;

/**
 *
 * @author kitfox
 */
public class GlyphDef
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int originX;
    private final int originY;
    private final int advanceX;

    public GlyphDef(int x, int y, int width, int height, int originX, int originY, int advanceX)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.originX = originX;
        this.originY = originY;
        this.advanceX = advanceX;
    }

    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return the originX
     */
    public int getOriginX()
    {
        return originX;
    }

    /**
     * @return the originY
     */
    public int getOriginY()
    {
        return originY;
    }

    /**
     * @return the advanceX
     */
    public int getAdvanceX()
    {
        return advanceX;
    }
}
