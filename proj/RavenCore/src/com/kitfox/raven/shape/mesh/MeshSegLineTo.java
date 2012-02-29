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
public class MeshSegLineTo extends MeshSegment
{
    private final int ex;
    private final int ey;

    public MeshSegLineTo(int ex, int ey)
    {
        this.ex = ex;
        this.ey = ey;
    }

    public static MeshSegLineTo create(String strn)
    {
        int[] arr = NumberText.findIntegerArray(strn.substring(1));
        switch (strn.charAt(0))
        {
            case 'h':
                return new MeshSegLineTo(0, arr[0]);
            case 'v':
                return new MeshSegLineTo(arr[0], 0);
            default:
                return new MeshSegLineTo(arr[0], arr[1]);
        }
    }

    @Override
    public String toString()
    {
        if (ex == 0)
        {
            return String.format("h%d", ey);
        }
        if (ey == 0)
        {
            return String.format("v%d", ex);
        }
        return String.format("l%d %d", ex, ey);
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
        final MeshSegLineTo other = (MeshSegLineTo) obj;
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
        int hash = 5;
        hash = 79 * hash + this.ex;
        hash = 79 * hash + this.ey;
        return hash;
    }

    @Override
    public void visit(MeshCurvesVisitor visitor)
    {
        visitor.lineTo(ex, ey);
    }



}
