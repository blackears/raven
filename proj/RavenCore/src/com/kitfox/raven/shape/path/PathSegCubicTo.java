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

package com.kitfox.raven.shape.path;

import java.awt.geom.Path2D;

/**
 *
 * @author kitfox
 */
public class PathSegCubicTo extends PathSeg
{
    private final int k0x;
    private final int k0y;
    private final int k1x;
    private final int k1y;
    private final int x;
    private final int y;

    public PathSegCubicTo(int k0x, int k0y, int k1x, int k1y, int x, int y)
    {
        this.k0x = k0x;
        this.k0y = k0y;
        this.k1x = k1x;
        this.k1y = k1y;
        this.x = x;
        this.y = y;
    }

    @Override
    public void append(Path2D path)
    {
        path.curveTo(k0x, k0y, k1x, k1y, x, y);
    }

    /**
     * @return the k0x
     */
    public int getK0x() {
        return k0x;
    }

    /**
     * @return the k0y
     */
    public int getK0y() {
        return k0y;
    }

    /**
     * @return the k1x
     */
    public int getK1x() {
        return k1x;
    }

    /**
     * @return the k1y
     */
    public int getK1y() {
        return k1y;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathSegCubicTo other = (PathSegCubicTo) obj;
        if (this.k0x != other.k0x) {
            return false;
        }
        if (this.k0y != other.k0y) {
            return false;
        }
        if (this.k1x != other.k1x) {
            return false;
        }
        if (this.k1y != other.k1y) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.k0x;
        hash = 47 * hash + this.k0y;
        hash = 47 * hash + this.k1x;
        hash = 47 * hash + this.k1y;
        hash = 47 * hash + this.x;
        hash = 47 * hash + this.y;
        return hash;
    }

    @Override
    public String toSVGPath()
    {
        return " C " + k0x + " " + k0y
            + " " + k1x + " " + k1y
            + " " + x + " " + y;
    }
}
