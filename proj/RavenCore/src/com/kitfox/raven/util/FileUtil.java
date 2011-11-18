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

package com.kitfox.raven.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author kitfox
 */
public class FileUtil
{

    public static void copyFile(File in, File out)
            throws IOException
    {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(),
                    outChannel);
        } catch (IOException e)
        {
            throw e;
        } finally
        {
            if (inChannel != null)
            {
                inChannel.close();
            }
            if (outChannel != null)
            {
                outChannel.close();
            }
        }
    }

    public static void saveFile(byte[] data, File out) throws IOException
    {
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try
        {
            ByteBuffer buf = ByteBuffer.wrap(data);
            outChannel.write(buf);
        } catch (IOException e)
        {
            throw e;
        } finally
        {
            if (outChannel != null)
            {
                outChannel.close();
            }
        }
    }
}
