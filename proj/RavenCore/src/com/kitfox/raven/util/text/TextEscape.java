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

package com.kitfox.raven.util.text;

/**
 *
 * @author kitfox
 */
public class TextEscape
{

    public static String unescapeSpecial(String value)
    {
        StringBuilder sb = new StringBuilder();
        int size = value.length();
        for (int i = 0; i < size; ++i)
        {
            char ch = value.charAt(i);

            if (ch == '\\')
            {
                ++i;
                if (i == size)
                {
                    break;
                }
                char type = value.charAt(i);
                switch (type)
                {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    default:
                        sb.append(type);
                        break;
                }

                continue;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    public static String escapeSpecial(String text)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ++i)
        {
            char ch = text.charAt(i);
            switch (ch)
            {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }


    public static String unescape(String value)
    {
        StringBuilder sb = new StringBuilder();
        int size = value.length();
        for (int i = 0; i < size; ++i)
        {
            char ch = value.charAt(i);

            if (ch == '\\')
            {
                ++i;
                if (i == size)
                {
                    break;
                }
                char type = value.charAt(i);
                switch (type)
                {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'x':
                    {
                        if (i + 2 >= size)
                        {
                            break;
                        }
                        String unicode = value.substring(i + 1, i + 3);
                        int val = Integer.parseInt(unicode, 16);
                        sb.append((char)val);
                        i += 2;
                        break;
                    }
                    case 'u':
                    {
                        if (i + 4 >= size)
                        {
                            break;
                        }
                        String unicode = value.substring(i + 1, i + 5);
                        int val = Integer.parseInt(unicode, 16);
                        sb.append((char)val);
                        i += 4;
                        break;
                    }
                    default:
                        sb.append(type);
                        break;
                }

                continue;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    public static String escape(String text)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ++i)
        {
            char ch = text.charAt(i);
            switch (ch)
            {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (ch >= 0x100)
                    {
                        sb.append(String.format("\\u%04x", (int)ch));
                    }
                    else if (ch < ' ')
                    {
                        sb.append(String.format("\\x%02x", (int)ch));
                    }
                    else
                    {
                        sb.append(ch);
                    }
                    break;
            }
        }
        return sb.toString();
    }

}
