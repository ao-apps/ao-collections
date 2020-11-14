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
public class TransformSortedSet<E,W> extends TransformSet<E,W> implements SortedSet<E> {

	/**
	 * Wraps a sorted set.
	 * <ol>
	 * <li>If the given set is a {@link NavigableSet}, then will return a {@link TransformNavigableSet}.</li>
	 * </ol>
	 *
	 * @see  TransformNavigableSet#of(java.util.NavigableSet, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformSortedSet<E,W> of(SortedSet<W> set, Transformer<E,W> transformer) {
		if(set instanceof NavigableSet) {
			return TransformNavigableSet.of((NavigableSet<W>)set, transformer);
		}
		return (set == null) ? null : new TransformSortedSet<>(set, transformer);
	}

	/**
	 * @see  #of(java.util.SortedSet, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformSortedSet<E,E> of(SortedSet<E> set) {
		return of(set, Transformer.identity());
	}

	protected TransformSortedSet(SortedSet<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
	}

	@Override
	protected SortedSet<W> getWrapped() {
		return (SortedSet<W>)super.getWrapped();
	}

	private TransformComparator<E,W> comparator;

	@Override
	public TransformComparator<E,W> comparator() {
		TransformComparator<E,W> c = comparator;
		if(c == null) {
			c = TransformComparator.of(getWrapped().comparator(), transformer);
			comparator = c;
		}
		return c;
	}

	@Override
	public TransformSortedSet<E,W> subSet(E fromElement, E toElement) {
		return of(getWrapped().subSet(
				transformer.toWrapped(fromElement),
				transformer.toWrapped(toElement)
			),
			transformer
		);
	}

	@Override
	public TransformSortedSet<E,W> headSet(E toElement) {
		return of(getWrapped().headSet(
				transformer.toWrapped(toElement)
			),
			transformer
		);
	}

	@Override
	public TransformSortedSet<E,W> tailSet(E fromElement) {
		return of(getWrapped().tailSet(
				transformer.toWrapped(fromElement)
			),
			transformer
		);
	}

	@Override
	public E first() {
		return transformer.fromWrapped(getWrapped().first());
	}

	@Override
	public E last() {
		return transformer.fromWrapped(getWrapped().last());
	}

	// TODO: spliterator()?
}
