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
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintList
{
    public static final String CACHE_REF = "ref";
    
    private final PaintEntry[] entries;

    public RavenPaintList(PaintEntry... entries)
    {
        this.entries = entries;
    }

    public RavenPaintList(CacheList list)
    {
        ArrayList<PaintEntry> entryList = new ArrayList<PaintEntry>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (!(list.get(i) instanceof CacheIdentifier))
            {
                continue;
            }
            CacheIdentifier entryIdent = (CacheIdentifier)list.get(i);

            RavenPaintInline paint = RavenPaintInline.create(entryIdent);
            if (paint != null)
            {
                entryList.add(new PaintEntryInline(paint));
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
                entryList.add(new PaintEntryRef(
                        ((CacheInteger)idEle).getValue()));
                continue;
            }
        }

        entries = entryList.toArray(new PaintEntry[entryList.size()]);
    }

    public static RavenPaintList create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheList))
            {
                return null;
            }
            return new RavenPaintList((CacheList)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * @return the entries
     */
    public PaintEntry[] getEntries()
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

    public RavenPaint lookupPaint(Integer index, NodeSymbol doc)
    {
        if (index == null || index.intValue() == 0)
        {
            return null;
        }
        PaintEntry entry = entries[index.intValue() - 1];
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
        final RavenPaintList other = (RavenPaintList) obj;
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

    abstract public static class PaintEntry
    {
        abstract public CacheIdentifier toCache();

        abstract protected RavenPaint getPaint(NodeSymbol doc);
    }

    public static class PaintEntryInline extends PaintEntry
    {
        final RavenPaintInline paint;

        public PaintEntryInline(RavenPaintInline paint)
        {
            this.paint = paint;
        }

        @Override
        public CacheIdentifier toCache()
        {
            return paint.toCache();
        }

        @Override
        protected RavenPaint getPaint(NodeSymbol doc)
        {
            return paint;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PaintEntryInline other = (PaintEntryInline) obj;
            if (this.paint != other.paint && (this.paint == null || !this.paint.equals(other.paint))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.paint != null ? this.paint.hashCode() : 0);
            return hash;
        }

    }

    public static class PaintEntryRef extends PaintEntry
    {
        final int uid;

        public PaintEntryRef(int uid)
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
        protected RavenPaint getPaint(NodeSymbol doc)
        {
            NodeObject node = doc.getNode(uid);
            return (RavenPaint)node;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PaintEntryRef other = (PaintEntryRef) obj;
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
