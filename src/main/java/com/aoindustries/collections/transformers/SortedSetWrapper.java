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
package com.aoindustries.collections.transformers;

import java.util.NavigableSet;
import java.util.SortedSet;

/**
 * Wraps a {@link SortedSet}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class SortedSetWrapper<E,W> extends SetWrapper<E,W> implements SortedSet<E> {

	/**
	 * Wraps a sorted set.
	 * <ol>
	 * <li>If the given set is a {@link NavigableSet}, then will return a {@link NavigableSetWrapper}.</li>
	 * </ol>
	 *
	 * @see  NavigableSetWrapper#of(java.util.NavigableSet, com.aoindustries.collections.transformers.Converter)
	 */
	public static <E,W> SortedSetWrapper<E,W> of(SortedSet<W> set, Converter<E,W> converter) {
		if(set instanceof NavigableSet) {
			return NavigableSetWrapper.of((NavigableSet<W>)set, converter);
		}
		return (set == null) ? null : new SortedSetWrapper<>(set, converter);
	}

	/**
	 * @see  #of(java.util.SortedSet, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> SortedSetWrapper<E,E> of(SortedSet<E> set) {
		return of(set, Converter.identity());
	}

	protected SortedSetWrapper(SortedSet<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected SortedSet<W> getWrapped() {
		return (SortedSet<W>)super.getWrapped();
	}

	private ComparatorWrapper<E,W> comparator;

	@Override
	public ComparatorWrapper<E,W> comparator() {
		ComparatorWrapper<E,W> c = comparator;
		if(c == null) {
			c = ComparatorWrapper.of(getWrapped().comparator(), converter);
			comparator = c;
		}
		return c;
	}

	@Override
	public SortedSetWrapper<E,W> subSet(E fromElement, E toElement) {
		return of(
			getWrapped().subSet(
				converter.toWrapped(fromElement),
				converter.toWrapped(toElement)
			),
			converter
		);
	}

	@Override
	public SortedSetWrapper<E,W> headSet(E toElement) {
		return of(
			getWrapped().headSet(
				converter.toWrapped(toElement)
			),
			converter
		);
	}

	@Override
	public SortedSetWrapper<E,W> tailSet(E fromElement) {
		return of(
			getWrapped().tailSet(
				converter.toWrapped(fromElement)
			),
			converter
		);
	}

	@Override
	public E first() {
		return converter.fromWrapped(getWrapped().first());
	}

	@Override
	public E last() {
		return converter.fromWrapped(getWrapped().last());
	}

	// TODO: spliterator()?
}
