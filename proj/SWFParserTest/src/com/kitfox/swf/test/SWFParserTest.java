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

package com.kitfox.swf.test;

import com.kitfox.swf.SWFDocument;
import com.kitfox.swf.SWFException;
import com.kitfox.swf.SWFParser;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author kitfox
 */
public class SWFParserTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SWFException
    {
        URL url = SWFParserTest.class.getResource("/faceShape.swf");

//        FileOutputStream fout = new FileOutputStream(new File("res/faceShapeDecomp.swf"));
//        SWFParser.decompress(url, fout);
//        fout.close();

//        SWFParser.parse(url, new SWFTagPrinter());
        SWFDocument doc = SWFParser.parse(url);
    }

}
