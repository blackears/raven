/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.raven.movie.exporter;

import com.kitfox.coyote.drawRecord.CyDrawGroupZOrder;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.jogl.CyGLWrapperJOGL;
import com.kitfox.raven.editor.node.scene.FrameKey;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.common.ServiceDeviceCamera;
import com.kitfox.raven.editor.view.displayCy.CyRenderService;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackLibrary;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.nativewindow.AbstractGraphicsDevice;
import javax.media.opengl.*;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class MovieExporterContext
{
    private static final String PROP_FORMAT = "format";
    private MovieExporterFormat format;
    
    private static final String PROP_WIDTH = "width";
    private int width;
    
    private static final String PROP_HEIGHT = "height";
    private int height;
    
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
    
    private NodeDocument doc;
    private PropertiesData pref;
    
    public MovieExporterContext(NodeDocument doc, Properties preferences)
    {
        this.doc = doc;
        this.pref = new PropertiesData(preferences);
        
        format = pref.getEnum(PROP_FORMAT, MovieExporterFormat.FRAMES);
        width = pref.getInt(PROP_WIDTH, 640);
        height = pref.getInt(PROP_HEIGHT, 480);
        
        framesDir = pref.getString(PROP_FRAMES_DIR, "");
        framesPrefix = pref.getString(PROP_FRAMES_PREFIX, "frame");
        framesImageFormat = pref.getString(PROP_FRAMES_IMG_FMT, "png");
        framesNumberPadding = pref.getInt(PROP_FRAMES_NUM_PAD, 4);

        seqFile = pref.getString(PROP_SEQ_FILE, "movie");
        seqFormat = pref.getString(PROP_SEQ_FORMAT, "avi");
    }
    
    public void savePreferences()
    {
        pref.setEnum(PROP_FORMAT, format);
        pref.setInt(PROP_WIDTH, width);
        pref.setInt(PROP_HEIGHT, height);
        
        pref.setString(PROP_FRAMES_DIR, framesDir);
        pref.setString(PROP_FRAMES_PREFIX, framesPrefix);
        pref.setString(PROP_FRAMES_IMG_FMT, framesImageFormat);
        pref.setInt(PROP_FRAMES_NUM_PAD, framesNumberPadding);

        pref.setString(PROP_SEQ_FILE, seqFile);
        pref.setString(PROP_SEQ_FORMAT, seqFormat);
    }

    public void doExport()
    {
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

    private void exportFrames()
    {
//        CyRenderService servRend = doc.getNodeService(CyRenderService.class, false);
//        ServiceDeviceCamera servCam = doc.getNodeService(ServiceDeviceCamera.class, false);
        
        GLProfile.initSingleton(true);
        GLDrawableFactory fact = GLDrawableFactory.getDesktopFactory();
        
//        boolean usePbuffer = true;
        if (!fact.canCreateGLPbuffer(null))
        {
            JOptionPane.showMessageDialog(doc.getEnv().getSwingRoot(),
                "Cannot create pbuffer to accellerate offscreen rendering", 
                "Error", JOptionPane.ERROR_MESSAGE);
//            usePbuffer = false;
            return;
        }
        
        File fileDir = new File(framesDir);
        if (!fileDir.isDirectory() || !fileDir.canWrite())
        {
            JOptionPane.showMessageDialog(doc.getEnv().getSwingRoot(),
                "Cannot write to " + fileDir, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        GLCapabilitiesChooser glCapsChooser = new DefaultGLCapabilitiesChooser();
        AbstractGraphicsDevice agd = fact.getDefaultDevice();
//        caps.setOnscreen(false);
//        caps.setPBuffer(usePbuffer);
//        caps.setDoubleBuffered(false);
        
        
        GLPbuffer drawable = 
                fact.createGLPbuffer(agd, 
                caps, glCapsChooser, 
                width, height, 
                null);
        GLContext context = drawable.getContext();
        context.makeCurrent();
        
        CyDrawGroupZOrder drawgroup = buildDrawlist();
        CyGLContext glContext = new CyGLContext();
        CyGLWrapperJOGL glWrap = new CyGLWrapperJOGL(drawable);
        
        drawgroup.render(glContext, glWrap, drawgroup);
        
        drawgroup.dispose();
        
        ByteBuffer buf = BufferUtil.allocateByte(width * height * 4);
        
        //pbuf.getGL().getGL2();
        glWrap.glReadPixels(0, 0, width, height, 
                CyGLWrapper.ReadPixelsFormat.GL_RGBA, 
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, buf);
        
        context.release();
        //drawable.destroy();
        
        //Prepare image
        buf.rewind();
        
        File fileOut = new File(fileDir, framesPrefix + "_test." + framesImageFormat);
        
        BufferedImage img = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < height; ++j)
        {
            for (int i = 0; i < width; ++i)
            {
                byte r = buf.get();
                byte g = buf.get();
                byte b = buf.get();
                byte a = buf.get();
                
                int rgba = ((a & 0xff) << 24) 
                        | ((r & 0xff) << 16) 
                        | ((g & 0xff) << 8) 
                        | (b & 0xff);
//if (r != -1)
//{
//System.err.printf("rgba(%d %d %d %d): %08x\n", r, g, b, a, rgba);
//}
                img.setRGB(i, (height - j - 1), rgba);
            }
        }
        
        try
        {
            ImageIO.write(img, framesImageFormat, fileOut);
        } catch (IOException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CyDrawGroupZOrder buildDrawlist()
    {
        TrackLibrary trackLib = doc.getTrackLibrary();
        Track track = trackLib.getCurTrack();
        int frame = trackLib.getCurFrame();
        
//        long curTime = System.currentTimeMillis();
        CyDrawGroupZOrder drawGroup = new CyDrawGroupZOrder();
        
        CyDrawStack stack = new CyDrawStack(width, height, 
//                track.getUid(), frame, 
//                curTime, curTime, 0, 
                drawGroup);
        
        FrameKey key = track == null ? FrameKey.DIRECT
                : new FrameKey(track.getUid(), frame);
        RenderContext ctx = new RenderContext(stack, key);

        CyRenderService serv = doc.getNodeService(CyRenderService.class, false);
        if (serv != null)
        {
            if (serv.getNumCameras() == 0)
            {
                serv.renderEditor(ctx);
            }
            else
            {
                serv.renderCamerasAll(ctx);
            }
        }
        
        return drawGroup;
    }
    
    private void exportSeq()
    {
//        FormatControl ctrl;
//        Format[] fmts = ctrl.getSupportedFormats();
        
        //FramePositioningControl
        //FrameGrabbingControl
        //PlugInManager.getPlugInList(null, null, width)
        
        throw new UnsupportedOperationException("Not yet implemented");
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
    public NodeDocument getDoc()
    {
        return doc;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(NodeDocument doc)
    {
        this.doc = doc;
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

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height)
    {
        this.height = height;
    }
}
