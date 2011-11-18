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

import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class StyleChangeRecord extends ShapeRecord
{
    boolean newStyles;
    boolean lineStyle;
    boolean fill1Style;
    boolean fill0Style;
    boolean stateMoveTo;

    int moveBits;
    int moveDx;
    int moveDy;

    int fillStyle0Idx;
    int fillStyle1Idx;
    int lineStyleIdx;
    
    FillStyleArray fillStyles;
    LineStyleArray lineStyles;

    StyleChangeRecord(SWFDataReader in, int shapeType,
            ShapeWithStyle shape, int flags) throws IOException
    {
        newStyles = (flags & (1 << 4)) != 0;
        lineStyle = (flags & (1 << 3)) != 0;
        fill1Style = (flags & (1 << 2)) != 0;
        fill0Style = (flags & (1 << 1)) != 0;
        stateMoveTo = (flags & (1 << 0)) != 0;

        if (stateMoveTo)
        {
            moveBits = (int)in.getUB(5);
            moveDx = (int)in.getSB(moveBits);
            moveDy = (int)in.getSB(moveBits);
        }

        if (fill0Style)
        {
            fillStyle0Idx = (int)in.getUB(shape.getNumFillBits());
        }
        if (fill1Style)
        {
            fillStyle1Idx = (int)in.getUB(shape.getNumFillBits());
        }
        if (lineStyle)
        {
            lineStyleIdx = (int)in.getUB(shape.getNumLineBits());
        }

        if (newStyles)
        {
            //Replace fill/line style array
            in.flushToByteBoundary();
            fillStyles = new FillStyleArray(in, shapeType);
            lineStyles = new LineStyleArray(in, shapeType);
            shape.setNumFillBits((int)in.getUB(4));
            shape.setNumLineBits((int)in.getUB(4));
        }
    }

}
