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

package com.kitfox.swf.tags.control;

import com.kitfox.swf.dataType.RGB;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ExportAssets extends SWFTag
{
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            int count = in.getUI16();
            int[] tags = new int[count];
            String[] names = new String[count];
            for (int i = 0; i < count; ++i)
            {
                tags[i] = in.getUI16();
                names[i] = in.getString();
            }

            return new ExportAssets(tags, names);
        }
    }

    public static final int TAG_ID = 56;

    int[] tags;
    String[] names;

    public ExportAssets(int[] tags, String[] names)
    {
        this.tags = tags;
        this.names = names;
    }

    public int getTagId()
    {
        return TAG_ID;
    }
}
