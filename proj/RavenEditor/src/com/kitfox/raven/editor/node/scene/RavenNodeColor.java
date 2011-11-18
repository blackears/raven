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

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyWrapper;

/**
 *
 * @author kitfox
 */
public class RavenNodeColor extends RavenNodePaint
{
    public static final String PROP_COLOR = "color";
    public final PropertyWrapper<RavenNodeColor, RavenPaintColor> color =
            new PropertyWrapper(this, PROP_COLOR, RavenPaintColor.class,
            RavenPaintColor.BLACK);

    protected RavenNodeColor(int uid)
    {
        super(uid);
    }

    @Override
    public RavenPaintColor createPaint()
    {
        return color.getValue();
    }

    @Override
    public void fillShape(CyDrawStack renderer,
            PaintLayout curFillLayout, CyVertexBuffer mesh)
    {
        CyMaterialColorDrawRecord rec = CyMaterialColorDrawRecordFactory.inst().allocRecord();

        {
            RavenPaintColor col = color.getValue();
            ColorStyle cs = col.getColor();
            rec.setColor(new CyColor4f(cs.r, cs.g, cs.b, cs.a));
        }

        {
//            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
//            CyVertexBuffer mesh = new CyVertexBuffer(meshProv);
            rec.setMesh(mesh);
        }

        rec.setOpacity(renderer.getOpacity());

        rec.setMvpMatrix(renderer.getModelViewProjXform());

        renderer.addDrawRecord(rec);
    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeColor>
    {
        public Provider()
        {
            super(RavenNodeColor.class, "Color", "/icons/node/color.png");
        }

        @Override
        public RavenNodeColor createNode(int uid)
        {
            return new RavenNodeColor(uid);
        }
    }
}
