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

package com.kitfox.raven.swf.importer;

import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;

/**
 *
 * @author kitfox
 */
public class PaintEntry
{
    private final RavenPaint paint;
    private final RavenPaintLayout layout;

    public PaintEntry(RavenPaint paint, RavenPaintLayout layout)
    {
        this.paint = paint;
        this.layout = layout;
    }

    /**
     * @return the paint
     */
    public RavenPaint getPaint()
    {
        return paint;
    }

    /**
     * @return the layout
     */
    public RavenPaintLayout getLayout()
    {
        return layout;
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
        final PaintEntry other = (PaintEntry) obj;
        if (this.paint != other.paint && (this.paint == null || !this.paint.equals(other.paint)))
        {
            return false;
        }
        if (this.layout != other.layout && (this.layout == null || !this.layout.equals(other.layout)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 47 * hash + (this.paint != null ? this.paint.hashCode() : 0);
        hash = 47 * hash + (this.layout != null ? this.layout.hashCode() : 0);
        return hash;
    }

}
