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

package com.kitfox.rabbit.codegen;

import com.kitfox.rabbit.style.StyleKey;

/**
 *
 * @author kitfox
 */
public class StyleInfo
{
    private final StyleKey key;
    private final String initValue;

    public StyleInfo(StyleKey key, String initValue)
    {
        this.key = key;
        this.initValue = initValue;
    }

    /**
     * @return the key
     */
    public StyleKey getKey() {
        return key;
    }

    /**
     * @return the initValue
     */
    public String getInitValue() {
        return initValue;
    }


}
