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

package com.kitfox.game.control.color;

import com.kitfox.cache.CacheMap;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PaintLayoutNone extends PaintLayoutAbstract
{
    public static final String CACHE_NAME = "none";

    public static final PaintLayoutNone LAYOUT = new PaintLayoutNone();

    private PaintLayoutNone()
    {
    }

    @Override
    protected CyMatrix4d createPaintToLocalTransform()
    {
        return CyMatrix4d.createIdentity();
    }

    @Override
    public CyVector2d getFocusLocal()
    {
        return new CyVector2d();
    }

    @Override
    public CacheMap toCache()
    {
        return new CacheMap(CACHE_NAME);
    }

    @Override
    public PaintLayout transform(CyMatrix4d l2w)
    {
        return this;
    }

}
