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
public class ActionPush<T> extends ActionRecord
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
            int typeIdx = in.getUI8();
            Type type = Type.values()[typeIdx];
            switch (type)
            {
                case BOOLEAN:
                    return new ActionPush(type, in.getUI8() != 0);
                case CONST16:
                    return new ActionPush(type, in.getUI16());
                case CONST8:
                    return new ActionPush(type, in.getUI16());
                case DOUBLE:
                    return new ActionPush(type, in.getDOUBLE());
                case FLOAT:
                    return new ActionPush(type, in.getFLOAT());
                case INTEGER:
                    return new ActionPush(type, in.getUI32());
                case NULL:
                    return new ActionPush(type, null);
                case REGISTER:
                    return new ActionPush(type, in.getUI8());
                case STRING:
                    return new ActionPush(type, in.getString());
                case UNDEFINED:
                    return new ActionPush(type, null);
            }
            throw new RuntimeException("Unknown type");
        }
    }

    public static final int CODE = 0x96;

    public static enum Type {
        STRING, 
        FLOAT, 
        NULL, 
        UNDEFINED, 
        REGISTER, 
        BOOLEAN, 
        DOUBLE, 
        INTEGER, 
        CONST8, 
        CONST16
    }

    Type type;
    T value;

    public ActionPush(Type type, T value) {
        this.type = type;
        this.value = value;
    }

}
