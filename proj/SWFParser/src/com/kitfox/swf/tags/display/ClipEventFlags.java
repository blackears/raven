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

package com.kitfox.swf.tags.display;

import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ClipEventFlags
{
    public static final int ClipEventKeyUp = 1 << 0;
    public static final int ClipEventKeyDown = 1 << 1;
    public static final int ClipEventMouseUp = 1 << 2;
    public static final int ClipEventMouseDown = 1 << 3;
    public static final int ClipEventMouseMove = 1 << 4;
    public static final int ClipEventUnload = 1 << 5;
    public static final int ClipEventEnterFrame = 1 << 6;
    public static final int ClipEventLoad = 1 << 7;
    public static final int ClipEventDragOver = 1 << 8;
    public static final int ClipEventRollOut = 1 << 9;
    public static final int ClipEventRollOver = 1 << 10;
    public static final int ClipEventReleaseOutside = 1 << 11;
    public static final int ClipEventRelease = 1 << 12;
    public static final int ClipEventPress = 1 << 13;
    public static final int ClipEventInitialize = 1 << 14;
    public static final int ClipEventData = 1 << 15;
    public static final int ClipEventConstruct = 1 << 16;
    public static final int ClipEventKeyPress = 1 << 17;
    public static final int ClipEventDragOut = 1 << 18;

    public static final int NUM_FLAGS = 19;

    private final int flags;

    public ClipEventFlags(int flags)
    {
        this.flags = flags;
    }

    public ClipEventFlags(SWFDataReader data) throws IOException
    {
        this(readFlags(data));
    }
    
    private static int readFlags(SWFDataReader data) throws IOException
    {
        int flags = 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventKeyUp : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventKeyDown : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventMouseUp : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventMouseDown : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventMouseMove : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventUnload : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventEnterFrame : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventLoad : 0;

        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventDragOver : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventRollOut : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventRollOver : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventReleaseOutside : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventRelease : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventPress : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventInitialize : 0;
        flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventData : 0;

        if (data.getVersion() >= 6)
        {
            data.getUB(5);  //Reserved
            flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventConstruct : 0;
            flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventKeyPress : 0;
            flags |= data.getUB(1) != 0 ? ClipEventFlags.ClipEventDragOut : 0;
            
            data.getUB(8);  //Reserved
        }

        data.flushToByteBoundary();
        return flags;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }


}
