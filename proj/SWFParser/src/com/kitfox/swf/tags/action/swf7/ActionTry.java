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
public class ActionTry extends ActionRecord
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
            in.getUB(5);
            boolean catchInRegister = in.getUB(1) != 0;
            boolean finallBlock = in.getUB(1) != 0;
            boolean catchBlock = in.getUB(1) != 0;
            in.flushToByteBoundary();

            int trySize = in.getUI16();
            int catchSize = in.getUI16();
            int finallySize = in.getUI16();

            String catchName = !catchInRegister ? in.getString() : null;
            int catchRegister = catchInRegister ? in.getUI8() : 0;

            readBlock(in, trySize);
            readBlock(in, catchSize);
            readBlock(in, finallySize);
            
            return new ActionTry(catchInRegister, finallBlock, catchBlock, catchName, catchRegister);
        }
    }

    public static final int CODE = 0x8F;

    boolean catchInRegister;
    boolean finallBlock;
    boolean catchBlock;

    String catchName;
    int catchRegister;

    public ActionTry(boolean catchInRegister, boolean finallBlock, boolean catchBlock, String catchName, int catchRegister) {
        this.catchInRegister = catchInRegister;
        this.finallBlock = finallBlock;
        this.catchBlock = catchBlock;
        this.catchName = catchName;
        this.catchRegister = catchRegister;
    }
}
