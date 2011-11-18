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

package com.kitfox.swf;

import com.kitfox.swf.dataType.RECT;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author kitfox
 */
public class SWFHeader
{
    int version;
    RECT frameSize;
    int frameRate;
    int frameCount;

    public SWFHeader(int version, RECT frameSize, int frameRate, int frameCount)
    {
        this.version = version;
        this.frameSize = frameSize;
        this.frameRate = frameRate;
        this.frameCount = frameCount;
    }

    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Ver: " + version);
        pw.println("frameSize: " + frameSize.toString());
        pw.println("frameRate: " + frameRate + " (" + ((float)frameRate / 0x100) + " fps)");
        pw.println("frameCount: " + frameCount);
        return sw.toString();
    }

}
