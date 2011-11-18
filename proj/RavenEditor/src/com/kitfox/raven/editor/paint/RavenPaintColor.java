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
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.game.control.color.PaintLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class RavenPaintColor extends RavenPaintInline
{
    public static final String CACHE_NAME = "color";

    private final ColorStyle color;

    public static final RavenPaintColor TRANSPARENT = new RavenPaintColor(0, 0, 0, 0);
    public static final RavenPaintColor BLACK = new RavenPaintColor(0, 0, 0, 1);
    public static final RavenPaintColor RED = new RavenPaintColor(1, 0, 0, 1);
    public static final RavenPaintColor GREEN = new RavenPaintColor(0, 1, 0, 1);
    public static final RavenPaintColor YELLOW = new RavenPaintColor(1, 1, 0, 1);
    public static final RavenPaintColor BLUE = new RavenPaintColor(0, 0, 1, 1);
    public static final RavenPaintColor MAGENTA = new RavenPaintColor(1, 0, 1, 1);
    public static final RavenPaintColor CYAN = new RavenPaintColor(0, 1, 1, 1);
    public static final RavenPaintColor WHITE = new RavenPaintColor(1, 1, 1, 1);

    public RavenPaintColor(ColorStyle color)
    {
        this.color = color;
    }

    public RavenPaintColor(CacheList list)
    {
        this(new ColorStyle(list));
    }

    public RavenPaintColor(Color color)
    {
        this(new ColorStyle(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }

    public RavenPaintColor(float red, float green, float blue, float alpha)
    {
        this(new Color(red, green, blue, alpha));
    }

    public static RavenPaintColor create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheList))
            {
                return null;
            }
            return new RavenPaintColor((CacheList)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public void fillShape(CyDrawStack renderer, PaintLayout curFillLayout, CyVertexBuffer mesh)
    {
        CyMaterialColorDrawRecord rec = CyMaterialColorDrawRecordFactory.inst().allocRecord();

        rec.setColor(new CyColor4f(color.r, color.g, color.b, color.a));

        {
//            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
//            CyVertexBuffer mesh = new CyVertexBuffer(meshProv);
            rec.setMesh(mesh);
        }

        rec.setOpacity(renderer.getOpacity());

        rec.setMvpMatrix(renderer.getModelViewProjXform());

        renderer.addDrawRecord(rec);
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        return color.getColor();
    }

    @Override
    public Color getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        return color.getPaint(layout, localToWorld);
    }

    public String toCode()
    {
        return "";
    }

    @Override
    public CacheList toCache()
    {
        return color.toCache();
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the color
     */
    public ColorStyle getColor()
    {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RavenPaintColor other = (RavenPaintColor) obj;
        if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }


}
