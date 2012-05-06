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

package com.kitfox.swf.tags.sprite;

import com.kitfox.swf.SWFException;
import com.kitfox.swf.SWFTagIndex;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class DefineSprite extends SWFTag
{
    public static final int TAG_ID = 39;
    
    private int spriteId;
    private int frameCount;

    private ArrayList<SWFTag> tags;

    public DefineSprite(int spriteId, int frameCount, ArrayList<SWFTag> tags)
    {
        this.spriteId = spriteId;
        this.frameCount = frameCount;
        this.tags = tags;
    }

    @Override
    public int getTagId()
    {
        return TAG_ID;
    }

    /**
     * @return the spriteId
     */
    public int getSpriteId()
    {
        return spriteId;
    }

    /**
     * @param spriteId the spriteId to set
     */
    public void setSpriteId(int spriteId)
    {
        this.spriteId = spriteId;
    }

    /**
     * @return the frameCount
     */
    public int getFrameCount()
    {
        return frameCount;
    }

    /**
     * @param frameCount the frameCount to set
     */
    public void setFrameCount(int frameCount)
    {
        this.frameCount = frameCount;
    }

    /**
     * @return the tags
     */
    public ArrayList<SWFTag> getTags()
    {
        return new ArrayList<SWFTag>(tags);
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(ArrayList<SWFTag> tags)
    {
        this.tags = new ArrayList<SWFTag>(tags);
    }
    
    
    //-----------------------------
    private static class TagReader
    {
        ArrayList<SWFTag> tagList = new ArrayList<SWFTag>();
        SWFDataReader in;

        public TagReader(SWFDataReader in)
        {
            this.in = in;
        }
        
        private void parse() throws IOException, SWFException
        {
            while (parseTag(in))
            {
            }
            
        }
        
        private boolean parseTag(SWFDataReader in) throws IOException, SWFException
        {
            int val = in.getUI16();
            int tag = val >> 6;
            int len = val & 0x3f;
            if (len == 0x3f)
            {
                len = in.getSI32();
            }

            int markStart = in.getBytesRead();

            readTag(in, tag, len);

            int markEnd = in.getBytesRead();
            int size = markEnd - markStart;

            if (size != len)
            {
                throw new SWFException("Tag overflow/underflow.  Tag id #" + tag + ", dataOffset: 0x" + Integer.toHexString(markStart) + ".  Declared length: " + len + ".  Bytes read: " + size + ".");
            }

            //End tag has an ID of 0
            return tag != 0;
        }

        public void readTag(SWFDataReader in, int tagId, int length) throws IOException
        {

            SWFTagLoader loader = SWFTagIndex.inst().getLoader(tagId);
            if (loader != null)
            {
//                out.println("Reading tag #" + tagId + "\tlen: " + length + "\t(" + loader.getClass().getName() + ")");
                tagList.add(loader.read(in, length));
                return;
            }

            //Skip this unknown tag
//            if (verbose)
//            {
//                out.println("Skipping unknown tag #" + tagId + "\tlen: " + length);
//            }
            in.skip(length);
        }
    }
    
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            int spriteId = in.getUI16();
            int frameCount = in.getUI16();

            TagReader reader = new TagReader(in);
            try
            {
                reader.parse();
            } catch (SWFException ex)
            {
                Logger.getLogger(DefineSprite.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return new DefineSprite(spriteId, frameCount, reader.tagList);
        }

    }
}
