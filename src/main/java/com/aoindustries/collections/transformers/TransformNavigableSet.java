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
public class TransformNavigableSet<E,W> extends TransformSortedSet<E,W> implements NavigableSet<E> {

	/**
	 * Wraps a navigable set.
	 */
	public static <E,W> TransformNavigableSet<E,W> of(NavigableSet<W> set, Transformer<E,W> transformer) {
		return (set == null) ? null : new TransformNavigableSet<>(set, transformer);
	}

	/**
	 * @see  #of(java.util.NavigableSet, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformNavigableSet<E,E> of(NavigableSet<E> set) {
		return of(set, Transformer.identity());
	}

	protected TransformNavigableSet(NavigableSet<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
	}

	@Override
	protected NavigableSet<W> getWrapped() {
		return (NavigableSet<W>)super.getWrapped();
	}

	@Override
	public E lower(E e) {
		return transformer.fromWrapped(getWrapped().lower(transformer.toWrapped(e)));
	}

	@Override
	public E floor(E e) {
		return transformer.fromWrapped(getWrapped().floor(transformer.toWrapped(e)));
	}

	@Override
	public E ceiling(E e) {
		return transformer.fromWrapped(getWrapped().ceiling(transformer.toWrapped(e)));
	}

	@Override
	public E higher(E e) {
		return transformer.fromWrapped(getWrapped().higher(transformer.toWrapped(e)));
	}

	@Override
	public E pollFirst() {
		return transformer.fromWrapped(getWrapped().pollFirst());
	}

	@Override
	public E pollLast() {
		return transformer.fromWrapped(getWrapped().pollLast());
	}

	@Override
	public TransformNavigableSet<E,W> descendingSet() {
		return of(getWrapped().descendingSet(), transformer);
	}

	@Override
	public TransformIterator<E,W> descendingIterator() {
		return TransformIterator.of(getWrapped().descendingIterator(), transformer);
	}

	@Override
	public TransformNavigableSet<E,W> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return of(getWrapped().subSet(
				transformer.toWrapped(fromElement),
				fromInclusive,
				transformer.toWrapped(toElement),
				toInclusive
			),
			transformer
		);
	}

	@Override
	public TransformNavigableSet<E,W> headSet(E toElement, boolean inclusive) {
		return of(getWrapped().headSet(
				transformer.toWrapped(toElement),
				inclusive
			),
			transformer
		);
	}

	@Override
	public TransformNavigableSet<E,W> tailSet(E fromElement, boolean inclusive) {
		return of(getWrapped().tailSet(
				transformer.toWrapped(fromElement),
				inclusive
			),
			transformer
		);
	}
}
