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

package com.kitfox.raven.swf.importer.timeline;

import com.kitfox.swf.dataType.MATRIX;

/**
 *
 * @author kitfox
 */
public class SWFEventPlaceCharacter extends SWFTrackEvent
{
    private int characterId;
    private MATRIX matrix;
    private String name;

    public SWFEventPlaceCharacter(int characterId, MATRIX matrix, String name)
    {
        this.characterId = characterId;
        this.matrix = matrix;
        this.name = name;
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

    
}
