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

import java.awt.Color;

/**
 *
 * @author kitfox
 */
public class RGBA
{
    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public RGBA(int r, int g, int b, int a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * @return the r
     */
    public int getR() {
        return r;
    }

    /**
     * @return the g
     */
    public int getG() {
        return g;
    }

    /**
     * @return the b
     */
    public int getB() {
        return b;
    }

    /**
     * @return the a
     */
    public int getA() {
        return a;
    }

    public Color asColor()
    {
        return new Color(r, g, b, a);
    }



}
