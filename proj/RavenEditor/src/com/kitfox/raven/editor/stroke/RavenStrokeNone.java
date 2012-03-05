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

package com.kitfox.raven.editor.stroke;

import com.kitfox.cache.CacheIdentifier;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenStrokeNone extends RavenStrokeInline
{
    public static final String CACHE_NAME = "none";

    public static RavenStrokeNone STROKE = new RavenStrokeNone();

    private RavenStrokeNone()
    {
    }

    public String toCodeGen()
    {
        return "null";
    }

    @Override
    public CacheIdentifier toCache()
    {
        return new CacheIdentifier(CACHE_NAME);
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    @Override
    public Stroke getStroke()
    {
        return null;
    }

    @Override
    public void drawPreview(Graphics2D g, Rectangle bounds)
    {
    }

}
