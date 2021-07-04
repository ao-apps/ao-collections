/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2016, 2020, 2021  AO Industries, Inc.
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
 * along with ao-collections.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.collections;

import java.util.List;

/**
 * A List that stores things using <code>long[]</code> instead of <code>Object[]</code>.  null values are not supported.
 *
 * @see  java.util.ArrayList
 */
// TODO: Support LongStream
public interface LongList extends LongCollection, List<Long> {

	int indexOf(long elem);

	int lastIndexOf(long elem);

	long getLong(int index);

	long set(int index, long element);

	void add(int index, long element);

	long removeAtIndex(int index);
}