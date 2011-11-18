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

package com.kitfox.swf.tags.display.filter;

import com.kitfox.swf.dataType.FIXED;
import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class BlurFilter extends Filter
{
    FIXED blurX;
    FIXED blurY;
    int passes;

    public BlurFilter(SWFDataReader data) throws IOException
    {
        blurX = data.getFIXED();
        blurY = data.getFIXED();
        passes = (int)data.getUB(5);
        data.getUB(3);
        data.flushToByteBoundary();
    }
}
