/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

/**
 * Provides access to an associated list of int's and long's.
 *
 * @author  AO Industries, Inc.
 */
public class IntsAndLongs {

  private IntList ints;
  private LongList longs;

  public IntsAndLongs(IntList ints, LongList longs) {
    if (ints.size() != longs.size()) {
      throw new AssertionError("ints.size() != longs.size()");
    }
    this.ints = ints;
    this.longs = longs;
  }

  public int size() {
    return ints.size();
  }

  public int getInt(int index) {
    return ints.getInt(index);
  }

  public long getLong(int index) {
    return longs.getLong(index);
  }

  public boolean contains(int value) {
    return ints.contains(value);
  }

  public int indexOf(int value) {
    return ints.indexOf(value);
  }
}
