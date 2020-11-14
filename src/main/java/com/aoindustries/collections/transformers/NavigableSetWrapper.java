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

/**
 * Wraps a {@link NavigableSet}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class NavigableSetWrapper<E,W> extends SortedSetWrapper<E,W> implements NavigableSet<E> {

	/**
	 * Wraps a navigable set.
	 */
	public static <E,W> NavigableSetWrapper<E,W> of(NavigableSet<W> set, Converter<E,W> converter) {
		return (set == null) ? null : new NavigableSetWrapper<>(set, converter);
	}

	/**
	 * @see  #of(java.util.NavigableSet, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> NavigableSetWrapper<E,E> of(NavigableSet<E> set) {
		return of(set, Converter.identity());
	}

	protected NavigableSetWrapper(NavigableSet<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected NavigableSet<W> getWrapped() {
		return (NavigableSet<W>)super.getWrapped();
	}

	@Override
	public E lower(E e) {
		return converter.fromWrapped(getWrapped().lower(converter.toWrapped(e)));
	}

	@Override
	public E floor(E e) {
		return converter.fromWrapped(getWrapped().floor(converter.toWrapped(e)));
	}

	@Override
	public E ceiling(E e) {
		return converter.fromWrapped(getWrapped().ceiling(converter.toWrapped(e)));
	}

	@Override
	public E higher(E e) {
		return converter.fromWrapped(getWrapped().higher(converter.toWrapped(e)));
	}

	@Override
	public E pollFirst() {
		return converter.fromWrapped(getWrapped().pollFirst());
	}

	@Override
	public E pollLast() {
		return converter.fromWrapped(getWrapped().pollLast());
	}

	@Override
	public NavigableSetWrapper<E,W> descendingSet() {
		return of(getWrapped().descendingSet(), converter);
	}

	@Override
	public IteratorWrapper<E,W> descendingIterator() {
		return IteratorWrapper.of(getWrapped().descendingIterator(), converter);
	}

	@Override
	public NavigableSetWrapper<E,W> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return of(
			getWrapped().subSet(
				converter.toWrapped(fromElement),
				fromInclusive,
				converter.toWrapped(toElement),
				toInclusive
			),
			converter
		);
	}

	@Override
	public NavigableSetWrapper<E,W> headSet(E toElement, boolean inclusive) {
		return of(
			getWrapped().headSet(
				converter.toWrapped(toElement),
				inclusive
			),
			converter
		);
	}

	@Override
	public NavigableSetWrapper<E,W> tailSet(E fromElement, boolean inclusive) {
		return of(
			getWrapped().tailSet(
				converter.toWrapped(fromElement),
				inclusive
			),
			converter
		);
	}
}
