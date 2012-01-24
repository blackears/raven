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

package com.kitfox.coyote.renderer.shader;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.CyVector3d;

/**
 *
 * @author kitfox
 */
public class ObjVertex
{
    CyVector3d pt;
    CyVector2d uv;
    CyVector3d norm;
    
    public ObjVertex(CyVector3d pt, CyVector2d uv, CyVector3d norm)
    {
        this.pt = pt;
        this.uv = uv;
        this.norm = norm;
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
        final ObjVertex other = (ObjVertex) obj;
        if (this.pt != other.pt && (this.pt == null || !this.pt.equals(other.pt)))
        {
            return false;
        }
        if (this.uv != other.uv && (this.uv == null || !this.uv.equals(other.uv)))
        {
            return false;
        }
        if (this.norm != other.norm && (this.norm == null || !this.norm.equals(other.norm)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.pt != null ? this.pt.hashCode() : 0);
        hash = 59 * hash + (this.uv != null ? this.uv.hashCode() : 0);
        hash = 59 * hash + (this.norm != null ? this.norm.hashCode() : 0);
        return hash;
    }
    
    
}
