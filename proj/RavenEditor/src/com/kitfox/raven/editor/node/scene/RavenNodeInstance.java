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

import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;

/**
 *
 * @author kitfox
 */
public class RavenNodeInstance extends RavenNodeXformable
{
    public static final String PROP_SOURCE = "source";
    public final PropertyWrapper<RavenNodeInstance, RavenNodeXformable> source =
            new PropertyWrapper(
            this, PROP_SOURCE, RavenNodeXformable.class);

    protected RavenNodeInstance(int uid)
    {
        super(uid);

        source.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        RavenNodeXformable other = source.getValue();
        if (other != null)
        {
            other.renderContent(renderer);
        }
    }

//    @Override
//    public Shape getPickShapeLocal()
//    {
//        RavenNodeXformable other = source.getValue();
//        if (other != null)
//        {
//            return other.getPickShapeLocal();
//        }
//
//        return null;
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
//        CyDrawStack renderer = ctx.getDrawStack();

        RavenNodeXformable other = source.getValue();
        if (other != null)
        {
            other.renderContent(ctx);
        }
    }

    @Override
    public CyShape getShapePickLocal()
    {
        RavenNodeXformable other = source.getValue();
        if (other != null)
        {
            return other.getShapePickLocal();
        }

        return null;
    }

    //-----------------------------------------------

    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeInstance>
    {
        public Provider()
        {
            super(RavenNodeInstance.class, "Instance", "/icons/node/instance.png");
        }

        @Override
        public RavenNodeInstance createNode(int uid)
        {
            return new RavenNodeInstance(uid);
        }
    }
}
