/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

import java.util.Collection;

/**
 * A primitive collection.
 *
 * @see  Collection
 */
// TODO: Support LongStream
public interface LongCollection extends Collection<Long> {

  boolean contains(long elem);

  long[] toArrayLong();

  boolean add(long o);

  boolean removeByValue(long value);
}
