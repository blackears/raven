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
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author kitfox
 */
public class FileFilterSuffix extends FileFilter
{
    String title;
    String[] suffix;

    public FileFilterSuffix(String title, String... suffix)
    {
        this.title = title;
        this.suffix = suffix;
    }

    @Override
    public boolean accept(File file)
    {
        if (file.isDirectory())
        {
            return true;
        }
        for (int i = 0; i < suffix.length; ++i)
        {
            if (file.getName().toLowerCase().endsWith("." + suffix[i].toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription()
    {
        StringBuilder sb = new StringBuilder(title).append(" (");
        for (int i = 0; i < suffix.length; ++i)
        {
            sb.append("*.").append(suffix[i]);
            if (i != suffix.length - 1)
            {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
