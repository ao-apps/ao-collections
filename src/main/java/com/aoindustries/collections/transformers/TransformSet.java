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

import java.util.Set;
import java.util.SortedSet;

/**
 * Wraps a {@link Set}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("EqualsAndHashcode")
public class TransformSet<E,W> extends TransformCollection<E,W> implements Set<E> {

	/**
	 * Wraps a set.
	 * <ol>
	 * <li>If the given set is a {@link SortedSet}, then will return a {@link TransformSortedSet}.</li>
	 * </ol>
	 *
	 * @see  TransformSortedSet#of(java.util.SortedSet, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformSet<E,W> of(Set<W> set, Transformer<E,W> transformer) {
		if(set instanceof SortedSet) {
			return TransformSortedSet.of((SortedSet<W>)set, transformer);
		}
		return (set == null) ? null : new TransformSet<>(set, transformer);
	}

	/**
	 * @see  #of(java.util.Set, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformSet<E,E> of(Set<E> set) {
		return of(set, Transformer.identity());
	}

	protected TransformSet(Set<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
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
				? of((Set<Object>)o, transformer.invert().unbounded())
				: o
		);
	}

	// TODO: spliterator()?
}
