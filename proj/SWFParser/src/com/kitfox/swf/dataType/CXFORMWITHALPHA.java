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

package com.kitfox.swf.dataType;

/**
 *
 * @author kitfox
 */
public class CXFORMWITHALPHA
{
    private final int mulR;
    private final int mulG;
    private final int mulB;
    private final int mulA;
    private final int addR;
    private final int addG;
    private final int addB;
    private final int addA;

    public CXFORMWITHALPHA(int mulR, int mulG, int mulB, int mulA, 
            int addR, int addG, int addB, int addA)
    {
        this.mulR = mulR;
        this.mulG = mulG;
        this.mulB = mulB;
        this.mulA = mulA;
        this.addR = addR;
        this.addG = addG;
        this.addB = addB;
        this.addA = addA;
    }

    /**
     * @return the mulR
     */
    public int getMulR() {
        return mulR;
    }

    /**
     * @return the mulG
     */
    public int getMulG() {
        return mulG;
    }

    /**
     * @return the mulB
     */
    public int getMulB() {
        return mulB;
    }

    /**
     * @return the mulA
     */
    public int getMulA() {
        return mulA;
    }

    /**
     * @return the addR
     */
    public int getAddR() {
        return addR;
    }

    /**
     * @return the addG
     */
    public int getAddG() {
        return addG;
    }

    /**
     * @return the addB
     */
    public int getAddB() {
        return addB;
    }

    /**
     * @return the addA
     */
    public int getAddA() {
        return addA;
    }


}
