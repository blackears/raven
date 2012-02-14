/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.util.tree;

import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ValueCache<T>
{
    FrameKey key;
    PropertyData<T> data;
    HashMap<Class, Object> userCache;

    public ValueCache(FrameKey key, PropertyData<T> data)
    {
        this.key = key;
        this.data = data;
    }

    public void copyState(ValueCache<T> cache)
    {
        this.data = cache.data;
        this.userCache = cache.userCache == null ? null 
                : new HashMap<Class, Object>(cache.userCache);
    }
    
    public <T> void setUserCache(Class<T> clsKey, T data)
    {
        if (userCache == null)
        {
            userCache = new HashMap<Class, Object>();
        }
        userCache.put(clsKey, data);
    }
    
    public <T> T getUserCache(Class<T> clsKey)
    {
        if (userCache == null)
        {
            return null;
        }
        return (T)userCache.get(clsKey);
    }
}
