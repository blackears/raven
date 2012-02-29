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

package com.kitfox.raven.shape.mesh;

import com.kitfox.rabbit.util.NumberText;

/**
 *
 * @author kitfox
 */
@Deprecated
public class MeshSegCubicTo extends MeshSegment
{
    private final int kx0;
    private final int ky0;
    private final int kx1;
    private final int ky1;
    private final int ex;
    private final int ey;

    public MeshSegCubicTo(int kx0, int ky0, int kx1, int ky1, int ex, int ey)
    {
        this.kx0 = kx0;
        this.ky0 = ky0;
        this.kx1 = kx1;
        this.ky1 = ky1;
        this.ex = ex;
        this.ey = ey;
    }

    public static MeshSegCubicTo create(String strn)
    {
        int[] arr = NumberText.findIntegerArray(strn.substring(1));
        return new MeshSegCubicTo(arr[0], arr[1], arr[2], arr[3],
                arr[4], arr[5]);
    }

    @Override
    public String toString()
    {
        return String.format("c%d %d %d %d %d %d",
                kx0, ky0, kx1, ky1, ex, ey);
    }

    /**
     * @return the kx0
     */
    public int getKx0() {
        return kx0;
    }

    /**
     * @return the ky0
     */
    public int getKy0() {
        return ky0;
    }

    /**
     * @return the kx1
     */
    public int getKx1() {
        return kx1;
    }

    /**
     * @return the ky1
     */
    public int getKy1() {
        return ky1;
    }

    /**
     * @return the ex
     */
    public int getEx() {
        return ex;
    }

    /**
     * @return the ey
     */
    public int getEy() {
        return ey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshSegCubicTo other = (MeshSegCubicTo) obj;
        if (this.kx0 != other.kx0) {
            return false;
        }
        if (this.ky0 != other.ky0) {
            return false;
        }
        if (this.kx1 != other.kx1) {
            return false;
        }
        if (this.ky1 != other.ky1) {
            return false;
        }
        if (this.ex != other.ex) {
            return false;
        }
        if (this.ey != other.ey) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.kx0;
        hash = 71 * hash + this.ky0;
        hash = 71 * hash + this.kx1;
        hash = 71 * hash + this.ky1;
        hash = 71 * hash + this.ex;
        hash = 71 * hash + this.ey;
        return hash;
    }

    @Override
    public void visit(MeshCurvesVisitor visitor)
    {
        visitor.cubicTo(kx0, ky0, kx1, ky1, ex, ey);
    }


}
