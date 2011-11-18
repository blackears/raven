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

package com.kitfox.swf.tags.shapes;

import com.kitfox.swf.dataType.RECT;
import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class DefineShape extends SWFTag
{
    public static final int TAG_ID = 2;
    private int shapeId;
    private RECT shapeBounds;
    private ShapeWithStyle shapes;

    public DefineShape(int shapeId, RECT shapeBounds, ShapeWithStyle shapes)
    {
        this.shapeId = shapeId;
        this.shapeBounds = shapeBounds;
        this.shapes = shapes;
    }

    public int getTagId()
    {
        return TAG_ID;
    }

    /**
     * @return the shapeId
     */
    public int getShapeId()
    {
        return shapeId;
    }

    /**
     * @param shapeId the shapeId to set
     */
    public void setShapeId(int shapeId)
    {
        this.shapeId = shapeId;
    }

    /**
     * @return the shapeBounds
     */
    public RECT getShapeBounds()
    {
        return shapeBounds;
    }

    /**
     * @param shapeBounds the shapeBounds to set
     */
    public void setShapeBounds(RECT shapeBounds)
    {
        this.shapeBounds = shapeBounds;
    }

    /**
     * @return the shapes
     */
    public ShapeWithStyle getShapes()
    {
        return shapes;
    }

    /**
     * @param shapes the shapes to set
     */
    public void setShapes(ShapeWithStyle shapes)
    {
        this.shapes = shapes;
    }

    //-------------------------
    public static class Reader extends SWFTagLoader
    {
        public Reader()
        {
            super(TAG_ID);
        }

        public SWFTag read(SWFDataReader in, int length) throws IOException
        {
            int shapeId = in.getUI16();
            RECT shapeBounds = in.getRECT();
            ShapeWithStyle shapes = new ShapeWithStyle(in, 1);
            return new DefineShape(shapeId, shapeBounds, shapes);
        }
    }

}
