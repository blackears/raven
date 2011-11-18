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

package com.kitfox.swf.tags.action;

import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public class DoABC extends SWFTag
{
    public static class Reader extends SWFTagLoader
    {
        ServiceLoader<ActionRecordLoader> loaders = ServiceLoader.load(ActionRecordLoader.class);
        HashMap<Integer, ActionRecordLoader> loaderMap = new HashMap<Integer, ActionRecordLoader>();

        public Reader()
        {
            super(TAG_ID);
            for (Iterator<ActionRecordLoader> it = loaders.iterator(); it.hasNext();)
            {
                ActionRecordLoader loader = it.next();
                loaderMap.put(loader.getActionId(), loader);
            }
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            int markStart = in.getBytesRead();
            int flags = in.getSI32();
            String name = in.getString();

            int markMid = in.getBytesRead();
            byte[] data = new byte[length - (markMid - markStart)];
            in.read(data);

            return new DoABC(flags, name, data);
        }
    }

    public static final int TAG_ID = 82;

    public static final int FLAG_LAZY_INIT = 1 << 0;

    int flags;
    String name;
    byte[] data;

    public DoABC(int flags, String name, byte[] data) {
        this.flags = flags;
        this.name = name;
        this.data = data;
    }

    public int getTagId()
    {
        return TAG_ID;
    }
}
