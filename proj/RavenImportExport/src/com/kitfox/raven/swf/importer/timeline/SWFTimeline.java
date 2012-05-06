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

import com.kitfox.swf.dataType.MATRIX;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class SWFTimeline
{
    HashMap<Integer, SWFTimelineTrack> trackMap = 
            new HashMap<Integer, SWFTimelineTrack>();
    
    int curFrame;
    
    public ArrayList<SWFTimelineTrack> getTracks()
    {
        return new ArrayList<SWFTimelineTrack>(trackMap.values());
    }
    
    private SWFTimelineTrack getOrCreateTrack(int depth)
    {
        SWFTimelineTrack track = trackMap.get(depth);
        if (track == null)
        {
            track = new SWFTimelineTrack(depth);
            trackMap.put(depth, track);
        }
        return track;
    }
    
    void placeObject(int characterId, int depth, MATRIX matrix, String name)
    {
        SWFTimelineTrack track = getOrCreateTrack(depth);
        
        SWFEventPlaceCharacter event = new SWFEventPlaceCharacter(
                characterId, matrix, name);
        track.setKeyFrame(curFrame, event);
    }

    void removeObject(int depth)
    {
        SWFTimelineTrack track = getOrCreateTrack(depth);
        track.setKeyFrame(curFrame, new SWFEventRemoveCharacter());
    }

    void showFrame()
    {
        ++curFrame;
    }
    
}
