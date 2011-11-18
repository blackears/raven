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

package com.kitfox.raven.build.headers;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.util.TaskLogger;

/**
 *
 * @author kitfox
 */
public class SetHeaders extends Task
{
    protected FileSet source;
    private File headerFile;
    Header header;

    @Override
    public void execute() throws BuildException
    {
        if (header == null)
        {        
            if (headerFile == null || !headerFile.exists())
            {
                log("Cannot find header file: " + headerFile, LogLevel.ERR.getLevel());
                return;
            }
            else if (!headerFile.canRead())
            {
                log("Cannot read header file: " + headerFile, LogLevel.ERR.getLevel());
                return;
            }
            
            String headerText = readFile(headerFile);
            header = new Header();
            header.addText(headerText);            
        }
        
        Matcher m = Pattern.compile("^\\s*/\\*[^*].*?\\*/\\s*", Pattern.DOTALL).matcher("");
        
        //Process all resources
        DirectoryScanner ds = source.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        for (int i = 0; i < files.length; i++)
        {
            String fileName = files[i];

            File file = new File(baseDir, fileName);
            String text = readFile(file);
            
            String mod = text;
            m.reset(text);
            if (m.find())
            {
                mod = text.substring(m.end());
            }
            mod = header.getFormatted() + mod;
            
            if (!mod.equals(text))
            {
                //Replace the file
                log("Updating header on " + file, LogLevel.INFO.getLevel());
                
                writeFile(file, mod);
            }
            
//            System.err.println(header.getFormatted() + text);            
//            System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        }
    }

    byte[] buf = new byte[2048];
    
    private String readFile(File file)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fin = new FileInputStream(file);
            
            int len = 0;
            for (len = fin.read(buf, 0, buf.length);
                    len != -1;
                    len = fin.read(buf, 0, buf.length))
            {
                baos.write(buf, 0, len);
            }
            
            fin.close();
            return baos.toString();
        } catch (IOException ex)
        {
            Logger.getLogger(SetHeaders.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public FileSet createSource()
    {
        source = new FileSet();
        return source;
    }
    
    public Header createHeader()
    {
        header = new Header();
        return header;
    }

    /**
     * @return the header
     */
    public File getHeaderFile()
    {
        return headerFile;
    }

    /**
     * @param header the header to set
     */
    public void setHeaderFile(File header)
    {
        this.headerFile = header;
    }

    private void writeFile(File file, String mod)
    {
        try
        {
            PrintWriter pw = new PrintWriter(file);
  
            pw.print(mod);
            
            pw.close();
        } catch (IOException ex)
        {
            Logger.getLogger(SetHeaders.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    //--------------------------------
    public class Header
    {
        private String header = "";
        
        public void addText(String text)
        {
            header += getProject().replaceProperties(text);
        }

        /**
         * @return the header
         */
        public String getHeader()
        {
            return header;
        }
        
        public String getFormatted()
        {
            StringReader sr = new StringReader(header);
            BufferedReader br = new BufferedReader(sr);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            pw.println("/*");
            
            try
            {
                for (String line = br.readLine(); line != null; line = br.readLine())
                {
                    pw.println(" * " + line);
                }
            } catch (IOException ex)
            {
                Logger.getLogger(SetHeaders.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            pw.println(" */");
            pw.println();
            
            return sw.toString();
        }
    }
}
