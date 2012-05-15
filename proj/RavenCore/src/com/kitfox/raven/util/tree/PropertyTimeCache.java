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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A time based cache that caches info per frame.
 *
 * Can set multiple properties to be watched.  When these properties emit
 * events indicating direct value or track values have changed, cached
 * values that are now potentially invalid are also cleared.
 *
 * @author kitfox
 */
public class PropertyTimeCache<ValueType>
        implements PropertyWrapperListener
{
    ArrayList<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();

    //Cache per track
//    HashMap<Integer, TrackCache> trackCache = new HashMap<Integer, TrackCache>();
    TrackCache trackCache;
    ValueType directCache;

    public void addDependentProperty(PropertyWrapper wrap)
    {
        properties.add(wrap);
        wrap.addPropertyWrapperListener(this);
        
        trackCache = new TrackCache();
    }

    public ValueType getValue(int frame)
    {
//        TrackCache track = trackCache.get(trackUid);

        return trackCache.getValue(frame);
    }

    public ValueType getDirectValue()
    {
        return directCache;
    }

    public void cacheDirect(ValueType value)
    {
        directCache = value;
    }

    public void cache(int trackUid, int frame, ValueType value)
    {
//        TrackCache track = trackCache.get(trackUid);
//        if (track == null)
//        {
//            track = new TrackCache(trackUid);
//            trackCache.put(trackUid, track);
//        }

        trackCache.cache(frame, value);
    }

    @Override
    public void propertyWrapperDataChanged(PropertyChangeEvent evt)
    {
        directCache = null;
        
        PropertyWrapper wrap = (PropertyWrapper)evt.getSource();
        TrackCurve curve = wrap.getTrackCurve();
        trackCache.clearNotKeyed(curve);

//            if (curve == null)
//            {
//                //This track has no keyed information for this
//                // property.  Hence, setting direct value will alter all
//                // frames and cache for track should be cleared
//                it.remove();
//            }
//            else
//            {
//                track.clearNotKeyed(curve);
//            }
    }

    @Override
    public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
    {
//        trackCache.remove(evt.getTrackUid());
    }

    @Override
    public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
    {
        PropertyWrapper wrap = evt.getSource();
//        int trackUid = evt.getTrackUid();
//        TrackCache frameCache = trackCache.get(trackUid);
//        if (frameCache == null)
//        {
//            return;
//        }

        TrackCurve curve = wrap.getTrackCurve();

        assert curve != null :
            "Could not find track that track change event occurred on";

        int frame = evt.getFrame();
        
        int min = curve.getPrevKeyFrame(frame);
        if (min == Integer.MIN_VALUE)
        {
            min = frame;
        }

        int max = curve.getNextKeyFrame(frame);
        if (max == Integer.MAX_VALUE)
        {
            max = frame;
        }

        trackCache.clearRange(min, max);
    }

    //-------------------------------------
    class TrackCache
    {
//        final int trackUid;
        //Cache per frame
        HashMap<Integer, ValueType> cache = new HashMap<Integer, ValueType>();

        public TrackCache()
        {
//            this.trackUid = trackUid;
        }

        public ValueType getValue(int frame)
        {
            return cache.get(frame);
        }

        public void cache(int frame, ValueType value)
        {
            cache.put(frame, value);
        }

        private void clearRange(int min, int max)
        {
            for (int i = min; i <= max; ++i)
            {
                cache.remove(i);
            }
        }

        private void clearNotKeyed(TrackCurve curve)
        {
            for (Iterator<Integer> it = cache.keySet().iterator();
                it.hasNext();)
            {
                Integer frame = it.next();
                if (!curve.isKeyAt(frame) && !curve.isInterpolatedAt(frame))
                {
                    it.remove();
                }
            }
        }
    }
}
