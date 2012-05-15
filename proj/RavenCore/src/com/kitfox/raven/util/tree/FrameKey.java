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
 * Indicates current track/animation frame to draw.  If trackUid == 0,
 * property's direct value is rendered
 *
 * @author kitfox
 */
public class FrameKey
{
//    private final int trackUid;
    private final int animFrame;
    boolean direct;

    public static final FrameKey DIRECT = new FrameKey(0, true);

    private FrameKey(int animFrame, boolean direct)
    {
        this.animFrame = animFrame;
        this.direct = direct;
    }

    public FrameKey(int animFrame)
    {
        this(animFrame, false);
    }

    /**
     * @return the animFrame
     */
    public int getAnimFrame()
    {
        return animFrame;
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
        final FrameKey other = (FrameKey)obj;
        if (this.animFrame != other.animFrame)
        {
            return false;
        }
        if (this.direct != other.direct)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 83 * hash + this.animFrame;
        hash = 83 * hash + (this.direct ? 1 : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return direct ? "(DIRECT)" : "(" + animFrame + ")";
    }
    
    
}
