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

package com.kitfox.raven.util.undo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class History
{
    private final ArrayList<HistoryAction> actionList = new ArrayList<HistoryAction>();  //Points to gap just before index location
    private int undoCursor;
    private int maxUndo = 100;

    //Cache for writing large objects to so they don't have to fill up
    // memory
    RandomAccessFile fileCache;
    long fileCachePosition = 0;
    ArrayList<FileCacheFrame> fileCacheFrames = new ArrayList<FileCacheFrame>();

    ArrayList<HistoryListener> listeners = new ArrayList<HistoryListener>();

    ArrayList<HistoryTransaction> xactionStack = new ArrayList<HistoryTransaction>();

    private void trimToMaxSize()
    {
        //Trim to max size
        while (actionList.size() > maxUndo)
        {
            actionList.remove(0);
            --undoCursor;
        }
    }

    public void beginTransaction(String title)
    {
        HistoryTransaction xact = new HistoryTransaction(title);
        xactionStack.add(xact);
    }

    public void commitTransaction()
    {
        HistoryTransaction xact = xactionStack.remove(xactionStack.size() - 1);
        doAction(xact);
    }

    public void cancelTransaction()
    {
        xactionStack.remove(xactionStack.size() - 1);
    }

    public void doAction(HistoryAction action)
    {
        if (xactionStack.isEmpty())
        {
            action.redo(this);
            add(action);
        }
        else
        {
            HistoryTransaction xact = xactionStack.get(xactionStack.size() - 1);
            xact.add(action);
        }
    }

    protected void add(HistoryAction action)
    {
        //Remove future actions
        while (actionList.size() > undoCursor)
        {
            actionList.remove(actionList.size() - 1);
        }

        trimToMaxSize();

        actionList.add(action);
        ++undoCursor;

        fireUndoHistoryChanged();
    }

    public synchronized boolean canUndo()
    {
        return undoCursor >= 1;
    }

    public synchronized boolean canRedo()
    {
        return undoCursor < actionList.size();
    }

    public synchronized void undo()
    {
        if (!canUndo())
        {
            return;
        }
        HistoryAction action = actionList.get(undoCursor - 1);
        action.undo(this);
        --undoCursor;
        fireUndoHistoryChanged();
    }

    public synchronized void redo()
    {
        if (!canRedo())
        {
            return;
        }
        HistoryAction action = actionList.get(undoCursor);
        action.redo(this);
        ++undoCursor;
        fireUndoHistoryChanged();
    }

    public void addHistoryListener(HistoryListener l)
    {
        listeners.add(l);
    }

    public void removeHistoryListener(HistoryListener l)
    {
        listeners.remove(l);
    }

    private void fireUndoHistoryChanged()
    {
        EventObject evt = new EventObject(this);
        ArrayList<HistoryListener> list =
                new ArrayList<HistoryListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).historyChanged(evt);
        }
    }

    /**
     * @return the maxUndo
     */
    public int getMaxUndo()
    {
        return maxUndo;
    }

    /**
     * @param maxUndo the maxUndo to set
     */
    public synchronized void setMaxUndo(int maxUndo)
    {
        this.maxUndo = maxUndo;
        trimToMaxSize();
    }

    /**
     * @return the actionList
     */
    public ArrayList<HistoryAction> getActionList()
    {
        return new ArrayList<HistoryAction>(actionList);
    }

    public HistoryAction[] getActionListAsArray()
    {
        return actionList.toArray(new HistoryAction[actionList.size()]);
    }

    /**
     * @return the undoCursor
     */
    public int getUndoCursor() {
        return undoCursor;
    }

    public int getNumActions()
    {
        return actionList.size();
    }

    public void moveCursorTo(int idx)
    {
        idx = Math.min(Math.max(0, idx), actionList.size());

        while (undoCursor < idx)
        {
            redo();
        }
        
        while (undoCursor > idx)
        {
            undo();
        }
    }

    public void clear()
    {
        if (!xactionStack.isEmpty())
        {
            throw new UnsupportedOperationException("Cannot clear while a transaction is open");
        }

        actionList.clear();
        undoCursor = 0;
    }
    public void pushFileCache(byte[] data)
    {
        if (fileCache == null)
        {
            try
            {
                File file = File.createTempFile("historyCache", "dat");
                fileCache = new RandomAccessFile(file, "rw");
            } catch (IOException ex)
            {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try
        {
            FileChannel chan = fileCache.getChannel();
            MappedByteBuffer buf = chan.map(FileChannel.MapMode.READ_WRITE,
                    fileCachePosition, data.length);
            buf.put(data);

            fileCacheFrames.add(new FileCacheFrame(fileCachePosition, data.length));
            fileCachePosition += data.length;
        } catch (IOException ex)
        {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] popFileCache()
    {
        if (fileCachePosition == 0)
        {
            return null;
        }

        FileCacheFrame frame = fileCacheFrames.remove(fileCacheFrames.size() - 1);
        fileCachePosition = frame.position;

        byte[] data = new byte[frame.size];
        FileChannel chan = fileCache.getChannel();
        MappedByteBuffer buf;
        try
        {
            buf = chan.map(FileChannel.MapMode.READ_WRITE, fileCachePosition, data.length);
            buf.get(data);
        } catch (IOException ex)
        {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public void pushFileCacheString(String text)
    {
        pushFileCache(text.getBytes());
    }

    public String popFileCacheString()
    {
        byte[] data = popFileCache();
        return new String(data);
    }


    //--------------------------------
    class FileCacheFrame
    {
        final long position;
        final int size;

        public FileCacheFrame(long position, int size)
        {
            this.position = position;
            this.size = size;
        }

    }
}
