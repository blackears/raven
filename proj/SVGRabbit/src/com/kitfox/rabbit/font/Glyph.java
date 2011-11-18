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

package com.kitfox.rabbit.font;

import java.awt.Shape;

/**
 *
 * @author kitfox
 */
public class Glyph
{
//    float vertOriginX;
//    float vertOriginY;
    private final Shape shape;
    private final float horixAdvX;
    private final float vertAdvY;

    public Glyph(Shape shape, float horixAdvX, float vertAdvY)
    {
        this.shape = shape;
        this.horixAdvX = horixAdvX;
        this.vertAdvY = vertAdvY;
    }

    /**
     * @return the shape
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @return the horixAdvX
     */
    public float getHorixAdvX() {
        return horixAdvX;
    }

    /**
     * @return the vertAdvY
     */
    public float getVertAdvY() {
        return vertAdvY;
    }



}
