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
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenNodeStrokeLibrary extends RavenNode
{
    public static final String CHILD_STROKES = "strokes";
    public final ChildWrapperList<RavenNodeStrokeLibrary, RavenNodeStroke> strokes =
            new ChildWrapperList(
            this, CHILD_STROKES, RavenNodeStroke.class);

    protected RavenNodeStrokeLibrary(int uid)
    {
        super(uid);
    }


//    private final ChildList<RavenNodeStroke> children = new ChildList<RavenNodeStroke>(RavenNodeStroke.class);
//
//
//    @Override
//    public void getPropertySheet() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    /**
//     * @return the children
//     */
//    public ChildList<RavenNodeStroke> getChildren()
//    {
//        return children;
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeStrokeLibrary>
    {
        public Provider()
        {
            super(RavenNodeStrokeLibrary.class, "Stroke Library", "/icons/node/strokeLibrary.png");
        }

        @Override
        public RavenNodeStrokeLibrary createNode(int uid)
        {
            return new RavenNodeStrokeLibrary(uid);
        }
    }
}
