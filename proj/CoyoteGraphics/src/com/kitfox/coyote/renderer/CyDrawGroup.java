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

package com.kitfox.coyote.renderer;

import com.kitfox.coyote.renderer.jogl.GLWrapperJOGL;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class CyDrawGroup extends CyDrawRecord
{
    //If not null, rendering will be done to a buffer and then this
    // filter will be applied.
    protected CyFilter filter;
    protected ArrayList<CyDrawRecord> records = new ArrayList<CyDrawRecord>();

    public void addRecord(CyDrawRecord rec)
    {
        records.add(rec);
    }

//    /**
//     * Draw everything to the current render surface.
//     * @param gl
//     */
//    abstract public void render(GLWrapperJOGL gl);

    /**
     * @return the filter
     */
    public CyFilter getFilter()
    {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(CyFilter filter)
    {
        this.filter = filter;
    }
}
