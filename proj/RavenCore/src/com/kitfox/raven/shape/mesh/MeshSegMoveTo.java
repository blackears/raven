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
public class MeshSegMoveTo extends MeshSegment
{
    private final int px;
    private final int py;

    public MeshSegMoveTo(int px, int py)
    {
        this.px = px;
        this.py = py;
    }

    public static MeshSegMoveTo create(String strn)
    {
        int[] arr = NumberText.findIntegerArray(strn.substring(1));
        return new MeshSegMoveTo(arr[0], arr[1]);
    }

    @Override
    public String toString()
    {
        return String.format("M%d %d", px, py);
    }

    /**
     * @return the px
     */
    public int getPx() {
        return px;
    }

    /**
     * @return the py
     */
    public int getPy() {
        return py;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshSegMoveTo other = (MeshSegMoveTo) obj;
        if (this.px != other.px) {
            return false;
        }
        if (this.py != other.py) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.px;
        hash = 71 * hash + this.py;
        return hash;
    }

    @Override
    public void visit(MeshCurvesVisitor visitor)
    {
        visitor.moveTo(px, py);
    }


}
