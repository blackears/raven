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

package com.kitfox.raven.util.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class HashKeyOutputStream extends OutputStream
{
    MessageDigest md;

    public HashKeyOutputStream()
    {
        try
        {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(HashKeyOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(int b) throws IOException
    {
        md.update((byte)b);
    }

    public HashKey getKey()
    {
        return new HashKey(md.digest());
    }
}
