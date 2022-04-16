/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2021, 2022  AO Industries, Inc.
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
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class UnmodifiableArraySetTest extends TestCase {

	private static final int NUM_TESTS = 1;

	public UnmodifiableArraySetTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(UnmodifiableArraySetTest.class);
	}

	/**
	 * A fast pseudo-random number generator for non-cryptographic purposes.
	 */
	private static final Random fastRandom = new Random(IoUtils.bufferToLong(new SecureRandom().generateSeed(Long.BYTES)));

	/**
	 * Used to find {@link UnmodifiableArraySet#BINARY_SEARCH_THRESHOLD}
	 */
	private void doTestBinarySearchThreshold() {
		final int numSearches = 10000;
		final int searchPasses = 100;
		Integer[] searches = new Integer[numSearches];
		for(int size = 0; size <= 32; size++) {
			ArrayList<Integer> values = new ArrayList<>(size);
			int range = 0;
			for(int i = 0; i < size; i++) {
				values.add(range);
				range += 1 + fastRandom.nextInt(10);
			}
			UnmodifiableArraySet<Integer> set = new UnmodifiableArraySet<>(values);
			for(int i = 0; i < numSearches; i++) {
				searches[i] = range == 0 ? 0 : fastRandom.nextInt(range);
			}
			long startNanos = System.nanoTime();
			for(int pass = 0; pass < searchPasses; pass++) {
				for(int i = 0; i < numSearches; i++) {
					set.contains(searches[i]);
				}
			}
			long timeNanos = System.nanoTime() - startNanos;
			System.out.println(size + ":" + BigDecimal.valueOf(timeNanos / 1000, 3) + " ms");
		}
	}

	public void testBinarySearchThreshold() {
		for(int c = 0; c < NUM_TESTS; c++) {
			doTestBinarySearchThreshold();
		}
	}
}
