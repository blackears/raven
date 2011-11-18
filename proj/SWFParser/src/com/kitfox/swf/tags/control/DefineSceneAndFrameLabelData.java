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

import com.kitfox.swf.dataType.RECT;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class DefineSceneAndFrameLabelData extends SWFTag
{
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            int sceneCount = (int)in.getEncodedU32();
            int[] offsets = new int[sceneCount];
            String[] names = new String[sceneCount];
            for (int i = 0; i < sceneCount; ++i)
            {
                offsets[i] = (int)in.getEncodedU32();
                names[i] = in.getString();
            }

            int frameLabelCount = (int)in.getEncodedU32();
            int[] frameNums = new int[frameLabelCount];
            String[] frameNames = new String[frameLabelCount];
            for (int i = 0; i < frameLabelCount; ++i)
            {
                frameNums[i] = (int)in.getEncodedU32();
                frameNames[i] = in.getString();
            }

            return new DefineSceneAndFrameLabelData(offsets, names, frameNums, frameNames);
        }
    }

    public static final int TAG_ID = 86;

    int[] offsets;
    String[] names;
    int[] frameNums;
    String[] frameNames;

    public DefineSceneAndFrameLabelData(int[] offsets, String[] names, int[] frameNums, String[] frameNames)
    {
        this.offsets = offsets;
        this.names = names;
        this.frameNums = frameNums;
        this.frameNames = frameNames;
    }

    public int getTagId()
    {
        return TAG_ID;
    }
}
