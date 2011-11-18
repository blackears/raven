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
public class DoAction extends SWFTag
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
            ArrayList<ActionRecord> records = new ArrayList<ActionRecord>();
            for (int code = in.getUI8(); code != 0; code = in.getUI8())
            {
                int actionLen = code >= 0x80 ? in.getUI16() : 0;
                ActionRecordLoader loader = loaderMap.get(code);
                records.add(loader.read(in, actionLen));
            }
            return new DoAction(records.toArray(new ActionRecord[records.size()]));
        }
    }

    public static final int TAG_ID = 12;

    ActionRecord[] records;

    public DoAction(ActionRecord[] records) {
        this.records = records;
    }

    public int getTagId()
    {
        return TAG_ID;
    }
}
