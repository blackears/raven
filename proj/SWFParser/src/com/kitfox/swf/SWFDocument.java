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

package com.kitfox.swf;

import com.kitfox.swf.tags.SWFTag;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class SWFDocument
{
    private final SWFHeader header;
    ArrayList<SWFTag> tags = new ArrayList<SWFTag>();

    public SWFDocument(SWFHeader header)
    {
        this.header = header;
    }

    /**
     * @return the header
     */
    public SWFHeader getHeader()
    {
        return header;
    }

    public void addTag(SWFTag tag)
    {
        tags.add(tag);
    }

    public ArrayList<SWFTag> getTags()
    {
        return new ArrayList<SWFTag>(tags);
    }
}
