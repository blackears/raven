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

import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author kitfox
 */
public class SWFTagPrinter implements SWFParseVisitor
{
    final PrintStream out;

    public SWFTagPrinter()
    {
        this(System.err);
    }

    public SWFTagPrinter(PrintStream out)
    {
        this.out = out;
    }

    public void setHeader(SWFHeader header)
    {
        out.println(header.toString());
    }

    public void readTag(SWFDataReader in, int tagId, int length) throws IOException
    {
        out.println("Found tag #" + tagId + " \tlen: " + length);

        //Skip this unknown tag
        in.skip(length);

    }

}
