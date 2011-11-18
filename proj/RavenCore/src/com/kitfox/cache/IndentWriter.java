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

package com.kitfox.cache;

import java.io.PrintWriter;
import java.io.Writer;

/**
 *
 * @author kitfox
 */
public class IndentWriter extends PrintWriter
{
    final String indentString;
    int indent;

    public IndentWriter(Writer writer, String indentString)
    {
        super(writer);
        this.indentString = indentString;
    }

    public void push()
    {
        ++indent;
    }

    public void pop()
    {
        --indent;
    }

    @Override
    public void println()
    {
        super.println();
        for (int i = 0; i < indent; ++i)
        {
            print(indentString);
        }
    }
}
