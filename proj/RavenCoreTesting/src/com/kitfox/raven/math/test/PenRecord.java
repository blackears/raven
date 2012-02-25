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

package com.kitfox.raven.math.test;

import jpen.PLevel.Type;
import jpen.Pen;

/**
 *
 * @author kitfox
 */
public class PenRecord
{
    final float pressure;
    final float sidePressure;
    final float x;
    final float y;
    final float tiltX;
    final float tiltY;
    final float rotation;

    public PenRecord(Pen pen)
    {
        this.pressure = pen.getLevelValue(Type.PRESSURE);
        this.sidePressure = pen.getLevelValue(Type.SIDE_PRESSURE);
        this.x = pen.getLevelValue(Type.X);
        this.y = pen.getLevelValue(Type.Y);
        this.tiltX = pen.getLevelValue(Type.TILT_X);
        this.tiltY = pen.getLevelValue(Type.TILT_Y);
        this.rotation = pen.getLevelValue(Type.ROTATION);
    }

    @Override
    public String toString()
    {
        return "Pres: " + pressure
                + " SidePres: " + sidePressure
                + " X: " + x
                + " Y: " + y
                + " TiltX: " + tiltX
                + " TiltY: " + tiltY
                + " Rot: " + rotation;
    }

}
