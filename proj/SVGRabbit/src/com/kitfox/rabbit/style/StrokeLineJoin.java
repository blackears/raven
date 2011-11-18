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

package com.kitfox.rabbit.style;

import java.awt.BasicStroke;

/**
 *
 * @author kitfox
 */
public enum StrokeLineJoin
{
    MITER(BasicStroke.JOIN_MITER),
    ROUND(BasicStroke.JOIN_ROUND),
    BEVEL(BasicStroke.JOIN_BEVEL);

    private final int strokeJoin;

    private StrokeLineJoin(int strokeJoin)
    {
        this.strokeJoin = strokeJoin;
    }

    /**
     * @return the strokeJoin
     */
    public int getStrokeJoin() {
        return strokeJoin;
    }


}
