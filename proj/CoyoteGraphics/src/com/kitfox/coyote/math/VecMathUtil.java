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

package com.kitfox.coyote.math;

/**
 *
 * @author kitfox
 */
public class VecMathUtil
{

    /**
     * Returns the representation of the specified floating-point
     * value according to the IEEE 754 floating-point "double format"
     * bit layout, after first mapping -0.0 to 0.0. This method is
     * identical to Double.doubleToLongBits(double) except that an
     * integer value of 0L is returned for a floating-point value of
     * -0.0. This is done for the purpose of computing a hash code
     * that satisfies the contract of hashCode() and equals(). The
     * equals() method in each vecmath class does a pair-wise "=="
     * test on each floating-point field in the class (e.g., x, y, and
     * z for a Tuple3d). Since 0.0&nbsp;==&nbsp;-0.0 returns true, we
     * must also return the same hash code for two objects, one of
     * which has a field with a value of -0.0 and the other of which
     * has a cooresponding field with a value of 0.0.
     *
     * @param d an input double precision floating-point number
     * @return the integer bits representing that floating-point
     * number, after first mapping -0.0f to 0.0f
     */
    static long doubleToLongBits(double d)
    {
        // Check for +0 or -0
        if (d == 0.0)
        {
            return 0L;
        } else
        {
            return Double.doubleToLongBits(d);
        }
    }
}
