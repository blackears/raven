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

package com.kitfox.rabbit.codegen;

import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.nodes.RaRect;
import com.kitfox.rabbit.nodes.RaSvg;
import com.kitfox.rabbit.style.StyleGradient;
import com.kitfox.rabbit.style.StyleKey;
import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.types.RaLength;

/**
 *
 * @author kitfox
 */
public class ExampleDoc
{
    public static final int DOC_ID = 0;

    RaSvg svg;
    RaRect rect1;

    public ExampleDoc()
    {
        buildSvg();
    }

    public RaElement lookupNode(int index)
    {
        switch (index)
        {
            case 0:
                return svg;
            case 1:
                return rect1;
        }
        return null;
    }

    private void buildRect1()
    {
        rect1 = new RaRect();
        rect1.setX(60);
        rect1.setY(70);
        rect1.setWidth(200);
        rect1.setHeight(100);

        rect1.getStyle().put(StyleKey.FILL, new StyleGradient(new ElementRef(0, 0)));
    }

    private void buildSvg()
    {
        buildRect1();

        svg = new RaSvg();
        svg.setX(new RaLength(0, RaLength.Type.NONE));
        svg.setWidth(new RaLength(100, RaLength.Type.NONE));

        svg.addChild(rect1);
    }
}
