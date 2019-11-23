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
 * Abstract test class that provides tests to indicate if a Murmur3 32x86 hash is
 * correctly implemented.
 * <p>
 * The most common error in implementations is that trailing bytes in the hash are not
 * masked.
 * </p><p>
 * This code is extracted  from Apache commons-codec tests for version 1.14
 * </p>
 *
 *
 */
public abstract class H32x86Test {


    /**
     * This method should be implemented to call the hash under test.
     * @param data the byte array of data.
     * @param offset the offset into the array to start processing.
     * @param length the length of the data from the offset.
     * @param seed the seed to start with.
     * @return the 128bit byte buffer as 2 longs.
     */
    protected abstract long getHash( byte[] data, int offset, int length, int seed );


    /**
     * Test to demonstrate {@link MurmurHash3#hash32x86(byte[], int, int, int)} is OK
     * if the final 1, 2, or 3 bytes are negative.
     * <p>
     * Proper masking would look something like:
     * <pre>
     *      // tail
     *      final int index = offset + (nblocks << 2);
     *      int k1 = 0;
     *      switch (offset + length - index) {
     *      case 3:
     *          k1 ^= (data[index + 2] & 0xff) << 16;
     *      case 2:
     *          k1 ^= (data[index + 1] & 0xff) << 8;
     *      case 1:
     *          k1 ^= (data[index] & 0xff);
     *
     * </pre>
     * </p>
     */
    @Test
    public void testTrailingNegativeSignedBytes() {
        // Data as above for testing MurmurHash3.hash32(byte[], int, int, int).
        // This test uses assertEquals().
        Assert.assertEquals("[Probable missing mask in tail processing]", -43192051, getHash(new byte[] {-1}, 0, 1, 0));
        Assert.assertEquals("[Probable missing mask in tail processing]", -582037868, getHash(new byte[] {0, -1}, 0, 2, 0));
        Assert.assertEquals("[Probable missing mask in tail processing]", 922088087, getHash(new byte[] {0, 0, -1}, 0, 3, 0));
        Assert.assertEquals("[Probable missing mask in tail processing]", -1309567588, getHash(new byte[] {-1, 0}, 0, 2, 0));
        Assert.assertEquals("[Probable missing mask in tail processing]", -363779670, getHash(new byte[] {-1, 0, 0}, 0, 3, 0));
        Assert.assertEquals("[Probable missing mask in tail processing]", -225068062, getHash(new byte[] {0, -1, 0}, 0, 3, 0));
    }


}
