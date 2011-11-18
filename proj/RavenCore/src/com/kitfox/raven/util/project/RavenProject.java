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

package com.kitfox.raven.util.project;

import com.kitfox.xml.schema.ravenprojectschema.ObjectFactory;
import com.kitfox.xml.schema.ravenprojectschema.RavenProjectType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author kitfox
 */
public class RavenProject
{
    private File source;
    private File homeDir;

    private File exportSourceDir;
    private File exportResourceDir;

    RavenProjectType projectType;

    public RavenProject(File source, File exportSourceDir, File exportResourceDir)
    {
        this.source = source;
        this.homeDir = source.getParentFile();

        this.exportSourceDir = new File(homeDir, exportSourceDir.getPath());
        this.exportResourceDir = new File(homeDir, exportResourceDir.getPath());
    }

    public static RavenProject create(File file)
    {
        RavenProjectType projectType = load(file);
        File src = new File(projectType.getExportSourceDir());
        File res = new File(projectType.getExportResourceDir());

        return new RavenProject(file, src, res);
    }

//    public void buildFonts()
//    {
//        SvgFontsType fonts = projectType.getSvgFonts();
//        for (SvgFontType fontType: fonts.getFont())
//        {
//            Font font = new Font(fontType.getFamily(), Font.PLAIN, 12);
//
//
//        }
//
//    }

    private static RavenProjectType load(File file)
    {
        if (file == null)
        {
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(RavenProjectType.class);
            StreamSource streamSource = new StreamSource(file);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<RavenProjectType> ele = unmarshaller.unmarshal(streamSource, RavenProjectType.class);
            return ele.getValue();
        } catch (JAXBException ex) {
            Logger.getLogger(RavenProject.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }

    protected RavenProjectType asJAXB()
    {
        RavenProjectType pref = new RavenProjectType();

        pref.setExportResourceDir(exportResourceDir.getPath());
        pref.setExportSourceDir(exportSourceDir.getPath());

        return pref;
    }

    public void save(File file)
    {
        RavenProjectType pref = asJAXB();

        ObjectFactory fact
                = new ObjectFactory();
        JAXBElement<RavenProjectType> value = fact.createProject(pref);

        try {
            FileWriter fw = new FileWriter(file);

            JAXBContext context = JAXBContext.newInstance(RavenProjectType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(value, fw);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RavenProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(RavenProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the exportSourceDir
     */
    public File getExportSourceDir()
    {
        return exportSourceDir;
    }

    /**
     * @param exportSourceDir the exportSourceDir to set
     */
    public void setExportSourceDir(File exportSourceDir)
    {
        this.exportSourceDir = exportSourceDir;
    }

    /**
     * @return the exportResourceDir
     */
    public File getExportResourceDir()
    {
        return exportResourceDir;
    }

    /**
     * @param exportResourceDir the exportResourceDir to set
     */
    public void setExportResourceDir(File exportResourceDir)
    {
        this.exportResourceDir = exportResourceDir;
    }

    /**
     * @return the source
     */
    public File getSource()
    {
        return source;
    }

    /**
     * @return the homeDir
     */
    public File getHomeDir()
    {
        return homeDir;
    }

}
