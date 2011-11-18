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

package com.kitfox.swf.dataType;

/**
 *
 * @author kitfox
 */
public class FIXED8
{
    private final short whole;
    private final short decimal;

    public FIXED8(short whole, short decimal)
    {
        this.whole = whole;
        this.decimal = decimal;
    }

    public float asFloat()
    {
        float mag = Math.abs(getWhole()) + ((float)getDecimal() / 0x10000);
        return getWhole() < 0 ? -mag : mag;
    }

    public double asDouble()
    {
        double mag = Math.abs(getWhole()) + ((double)getDecimal() / 0x10000);
        return getWhole() < 0 ? -mag : mag;
    }

    /**
     * @return the whole
     */
    public short getWhole() {
        return whole;
    }

    /**
     * @return the decimal
     */
    public short getDecimal() {
        return decimal;
    }
}
