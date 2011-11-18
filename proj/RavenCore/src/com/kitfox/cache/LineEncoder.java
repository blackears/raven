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

package com.kitfox.cache;

import java.util.List;
import java.util.Map;

/**
 *
 * @author kitfox
 */
public class LineEncoder
{
    public static String encode(Object obj)
    {
        LineEncoderFactory fact = LineEncoderIndex.inst().getFactory(obj.getClass());

        if (fact == null)
        {
            return null;
        }

        String strn = fact.asString(obj).trim();
        if (strn != null)
        {
            char ch0 = strn.charAt(0);
            if (ch0 == '(' || ch0 == '{'
                    || strn.indexOf('"') != -1
                    || strn.indexOf(',') != -1
                    || strn.indexOf(';') != -1)
            {
            }
            return strn;
        }

        List<Object> list = fact.asList(obj);
        if (list != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            int size = list.size();
            if (size > 0)
            {
                sb.append(encode(list.get(0)));
            }
            for (int i = 1; i < size; ++i)
            {
                sb.append(',');
                sb.append(encode(list.get(i)));
            }
            sb.append(')');
            return sb.toString();
        }

        Map<String, Object> map = fact.asMap(obj);
        if (list != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            for (String key: map.keySet())
            {
                if (sb.length() != 1)
                {
                    sb.append(';');
                }
                sb.append(key);
                sb.append(':');
                sb.append(encode(map.get(key)));
            }
            sb.append('}');
            return sb.toString();
        }

        return null;
    }
}
