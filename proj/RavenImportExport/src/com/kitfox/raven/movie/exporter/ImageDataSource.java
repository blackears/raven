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

import java.io.IOException;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 *
 * @author kitfox
 */
public class ImageDataSource extends PullBufferDataSource
{
    ImageSourceStream imgStream;
    MovieExporterContext ctx;

    public ImageDataSource(MovieExporterContext ctx)
    {
        this.ctx = ctx;
        imgStream = new ImageSourceStream(ctx);
    }

    @Override
    public PullBufferStream[] getStreams()
    {
        return new PullBufferStream[]{imgStream};
    }

    @Override
    public String getContentType()
    {
        return ContentDescriptor.RAW;
    }

    @Override
    public void connect() throws IOException
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public void start() throws IOException
    {
    }

    @Override
    public void stop() throws IOException
    {
    }

    @Override
    public Object getControl(String string)
    {
        return null;
    }

    @Override
    public Object[] getControls()
    {
        return new Object[0];
    }

    @Override
    public Time getDuration()
    {
        return DURATION_UNKNOWN;
    }
    
}
