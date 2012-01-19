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

package com.kitfox.rabbit.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kitfox
 */
public class NumberText
{
    public static final String REGEX_FLOAT = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
    public static final String REGEX_INT = "[-+]?\\d+";

    public static float findFloat(String text, float defaultValue)
    {
        if (text == null)
        {
            return defaultValue;
        }

        Matcher match = Pattern.compile(REGEX_FLOAT).matcher(text);
        if (match.find())
        {
            return Float.parseFloat(match.group());
        }
        return defaultValue;
    }

    public static float[] findFloatArray(String text)
    {
        ArrayList<Float> values = new ArrayList<Float>();
        Matcher match = Pattern.compile(REGEX_FLOAT).matcher(text);
        while (match.find())
        {
            values.add(Float.parseFloat(match.group()));
        }

        float[] list = new float[values.size()];
        for (int i = 0; i < list.length; ++i)
        {
            list[i] = values.get(i);
        }
        return list;
    }

    public static String asStringCodeGen(float[] list)
    {
        if (list == null)
        {
            return null;
        }
        if (list.length == 0)
        {
            return "new float[0]";
        }

        return "new float[]{" + asString(list, "f,") + "f}";
    }

    public static String asString(float[] list, String separator)
    {
        if (list.length == 0)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(list[0]);

        for (int i = 1; i < list.length; ++i)
        {
            sb.append(separator);
            sb.append(list[i]);
        }
        return sb.toString();
    }

    public static double findDouble(String text, double defaultValue)
    {
        Matcher match = Pattern.compile(REGEX_FLOAT).matcher(text);
        if (match.find())
        {
            return Double.parseDouble(match.group());
        }
        return defaultValue;
    }

    public static String asString(int[] list, String separator)
    {
        if (list == null || list.length == 0)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(list[0]);

        for (int i = 1; i < list.length; ++i)
        {
            sb.append(separator);
            sb.append(list[i]);
        }
        return sb.toString();
    }

    public static String asStringCodeGen(int[] list)
    {
        if (list == null)
        {
            return null;
        }
        if (list.length == 0)
        {
            return "new int[0]";
        }

        return "new int[]{" + asString(list, ",") + "}";
    }

    public static String cleanFloatArray(String text, String separator)
    {
        float[] arr = findFloatArray(text);
        return asString(arr, separator);
    }

    public static int findInteger(String text, int defaultValue)
    {
        Matcher match = Pattern.compile(REGEX_INT).matcher(text);
        if (match.find())
        {
            return Integer.parseInt(match.group());
        }
        return defaultValue;
    }

    public static int[] findIntegerArray(String text)
    {
        ArrayList<Integer> values = new ArrayList<Integer>();
        Matcher match = Pattern.compile(REGEX_INT).matcher(text);
        while (match.find())
        {
            values.add(Integer.parseInt(match.group()));
        }

        int[] list = new int[values.size()];
        for (int i = 0; i < list.length; ++i)
        {
            list[i] = values.get(i);
        }
        return list;
    }

}
