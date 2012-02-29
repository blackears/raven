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
public class MeshSegPaintLine extends MeshSegment
{
    private final int id;

    public MeshSegPaintLine(int id)
    {
        this.id = id;
    }

    public static MeshSegPaintLine create(String strn)
    {
        int[] arr = NumberText.findIntegerArray(strn.substring(1));
        return new MeshSegPaintLine(arr[0]);
    }

    @Override
    public String toString()
    {
        return String.format("f%d", id);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshSegPaintLine other = (MeshSegPaintLine) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        return hash;
    }

    @Override
    public void visit(MeshCurvesVisitor visitor)
    {
        visitor.paintLine(id);
    }


}
