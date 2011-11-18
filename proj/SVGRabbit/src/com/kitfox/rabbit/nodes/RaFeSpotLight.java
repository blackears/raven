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
public class RaFeSpotLight extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeSpotLight>
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
        public RaFeSpotLight create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeSpotLight haNode = new RaFeSpotLight();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setZ(parseFloat(attr.get("z"), 0));
            haNode.setPointsAtX(parseFloat(attr.get("pointsAtX"), 0));
            haNode.setPointsAtY(parseFloat(attr.get("pointsAtY"), 0));
            haNode.setPointsAtZ(parseFloat(attr.get("pointsAtZ"), 0));
            haNode.setSpecularExponent(parseFloat(attr.get("specularExponent"), 0));
            haNode.setLimitingConeAngle(parseFloat(attr.get("limitingConeAngle"), 0));

            return haNode;
        }
    }

    private float x;
    private float y;
    private float z;
    private float pointsAtX;
    private float pointsAtY;
    private float pointsAtZ;
    private float specularExponent;
    private float limitingConeAngle;

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

    /**
     * @return the pointsAtX
     */
    public float getPointsAtX() {
        return pointsAtX;
    }

    /**
     * @param pointsAtX the pointsAtX to set
     */
    public void setPointsAtX(float pointsAtX) {
        this.pointsAtX = pointsAtX;
    }

    /**
     * @return the pointsAtY
     */
    public float getPointsAtY() {
        return pointsAtY;
    }

    /**
     * @param pointsAtY the pointsAtY to set
     */
    public void setPointsAtY(float pointsAtY) {
        this.pointsAtY = pointsAtY;
    }

    /**
     * @return the pointsAtZ
     */
    public float getPointsAtZ() {
        return pointsAtZ;
    }

    /**
     * @param pointsAtZ the pointsAtZ to set
     */
    public void setPointsAtZ(float pointsAtZ) {
        this.pointsAtZ = pointsAtZ;
    }

    /**
     * @return the specularExponent
     */
    public float getSpecularExponent() {
        return specularExponent;
    }

    /**
     * @param specularExponent the specularExponent to set
     */
    public void setSpecularExponent(float specularExponent) {
        this.specularExponent = specularExponent;
    }

    /**
     * @return the limitingConeAngle
     */
    public float getLimitingConeAngle() {
        return limitingConeAngle;
    }

    /**
     * @param limitingConeAngle the limitingConeAngle to set
     */
    public void setLimitingConeAngle(float limitingConeAngle) {
        this.limitingConeAngle = limitingConeAngle;
    }
}
