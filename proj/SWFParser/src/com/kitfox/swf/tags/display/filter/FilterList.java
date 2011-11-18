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

package com.kitfox.swf.tags.display.filter;

import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class FilterList
{
    Filter[] filters;

    public FilterList(SWFDataReader data) throws IOException
    {
        int numFilters = data.getUI8();
        filters = new Filter[numFilters];
        for (int i = 0; i < numFilters; ++i)
        {
            filters[i] = getFilter(data);
        }
    }

    public Filter getFilter(SWFDataReader data) throws IOException
    {
        int filterId = data.getUI8();
        switch (filterId)
        {
            case 0:
                return new DropShadowFilter(data);
            case 1:
                return new BlurFilter(data);
            case 2:
                return new GlowFilter(data);
            case 3:
                return new BevelFilter(data);
            case 4:
                return new GradientGlowFilter(data);
            case 5:
                return new ConvolutionFilter(data);
            case 6:
                return new ColorMatrixFilter(data);
            case 7:
                return new GradientBevelFilter(data);
            default:
                return null;
        }
    }



}
