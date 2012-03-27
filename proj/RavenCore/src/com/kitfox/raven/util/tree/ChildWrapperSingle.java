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
import com.kitfox.raven.util.undo.HistoryAction;

/**
 *
 * @author kitfox
 */
public class ChildWrapperSingle<NodeType extends NodeObject, ChildType extends NodeObject> extends ChildWrapper<NodeType, ChildType>
{
    ChildType child;

    public ChildWrapperSingle(NodeType node, String name, Class<ChildType> childType)
    {
        super(node, name, childType);
//        this.child = child;
//        child.setParent(node);
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public ChildType get(int index)
    {
        return child;
    }

    public ChildType getChild()
    {
        return child;
    }

    @Override
    public void set(int index, ChildType child)
    {
        SetChildAction action = new SetChildAction(this.child, child);
        doAction(action);
    }

    public void set(ChildType child)
    {
        set(child, true);
    }

    public void set(ChildType child, boolean history)
    {
        SetChildAction action = new SetChildAction(this.child, child);
        if (history)
        {
            doAction(action);
        }
        else
        {
            action.redo(null);
        }
    }

    //----------------------------------
    public class SetChildAction implements HistoryAction
    {
        private final ChildType childOld;
        private final ChildType childNew;

        public SetChildAction(ChildType childOld, ChildType childNew)
        {
            this.childOld = childOld;
            this.childNew = childNew;
        }

        @Override
        public void undo(History history)
        {
            child = childOld;

            if (childOld != null)
            {
                childOld.setParent(ChildWrapperSingle.this);
            }
            childNew.setParent(null);
            fireNodeRemoved(0, childNew);
            fireNodeAdded(0, childOld);
        }

        @Override
        public void redo(History history)
        {
            child = childNew;

            if (childOld != null)
            {
                childOld.setParent(null);
            }
            childNew.setParent(ChildWrapperSingle.this);
            fireNodeRemoved(0, childOld);
            fireNodeAdded(0, childNew);
        }

        @Override
        public String getTitle()
        {
            return "Set child: " + childNew.toString();
        }
    }

    
}
