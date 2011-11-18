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

package com.kitfox.raven.util;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kitfox
 */
public class UniqueNameBuilder
{
    HashSet<String> nameSet = new HashSet<String>();

    public UniqueNameBuilder()
    {
    }

    public boolean containsName(String name)
    {
        return nameSet.contains(name);
    }

    public void addReferenceName(String name)
    {
        nameSet.add(name);
    }

    public String createUniqueName(String rootName)
    {
        int idx = 0;
        Matcher m = Pattern.compile("\\d+$").matcher(rootName);
        if (m.find())
        {
            String val = m.group();
            rootName = rootName.substring(0, rootName.length() - val.length());
            idx = Integer.parseInt(val);
        }

        String name = rootName;
        while (nameSet.contains(name))
        {
            name = rootName + ++idx;
        }

        return name;
    }

}
