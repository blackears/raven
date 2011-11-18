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

package com.kitfox.raven.editor.node.scene;

/**
 * Indicates current track/animation frame to draw.  If trackUid == 0,
 * property's direct value is rendered
 *
 * @author kitfox
 */
public class FrameKey
{
    private final int trackUid;
    private final int animFrame;

    public static final FrameKey DIRECT = new FrameKey(0, 0);

    public FrameKey(int trackUid, int animFrame)
    {
        this.trackUid = trackUid;
        this.animFrame = animFrame;
    }

    /**
     * @return the trackUid
     */
    public int getTrackUid()
    {
        return trackUid;
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
        final FrameKey other = (FrameKey) obj;
        if (this.trackUid != other.trackUid)
        {
            return false;
        }
        if (this.animFrame != other.animFrame)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + this.trackUid;
        hash = 29 * hash + this.animFrame;
        return hash;
    }
}
