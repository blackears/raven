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
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class FillStyleArray
{
    ArrayList<FillStyle> fillStyles = new ArrayList<FillStyle>();

    public FillStyleArray(SWFDataReader in, int shapeType) throws IOException
    {
        int count = in.getUI8();
        if (count == 0xFF)
        {
            count = in.getSI16();
        }

        for (int i = 0; i < count; ++i)
        {
            fillStyles.add(new FillStyle(in, shapeType));
        }
    }

    public FillStyle get(int index)
    {
        //Fill style array indices start at 1
        return index <= 0 ? null : fillStyles.get(index - 1);
    }
}
