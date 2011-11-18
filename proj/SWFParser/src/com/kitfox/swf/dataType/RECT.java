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

import static com.kitfox.swf.SWFMath.*;

/**
 *
 * @author kitfox
 */
public class RECT
{
    private final int Xmin;
    private final int Xmax;
    private final int Ymin;
    private final int Ymax;

    public RECT(int Xmin, int Xmax, int Ymin, int Ymax)
    {
        this.Xmin = Xmin;
        this.Xmax = Xmax;
        this.Ymin = Ymin;
        this.Ymax = Ymax;
    }

    /**
     * @return the Xmin
     */
    public int getXmin() {
        return Xmin;
    }

    /**
     * @return the Xmax
     */
    public int getXmax() {
        return Xmax;
    }

    /**
     * @return the Ymin
     */
    public int getYmin() {
        return Ymin;
    }

    /**
     * @return the Ymax
     */
    public int getYmax() {
        return Ymax;
    }

    @Override
    public String toString()
    {
        return String.format("RECT(%d %d %d %d) pixXYWH(%f %f %f %f)",
                Xmin, Xmax, Ymin, Ymax,
                toPix(Xmin), toPix(Ymin), toPix(Xmax - Xmin), toPix(Ymax - Ymin));
    }


}
