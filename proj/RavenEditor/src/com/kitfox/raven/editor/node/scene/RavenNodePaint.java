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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.shape.bezier.BezierPaint;
import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintInline;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
@Deprecated
abstract public class RavenNodePaint extends RavenNode
        implements BezierPaint, RavenPaint
{

    RavenPaint ravenPaint;

    public RavenNodePaint(int uid)
    {
        super(uid);
    }

    protected void clearCache()
    {
        ravenPaint = null;
    }

    @Override
    public Paint getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        if (ravenPaint == null)
        {
            ravenPaint = createPaint();
        }
        return ravenPaint.getPaint(layout, localToWorld);
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        if (ravenPaint == null)
        {
            ravenPaint = createPaint();
        }
        //box.x = box.y = 0;
        return ravenPaint.getPaintSwatch(box);
    }

    abstract public RavenPaintInline createPaint();

}
