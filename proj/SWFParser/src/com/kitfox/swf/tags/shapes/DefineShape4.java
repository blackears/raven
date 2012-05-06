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
public class DefineShape4 extends SWFTag
{
    public static final int TAG_ID = 83;
    private int shapeId;
    private RECT shapeBounds;
    private RECT edgeBounds;
    private boolean usesFillWindingRule;
    private boolean usesNonScalingStrokes;
    private boolean usesScalingStrokes;
    private ShapeWithStyle shapes;

    public DefineShape4(int shapeId, RECT shapeBounds, RECT edgeBounds, boolean usesFillWindingRule, boolean usesNonScalingStrokes, boolean usesScalingStrokes, ShapeWithStyle shapes)
    {
        this.shapeId = shapeId;
        this.shapeBounds = shapeBounds;
        this.edgeBounds = edgeBounds;
        this.usesFillWindingRule = usesFillWindingRule;
        this.usesNonScalingStrokes = usesNonScalingStrokes;
        this.usesScalingStrokes = usesScalingStrokes;
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
     * @return the edgeBounds
     */
    public RECT getEdgeBounds()
    {
        return edgeBounds;
    }

    /**
     * @param edgeBounds the edgeBounds to set
     */
    public void setEdgeBounds(RECT edgeBounds)
    {
        this.edgeBounds = edgeBounds;
    }

    /**
     * @return the usesFillWindingRule
     */
    public boolean isUsesFillWindingRule()
    {
        return usesFillWindingRule;
    }

    /**
     * @param usesFillWindingRule the usesFillWindingRule to set
     */
    public void setUsesFillWindingRule(boolean usesFillWindingRule)
    {
        this.usesFillWindingRule = usesFillWindingRule;
    }

    /**
     * @return the usesNonScalingStrokes
     */
    public boolean isUsesNonScalingStrokes()
    {
        return usesNonScalingStrokes;
    }

    /**
     * @param usesNonScalingStrokes the usesNonScalingStrokes to set
     */
    public void setUsesNonScalingStrokes(boolean usesNonScalingStrokes)
    {
        this.usesNonScalingStrokes = usesNonScalingStrokes;
    }

    /**
     * @return the usesScalingStrokes
     */
    public boolean isUsesScalingStrokes()
    {
        return usesScalingStrokes;
    }

    /**
     * @param usesScalingStrokes the usesScalingStrokes to set
     */
    public void setUsesScalingStrokes(boolean usesScalingStrokes)
    {
        this.usesScalingStrokes = usesScalingStrokes;
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
    
    //-----------------------------
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
            RECT edgeBounds = in.getRECT();

            in.getUB(5);
            boolean usesFillWindingRule = in.getUB(1) != 0;
            boolean usesNonScalingStrokes = in.getUB(1) != 0;
            boolean usesScalingStrokes = in.getUB(1) != 0;
            in.flushToByteBoundary();

            ShapeWithStyle shapes = new ShapeWithStyle(in, 4);
            return new DefineShape4(shapeId, shapeBounds, edgeBounds, usesFillWindingRule, usesNonScalingStrokes, usesScalingStrokes, shapes);
        }
    }
}
