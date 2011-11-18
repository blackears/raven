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

import com.kitfox.swf.dataType.CXFORM;
import com.kitfox.swf.dataType.MATRIX;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class PlaceObject extends SWFTag
{
    public static final int TAG_ID = 4;
    private int characterId;
    private int depth;
    private MATRIX matrix;
    private CXFORM colXform;

    public PlaceObject(int characterId, int depth, MATRIX matrix, CXFORM colXform)
    {
        this.characterId = characterId;
        this.depth = depth;
        this.matrix = matrix;
        this.colXform = colXform;
    }

    public int getTagId()
    {
        return TAG_ID;
    }

    /**
     * @return the characterId
     */
    public int getCharacterId()
    {
        return characterId;
    }

    /**
     * @param characterId the characterId to set
     */
    public void setCharacterId(int characterId)
    {
        this.characterId = characterId;
    }

    /**
     * @return the depth
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * @return the matrix
     */
    public MATRIX getMatrix()
    {
        return matrix;
    }

    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(MATRIX matrix)
    {
        this.matrix = matrix;
    }

    /**
     * @return the colXform
     */
    public CXFORM getColXform()
    {
        return colXform;
    }

    /**
     * @param colXform the colXform to set
     */
    public void setColXform(CXFORM colXform)
    {
        this.colXform = colXform;
    }

    //------------------------------
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader reader, int length) throws IOException
        {
            return new PlaceObject(reader.getUI16(),
                    reader.getUI16(),
                    reader.getMATRIX(),
                    reader.getCXFORM());
        }
    }
}
