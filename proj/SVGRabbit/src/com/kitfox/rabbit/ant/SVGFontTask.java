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

package com.kitfox.rabbit.ant;

import com.kitfox.rabbit.font.svg.SVGFontExporter;
import com.kitfox.rabbit.parser.XmlCharacterParser;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;

/**
 *
 * @author kitfox
 */
public class SVGFontTask extends Task
{
    public class FontDef
    {
        private String name;
        private boolean italic;
        private boolean bold;
        private File file;

        public Font getFont()
        {
            if (file != null)
            {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, file);
                } catch (FontFormatException ex) {
                    Logger.getLogger(SVGFontExporter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SVGFontExporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new Font(name,
                    (italic ? Font.ITALIC : Font.PLAIN) | (bold ? Font.BOLD : Font.PLAIN),
                    1);
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the italic
         */
        public boolean isItalic() {
            return italic;
        }

        /**
         * @param italic the italic to set
         */
        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        /**
         * @return the bold
         */
        public boolean isBold() {
            return bold;
        }

        /**
         * @param bold the bold to set
         */
        public void setBold(boolean bold) {
            this.bold = bold;
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * @param file the file to set
         */
        public void setFile(File file) {
            this.file = file;
        }
    }

    private FileSet characterData;
    private File dest;
    ArrayList<FontDef> fontDefs = new ArrayList<FontDef>();
    private String characters;

    @Override
    public void execute() throws BuildException
    {
        if (characterData == null)
        {
            throw new BuildException("No character data files specified");
        }

        //Process all resources
        DirectoryScanner ds = characterData.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        //Count all characters
        XmlCharacterParser parser = new XmlCharacterParser();
        parser.addCharacters(characters);

        for (int i = 0; i < files.length; i++)
        {
            String path = files[i].replace('\\', '/');
            File file = new File(baseDir, path);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(file, parser);

                //parser.getRootNode();
            } catch (IOException ex) {
                Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Write out font
        FontDef def = fontDefs.get(0);

        SVGFontExporter exp = new SVGFontExporter(def.getFont(), parser.getCharacters());
        exp.exportSVG(dest);
    }

    public FileSet createCharacterData()
    {
        characterData = new FileSet();
        return characterData;
    }

    public FontDef createFont()
    {
        FontDef def = new FontDef();
        fontDefs.add(def);
        return def;
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

    /**
     * @return the characters
     */
    public String getCharacters() {
        return characters;
    }

    /**
     * @param characters the characters to set
     */
    public void setCharacters(String characters) {
        this.characters = characters;
    }

}
