/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2016, 2020, 2021  AO Industries, Inc.
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

import java.util.ArrayList;
import java.util.Collection;
// import org.checkthread.annotations.NotThreadSafe;

/**
 * Automatically extends the size of the list instead of throwing exceptions on set, add, and addAll.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("CloneableImplementsClone")
public class AutoGrowArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 4698056683308968140L;

	public AutoGrowArrayList() {
		super();
	}

	public AutoGrowArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public AutoGrowArrayList(Collection<E> c) {
		super(c);
	}

	// @NotThreadSafe
	@Override
	public E set(int index, E element) {
		int minSize = index+1;
		ensureCapacity(minSize);
		while(size() < minSize) {
			add(null);
		}
		return super.set(index, element);
	}

	// @NotThreadSafe
	@Override
	public void add(int index, E element) {
		ensureCapacity(index+1);
		while(size() < index) {
			add(null);
		}
		super.add(index, element);
	}

	// @NotThreadSafe
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		ensureCapacity(index+c.size());
		while(size() < index) {
			add(null);
		}
		return super.addAll(index, c);
	}
}
