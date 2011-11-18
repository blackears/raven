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

package com.kitfox.raven.util.resource.game;

import java.io.InputStream;

/**
 *
 * @author kitfox
 */
public class ResourceGameLibrary
{
    static ResourceGameLibrary instance = new ResourceGameLibrary();
    private ResourceGameLoader loader;

    private ResourceGameLibrary()
    {
    }
    
    public static ResourceGameLibrary inst()
    {
        return instance;
    }

    /**
     * @return the loader
     */
    public ResourceGameLoader getLoader()
    {
        return loader;
    }

    /**
     * @param loader the loader to set
     */
    public void setLoader(ResourceGameLoader loader)
    {
        this.loader = loader;
    }

    public InputStream openResourceData(int id)
    {
        return loader.openResource(id);
    }
    
    public String getFileName(int id)
    {
        return loader.getFileName(id);
    }

}
