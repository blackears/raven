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
public class RaFePointLight extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFePointLight>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("fePointLight");
        }

        @Override
        public RaFePointLight create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFePointLight haNode = new RaFePointLight();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setZ(parseFloat(attr.get("z"), 0));

            return haNode;
        }
    }

    private float x;
    private float y;
    private float z;

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public float getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(float z) {
        this.z = z;
    }
}
