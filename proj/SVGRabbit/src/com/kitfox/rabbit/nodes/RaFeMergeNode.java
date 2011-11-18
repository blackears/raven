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

package com.kitfox.rabbit.nodes;

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.raven.util.service.ServiceInst;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaFeMergeNode extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeMergeNode>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feMergeNode");
        }

        @Override
        public RaFeMergeNode create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeMergeNode haNode = new RaFeMergeNode();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));

            return haNode;
        }
    }

    private int in;

    /**
     * @return the in
     */
    public int getIn() {
        return in;
    }

    /**
     * @param in the in to set
     */
    public void setIn(int in) {
        this.in = in;
    }
}
