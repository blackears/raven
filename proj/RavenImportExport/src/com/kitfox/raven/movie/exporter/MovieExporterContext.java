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

package com.kitfox.raven.movie.exporter;

import com.kitfox.raven.editor.node.scene.RavenNodeComposition;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.tree.NodeSymbol;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class MovieExporterContext
{
    private static final String PROP_COMPOSITION = "composition";
    private RavenNodeComposition composition;
    
    private static final String PROP_FORMAT = "format";
    private MovieExporterFormat format;
    
//    private static final String PROP_WIDTH = "width";
//    private int width;
//    
//    private static final String PROP_HEIGHT = "height";
//    private int height;
    
    private static final String PROP_FRAME_CURRENT = "frameCurrent";
    private boolean frameCur;
    
    private static final String PROP_FRAME_START = "frameStart";
    private int frameStart;
    
    private static final String PROP_FRAME_END = "frameEnd";
    private int frameEnd;
    
    private static final String PROP_FRAME_STRIDE = "frameStride";
    private int frameStride;
    
    
    private static final String PROP_FRAMES_DIR = "framesDir";
    private String framesDir;
    
    private static final String PROP_FRAMES_PREFIX = "framesPrefix";
    private String framesPrefix;
    
    private static final String PROP_FRAMES_IMG_FMT = "framesImageFormat";
    private String framesImageFormat;
    
    private static final String PROP_FRAMES_NUM_PAD = "framesNumberPadding";
    private int framesNumberPadding;
    
    private static final String PROP_SEQ_FILE = "seqFile";
    private String seqFile;
    
    private static final String PROP_SEQ_FORMAT = "seqFormat";
    private String seqFormat;
    
    private NodeSymbol sym;
    private PropertiesData pref;
    
    public MovieExporterContext(NodeSymbol doc, Properties preferences)
    {
        this.sym = doc;
        this.pref = new PropertiesData(preferences);
        
        format = pref.getEnum(PROP_FORMAT, MovieExporterFormat.FRAMES);
//        width = pref.getInt(PROP_WIDTH, 640);
//        height = pref.getInt(PROP_HEIGHT, 480);
        frameCur = pref.getBoolean(PROP_FRAME_CURRENT, true);
        frameStart = pref.getInt(PROP_FRAME_START, 0);
        frameEnd = pref.getInt(PROP_FRAME_END, 10);
        frameStride = pref.getInt(PROP_FRAME_STRIDE, 1);
        
        framesDir = pref.getString(PROP_FRAMES_DIR, "");
        framesPrefix = pref.getString(PROP_FRAMES_PREFIX, "frame");
        framesImageFormat = pref.getString(PROP_FRAMES_IMG_FMT, "png");
        framesNumberPadding = pref.getInt(PROP_FRAMES_NUM_PAD, 4);

        seqFile = pref.getString(PROP_SEQ_FILE, "movie");
        seqFormat = pref.getString(PROP_SEQ_FORMAT, JMFExporter.VIDEO_QT);
    }
    
    public void savePreferences()
    {
        pref.setEnum(PROP_FORMAT, format);
//        pref.setInt(PROP_WIDTH, width);
//        pref.setInt(PROP_HEIGHT, height);
        pref.setBoolean(PROP_FRAME_CURRENT, frameCur);
        pref.setInt(PROP_FRAME_START, frameStart);
        pref.setInt(PROP_FRAME_END, frameEnd);
        pref.setInt(PROP_FRAME_STRIDE, frameStride);
        
        pref.setString(PROP_FRAMES_DIR, framesDir);
        pref.setString(PROP_FRAMES_PREFIX, framesPrefix);
        pref.setString(PROP_FRAMES_IMG_FMT, framesImageFormat);
        pref.setInt(PROP_FRAMES_NUM_PAD, framesNumberPadding);

        pref.setString(PROP_SEQ_FILE, seqFile);
        pref.setString(PROP_SEQ_FORMAT, seqFormat);
    }

    public void doExport()
    {
        if (composition == null)
        {
            return;
        }
        
        switch (format)
        {
            case FRAMES:
                exportFrames();
                break;
            case SEQ:
                exportSeq();
                break;
        }
    }
    
    private void exportSeq()
    {
        JMFExporter exporter = new JMFExporter(this);
        exporter.exportSeq();
    }
    
    private void exportFrames()
    {
        final File fileDir = new File(framesDir);
        if (!fileDir.isDirectory() || !fileDir.canWrite())
        {
            JOptionPane.showMessageDialog(sym.getDocument().getEnv().getSwingRoot(),
                "Cannot write to " + fileDir, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MovieCapture capture = new MovieCapture(composition);
        
        int trackFrame = sym.getRoot().getTrackLibrary().getFrameCur();
//        int trackUid = sym.getRoot().getTrackLibrary().getCurTrackUid();
        
        int fBegin = frameCur ? trackFrame : frameStart;
        int fEnd = frameCur ? trackFrame : frameEnd;
        
        for (int i = fBegin; i <= fEnd; i += frameStride)
        {
            String frameStrn = "" + i;
            while (frameStrn.length() < framesNumberPadding)
            {
                frameStrn = "0" + frameStrn;
            }

            File fileOut = new File(fileDir, 
                    framesPrefix + "_" + frameStrn + "." + framesImageFormat);

            BufferedImage img = capture.getImage(new FrameKey(i), true);

            try
            {
                ImageIO.write(img, framesImageFormat, fileOut);
            } catch (IOException ex)
            {
                Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //--------------
    
    /**
     * @return the format
     */
    public MovieExporterFormat getFormat()
    {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(MovieExporterFormat format)
    {
        this.format = format;
    }

    /**
     * @return the framesImageFormat
     */
    public String getFramesImageFormat()
    {
        return framesImageFormat;
    }

    /**
     * @param framesImageFormat the framesImageFormat to set
     */
    public void setFramesImageFormat(String framesImageFormat)
    {
        this.framesImageFormat = framesImageFormat;
    }

    /**
     * @return the framesNumberPadding
     */
    public int getFramesNumberPadding()
    {
        return framesNumberPadding;
    }

    /**
     * @param framesNumberPadding the framesNumberPadding to set
     */
    public void setFramesNumberPadding(int framesNumberPadding)
    {
        this.framesNumberPadding = framesNumberPadding;
    }

    /**
     * @return the doc
     */
    public NodeSymbol getSymbol()
    {
        return sym;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(NodeSymbol doc)
    {
        this.sym = doc;
    }

    /**
     * @return the pref
     */
    public PropertiesData getPref()
    {
        return pref;
    }

    /**
     * @param pref the pref to set
     */
    public void setPref(PropertiesData pref)
    {
        this.pref = pref;
    }

    /**
     * @return the framesDir
     */
    public String getFramesDir()
    {
        return framesDir;
    }

    /**
     * @param framesDir the framesDir to set
     */
    public void setFramesDir(String framesDir)
    {
        this.framesDir = framesDir;
    }

    /**
     * @return the framesPrefix
     */
    public String getFramesPrefix()
    {
        return framesPrefix;
    }

    /**
     * @param framesPrefix the framesPrefix to set
     */
    public void setFramesPrefix(String framesPrefix)
    {
        this.framesPrefix = framesPrefix;
    }

    /**
     * @return the seqFile
     */
    public String getSeqFile()
    {
        return seqFile;
    }

    /**
     * @param seqFile the seqFile to set
     */
    public void setSeqFile(String seqFile)
    {
        this.seqFile = seqFile;
    }

    /**
     * @return the seqFormat
     */
    public String getSeqFormat()
    {
        return seqFormat;
    }

    /**
     * @param seqFormat the seqFormat to set
     */
    public void setSeqFormat(String seqFormat)
    {
        this.seqFormat = seqFormat;
    }

//    /**
//     * @return the width
//     */
//    public int getWidth()
//    {
//        return width;
//    }
//
//    /**
//     * @param width the width to set
//     */
//    public void setWidth(int width)
//    {
//        this.width = width;
//    }
//
//    /**
//     * @return the height
//     */
//    public int getHeight()
//    {
//        return height;
//    }
//
//    /**
//     * @param height the height to set
//     */
//    public void setHeight(int height)
//    {
//        this.height = height;
//    }

    /**
     * @return the frameCur
     */
    public boolean isFrameCur()
    {
        return frameCur;
    }

    /**
     * @param frameCur the frameCur to set
     */
    public void setFrameCur(boolean frameCur)
    {
        this.frameCur = frameCur;
    }

    /**
     * @return the frameStart
     */
    public int getFrameStart()
    {
        return frameStart;
    }

    /**
     * @param frameStart the frameStart to set
     */
    public void setFrameStart(int frameStart)
    {
        this.frameStart = frameStart;
    }

    /**
     * @return the frameEnd
     */
    public int getFrameEnd()
    {
        return frameEnd;
    }

    /**
     * @param frameEnd the frameEnd to set
     */
    public void setFrameEnd(int frameEnd)
    {
        this.frameEnd = frameEnd;
    }

    /**
     * @return the frameStride
     */
    public int getFrameStride()
    {
        return frameStride;
    }

    /**
     * @param frameStride the frameStride to set
     */
    public void setFrameStride(int frameStride)
    {
        this.frameStride = frameStride;
    }

    /**
     * @return the composition
     */
    public RavenNodeComposition getComposition()
    {
        return composition;
    }

    /**
     * @param composition the composition to set
     */
    public void setComposition(RavenNodeComposition composition)
    {
        this.composition = composition;
    }

}
