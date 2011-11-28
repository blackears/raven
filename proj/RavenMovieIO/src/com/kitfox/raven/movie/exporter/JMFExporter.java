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

import com.sun.media.util.Registry;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.JOptionPane;

/**
 * Based on JpegImagesToMovie.java
 *
 * @author kitfox
 */
public class JMFExporter
        implements ControllerListener, DataSinkListener
{
    public static final String VIDEO_MPEG = "mpeg";
    public static final String VIDEO_AVI = "avi";
    public static final String VIDEO_QT = "qt";
    public static final String VIDEO_VIVO = "viv";

    final MovieExporterContext ctx;
    final Object waitSync = new Object();
    boolean stateTransitionOK = true;
    final Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;

    public JMFExporter(MovieExporterContext ctx)
    {
        this.ctx = ctx;
    }

    public void exportSeq()
    {
        Registry.set("secure.allowSaveFileFromApplets", Boolean.TRUE);
        
        String seqFile = ctx.getSeqFile();

        File outFile = new File(seqFile);
        File parentFile = outFile.getParentFile();
        if (parentFile != null)
        {
            //Under webstart, parentFile may be null
            parentFile.mkdirs();
        }
        
        if (outFile.exists() && !outFile.canWrite())
        {
            error("Cannot write to directory " + parentFile.getAbsolutePath());
            return;
        }

        ImageDataSource ids = new ImageDataSource(ctx);

        Processor p;
        try
        {
            p = Manager.createProcessor(ids);
        } catch (IOException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (NoProcessorException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        p.addControllerListener(this);

        p.configure();
        if (!waitForState(p, p.Configured))
        {
            error("Failed to configure the processor.");
            return;
        }

//            p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
        String format = ctx.getSeqFormat();
        
        //At the moment I have only got this to work for QuickTime
format = VIDEO_QT;
        
        System.err.println("Exporting " + format);
        if (VIDEO_AVI.equals(format))
        {
            p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.MSVIDEO));
        }
        else if (VIDEO_MPEG.equals(format))
        {
            p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.MPEG));
        }
        else if (VIDEO_QT.equals(format))
        {
            p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
        }
        else if (VIDEO_VIVO.equals(format))
        {
            p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.VIVO));
        }
        else
        {
            error("Unknown format " + format);
            return;
        }

        // Query for the processor for supported formats.
        // Then set it on the processor.
        TrackControl tcs[] = p.getTrackControls();
        Format f[] = tcs[0].getSupportedFormats();
        if (f == null || f.length <= 0)
        {
            error("The mux does not support the input format: " + tcs[0].getFormat());
            return;
        }
        tcs[0].setFormat(f[0]);

        p.realize();
        if (!waitForState(p, p.Realized))
        {
            error("Failed to realize the processor.");
            return;
        }

        //Create sink to write data to
        String fileSuffix = "." + format;
        if (!outFile.getName().endsWith(fileSuffix))
        {
            outFile = new File(outFile.getParentFile(), outFile.getName() + fileSuffix);
        }
        MediaLocator outML = new MediaLocator(outFile.toURI().toString());

        DataSource ds = p.getDataOutput();
        DataSink dsink;
        try
        {
            dsink = Manager.createDataSink(ds, outML);
            dsink.open();
        } catch (IOException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (SecurityException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (NoDataSinkException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        dsink.addDataSinkListener(this);
        fileDone = false;

        try
        {
            p.start();
            dsink.start();
        } catch (IOException e)
        {
            error("IO error during processing");
            return;
        }

        waitForFileDone();

        try
        {
            dsink.close();
        } catch (Exception e)
        {
        }
        p.removeControllerListener(this);
    }

    private void error(String message)
    {
        JOptionPane.showMessageDialog(ctx.getDoc().getEnv().getSwingRoot(),
                message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    boolean waitForFileDone()
    {
        synchronized (waitFileSync)
        {
            try
            {
                while (!fileDone)
                {
                    waitFileSync.wait();
                }
            } catch (Exception e)
            {
            }
        }
        return fileSuccess;
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state)
    {
        synchronized (waitSync)
        {
            try
            {
                while (p.getState() < state && stateTransitionOK)
                {
                    waitSync.wait();
                }
            } catch (Exception e)
            {
            }
        }
        return stateTransitionOK;
    }

    @Override
    public void controllerUpdate(ControllerEvent evt)
    {
        if (evt instanceof ConfigureCompleteEvent
                || evt instanceof RealizeCompleteEvent
                || evt instanceof PrefetchCompleteEvent)
        {
            synchronized (waitSync)
            {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else
        {
            if (evt instanceof ResourceUnavailableEvent)
            {
                synchronized (waitSync)
                {
                    stateTransitionOK = false;
                    waitSync.notifyAll();
                }
            } else
            {
                if (evt instanceof EndOfMediaEvent)
                {
                    evt.getSourceController().stop();
                    evt.getSourceController().close();
                }
            }
        }
    }

    @Override
    public void dataSinkUpdate(DataSinkEvent evt)
    {
        if (evt instanceof EndOfStreamEvent)
        {
            synchronized (waitFileSync)
            {
                fileDone = true;
                waitFileSync.notifyAll();
            }
        } else
        {
            if (evt instanceof DataSinkErrorEvent)
            {
                synchronized (waitFileSync)
                {
                    fileDone = true;
                    fileSuccess = false;
                    waitFileSync.notifyAll();
                }
            }
        }
    }
}
