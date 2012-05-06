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
import com.kitfox.cache.CacheList;
import com.kitfox.raven.editor.node.scene.RavenNodePaint;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeSymbol;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintProxy
{
    public static final String PROP_REF = "ref";

    int refUid;
    RavenPaintInline inlinePaint;

    private RavenPaintProxy(int refUid, RavenPaintInline inlinePaint)
    {
        this.refUid = refUid;
        this.inlinePaint = inlinePaint;
    }

    public RavenPaintProxy(int refUid)
    {
        this(refUid, null);
    }

    public RavenPaintProxy(RavenPaintInline inlinePaint)
    {
        this(-1, inlinePaint);
    }

    public static RavenPaintProxy create(RavenPaint value)
    {
        if (value instanceof RavenNodePaint)
        {
            return new RavenPaintProxy(((RavenNodePaint)value).getUid());
        }

        return new RavenPaintProxy((RavenPaintInline)value);
    }

    public RavenPaint getPaint(NodeSymbol doc)
    {
        if (inlinePaint != null)
        {
            return inlinePaint;
        }

        return (RavenPaint)doc.getNode(refUid);
    }

    public RavenPaintInline getPaintInline(NodeSymbol doc)
    {
        if (inlinePaint != null)
        {
            return inlinePaint;
        }

        return ((RavenNodePaint)doc.getNode(refUid)).createPaint();
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
        final RavenPaintProxy other = (RavenPaintProxy) obj;
        if (this.refUid != other.refUid)
        {
            return false;
        }
        if (this.inlinePaint != other.inlinePaint && (this.inlinePaint == null || !this.inlinePaint.equals(other.inlinePaint)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 79 * hash + this.refUid;
        hash = 79 * hash + (this.inlinePaint != null ? this.inlinePaint.hashCode() : 0);
        return hash;
    }

    //----------------------------------------
    @ServiceInst(service=PlaneDataProvider.class)
    public static class PlaneData extends PlaneDataProvider<RavenPaintProxy>
    {
        public PlaneData()
        {
            super(RavenPaintProxy.class, "RavenPaintProxy");
        }

        @Override
        public CacheElement asCache(RavenPaintProxy data)
        {
            if (data.inlinePaint != null)
            {
                return data.inlinePaint.toCache();
            }
            else
            {
                CacheList cache = new CacheList(PROP_REF);
                cache.add(data.refUid);
                return cache;
            }
        }

        @Override
        public RavenPaintProxy parse(CacheElement cacheElement)
        {
            if (cacheElement instanceof CacheIdentifier)
            {
                CacheIdentifier ident = (CacheIdentifier)cacheElement;
                String name = ident.getName();
                if (PROP_REF.equals(name))
                {
                    int uid = ((CacheList)ident).getInteger(0, 0);
                    return new RavenPaintProxy(uid);
                }

                return new RavenPaintProxy(RavenPaintInline.create(ident));
            }

            throw new UnsupportedOperationException();
        }
    }

}
