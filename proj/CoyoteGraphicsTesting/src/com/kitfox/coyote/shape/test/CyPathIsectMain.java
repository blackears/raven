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

package com.kitfox.coyote.shape.test;

import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;

/**
 *
 * @author kitfox
 */
public class CyPathIsectMain
{

    public CyPathIsectMain()
    {
        testContains();
    }
    
    private void testContains()
    {
        CyPath2d path = new CyPath2d();
        
        path.moveTo(0, 0);
        path.lineTo(50, 50);
        path.lineTo(60, -50);
        path.close();

        {
            CyRectangle2d region = new CyRectangle2d(20, 10, 1, 1);
            System.err.println(path.contains(region));
        }

        {
            CyRectangle2d region = new CyRectangle2d(-10, 10, 1, 1);
            System.err.println(path.contains(region));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new CyPathIsectMain();
    }
}
