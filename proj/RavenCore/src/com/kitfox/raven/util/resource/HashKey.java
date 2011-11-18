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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class HashKey
{
    byte[] hashcode;

    public HashKey(byte[] hash)
    {
        this.hashcode = hash;
    }

    public static HashKey create(byte[] data)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);

            return new HashKey(md.digest());
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(HashKey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static HashKey create(File file)
    {
        try
        {
            byte[] buf = new byte[0x10000];
            FileInputStream fin = new FileInputStream(file);

            MessageDigest md = MessageDigest.getInstance("MD5");
            int len = 0;
            for (len = fin.read(buf, 0, buf.length);
                len != -1;
                len = fin.read(buf, 0, buf.length))
            {
                md.update(buf, 0, len);
            }

            return new HashKey(md.digest());
        } catch (IOException ex)
        {
            Logger.getLogger(HashKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(HashKey.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final HashKey other = (HashKey) obj;
        if (!Arrays.equals(this.hashcode, other.hashcode))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + Arrays.hashCode(this.hashcode);
        return hash;
    }

}
