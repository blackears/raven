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

import com.kitfox.raven.util.Selection;

/**
 *
 * @author kitfox
 */
public class TrackCurveComponentKey extends TrackCurveComponent
{
    private final int frame;

    public TrackCurveComponentKey(PropertyWrapper wrapper, int frame)
    {
        super(wrapper);
        this.frame = frame;
    }

    /**
     * @return the frame
     */
    public int getFrame()
    {
        return frame;
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
        if (this.frame != other.frame)
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
        hash = 41 * hash + this.frame;
        return hash;
    }


    //-----------------------------------
//    static public enum Subcomponent { KNOT_IN, KNOT_OUT };
    public static class Subselect
    {
        private final boolean knotIn;
        private final boolean knotOut;

        public Subselect()
        {
            this(false, false);
        }

        public Subselect(boolean knotIn, boolean knotOut)
        {
            this.knotIn = knotIn;
            this.knotOut = knotOut;
        }

        /**
         * @return the knotIn
         */
        public boolean isKnotIn()
        {
            return knotIn;
        }

        /**
         * @return the knotOut
         */
        public boolean isKnotOut()
        {
            return knotOut;
        }

        public Subselect selectKnotIn(Selection.Type type)
        {
            switch (type)
            {
                default:
                case REPLACE:
                    return new Subselect(true, false);
                case ADD:
                    return new Subselect(true, knotOut);
                case SUB:
                    return new Subselect(false, knotOut);
                case INVERSE:
                    return new Subselect(!knotIn, knotOut);
            }
        }

        public Subselect selectKnotOut(Selection.Type type)
        {
            switch (type)
            {
                default:
                case REPLACE:
                    return new Subselect(false, true);
                case ADD:
                    return new Subselect(knotIn, true);
                case SUB:
                    return new Subselect(knotIn, false);
                case INVERSE:
                    return new Subselect(knotIn, !knotOut);
            }
        }
    }
}
