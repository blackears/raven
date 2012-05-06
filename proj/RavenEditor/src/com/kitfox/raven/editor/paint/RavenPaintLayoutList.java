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

package com.kitfox.raven.editor.paint;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheInteger;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutSerializer;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintLayoutList
{
    public static final String CACHE_REF = "ref";
    
    private final PaintLayoutEntry[] entries;

    public RavenPaintLayoutList(PaintLayoutEntry... entries)
    {
        this.entries = entries;
    }

    public RavenPaintLayoutList(CacheList list)
    {
        ArrayList<PaintLayoutEntry> entryList = new ArrayList<PaintLayoutEntry>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (!(list.get(i) instanceof CacheIdentifier))
            {
                continue;
            }
            CacheIdentifier entryIdent = (CacheIdentifier)list.get(i);

            if (entryIdent instanceof CacheMap)
            {
                PaintLayout layout = PaintLayoutSerializer.create((CacheMap)entryIdent);
                if (layout != null)
                {
                    entryList.add(new PaintLayoutEntryInline(layout));
                    continue;
                }
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
                entryList.add(new PaintLayoutEntryRef(
                        ((CacheInteger)idEle).getValue()));
                continue;
            }
        }

        entries = entryList.toArray(new PaintLayoutEntry[entryList.size()]);
    }

    public static RavenPaintLayoutList create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheList))
            {
                return null;
            }
            return new RavenPaintLayoutList((CacheList)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * @return the entries
     */
    public PaintLayoutEntry[] getEntries()
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

    public PaintLayout lookupPaint(Integer index, NodeSymbol doc)
    {
        if (index == null || index.intValue() == 0)
        {
            return null;
        }
        PaintLayoutEntry entry = entries[index.intValue() - 1];
        return entry.getPaint(doc);
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
        final RavenPaintLayoutList other = (RavenPaintLayoutList) obj;
        if (!Arrays.deepEquals(this.entries, other.entries)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Arrays.deepHashCode(this.entries);
        return hash;
    }


    //--------------------------

    abstract public static class PaintLayoutEntry
    {
        abstract public CacheIdentifier toCache();

        abstract protected PaintLayout getPaint(NodeSymbol doc);
    }

    public static class PaintLayoutEntryInline extends PaintLayoutEntry
    {
        final PaintLayout layout;

        public PaintLayoutEntryInline(PaintLayout paint)
        {
            this.layout = paint;
        }

        @Override
        public CacheIdentifier toCache()
        {
            return layout.toCache();
        }

        @Override
        protected PaintLayout getPaint(NodeSymbol doc)
        {
            return layout;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PaintLayoutEntryInline other = (PaintLayoutEntryInline) obj;
            if (this.layout != other.layout &&
                    (this.layout == null || !this.layout.equals(other.layout))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.layout != null ? this.layout.hashCode() : 0);
            return hash;
        }
    }

    public static class PaintLayoutEntryRef extends PaintLayoutEntry
    {
        final int uid;

        public PaintLayoutEntryRef(int uid)
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
        protected PaintLayout getPaint(NodeSymbol doc)
        {
            NodeObject node = doc.getNode(uid);
            return (PaintLayout)node;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PaintLayoutEntryRef other = (PaintLayoutEntryRef)obj;
            if (this.uid != other.uid) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this.uid;
            return hash;
        }
    }

}
