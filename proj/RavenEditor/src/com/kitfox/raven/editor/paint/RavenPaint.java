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

import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.shape.bezier.BezierPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
@Deprecated
public interface RavenPaint extends BezierPaint
{
    public Paint getPaint(PaintLayout layout, AffineTransform localToWorld);

    public Paint getPaintSwatch(Rectangle box);

    /**
     * Add the appropriate CyDrawRecord that will render this shape with
     * current stack state.
     *
     * @param renderer Collects draw records.  Also provides render and stack information.
     * @param curFillLayout UV layout matrix for shape
     * @param mesh Mesh to render
     */
    public void fillShape(CyDrawStack renderer, PaintLayout curFillLayout, CyVertexBuffer mesh);
}
