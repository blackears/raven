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

package com.kitfox.raven.shape.network.keys;

import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintIndex;
import com.kitfox.raven.paint.RavenPaintProvider;
import com.kitfox.raven.shape.network.NetworkDataType;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=NetworkDataType.class)
public class NetworkDataTypePaint extends NetworkDataType<RavenPaint>
{

    public NetworkDataTypePaint()
    {
        super(RavenPaint.class);
    }

    @Override
    public String asText(RavenPaint value)
    {
        if (value == null)
        {
            return "";
        }
        
        RavenPaintProvider prov = 
                RavenPaintIndex.inst().getByPaint(value.getClass());
        if (prov == null)
        {
            return null;
        }
        return prov.asText(value);
    }

    @Override
    public RavenPaint fromText(String text)
    {
        RavenPaintProvider prov = 
                RavenPaintIndex.inst().getProviderSupporting(text);
        return prov.fromText(text);
    }
    
}
