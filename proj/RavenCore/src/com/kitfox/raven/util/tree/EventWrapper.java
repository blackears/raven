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

import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class EventWrapper<NodeType extends NodeObject,
        EventClassType extends EventObject>
{
    private final NodeType node;
    private final String name;
    private final Class<EventClassType> event;
    private final SourceCode source = new SourceCode();

    ArrayList<EventWrapperListener> listeners = new ArrayList<EventWrapperListener>();

    public EventWrapper(NodeType node, String name, Class<EventClassType> event)
    {
        this.node = node;
        this.name = name;
        this.event = event;

        node.registerEventWrapper(this);
    }

    /**
     * @return the source
     */
    public SourceCode getSource()
    {
        return source;
    }

    /**
     * @return the node
     */
    public NodeType getNode()
    {
        return node;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the event
     */
    public Class<EventClassType> getEvent()
    {
        return event;
    }

//    public EventType export()
//    {
//        EventType type = new EventType();
//
//        type.setName(name);
//        type.setSource(source.export());
//
//        return type;
//    }
//
//    public void load(EventType eventType)
//    {
//        source.load(eventType.getSource());
//    }

    
}
