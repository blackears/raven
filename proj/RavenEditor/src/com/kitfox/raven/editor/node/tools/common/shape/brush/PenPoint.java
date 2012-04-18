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

package com.kitfox.raven.editor.node.tools.common.shape.brush;

/**
 *
 * @author kitfox
 */
public class PenPoint
{
    float x;
    float y;
    float pressure;
    float tiltX;
    float tiltY;

    public PenPoint(float x, float y, float pressure, float tiltX, float tiltY)
    {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.tiltX = tiltX;
        this.tiltY = tiltY;
    }

    public PenPoint(float x, float y, float pressure)
    {
        this(x, y, pressure, 0, 0);
    }

    public PenPoint(float x, float y)
    {
        this(x, y, 1, 0, 0);
    }

    @Override
    public String toString()
    {
        return String.format("pos (%f %f) pres %f tilt(%f %f)",
                x, y, pressure, tiltX, tiltY);
    }
    
}
