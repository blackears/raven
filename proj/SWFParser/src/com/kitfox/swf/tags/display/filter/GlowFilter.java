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
import com.kitfox.swf.dataType.FIXED8;
import com.kitfox.swf.dataType.RGBA;
import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class GlowFilter extends Filter
{
    RGBA glowColor;
    FIXED blurX;
    FIXED blurY;
    FIXED8 strength;
    boolean innerShadow;
    boolean knockOut;
    boolean compositeSource;
    int passes;

    public GlowFilter(SWFDataReader data) throws IOException
    {
        glowColor = data.getRGBA();
        blurX = data.getFIXED();
        blurY = data.getFIXED();
        strength = data.getFIXED8();

        innerShadow = data.getUB(1) != 0;
        knockOut = data.getUB(1) != 0;
        compositeSource = data.getUB(1) != 0;
        passes = (int)data.getUB(5);
        data.flushToByteBoundary();
    }
}
