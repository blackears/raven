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

package com.kitfox.raven.util.help;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author kitfox
 */
abstract public class HelpEntry
{
    private final DirEntry parent;
    private final File src;
    private final File dest;
    private final String title;

    public HelpEntry(DirEntry parent, File src, File dest, String title)
    {
        this.parent = parent;
        this.src = src;
        this.dest = dest;
        this.title = title;
    }

    abstract protected void generate();

    public ArrayList<NavElement> getNavBar()
    {
        return getNavBar(null);
    }

    public ArrayList<NavElement> getNavBar(ArrayList<NavElement> list)
    {
        if (list == null)
        {
            list = new ArrayList<NavElement>();
        }

        if (parent != null)
        {
            parent.buildParentNav("", list);
        }

        return list;
    }


    public String getRelPath(File base, File target)
    {
        if (base.isFile())
        {
            base = base.getParentFile();
        }

        ArrayList<File> targetList = new ArrayList<File>();
        for (File file = target; file != null; file = file.getParentFile())
        {
            targetList.add(file);
        }

        ArrayList<File> baseList = new ArrayList<File>();
        for (File file = base; file != null; file = file.getParentFile())
        {
            baseList.add(file);
        }

//        int j = 9;

        while (!targetList.isEmpty() && !baseList.isEmpty()
                && targetList.get(targetList.size() - 1).equals(
                baseList.get(baseList.size() - 1)))
        {
            targetList.remove(targetList.size() - 1);
            baseList.remove(baseList.size() - 1);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseList.size(); ++i)
        {
            sb.append("../");
        }

        for (int i = targetList.size() - 1; i >= 0; --i)
        {
            sb.append(targetList.get(i).getName());
            if (i > 0)
            {
                sb.append('/');
            }
        }

        return sb.toString();
    }

    public File getRootDir()
    {
        if (parent == null)
        {
            return dest.getParentFile();
        }
        return parent.getRootDir();
    }

    public String getCssPath()
    {
        return getRelPath(dest, new File(getRootDir(), "style.css"));
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    public List<HelpEntry> getChildren()
    {
        return Collections.emptyList();
    }

    /**
     * @return the parent
     */
    public DirEntry getParent()
    {
        return parent;
    }

    /**
     * @return the src
     */
    public File getSrc()
    {
        return src;
    }

    /**
     * @return the dest
     */
    public File getDest()
    {
        return dest;
    }
}
