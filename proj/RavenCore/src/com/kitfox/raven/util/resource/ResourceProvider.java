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

package com.kitfox.raven.util.resource;

import java.io.File;
import java.net.URI;
import javax.swing.filechooser.FileFilter;

/**
 * Each resource will be served by a ResourceProvider.  This provider
 * will return an object that represents the 'loaded' state of the object.
 * This object will be treated as if it is immutable, so that it may be
 * cached and served to other object properties that request the same
 * resource.
 * 
 * @author kitfox
 */
abstract public class ResourceProvider<ResEdClass>
{
    private final Class<ResEdClass> resourceEditorClass;

    public ResourceProvider(Class<ResEdClass> resourceEditorClass)
    {
        this.resourceEditorClass = resourceEditorClass;
    }

    /**
     * @return the resourceClass
     */
    public Class<ResEdClass> getResourceEditorClass()
    {
        return resourceEditorClass;
    }

    abstract public FileFilter getFileFilter();

    abstract public boolean accepts(URI uri);

    abstract public ResEdClass load(URI uri);

    /**
     * Export resource to library builder.
     * @return Resource id of top level entry in resource library
     */
    abstract public int exportToLibrary(ResourceLibraryBuilder builder, File source);
}
