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

package com.kitfox.swf.tags.action.swf5;

import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.action.ActionRecord;
import com.kitfox.swf.tags.action.ActionRecordLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ActionDefineFunction extends ActionRecord
{
    public static class Loader extends ActionRecordLoader
    {
        public Loader()
        {
            super(CODE);
        }

        @Override
        public ActionRecord read(SWFDataReader in, int length) throws IOException
        {
            String functionName = in.getString();

            int count = in.getUI16();
            String[] params = new String[count];
            for (int i = 0; i < count; ++i)
            {
                params[i] = in.getString();
            }

            int codeSize = in.getUI16();
            return new ActionDefineFunction(functionName, params, codeSize);
        }
    }

    public static final int CODE = 0x9B;

    String functionName;
    String[] params;
    int codeSize;

    public ActionDefineFunction(String functionName, String[] params, int codeSize) {
        this.functionName = functionName;
        this.params = params;
        this.codeSize = codeSize;
    }
}
