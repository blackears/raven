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

package com.kitfox.rabbit.types;

/**
 *
 * @author kitfox
 */
public class RaLength extends Number
{
    static public enum Type { NONE, EM, EX, PX, IN, CM, MM, PT, PC, PERCENT }
    private final float value;
    private final Type type;

    public RaLength(float value)
    {
        this(value, Type.NONE);
    }

    public RaLength(float value, Type type)
    {
        this.value = value;
        this.type = type;
    }

    public String getUnitsStrn()
    {
        switch (type)
        {
            case CM:
                return "cm";
            case EM:
                return "em";
            case EX:
                return "ex";
            case IN:
                return "in";
            case MM:
                return "mm";
            default:
            case NONE:
                return "";
            case PC:
                return "pc";
            case PERCENT:
                return "%";
            case PT:
                return "pt";
            case PX:
                return "px";
        }
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    @Override
    public int intValue()
    {
        return (int)value;
    }

    @Override
    public long longValue()
    {
        return (long)value;
    }

    @Override
    public float floatValue()
    {
        return value;
    }

    @Override
    public double doubleValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "" + value + getUnitsStrn();
    }

}
