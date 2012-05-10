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

package com.kitfox.raven.util.tree;

import com.kitfox.raven.util.undo.History;

/**
 * Forms the root node of a node tree.
 *
 * @author kitfox
 */
public class NodeRoot extends NodeObject
{
    public static final String CHILD_TRACKLIBRARY = "trackLibrary";
    public final ChildWrapperSingle<NodeRoot, TrackLibrary>
            childTrackLibrary =
            new ChildWrapperSingle(this,
            CHILD_TRACKLIBRARY, TrackLibrary.class);

    private NodeSymbol symbol;
    
    public NodeRoot(int uid)
    {
        super(uid);
    }

    public History getHistory()
    {
        return symbol == null ? null : symbol.getHistory();
    }
    
    /**
     * @return the symbol
     */
    @Override
    public NodeSymbol getSymbol()
    {
        return symbol;
    }
    
    @Override
    public NodeRoot getRoot()
    {
        return this;
    }

    /**
     * @param symbol the symbol to set
     */
    protected void setSymbol(NodeSymbol symbol)
    {
        this.symbol = symbol;
    }

    public TrackLibrary getTrackLibrary()
    {
        return childTrackLibrary.getChild();
    }

    
}
