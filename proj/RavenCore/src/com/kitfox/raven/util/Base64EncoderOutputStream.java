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

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class Base64EncoderOutputStream extends FilterOutputStream
{
    public static final String CODE_TABLE 
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    int queue;
    int count;
    boolean closed;
    
    public Base64EncoderOutputStream(OutputStream out)
    {
        super(out);
    }
    
    private int getCharacter(int index)
    {
        return CODE_TABLE.charAt(index);
    }
    
    @Override
    public void write(int b) throws IOException
    {
        if (closed)
        {
            return;
        }
        
        switch (count)
        {
            case 0:
            {
                out.write(getCharacter((b >> 2) & 0x3f));
                queue = b & 3;
                count = 1;
                break;
            }
            case 1:
            {
                out.write(getCharacter((queue << 4) | ((b >> 4) & 0xf)));
                queue = b & 0xf;
                count = 2;
                break;
            }
            case 2:
            {
                out.write(getCharacter((queue << 2) | ((b >> 6) & 0x3)));
                out.write(getCharacter(b & 0x3f));
                queue = 0;
                count = 0;
                break;
            }
        }
    }
    
    @Override
    public void close() throws IOException
    {
        if (closed)
        {
            return;
        }
        
        switch (count)
        {
            case 1:
            {
                out.write(getCharacter(queue << 4));
                out.write('=');
                out.write('=');
                break;
            }
            case 2:
            {
                out.write(getCharacter(queue << 2));
                out.write('=');
                break;
            }
        }
        
        closed = true;
    }

    public static String encode(byte[] data)
    {
        try
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Base64EncoderOutputStream b64out = new Base64EncoderOutputStream(bout);
            
            b64out.write(data);
            b64out.close();
            
            return bout.toString();
        } catch (IOException ex)
        {
            Logger.getLogger(Base64EncoderOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
