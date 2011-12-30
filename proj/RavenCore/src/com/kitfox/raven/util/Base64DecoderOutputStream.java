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
public class Base64DecoderOutputStream extends FilterOutputStream
{
    int queue;
    int count;
    boolean closed;
    
    public Base64DecoderOutputStream(OutputStream in)
    {
        super(in);
    }
    
    @Override
    public void write(int b) throws IOException
    {
        if (closed)
        {
            return;
        }
        
        char ch = (char)b;
        switch (count)
        {
            case 0:
            {
                queue = getCodeValue(ch);
                count = 1;
                break;
            }
            case 1:
            {
                int v = getCodeValue(ch);
                
                out.write((queue << 2) | (v >> 4));
                queue = v & 0xf;
                count = 2;
                break;
            }
            case 2:
            {
                if (ch == '=')
                {
                    closed = true;
                    return;
                }
                
                int v = getCodeValue(ch);
                
                out.write((queue << 4) | (v >> 2));
                queue = v & 0x3;
                count = 3;
                break;
            }
            case 3:
            {
                if (ch == '=')
                {
                    closed = true;
                    return;
                }
                
                int v = getCodeValue(ch);
                
                out.write((queue << 6) | v);
                queue = 0;
                count = 0;
                break;
            }
        }
    }
    
    @Override
    public void close()
    {
        closed = true;
    }
    
    public static int getCodeValue(char ch)
    {
        if (ch >= 'A' && ch <= 'Z')
        {
            return ch - 'A';
        }
        else if (ch >= 'a' && ch <= 'z')
        {
            return ch - 'a' + 26;
        }
        else if (ch >= '0' && ch <= '9')
        {
            return ch - '0' + 52;
        }
        else if (ch == '+')
        {
            return 62;
        }
        else if (ch == '/')
        {
            return 63;
        }
        
        throw new IllegalArgumentException();
    }

    public static byte[] decode(String code)
    {
        try
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Base64DecoderOutputStream b64out = new Base64DecoderOutputStream(bout);
            
            b64out.write(code.getBytes());
            b64out.close();
            
            return bout.toByteArray();
        } catch (IOException ex)
        {
            Logger.getLogger(Base64EncoderOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
