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
import com.kitfox.swf.dataType.MATRIX;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class PlaceObject2 extends SWFTag
{
    public static final int TAG_ID = 26;
    private int depth;
    private int characterId;
    private MATRIX matrix;
    private CXFORMWITHALPHA cxform;
    private int ratio;
    private String name;
    private int clipDepth;
    private ClipActions clipActions;

    public PlaceObject2(int depth, int characterId, MATRIX matrix, CXFORMWITHALPHA cxform, int ratio, String name, int clipDepth, ClipActions clipActions)
    {
        this.depth = depth;
        this.characterId = characterId;
        this.matrix = matrix;
        this.cxform = cxform;
        this.ratio = ratio;
        this.name = name;
        this.clipDepth = clipDepth;
        this.clipActions = clipActions;
    }

    public int getTagId()
    {
        return TAG_ID;
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
     * @return the cxform
     */
    public CXFORMWITHALPHA getCxform()
    {
        return cxform;
    }

    /**
     * @param cxform the cxform to set
     */
    public void setCxform(CXFORMWITHALPHA cxform)
    {
        this.cxform = cxform;
    }

    /**
     * @return the ratio
     */
    public int getRatio()
    {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(int ratio)
    {
        this.ratio = ratio;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the clipDepth
     */
    public int getClipDepth()
    {
        return clipDepth;
    }

    /**
     * @param clipDepth the clipDepth to set
     */
    public void setClipDepth(int clipDepth)
    {
        this.clipDepth = clipDepth;
    }

    /**
     * @return the clipActions
     */
    public ClipActions getClipActions()
    {
        return clipActions;
    }

    /**
     * @param clipActions the clipActions to set
     */
    public void setClipActions(ClipActions clipActions)
    {
        this.clipActions = clipActions;
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

            int depth = data.getUI16();
            int characterId = hasCharacter ? data.getUI16() : -1;
            MATRIX matrix = hasMatrix ? data.getMATRIX() : null;
            CXFORMWITHALPHA cxform = hasColorXform ? data.getCXFORMWITHALPHA() : null;
            int ratio = hasRatio ? data.getUI16() : 0;
            String name = hasName ? data.getString() : null;
            int clipDepth = hasClipDepth ? data.getUI16() : -1;
            ClipActions clipActions = hasClipActions ? new ClipActions(data) : null;

            return new PlaceObject2(depth, characterId, matrix,
                    cxform, ratio, name, clipDepth, clipActions);
        }
    }
}
