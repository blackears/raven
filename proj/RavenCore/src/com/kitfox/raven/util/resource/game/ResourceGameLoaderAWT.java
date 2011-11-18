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

package com.kitfox.raven.util.resource.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class ResourceGameLoaderAWT implements ResourceGameLoader
{
    ArrayList<String> fileNames = new ArrayList<String>();
    String resDir;

    public ResourceGameLoaderAWT(String resDir, String indexName)
    {
        this.resDir = resDir;
        InputStream is = 
                ResourceGameLoaderAWT.class.getResourceAsStream(resDir + indexName);
        
        loadIndex(is);
    }
    
    private void loadIndex(InputStream is)
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            for (String line = br.readLine(); line != null; line = br.readLine())
            {
                fileNames.add(line);
            }
        } catch (IOException ex)
        {
            Logger.getLogger(ResourceGameLoaderAWT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String getFileName(int id)
    {
        String shortName = fileNames.get(id);
        
        return resDir + shortName;
    }
    
    @Override
    public InputStream openResource(int id)
    {
        String shortName = fileNames.get(id);
        
        return ResourceGameLoaderAWT.class.getResourceAsStream(resDir + shortName);
    }
}
