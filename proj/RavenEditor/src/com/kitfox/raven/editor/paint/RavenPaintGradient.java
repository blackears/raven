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

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.material.gradient.CyMaterialGradientDrawRecord;
import com.kitfox.coyote.material.gradient.CyMaterialGradientDrawRecordFactory;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.ShapeMeshProvider;
import com.kitfox.game.control.color.MultipleGradientStops;
import com.kitfox.game.control.color.MultipleGradientStyle;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutTexture;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintGradient extends RavenPaintInline
{
    private final MultipleGradientStyle gradient;

    public static final String CACHE_NAME = "gradient";

    public RavenPaintGradient(MultipleGradientStyle gradient)
    {
        this.gradient = gradient;
    }

    public RavenPaintGradient(CacheMap map)
    {
        this(new MultipleGradientStyle(map));
    }

    public static RavenPaintGradient create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            return new RavenPaintGradient((CacheMap)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
//        AffineTransform xform = new AffineTransform();
//        xform.translate(box.x, box.y);
//        xform.scale(box.width, box.height);

        MultipleGradientStyle style = new MultipleGradientStyle(
                gradient.getStops());
        return style.getPaint(new PaintLayoutTexture(box), new AffineTransform());
    }

    public MultipleGradientStyle getGradient()
    {
        return gradient;
    }

    @Override
    public MultipleGradientPaint getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        return gradient.getPaint(layout, localToWorld);
    }

    @Override
    public void fillShape(CyDrawStack renderer, PaintLayout curFillLayout, CyVertexBuffer mesh)
    {
//        RavenPaintGradient grad = gradient.getValue();
//        MultipleGradientStyle sty = gradient.getGradient();
//        MultipleGradientStops stops = sty.getStops();
        MultipleGradientStops stops = gradient.getStops();

        CyMaterialGradientDrawRecord rec =
                CyMaterialGradientDrawRecordFactory.inst().allocRecord();
        {
//            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
//            CyVertexBuffer mesh = new CyVertexBuffer(meshProv);
            rec.setMesh(mesh);
        }

        rec.setOpacity(renderer.getOpacity());

        rec.setMvpMatrix(renderer.getModelViewProjXform());

        {
//            CyMatrix4d m = CyMatrix4d.createIdentity();
//            m.set(curFillLayout.getPaintToLocalTransform());
            CyMatrix4d m = curFillLayout.getPaintToLocalTransform();
            rec.setLocalToTexMatrix(m);
        }

        rec.setStops(stops.asCyGradientStops());

        renderer.addDrawRecord(rec);
    }

    public String toCode()
    {
        return "";
    }

    @Override
    public CacheMap toCache()
    {
        return gradient.toCache();
    }

    @Override
    public String toString()
    {
        return toCache().toString();
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
        final RavenPaintGradient other = (RavenPaintGradient) obj;
        if (this.gradient != other.gradient && (this.gradient == null || !this.gradient.equals(other.gradient)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 67 * hash + (this.gradient != null ? this.gradient.hashCode() : 0);
        return hash;
    }



}
