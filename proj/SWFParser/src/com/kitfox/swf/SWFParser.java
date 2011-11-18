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
import com.kitfox.swf.dataType.SWFDataReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author kitfox
 */
public class SWFParser
{

    private SWFParser()
    {
    }

    public static void decompress(URL url, OutputStream os) throws IOException, SWFException
    {
        BufferedInputStream is = new BufferedInputStream(url.openStream());
        decompress(is, os);
        is.close();
    }

    public static void decompress(InputStream is, OutputStream os) throws IOException, SWFException
    {
        byte[] magic = new byte[8];
        is.read(magic, 0, 8);

        if (!((magic[0] == 'C' || magic[0] == 'F')
                && magic[1] == 'W'
                && magic[2] == 'S'
                ))
        {
            throw new SWFException("Not an SWF file");
        }

        //Bytes 4 - 7 have uncompressed length of file

        if (magic[0] == 'C')
        {
            //Compressed file format
            is = new InflaterInputStream(is);
            magic[0] = 'F';
        }

        os.write(magic);
        for (int b = is.read(); b != -1; b = is.read())
        {
            os.write(b);
        }
    }

    public static SWFDocument parse(File file) throws IOException, SWFException
    {
        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream is = new BufferedInputStream(fin);
        SWFDocument doc = parse(is);
        is.close();
        return doc;
    }

    public static SWFDocument parse(URL url) throws IOException, SWFException
    {
        BufferedInputStream is = new BufferedInputStream(url.openStream());
        SWFDocument doc = parse(is);
        is.close();
        return doc;
    }

    public static SWFDocument parse(InputStream is) throws IOException, SWFException
    {
        SWFBuilder builder = new SWFBuilder(true, System.err);

        parse(is, builder);

        return builder.getDoc();
    }

    public static void parse(URL url, SWFParseVisitor visitor) throws IOException, SWFException
    {
        BufferedInputStream is = new BufferedInputStream(url.openStream());
        parse(is, visitor);
        is.close();
    }

    public static void parse(InputStream is, SWFParseVisitor visitor) throws IOException, SWFException
    {
        byte[] magic = new byte[8];
        is.read(magic, 0, 8);

        if (!((magic[0] == 'C' || magic[0] == 'F')
                && magic[1] == 'W'
                && magic[2] == 'S'
                ))
        {
            throw new SWFException("Not an SWF file");
        }

        //Bytes 4 - 7 have uncompressed length of file

        if (magic[0] == 'C')
        {
            //Compressed file format
            is = new InflaterInputStream(is);
        }

        int version = magic[3];
        SWFDataReader sin = new SWFDataReader(is, version);

        //Finish reading header
        RECT frameSize = sin.getRECT();
        int frameRate = sin.getUI16();
        int frameCount = sin.getUI16();

        visitor.setHeader(new SWFHeader(version, frameSize, frameRate, frameCount));

        while (readTag(sin, visitor))
        {
        }
    }

    private static boolean readTag(SWFDataReader in, SWFParseVisitor visitor) throws IOException, SWFException
    {
        int val = in.getUI16();
        int tag = val >> 6;
        int len = val & 0x3f;
        if (len == 0x3f)
        {
            len = in.getSI32();
        }

        int markStart = in.getBytesRead();

        visitor.readTag(in, tag, len);

        int markEnd = in.getBytesRead();
        int size = markEnd - markStart;

        if (size != len)
        {
            throw new SWFException("Tag overflow/underflow.  Tag id #" + tag + ", dataOffset: 0x" + Integer.toHexString(markStart) + ".  Declared length: " + len + ".  Bytes read: " + size + ".");
        }

        //End tag has an ID of 0
        return tag != 0;
    }

}
