/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.murmur3tests;

import org.junit.Assert;
import org.junit.Test;

/**
 * Abstract test class that provides tests to indicate if a Murmur3 128x64 hash is
 * correctly implemented.
 * <p>
 * This code is extracted  from Apache commons-codec tests for version 1.14
 * </p>
 *
 *
 */
public abstract class H128x64Test {

    /**
     * 256 integers in the range [0,255] arranged in random order.
     * This ensure all bytes are covered in a full hash of the bytes.
     *
     * <p>To create test data using the python library mmh3 (which invokes the c++ code):</p>
     *
     * <pre>
     * import mmh3
     * import numpy as np
     * # Put random bytes in here:
     * bytes = np.uint8([46,246,249,184,247,84,99,144,62,77,195,220,92,20,150,159,38,40,124,252,185,28,63,13,213,172,85,198,118,74,109,157,132,216,76,177,173,23,140,86,146,95,54,176,114,179,234,174,183,141,122,12,60,116,200,142,6,167,59,240,33,29,165,111,243,30,219,110,255,53,32,35,64,225,96,152,70,41,133,80,244,127,57,199,5,164,151,49,26,180,203,83,108,39,126,208,42,206,178,19,69,223,71,231,250,125,211,232,189,55,44,82,48,221,43,192,241,103,155,27,51,163,21,169,91,94,217,191,78,72,93,102,104,105,8,113,100,143,89,245,227,120,160,251,153,145,45,218,168,233,229,253,67,22,182,98,137,128,135,11,214,66,73,171,188,170,131,207,79,106,24,75,237,194,7,129,215,81,248,242,16,25,136,147,156,97,52,10,181,17,205,58,101,68,230,1,37,0,222,88,130,148,224,47,50,197,34,212,196,209,14,36,139,228,154,31,175,202,236,161,3,162,190,254,134,119,4,61,65,117,186,107,204,9,187,201,90,149,226,56,239,238,235,112,87,18,121,115,138,123,210,2,193,166,158,15])
     * # Seed as appropriate. The default seed in mmh3 is zero.
     * seed = 789
     * # Output data. Adjust as appropriate.
     * for x in range(0, 256):
     *   # 32-bit hash
     *   mmh3.hash(bytes[:x])
     *   mmh3.hash(bytes[:x], seed)
     *   # 128-bit hash as two 64 bit signed ints
     *   mmh3.hash64(bytes[:x])
     *   mmh3.hash64(bytes[:x], seed)
     * # Sub-arrays: lower inclusive, upper exclusive:
     * mmh3.hash(bytes[13:15])
     * </pre>
     */
    private static final int[] RANDOM_INTS = {
        46,246,249,184,247,84,99,144,62,77,195,220,92,20,150,159,38,40,124,252,185,28,63,13,213,172,85,198,118,74,109,157,132,216,76,177,173,23,140,86,146,95,54,176,114,179,234,174,183,141,122,12,60,116,200,142,6,167,59,240,33,29,165,111,243,30,219,110,255,53,32,35,64,225,96,152,70,41,133,80,244,127,57,199,5,164,151,49,26,180,203,83,108,39,126,208,42,206,178,19,69,223,71,231,250,125,211,232,189,55,44,82,48,221,43,192,241,103,155,27,51,163,21,169,91,94,217,191,78,72,93,102,104,105,8,113,100,143,89,245,227,120,160,251,153,145,45,218,168,233,229,253,67,22,182,98,137,128,135,11,214,66,73,171,188,170,131,207,79,106,24,75,237,194,7,129,215,81,248,242,16,25,136,147,156,97,52,10,181,17,205,58,101,68,230,1,37,0,222,88,130,148,224,47,50,197,34,212,196,209,14,36,139,228,154,31,175,202,236,161,3,162,190,254,134,119,4,61,65,117,186,107,204,9,187,201,90,149,226,56,239,238,235,112,87,18,121,115,138,123,210,2,193,166,158,15
    };

    /**
     * 256 bytes in the range [0,255] arranged in random order.
     * This ensure all bytes are covered in a full hash of the bytes.
     */
    private static final byte[] RANDOM_BYTES;

    static {
        RANDOM_BYTES = new byte[RANDOM_INTS.length];
        for (int i=0; i<RANDOM_BYTES.length; i++) {
            RANDOM_BYTES[i] = (byte)RANDOM_INTS[i];
        }
    }

    /**
     * This method should be implemented to call the hash under test.
     * @param data the byte array of data.
     * @param offset the offset into the array to start processing.
     * @param length the length of the data from the offset.
     * @param seed the seed to start with.
     * @return the 128bit byte buffer as 2 longs.
     */
    protected abstract long[] getHash( byte[] data, int offset, int length, int seed );

    /**
     * Test the {@link MurmurHash3#hash128x64(byte[], int, int, int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash128x64_positiveSeed() {
        // Seed can be positive
        final int seed = 42;
        final int offset = 13;

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash64(bytes[13:x+13], 42), ',')
        final long[][] answers = {{-1140915396076141277L, -3386313222241793095L},
            {2745805417334040752L, -3045882272665292331L}, {6807939080212835946L, -1975749467247671127L},
            {-7924884987449335214L, -4468571497642087939L}, {3005389733967167773L, -5809440073240597398L},
            {8032745196600164727L, 4545709434702374224L}, {2095398623732573832L, 1778447136435513908L},
            {4492723708121417255L, -7411125500882394867L}, {8467397417110552178L, -1503802302645548949L},
            {4189760269121918355L, -8004336343217265057L}, {4939298084211301953L, -8419135013628844658L},
            {5497136916151148085L, -394028342910298191L}, {3405983294878231737L, -3216533807498089078L},
            {5833223403351466775L, -1792451370239813325L}, {7730583391236194819L, 5356157313842354092L},
            {3111977482488580945L, -3119414725698132191L}, {3314524606404365027L, -1923219843083192742L},
            {7299569240140613949L, 4176392429810027494L}, {6398084683727166117L, 7703960505857395788L},
            {-8594572031068184774L, 4394224719145783692L}, {-7589785442804461713L, 4110439243215224554L},
            {-5343610105946840628L, -4423992782020122809L}, {-522490326525787270L, 289136460641968781L},
            {-5320637070354802556L, -7845553044730489027L}, {1344456408744313334L, 3803048032054968586L},
            {1131205296221907191L, -6256656049039287019L}, {8583339267101027117L, 8934225022848628726L},
            {-6379552869905441749L, 8973517768420051734L}, {5076646564516328801L, 8561479196844000567L},
            {-4610341636137642517L, -6694266039505142069L}, {-758896383254029789L, 4050360662271552727L},
            {-6123628195475753507L, 4283875822581966645L},};
        for (int i = 0; i < answers.length; i++) {
            Assert.assertArrayEquals("Length: " + i, answers[i], getHash(RANDOM_BYTES, offset, i, seed));
        }
    }
    /**
     * Test the {@link MurmurHash3#hash128x64(byte[], int, int, int)} algorithm.
     *
     * <p>Reference data is taken from the Python library {@code mmh3}.</p>
     * <p>
     * The most common error in implementations is that the seed is not masked when it is
     * converted to a long thus creating sign-extension errors.
     * </p><p>
     * Proper masking would look something like:
     * <pre>
     * long h1 = seed & 0xffffffffL
     * long h2 = seed & 0xffffffffL
     * </pre>
     * </p>
     *
     * @see <a href="https://pypi.org/project/mmh3/">mmh3</a>
     */
    @Test
    public void testHash128x64_negativeSeed() {
        // Seed can be negative
        final int seed = -42;
        final int offset = 13;

        // Test with all sizes up to 31 bytes. This ensures a full round of 16-bytes plus up to
        // 15 bytes remaining.
        // for x in range(0, 32):
        //   print(mmh3.hash64(bytes[13:x+13], -42), ',')
        final long[][] answers2 = {{7182599573337898253L, -6490979146667806054L},
            {-461284136738605467L, 7073284964362976233L}, {-3090354666589400212L, 2978755180788824810L},
            {5052807367580803906L, -4497188744879598335L}, {5003711854877353474L, -6616808651483337088L},
            {2043501804923817748L, -760668448196918637L}, {6813003268375229932L, -1818545210475363684L},
            {4488070015393027916L, 8520186429078977003L}, {4709278711722456062L, -2262080641289046033L},
            {-5944514262756048380L, 5968714500873552518L}, {-2304397529301122510L, 6451500469518446488L},
            {-1054078041081348909L, -915114408909600132L}, {1300471646869277217L, -399493387666437046L},
            {-2821780479886030222L, -9061571187511294733L}, {8005764841242557505L, 4135287855434326053L},
            {318307346637037498L, -5355856739901286522L}, {3380719536119187025L, 1890890833937151467L},
            {2691044185935730001L, 7963546423617895734L}, {-5277462388534000227L, 3613853764390780573L},
            {8504421722476165699L, 2058020162708103700L}, {-6578421288092422241L, 3311200163790829579L},
            {-5915037218487974215L, -7385137075895184179L}, {659642911937398022L, 854071824595671049L},
            {-7007237968866727198L, 1372258010932080058L}, {622891376282772539L, -4140783491297489868L},
            {8357110718969014985L, -4737117827581590306L}, {2208857857926305405L, -8360240839768465042L},
            {858120048221036376L, -5822288789703639119L}, {-1988334009458340679L, 1262479472434068698L},
            {-8580307083590783934L, 3634449965473715778L}, {6705664584730187559L, 5192304951463791556L},
            {-6426410954037604142L, -1579122709247558101L},};
        for (int i = 0; i < answers2.length; i++) {
            Assert.assertArrayEquals("[Possible seed masking error]Length: " + i, answers2[i], getHash(RANDOM_BYTES, offset, i, seed));
        }
    }

}
