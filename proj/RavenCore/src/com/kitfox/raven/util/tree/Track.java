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

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;

/**
 *
 * @author kitfox
 */
public class Track extends NodeObject
{
    public static final String PROP_FRAMESTART = "frameStart";
    public final PropertyWrapperInteger<Track> frameStart =
            new PropertyWrapperInteger(this, PROP_FRAMESTART,
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_FRAMEEND = "frameEnd";
    public final PropertyWrapperInteger<Track> frameEnd =
            new PropertyWrapperInteger(this, PROP_FRAMEEND,
            PropertyWrapper.FLAGS_NOANIM, 100);

    protected Track(int uid)
    {
        super(uid);
    }

//    protected Track(NodeDocument doc)
//    {
//        this(doc.allocUid());
//    }
//
//    private Track(NodeObjectType nodeType)
//    {
//        this(nodeType.getUid());
//    }

    //----------------------------------------


    //-----------------------------------------------

    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<Track>
    {
        public Provider()
        {
            super(Track.class, "Track", "/icons/node/track.png");
        }

        @Override
        public Track createNode(int uid)
        {
            return new Track(uid);
        }
    }
}
