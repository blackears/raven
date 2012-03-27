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

import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class RavenNodeCompositionLibrary extends RavenNode
{

    public static final String CHILD_COMPOSITIONS = "compositions";
    public final ChildWrapperList<RavenNodeCompositionLibrary, RavenNodeComposition> 
            compositions =
            new ChildWrapperList(
            this, CHILD_COMPOSITIONS, RavenNodeComposition.class);

    protected RavenNodeCompositionLibrary(int uid)
    {
        super(uid);
    }
    
    public ArrayList<RavenNodeComposition> getCompositions()
    {
        return compositions.getChildren();
    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeCompositionLibrary>
    {
        public Provider()
        {
            super(RavenNodeCompositionLibrary.class, 
                    "Composition Library", "/icons/node/compositionLibrary.png");
        }

        @Override
        public RavenNodeCompositionLibrary createNode(int uid)
        {
            return new RavenNodeCompositionLibrary(uid);
        }
    }
    
}
