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

package com.kitfox.coyote.shape.bezier.builder;

import com.kitfox.coyote.math.Math2DUtil;

/**
 * Must implement equals() and hashCode()
 *
 * @author kitfox
 */
public class BezierPointNd
{
    double[] elements;
    
    public BezierPointNd(double... elements)
    {
        this.elements = elements;
    }

    public BezierPointNd(BezierPointNd p)
    {
        this(p.elements.clone());
    }
    
    /**
     * Get the indicated element of this tuple
     * 
     * @param index
     * @return 
     */
    public double get(int index)
    {
        return elements[index];
    }
    
    public BezierPointNd lerp(BezierPointNd p, double t)
    {
        if (p.elements.length != elements.length)
        {
            throw new IllegalArgumentException();
        }
        
        double[] newEles = new double[elements.length];
        for (int i = 0; i < elements.length; ++i)
        {
            newEles[i] = Math2DUtil.lerp(elements[i], p.elements[i], t);
        }
        return new BezierPointNd(newEles);
    }

    public void add(BezierPointNd p)
    {
        for (int i = 0; i < elements.length; ++i)
        {
            elements[i] += p.elements[i];
        }
    }

    public void sub(BezierPointNd p)
    {
        for (int i = 0; i < elements.length; ++i)
        {
            elements[i] -= p.elements[i];
        }
    }

    public double dot(BezierPointNd p)
    {
        double sum = 0;
        for (int i = 0; i < elements.length; ++i)
        {
            sum += elements[i] * p.elements[i];
        }
        return sum;
    }

    public void scale(double s)
    {
        for (int i = 0; i < elements.length; ++i)
        {
            elements[i] *= s;
        }        
    }
}
