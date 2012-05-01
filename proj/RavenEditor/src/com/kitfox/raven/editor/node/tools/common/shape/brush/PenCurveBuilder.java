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

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.builder.BezierPointNd;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierBuilder;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PenCurveBuilder extends PiecewiseBezierBuilder
{
    public PenCurveBuilder(double maxError)
    {
        super(3, maxError);
    }

    @Override
    protected double distanceSpatial(BezierPointNd p0, BezierPointNd p1)
    {
        return Math2DUtil.dist(p0.get(0), p0.get(1), p1.get(0), p1.get(1));
    }

    @Override
    protected double distanceError(BezierPointNd p0, BezierPointNd p1)
    {
        double dx = p1.get(0) - p0.get(0);
        double dy = p1.get(1) - p0.get(1);
        double dp = p1.get(2) - p0.get(2);
        
        return Math.sqrt(dx * dx + dy * dy + dp * dp);
    }
    
}
