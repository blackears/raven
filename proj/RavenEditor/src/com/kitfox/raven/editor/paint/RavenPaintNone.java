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

package com.kitfox.raven.editor.paint;

import com.kitfox.cache.CacheIdentifier;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.game.control.color.PaintLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintNone extends RavenPaintInline
{
    public static RavenPaintNone PAINT = new RavenPaintNone();

    public static final String CACHE_NAME = "none";

    private RavenPaintNone()
    {
    }

    @Override
    public void fillShape(CyDrawStack renderer, PaintLayout curFillLayout, CyVertexBuffer mesh)
    {
    }

    @Override
    public Paint getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        return null;
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    @Override
    public CacheIdentifier toCache()
    {
        return new CacheIdentifier(CACHE_NAME);
    }

}
