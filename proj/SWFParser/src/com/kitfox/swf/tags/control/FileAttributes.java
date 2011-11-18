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

import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class FileAttributes extends SWFTag
{
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            in.getUB(1);  //Reserved

            boolean useDirectBlit = in.getUB(1) != 0;
            boolean useGPU = in.getUB(1) != 0;
            boolean hasMetaData = in.getUB(1) != 0;
            boolean hasActionScript3 = in.getUB(1) != 0;
            in.getUB(2);  //Reserved
            
            boolean useNetwork = in.getUB(1) != 0;

            in.getUB(24);  //Reserved
            in.flushToByteBoundary();
            return new FileAttributes(useDirectBlit, useGPU, hasMetaData, hasActionScript3, useNetwork);
        }
    }

    public static final int TAG_ID = 69;

    boolean useDirectBlit;
    boolean useGPU;
    boolean hasMetaData;
    boolean hasActionScript3;
    boolean useNetwork;

    public FileAttributes(boolean useDirectBlit, boolean useGPU, boolean hasMetaData, boolean hasActionScript3, boolean useNetwork)
    {
        this.useDirectBlit = useDirectBlit;
        this.useGPU = useGPU;
        this.hasMetaData = hasMetaData;
        this.hasActionScript3 = hasActionScript3;
        this.useNetwork = useNetwork;
    }

    public int getTagId()
    {
        return TAG_ID;
    }
}
