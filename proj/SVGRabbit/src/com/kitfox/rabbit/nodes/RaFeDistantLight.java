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
public class RaFeDistantLight extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeDistantLight>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feDistantLight");
        }

        @Override
        public RaFeDistantLight create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeDistantLight haNode = new RaFeDistantLight();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setAzimuth(parseFloat(attr.get("azimuth"), 0));
            haNode.setElevation(parseFloat(attr.get("elevation"), 0));

            return haNode;
        }
    }

    private float azimuth;
    private float elevation;

    /**
     * @return the azimuth
     */
    public float getAzimuth() {
        return azimuth;
    }

    /**
     * @param azimuth the azimuth to set
     */
    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * @return the elevation
     */
    public float getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(float elevation) {
        this.elevation = elevation;
    }
}
