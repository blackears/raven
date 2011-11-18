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

import com.kitfox.raven.util.FileUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kitfox
 */
public class ResourceDataStore
{
    HashMap<HashKey, Record> keyMap = new HashMap<HashKey, Record>();
    HashMap<String, Record> nameMap = new HashMap<String, Record>();
    ArrayList<Record> idList = new ArrayList<Record>();

    HashSet<String> reservedNames = new HashSet<String>();

    final File destDir;

    public ResourceDataStore(File destDir)
    {
        this.destDir = destDir;
    }

    /**
     * Exports a chunk of data as a resource.
     *
     * @param data
     * @param preferredFileName Preferred file name to use.  If
     * name is already allocated it will be altered to create a
     * unique name.
     * @return
     */
    public Record exportData(byte[] data, String preferredFileName)
    {
        HashKey key = HashKey.create(data);
        Record rec = keyMap.get(key);

        if (rec == null)
        {
            preferredFileName = createUniqueName(preferredFileName);

            try
            {
                File destFile = new File(destDir, preferredFileName);
                FileUtil.saveFile(data, destFile);
            } catch (IOException ex)
            {
                Logger.getLogger(ResourceDataStore.class.getName()).log(Level.SEVERE, null, ex);
            }

            rec = new Record(idList.size(), key, preferredFileName);
            keyMap.put(key, rec);
            nameMap.put(preferredFileName, rec);
            idList.add(rec);
        }

        return rec;
    }

    public Record exportData(BufferedImage img, String preferredFileName)
    {
        try
        {
            int dot = preferredFileName.lastIndexOf('.');
            String extn = preferredFileName.substring(dot + 1);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(img, extn, bout);
            bout.close();

            return exportData(bout.toByteArray(), preferredFileName);
        } catch (IOException ex)
        {
            Logger.getLogger(ResourceDataStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

//    public Record exportFile(File base, String fileName)
//    {
//        URI uri = base.isDirectory() ? base.toURI() : base.getParentFile().toURI();
//        URI resUri = uri.resolve(fileName);
//
//        return exportFile(resUri);
//    }

    public Record exportFile(URI resUri)
    {
        File resSrc = new File(resUri);

        HashKey key = HashKey.create(resSrc);
        Record rec = keyMap.get(key);
        if (rec == null)
        {
            String name = resSrc.getName();
            name = createUniqueName(name);

            try
            {
                File destFile = new File(destDir, name);
                FileUtil.copyFile(resSrc, destFile);
            } catch (IOException ex)
            {
                Logger.getLogger(ResourceDataStore.class.getName()).log(Level.SEVERE, null, ex);
            }

            rec = new Record(idList.size(), key, name);
            keyMap.put(key, rec);
            nameMap.put(name, rec);
            idList.add(rec);
        }

        return rec;
    }

    private String createUniqueName(String name)
    {
        if (!reservedNames.contains(name)
                && !nameMap.containsKey(name))
        {
            return name;
        }

        String suffix = "";
        int dotIdx = name.lastIndexOf('.');
        if (dotIdx != -1)
        {
            name = name.substring(0, dotIdx);
            suffix = name.substring(dotIdx);
        }

        int i = 1;
        while (true)
        {
            String newName = name + i + suffix;
            if (!reservedNames.contains(name)
                && !nameMap.containsKey(newName))
            {
                return newName;
            }
            ++i;
        }
    }

    public void reserveName(String string)
    {
        reservedNames.add(string);
    }

    public void exportIndex(PrintWriter pw)
    {
        for (Record rec: idList)
        {
            pw.println(rec.fileName);
        }
    }

    //------------------------
    
    public class Record
    {
        private final int id;
        private final HashKey key;
        private final String fileName;

        public Record(int id, HashKey key, String fileName)
        {
            this.id = id;
            this.key = key;
            this.fileName = fileName;
        }

        /**
         * @return the fileName
         */
        public String getFileName()
        {
            return fileName;
        }

        /**
         * @return the key
         */
        public HashKey getKey()
        {
            return key;
        }

        /**
         * @return the id
         */
        public int getId()
        {
            return id;
        }

    }

}
