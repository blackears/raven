/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.swf.importer;

import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.tree.NodeDocument;
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
    
    private final NodeDocument doc;
    private PropertiesData pref;
    
    public SWFImporterContext(NodeDocument doc, Properties preferences)
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
        JOptionPane.showMessageDialog(doc.getEnv().getSwingRoot(),
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
        
        ImportSWFBuilder builder = 
                new ImportSWFBuilder((RavenNodeRoot)doc);
        
        builder.importDoc(swfDoc);
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
