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

/**
 *
 * @author kitfox
 */
@Deprecated
public class SelectionRecord
{    private final NodeObject node;
    private final Object subselection;

    public SelectionRecord(NodeObject node)
    {
        this.node = node;
        this.subselection = null;
    }

    @Deprecated
    public SelectionRecord(NodeObject node, Object subselection)
    {
        this.node = node;
        this.subselection = subselection;
    }

    /**
     * @return the node
     */
    public NodeObject getNode()
    {
        return node;
    }

    /**
     * @return the subselection
     */
    @Deprecated
    public Object getSubselection()
    {
        return subselection;
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
        final SelectionRecord other = (SelectionRecord)obj;
        if (this.node != other.node && (this.node == null || !this.node.equals(other.node)))
        {
            return false;
        }
        if (this.subselection != other.subselection && (this.subselection == null || !this.subselection.equals(other.subselection)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 31 * hash + (this.node != null ? this.node.hashCode() : 0);
        hash = 31 * hash + (this.subselection != null ? this.subselection.hashCode() : 0);
        return hash;
    }
}
