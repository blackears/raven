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
public class TextChunk
{

    private final float x;
    private final float y;
    private final Direction direction;
    private final WritingMode writingMode;
    private final TextAlign textAlign;
    private final TextAnchor textAnchor;
    
    private ArrayList<GlyphLayout> segments = new ArrayList<GlyphLayout>();

    public TextChunk(float x, float y, Direction direction, WritingMode writingMode, TextAlign textAlign, TextAnchor textAnchor)
    {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.writingMode = writingMode;
        this.textAlign = textAlign;
        this.textAnchor = textAnchor;
    }

    public int getNumSegments()
    {
        return segments.size();
    }

    public GlyphLayout getSegment(int index)
    {
        return segments.get(index);
    }

    public void addSegment(GlyphLayout seg)
    {
        segments.add(seg);
    }

    public float getHorizAdvX()
    {
        float width = 0;
        for (int i = 0; i < segments.size(); ++i)
        {
            width += segments.get(i).getHorizAdvX();
        }
        return width;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @return the writingMode
     */
    public WritingMode getWritingMode() {
        return writingMode;
    }

    /**
     * @return the textAlign
     */
    public TextAlign getTextAlign() {
        return textAlign;
    }

    /**
     * @return the textAnchor
     */
    public TextAnchor getTextAnchor() {
        return textAnchor;
    }

    /**
     * @return the segments
     */
    public ArrayList<GlyphLayout> getSegments() {
        return segments;
    }


}
