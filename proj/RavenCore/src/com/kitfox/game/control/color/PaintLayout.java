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

/**
 * All paints are defined over a [0 1] unit square.  (Eg, a linear gradient
 * with no transform applied will smoothly fill a space from (0, 0)
 * to (1, 1) with the gradient stops smoothly changing with the X axis
 * coordinate.  A radial gradient will smoothly change along the radius
 * of a circle with radius length .5 and centered at (.5, .5).)
 *
 * The paint layout provides the means to map this restricted space into
 * any other coordinate system.
 *
 * @author kitfox
 */
@Deprecated
public interface PaintLayout
{
    /**
     * 
     * @return
     */
    public CyMatrix4d getPaintToLocalTransform();

    /**
     * Some paints (radial gradients) require a focal point.  This
     * is given in paint space coordinates.
     *
     * @return
     */
    public CyVector2d getFocusPaint();
    public CyVector2d getFocusLocal();
    
    public CacheMap toCache();

    abstract public PaintLayout transform(CyMatrix4d l2w);
}
