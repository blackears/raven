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

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeDocument;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.SourceStream;

/**
 *
 * @author kitfox
 */
public class ImageSourceStream 
        implements PullBufferStream
{
    VideoFormat format;
    int width;
    int height;
    int frameCur;
    int frameEnd;
    int frameStride;
    float frameRate;
    int trackUid;
    
    MovieExporterContext ctx;
    MovieCapture capture;

    boolean finished = false;
    
    public ImageSourceStream(MovieExporterContext ctx)
    {
        this.ctx = ctx;

        NodeDocument doc = ctx.getDoc();
        width = ctx.getWidth();
        height = ctx.getHeight();
        
        this.capture = new MovieCapture(doc, width, height);
        
        frameRate = doc.getTrackLibrary().getFps();
        trackUid = doc.getTrackLibrary().getCurTrackUid();
        
        if (ctx.isFrameCur())
        {
            frameCur = frameEnd = doc.getTrackLibrary().getCurFrame();
            frameStride = 1;
        }
        else
        {
            frameCur = ctx.getFrameStart();
            frameEnd = ctx.getFrameEnd();
            frameStride = ctx.getFrameStride();
        }
        
        
        format = new VideoFormat(VideoFormat.JPEG,
                new Dimension(ctx.getWidth(), ctx.getHeight()),
                Format.NOT_SPECIFIED,
                Format.byteArray,
                frameRate);
    }

    @Override
    public Format getFormat()
    {
        return format;
    }

    @Override
    public void read(Buffer buffer) throws IOException
    {
        if (frameCur > frameEnd)
        {
            // We are done.  Set EndOfMedia.
            buffer.setEOM(true);
            buffer.setOffset(0);
            buffer.setLength(0);
            finished = true;
            return;
        }

        BufferedImage img = capture.getImage(new FrameKey(trackUid, frameCur), false);
        frameCur += frameStride;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(img, "jpg", baos);
        } catch (IOException ex)
        {
            Logger.getLogger(MovieExporterContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] imageBytes = baos.toByteArray();

        buffer.setOffset(0);
        buffer.setLength(imageBytes.length);
        buffer.setFormat(format);
        buffer.setFlags(buffer.getFlags() | Buffer.FLAG_KEY_FRAME);

        buffer.setData(imageBytes);
    }

    @Override
    public ContentDescriptor getContentDescriptor()
    {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    @Override
    public long getContentLength()
    {
        return SourceStream.LENGTH_UNKNOWN;
    }

    @Override
    public boolean endOfStream()
    {
        return finished;
    }

    @Override
    public Object[] getControls()
    {
        return new Object[0];
    }

    @Override
    public Object getControl(String string)
    {
        return null;
    }

    @Override
    public boolean willReadBlock()
    {
        return false;
    }
    
}
