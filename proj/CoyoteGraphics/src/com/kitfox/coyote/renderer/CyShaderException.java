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

package com.kitfox.coyote.renderer;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author kitfox
 */
public class CyShaderException extends Exception
{
    String path;
    String log;

    public CyShaderException(String path, String log)
    {
        super(buildMsg(path, log));
        this.path = path;
        this.log = log;
    }

    private static String buildMsg(String path, String log)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Error compiling: " + path);
        pw.println(log);
        pw.close();
        return sw.toString();
    }
}
