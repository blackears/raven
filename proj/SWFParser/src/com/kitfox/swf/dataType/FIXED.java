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
public class FIXED extends Number
{
    private final int whole;
    private final int decimal;

    public FIXED(int whole, int decimal)
    {
        this.whole = whole;
        this.decimal = decimal;
    }

    public float floatValue()
    {
        float mag = Math.abs(getWhole()) + ((float)getDecimal() / 0x10000);
        return getWhole() < 0 ? -mag : mag;
    }

    public double doubleValue()
    {
        double mag = Math.abs(getWhole()) + ((double)getDecimal() / 0x10000);
        return getWhole() < 0 ? -mag : mag;
    }

    /**
     * @return the whole
     */
    public int getWhole() {
        return whole;
    }

    /**
     * @return the decimal
     */
    public int getDecimal() {
        return decimal;
    }

    @Override
    public int intValue()
    {
        return whole;
    }

    @Override
    public long longValue()
    {
        return whole;
    }
}
