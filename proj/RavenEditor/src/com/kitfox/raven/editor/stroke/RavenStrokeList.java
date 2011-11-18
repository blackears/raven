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

package com.kitfox.raven.editor.stroke;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheInteger;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
public class RavenStrokeList
{
    public static final String CACHE_REF = "ref";
    
    private final StrokeEntry[] entries;

    public RavenStrokeList(StrokeEntry... entries)
    {
        this.entries = entries;
    }

    public RavenStrokeList(CacheList list)
    {
        ArrayList<StrokeEntry> entryList = new ArrayList<StrokeEntry>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (!(list.get(i) instanceof CacheIdentifier))
            {
                continue;
            }
            CacheIdentifier entryIdent = (CacheIdentifier)list.get(i);

            RavenStrokeInline stroke = RavenStrokeInline.create(entryIdent);
            if (stroke != null)
            {
                entryList.add(new StrokeEntryInline(stroke));
                continue;
            }

            String name = entryIdent.getName();
            if (CACHE_REF.equalsIgnoreCase(name))
            {
                if (!(entryIdent instanceof CacheList))
                {
                    continue;
                }
                CacheList cacheList = (CacheList)entryIdent;
                if (cacheList.isEmpty())
                {
                    continue;
                }
                CacheElement idEle = cacheList.get(0);
                if (!(idEle instanceof CacheInteger))
                {
                    continue;
                }
                entryList.add(new StrokeEntryRef(
                        ((CacheInteger)idEle).getValue()));
                continue;
            }
        }

        entries = entryList.toArray(new StrokeEntry[entryList.size()]);
    }

    public static RavenStrokeList create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheList))
            {
                return null;
            }
            return new RavenStrokeList((CacheList)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * @return the entries
     */
    public StrokeEntry[] getEntries()
    {
        return entries.clone();
    }

    public CacheList toCache()
    {
        CacheList list = new CacheList();

        for (int i = 0; i < entries.length; ++i)
        {
            list.add(entries[i].toCache());
        }

        return list;
    }

    public RavenStroke lookupStroke(Integer index, NodeDocument doc)
    {
        if (index == null || index.intValue() == 0)
        {
            return null;
        }
        StrokeEntry entry = entries[index.intValue() - 1];
        return entry.getStroke(doc);
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RavenStrokeList other = (RavenStrokeList) obj;
        if (!Arrays.deepEquals(this.entries, other.entries)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.deepHashCode(this.entries);
        return hash;
    }


    //--------------------------

    abstract public static class StrokeEntry
    {
        abstract public CacheIdentifier toCache();

        abstract protected RavenStroke getStroke(NodeDocument doc);
    }

    public static class StrokeEntryInline extends StrokeEntry
    {
        final RavenStrokeInline stroke;

        public StrokeEntryInline(RavenStrokeInline stroke)
        {
            this.stroke = stroke;
        }

        @Override
        public CacheIdentifier toCache()
        {
            return stroke.toCache();
        }

        @Override
        protected RavenStroke getStroke(NodeDocument doc)
        {
            return stroke;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StrokeEntryInline other = (StrokeEntryInline) obj;
            if (this.stroke != other.stroke && (this.stroke == null || !this.stroke.equals(other.stroke))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.stroke != null ? this.stroke.hashCode() : 0);
            return hash;
        }
    }

    public static class StrokeEntryRef extends StrokeEntry
    {
        final int uid;

        public StrokeEntryRef(int uid)
        {
            this.uid = uid;
        }

        @Override
        public CacheIdentifier toCache()
        {
            CacheList list = new CacheList();
            list.setName(CACHE_REF);
            list.add(uid);
            return list;
        }

        @Override
        protected RavenStroke getStroke(NodeDocument doc)
        {
            NodeObject node = doc.getNode(uid);
            return (RavenStroke)node;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StrokeEntryRef other = (StrokeEntryRef) obj;
            if (this.uid != other.uid) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + this.uid;
            return hash;
        }
    }

}
