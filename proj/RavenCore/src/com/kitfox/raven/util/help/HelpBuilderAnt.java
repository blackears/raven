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

import com.kitfox.xml.schema.helpindexschema.HeaderType;
import com.kitfox.xml.schema.helpindexschema.HelpIndexType;
import com.kitfox.xml.schema.helpindexschema.OutlineEntryType;
import com.kitfox.xml.schema.helpindexschema.PageType;
import com.kitfox.xml.schema.helpindexschema.SubindexType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 *
 * @author kitfox
 */
public class HelpBuilderAnt extends Task
{
    private File dest;
    private File css;
    private File rootHelpIndex;  //Root help index

    HashMap<File, DirEntry> dirInfos = new HashMap<File, DirEntry>();


    @Override
    public void execute() throws BuildException
    {
        DirEntry root = buildIndex(null, rootHelpIndex, dest);
        root.generate();

        try
        {
            FileWriter fw = new FileWriter(new File(dest, "style.css"));
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            FileReader fr = new FileReader(css);
            BufferedReader br = new BufferedReader(fr);
            for (String line = br.readLine(); line != null; line = br.readLine())
            {
                pw.println(line);
            }

            br.close();
            pw.close();
        } catch (IOException ex)
        {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DirEntry buildIndex(DirEntry parent, File srcFile, File destDir)
    {
        File parentFile = srcFile.getParentFile();

        HelpIndexType indexType = loadHelpIndex(srcFile);

        HeaderType header = indexType.getHeader();
        if (header != null)
        {
            header.getSrc();
        }

        File destFile = new File(destDir, "index.html");
        DirEntry info = new DirEntry(parent,
                srcFile,
                destFile,
                null,
                indexType.getTitle().getValue());

        for (OutlineEntryType entry: indexType.getOutline().getPageOrSubindex())
        {
            if (entry instanceof PageType)
            {
                PageType value = (PageType)entry;
                FileEntry fileInfo = new FileEntry(info,
                        new File(parentFile, value.getSrc()),
                        new File(destDir, value.getSrc()),
                        value.getTitle());
                info.add(fileInfo);
            }
            else if (entry instanceof SubindexType)
            {
                SubindexType value = (SubindexType)entry;
                File indexFile = new File(parentFile, value.getSrc());
                info.add(buildIndex(info, indexFile,
                        new File(destDir, value.getSrc()).getParentFile()));
            }
        }

        return info;
    }

    private HelpIndexType loadHelpIndex(File file)
    {
        try {
//            JAXBContext context = JAXBContext.newInstance(
//                    HelpIndexType.class.getPackage().getName(),
//                    HelpIndexType.class.getClassLoader());
            JAXBContext context = JAXBContext.newInstance(HelpIndexType.class);
            FileReader reader = new FileReader(file);
            StreamSource source = new StreamSource(reader);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<HelpIndexType> ele = unmarshaller.unmarshal(
                    source, HelpIndexType.class);

            return ele.getValue();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the dest
     */
    public File getDest() {
        return dest;
    }

    /**
     * @param dest the dest to set
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    private String changeExtension(String path, String extn)
    {
        int idx = path.lastIndexOf('.');
        if (idx == -1)
        {
            return path + "." + extn;
        }
        return path.substring(0, idx + 1) + extn;
    }

    private String fromCamelCase(String dir)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < dir.length(); ++i)
        {
            char ch = dir.charAt(i);
            if (i == 0)
            {
                ch = Character.toUpperCase(ch);
            }
            else if (Character.isUpperCase(ch))
            {
                sb.append(' ');
            }
            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * @return the rootHelpIndex
     */
    public File getRoot()
    {
        return rootHelpIndex;
    }

    /**
     * @param rootHelpIndex the rootHelpIndex to set
     */
    public void setRoot(File rootHelpIndex)
    {
        this.rootHelpIndex = rootHelpIndex;
    }

    /**
     * @return the css
     */
    public File getCss()
    {
        return css;
    }

    /**
     * @param css the css to set
     */
    public void setCss(File css)
    {
        this.css = css;
    }

}
