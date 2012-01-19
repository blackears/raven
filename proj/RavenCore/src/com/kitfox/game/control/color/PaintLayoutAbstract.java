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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;

/**
 *
 * @author kitfox
 */
@Deprecated
abstract public class PaintLayoutAbstract implements PaintLayout
{
    CyMatrix4d paintToLocal;

    abstract protected CyMatrix4d createPaintToLocalTransform();

    @Override
    public CyMatrix4d getPaintToLocalTransform()
    {
        if (paintToLocal == null)
        {
            paintToLocal = createPaintToLocalTransform();
        }
        return new CyMatrix4d(paintToLocal);
    }

    @Override
    public CyVector2d getFocusPaint()
    {
        CyVector2d focus = getFocusLocal();

        CyMatrix4d localToPaint = new CyMatrix4d(paintToLocal);
        localToPaint.invert();
        localToPaint.transformPoint(focus);

        return focus;
    }

}
