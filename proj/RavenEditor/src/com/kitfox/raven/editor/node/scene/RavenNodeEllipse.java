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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.shape.CyEllipse2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;

/**
 *
 * @author kitfox
 */
public class RavenNodeEllipse extends RavenNodeInscribedShape
{
    protected RavenNodeEllipse(int uid)
    {
        super(uid);
    }

//    @Override
//    protected CyEllipse2d createShape()
//    {
//        return new CyEllipse2d(0, 0, width.getValue(), height.getValue());
//    }

    @Override
    public CyShape createShapeLocal(FrameKey time)
    {
        NodeDocument doc = getDocument();
        float cx = x.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        float cy = y.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        double cWidth = width.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);
        double cHeight = height.getData(time.getTrackUid(), time.getTrackUid()).getValue(doc);

        return new CyEllipse2d(cx, cy, cWidth, cHeight);
    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeEllipse>
    {
        public Provider()
        {
            super(RavenNodeEllipse.class, "Ellipse", "/icons/node/ellipse.png");
        }

        @Override
        public RavenNodeEllipse createNode(int uid)
        {
            return new RavenNodeEllipse(uid);
        }
    }
}
