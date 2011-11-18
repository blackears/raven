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

package com.kitfox.swf.tags.action.swf4;

import com.kitfox.swf.dataType.SWFDataReader;
import com.kitfox.swf.tags.action.ActionRecord;
import com.kitfox.swf.tags.action.ActionRecordLoader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ActionGotoFrame2 extends ActionRecord
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
            in.getUB(6);
            boolean sceneBiasFlag = in.getUB(1) != 0;
            boolean play = in.getUB(1) != 0;
            in.flushToByteBoundary();

            int sceneBias = sceneBiasFlag ? in.getUI16() : 0;
            return new ActionGotoFrame2(play, sceneBias);
        }
    }

    public static final int CODE = 0x9F;

    boolean play;
    int sceneBias;

    public ActionGotoFrame2(boolean play, int sceneBias) {
        this.play = play;
        this.sceneBias = sceneBias;
    }

}
