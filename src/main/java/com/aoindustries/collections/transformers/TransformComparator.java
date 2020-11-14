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

import java.util.Comparator;

/**
 * Wraps a {@link Comparator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformComparator<T,W> implements Comparator<T> {

	/**
	 * Wraps a comparator.
	 */
	public static <T,W> TransformComparator<T,W> of(Comparator<? super W> comparator, Transformer<T,W> transformer) {
		return (comparator == null) ? null : new TransformComparator<>(comparator, transformer);
	}

	/**
	 * @see  #of(java.util.Comparator, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <T> TransformComparator<T,T> of(Comparator<? super T> comparator) {
		return of(comparator, Transformer.identity());
	}

	private final Comparator<? super W> wrapped;
	protected final Transformer<T,W> transformer;

	protected TransformComparator(Comparator<? super W> wrapped, Transformer<T,W> transformer) {
		this.wrapped = wrapped;
		this.transformer = transformer;
	}

	protected Comparator<? super W> getWrapped() {
		return wrapped;
	}

	@Override
	public int compare(T t1, T t2) {
		return getWrapped().compare(transformer.toWrapped(t1),
			transformer.toWrapped(t2)
		);
	}
}
