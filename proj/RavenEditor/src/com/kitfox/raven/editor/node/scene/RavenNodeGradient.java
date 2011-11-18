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
import com.kitfox.raven.editor.paint.RavenPaintGradient;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
public class RavenNodeGradient extends RavenNodePaint
{
//    public static final String PROP_GRADIENT = "gradient";
//    public final PropertyWrapper<RavenNodeGradient, MultipleGradientStops> gradient =
//            new PropertyWrapper(this, PROP_GRADIENT, MultipleGradientStops.class);

    public static final String PROP_GRADIENT = "gradient";
    public final PropertyWrapper<RavenNodeGradient, RavenPaintGradient> gradient =
            new PropertyWrapper(this, PROP_GRADIENT, RavenPaintGradient.class,
            new RavenPaintGradient(new MultipleGradientStyle(new MultipleGradientStops())));

    protected RavenNodeGradient(int uid)
    {
        super(uid);

        PropertyWrapperAdapter adapt = new PropertyWrapperAdapter()
        {
            @Override
            public void propertyWrapperDataChanged(PropertyChangeEvent evt) {
                clearCache();
            }
        };

        gradient.addPropertyWrapperListener(adapt);
    }

    @Override
    public RavenPaintGradient createPaint()
    {
//        MultipleGradientStyle style = new MultipleGradientStyle(
//                gradient.getValue());
//
//        return new RavenPaintGradient(style);
        return gradient.getValue();
    }

    @Override
    public void fillShape(CyDrawStack renderer, PaintLayout curFillLayout, CyVertexBuffer mesh)
    {
        RavenPaintGradient grad = gradient.getValue();
        MultipleGradientStyle sty = grad.getGradient();
        MultipleGradientStops stops = sty.getStops();

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
            CyMatrix4d m = CyMatrix4d.createIdentity();
            m.set(curFillLayout.getPaintToLocalTransform());
            rec.setTexToLocalMatrix(m);
        }

        rec.setStops(stops.asCyGradientStops());

        renderer.addDrawRecord(rec);
    }

    //-----------------------------------------------
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeGradient>
    {
        public Provider()
        {
            super(RavenNodeGradient.class, "Gradient", "/icons/node/gradient.png");
        }

        @Override
        public RavenNodeGradient createNode(int uid)
        {
            return new RavenNodeGradient(uid);
        }
    }

}
