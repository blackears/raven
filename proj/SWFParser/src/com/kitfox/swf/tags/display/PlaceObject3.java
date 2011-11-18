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

import com.kitfox.swf.dataType.CXFORMWITHALPHA;
import com.kitfox.swf.tags.display.filter.FilterList;
import com.kitfox.swf.dataType.MATRIX;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class PlaceObject3 extends PlaceObject2
{
    public static final int TAG_ID = 70;
    private String className;
    private FilterList filterList;
    private int blendMode;
    private int bitmapCache;

    public PlaceObject3(int depth, String className, int characterId, MATRIX matrix, CXFORMWITHALPHA cxform, int ratio, String name, int clipDepth, FilterList filterList, int blendMode, int bitmapCache, ClipActions clipActions)
    {
        super(depth, characterId, matrix, cxform, ratio, name, clipDepth, clipActions);
        this.className = className;
        this.filterList = filterList;
        this.blendMode = blendMode;
        this.bitmapCache = bitmapCache;
    }

    @Override
    public int getTagId()
    {
        return TAG_ID;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the filterList
     */
    public FilterList getFilterList()
    {
        return filterList;
    }

    /**
     * @param filterList the filterList to set
     */
    public void setFilterList(FilterList filterList)
    {
        this.filterList = filterList;
    }

    /**
     * @return the blendMode
     */
    public int getBlendMode()
    {
        return blendMode;
    }

    /**
     * @param blendMode the blendMode to set
     */
    public void setBlendMode(int blendMode)
    {
        this.blendMode = blendMode;
    }

    /**
     * @return the bitmapCache
     */
    public int getBitmapCache()
    {
        return bitmapCache;
    }

    /**
     * @param bitmapCache the bitmapCache to set
     */
    public void setBitmapCache(int bitmapCache)
    {
        this.bitmapCache = bitmapCache;
    }

    //-------------------------------
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader data, int length) throws IOException
        {
            boolean hasClipActions = data.getUB(1) != 0;
            boolean hasClipDepth = data.getUB(1) != 0;
            boolean hasName = data.getUB(1) != 0;
            boolean hasRatio = data.getUB(1) != 0;
            boolean hasColorXform = data.getUB(1) != 0;
            boolean hasMatrix = data.getUB(1) != 0;
            boolean hasCharacter = data.getUB(1) != 0;
            boolean hasMove = data.getUB(1) != 0;

            data.getUB(3); //reserved

            boolean hasImage = data.getUB(1) != 0;
            boolean hasClassName = data.getUB(1) != 0;
            boolean hasCacheAsBitmap = data.getUB(1) != 0;
            boolean hasBlendMode = data.getUB(1) != 0;
            boolean hasFilterList = data.getUB(1) != 0;

            int depth = data.getUI16();
            String className = (hasClassName || (hasImage && hasCharacter))
                    ? data.getString() : null;
            int characterId = hasCharacter ? data.getUI16() : -1;
            MATRIX matrix = hasMatrix ? data.getMATRIX() : null;
            CXFORMWITHALPHA cxform = hasColorXform ? data.getCXFORMWITHALPHA() : null;
            int ratio = hasRatio ? data.getUI16() : 0;
            String name = hasName ? data.getString() : null;
            int clipDepth = hasClipDepth ? data.getUI16() : -1;

            FilterList filterList = hasFilterList ? new FilterList(data) : null;
            int blendMode = hasBlendMode ? data.getUI8() : 0;
            int bitmapCache = hasCacheAsBitmap ? data.getUI8() : 0;

            ClipActions clipActions = hasClipActions ? new ClipActions(data) : null;

            return new PlaceObject3(depth, className, characterId, matrix,
                    cxform, ratio, name, clipDepth,
                    filterList, blendMode, bitmapCache,
                    clipActions);
        }
    }
}
