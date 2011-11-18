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

package com.kitfox.rabbit.style;

import com.kitfox.rabbit.parser.RabbitDocument;
import java.util.regex.Pattern;

/**
 *
 * @author kitfox
 */
abstract public class StyleElementLoader
{
    private final String name;
    private final StyleKey key;

    final protected Pattern regexUrl = Pattern.compile("url\\s*\\(\\s*(.*)\\s*\\)");

    public StyleElementLoader(String name, StyleKey key)
    {
        this.name = name;
        this.key = key;
    }

    abstract public Object parse(String value, RabbitDocument builder);

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the key
     */
    public StyleKey getKey() {
        return key;
    }
}
