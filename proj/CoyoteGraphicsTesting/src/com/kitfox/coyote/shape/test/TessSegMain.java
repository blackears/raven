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

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.tessellator2.TessSeg;

/**
 *
 * @author kitfox
 */
public class TessSegMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
//        TessSeg s0 = new TessSeg(new Coord(15202, 13449), 
//                new Coord(14167, 19971));
//        TessSeg s1 = new TessSeg(new Coord(13000, 11905), 
//                new Coord(13000, 11300));
//        System.err.println("Hit " + s0.isLineHit(s1) + " " + s0 + " " + s1);
        
        TessSeg s0 = new TessSeg(new Coord(22896, 19566), 
                new Coord(22894, 19565));
        TessSeg s1 = new TessSeg(new Coord(22892, 19564), 
                new Coord(22890, 19563));
        System.err.println("Hit " + s0.isLineHit(s1) + " " + s0 + " " + s1);
        s0.splitAtIsect(s1);
    }
}
