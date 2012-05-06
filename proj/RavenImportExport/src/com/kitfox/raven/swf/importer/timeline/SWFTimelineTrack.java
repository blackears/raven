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

package com.kitfox.raven.swf.importer.timeline;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class SWFTimelineTrack implements Comparable<SWFTimelineTrack>
{
    HashMap<Integer, SWFTrackEvent> frameMap
            = new HashMap<Integer, SWFTrackEvent>();
    int depth;

    public SWFTimelineTrack(int depth)
    {
        this.depth = depth;
    }
    
    public SWFTrackEvent getKeyFrame(int frame)
    {
        return frameMap.get(frame);
    }
    
    public void setKeyFrame(int frame, SWFTrackEvent event)
    {
        frameMap.put(frame, event);
    }

    @Override
    public int compareTo(SWFTimelineTrack obj)
    {
        return depth - obj.depth;
    }
    
    public ArrayList<Integer> getKeyFrames()
    {
        return new ArrayList<Integer>(frameMap.keySet());
    }
    
    public ArrayList<SWFTrackEvent> getEvents()
    {
        return new ArrayList<SWFTrackEvent>(frameMap.values());
    }
}
