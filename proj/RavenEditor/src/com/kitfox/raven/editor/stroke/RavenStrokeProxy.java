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
import com.kitfox.cache.CacheList;
import com.kitfox.raven.editor.node.scene.RavenNodeStroke;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenStrokeProxy
{
    public static final String PROP_REF = "ref";

    private int refUid;
    private RavenStrokeInline inlineStroke;

    private RavenStrokeProxy(int refUid, RavenStrokeInline inlineStroke)
    {
        this.refUid = refUid;
        this.inlineStroke = inlineStroke;
    }

    public RavenStrokeProxy(int refUid)
    {
        this(refUid, null);
    }

    public RavenStrokeProxy(RavenStrokeInline inlineStroke)
    {
        this(-1, inlineStroke);
    }
    

    public static RavenStrokeProxy create(RavenStroke value)
    {
        if (value instanceof RavenStrokeProxy)
        {
            return new RavenStrokeProxy(((RavenNodeStroke)value).getUid());
        }

        return new RavenStrokeProxy((RavenStrokeInline)value);
    }

    public RavenStroke getStroke(NodeDocument doc)
    {
        if (inlineStroke != null)
        {
            return inlineStroke;
        }

        return (RavenStroke)doc.getNode(refUid);
    }

    public RavenStrokeInline getStrokeInline(NodeDocument doc)
    {
        if (inlineStroke != null)
        {
            return inlineStroke;
        }

        return ((RavenNodeStroke)doc.getNode(refUid)).getRavenStroke();
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
        final RavenStrokeProxy other = (RavenStrokeProxy) obj;
        if (this.refUid != other.refUid)
        {
            return false;
        }
        if (this.inlineStroke != other.inlineStroke && (this.inlineStroke == null || !this.inlineStroke.equals(other.inlineStroke)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + this.refUid;
        hash = 67 * hash + (this.inlineStroke != null ? this.inlineStroke.hashCode() : 0);
        return hash;
    }


    //------------------------------------------
    @ServiceInst(service=PlaneDataProvider.class)
    public static class PlaneData extends PlaneDataProvider<RavenStrokeProxy>
    {
        public PlaneData()
        {
            super(RavenStrokeProxy.class, "RavenStrokeProxy");
        }

        @Override
        public CacheElement asCache(RavenStrokeProxy data)
        {
            if (data.inlineStroke != null)
            {
                return data.inlineStroke.toCache();
            }
            else
            {
                CacheList cache = new CacheList(PROP_REF);
                cache.add(data.refUid);
                return cache;
            }
        }

        @Override
        public RavenStrokeProxy parse(CacheElement cacheElement)
        {
            if (cacheElement instanceof CacheIdentifier)
            {
                CacheIdentifier ident = (CacheIdentifier)cacheElement;
                String name = ident.getName();
                if (PROP_REF.equals(name))
                {
                    int uid = ((CacheList)ident).getInteger(0, 0);
                    return new RavenStrokeProxy(uid);
                }

                return new RavenStrokeProxy(RavenStrokeInline.create(ident));
            }

            throw new UnsupportedOperationException();
        }
    }

}
