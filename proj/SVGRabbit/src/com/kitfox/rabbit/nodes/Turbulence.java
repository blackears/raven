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

package com.kitfox.rabbit.nodes;

/**
Produces results in the range [1, 2**31 - 2].
Algorithm is: r = (a * r) mod m
where a = 16807 and m = 2**31 - 1 = 2147483647
See [Park & Miller], CACM vol. 31 no. 10 p. 1195, Oct. 1988
To test: the algorithm should produce the result 1043618065
as the 10,000th generated number if the original seed is 1.

 *
 * @author kitfox
 */
public class Turbulence {

    private static final int RAND_m = 2147483647; // 2**31 - 1
    private static final int RAND_a = 16807; // 7**5; primitive root of m
    private static final int RAND_q = 127773; // m / a
    private static final int RAND_r = 2836; // m % a

    long setup_seed(long lSeed) {
        if (lSeed <= 0) {
            lSeed = -(lSeed % (RAND_m - 1)) + 1;
        }
        if (lSeed > RAND_m - 1) {
            lSeed = RAND_m - 1;
        }
        return lSeed;
    }

    long random(long lSeed) {
        long result;
        result = RAND_a * (lSeed % RAND_q) - RAND_r * (lSeed / RAND_q);
        if (result <= 0) {
            result += RAND_m;
        }
        return result;
    }
    private static final int BSize = 0x100;
    private static final int BM = 0xff;
    private static final int PerlinN = 0x1000;
    private static final int NP = 12; // 2^PerlinN
    private static final int NM = 0xfff;
    int[] uLatticeSelector = new int[BSize + BSize + 2];
    double[][][] fGradient = new double[4][BSize + BSize + 2][2];

    class StitchInfo {

        int nWidth; // How much to subtract to wrap for stitching.
        int nHeight;
        int nWrapX; // Minimum value to wrap.
        int nWrapY;
    };


    public Turbulence(long lSeed) {
        init(lSeed);
    }

    void init(long lSeed) {
        double s;
        int i = 0, j, k;
        lSeed = setup_seed(lSeed);
        for (k = 0; k < 4; k++) {
            for (i = 0; i < BSize; i++) {
                uLatticeSelector[i] = i;
                for (j = 0; j < 2; j++) {
                    fGradient[k][i][j] = (double) (((lSeed = random(lSeed)) % (BSize + BSize)) - BSize) / BSize;
                }
                s = (Math.sqrt(fGradient[k][i][0] * fGradient[k][i][0]
                        + fGradient[k][i][1] * fGradient[k][i][1]));
                fGradient[k][i][0] /= s;
                fGradient[k][i][1] /= s;
            }
        }

        while (--i > 0) {
            k = uLatticeSelector[i];
            uLatticeSelector[i] = uLatticeSelector[j = (int) (lSeed = random(lSeed)) % BSize];
            uLatticeSelector[j] = k;
        }

        for (i = 0; i < BSize + 2; i++) {
            uLatticeSelector[BSize + i] = uLatticeSelector[i];
            for (k = 0; k < 4; k++) {
                for (j = 0; j < 2; j++) {
                    fGradient[k][BSize + i][j] = fGradient[k][i][j];
                }
            }
        }
    }

    double s_curve(double t) {
        return t * t * (3. - 2. * t);
    }

    double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    double noise2(int nColorChannel, double[] vec, StitchInfo pStitchInfo) {
        int bx0, bx1, by0, by1, b00, b10, b01, b11;
        double rx0, rx1, ry0, ry1, q[], sx, sy, a, b, t, u, v;
        int i, j;
        t = vec[0] + PerlinN;
        bx0 = (int) t;
        bx1 = bx0 + 1;
        rx0 = t - (int) t;
        rx1 = rx0 - 1.0f;
        t = vec[1] + PerlinN;
        by0 = (int) t;
        by1 = by0 + 1;
        ry0 = t - (int) t;
        ry1 = ry0 - 1.0f;
        // If stitching, adjust lattice points accordingly.
        if (pStitchInfo != null) {
            if (bx0 >= pStitchInfo.nWrapX) {
                bx0 -= pStitchInfo.nWidth;
            }
            if (bx1 >= pStitchInfo.nWrapX) {
                bx1 -= pStitchInfo.nWidth;
            }
            if (by0 >= pStitchInfo.nWrapY) {
                by0 -= pStitchInfo.nHeight;
            }
            if (by1 >= pStitchInfo.nWrapY) {
                by1 -= pStitchInfo.nHeight;
            }
        }
        bx0 &= BM;
        bx1 &= BM;
        by0 &= BM;
        by1 &= BM;
        i = uLatticeSelector[bx0];
        j = uLatticeSelector[bx1];
        b00 = uLatticeSelector[i + by0];
        b10 = uLatticeSelector[j + by0];
        b01 = uLatticeSelector[i + by1];
        b11 = uLatticeSelector[j + by1];
        sx = s_curve(rx0);
        sy = s_curve(ry0);
        q = fGradient[nColorChannel][b00];
        u = rx0 * q[0] + ry0 * q[1];
        q = fGradient[nColorChannel][b10];
        v = rx1 * q[0] + ry0 * q[1];
        a = lerp(sx, u, v);
        q = fGradient[nColorChannel][b01];
        u = rx0 * q[0] + ry1 * q[1];
        q = fGradient[nColorChannel][b11];
        v = rx1 * q[0] + ry1 * q[1];
        b = lerp(sx, u, v);
        return lerp(sy, a, b);
    }

    public double turbulence(int nColorChannel, double[] point, double fBaseFreqX, double fBaseFreqY,
            int nNumOctaves, boolean bFractalSum, boolean bDoStitching,
            double fTileX, double fTileY, double fTileWidth, double fTileHeight) {
        StitchInfo stitch = null;
//  StitchInfo pStitchInfo = null; // Not stitching when NULL.
        // Adjust the base frequencies if necessary for stitching.
        if (bDoStitching) {
            // When stitching tiled turbulence, the frequencies must be adjusted
            // so that the tile borders will be continuous.
            if (fBaseFreqX != 0.0) {
                double fLoFreq = Math.floor(fTileWidth * fBaseFreqX) / fTileWidth;
                double fHiFreq = Math.ceil(fTileWidth * fBaseFreqX) / fTileWidth;
                if (fBaseFreqX / fLoFreq < fHiFreq / fBaseFreqX) {
                    fBaseFreqX = fLoFreq;
                } else {
                    fBaseFreqX = fHiFreq;
                }
            }
            if (fBaseFreqY != 0.0) {
                double fLoFreq = Math.floor(fTileHeight * fBaseFreqY) / fTileHeight;
                double fHiFreq = Math.ceil(fTileHeight * fBaseFreqY) / fTileHeight;
                if (fBaseFreqY / fLoFreq < fHiFreq / fBaseFreqY) {
                    fBaseFreqY = fLoFreq;
                } else {
                    fBaseFreqY = fHiFreq;
                }
            }
            // Set up initial stitch values.
            //pStitchInfo = &stitch;
            stitch = new StitchInfo();
            stitch.nWidth = (int) (fTileWidth * fBaseFreqX + 0.5f);
            stitch.nWrapX = (int) (fTileX * fBaseFreqX + PerlinN + stitch.nWidth);
            stitch.nHeight = (int) (fTileHeight * fBaseFreqY + 0.5f);
            stitch.nWrapY = (int) (fTileY * fBaseFreqY + PerlinN + stitch.nHeight);
        }
        double fSum = 0.0f;
        double[] vec = new double[2];
        vec[0] = point[0] * fBaseFreqX;
        vec[1] = point[1] * fBaseFreqY;
        double ratio = 1;
        for (int nOctave = 0; nOctave < nNumOctaves; nOctave++) {
            if (bFractalSum) {
                fSum += (noise2(nColorChannel, vec, stitch) / ratio);
            } else {
                fSum += (Math.abs(noise2(nColorChannel, vec, stitch)) / ratio);
            }
            vec[0] *= 2;
            vec[1] *= 2;
            ratio *= 2;
            if (stitch != null) {
                // Update stitch values. Subtracting PerlinN before the multiplication and
                // adding it afterward simplifies to subtracting it once.
                stitch.nWidth *= 2;
                stitch.nWrapX = 2 * stitch.nWrapX - PerlinN;
                stitch.nHeight *= 2;
                stitch.nWrapY = 2 * stitch.nWrapY - PerlinN;
            }
        }
        return fSum;
    }
}
