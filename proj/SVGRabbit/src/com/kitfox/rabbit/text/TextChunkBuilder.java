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

package com.kitfox.rabbit.text;

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TextChunkBuilder
{
    ArrayList<TextChunk> chunks = new ArrayList<TextChunk>();
    TextChunk curChunk;

    public int getNumChunks()
    {
        return chunks.size();
    }

    public TextChunk getChunk(int index)
    {
        return chunks.get(index);
    }

    public void startChunk(float x, float y, Direction direction, WritingMode writingMode, TextAlign textAlign, TextAnchor textAnchor)
    {
        curChunk = new TextChunk(x, y, direction, writingMode, textAlign, textAnchor);
        chunks.add(curChunk);
    }
    
    public void addGlyphs(GlyphLayout layout)
    {
        curChunk.addSegment(layout);
    }
}
