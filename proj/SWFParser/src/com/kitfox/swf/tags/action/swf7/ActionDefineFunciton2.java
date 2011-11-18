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

package com.kitfox.swf.tags.action.swf7;

import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.action.ActionRecord;
import com.kitfox.swf.tags.action.ActionRecordLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ActionDefineFunciton2 extends ActionRecord
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
            int numParams = in.getUI16();
            int regCount = in.getUI8();

            boolean preloadParent = in.getUB(1) != 0;
            boolean preloadRoot = in.getUB(1) != 0;
            boolean supressSuper = in.getUB(1) != 0;
            boolean preloadSuper = in.getUB(1) != 0;
            boolean suppressArgs = in.getUB(1) != 0;
            boolean preloadArgs = in.getUB(1) != 0;
            boolean suppressThis = in.getUB(1) != 0;
            boolean preloadThis = in.getUB(1) != 0;
            in.getUB(7);
            boolean preloadGlobal = in.getUB(1) != 0;
            in.flushToByteBoundary();

            RegParam[] params = new RegParam[numParams];
            for (int i = 0; i < numParams; ++i)
            {
                params[i] = new RegParam(in);
            }

            int codeSize = in.getUI16();
            return new ActionDefineFunciton2(functionName, regCount, preloadParent, preloadRoot, supressSuper, preloadSuper, suppressArgs, preloadArgs, suppressThis, preloadThis, preloadGlobal, params, codeSize);
        }
    }

    public static class RegParam
    {
        int register;
        String paramName;

        public RegParam(SWFDataReader in) throws IOException
        {
            register = in.getUI8();
            paramName = in.getString();
        }

    }

    public static final int CODE = 0x8E;

    String functionName;
    int regCount;

    boolean preloadParent;
    boolean preloadRoot;
    boolean supressSuper;
    boolean preloadSuper;
    boolean suppressArgs;
    boolean preloadArgs;
    boolean suppressThis;
    boolean preloadThis;
    boolean preloadGlobal;

    RegParam[] params;

    int codeSize;

    public ActionDefineFunciton2(String functionName, int regCount, boolean preloadParent, boolean preloadRoot, boolean supressSuper, boolean preloadSuper, boolean suppressArgs, boolean preloadArgs, boolean suppressThis, boolean preloadThis, boolean preloadGlobal, RegParam[] params, int codeSize) {
        this.functionName = functionName;
        this.regCount = regCount;
        this.preloadParent = preloadParent;
        this.preloadRoot = preloadRoot;
        this.supressSuper = supressSuper;
        this.preloadSuper = preloadSuper;
        this.suppressArgs = suppressArgs;
        this.preloadArgs = preloadArgs;
        this.suppressThis = suppressThis;
        this.preloadThis = preloadThis;
        this.preloadGlobal = preloadGlobal;
        this.params = params;
        this.codeSize = codeSize;
    }
}
