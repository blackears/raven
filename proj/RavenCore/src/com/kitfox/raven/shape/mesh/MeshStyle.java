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

/**
 *
 * @author kitfox
 */
@Deprecated
public class MeshStyle
{
    private final int paintLeft;
    private final int paintRight;
    private final int paintLine;
    private final int strokeLine;

    public MeshStyle(int paintLeft, int paintRight, int paintLine, int strokeLine)
    {
        this.paintLeft = paintLeft;
        this.paintRight = paintRight;
        this.paintLine = paintLine;
        this.strokeLine = strokeLine;
    }

    /**
     * @return the paintLeft
     */
    public int getPaintLeft() {
        return paintLeft;
    }

    /**
     * @return the paintRight
     */
    public int getPaintRight() {
        return paintRight;
    }

    /**
     * @return the paintLine
     */
    public int getPaintLine() {
        return paintLine;
    }

    /**
     * @return the strokeLine
     */
    public int getStrokeLine() {
        return strokeLine;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshStyle other = (MeshStyle) obj;
        if (this.paintLeft != other.paintLeft) {
            return false;
        }
        if (this.paintRight != other.paintRight) {
            return false;
        }
        if (this.paintLine != other.paintLine) {
            return false;
        }
        if (this.strokeLine != other.strokeLine) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.paintLeft;
        hash = 29 * hash + this.paintRight;
        hash = 29 * hash + this.paintLine;
        hash = 29 * hash + this.strokeLine;
        return hash;
    }


}
