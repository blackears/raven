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
 *
 * @author kitfox
 */
public class TrackCurveComponentCurve extends TrackCurveComponent
{
    public TrackCurveComponentCurve(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final TrackCurveComponentKey other = (TrackCurveComponentKey) obj;
        if (this.wrapper != other.wrapper && (this.wrapper == null || !this.wrapper.equals(other.wrapper)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 41 * hash + (this.wrapper != null ? this.wrapper.hashCode() : 0);
        return hash;
    }
}
