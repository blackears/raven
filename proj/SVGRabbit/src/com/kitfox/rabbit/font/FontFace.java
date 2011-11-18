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

package com.kitfox.rabbit.font;

/**
 *
 * @author kitfox
 */
public class FontFace
{
    private String fontFamily;
    private float unitsPerEm;
    private float ascent;
    private float descent;
//    private float underlineThickness;
//    private float underlinePosition;

    /**
     * @return the fontFamily
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * @param fontFamily the fontFamily to set
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * @return the unitsPerEm
     */
    public float getUnitsPerEm() {
        return unitsPerEm;
    }

    /**
     * @param unitsPerEm the unitsPerEm to set
     */
    public void setUnitsPerEm(float unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
    }

    /**
     * @return the ascent
     */
    public float getAscent() {
        return ascent;
    }

    /**
     * @param ascent the ascent to set
     */
    public void setAscent(float ascent) {
        this.ascent = ascent;
    }

    /**
     * @return the descent
     */
    public float getDescent() {
        return descent;
    }

    /**
     * @param descent the descent to set
     */
    public void setDescent(float descent) {
        this.descent = descent;
    }

//    /**
//     * @return the underlineThickness
//     */
//    public float getUnderlineThickness() {
//        return underlineThickness;
//    }
//
//    /**
//     * @param underlineThickness the underlineThickness to set
//     */
//    public void setUnderlineThickness(float underlineThickness) {
//        this.underlineThickness = underlineThickness;
//    }
//
//    /**
//     * @return the underlinePosition
//     */
//    public float getUnderlinePosition() {
//        return underlinePosition;
//    }
//
//    /**
//     * @param underlinePosition the underlinePosition to set
//     */
//    public void setUnderlinePosition(float underlinePosition) {
//        this.underlinePosition = underlinePosition;
//    }
    
}
