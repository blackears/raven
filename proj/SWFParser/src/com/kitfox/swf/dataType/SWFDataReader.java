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

package com.kitfox.swf.dataType;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author kitfox
 */
public class SWFDataReader extends FilterInputStream
{
    //Store a byte worth of bits for bit reading mode
    int bitCache;
    //Current bit pointer.  -1 if not in bit reading mode
    int bitPtr = -1;

    private int bytesRead = 8;  //8 byte magic number already read

    byte[] buf = new byte[8];

    private final int version;

    public SWFDataReader(InputStream is, int version)
    {
        super(is);
        this.version = version;
    }

//    public void endBitReadingMode()
//    {
//        bitPtr = -1;
//        bitCache = 0;
//    }

    public void flushToByteBoundary()
    {
        bitPtr = -1;
        bitCache = 0;
    }

    private long readBits(int bits) throws IOException
    {
        long val = 0;
        for (int i = 0; i < bits; ++i)
        {
            if (bitPtr == -1)
            {
                bitCache = read();
                bitPtr = 7;
            }

            val <<= 1;
            val |= (bitCache & (1 << bitPtr)) == 0 ? 0 : 1;
            --bitPtr;
        }

        return val;
    }

    public FIXED getFB(int bits) throws IOException
    {
        if (bits <= 16)
        {
            long decimal = getUB(bits);
            return new FIXED(0, (int)decimal);
        }

        long decimal = getUB(16);
        long whole = getSB(bits - 16);
        return new FIXED((int)whole, (int)decimal);
    }

    public long getSB(int bits) throws IOException
    {
        long val = readBits(bits);
        long sgnMask = 1 << (bits - 1);
        return (sgnMask & val) == 0 ? val : ~(sgnMask - 1) | val;
    }

    public long getUB(int bits) throws IOException
    {
        return readBits(bits);
    }

    public RECT getRECT() throws IOException
    {
        int bits = (int)getUB(5);

        int Xmin = (int)getSB(bits);
        int Xmax = (int)getSB(bits);
        int Ymin = (int)getSB(bits);
        int Ymax = (int)getSB(bits);
        flushToByteBoundary();

        return new RECT(Xmin, Xmax, Ymin, Ymax);
    }

    public String getString() throws IOException
    {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        for (int val = getUI8(); val != 0; val = getUI8())
        {
            arr.write(val);
        }
        return arr.toString("UTF-8");
    }

    public LANGCODE getLANGCODE() throws IOException
    {
        int code = getUI8();
        return LANGCODE.values()[code];
    }

    public RGB getRGB() throws IOException
    {
        return new RGB(getUI8(), getUI8(), getUI8());
    }

    public RGBA getRGBA() throws IOException
    {
        return new RGBA(getUI8(), getUI8(), getUI8(), getUI8());
    }

    public ARGB getARGB() throws IOException
    {
        return new ARGB(getUI8(), getUI8(), getUI8(), getUI8());
    }

    public MATRIX getMATRIX() throws IOException
    {
        FIXED scaleX = null;
        FIXED scaleY = null;
        FIXED rotSkew0 = null;
        FIXED rotSkew1 = null;

        if (getUB(1) != 0)
        {
            int bits = (int)getUB(5);
            scaleX = getFB(bits);
            scaleY = getFB(bits);
        }

        if (getUB(1) != 0)
        {
            int bits = (int)getUB(5);
            rotSkew0 = getFB(bits);
            rotSkew1 = getFB(bits);
        }
        
        int bits = (int)getUB(5);
        int xlateX = (int)getSB(bits);
        int xlateY = (int)getSB(bits);

        flushToByteBoundary();
        return new MATRIX(xlateX, xlateY, rotSkew0, rotSkew1, scaleX, scaleY);
    }

    public CXFORM getCXFORM() throws IOException
    {
        boolean hasAdd = getUB(1) != 0;
        boolean hasMul = getUB(1) != 0;

        int bits = (int)getUB(4);
        int addR = 0;
        int addG = 0;
        int addB = 0;
        int mulR = 1;
        int mulG = 1;
        int mulB = 1;

        if (hasMul)
        {
            mulR = (int)getSB(bits);
            mulG = (int)getSB(bits);
            mulB = (int)getSB(bits);
        }

        if (hasAdd)
        {
            addR = (int)getSB(bits);
            addG = (int)getSB(bits);
            addB = (int)getSB(bits);
        }

        flushToByteBoundary();
        return new CXFORM(mulR, mulG, mulB, addR, addG, addB);
    }

    public CXFORMWITHALPHA getCXFORMWITHALPHA() throws IOException
    {
        boolean hasAdd = getUB(1) != 0;
        boolean hasMul = getUB(1) != 0;

        int bits = (int)getUB(4);
        int addR = 0;
        int addG = 0;
        int addB = 0;
        int addA = 0;
        int mulR = 1;
        int mulG = 1;
        int mulB = 1;
        int mulA = 1;

        if (hasMul)
        {
            mulR = (int)getSB(bits);
            mulG = (int)getSB(bits);
            mulB = (int)getSB(bits);
            mulA = (int)getSB(bits);
        }

        if (hasAdd)
        {
            addR = (int)getSB(bits);
            addG = (int)getSB(bits);
            addB = (int)getSB(bits);
            addA = (int)getSB(bits);
        }

        flushToByteBoundary();
        return new CXFORMWITHALPHA(mulR, mulG, mulB, mulA, addR, addG, addB, addA);
    }


    public double getDOUBLE() throws IOException
    {
        long bits = getUI64();
        return Double.longBitsToDouble(bits);
    }

    public long getEncodedU32() throws IOException
    {
        long value = getUI8();
        if ((value & 0x80) == 0)
        {
            return value;
        }

        value |= getUI8() << 7;
        if ((value & 0x4000) == 0)
        {
            return value;
        }

        value |= getUI8() << 14;
        if ((value & 0x200000) == 0)
        {
            return value;
        }

        value |= getUI8() << 21;
        if ((value & 0x10000000) == 0)
        {
            return value;
        }

        value |= getUI8() << 28;
        return value;
    }

    public FIXED getFIXED() throws IOException
    {
        int dec = getUI16();
        int who = getSI16();
        return new FIXED(who, dec);
    }

    public FIXED8 getFIXED8() throws IOException
    {
        short dec = (short)getUI8();
        short who = getSI8();
        return new FIXED8(who, dec);
    }

    public float getFLOAT() throws IOException
    {
        int bits = getSI32();
        return Float.intBitsToFloat(bits);
    }

    /**
     * http://www.psc.edu/general/software/packages/ieee/ieee.php
     * @return Next 16 bit floating point value
     * @throws IOException
     */
    public float getFLOAT16() throws IOException
    {
        int val = getUI16();
        int exp = (val >> 10) & 0x1f;
        int man = val & 0x3ff;
        int sgn = val & 0x8000;

        //Convert to float32
        if (exp == 0x1f)
        {
            exp = 0xff;
        }
        man = man << 13;
        if (sgn != 0)
        {
            sgn = 0x80000000;
        }
        int bits = sgn | (exp << 23) | man;
        return Float.intBitsToFloat(bits);
//        if (exp == 0x1f)
//        {
//            if (man == 0)
//            {
//                return Float.POSITIVE_INFINITY;
//            }
//            if (man == 1)
//            {
//                return Float.NEGATIVE_INFINITY;
//            }
//            return Float.NaN;
//        }
//
//        if (exp == 0)
//        {
//            if (man == 0)
//            {
//                return sgn == 0 ? 0 : -0;
//            }
//            float v = (1f / (1 << 30)) * man;
//            return sgn == 0 ? v : -v;
//        }
//
//        float v = (1f / (1 << (exp - 31))) * (1 + man);
//        return (short)((buf[1] << 8) | buf[0]);
    }

    public byte getSI8() throws IOException
    {
        return (byte)read();
    }

    public short getSI16() throws IOException
    {
        read(buf, 0, 2);
        return (short)(
                ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff)
                );
    }

    public int getSI32() throws IOException
    {
        read(buf, 0, 4);
        return ((buf[3] & 0xff) << 24)
                | ((buf[2] & 0xff) << 16)
                | ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff);
    }

    public int getUI8() throws IOException
    {
        return read() & 0xff;
    }

    public int getUI16() throws IOException
    {
        read(buf, 0, 2);
        return ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff);
    }

    public int getUI24() throws IOException
    {
        read(buf, 0, 3);
        return ((buf[2] & 0xff) << 16)
                | ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff);
    }

    public long getUI32() throws IOException
    {
        read(buf, 0, 4);
        return ((buf[3] & 0xffL) << 24)
                | ((buf[2] & 0xff) << 16)
                | ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff);
    }

    public long getUI64() throws IOException
    {
        read(buf, 0, 8);
        return ((buf[7] & 0xffL) << 56)
                | ((buf[6] & 0xffL) << 48)
                | ((buf[5] & 0xffL) << 40)
                | ((buf[4] & 0xffL) << 32)
                | ((buf[3] & 0xffL) << 24)
                | ((buf[2] & 0xff) << 16)
                | ((buf[1] & 0xff) << 8)
                | (buf[0] & 0xff);
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    @Override
    public int read() throws IOException
    {
        ++bytesRead;
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        bytesRead += b.length;
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        bytesRead += len;
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException
    {
        bytesRead += n;
        return super.skip(n);
    }

    /**
     * @return the bytesRead
     */
    public int getBytesRead()
    {
        return bytesRead;
    }


}
