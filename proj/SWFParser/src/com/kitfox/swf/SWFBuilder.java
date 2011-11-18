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
import com.kitfox.swf.tags.SWFTagLoader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public class SWFBuilder implements SWFParseVisitor
{
    final boolean verbose;
    private SWFDocument doc;
    final PrintStream out;

    HashMap<Integer, SWFTagLoader> loaderMap = new HashMap<Integer, SWFTagLoader>();

    public SWFBuilder(boolean verbose, PrintStream out)
    {
        this.verbose = verbose;
        this.out = out;

        ServiceLoader<SWFTagLoader> tagLoaders = ServiceLoader.load(SWFTagLoader.class);
        for (Iterator<SWFTagLoader> it = tagLoaders.iterator(); it.hasNext();)
        {
            SWFTagLoader loader = it.next();
            int id = loader.getTagId();
            loaderMap.put(id, loader);
        }
    }

    public void setHeader(SWFHeader header)
    {
        doc = new SWFDocument(header);

        if (verbose)
        {
            out.println(header.toString());
        }
    }

    public void readTag(SWFDataReader in, int tagId, int length) throws IOException
    {

        SWFTagLoader loader = loaderMap.get(tagId);
        if (loader != null)
        {
            out.println("Reading tag #" + tagId + "\tlen: " + length + "\t(" + loader.getClass().getName() + ")");
            doc.addTag(loader.read(in, length));
            return;
        }

        //Skip this unknown tag
        if (verbose)
        {
            out.println("Skipping unknown tag #" + tagId + "\tlen: " + length);
        }
        in.skip(length);
    }

    /**
     * @return the doc
     */
    public SWFDocument getDoc()
    {
        return doc;
    }

}
