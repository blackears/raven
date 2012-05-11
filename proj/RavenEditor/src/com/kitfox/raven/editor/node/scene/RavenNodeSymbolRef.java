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
import com.kitfox.raven.editor.node.scene.property.NodeSymbolReference;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeRoot;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.PropertyWrapper;

/**
 *
 * @author kitfox
 */
public class RavenNodeSymbolRef extends RavenNodeXformable
{
    public static final String PROP_SYMBOL = "symbol";
    public final PropertyWrapper<RavenNodeSymbolRef, NodeSymbolReference> symbolRef =
            new PropertyWrapper(
            this, PROP_SYMBOL, NodeSymbolReference.class);

    protected RavenNodeSymbolRef(int uid)
    {
        super(uid);

        symbolRef.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
//        RavenNodeXformable other = source.getValue();
//        if (other != null)
//        {
//            other.renderContent(renderer);
//        }
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
//        CyDrawStack renderer = ctx.getDrawStack();

        NodeSymbolReference ref = symbolRef.getValue();
        if (ref != null)
        {
            NodeSymbol tgtSym = getSymbol().getDocument().getSymbol(ref.getUid());
            NodeRoot nodeRoot = tgtSym.getRoot();
            
            if (nodeRoot instanceof RavenSymbolRoot)
            {
                RavenSymbolRoot root = (RavenSymbolRoot)nodeRoot;
                root.getSceneGraph().render(ctx);
            }
            //other.renderContent(ctx);
        }
    }

    @Override
    public CyShape getShapePickLocal()
    {
        NodeSymbolReference ref = symbolRef.getValue();
        if (ref != null)
        {
            NodeSymbol tgtSym = getSymbol().getDocument().getSymbol(ref.getUid());
            NodeRoot nodeRoot = tgtSym.getRoot();
            
            if (nodeRoot instanceof RavenSymbolRoot)
            {
                RavenSymbolRoot root = (RavenSymbolRoot)nodeRoot;
                return root.getSceneGraph().getShapePickLocal();
            }
            //other.renderContent(ctx);
        }

        return null;
    }

    //-----------------------------------------------

    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeSymbolRef>
    {
        public Provider()
        {
            super(RavenNodeSymbolRef.class, "Symbol Reference", "/icons/node/symbolRef.png");
        }

        @Override
        public RavenNodeSymbolRef createNode(int uid)
        {
            return new RavenNodeSymbolRef(uid);
        }
    }
}
