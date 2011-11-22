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

package com.kitfox.coyote.drawRecord;

import com.kitfox.coyote.renderer.CyDrawGroup;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 * Draws objects in order they are submitted.
 *
 * @author kitfox
 */
public class CyDrawGroupZOrder extends CyDrawGroup
{

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        if (filter != null)
        {
            //We should render everything to a fresh buffer and then
            // apply the filter.
            throw new UnsupportedOperationException(
                    "Filters are not implemented yet.");
        }

        CyDrawRecord prev = null;
        for (int i = 0; i < records.size(); ++i)
        {
            CyDrawRecord rec = records.get(i);

            rec.render(ctx, gl, prev);

            prev = rec;
        }
    }

    @Override
    public void dispose()
    {
        for (int i = 0; i < records.size(); ++i)
        {
            CyDrawRecord rec = records.get(i);
            rec.dispose();
        }
    }
}
