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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class DirEntry extends HelpEntry
{
    Template ftlTemplate;

    private final ArrayList<HelpEntry> children = new ArrayList<HelpEntry>();
    final String header;

    public DirEntry(DirEntry parent, File src, File dest, String header, String title)
    {
        super(parent, src, dest, title);
        this.header = header;

        //Prepare freemarker
        final Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            ftlTemplate = cfg.getTemplate("HelpTemplateIndex.ftl");
        } catch (IOException ex) {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    public void add(HelpEntry child)
    {
        children.add(child);
    }

    @Override
    protected void generate()
    {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("date", new Date());
            map.put("author", "kitfox");
            map.put("page", this);

            File parentDir = getDest().getParentFile();
            if (!parentDir.exists())
            {
                parentDir.mkdirs();
            }
            FileWriter w = new FileWriter(getDest());
            ftlTemplate.process(map, w);
            w.close();
        } catch (TemplateException ex)
        {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (HelpEntry entry: children)
        {
            entry.generate();
        }
    }

    public ArrayList<NavElement> buildParentNav(String prefix, ArrayList<NavElement> list)
    {
        if (getParent() != null)
        {
            getParent().buildParentNav(prefix + "../", list);
        }
        list.add(new NavElement(getTitle(), prefix + "index.html"));

        return list;
    }

    @Override
    public ArrayList<NavElement> getNavBar(ArrayList<NavElement> list)
    {
        if (list == null)
        {
            list = new ArrayList<NavElement>();
        }

        if (getParent() != null)
        {
            getParent().buildParentNav("../", list);
        }

        return list;
    }

    /**
     * @return the children
     */
    @Override
    public List<HelpEntry> getChildren()
    {
        return new ArrayList<HelpEntry>(children);
    }
}
