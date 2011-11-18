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
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class ClipActions
{
    ClipEventFlags allFlags;
    ClipActionsRecord[] records;

    public ClipActions(SWFDataReader data) throws IOException
    {
        data.getUI16();  //Reserved

        this.allFlags = new ClipEventFlags(data);

        ArrayList<ClipActionsRecord> recordList = new ArrayList<ClipActionsRecord>();
        for (int flags = 0; flags != allFlags.getFlags();)
        {
            ClipActionsRecord record = new ClipActionsRecord(data);
            recordList.add(record);
            flags |= record.flags.getFlags();
        }
        this.records = recordList.toArray(new ClipActionsRecord[recordList.size()]);

        //Reserved
        if (data.getVersion() <= 5)
        {
            data.getUI16();
        }
        else
        {
            data.getUI32();
        }
    }

}
