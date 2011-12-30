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

package com.kitfox.raven.editor;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackLibrary;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class PlayerThread extends Thread
{
    final RavenEditor editor;
    long lastEmitTime = 0;

    PlayState state = PlayState.PAUSE;

    public PlayerThread(RavenEditor editor)
    {
        super("Raven Player");
        this.editor = editor;
        setDaemon(true);
        setPriority(MIN_PRIORITY);
    }

    public PlayState getPlayState()
    {
        return state;
    }

    public void setPlayState(PlayState state)
    {
        this.state = state;
        lastEmitTime = System.currentTimeMillis();
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (state == PlayState.PAUSE)
            {
//                Thread.yield();
                continue;
            }

            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                continue;
            }

            TrackLibrary lib = doc.getCurDocument().getTrackLibrary();
            Track track = lib.getCurTrack();
            if (track == null)
            {
                continue;
            }
            float fps = lib.fps.getValue();
            int frame = lib.getCurFrame();
            boolean loop = lib.loop.getValue();
            int frameFirst = track.frameStart.getValue();
            int frameLast = track.frameEnd.getValue();

            long curTime = System.currentTimeMillis();
            long delta = curTime - lastEmitTime;

            int frames = (int)((int)delta * fps / 1000);
            if (frames != 0)
            {
                lastEmitTime = curTime;
                if (state == PlayState.FORWARD)
                {
                    int newFrame = frame + frames;
                    if (newFrame > frameLast)
                    {
                        newFrame = loop ? frameFirst : frameLast;
                    }
                    lib.curFrame.setValue(newFrame, false);
//                    setFrame(newFrame);
                }
                else
                {
                    int newFrame = frame - frames;
                    if (newFrame < frameFirst)
                    {
                        newFrame = loop ? frameLast : frameFirst;
                    }
                    lib.curFrame.setValue(newFrame, false);
//                    setFrame(newFrame);
                }
            }

            try
            {
                Thread.sleep(100);
//                System.err.println("Daemon " + daemon++);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    int daemon = 0;

    public void pause()
    {
        setPlayState(PlayState.PAUSE);
    }

    public void playBackward()
    {
        setPlayState(PlayState.BACKWARD);
    }

    public void playForward()
    {
        setPlayState(PlayState.FORWARD);
    }

    //-------------------------
    public static enum PlayState
    {
        PAUSE, FORWARD, BACKWARD
    }
}

