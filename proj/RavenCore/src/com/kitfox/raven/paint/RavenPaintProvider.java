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

package com.kitfox.raven.paint;

import com.kitfox.raven.paint.control.RavenPaintControl;
import java.beans.PropertyEditor;

/**
 *
 * @author kitfox
 */
abstract public class RavenPaintProvider<T extends RavenPaint>
{
    private final Class<T> dataType;
    private final String name;

    public RavenPaintProvider(String name, Class<T> dataType)
    {
        this.name = name;
        this.dataType = dataType;
    }

    abstract public boolean canParse(String text);

    abstract public T fromText(String text);
    
    abstract public String asText(T value);

    abstract public T getDefaultValue();

    
    
    /**
     * @return the dataType
     */
    public Class<T> getDataType()
    {
        return dataType;
    }

    /**
     * @return the propEditorType
     */
    abstract public RavenPaintControl createEditor();

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
}
