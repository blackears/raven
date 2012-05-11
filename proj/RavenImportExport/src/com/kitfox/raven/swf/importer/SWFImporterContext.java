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

package com.kitfox.raven.swf.importer;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.swf.SWFDocument;
import com.kitfox.swf.SWFException;
import com.kitfox.swf.SWFParser;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class SWFImporterContext
{
    public static final String PROP_FILE = "seqFile";
    private String file;
    
    public static final String PROP_USE_BACKGROUND = "useBackground";
    private boolean useBackground;
    
    private final NodeSymbol doc;
    private PropertiesData pref;
    
    public SWFImporterContext(NodeSymbol doc, Properties preferences)
    {
        this.doc = doc;
        this.pref = new PropertiesData(preferences);
        
        file = pref.getString(PROP_FILE, "");
        useBackground = pref.getBoolean(PROP_USE_BACKGROUND, true);
    }
    
    public void savePreferences()
    {
        pref.setString(PROP_FILE, file);
        pref.setBoolean(PROP_USE_BACKGROUND, useBackground);
    }

    private void errMessage(String message)
    {
        JOptionPane.showMessageDialog(doc.getDocument().getEnv().getSwingRoot(),
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    public void doImport()
    {
        File swfFile = new File(file);
        if (!swfFile.canRead())
        {
            errMessage("Could not open file for reading: " + swfFile);
            return;
        }
        
        SWFDocument swfDoc;
        try
        {
            swfDoc = SWFParser.parse(swfFile);
        } catch (IOException ex)
        {
            errMessage("Error parsing swf: " + swfFile + "\n"
                    + ex.getLocalizedMessage());
            Logger.getLogger(SWFImporterContext.class.getName()).log(Level.WARNING, null, ex);
            return;
        } catch (SWFException ex)
        {
            Logger.getLogger(SWFImporterContext.class.getName()).log(Level.WARNING, null, ex);
            return;
        }
        
//        Environment env = doc.getDocument().getEnv();
        ImportSWFBuilder builder = 
                new ImportSWFBuilder((RavenDocument)doc.getDocument());
        builder.importDoc(swfDoc);
        
//        SWFTimelineBuilder builder = SWFTimelineBuilder.importDoc(swfDoc);
        
    }

    /**
     * @return the seqFile
     */
    public String getFile()
    {
        return file;
    }

    /**
     * @param seqFile the seqFile to set
     */
    public void setFile(String seqFile)
    {
        this.file = seqFile;
    }

    /**
     * @return the useBackground
     */
    public boolean isUseBackground()
    {
        return useBackground;
    }

    /**
     * @param useBackground the useBackground to set
     */
    public void setUseBackground(boolean useBackground)
    {
        this.useBackground = useBackground;
    }
    
}
