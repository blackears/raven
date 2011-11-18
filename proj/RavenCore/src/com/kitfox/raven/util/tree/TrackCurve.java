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

package com.kitfox.raven.util.tree;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class TrackCurve<T>
{
    private final Class<T> dataType;
    final PropertyProvider<T> prov;

    private Repeat before;
    private Repeat after;
    
    private HashMap<Integer, TrackKey<T>> keyMap =
            new HashMap<Integer, TrackKey<T>>();

    ArrayList<TrackEntry> entries;
    private int minFrame = Integer.MAX_VALUE;
    private int maxFrame = Integer.MIN_VALUE;

    public TrackCurve(Class<T> dataType)
    {
        this(dataType, Repeat.NONE, Repeat.NONE, null);
    }

    public TrackCurve(Class<T> dataType, Repeat before, Repeat after, HashMap<Integer, TrackKey<T>> keyMap)
    {
        this.dataType = dataType;
        this.before = before;
        this.after = after;

        this.prov = PropertyProviderIndex.inst().getProviderBest(dataType);

        if (keyMap != null)
        {
            this.keyMap.putAll(keyMap);
        }
    }

    public TrackCurve(TrackCurve<T> curve)
    {
        this(curve.dataType, curve.before, curve.after, curve.keyMap);
    }

    public ArrayList<Integer> getFrames()
    {
        ArrayList<Integer> list = new ArrayList<Integer>(keyMap.keySet());
        Collections.sort(list);
        return list;
    }

    private void setDirty()
    {
        entries = null;
        minFrame = Integer.MAX_VALUE;
        maxFrame = Integer.MIN_VALUE;
    }

    public T evaluate(int frame, NodeDocument doc)
    {
        if (keyMap.isEmpty())
        {
            return null;
        }

        //Remap frame if outside domain
        buildCache();
        int span = (int)Math.floor(
                (float)(frame - minFrame) / (maxFrame - minFrame));

        Repeat repeatStyle = null;
        TrackKey<T> minKey = keyMap.get(minFrame);
        TrackKey<T> maxKey = keyMap.get(maxFrame);
        TrackKey<T> repeatKey = null;
        if (span < 0)
        {
            repeatStyle = before;
            repeatKey = minKey;
        }
        else if (span > 1)
        {
            repeatStyle = after;
            repeatKey = maxKey;
        }

        if (repeatStyle != null)
        {
            switch (before)
            {
                case NONE:
                    return null;
                case CONST:
                    return repeatKey.getData().getValue(doc);
                case REPEAT:
                case REPEAT_OFFSET:
                    frame = frame - minFrame + span * (maxFrame - minFrame);
                    break;
                case REFLECT:
                    if ((span & 1) == 1)
                    {
                        //odd
                        frame = minFrame + span * (maxFrame - minFrame + 1) - frame - 1;
                    }
                    else
                    {
                        //even
                        frame = frame - minFrame + span * (maxFrame - minFrame);
                    }
                    break;
            }
        }

        //If we hit a key, return it directly
        TrackKey<T> key = keyMap.get(frame);
        if (key != null)
        {
            return key.getData().getValue(doc);
        }

        //Interpolate value
        ArrayList<TrackEntry> arr = getEntries();

        for (int i = 0; i < arr.size() - 1; ++i)
        {
            TrackEntry e0 = arr.get(i);
            TrackEntry e1 = arr.get(i + 1);
            if (frame > e0.frame && frame < e1.frame)
            {
                TrackKey<T> k0 = e0.key;
                TrackKey<T> k1 = e1.key;

                if (k0.getInterp() == TrackKey.Interp.NONE)
                {
                    return null;
                }

                if (repeatStyle == Repeat.REPEAT_OFFSET)
                {
                    return prov.interpolateWithOffset(doc,
                            k0, k1, frame, e0.frame, e1.frame,
                            minKey, maxKey, span);
                }
                else
                {
                    return prov.interpolate(doc,
                            k0, k1, frame, e0.frame, e1.frame);
                }
            }
        }

        return null;
    }

    private void buildCache()
    {
        if (entries == null)
        {
            minFrame = Integer.MAX_VALUE;
            maxFrame = Integer.MIN_VALUE;

            entries = new ArrayList<TrackEntry>(keyMap.size());

            for (Integer i: keyMap.keySet())
            {
                minFrame = Math.min(minFrame, i);
                maxFrame = Math.max(maxFrame, i);

                TrackKey<T> key = keyMap.get(i);
                entries.add(new TrackEntry(i, key));
            }
            Collections.sort(entries);
        }
    }

    protected ArrayList<TrackEntry> getEntries()
    {
        buildCache();
        return entries;
    }

    public TrackKey<T> getKey(int frame)
    {
        return keyMap.get(frame);
    }

    public void setKey(int frame, TrackKey<T> key)
    {
        keyMap.put(frame, key);
        setDirty();
    }

    public TrackKey<T> removeKey(int frame)
    {
        setDirty();
        return keyMap.remove(frame);
    }

    public boolean isKeyAt(int frame)
    {
        return keyMap.containsKey(frame);
    }

    /**
     * Determines if an interpolated value is calcuated for a given frame.
     * Keys aren't interpolated, so this method will return false if frame
     * is a keyframe.  A span with an interpolation type of NONE will also
     * return false.
     *
     * @param frame
     * @return true if an interpolated value is calculated for this frame.
     */
    public boolean isInterpolatedAt(int frame)
    {
        if (keyMap.isEmpty() || isKeyAt(frame))
        {
            return false;
        }

        ArrayList<TrackEntry> arr = getEntries();

        for (int i = 0; i < arr.size() - 1; ++i)
        {
            TrackEntry entry = arr.get(i);
            TrackEntry entryNext = arr.get(i + 1);
            if (frame > entry.frame && frame < entryNext.frame)
            {
                return entry.key.getInterp() != TrackKey.Interp.NONE;
            }
        }

        return false;
    }

    public int getNumKeys()
    {
        return keyMap.size();
    }

    /**
     * @return the before
     */
    public Repeat getBefore()
    {
        return before;
    }

    /**
     * @param before the before to set
     */
    public void setBefore(Repeat before)
    {
        this.before = before;
    }

    /**
     * @return the after
     */
    public Repeat getAfter()
    {
        return after;
    }

    /**
     * @param after the after to set
     */
    public void setAfter(Repeat after)
    {
        this.after = after;
    }

    /**
     * @return the minFrame
     */
    public int getMinFrame()
    {
        buildCache();
        return minFrame;
    }

    /**
     * @return the maxFrame
     */
    public int getMaxFrame()
    {
        buildCache();
        return maxFrame;
    }

    public Path2D.Double getCurvePath(NodeDocument doc)
    {
        Path2D.Double path = new Path2D.Double();
        T prev = null;
        for (int i = getMinFrame(); i <= getMaxFrame(); ++i)
        {
            T val = evaluate(i, doc);
            if (prev == null)
            {
                path.moveTo(i, prov.asDouble(val));
            }
            else
            {
                path.lineTo(i, prov.asDouble(val));
            }
            prev = val;
        }

        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrackCurve<T> other = (TrackCurve<T>) obj;
        if (this.before != other.before) {
            return false;
        }
        if (this.after != other.after) {
            return false;
        }
        if (this.keyMap != other.keyMap && (this.keyMap == null || !this.keyMap.equals(other.keyMap))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.before != null ? this.before.hashCode() : 0);
        hash = 79 * hash + (this.after != null ? this.after.hashCode() : 0);
        hash = 79 * hash + (this.keyMap != null ? this.keyMap.hashCode() : 0);
        return hash;
    }

    /**
     * @return the dataType
     */
    public Class<T> getDataType() {
        return dataType;
    }

    public boolean isEmpty()
    {
        return keyMap.isEmpty();
    }

    public double getNumericValue(int frame, NodeDocument doc)
    {
        T value = evaluate(frame, doc);
        return prov.asDouble(value);
    }

    public int getPrevKeyFrame(int curFrame)
    {
        int bestFrame = Integer.MIN_VALUE;

        for (Integer frame: getFrames())
        {
            if (frame < curFrame && frame > bestFrame)
            {
                bestFrame = frame;
            }
        }

        return bestFrame;
    }

    public int getNextKeyFrame(int curFrame)
    {
        int bestFrame = Integer.MAX_VALUE;

        for (Integer frame: getFrames())
        {
            if (frame > curFrame && frame < bestFrame)
            {
                bestFrame = frame;
            }
        }

        return bestFrame;
    }


    
    //---------------------------------

    public class TrackEntry implements Comparable<TrackEntry>
    {
        final int frame;
        final TrackKey<T> key;

        public TrackEntry(int frame, TrackKey<T> key)
        {
            this.frame = frame;
            this.key = key;
        }

        @Override
        public int compareTo(TrackEntry other)
        {
            return frame - other.frame;
        }
    }

    public static enum Repeat {
        NONE, //Do not write values
        CONST, //Write final value
        REPEAT, //Repeat sequence from begining
        REFLECT, //Repeat reflection of the sequence
        REPEAT_OFFSET //Repeat sequence offset by the final value
    }
    
}
