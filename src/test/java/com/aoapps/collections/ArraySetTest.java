/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-collections.
 *
 * ao-collections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-collections is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-collections.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.collections;

import com.aoapps.lang.io.IoUtils;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ArraySetTest extends TestCase {

  private static final int NUM_TESTS = 1;

  public ArraySetTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(ArraySetTest.class);
  }

  /**
   * A fast pseudo-random number generator for non-cryptographic purposes.
   */
  private static final Random fastRandom = new Random(IoUtils.bufferToLong(new SecureRandom().generateSeed(Long.BYTES)));

  private void doTestPerformance() {
    final int endTestSize = 1000000;
    Set<Integer> randomValues = AoCollections.newHashSet(endTestSize);
    for (int testSize = 1; testSize <= endTestSize; testSize *= 10) {
      // Generate testSize random ints
      while (randomValues.size() < testSize) {
        randomValues.add(fastRandom.nextInt());
      }
      List<Integer> randomList = new ArrayList<>(randomValues);
      // Time new
      long startNanos = System.nanoTime();
      HashSet<Integer> hashSet = new HashSet<>(randomList);
      long timeNanos = System.nanoTime() - startNanos;
      System.out.println(testSize + ": Created HashSet in " + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");
      startNanos = System.nanoTime();
      ArrayList<Integer> list = new ArrayList<>(randomList);
      java.util.Collections.sort(list, HashCodeComparator.getInstance());
      ArraySet<Integer> arraySet = new ArraySet<>(list);
      timeNanos = System.nanoTime() - startNanos;
      System.out.println(testSize + ": Created ArraySet in " + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");
      // Test contains
      startNanos = System.nanoTime();
      for (Integer value : randomList) {
        if (!hashSet.contains(value)) {
          throw new AssertionError();
        }
      }
      timeNanos = System.nanoTime() - startNanos;
      System.out.println(testSize + ": HashSet contains in " + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");
      startNanos = System.nanoTime();
      for (Integer value : randomList) {
        if (!arraySet.contains(value)) {
          throw new AssertionError();
        }
      }
      timeNanos = System.nanoTime() - startNanos;
      System.out.println(testSize + ": ArraySet contains in " + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");

    }
  }

  public void testPerformance() {
    for (int c = 0; c < NUM_TESTS; c++) {
      doTestPerformance();
    }
  }

  /**
   * Used to find {@link ArraySet#BINARY_SEARCH_THRESHOLD}.
   */
  private void doTestBinarySearchThreshold() {
    final int numSearches = 10000;
    final int searchPasses = 100;
    Integer[] searches = new Integer[numSearches];
    for (int size = 0; size <= 32; size++) {
      ArrayList<Integer> values = new ArrayList<>(size);
      int range = 0;
      for (int i = 0; i < size; i++) {
        values.add(range);
        range += 1 + fastRandom.nextInt(10);
      }
      ArraySet<Integer> set = new ArraySet<>(values);
      for (int i = 0; i < numSearches; i++) {
        searches[i] = range == 0 ? 0 : fastRandom.nextInt(range);
      }
      long startNanos = System.nanoTime();
      for (int pass = 0; pass < searchPasses; pass++) {
        for (int i = 0; i < numSearches; i++) {
          set.contains(searches[i]);
        }
      }
      long timeNanos = System.nanoTime() - startNanos;
      System.out.println(size + ":" + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");
    }
  }

  public void testBinarySearchThreshold() {
    for (int c = 0; c < NUM_TESTS; c++) {
      doTestBinarySearchThreshold();
    }
  }
}
