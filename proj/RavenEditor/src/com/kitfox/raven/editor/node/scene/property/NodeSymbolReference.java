/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.scene.property;

import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.raven.shape.network.NetworkMesh;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class NodeSymbolReference
{
    private final int uid;
    public static final String PROP_UID = "uid";

    public NodeSymbolReference(int uid)
    {
        this.uid = uid;
    }

    public NodeSymbolReference(CacheMap map)
    {
        this.uid = map.getInteger(PROP_UID, -1);
    }

    /**
     * @return the uid
     */
    public int getUid()
    {
        return uid;
    }
    
    public static NodeSymbolReference create(String text)
    {
        if (text == null || "".equals(text))
        {
            return null;
        }
        
        try
        {
            CacheMap map = (CacheMap)CacheParser.parse(text);

            return new NodeSymbolReference(map);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(NetworkMesh.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    

    public CacheMap toCache()
    {
        CacheMap map = new CacheMap();
        map.put(PROP_UID, uid);
        return map;
    }
    
    @Override
    public String toString()
    {
        return toCache().toString();
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
        final NodeSymbolReference other = (NodeSymbolReference)obj;
        if (this.uid != other.uid)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 41 * hash + this.uid;
        return hash;
    }

    
}
