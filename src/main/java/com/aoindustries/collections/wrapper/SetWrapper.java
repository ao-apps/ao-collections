/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2020  AO Industries, Inc.
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
package com.aoindustries.collections.wrapper;

import java.util.Set;
import java.util.SortedSet;

/**
 * Wraps a {@link Set}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("EqualsAndHashcode")
public class SetWrapper<E,W> extends CollectionWrapper<E,W> implements Set<E> {

	/**
	 * Wraps a set.
	 * <ol>
	 * <li>If the given set is a {@link SortedSet}, then will return a {@link SortedSetWrapper}.</li>
	 * </ol>
	 *
	 * @see  SortedSetWrapper#of(java.util.SortedSet, com.aoindustries.collections.wrapper.Converter)
	 */
	public static <E,W> SetWrapper<E,W> of(Set<W> set, Converter<E,W> converter) {
		if(set instanceof SortedSet) {
			return SortedSetWrapper.of((SortedSet<W>)set, converter);
		}
		return (set == null) ? null : new SetWrapper<>(set, converter);
	}

	/**
	 * @see  #of(java.util.Set, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> SetWrapper<E,E> of(Set<E> set) {
		return of(set, Converter.identity());
	}

	protected SetWrapper(Set<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected Set<W> getWrapped() {
		return (Set<W>)super.getWrapped();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return getWrapped().equals(
			(o instanceof Set)
				? of((Set<Object>)o, converter.invert().unbounded())
				: o
		);
	}

	// TODO: spliterator()?
}
