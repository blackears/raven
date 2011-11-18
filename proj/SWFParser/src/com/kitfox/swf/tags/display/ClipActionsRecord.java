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
public class ClipActionsRecord
{
    ClipEventFlags flags;
    int keycode;

    public ClipActionsRecord(ClipEventFlags flags, int keycode)
    {
        this.flags = flags;
        this.keycode = keycode;
    }

    public ClipActionsRecord(SWFDataReader data) throws IOException
    {
        this.flags = new ClipEventFlags(data);

        long recordSize = data.getUI32();
        this.keycode = -1;
        if ((flags.getFlags() & ClipEventFlags.ClipEventKeyPress) != 0)
        {
            //Key to trap for buttons
            keycode = data.getUI8();
            recordSize -= 1;
        }

        //Get actions

        //For now, just skip actions
        data.skip(recordSize);
    }



}
