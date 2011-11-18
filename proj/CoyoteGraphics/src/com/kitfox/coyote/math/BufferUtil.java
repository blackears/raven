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

package com.kitfox.coyote.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kitfox
 */
public class BufferUtil
{
    public static final int SIZEOF_DOUBLE = 8;
    public static final int SIZEOF_FLOAT = 4;
    public static final int SIZEOF_INT = 4;
    public static final int SIZEOF_SHORT = 2;
    public static final int SIZEOF_BYTE = 1;

    public static DoubleBuffer allocateDouble(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * SIZEOF_DOUBLE);
        bb.order(ByteOrder.nativeOrder());
        return bb.asDoubleBuffer();
    }

    public static FloatBuffer allocateFloat(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        return bb.asFloatBuffer();
    }

    public static IntBuffer allocateInt(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * SIZEOF_INT);
        bb.order(ByteOrder.nativeOrder());
        return bb.asIntBuffer();
    }

    public static ShortBuffer allocateShort(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * SIZEOF_SHORT);
        bb.order(ByteOrder.nativeOrder());
        return bb.asShortBuffer();
    }

    public static ByteBuffer allocateByte(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * SIZEOF_BYTE);
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }
}
