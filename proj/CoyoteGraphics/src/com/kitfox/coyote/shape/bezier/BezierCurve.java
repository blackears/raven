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

package com.kitfox.coyote.shape.bezier;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
abstract public class BezierCurve
{
    abstract public BezierCurve reverse();

    abstract public double getTanInX();
    abstract public double getTanInY();
    abstract public double getTanOutX();
    abstract public double getTanOutY();

    abstract public double getStartX();
    abstract public double getStartY();
    abstract public double getEndX();
    abstract public double getEndY();

    abstract public BezierCurve[] split(double t);
    abstract public void evaluate(double t, CyVector2d pos, CyVector2d tan);

    abstract public double getCurvatureSquared();

    abstract public BezierCurve offset(double width);

    abstract public void append(PathConsumer out);
}
