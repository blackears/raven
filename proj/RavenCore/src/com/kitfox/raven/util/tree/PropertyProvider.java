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

package com.kitfox.raven.util.tree;

/**
 * Defines a property used by the RavenDocument.  Provides methods to
 * serialize properties, create property editors and to interpolate
 * properties between keyframes.
 *
 * @author kitfox
 */
abstract public class PropertyProvider<T>
{
    private final Class<T> propertyType;

    public PropertyProvider(Class<T> propertyType)
    {
        this.propertyType = propertyType;
    }

    public Class<T> getPropertyType()
    {
        return propertyType;
    }

    protected double fraction(int frame, int frameStart, int frameEnd)
    {
        return (frame - frameStart) / (double)(frameEnd - frameStart);
    }

    /**
     * Interpolates a value between the from and to points on a curve.
     *
     * The tangent and span components are only used when interpolating
     * a bezier curve.  Tangent units are (values/frames).  Tangent
     * magnitude is unimportant - it's just specified as a tuple so
     * divide-by-zero cases are avoided.
     *
     *
     * Math behind Bezier interpolation:
     * Eqn of cubic interpolation for f(0) = y0, f(1) = y1, f'(0) = y0', f'(1) = y1'
     *
     * X = [0 0 0 1]  C = [a]   Y = [y0 ]
     *     [1 1 1 1]      [b]       [y1 ]
     *     [0 0 1 0]      [c]       [y0']
     *     [3 2 1 0]      [d]       [y1']
     *
     *  X C = Y
     *  C = X^-1 Y
     *
     * So X^-1 = [ 2 -2  1  1]
     *           [-3  3 -2 -1]
     *           [ 0  0  1  0]
     *           [ 1  0  0  0]
     *
     * @param interpolator Type of interpolation to perform
     * @param from Value of fn at from point
     * @param fromTanX X tangent component of fn at from point
     * @param fromTanY Y tangent component of fn at from point
     * @param to Value of fn at to point
     * @param toTanX X tangent component of fn at to point
     * @param toTanY Y tangent component of fn at to point
     * @param span Number of frames between to and from
     * @param alpha Fraction of distance between from and to for interpolation
     * @return
     */
    protected double interpolate(TrackKey.Interp interpolator,
            double from, double fromTanX, double fromTanY,
            double to, double toTanX, double toTanY,
            int span, double alpha)
    {
        switch (interpolator)
        {
            default:
            case CONST:
                return from;
            case LINEAR:
                return (1 - alpha) * from + alpha * to;
            case SMOOTH_STEP:
                alpha = (-2 * alpha + 3) * alpha * alpha;
                return (1 - alpha) * from + alpha * to;
            case SMOOTH:
            case BEZIER:
            {
                //Tangents mapped to a span of [0 1]
                double dydx0;
                double dydx1;
                dydx0 = span * fromTanY / fromTanX;
                dydx1 = span * toTanY / toTanX;

                double y0 = from;
                double y1 = to;

                double a = 2 * y0 + -2 * y1 + dydx0 + dydx1;
                double b = -3 * y0 + 3 * y1 + -2 * dydx0 - dydx1;
                double c = dydx0;
                double d = y0;

                return ((a * alpha + b) * alpha + c) * alpha + d;
            }
        }
    }

    /**
     * @return the editor
     */
    abstract public PropertyWrapperEditor createEditor(PropertyWrapper wrapper);

    /**
     * Interpolate two keys of this parameter type.  Default implementation
     * just returns value in k0.  Override to provide more sophisticated
     * interpolation.
     *
     * @param doc Document to dereference against
     * @param k0 First key
     * @param k1 Last Key
     * @param frame Frame to interpolate value for
     * @param k0Frame Frame where k0 is positioned
     * @param k1Frame Frame where k1 is positioned
     * @return
     */
    public T interpolate(NodeDocument doc,
            TrackKey<T> k0, TrackKey<T> k1,
            int frame, int k0Frame, int k1Frame)
    {
        return k0.getData().getValue(doc);
    }

    /**
     * Override if there is a meaningful way to interpolate with an offset
     *
     * @param doc Document to dereference against
     * @param k0 First key
     * @param k1 Last Key
     * @param frame Frame to interpolate value for
     * @param k0Frame Frame where k0 is positioned
     * @param k1Frame Frame where k1 is positioned
     * @param firstKey First key in animation track
     * @param lastKey Last key in animation track
     * @param offsetSize Number of spans to offset by
     * @return
     */
    public T interpolateWithOffset(NodeDocument doc,
            TrackKey<T> k0, TrackKey<T> k1,
            int frame, int k0Frame, int k1Frame,
            TrackKey<T> firstKey, TrackKey<T> lastKey, int offsetSize)
    {
        return interpolate(doc, k0, k1, frame, k0Frame, k1Frame);
    }

    public boolean isNumeric()
    {
        return false;
    }

    /**
     * Used by animation curve editors which need to interpret
     * property as a numeric value.
     *
     * @param value
     * @return
     */
    public double asDouble(T value)
    {
        return 0;
    }

    public T createNumericValue(PropertyWrapper wrapper, double value)
    {
        return null;
    }

    abstract public String asText(T value);
    abstract public T fromText(String text);
}
