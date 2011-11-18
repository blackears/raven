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
abstract public class ShapeRecord
{
    public static ShapeRecord create(SWFDataReader in, int shapeType, 
            ShapeWithStyle shape) throws IOException
    {
        boolean edgeRecord = in.getUB(1) != 0;
        if (!edgeRecord)
        {
            int flags = (int)in.getUB(5);
            if (flags == 0)
            {
                return null;
            }

            return new StyleChangeRecord(in, shapeType,
                    shape, flags);
        }
        else
        {
            boolean straight = in.getUB(1) != 0;
            if (straight)
            {
                return new StraightEdgeRecord(in);
            }

            return new CurvedEdgeRecord(in);
        }
        
    }
}
